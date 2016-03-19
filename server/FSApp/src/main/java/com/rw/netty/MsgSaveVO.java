package com.rw.netty;

import com.google.protobuf.ByteString;
import com.rwproto.MsgDef;

public class MsgSaveVO {

	public MsgDef.Command Cmd; 
	public ByteString pBuffer;
	public MsgSaveVO(MsgDef.Command Cmd,ByteString pBuffer)
	{
		this.Cmd=Cmd;
		this.pBuffer=pBuffer;
	}
}
