package com.rw.handler.daily;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.actionHelper.ActionEnum;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.DailyActivityProtos.DailyActivityInfo;
import com.rwproto.DailyActivityProtos.EDailyActivityRequestType;
import com.rwproto.DailyActivityProtos.MsgDailyActivityRequest;
import com.rwproto.DailyActivityProtos.MsgDailyActivityResponse;
import com.rwproto.DailyActivityProtos.eDailyActivityResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class DailyHandler implements RandomMethodIF{
	private static DailyHandler handler = new DailyHandler();
	private static final Command command = Command.MSG_DAILY_ACTIVITY;
	private static final String functionName = "日常模块";
	
	private static ConcurrentHashMap<String, Integer> funcStageMap = new ConcurrentHashMap<String, Integer>();
	
	public static DailyHandler getInstance(){
		return handler;
	}
	
	public boolean processDaily(Client client){
		MsgDailyActivityRequest.Builder request = MsgDailyActivityRequest.newBuilder();
		request.setRequestType(EDailyActivityRequestType.Task_List);
		return client.getMsgHandler().sendMsg(Command.MSG_DAILY_ACTIVITY, request.build().toByteString(), new DailyMsgReceier(command, functionName, "日常"));
	}
	
	public void achieveDailyReward(Client client){
		DailyActivityDataHolder dailyActivityDataHolder = client.getDailyActivityDataHolder();
		List<DailyActivityInfo> taskList = dailyActivityDataHolder.getTaskList();
		DailyActivityInfo info = null;
		for (DailyActivityInfo dailyActivityInfo : taskList) {
			if(dailyActivityInfo.getCanGetReward() == 1){
				info = dailyActivityInfo;
				break;
			}
		}
		if(info != null){
			dailyActivityDataHolder.setNum(dailyActivityDataHolder.getNum() + 1);
			MsgDailyActivityRequest.Builder request = MsgDailyActivityRequest.newBuilder();
			request.setRequestType(EDailyActivityRequestType.Task_Finish);
			request.setTaskId(info.getTaskId());
			client.getMsgHandler().sendMsg(Command.MSG_DAILY_ACTIVITY, request.build().toByteString(), new DailyMsgReceier(command, functionName, "日常"));
		}else{
			RobotLog.info("所有已完成任务的奖励都已领取,此次机器人测试完成了任务数为 = " + dailyActivityDataHolder.getNum());
		}
	}
	
	private boolean responseAchieveDailyReward(Client client, MsgDailyActivityResponse response) throws Exception {
		eDailyActivityResultType resultType = response.getResultType();
		switch (resultType) {
		case SUCCESS:
			RobotLog.info("完成任务成功");
			achieveDailyReward(client);
			return true;
		default:
			RobotLog.info("完成任务失败");
			return false;
		}
		
	}
	
	private boolean processResponse(Client client, MsgDailyActivityResponse response) throws Exception{
		DailyActivityDataHolder dailyActivityDataHolder = client.getDailyActivityDataHolder();
		EDailyActivityRequestType responseType = response.getResponseType();
		switch (responseType) {
		case Task_List:
			dailyActivityDataHolder.setTaskList(response.getTaskListList());
			achieveDailyReward(client);
			break;
		case Task_Finish:
			dailyActivityDataHolder.setTaskList(response.getTaskListList());
			return responseAchieveDailyReward(client, response);
		default:
			break;
		}
		RobotLog.info("获取任务列表");		
		return true;
	}
	
	private class DailyMsgReceier extends PrintMsgReciver{

		public DailyMsgReceier(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean execute(Client client, Response response) {
			// TODO Auto-generated method stub
			ByteString bs = response.getSerializedContent();
			try{
				MsgDailyActivityResponse resp = MsgDailyActivityResponse.parseFrom(bs);
				if(resp == null){
					RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
					return false;
				}
				eDailyActivityResultType resultype = resp.getResultType();
				switch (resultype) {
				case SUCCESS:
					return processResponse(client, resp);
					 
				case FAIL:
					throw new Exception("获取任务失败");
				default:
					throw new Exception("出现了未知的状况");
				}
				
			}catch(Exception ex){
				RobotLog.fail(parseFunctionDesc() + "失败", ex);
			}
			return false;
		}
		
		
		private String parseFunctionDesc() {
			return functionName + "[" + protoType + "] ";
		}
	}

	@Override
	public boolean executeMethod(Client client) {
		Integer stage = funcStageMap.get(client.getAccountId());
		if(null == stage){
			stage = new Integer(0);
			funcStageMap.put(client.getAccountId(), stage);
		}
		switch (stage) {
		case 0:
			funcStageMap.put(client.getAccountId(), 1);
			client.getRateHelper().addActionToQueue(ActionEnum.Daily);
			return processDaily(client);
		case 1:
			funcStageMap.put(client.getAccountId(), 0);
			achieveDailyReward(client);
			return true;
		default:
			return true;
		}
	}
}
