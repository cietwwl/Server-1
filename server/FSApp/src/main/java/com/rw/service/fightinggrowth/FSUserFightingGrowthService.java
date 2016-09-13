package com.rw.service.fightinggrowth;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rw.service.fightinggrowth.msgprocesser.RequestUIDataProcesser;
import com.rw.service.fightinggrowth.msgprocesser.RequestUpgradeTitleProcesser;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

public class FSUserFightingGrowthService implements FsService<Request, Command> {

	private static final Map<Integer, IMsgProcesser> _processers = new HashMap<Integer, IMsgProcesser>();
	
	static {
		_processers.put(MsgDef.Command.MSG_FIGHTING_GROWTH_REQUEST_UI_DATA_VALUE, new RequestUIDataProcesser());
		_processers.put(MsgDef.Command.MSG_FIGHTING_GROWTH_REQUEST_UPGRADE_VALUE, new RequestUpgradeTitleProcesser());
	}
	
	@Override
	public ByteString doTask(Request request, Player player) {
		return _processers.get(request.getHeader().getCommand().getNumber()).process(player, request);
	}

	@Override
	public Request parseMsg(Request request) throws InvalidProtocolBufferException {
		return request;
	}

	@Override
	public Command getMsgType(Request request) {
		return request.getHeader().getCommand();
	}

}
