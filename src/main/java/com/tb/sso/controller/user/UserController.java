package com.tb.sso.controller.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tb.sso.controller.base.BaseController;
import com.tb.sso.pojo.user.TbUser;
import com.tb.sso.service.user.UserService;
import com.tb.sso.sys.constast.ServiceCode.loginCode;
import com.tb.sso.sys.constast.ServiceCode.registerCode;
import com.tb.sso.sys.util.CookieUtils;

@Controller()
@RequestMapping("/user")
public class UserController extends BaseController {

	/**
	 * 用户服务
	 */
	@Resource(name = "userService")
	private UserService userService;

	/**
	 * 返回注册页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register() {

		return "register";
	}

	/**
	 * 登录页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	/**
	 * 检查用户有效性 param：检查参数 checkType：检查类型
	 * 
	 * @return
	 */
	@RequestMapping(value = "/checkUser/{param}/{checkType}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> checkUserEffective(@PathVariable("param") String param,
			@PathVariable("checkType") Integer checkType) {

		try {
			Boolean isUser = this.userService.checkUserEffective(param, checkType);
			if (null == isUser) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			return ResponseEntity.ok(isUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

	}

	/**
	 * 用户注册
	 * 
	 * @param userPassword
	 * @param userName
	 * @param userPhone
	 * @return
	 */
	@RequestMapping(value = "/userRegister")
	@ResponseBody
	public Map<String, Object> userRegister(
			 @RequestParam("password")String userPassword,
			 @RequestParam("username")String userName,
			 @RequestParam("phone")String userPhone) {
		Map<String, Object> resp = new HashMap<String, Object>();
		if(userPassword.length()<6||userPassword.length()>20){
			resp.put("status", registerCode.REGISTER_PARAM_FAIL);
			resp.put("data", "参数有误：密码必须在6-20位" );
			return resp;
		}
		if(userName.length()<6||userName.length()>20){
			resp.put("status", registerCode.REGISTER_PARAM_FAIL);
			resp.put("data", "参数有误：用户名必须在6-20位");
			return resp;
		}
		if(userPhone.length()!=11){
			resp.put("status", registerCode.REGISTER_PARAM_FAIL);
			resp.put("data", "参数有误：手机号必须11位");
			return resp;
		}
		try {

			boolean register = this.userService.userRegister(userName, userPassword, userPhone);
			if (register) {
				resp.put("status", registerCode.REGISTERSUCC);
			} else {
				resp.put("status", registerCode.REGISTERFAIL);
				resp.put("data", "注册失败");
			}

		} catch (Exception e) {
			resp.put("status", registerCode.REGISTERFAIL);
			resp.put("data", e.getMessage());
		}
		return resp;

	}
	
	/**
	 * 用户登录
	 * @return
	 */
	@RequestMapping(value="/userLogin",method=RequestMethod.POST)
	@ResponseBody
	public Map<String ,Object> userLogin(@RequestParam("username")String userName,
			@RequestParam("password") String userPassword,
			HttpServletRequest request,HttpServletResponse response
			
			){
		Map<String, Object> resp = new HashMap<String, Object>();
		if(userPassword.length()<6||userPassword.length()>20){
			resp.put("status", loginCode.LOGIN_PARAM_FAIL);
			resp.put("data", "参数有误：密码必须在6-20位" );
			return resp;
		}
		if(userName.length()<6||userName.length()>20){
			resp.put("status", loginCode.LOGIN_PARAM_FAIL);
			resp.put("data", "参数有误：用户名必须在6-20位");
			return resp;
		}
		try {
		String token=	this.userService.userLogin(userName,userPassword);
		if(StringUtils.isEmpty(token)){
			resp.put("status", loginCode.LOGINFAIL);
			resp.put("data", "生成token失败");
			return resp;
		}
		//登录成功保存在cookie
		resp.put("status", loginCode.LOGINSUCC);
		CookieUtils.setCookie(request, response, loginCode.LOGIN_COOKIE_TOKEN_NAME, token);
		} catch (Exception e) {
			resp.put("status", loginCode.LOGINFAIL);
			resp.put("data",  e.getMessage());		
		}
		return resp;
	}
	
	/**
	 * 用户注销
	 * @param token
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/logout/{token}")
	public String logout(@PathVariable("token")String token,
			HttpServletRequest request,HttpServletResponse response
			){

		CookieUtils.deleteCookie(request, response, loginCode.LOGIN_COOKIE_TOKEN_NAME);
		this.userService.logout(token);
		return "login";
	}
	
	/**
	 * 更具生成token获取用户信息
	 * @param token
	 * @return
	 */
	@RequestMapping(value="/queryUserByToken/{token}",method=RequestMethod.GET)
	public ResponseEntity<TbUser> queryUserByToken(@PathVariable("token")String token){
	try {
		TbUser user=	this.userService.queryUserByToken(token);
		if(null ==user){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(user);
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

}
