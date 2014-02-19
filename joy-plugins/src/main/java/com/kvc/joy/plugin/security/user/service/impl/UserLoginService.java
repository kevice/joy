package com.kvc.joy.plugin.security.user.service.impl;

import com.kvc.joy.commons.log.Log;
import com.kvc.joy.commons.log.LogFactory;
import com.kvc.joy.plugin.security.user.model.po.TUserBasic;
import com.kvc.joy.plugin.security.user.model.po.TUserLoginLog;
import com.kvc.joy.plugin.security.user.service.IUserLoginLogService;
import com.kvc.joy.plugin.security.user.service.IUserLoginService;
import com.kvc.joy.plugin.security.user.support.enums.LoginState;
import com.kvc.joy.plugin.security.user.support.vo.UserLoginVo;
import com.kvc.joy.plugin.support.PluginBeanFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;

/**
 * 用户登陆服务
 * 
 * @since 1.0.0
 * @author 唐玮琳
 * @time 2013年9月29日 上午1:18:42
 */
public class UserLoginService implements IUserLoginService {
	
	private IUserLoginLogService loginLogService;
	private final Log log = LogFactory.getLog(getClass());

	@Override
	public String login(UserLoginVo loginVo) {
		// validate
		// / captcha
		String errMsg = null;
		if (loginVo.isCaptchaRequire()) {
			errMsg = validateCaptcha(loginVo);
			if (errMsg != null) {
				loginVo.setLoginState(LoginState.CAPTCHA_ERR);
				loginLogService.logOnLogin(loginVo);
				return errMsg;
			}	
		}
		
		// / username & password
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser.isAuthenticated() == false) {
			errMsg = validateUserAndPassword(currentUser, loginVo);
		} else { // 反复登录
			TUserBasic user = (TUserBasic) currentUser.getPrincipal();
			if (user.getAccount().equalsIgnoreCase(loginVo.getUserAccount()) == false) { // 若是登录名不合
				currentUser.logout();
				errMsg = validateUserAndPassword(currentUser, loginVo);
			}
		}
		
		if (errMsg == null) {
			loginVo.setLoginState(LoginState.SUCCESS);
			TUserLoginLog logOnLogin = loginLogService.logOnLogin(loginVo);
			PluginBeanFactory.getUserLogoutService().mendLogoutLogOnLogin(logOnLogin);
		}
		
		//TODO 多处登陆
		
		//TODO 帐号状态

		return errMsg;
	}

	@Override
	public boolean hasLogin() {
		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.isAuthenticated();
	}
	
	private String validateCaptcha(UserLoginVo loginVo) {
		String errMsg = null;

		// 二次校验验证码(第一次为通过jquer.validate的remote配置校验)
		boolean validateTime = PluginBeanFactory.getCaptchaService().validateTime(loginVo.getCaptchaGenTime());
		if (validateTime == false) {
			errMsg = "验证码已过期，请重新获取.";
		} else {
			boolean success = PluginBeanFactory.getCaptchaService().validateCode(
					loginVo.getCaptchaServer(), loginVo.getCaptchaClient());
			if (success == false) {
				errMsg = "验证码不正确.";
			}
		}

		return errMsg;
	}

	private String validateUserAndPassword(Subject currentUser, UserLoginVo loginVo) {
		String account = loginVo.getUserAccount();
		String password = loginVo.getUserPassword();
		boolean rememberMe = loginVo.rememberMe();
				
		String errMsg = null;
		UsernamePasswordToken token = new UsernamePasswordToken(account, password);
		token.setRememberMe(rememberMe);
		try {
			currentUser.login(token);
		} catch (UnknownAccountException uae) {
			log.error(uae);
			errMsg = "用户名或密码错误！"; // 用户名不存在，为了安全性，不精确提示
		} catch (IncorrectCredentialsException ice) {
			log.error(ice);
			errMsg = "用户名或密码错误！";
			loginVo.setLoginState(LoginState.PASSWORD_ERR);
			loginLogService.logOnLogin(loginVo);
		} catch (LockedAccountException lae) {
			log.error(lae);
			errMsg = "您的帐号已被锁定！";
			loginVo.setLoginState(LoginState.ACCOUNT_LOCK);
			loginLogService.logOnLogin(loginVo);
		} catch (AuthenticationException ae) {
			log.error(ae);
			errMsg = "授权发生异常！";
		} catch (Exception ae) {
			log.error(ae);
			errMsg = "发生未预期的错误！";
		}
		return errMsg;
	}
	
	public void setLoginLogService(IUserLoginLogService loginLogService) {
		this.loginLogService = loginLogService;
	}

}
