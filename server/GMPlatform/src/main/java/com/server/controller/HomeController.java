package com.server.controller;

import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.server.beans.User;
import com.server.paramers.RESTResponse;

@RestController
public class HomeController {

	/**
	 * 玩家登录
	 * @param user
	 * @return
	 */
	@RequestMapping("/user/login")
	public @ResponseBody RESTResponse showHomePage(@RequestBody User user){
		
		return new RESTResponse().success();
	}
	
	
	/**
	 * 玩家注册
	 * @param user
	 * @return
	 */
	@RequestMapping("/user/register")
	public @ResponseBody RESTResponse register(HttpRequest request){
		return new RESTResponse().success();
	}
}
