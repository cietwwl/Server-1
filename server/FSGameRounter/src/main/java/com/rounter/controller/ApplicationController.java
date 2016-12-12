package com.rounter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApplicationController {
	Logger logger = LoggerFactory.getLogger("mainLogger");

	@RequestMapping("/hello")
	public String greeting() {
		logger.info("recv msg:{}", "hello");
		return "Say hello";
	}
	
	@RequestMapping("/index")
	public String index(){
		return "<html><body><h2>This is an index html</h2></body></html>";
	}
}
