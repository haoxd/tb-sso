package com.tb.sso.service.user;

import java.io.IOException;
import java.util.Date;
import javax.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tb.common.service.cache.RedisCacheService;
import com.tb.sso.dao.user.UserDao;
import com.tb.sso.pojo.user.TbUser;
import com.tb.sso.sys.constast.ssoRedisCode.ssoCode;
import com.tb.sso.sys.util.tokenUtil;

@Service("userService")
public class UserService {
	
	@Autowired
	private UserDao dao;
	
	@Resource(name = "redis")
	private RedisCacheService redis;
	
	private static final ObjectMapper oMapper = new ObjectMapper();

	/**
	 * 检查用户有效性
	 * @param param
	 * @param checkType
	 * @return
	 */
	public Boolean checkUserEffective(String param, Integer checkType) throws Exception {
		//用户类型检查 1：username 2：phone 3 ：email
		TbUser inparam = new TbUser();
		switch (checkType) {
		case 1:
			inparam.setUserName(param);
			break;
		case 2:
			inparam.setUserPhone(param);
			break;
		case 3:
			inparam.setUserEmail(param);
			break;	
		default:
			return null;
		}
		TbUser user = this.dao.selectOne(inparam);
		
		return user == null;
	}

	/**
	 * 用户注册
	 * @param userName
	 * @param userPassword
	 * @param userPhone
	 * @return
	 */
	public boolean userRegister(String userName, String userPassword, String userPhone) {
		TbUser user = new TbUser();
		user.setCreateTime(new Date());
		user.setUpdateTime(new Date());
		user.setUserEmail(null);
		user.setUserId(null);
		user.setUserName(userName);
		//MD5加密
		user.setUserPassWord(DigestUtils.md5Hex(userPassword));
		user.setUserPhone(userPhone);
		return this.dao.insert(user)==1 ?true :false;
	}

	/**
	 * 用户登录
	 * @param userName
	 * @param userPassword
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String userLogin(String userName, String userPassword) throws Exception {
		TbUser inParam = new TbUser();
		inParam.setUserName(userName);
		TbUser user=	this.dao.selectOne(inParam);
		if(null == user){
			return null;
		}
		//判断密码是否相同
		if(!StringUtils.equals(DigestUtils.md5Hex(userPassword), user.getUserPassWord())){
			return null;
		}
		//登录成功，将用户信息保存到redis
		String token = tokenUtil.createUserToken(userName);
		this.redis.set(ssoCode.SSO_TOKEN+token, oMapper.writeValueAsString(user),ssoCode.USER_REDIS_EFFECTIVE_TIME);
		return token;		
	}

	public TbUser queryUserByToken(String token) {
		String userData =this.redis.get(ssoCode.SSO_TOKEN+token);
		if(StringUtils.isEmpty(userData)){
			return null;
		}
		//重新设置redis用户信息的生存时间
		this.redis.expire(ssoCode.SSO_TOKEN+token, ssoCode.USER_REDIS_EFFECTIVE_TIME);
		try {
			return oMapper.readValue(userData, TbUser.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void logout(String token){
		this.redis.del(ssoCode.SSO_TOKEN+token);
	}
}
