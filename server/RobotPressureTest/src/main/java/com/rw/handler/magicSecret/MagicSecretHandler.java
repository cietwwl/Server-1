package com.rw.handler.magicSecret;


import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msRequestType;
import com.rwproto.MagicSecretProto.msResultType;
import com.rwproto.MagicSecretProto.msRewardBox;
import com.rwproto.MagicSecretProto.msRewardBox.Builder;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;





public class MagicSecretHandler {
	private static final int GET_SELF_MS_RANK =9;
	private static final int GET_MS_RANK = 1;//查看
	
	private static final int CHANGE_ARMY =7;
	private static final int ENTER_MS_FIGHT =2;//战斗
	
	private static final int OPEN_REWARD_BOX =6;//打开箱子
	private static final int GIVE_UP_REWARD_BOX =10;
	
	private static final int EXCHANGE_BUFF =5;//换buff
	private static final int GIVE_UP_BUFF =11;
	
	private static  MagicSecretHandler handler = new MagicSecretHandler();
	
	private static String dungeonId = "";
	
	public static  MagicSecretHandler getHandler() {
		return handler;
	}
	
	public boolean doType(Client client, int id) {
		boolean isucc = false;
		if(id == GET_MS_RANK){			
			isucc=getMsRank(client);
		}else if(id == ENTER_MS_FIGHT){
			isucc = changeTeam(client);
			RobotLog.fail("战斗前的设置队伍反馈结果="+isucc);
			isucc=fight(client);
		}else if(id == OPEN_REWARD_BOX){
			isucc = getReward(client);
			RobotLog.fail("领取前的生成奖励反馈结果="+isucc);			
			isucc=openBox(client);//奖励需要战斗来生成，多次连续申请会失败
			RobotLog.fail("领取道具反馈结果="+isucc);	
			isucc=exchangeBuff(client);//坑爹的协议，兑换buff和奖励的内容在战斗时已推送，所以机器人无法分开操作功能			
		}
		return isucc;
	}

	public boolean getMsRank(Client client) {		
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();		
		req.setReqType(msRequestType.GET_MS_RANK);		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send] 转换响应消息为null");
						return false;
					}
					msResultType result =rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send] 服务器处理消息失败 " + result);
						return false;
					}				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send] 失败", e);
					return false;
				}				
				return true;
			}			
		});		
		return success;		
	}
	
	private boolean changeTeam(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();		
		req.setReqType(msRequestType.CHANGE_ARMY);		
		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send] 转换响应消息为null");
						return false;
					}
					msResultType result =rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send] 服务器处理消息失败 " + result);
						return false;
					}				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send] 失败", e);
					return false;
				}				
				return true;
			}			
		});		
		return success;		
	}
	
	
	
	
	
	
	
	private boolean fight(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();		
		req.setReqType(msRequestType.ENTER_MS_FIGHT);
		dungeonId = "";
		Map<String,UserMagicSecretData> userMagicSecretDatalist = client.getMagicSecretHolder().getList();
		UserMagicSecretData userMagicSecretData = userMagicSecretDatalist.get(client.getUserId());
		int tmp = userMagicSecretData.getMaxStageID()+1;
		dungeonId = tmp+"_"+3;//难度还tm不能随时改的0.0我区，策划这么逗比，抄刀塔都抄的不痛快要标新立异
		req.setDungeonId(dungeonId);
		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send] 转换响应消息为null");
						return false;
					}
					msResultType result =rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send] 服务器处理消息失败 " + result);
						return false;
					}				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send] 失败", e);
					return false;
				}				
				return true;
			}			
		});		
		return success;		
	}
	
	private boolean getReward(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();		
		req.setReqType(msRequestType.GET_MS_SINGLE_REWARD);
		dungeonId = "";
		Map<String,UserMagicSecretData> userMagicSecretDatalist = client.getMagicSecretHolder().getList();
		UserMagicSecretData userMagicSecretData = userMagicSecretDatalist.get(client.getUserId());
		int tmp = userMagicSecretData.getMaxStageID()+1;
		dungeonId = tmp+"_"+3;//难度还tm不能随时改的0.0我区，策划这么逗比，抄刀塔都抄的不痛快要标新立异
		req.setDungeonId(dungeonId);
		req.setFinishState("3");
		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send] 转换响应消息为null");
						return false;
					}
					msResultType result =rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send] 服务器处理消息失败 " + result);
						return false;
					}				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send] 失败", e);
					return false;
				}				
				return true;
			}			
		});		
		return success;		
	}
	
	
	private boolean openBox(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();		
		req.setReqType(msRequestType.OPEN_REWARD_BOX);
		Map<String,MagicChapterInfo> magiChapterInfolist = client.getMagicChapterInfoHolder().getList();
		if(magiChapterInfolist.size()>0){
		MagicChapterInfo magiChapterInfo = magiChapterInfolist.get(client.getUserId());	
			req.setChapterId(magiChapterInfo.getChapterId());
		}else{
			req.setChapterId(client.getMagicSecretHolder().getChapterId());
		}		
		Builder box = msRewardBox.newBuilder();
		box.setBoxID("2");
		box.setBoxCount(1);
		req.setRwdBox(box);
		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send] 转换响应消息为null");
						return false;
					}
					msResultType result =rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send] 服务器处理消息失败 " + result);
						return false;
					}				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send] 失败", e);
					return false;
				}				
				return true;
			}			
		});		
		return success;	
	}	
	
	private boolean exchangeBuff(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();		
		req.setReqType(msRequestType.EXCHANGE_BUFF);
		Map<String,MagicChapterInfo> magiChapterInfolist = client.getMagicChapterInfoHolder().getList();
		if(magiChapterInfolist.size()>0){
		MagicChapterInfo magiChapterInfo = magiChapterInfolist.get(client.getUserId());	
			req.setChapterId(magiChapterInfo.getChapterId());
			req.setBuffId(magiChapterInfo.getUnselectedBuff().get(0)+"");
		}else{
			req.setChapterId(client.getMagicSecretHolder().getChapterId());
			req.setBuffId("1");
		}
		
		
		

		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send] 转换响应消息为null");
						return false;
					}
					msResultType result =rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send] 服务器处理消息失败 " + result);
						return false;
					}				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send] 失败", e);
					return false;
				}				
				return true;
			}			
		});		
		return success;	
	}	
}