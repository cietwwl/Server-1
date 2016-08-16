package com.rw.handler.sign;

import java.util.Calendar;
import java.util.List;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.SignServiceProtos.ERequestType;
import com.rwproto.SignServiceProtos.EResultType;
import com.rwproto.SignServiceProtos.MsgSignRequest;
import com.rwproto.SignServiceProtos.MsgSignResponse;

public class SignHandler {
	private static SignHandler handler = new SignHandler();
	private static final Command command = Command.MSG_SIGN;
	private static final String functionName = "签到模块";
	public static SignHandler getInstance(){
		return handler;
	}
	
	public boolean processsSign(Client client){
		boolean result;
		SignDataHolder signDataHolder = client.getSignDataHolder();
		List<String> signDataList = signDataHolder.getSignDataList();
		if (signDataList == null || signDataList.size() <= 0) {
			MsgSignRequest.Builder request = MsgSignRequest.newBuilder();
			request.setRequestType(ERequestType.SIGNDATA_BACK);
			result = client.getMsgHandler().sendMsg(Command.MSG_SIGN, request.build().toByteString(), new SignMsgReceier(command, functionName, "签到"));
		} else {
			result = processRequestSign(client);
		}
		return result;
	}
	
	public boolean processSignReward(Client client){
		MsgSignRequest.Builder request = MsgSignRequest.newBuilder();
		request.setRequestType(ERequestType.SIGN_REWARD);
		boolean result = client.getMsgHandler().sendMsg(Command.MSG_SIGN, request.build().toByteString(), new SignMsgReceier(command, functionName, "签到领奖励"));
		return result;
	}
	
	private boolean processRequestSign(Client client){
		SignDataHolder signDataHolder = client.getSignDataHolder();
		List<String> signDataList = signDataHolder.getSignDataList();
		
		String maxSignId = "";
		int maxDay = 0;
		for (String value : signDataList) {
			String[] split = value.split(",");
			String signId = split[0];
			String[] split2 = signId.split("_");
			int day = Integer.parseInt(split2[2]);
			if(day > maxDay){
				maxDay = day;
				maxSignId = signId;
			}
		}
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		if (maxDay < currentDay) {
			MsgSignRequest.Builder request = MsgSignRequest.newBuilder();
			request.setRequestType(ERequestType.SIGN);
			request.setSignId(maxSignId);
			client.getMsgHandler().sendMsg(Command.MSG_SIGN, request.build().toByteString(), new SignMsgReceier(command, functionName, "签到"));
		}
		return processSignReward(client);
	}
	
	public void initSignData(Client client, List<String> signDataList, int year, int month, int reSignCount){

		
		SignDataHolder signDataHolder = client.getSignDataHolder();
		signDataHolder.setSignDataList(signDataList);
		signDataHolder.setReSignCount(reSignCount);

		
		processRequestSign(client);
	}
	
	
	private class SignMsgReceier extends PrintMsgReciver{

		public SignMsgReceier(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean execute(Client client, Response response) {
			// TODO Auto-generated method stub
			ByteString bs = response.getSerializedContent();
			try{
				MsgSignResponse resp = MsgSignResponse.parseFrom(bs);
				if(resp == null){
					RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
					return false;
				}
				EResultType resultype = resp.getResultype();
				switch (resultype) {
				case INIT_DATA:
					initSignData(client, resp.getTagSignDataList(), resp.getYear(), resp.getMonth(), resp.getReSignCount());
					return true;
				case NEED_REFRESH:
					initSignData(client, resp.getTagSignDataList(), resp.getYear(), resp.getMonth(), resp.getReSignCount());
					return true;
				
				case SUCCESS:
					RobotLog.info(parseFunctionDesc() + "成功");
					return true;
				case SIGN_REWARD_SUCCESS:
					RobotLog.info(parseFunctionDesc() + "成功");
					return true;
				case NOT_ENOUGH_DIAMOND:
					RobotLog.fail("砖石不足");
					return true;
				case FAIL:
					RobotLog.fail(resp.getResultMsg());
					return true;
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
}
