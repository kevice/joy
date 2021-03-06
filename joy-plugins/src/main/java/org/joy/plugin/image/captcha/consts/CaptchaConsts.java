package org.joy.plugin.image.captcha.consts;

/**
 * 验证码插件的常量
 * 
 * @since 1.0.0
 * @author Kevice
 * @time 2013年9月28日 下午10:28:16
 */
public interface CaptchaConsts {

	/**
	 * session中验证码的key
     *
     * @since 1.0.0
     * @author Kevice
     * @time 2013年9月28日 下午10:28:16
	 */
	String CAPTCHA_SESSION_KEY = com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;
	/**
	 * session中验证码生成日期的key
     *
     * @since 1.0.0
     * @author Kevice
     * @time 2013年9月28日 下午10:28:16
	 */
	String CAPTCHA_SESSION_DATE = com.google.code.kaptcha.Constants.KAPTCHA_SESSION_DATE;
	
}
