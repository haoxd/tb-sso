package com.tb.sso.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.tb.sso.controller.base.BaseController;

@Controller()
@RequestMapping("/user")
public class UserController extends BaseController {
	
	
	/**
	 * 返回注册页面
	 * @return
	 */
	@RequestMapping(value="/register",method=RequestMethod.GET)
	public String register(){
		
		return "register";
	}

	
	
}
