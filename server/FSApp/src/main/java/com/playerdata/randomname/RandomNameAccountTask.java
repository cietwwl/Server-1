package com.playerdata.randomname;

import com.google.protobuf.ByteString;
import com.rw.netty.UserChannelMgr;
import com.rwproto.RandomNameServiceProtos.RandomNameRequest;
import com.rwproto.RequestProtos.RequestHeader;

import io.netty.channel.ChannelHandlerContext;

public class RandomNameAccountTask implements Runnable {

	private RequestHeader header;
	private RandomNameRequest randomNameRequest;
	private ChannelHandlerContext ctx;

	public RandomNameAccountTask(RequestHeader headerP, RandomNameRequest requestP, ChannelHandlerContext ctxP) {
		this.header = headerP;
		this.randomNameRequest = requestP;
		this.ctx = ctxP;
	}

	@Override
	public void run() {
		ByteString result = RandomNameHandler.getInstance().fetchRandomName(randomNameRequest);
		UserChannelMgr.sendResponse(null, header, null, result, 200, ctx, null);
	}

}
