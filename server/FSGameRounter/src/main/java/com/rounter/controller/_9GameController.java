package com.rounter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
	
	
	public String getRoleInfo(@RequestBody Request9Game request){
		
		return "";
	}
}
