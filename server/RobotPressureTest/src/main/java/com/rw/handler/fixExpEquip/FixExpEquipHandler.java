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
	
	private static int expId= 806505;//经验道具左侧用这个！！！ 
	private static int expIdRight= 806510;//经验道具左侧用这个！！！ 
	
	public boolean doExpEquip(Client client,int heronum,int equipid,int type){
		boolean issucc = false;
		if(type == Exp_level_up){
			issucc = doLevelUp(client,heronum,equipid);
		}else if(type == Exp_quality_up){
			doLevelUp(client,heronum,equipid);
			issucc = doQualityUp(client,heronum,equipid);
		}else if(type == Exp_star_up){			
			issucc = doStarUp(client,heronum,equipid);
		}else if(type == Exp_star_down){			
			doStarUp(client,heronum,equipid);
			issucc = doStarDown(client,heronum,equipid);
		}
		return issucc;
	}


	private boolean doStarDown(Client client,int heronum, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_star_down);
		Map<Integer, String> equipMap = client.getFixExpEquipDataItemHolder().getEquipMap();
		if(equipid+heronum*2 +1> equipMap.size()){
			RobotLog.fail("fixExpequipHandler[send.doStarDown]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = equipMap.get(equipid+ heronum*2);
		if(tmp==null){
			RobotLog.fail("fixExpequipHandler[send.doStarDown]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixExpequipHandler[send.doStarDown] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("已是最低") > 0){
							
							return true;
						}
						RobotLog.fail("fixExpequipHandler[send.doStarDown] 服务器处理获取列表消息失败 " + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixExpequipHandler[send.doStarDown]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixExpequipHandler[send.doStarDown] 获取列表成功");
				return true;
			}

		});
		return success;		
	}


	private boolean doStarUp(Client client,int heronum, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_star_up);
		Map<Integer, String> equipMap = client.getFixExpEquipDataItemHolder().getEquipMap();
	
		if(equipid+heronum*2 +1> equipMap.size()){
			RobotLog.fail("fixExpequipHandler[send.doStarUp]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = equipMap.get(equipid+ heronum*2);
		if(tmp==null){
			RobotLog.fail("fixExpequipHandler[send.doStarUp]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixExpequipHandler[send.doStarUp] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("已达最高") > 0){
							
							return true;
						}
						RobotLog.fail("fixExpequipHandler[send.doStarUp] 服务器处理获取列表消息失败 " + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixExpequipHandler[send.doStarUp]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixExpequipHandler[send.doStarUp] 获取列表成功");
				return true;
			}

		});
		return success;		
	}


	private boolean doQualityUp(Client client,int heronum, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_quality_up);
		Map<Integer, String> equipMap = client.getFixExpEquipDataItemHolder().getEquipMap();
		if(equipid+heronum*2 +1> equipMap.size()){
			RobotLog.fail("fixExpequipHandler[send.doQualityUp]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = equipMap.get(equipid+ heronum*2);
		if(tmp==null){
			RobotLog.fail("fixExpequipHandler[send.doQualityUp]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixExpequipHandler[send.doQualityUp] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("已经达到最品质") > 0){
							
							return true;
						}
						RobotLog.fail("fixExpequipHandler[send.doQualityUp] 服务器处理获取列表消息失败 " +  rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixExpequipHandler[send.doQualityUp]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixExpequipHandler[send.doQualityUp] 获取列表成功");
				return true;
			}

		});
		return success;		
	}


	private boolean doLevelUp(Client client,int heronum, int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Exp_level_up);
		Map<Integer, String> equipMap = client.getFixExpEquipDataItemHolder().getEquipMap();
		if(equipid+heronum*2 +1> equipMap.size()){
			RobotLog.fail("fixExpequipHandler[send.doLevelUp]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = equipMap.get(equipid+ heronum*2);
		if(tmp==null){
			RobotLog.fail("fixExpequipHandler[send.doLevelUp]  传入的参数没获得对应的数据");
			return false;
		}
		String[] tmps = tmp.split("_");
		req.setOwnerId(tmps[0]);
		req.setEquipId(tmp);
		
		Builder item = SelectItem.newBuilder();
		if(equipid%2 == 1){
			item.setModelId(expIdRight);
		}else{
			item.setModelId(expId);
		}
		item.setCount(59);
		
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
						RobotLog.fail("fixExpequipHandler[send.doLevelUp] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("不能超过英雄等级") > 0){
							
							return true;
						}
						
						RobotLog.fail("fixExpequipHandler[send.doLevelUp] 服务器处理获取列表消息失败 " +  rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixExpequipHandler[send.doLevelUp]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixExpequipHandler[send.doLevelUp] 获取列表成功");
				return true;
			}

		});
		return success;		
	}	
}