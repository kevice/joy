package org.joy.plugin.seqgen.service.impl;

import org.joy.commons.bean.Pair;
import org.joy.commons.enums.YesNot;
import org.joy.commons.lang.DateTool;
import org.joy.commons.lang.string.StringTool;
import org.joy.commons.log.Log;
import org.joy.commons.log.LogFactory;
import org.joy.core.persistence.orm.jpa.JpaTool;
import org.joy.plugin.seqgen.model.po.TSysSeqNum;
import org.joy.plugin.seqgen.model.po.TSysSeqNumRule;
import org.joy.plugin.seqgen.service.ISequenceGenerator;
import org.joy.plugin.seqgen.support.CycleProcessor;
import org.joy.plugin.seqgen.support.PrefixSuffixPatternParser;
import org.joy.plugin.seqgen.support.SeqNumStat;
import org.joy.plugin.seqgen.support.SequenceCache;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * 序列号生成器
 *
 * @author Kevice
 * @time 2012-04-30 下午5:08:04
 * @since 1.0.0
 */
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class SequenceGenerator implements ISequenceGenerator {

	protected static final Log log = LogFactory.getLog(SequenceGenerator.class);

	private static final Map<String, SequenceCache> cacheSeq = new HashMap<String, SequenceCache>();
	
	/**
	 * 当前服务是否在运行，用来作服务在多台机子上迁移用
	 */
	public static boolean running;

	public SequenceGenerator() {
		try {
			List<TSysSeqNum> seqs = JpaTool.searchAll(TSysSeqNum.class);
			for (TSysSeqNum seqNum : seqs) {
				TSysSeqNumRule seqRule = seqNum.getSeqRule();
				checkConfig(seqRule, seqNum);
				SequenceCache seqCache = new SequenceCache(seqNum);
				cacheSeq.put(seqNum.getId(), seqCache);
			}
			log.info("序列号种数：" + seqs.size());
			log.info("序列号缓存：" + cacheSeq);
		} catch (Exception e) {
			log.error(e, "获取序列号规则和号码信息出错！");
		}
	}
	
	/**
	 * 取得下一个序列号
	 * @param seqNumId 序列号码表的主键
	 * @return 序列号(包含前缀、后缀)
	 */
	public synchronized String nextSequence(String seqNumId) {
		SequenceCache seqCache = cacheSeq.get(seqNumId);
		TSysSeqNum seqNum = seqCache.getSeqNum();
		
		// 如果序列号被置为无效，返回空串
		if (YesNot.NOT.getBinary().equals(seqNum.getActive())) {
			return "";
		}

		// 周期的开始时间在未来，返回空串
		String curPeriodStartTimeStr = seqNum.getCurPeriodStartTime();
		if (curPeriodStartTimeStr != null) {
			Calendar curPeriodStartTime = CycleProcessor.formatCurPeriodStartTime(curPeriodStartTimeStr);
			Calendar now = Calendar.getInstance();
			if (curPeriodStartTime.after(now)) {
				return "";
			}
		}

		long curSeq = getSequenceNo(seqNumId);
		return fillPattern(curSeq, seqNum);
	}
	
	/**
	 * 取得当前序列号
	 * @param seqNumId 序列号码表的主键
	 * @return 序列号(包含前缀、后缀)
	 */
	public String curSequence(String seqNumId) {
		SequenceCache seqCache = cacheSeq.get(seqNumId);
		TSysSeqNum seqNum = seqCache.getSeqNum();
		long curSeq = seqCache.getRealSeqNum();
		return fillPattern(curSeq, seqNum);
	}
	
	/**
	 * 取得当前序列号
	 * @param seqNumId 序列号码表的主键
	 * @return 序列号(包含前缀、后缀)
	 */
	public long curSeqNum(String seqNumId) {
		SequenceCache seqCache = cacheSeq.get(seqNumId);
		return seqCache.getRealSeqNum();
	}
	
	public String fillPattern(long curSeq, TSysSeqNum seqNum) {
		String curSeqStr = curSeq + "";
		TSysSeqNumRule tSysSeqNumRule = seqNum.getSeqRule();
		Integer length = tSysSeqNumRule.getLength();
		if (length != null && length != 0) {
			String pattern = StringTool.rightPad("", length, "0");
			NumberFormat format = new DecimalFormat(pattern);
			curSeqStr = format.format(curSeq);
		}
		
		Pair<String, String> prefixAndSuffix = parsePrefixAndSuffixPattern(seqNum.getId());

		return prefixAndSuffix.getLeft() + curSeqStr + prefixAndSuffix.getRight();
	}
	
	private long getSequenceNo(String seqNunId) {
		long nextNum;
		SequenceCache seqCache = (SequenceCache) cacheSeq.get(seqNunId);
		TSysSeqNum tSysSeqNum = seqCache.getSeqNum();
		
		Pair<Boolean, Calendar> result = CycleProcessor.processCycle(seqCache);
		boolean restart = result.getLeft(); 
		if (restart || seqCache.hasNext() == false) {
			if (restart) {
				onCycleRestart(seqCache, result.getRight());
			} else {
				seqCache.refreshCache(restart);	
			}

			// update SequenceNum
			nextNum = seqCache.next();
			tSysSeqNum.setCurSeq(seqCache.getCacheSeqNum());
			JpaTool.persist(tSysSeqNum);
		} else {
			nextNum = seqCache.next();
			tSysSeqNum.setCurSeq(nextNum);
		}

		return nextNum;
	}
	
	/**
	 * 周期重新开始时
	 * @param seqCache
	 */
	private void onCycleRestart(SequenceCache seqCache, Calendar calendar) {
		new SeqNumStat().stat(seqCache);
		
		TSysSeqNum seqNum = seqCache.getSeqNum();
		seqNum.setCurPeriodStartTime(DateTool.formatDate(calendar.getTime(), DateTool.UNFMT_yyyyMMddHHmmss));
		seqCache.refreshCache(true);
	}
	
	/**
	 * 解析前缀和后缀模板
	 */
	private Pair<String, String> parsePrefixAndSuffixPattern(String seqNumId) {
		SequenceCache cache = (SequenceCache) cacheSeq.get(seqNumId);
		TSysSeqNum seqNum = cache.getSeqNum();
		TSysSeqNumRule rule = seqNum.getSeqRule();
		String prefixPattern = rule.getPrefix();
		if (prefixPattern == null) {
			prefixPattern = "";
		}
		String suffixPattern = rule.getSuffix();
		if (suffixPattern == null) {
			suffixPattern = "";
		}
		Pair<String, String> prefixPair = new Pair<String, String>(rule.getPrefix(), seqNum.getPrefixParams());
		Pair<String, String> suffixPair = new Pair<String, String>(rule.getSuffix(), seqNum.getSuffixParams());
		PrefixSuffixPatternParser paser = new PrefixSuffixPatternParser(prefixPair, suffixPair);
		return paser.parse();
	}
	
	private void checkConfig(TSysSeqNumRule rule, TSysSeqNum tSysSeqNum) {
		Long maxValue = rule.getMaxValue();
		if (maxValue == null || maxValue.intValue() <= 0) {
			Integer periodType = rule.getPeriodType();
			if (periodType == null) {
				String errMsg = "序列号生成规则配置错误：周期类型为空(不循环)时，maxValue（序列号最大值）不能为空或小于等于0！";
				log.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		}
	}
	
	public void start() {
		running = true;
		log.info("开始序列号服务");
	}
	
	public void stop() {
		running = false;
		log.info("停止序列号服务");
		onExit();
	}
	
	/**
	 * 系统退出时，将缓存中的序列号同步到数据库
	 */
	public void onExit() {
			Collection<SequenceCache> seqCaches = cacheSeq.values();
			for (SequenceCache seqCache : seqCaches) {
				TSysSeqNum seqNum = seqCache.getSeqNum();
				long realSeqNum = seqCache.getRealSeqNum();
				seqNum.setCurSeq(realSeqNum);
				JpaTool.persist(seqNum);
			}
	}
	
}