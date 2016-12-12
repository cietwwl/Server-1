package com.rounter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContext;

import com.rounter.param.Request9Game;


@RestController
@RequestMapping("/9game")
public class _9GameController {

	Logger logger = LoggerFactory.getLogger("mainLogger");

	@RequestMapping("/roleInfo")
	public @ResponseBody String getRoleInfo(String value) {
		logger.info("role info,request value{}",value);
		return "roleInfo";
	}

	@RequestMapping("/zonelist")
	public @ResponseBody String getZoneList(String value) {
		logger.info("get zone list, request value{}", value);
		return "zoneList";
	}
	
	@RequestMapping(value="roleInfo", method={RequestMethod.POST})
	@ResponseBody
	public String getRoleInfo(@RequestBody Request9Game request){
		logger.info("get role info by post param");
		return "response role info";
	}
	
	
}
