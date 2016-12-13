package com.rounter.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rounter.client.sender.node.SenderToGameServer;
import com.rounter.param.IRequestData;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.Response9Game;
import com.rounter.service.IResponseHandler;


@RestController
public class ApplicationController {
	Logger logger = LoggerFactory.getLogger("mainLogger");

	@Resource
	SenderToGameServer server;
	
	@RequestMapping("/hello")
	public String greeting() {
		long startTime = System.currentTimeMillis();
//		IResponseData resData = new Response9Game();
//
//		server.sendMsgToGameServer(new IRequestData() {
//			
//			@Override
//			public long getId() {
//				return 999;
//			}
//		}, new IResponseHandler() {
//			
//			@Override
//			public void handleServerResponse(Object msgBack, IResponseData response) {
//				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@handleServerResponse: " + msgBack);
//			}
//			
//			@Override
//			public void handleSendFailResponse(IResponseData response) {
//				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@handleSendFailResponse..........");
//			}
//		}, resData);
		System.out.println(System.currentTimeMillis() - startTime);
		return "";//resData.toString();
	}
	
	@RequestMapping("/index")
	public String index(){
		return "<html><body><h2>This is an index html</h2></body></html>";
	}
	
	
	@RequestMapping("/roleInfo")
	public @ResponseBody String getRoleInfo(@RequestBody String value) {
		logger.info("role info,request value{}",value);
		return "roleInfo";
	}

	@RequestMapping("/zonelist")
	public @ResponseBody String getZoneList(String value) {
		logger.info("get zone list, request value{}", value);
		return "zoneList";
	}
	
	
}
