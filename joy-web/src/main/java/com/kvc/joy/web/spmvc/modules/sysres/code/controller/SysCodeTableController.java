package com.kvc.joy.web.spmvc.modules.sysres.code.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kvc.joy.core.sysres.code.po.TSysCodeTable;
import com.kvc.joy.web.spmvc.core.BaseController;

/**
 * 
 * @since 1.0.0
 * @author 唐玮琳
 * @time 2013年10月15日 下午10:43:37
 */
@Controller
@RequestMapping("/sysCodeTable")
public class SysCodeTableController extends BaseController<TSysCodeTable> {

	@Override
	protected String getCurrentViewName() {
		return "joy/core/sysres/code/sysCodeTable";
	}

}