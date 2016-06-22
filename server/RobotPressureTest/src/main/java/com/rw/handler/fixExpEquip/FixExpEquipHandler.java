package com.rw.handler.fixExpEquip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.FixEquipProto.CommonReqMsg;
import com.rwproto.FixEquipProto.CommonRspMsg;
import com.rwproto.FixEquipProto.ExpLevelUpReqParams;
import com.rwproto.FixEquipProto.RequestType;
import com.rwproto.FixEquipProto.SelectItem;
import com.rwproto.FixEquipProto.SelectItem.Builder;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;



public class FixExpEquipHandler {

	private static FixExpEquipHandler instance = new FixExpEquipHandler();

	public static FixExpEquipHandler instance() {
		return instance;
	}
	private static int Exp_level_up = 6;
	private static int Exp_quality_up = 7;
	private static int Exp_star_up = 8;
	private static int Exp_star_down = 9;
	
	private static int expId= 806501;//经验道具统一用这个！！！ 
	
	public boolean doExpEquip(Client client,int equipid,int type){
		boolean issucc = false;
		if(type == Exp_level_up){
			issucc = doLevelUp(client,equipid);
		}else if(type == Exp_quality_up){
			issucc = doQualityUp(client,equipid);
		}else if(type == Exp_star_up){			
			issucc = doStarUp(client,equipid);
		}else if(type == Exp_star_down){			
			issucc = doStarDown(client,equipid);
		}
		return issucc;
	}


	private boolean doStarDown(Client client, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_star_down);
		Map<Integer,String> giftList = client.getFixExpEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send]  传入的参数没获得对应的数据");
			return false;
		}
		String[] tmps = tmp.split("_");
		req.setOwnerId(tmps[0]);
		req.setEquipId(tmp);
		
		
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FIX_EQUIP, req.build().toByteString(), new MsgReciver() {
			
			public Command getCmd() {
				return Command.MSG_FIX_EQUIP;
			}

			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					CommonRspMsg rsp = CommonRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("fixequipHandler[send] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						RobotLog.fail("fixequipHandler[send] 服务器处理获取列表消息失败 " + result);
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send] 获取列表成功");
				return true;
			}

		});
		return success;		
	}


	private boolean doStarUp(Client client, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_star_up);
		Map<Integer,String> giftList = client.getFixExpEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send]  传入的参数没获得对应的数据");
			return false;
		}
		String[] tmps = tmp.split("_");
		req.setOwnerId(tmps[0]);
		req.setEquipId(tmp);
		
		
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FIX_EQUIP, req.build().toByteString(), new MsgReciver() {
			
			public Command getCmd() {
				return Command.MSG_FIX_EQUIP;
			}

			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					CommonRspMsg rsp = CommonRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("fixequipHandler[send] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						RobotLog.fail("fixequipHandler[send] 服务器处理获取列表消息失败 " + result);
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send] 获取列表成功");
				return true;
			}

		});
		return success;		
	}


	private boolean doQualityUp(Client client, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_quality_up);
		Map<Integer,String> giftList = client.getFixExpEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send]  传入的参数没获得对应的数据");
			return false;
		}
		String[] tmps = tmp.split("_");
		req.setOwnerId(tmps[0]);
		req.setEquipId(tmp);
		
		
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FIX_EQUIP, req.build().toByteString(), new MsgReciver() {
			
			public Command getCmd() {
				return Command.MSG_FIX_EQUIP;
			}

			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					CommonRspMsg rsp = CommonRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("fixequipHandler[send] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						RobotLog.fail("fixequipHandler[send] 服务器处理获取列表消息失败 " + result);
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send] 获取列表成功");
				return true;
			}

		});
		return success;		
	}


	private boolean doLevelUp(Client client, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_level_up);
		Map<Integer,String> giftList = client.getFixExpEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send]  传入的参数没获得对应的数据");
			return false;
		}
		String[] tmps = tmp.split("_");
		req.setOwnerId(tmps[0]);
		req.setEquipId(tmp);
		
		Builder item = SelectItem.newBuilder();
		item.setModelId(expId);
		item.setCount(30);
		
		SelectItem tmpitem = item.buildPartial();	
		com.rwproto.FixEquipProto.ExpLevelUpReqParams.Builder expIdAndNum = ExpLevelUpReqParams.newBuilder();
		expIdAndNum.addSelectItem(tmpitem);
		req.setExpLevelUpReqParams(expIdAndNum);
		
		
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_FIX_EQUIP, req.build().toByteString(), new MsgReciver() {
			
			public Command getCmd() {
				return Command.MSG_FIX_EQUIP;
			}

			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					CommonRspMsg rsp = CommonRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("fixequipHandler[send] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						RobotLog.fail("fixequipHandler[send] 服务器处理获取列表消息失败 " + result);
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send] 获取列表成功");
				return true;
			}

		});
		return success;		
	}	
}