package com.rw.handler.groupsecret;

import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.battle.army.ArmyInfo;
import com.rw.handler.gameLogin.GameLoginHandler;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.GroupSecretMatchProto.AttackEnemyEndReqMsg;
import com.rwproto.GroupSecretMatchProto.AttackEnemyStartReqMsg;
import com.rwproto.GroupSecretMatchProto.AttackEnemyStartRspMsg;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonReqMsg;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonRspMsg;
import com.rwproto.GroupSecretMatchProto.HeroLeftInfo;
import com.rwproto.GroupSecretMatchProto.MatchRequestType;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupSecretMatchHandler {
	private static GroupSecretMatchHandler handler = new GroupSecretMatchHandler();
	private static final Command command = Command.MSG_GROUP_SECRET_MATCH;
	private static final String functionName = "帮派秘境匹配";
	private static ArmyInfo armyInfo = new ArmyInfo();
	
	
	public static GroupSecretMatchHandler getInstance(){
		return handler;
	}
	
	/**
	 * 匹配秘境
	 * @param client
	 */
	public boolean searchGroupSecret(Client client){
		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.SEARCHING_ENEMY);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceier(command, functionName, "匹配秘境"));
		
	}
	
	/**
	 * 发起攻击
	 * @param client
	 */
	public boolean attackEnemyGroupSecret(Client client){
		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.ATTACK_ENEMY_START);
		AttackEnemyStartReqMsg.Builder msg = AttackEnemyStartReqMsg.newBuilder();
		msg.setIndex(GroupSecretIndex.MAIN);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		int mainRoleIndex = 0;
		for (Iterator iterator = heroIds.iterator(); iterator.hasNext();) {
			String heroId = (String) iterator.next();
			BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
			pos.setHeroId(heroId);
			pos.setPos(mainRoleIndex);				
			msg.addHeroList(pos);
			mainRoleIndex ++;
			if(mainRoleIndex > 1){
				break;
			}				
		}
		req.setAttackStartReq(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceierTmp(command, functionName, "发起进攻"));
	}
	
	/**
	 * 完成攻击
	 * @param client
	 */
	public boolean attackEndEnemyGroupSecret(Client client){
		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.ATTACK_ENEMY_END);
		AttackEnemyEndReqMsg.Builder msg = AttackEnemyEndReqMsg.newBuilder();
		msg.setIndex(GroupSecretIndex.MAIN);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		int mainRoleIndex = 0;
		for (Iterator iterator = heroIds.iterator(); iterator.hasNext();) {
			String heroId = (String) iterator.next();
			HeroLeftInfo.Builder info = HeroLeftInfo.newBuilder();
			info.setId(heroId);
			info.setLeftLife(100);
			info.setLeftEnergy(100);
			msg.addMyLeft(info);
			mainRoleIndex ++;
			if(mainRoleIndex > 1){
				break;
			}				
		}
		int length = armyInfo.getHeroList().size();
		for(int i =0;i < length;i++){
			String heroId = (String) armyInfo.getHeroList().get(i).getRoleBaseInfo().getId();
			HeroLeftInfo.Builder info = HeroLeftInfo.newBuilder();
			info.setId(heroId);
			info.setLeftLife(0);
			info.setLeftEnergy(0);
			msg.addEnemyLeft(info);				
		}

		req.setAttackEndReq(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceier(command, functionName, "完成进攻"));
	}
	
	
	
	
	/**
	 * 秘境奖励
	 * @param client
	 */
	public boolean getGroupSecretReward(Client client){
		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.GET_REWARD);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceier(command, functionName, "秘境奖励"));
	}
	
	
	
	
	private class GroupSecretMatchReceier extends PrintMsgReciver {

		public GroupSecretMatchReceier(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean execute(Client client, Response response) {
			// TODO Auto-generated method stub
			ByteString bs = response.getSerializedContent();
			try {
				GroupSecretMatchCommonRspMsg resp = GroupSecretMatchCommonRspMsg.parseFrom(bs);
				if (resp.getIsSuccess()) {
					RobotLog.info(parseFunctionDesc() + "成功");
					if(resp.getTipMsg().indexOf("战前数据发送完毕") != -1){
						AttackEnemyStartRspMsg startInfo = resp.getAttackStartRsp();
						String armyInfoStr = startInfo.getArmyInfo();
						armyInfo = new ArmyInfo();
						armyInfo =ArmyInfo.fromJson(armyInfoStr);
					}
					return true;
				} else {
					if(resp.getTipMsg().indexOf("当前您没有可以领取" )!= -1){
						RobotLog.fail(resp.getTipMsg());
						return true;
					}	
					if(resp.getTipMsg().indexOf("没有可以挑战的秘境")!= -1){
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if(resp.getTipMsg().indexOf("对方已被其他玩家挑战，搜索秘境费用返回") != -1){
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if(resp.getTipMsg().indexOf("当前您没有可以挑战的秘境") != -1){
						RobotLog.fail("发起进攻收到了地方的数据信息，但结束进攻时对方已经被别人打了");
						return true;
					}					
					RobotLog.fail(resp.getTipMsg());
					throw new Exception(resp.getTipMsg());
				}
			} catch (Exception ex) {
				RobotLog.fail(parseFunctionDesc() + "失败", ex);
			}
			return false;
		}

		private String parseFunctionDesc() {
			return functionName + "[" + protoType + "] ";
		}
	}
	
	/**进攻秘境需要用反馈的rsp.string来初始化数据，但原有方法是多个功能的综合，不能保证其他功能不发进攻秘境一样的空的string回来*/
	private class GroupSecretMatchReceierTmp extends PrintMsgReciver {
		public GroupSecretMatchReceierTmp(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
			// TODO Auto-generated constructor stub
		}
		
		

		@Override
		public boolean execute(Client client, Response response) {
			// TODO Auto-generated method stub
			ByteString bs = response.getSerializedContent();
			try {
				GroupSecretMatchCommonRspMsg resp = GroupSecretMatchCommonRspMsg.parseFrom(bs);
				if (resp.getIsSuccess()) {
					RobotLog.info(parseFunctionDesc() + "成功");
					AttackEnemyStartRspMsg startInfo = resp.getAttackStartRsp();
					String armyInfoStr = startInfo.getArmyInfo();
					armyInfo = new ArmyInfo();
					armyInfo =ArmyInfo.fromJson(armyInfoStr);
					return true;
				} else {
							
					if(resp.getTipMsg().indexOf("没有可以挑战的秘境")!= -1){
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if(resp.getTipMsg().indexOf("对方已被其他玩家挑战，搜索秘境费用返回") != -1){
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if(resp.getTipMsg().indexOf("当前您没有可以挑战的秘境") != -1){
						RobotLog.fail("发起进攻收到了地方的数据信息，但结束进攻时对方已经被别人打了");
						return true;
					}					
					RobotLog.fail(resp.getTipMsg());
					throw new Exception(resp.getTipMsg());
				}
			} catch (Exception ex) {
				RobotLog.fail(parseFunctionDesc() + "失败", ex);
			}
			return false;
		}

		private String parseFunctionDesc() {
			return functionName + "[" + protoType + "] ";
		}
	}
	
}
