package com.rw.handler.GroupCopy;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.Client;
import com.rw.Robot;
import com.rw.common.MsgReciver;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.controler.GameLogicTask;
import com.rw.dataSyn.DataSynHelper;
import com.rw.dataSyn.JsonUtil;
import com.rw.handler.GroupCopy.data.GroupCopyDataHolder;
import com.rw.handler.GroupCopy.data.GroupCopyDataVersion;
import com.rw.handler.GroupCopy.data.GroupCopyMapRecord;
import com.rw.handler.GroupCopy.data.GroupCopyMonsterSynStruct;
import com.rw.handler.hero.TableUserHero;
import com.rwbase.common.RandomUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComRspMsg;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminOpenCopyReqMsg;
import com.rwproto.GroupCopyAdminProto.RequestType;
import com.rwproto.GroupCopyBattleProto.CopyBattleRoleStruct;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComReqMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyMonsterData;
import com.rwproto.GroupCopyBattleProto.HeroList;
import com.rwproto.GroupCopyBattleProto.HeroList.Builder;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdRspMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyDonateData;
import com.rwproto.GroupCopyCmdProto.GroupCopyReqType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupCopyHandler {

	private static GroupCopyHandler handler = new GroupCopyHandler();
	private final static String functionName = "帮派副本";
	
	
	private GroupCopyHandler(){
	}
	
	public static GroupCopyHandler getInstance(){
		return handler;
	}
	
	private final static Command cmdAdm  = Command.MSG_GROUP_COPY_ADMIN;
	private final static Command cmdBattle = Command.MSG_GROUP_COPY_BATTLE;
	private final static Command cmdCom = Command.MSG_GROUP_COPY_CMD;
	
	
	
	public void applyCopyInfo(Client client){
		if(client == null){
			return;
		}

		GroupCopyCmdReqMsg.Builder req = GroupCopyCmdReqMsg.newBuilder();
		req.setVersion(GroupCopyDataVersion.getDataVersion());
		req.setReqType(GroupCopyReqType.GET_INFO);
		client.getMsgHandler().sendMsg(cmdCom, req.build().toByteString(), new PrintMsgReciver(cmdCom, functionName,"请求帮派副本主界面") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyCmdRspMsg resp = GroupCopyCmdRspMsg.parseFrom(bs);
					if(resp.getIsSuccess()){
						RobotLog.info(parseFunctionDesc() + "成功");
						return true;
					}else{
						RobotLog.info(parseFunctionDesc() + "失败，当前机器人没有帮派");
						return true;
					}
					
				} catch (Exception e) {
					RobotLog.fail(parseFunctionDesc() + "失败，请求数据时出现异常!", e);
				}
				return false;
			}
			
			private String parseFunctionDesc() {
				return functionName + "[" + protoType + "] ";
			}
		});
	}
	
	/**
	 * 开启或者重置副本
	 * @param client
	 * @param chaterID
	 * @param type 
	 */
	public void openLevel(Client client, final String chaterID, RequestType type){
		if(client == null){
			return ;
		}
		
		GroupCopyAdminOpenCopyReqMsg.Builder openMsg = GroupCopyAdminOpenCopyReqMsg.newBuilder();
		openMsg.setMapId(chaterID);
		GroupCopyAdminComReqMsg.Builder msg = GroupCopyAdminComReqMsg.newBuilder();
		msg.setOpenReqMsg(openMsg);
		msg.setReqType(type);
		client.getMsgHandler().sendMsg(cmdAdm, msg.build().toByteString(), new PrintMsgReciver(cmdAdm, functionName, "请求重置或开启帮派副本") {
			
			@Override
			public boolean execute(Client client, Response response) {

				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyAdminComRspMsg resp = GroupCopyAdminComRspMsg.parseFrom(bs);
					if(resp.getIsSuccess()){
						RobotLog.info("开启或重置副本["+chaterID+"]成功");
						return true;
					}else{
						RobotLog.info("开启或重置副本["+chaterID+"]失败：" + resp.getTipMsg());
						return true;
					}
					
				} catch (Exception e) {
					RobotLog.fail("开启或重置帮派副本["+chaterID+"]出现异常:" + e);
				}
				
				return false;
			}
		});
	}
	
	/**
	 * 尝试进入帮派副本关卡战斗
	 * @param client
	 * @param levelID
	 */
	public void try2EnterBattle(Client client, final String levelID){
		GroupCopyBattleComReqMsg.Builder msg = GroupCopyBattleComReqMsg.newBuilder();
		msg.setReqType(com.rwproto.GroupCopyBattleProto.RequestType.ENTER_APPLY);
		msg.setLevel(levelID);
		client.getMsgHandler().sendMsg(cmdBattle, msg.build().toByteString(), new PrintMsgReciver(cmdBattle, functionName, "请求进入关卡布阵状态") {
			
			@Override
			public boolean execute(Client client, Response response) {
			
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyBattleComRspMsg resp = GroupCopyBattleComRspMsg.parseFrom(bs);
					if(resp.getIsSuccess()){
						RobotLog.info("请求进入帮派副本["+levelID+"]关卡布阵，角色可以进入");
						return true;
					}else{
						CopyBattleRoleStruct battleRole = resp.getBattleRole();
						if(battleRole != null){
							RobotLog.info("请求进入帮派副本关卡["+levelID+"]布阵，不可进入，关卡被占用,请求角色["+battleRole.getRoleName()+"],id["+battleRole.getRoleID()
									+"],关卡内角色id["+client.getUserId()+"]");
							return true;
						}
						
						if(resp.getTipMsg() != null){
							RobotLog.info("请求进入帮派副本["+levelID+"]布阵，不可进入" + resp.getTipMsg());
							return true;
						}else{
							RobotLog.info("请求进入帮派副本["+levelID+"]布阵，不可进入");
							return true;
						}
						
					}
					
				} catch (Exception e) {
					RobotLog.fail("请求进入帮派副本["+levelID+"]布阵状态，出现异常"+ e);
				}
				return false;
			}
		});
	}
	
	/**
	 * 请求开始战斗
	 * @param client
	 * @param levelID
	 */
	public void clientBeginFight(Client client, final String levelID){

		GroupCopyBattleComReqMsg.Builder msg = GroupCopyBattleComReqMsg.newBuilder();
		msg.setReqType(com.rwproto.GroupCopyBattleProto.RequestType.FIGHT_BEGIN);
		msg.setLevel(levelID);
		client.getMsgHandler().sendMsg(cmdBattle, msg.build().toByteString(), new PrintMsgReciver(cmdBattle, functionName, "请求进入关卡战斗") {
			
			@Override
			public boolean execute(final Client client, Response response) {
			
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyBattleComRspMsg resp = GroupCopyBattleComRspMsg.parseFrom(bs);
					if(resp.getIsSuccess()){
						
						final GroupCopyMonsterData monster = resp.getMData();
						if(monster != null){
							RobotLog.info("请求进入帮派副本["+levelID+"]战斗，角色进入成功");
							//异步执行结束通知
							GroupCopyMgr.getInstance().submitGroupCopyEndFihgtTask(new Runnable() {
								
								@Override
								public void run() {
									clientEndBattle(client, levelID, monster);
									
								}
							});
							return true;
						}else{
							RobotLog.info("请求进入帮派副本["+levelID+"]战斗，不可进入，关卡正在战斗中");
							return true;
						}
					}else{
						if(resp.getTipMsg() != null){
							RobotLog.info("请求进入帮派副本["+levelID+"]战斗，不可进入" + resp.getTipMsg());
							return true;
						}else{
							RobotLog.info("请求进入帮派副本["+levelID+"]战斗，不可进入");
							return true;
						}
						
					}
					
				} catch (Exception e) {
					RobotLog.fail("请求进入帮派副本["+levelID+"]战斗状态，出现异常"+ e);
				}
				return false;
			}
		});
		
	}
	
	
	
	/**
	 * 角色结束战斗
	 * @param client
	 * @param levelID
	 * @param monster
	 */
	private void clientEndBattle(Client client, final String levelID, GroupCopyMonsterData mData){
		try {
			
			//先将所有怪物扣100hp
			List<GroupCopyMonsterSynStruct> m_Data  = new ArrayList<GroupCopyMonsterSynStruct>();
			List<String> dataList = mData.getMonsterDataList();
			for (String jsonStr : dataList) {
				GroupCopyMonsterSynStruct m = DataSynHelper.ToObject(GroupCopyMonsterSynStruct.class, jsonStr);
				m_Data.add(m);
			}
			
			for (GroupCopyMonsterSynStruct md : m_Data) {
				int curHP = md.getCurHP();
				curHP -= 100;
				curHP = curHP > 0 ? curHP : 0;
				md.setCurHP(curHP);
			}
			
			HeroList.Builder heros = HeroList.newBuilder();
			TableUserHero tableUserHero = client.getUserHerosDataHolder().getTableUserHero();
			List<String> heroIds = tableUserHero.getHeroIds();
			int num = heroIds.size() > 5 ? 5 : heroIds.size();
			for (int i = 0; i < num; i++) {
				if(i > heroIds.size()){
					break;
				}
				heros.addId(heroIds.get(i));
			}
			
			GroupCopyMonsterData.Builder gmd = GroupCopyMonsterData.newBuilder();
			for (GroupCopyMonsterSynStruct m : m_Data) {
				String clientData = JsonUtil.writeValue(m);
				gmd.addMonsterData(clientData);
			}
			
			
			
			GroupCopyBattleComReqMsg.Builder msg = GroupCopyBattleComReqMsg.newBuilder();
			msg.setReqType(com.rwproto.GroupCopyBattleProto.RequestType.FIGHT_END);
			msg.setLevel(levelID);
			msg.setMData(gmd);
			msg.setHeros(heros);
			client.getMsgHandler().sendMsg(cmdBattle, msg.build().toByteString(), new PrintMsgReciver(cmdBattle, functionName, "副本关卡战斗结束") {
				
				@Override
				public Command getCmd() {
					return cmdBattle;
				}
				
				@Override
				public boolean execute(Client client, Response response) {
					ByteString bs = response.getSerializedContent();
					try {
						String tips = "";
						GroupCopyBattleComRspMsg rspMsg = GroupCopyBattleComRspMsg.parseFrom(bs);
						if(rspMsg.getTipMsg() != null){
							tips = rspMsg.getTipMsg();
						}
						if(rspMsg.getIsSuccess()){
							RobotLog.info("帮派副本["+levelID+"]战斗结束成功！" + tips);
							return true;
						}else{
							RobotLog.info("帮派副本["+levelID+"]战斗结束出现失败！" + tips);
							return true;
						}
					} catch (Exception e) {
						RobotLog.fail("帮派副本["+levelID+"]战斗结束出现异常！" + e);
					}
					
					return false;
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			RobotLog.fail("帮派副本["+levelID+"]战斗结束出现异常！" + e);
		}
		
	}
	
	/**
	 * 请求发送掉落及申请列表
	 * @param client
	 * @param chaterID
	 */
	public void clientApplyDropData(Client client, String chaterID){
		GroupCopyCmdReqMsg.Builder reqMsg = GroupCopyCmdReqMsg.newBuilder();
		reqMsg.setId(chaterID);
		reqMsg.setReqType(GroupCopyReqType.GET_DROP_APPLY_INFO);
		client.getMsgHandler().sendMsg(cmdCom, reqMsg.build().toByteString(), new PrintMsgReciver(cmdCom, functionName, "请求帮派副本章节战胜品数据") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyCmdRspMsg rspMsg = GroupCopyCmdRspMsg.parseFrom(bs);
					if(rspMsg.getIsSuccess()){
						RobotLog.info("请求发送掉落及申请列表成功");
						return true;
					}else{
						RobotLog.info("请求掉落及申请列表存在异常");
						return false;
					}
				} catch (Exception e) {
					RobotLog.info("请求掉落及申请列表存在异常" + e);
				}
				return false;
			}
		});
	}
	
	
	/**
	 * 请求全服排行榜
	 * @param client
	 * @param levelID
	 */
	public void clientApplyServerRank(Client client, String levelID){
		GroupCopyCmdReqMsg.Builder reqMsg = GroupCopyCmdReqMsg.newBuilder();
		reqMsg.setReqType(GroupCopyReqType.APPLY_SERVER_RANK);
		reqMsg.setVersion(GroupCopyDataVersion.getDataVersion());
		reqMsg.setId(levelID);
		client.getMsgHandler().sendMsg(cmdCom, reqMsg.build().toByteString(), new PrintMsgReciver(cmdCom, functionName, "请求全服排行榜") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyCmdRspMsg rspMsg = GroupCopyCmdRspMsg.parseFrom(bs);
					if(rspMsg.getIsSuccess()){
						RobotLog.info("请求全服排行榜成功");
						return true;
					}else{
						RobotLog.info("请求全服排行榜异常");
						return false;
					}
				} catch (Exception e) {
					RobotLog.info("请求全服排行榜存在异常" + e);
				}
				return false;
			}
		});
	}
	
	
	/**
	 * 请求帮派前10排行榜
	 * @param client
	 */
	public void clientApplyGroupDamageRank(Client client){
		GroupCopyCmdReqMsg.Builder reqMsg = GroupCopyCmdReqMsg.newBuilder();
		reqMsg.setReqType(GroupCopyReqType.GET_GROUP_HURT_RANK);
		client.getMsgHandler().sendMsg(cmdCom, reqMsg.build().toByteString(), new PrintMsgReciver(cmdCom, functionName, "请求帮派前10排行榜") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyCmdRspMsg rspMsg = GroupCopyCmdRspMsg.parseFrom(bs);
					String tips = "";
					if(rspMsg.getTipMsg() != null){
						tips = rspMsg.getTipMsg();
					}
					if(rspMsg.getIsSuccess()){
						RobotLog.info("请求帮派前10排行榜成功" + tips);
						return true;
					}else{
						RobotLog.info("请求帮派前10排行榜异常" + tips);
						return false;
					}
				} catch (Exception e) {
					RobotLog.info("请求帮派前10排行榜存在异常" + e);
				}
				return false;
			}
		});
	}
	
	/**
	 * 获取奖励分配记录
	 * @param client
	 */
	public void clientApplyDistRewardLog(Client client){
		GroupCopyCmdReqMsg.Builder reqMsg = GroupCopyCmdReqMsg.newBuilder();
		reqMsg.setReqType(GroupCopyReqType.GET_DIST_REWARD_LOG);
		client.getMsgHandler().sendMsg(cmdCom, reqMsg.build().toByteString(), new PrintMsgReciver(cmdCom, functionName, "获取奖励分配记录") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyCmdRspMsg rspMsg = GroupCopyCmdRspMsg.parseFrom(bs);
					String tips = "";
					if(rspMsg.getTipMsg() != null){
						tips = rspMsg.getTipMsg();
					}
					if(rspMsg.getIsSuccess()){
						RobotLog.info("获取奖励分配记录成功" + tips);
						return true;
					}else{
						RobotLog.info("获取奖励分配记录异常" + tips);
						return false;
					}
				} catch (Exception e) {
					RobotLog.info("获取奖励分配记录存在异常" + e);
				}
				return false;
			}
		});
		
	}

	
	/**
	 * 获取所有章节的帮派副本奖励申请数据
	 */
	public void getAllRewardApplyInfo(Client client){
		GroupCopyAdminComReqMsg.Builder reqMsg = GroupCopyAdminComReqMsg.newBuilder();
		reqMsg.setReqType(RequestType.GET_APPLY_REWARD_INFO);
		client.getMsgHandler().sendMsg(cmdAdm, reqMsg.build().toByteString(), new PrintMsgReciver(cmdAdm, functionName, "获取所有章节的帮派副本奖励申请数据") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyAdminComRspMsg rspMsg = GroupCopyAdminComRspMsg.parseFrom(bs);
					String tips = "";
					if(rspMsg.getTipMsg() != null){
						tips = rspMsg.getTipMsg();
					}
					if(rspMsg.getIsSuccess()){
						RobotLog.info("获取所有章节的帮派副本奖励申请数据成功" + tips);
						return true;
					}else{
						RobotLog.info("获取所有章节的帮派副本奖励申请数据异常" + tips);
						return false;
					}
				} catch (Exception e) {
					RobotLog.info("获取所有章节的帮派副本奖励申请数据存在异常" + e);
				}
				return false;
			}
		});
	}
	
	
	/**
	 * 赞助一次
	 * @param client
	 * @param level
	 * @param cout TODO 次数
	 */
	public void donate(Client client, final String level, final int cout){
		GroupCopyCmdReqMsg.Builder reqMsg = GroupCopyCmdReqMsg.newBuilder();
		reqMsg.setReqType(GroupCopyReqType.BUFF_DONATE);
		GroupCopyDonateData.Builder value = GroupCopyDonateData.newBuilder();
		value.setDonateTime(cout);
		value.setLevel(level);
		reqMsg.setDonateData(value);
		
		client.getMsgHandler().sendMsg(cmdCom, reqMsg.build().toByteString(), new PrintMsgReciver(cmdCom, functionName, "赞助["+cout+"]次") {
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString bs = response.getSerializedContent();
				try {
					GroupCopyCmdRspMsg rspMsg = GroupCopyCmdRspMsg.parseFrom(bs);
					String tips = "";
					if(rspMsg.getTipMsg() != null){
						tips = rspMsg.getTipMsg();
					}
					if(rspMsg.getIsSuccess()){
						RobotLog.info("帮派副本关卡["+level+"]赞助["+cout+"]次成功" + tips);
						return true;
					}else{
						RobotLog.info("帮派副本关卡["+level+"]赞助["+cout+"]次异常" + tips);
						return false;
					}
				} catch (Exception e) {
					RobotLog.info("帮派副本关卡["+level+"]赞助["+cout+"]次存在异常" + e);
				}
				return false;
			}
		});
	}
	
	
	
}
