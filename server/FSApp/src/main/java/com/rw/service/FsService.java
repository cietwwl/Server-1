package com.rw.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ProtocolMessageEnum;
import com.playerdata.Player;
import com.rwbase.dao.user.UserGameData;
import com.rwproto.RequestProtos.Request;


public interface FsService <Msg extends GeneratedMessage, Type extends ProtocolMessageEnum>{
	
	public ByteString doTask(Msg request, Player player);
	
	public Msg parseMsg(Request request) throws InvalidProtocolBufferException;
	
	public Type getMsgType(Msg request);
	
}
