package org.joy.web.spmvc.modules.monitor.performance.controller;

import org.joy.web.spmvc.core.BaseCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @since 1.0.0
 * @author Kevice
 * @time 2013年11月30日 下午11:29:09
 */
@Controller
@RequestMapping("/sysResMonitor")
public class SysResMonitorController extends BaseCrudController {

	@Override
	protected String getCurrentViewName() {
		return "joy/commons/core/monitor/performance/sysResMonitor";
	}

}
