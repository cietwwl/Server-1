package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import com.rw.service.ResponseService;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class ResponseNettyControler {
	private Map<Command, ResponseService> commandMap;
	
	public void doService(final Response exResponse, final ChannelHandlerContext ctx){
		Command command = exResponse.getHeader().getCommand();
		getSerivice(command).processResponse(exResponse);
	}
	
	private ResponseService getSerivice(Command command) {
		return commandMap.get(command);
	}

	public void setCommandMap(Map<Command, ResponseService> commandMap) {
		this.commandMap = commandMap;
	}
}
