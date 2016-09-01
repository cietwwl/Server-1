package com.rw.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolMessageEnum;
import com.playerdata.Player;
import com.rwbase.dao.user.UserGameData;
import com.rwproto.RequestProtos.Request;


public interface FsService <Msg extends GeneratedMessage, Type extends ProtocolMessageEnum>{
	
	/**
	 * 消息分发 逻辑处理
	 * @param request
	 * @param player
	 * @return
	 */
	public ByteString doTask(Msg request, Player player);
	
	/**
	 * 消息解析统一入口
	 * @param request 返回消息请求
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public Msg parseMsg(Request request) throws InvalidProtocolBufferException;
	
	/**
	 * 返回消息处理类型
	 * @param request
	 * @return
	 */
	public Type getMsgType(Msg request);
	
}
