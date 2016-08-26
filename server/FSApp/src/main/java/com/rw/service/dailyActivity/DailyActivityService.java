package com.rw.service.dailyActivity;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.DailyActivityMgr;
import com.rw.service.FsService;
import com.rwproto.DailyActivityProtos.EDailyActivityRequestType;
import com.rwproto.DailyActivityProtos.MsgDailyActivityRequest;
import com.rwproto.DailyActivityProtos.eDailyActivityResultType;
import com.rwproto.RequestProtos.Request;

public class DailyActivityService implements FsService<MsgDailyActivityRequest, EDailyActivityRequestType>{

	@Override
	public ByteString doTask(MsgDailyActivityRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString responseData = null;
		try 
		{
			EDailyActivityRequestType requestType = request.getRequestType();
			switch(requestType) 
			{
				case Task_List:
					return DailyActivityHandler.getInstance().getTaskList(player, request);
				case Task_Finish:
					return DailyActivityHandler.getInstance().taskFinish(player, request);
			}
		}catch (Exception e) 
		{
			e.printStackTrace();
		}
		return responseData;
	}

	@Override
	public MsgDailyActivityRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgDailyActivityRequest taskRequest = MsgDailyActivityRequest.parseFrom(request.getBody().getSerializedContent());
		return taskRequest;
	}

	@Override
	public EDailyActivityRequestType getMsgType(MsgDailyActivityRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}

}
