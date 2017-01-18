package com.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.server.beans.User;
import com.server.paramers.RESTResponse;

@RestController
@RequestMapping("/user")
public class HomeController {

	/**
	 * 玩家登录
	 * @param user
	 * @return
	 */
	@RequestMapping("login")
	public @ResponseBody RESTResponse showHomePage(@RequestBody User user){
		
		return new RESTResponse().success();
	}
	
	
	/**
	 * 玩家注册
	 * @param user
	 * @return
	 */
	@RequestMapping(value="register", method={RequestMethod.POST})
	public @ResponseBody RESTResponse register(HttpServletRequest request){
		return new RESTResponse().success();
	}
}
