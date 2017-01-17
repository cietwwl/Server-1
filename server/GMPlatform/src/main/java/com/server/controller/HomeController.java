package com.server.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.server.beans.User;
import com.server.paramers.RESTRespone;

@RestController
public class HomeController {

	@RequestMapping("/user/login")
	public @ResponseBody RESTRespone showHomePage(@RequestBody User user){
		
		return new RESTRespone().success();
	}
}
