package com.kvc.joy.plugin.seqgen;

import org.springframework.stereotype.Component;

import com.kvc.joy.core.init.service.IJoyPlugin;
import com.kvc.joy.core.init.support.JoyPropeties;
import com.kvc.joy.plugin.seqgen.model.po.TSysSeqNumRule;

/**
 * 
 * @author 唐玮琳
 * @time 2013-2-5 上午12:48:26
 */
@Component
public class SeqGenPlugin implements IJoyPlugin {

	public String getName() {
		return "序列号生成器";
	}

	public void startup() {
		
	}

	public void destroy() {
		
	}

	public boolean isEnabled() {
		return JoyPropeties.PLUGIN_SEQGEN_ENABLED;
	}

	public int getInitPriority() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String getSqlMigrationPrefix() {
		return "SEQGEN";
	}

	@Override
	public String getPoPackage() {
		return TSysSeqNumRule.class.getPackage().getName();
	}
	
	@Override
	public String getCtxConfLocation() {
		return "classpath*:/conf/plugin-appCtx-seqgen.xml";
	}

}
