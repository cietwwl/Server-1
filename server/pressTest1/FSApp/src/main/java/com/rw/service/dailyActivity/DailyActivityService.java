package com.rw.service.dailyActivity;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.DailyActivityMgr;
import com.rw.service.FsService;
import com.rwproto.DailyActivityProtos.EDailyActivityRequestType;
import com.rwproto.DailyActivityProtos.MsgDailyActivityRequest;
import com.rwproto.DailyActivityProtos.eDailyActivityResultType;
import com.rwproto.RequestProtos.Request;

public class DailyActivityService implements FsService{
	
	//private TaskHandler taskHandler = TaskHandler.getInstance();

	public ByteString doTask(Request request, Player player) 
	{
		ByteString responseData = null;
		try 
		{
			MsgDailyActivityRequest taskRequest = MsgDailyActivityRequest.parseFrom(request.getBody().getSerializedContent());
			EDailyActivityRequestType requestType = taskRequest.getRequestType();
			switch(requestType) 
			{
				case Task_List:
					return DailyActivityHandler.getInstance().getTaskList(player, taskRequest);
				case Task_Finish:
					return DailyActivityHandler.getInstance().taskFinish(player, taskRequest);
			}
		}catch (InvalidProtocolBufferException e) 
		{
			e.printStackTrace();
		}
		return responseData;
	}

}
