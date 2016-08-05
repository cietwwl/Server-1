package com.rw.handler.GroupCopy.msg;

import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCopyMsgReceier extends PrintMsgReciver{

	public GroupCopyMsgReceier(Command command, String functionName, String protoType) {
		super(command, functionName, protoType);
	}

	@Override
	public boolean execute(Client client, Response response) {
		return false;
	}

}
