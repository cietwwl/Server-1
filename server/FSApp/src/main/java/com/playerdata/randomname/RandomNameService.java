package com.playerdata.randomname;

import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.RandomNameServiceProtos.RandomNameRequest;
import com.rwproto.RequestProtos.Request;

import io.netty.channel.ChannelHandlerContext;

public class RandomNameService {

	private static RandomNameService instance = new RandomNameService();
	
	public static RandomNameService getInstance() {
		return instance;
	}
	
	public void processGetRandomName(Request request, ChannelHandlerContext ctx) {
		try {
			RandomNameRequest randomNameRequest = RandomNameRequest.parseFrom(request.getBody().getSerializedContent());
			GameWorldFactory.getGameWorld().executeAccountTask(randomNameRequest.getAccountId(), new RandomNameAccountTask(request.getHeader(), randomNameRequest, ctx));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
