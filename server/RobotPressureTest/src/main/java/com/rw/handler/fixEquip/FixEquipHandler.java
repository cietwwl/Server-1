package com.rw.handler.fixEquip;

import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.FixEquipProto.CommonReqMsg;
import com.rwproto.FixEquipProto.CommonRspMsg;
import com.rwproto.FixEquipProto.RequestType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;



public class FixEquipHandler {

	private static FixEquipHandler instance = new FixEquipHandler();
	
	
	private static int Norm_level_up = 1;
	private static int Norm_level_up_one_key = 5;
	private static int Norm_quality_up = 2;
	private static int Norm_star_up = 3;
	private static int Norm_star_down = 4;
	
	
	public static FixEquipHandler instance() {
		return instance;
	}
	
	public boolean doEquip(Client client,int heronum,int equipid,int type){
		boolean issucc = false;
		if(type == Norm_level_up){
			issucc = doLevelUp(client,heronum,equipid);
		}else if(type == Norm_level_up_one_key){
			for(int i=0;i< 4;i++){
				issucc = doLevelUpOneKey(client,heronum,i);
			}
		}else if(type == Norm_quality_up){			
			issucc = doQualityUp(client,heronum,equipid);
		}else if(type == Norm_star_up){			
			issucc = doStarUp(client,heronum,equipid);
		}else if(type == Norm_star_down){
			issucc = doStarDown(client,heronum,equipid);
		}
		return issucc;
	}

	private boolean doLevelUp(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_level_up);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid+hero*4);
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
	
	private boolean doLevelUpOneKey(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_level_up_one_key);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid+hero*4);
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
	
	private boolean doQualityUp(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_quality_up);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid+hero*4);
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
	
	private boolean doStarUp(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_star_up);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid+hero*4);
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
	
	private boolean doStarDown(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_star_down);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		String tmp = giftList.get(equipid+hero*4);
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
	
}