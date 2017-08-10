package com.tb.sso.sys.constast;

public class ssoRedisCode {
	
	/**
	* 创建时间：2017年6月16日 上午11:27:03  
	* 类说明：单点登录系统
	 */
	public interface ssoCode{
		Integer USER_REDIS_EFFECTIVE_TIME = 60*30;//30分钟
		String SSO_TOKEN="SSO_TOKEN_";
	}

}
