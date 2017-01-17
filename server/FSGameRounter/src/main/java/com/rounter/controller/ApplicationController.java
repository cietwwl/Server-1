package com.rounter.controller;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rounter.client.sender.exception.NoCanUseNodeException;
import com.rounter.client.sender.exception.ParamInvalidException;
import com.rounter.client.sender.node.ChannelNodeManager;
import com.rounter.client.sender.node.ServerChannelManager;
import com.rounter.param.IRequestData;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.Response9Game;
import com.rounter.service.IResponseHandler;

@RestController
public class ApplicationController {
	Logger logger = LoggerFactory.getLogger("mainLogger");

	@Autowired
	private ServerChannelManager serverMgr;

	@RequestMapping("/hello")
	public String greeting() {
		final long startTime = System.currentTimeMillis();
		IResponseData resData = new Response9Game();
		
		ChannelNodeManager channelMgr = serverMgr.getAreaNodeManager("1001");
		if(null != channelMgr){
			try {
				channelMgr.sendMessage(new IRequestData() {
					
					@Override
					public long getId() {
						return 999;
					}
				}, new IResponseHandler() {
					
					@Override
					public void handleServerResponse(Object msgBack, IResponseData response) {
						System.out.println("Success" + (System.currentTimeMillis() - startTime));
						((Response9Game)response).setId(789);
					}
					
					@Override
					public void handleSendFailResponse(IResponseData response) {
						System.out.println("Fail" + (System.currentTimeMillis() - startTime));
						((Response9Game)response).setId(456);
					}
					
				}, resData);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ParamInvalidException e) {
				e.printStackTrace();
			} catch (NoCanUseNodeException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("连接失败。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
		}
		System.out.println(System.currentTimeMillis() - startTime);
		System.out.println("resData:" + resData.getId());
		return resData.toString();
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
