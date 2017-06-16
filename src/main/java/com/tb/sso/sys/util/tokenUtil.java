package com.tb.sso.sys.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author acer11
 *  作者：haoxud
* 创建时间：2017年6月16日 上午11:17:11  
* 项目名称：tb-sso  
* 文件名称：tokenUtil.java  
* 类说明：token工具类
 */
public class tokenUtil {
	
	
	/**
	 * 生成用户token
	 * 采用用户名加时间搓
	 * @param userName
	 * @return
	 */
	public static  String createUserToken(String userName){
		return DigestUtils.md5Hex(userName+System.currentTimeMillis());
		
	}

}
