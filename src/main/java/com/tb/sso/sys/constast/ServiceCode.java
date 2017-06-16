package com.tb.sso.sys.constast;

public class ServiceCode {
	
	 /**
	* 类说明：注册相应编码
	 */
	public interface registerCode{
		 String REGISTERSUCC ="200";
		 String REGISTERFAIL ="500";
		 String REGISTER_PARAM_FAIL ="400";
	 }
	public interface loginCode{
		 String LOGINSUCC ="200";
		 String LOGINFAIL ="500";
		 String LOGIN_PARAM_FAIL ="400";
		 String LOGIN_COOKIE_TOKEN_NAME="SSO_TOKEN";
	 }
}
