package com.rw.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolMessageEnum;
import com.playerdata.Player;
import com.rwproto.RequestProtos.Request;


public interface FsService <Msg extends GeneratedMessage, Type extends ProtocolMessageEnum>{
	
	/**
	 * 消息分发 
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString doTask(Msg request, Player player);
	
	/**
	 * 消息解析
	 * @param request 返回消息请求
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public Msg parseMsg(Request request) throws InvalidProtocolBufferException;
	
	/**
	 * 消息处理类型
	 * @param request
	 * @return
	 */
	public Type getMsgType(Msg request);
	
}
