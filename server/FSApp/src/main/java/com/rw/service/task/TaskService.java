package com.rw.service.task;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TaskProtos.TaskRequest;
import com.rwproto.TaskProtos.TaskResponse;
import com.rwproto.TaskProtos.eTaskRequestType;
import com.rwproto.TaskProtos.eTaskResultType;

public class TaskService implements FsService {

	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			TaskRequest req = TaskRequest.parseFrom(request.getBody().getSerializedContent());
			eTaskRequestType reqType = req.getRequestType();
			switch (reqType) {
			case GetReward:
				if(req.getId() <= 0) result = getAllReward(player);
				else result =getReward(req.getId(),player);
				break;
			default:
				break;
			}
		}catch(InvalidProtocolBufferException e){
			e.printStackTrace();
		}
		return result;
	}

	private ByteString getReward(int id, Player player) {
		TaskResponse.Builder resp = TaskResponse.newBuilder();
		int result = player.getTaskMgr().getReward(id);
		resp.setReslutType(eTaskResultType.FAIL);
		if(result == -3){
			resp.setReslutValue("任务尚未完成！");
		}else if(result == 1){
			resp.setReslutType(eTaskResultType.SUCCESS);
			resp.setId(id);
		}
		return resp.build().toByteString();
	}
	
	private ByteString getAllReward(Player player) {
		TaskResponse.Builder resp = TaskResponse.newBuilder();
		int result = player.getTaskMgr().getAllReward();
		if(result != 1){
			resp.setReslutType(eTaskResultType.FAIL);
			resp.setReslutValue("数据错误！");
		}else{
			resp.setReslutType(eTaskResultType.SUCCESS);
		}
		return resp.build().toByteString();
	}
}
