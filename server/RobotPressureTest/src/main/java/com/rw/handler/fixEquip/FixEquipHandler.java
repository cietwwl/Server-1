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
			for(int i=0;i< 4;i++){
				doLevelUpOneKey(client,heronum,i);
			}
			issucc = doQualityUp(client,heronum,equipid);
		}else if(type == Norm_star_up){			
			issucc = doStarUp(client,heronum,equipid);
		}else if(type == Norm_star_down){
			issucc=doStarUp(client,heronum,equipid);
			issucc = doStarDown(client,heronum,equipid);
		}
		return issucc;
	}

	private boolean doLevelUp(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_level_up);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		if(equipid+hero*4 +1> giftList.size()){
			RobotLog.fail("fixequipHandler[send.levelUp]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = giftList.get(equipid+hero*4);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send.levelUp]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixequipHandler[send.levelUp] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("已经达到最高级") > 0){
							return true;
						}
							
						RobotLog.fail("fixequipHandler[send.levelUp]服务器处理获取列表消息失败 !" + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send.levelUp]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send.levelUp] 获取列表成功");
				return true;
			}

		});
		return success;		
		
	}
	
	private boolean doLevelUpOneKey(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_level_up_one_key);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		if(equipid+hero*4 +1> giftList.size()){
			RobotLog.fail("fixequipHandler[send.levelUpOneKey]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = giftList.get(equipid+hero*4);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send.levelUpOneKey]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixequipHandler[send.levelUpOneKey] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						RobotLog.fail("fixequipHandler[send.levelUpOneKey] 服务器处理获取列表消息失败 !" + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send.levelUpOneKey]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send.levelUpOneKey] 获取列表成功");
				return true;
			}

		});
		return success;				
	}
	
	private boolean doQualityUp(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_quality_up);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		if(equipid+hero*4 +1> giftList.size()){
			RobotLog.fail("fixequipHandler[send.doQualityup]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = giftList.get(equipid+hero*4);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send.doQualityup]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixequipHandler[send.doQualityup] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("已经达到最品质") > 0){
							
							return true;
						}
						
						RobotLog.fail("fixequipHandler[send.doQualityup]服务器处理获取列表消息失败 !" + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send.doQualityup]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send.doQualityup] 获取列表成功");
				return true;
			}

		});
		return success;		
		
	}
	
	private boolean doStarUp(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_star_up);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		if(equipid+hero*4 +1> giftList.size()){
			RobotLog.fail("fixequipHandler[send.doStarUp]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = giftList.get(equipid+hero*4);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send.doStarUp]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixequipHandler[send.doStarUp] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("已达最高星级") > 0){
							
							return true;
						}
						RobotLog.fail("fixequipHandler[send.doStarUp]服务器处理获取列表消息失败 !" + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send.doStarUp]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send.doStarUp] 获取列表成功");
				return true;
			}

		});
		return success;		
		
	}
	
	private boolean doStarDown(Client client,int hero,int equipid) {
		CommonReqMsg.Builder req = CommonReqMsg.newBuilder();
		req.setReqType(RequestType.Norm_star_down);
		List<String> giftList = client.getFixNormEquipDataItemHolder().getEquiplist();
		if(equipid+hero*4 +1> giftList.size()){
			RobotLog.fail("fixequipHandler[send.doStarDown]  输入的英雄编号或装备编号超出");
			return false;
		}
		String tmp = giftList.get(equipid+hero*4);
		if(tmp==null){
			RobotLog.fail("fixequipHandler[send.doStarDown]  传入的参数没获得对应的数据");
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
						RobotLog.fail("fixequipHandler[send.doStarDown] 转换响应消息为null");
						return false;
					}

					boolean result = rsp.getIsSuccess();
					if (result == false) {
						if(rsp.getTipMsg().indexOf("已是最低等级") > 0){
							
							return true;
						}
						RobotLog.fail("fixequipHandler[send.doStarDown] 服务器处理获取列表消息失败 !" + rsp.getTipMsg());
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("fixequipHandler[send.doStarDown]获取列表 失败", e);
					return false;
				}
				RobotLog.info("fixequipHandler[send.doStarDown] 获取列表成功");
				return true;
			}

		});
		return success;		
		
	}
	
}