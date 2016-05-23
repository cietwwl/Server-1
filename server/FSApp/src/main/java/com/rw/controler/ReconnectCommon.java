package com.rw.controler;

import io.netty.channel.ChannelHandlerContext;

import com.rwproto.MsgDef.Command;
import com.rwproto.ReConnectionProtos.ReConnectResponse;
import com.rwproto.ReConnectionProtos.ReConnectResultType;
import com.rwproto.RequestProtos.Request;
import com.rwproto.ResponseProtos.Response;

public class ReconnectCommon {

	public static ReconnectCommon instance = new ReconnectCommon();

	public void reLoginGame(FsNettyControler nettyControler, ChannelHandlerContext ctx, Request request) {
		returnReconnectRequest(nettyControler, ctx, request, ReConnectResultType.RETURN_GAME_LOGIN);
	}

	public void returnReconnectRequest(FsNettyControler nettyControler, ChannelHandlerContext ctx, Request request, ReConnectResultType resultType) {
		ReConnectResponse.Builder reConnectRsp = ReConnectResponse.newBuilder();
		reConnectRsp.setResultType(resultType);
		nettyControler.sendResponse(null, request.getHeader(), reConnectRsp.build().toByteString(), ctx);
	}

	public void reconnectSuccess(FsNettyControler nettyControler, ChannelHandlerContext ctx, Request request) {
		returnReconnectRequest(nettyControler, ctx, request, ReConnectResultType.RECONNECT_SUCCESS);
	}

	public static ReconnectCommon getInstance() {
		return instance;
	}

}
