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

public class TaskService implements FsService<TaskRequest, eTaskRequestType> {

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

	@Override
	public ByteString doTask(TaskRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eTaskRequestType reqType = request.getRequestType();
			switch (reqType) {
			case GetReward:
				result =getReward(request.getId(),player);
				break;
			default:
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public TaskRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		TaskRequest req = TaskRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public eTaskRequestType getMsgType(TaskRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}
}
