package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import com.google.protobuf.ByteString;
import com.rw.netty.UserChannelMgr;
import com.rwproto.ReConnectionProtos.ReConnectResponse;
import com.rwproto.ReConnectionProtos.ReConnectResultType;
import com.rwproto.RequestProtos.Request;

public class ReconnectCommon {

	public static ReconnectCommon instance = new ReconnectCommon();

	public void reLoginGame(FsNettyControler nettyControler, ChannelHandlerContext ctx, Request request) {
		returnReconnectRequest(nettyControler, ctx, request, ReConnectResultType.RETURN_GAME_LOGIN, null);
	}

	private void returnReconnectRequest(FsNettyControler nettyControler, ChannelHandlerContext ctx, Request request, ReConnectResultType resultType, ByteString synData) {
		ReConnectResponse.Builder reConnectRsp = ReConnectResponse.newBuilder();
		reConnectRsp.setResultType(resultType);
		UserChannelMgr.sendResponse(null, request.getHeader(), reConnectRsp.build().toByteString(), ctx, synData);
	}

	public void reconnectSuccess(FsNettyControler nettyControler, ChannelHandlerContext ctx, Request request, ByteString synData) {
		returnReconnectRequest(nettyControler, ctx, request, ReConnectResultType.RECONNECT_SUCCESS, synData);
	}

	public static ReconnectCommon getInstance() {
		return instance;
	}

}
