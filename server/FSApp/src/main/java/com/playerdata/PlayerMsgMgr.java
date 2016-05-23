package com.playerdata;

import io.netty.channel.ChannelHandlerContext;

import com.common.GameUtil;
import com.google.protobuf.ByteString;
import com.rw.netty.UserChannelMgr;
import com.rwproto.MsgDef;
import com.rwproto.ResponseProtos;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.ResponseProtos.ResponseHeader;

public class PlayerMsgMgr {

	private boolean m_bIgnoreSendMsg = false;
	final private String userId;
	
	public PlayerMsgMgr(String userIdP){
		this.userId = userIdP;
	}
	
	/**
	 * 向Player发送消息 modified by: kevin
	 * 
	 * @param Cmd
	 * @param pBuffer
	 * @param ctx
	 */
	public void sendMsg(MsgDef.Command Cmd, ByteString pBuffer, boolean needToCache) {
		
//		System.out.println("-----------------------------------"+userId+":"+Cmd);
		if (m_bIgnoreSendMsg) {
			return;
		}

		try {
			ChannelHandlerContext ctx = UserChannelMgr.get(userId);
			if (ctx == null) {
				return;
			}
			Response.Builder builder = Response.newBuilder().setHeader(ResponseHeader.newBuilder().setCommand(Cmd).setToken("").setStatusCode(200));
			if (pBuffer != null) {
				builder.setSerializedContent(pBuffer);
			}

			ResponseProtos.Response.Builder response = ResponseProtos.Response.newBuilder();
			ResponseHeader.Builder header = ResponseHeader.newBuilder();
			header.mergeFrom(builder.getHeader());
			header.setStatusCode(200);
			response.setHeader(header.build());
			response.setSerializedContent(builder.getSerializedContent());
			if (!GameUtil.checkMsgSize(response, userId)) {
				return;
			}
			ctx.channel().writeAndFlush(response.build());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	


	public void setIgnoreSendMsg(boolean m_bIgnoreSendMsg) {
		this.m_bIgnoreSendMsg = m_bIgnoreSendMsg;
	}
}
