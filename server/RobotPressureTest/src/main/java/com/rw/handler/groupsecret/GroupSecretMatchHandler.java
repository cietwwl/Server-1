package com.rw.handler.groupsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.actionHelper.ActionEnum;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rw.handler.battle.army.ArmyHero;
import com.rw.handler.battle.army.ArmyInfo;
import com.rw.handler.group.holder.GroupBaseDataHolder;
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

public class GroupSecretMatchHandler implements RandomMethodIF{
	
	private static ConcurrentHashMap<String, Integer> funcStageMap = new ConcurrentHashMap<String, Integer>();
	
	private static GroupSecretMatchHandler handler = new GroupSecretMatchHandler();
	private static final Command command = Command.MSG_GROUP_SECRET_MATCH;
	private static final String functionName = "帮派秘境匹配";

	public static GroupSecretMatchHandler getInstance() {
		return handler;
	}

	/**
	 * 匹配秘境
	 * 
	 * @param client
	 */
	public boolean searchGroupSecret(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return true;
		}

		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.SEARCHING_ENEMY);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceier(command, functionName, "匹配秘境"));

	}

	/**
	 * 发起攻击
	 * 
	 * @param client
	 */
	public boolean attackEnemyGroupSecret(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return true;
		}

		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.ATTACK_ENEMY_START);
		AttackEnemyStartReqMsg.Builder msg = AttackEnemyStartReqMsg.newBuilder();
		msg.setIndex(GroupSecretIndex.MAIN);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		if (userHerosDataHolder.getTableUserHero() == null) {
			RobotLog.fail("groupattack.start.获取的自己英雄数据失败");
			return false;
		}
		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());

		int mainRoleIndex = 0;
		int fightHeroNum = 1;// 雇佣兵数量
		boolean isOk = false;
		for (Iterator<String> iterator = heroIds.iterator(); iterator.hasNext();) {
			String heroId = (String) iterator.next();
			if (heroId.equals(client.getUserId())) {
				continue;
			}

			BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
			pos.setHeroId(heroId);
			pos.setPos(++mainRoleIndex);
			msg.addHeroList(pos);
			isOk = true;
			if (mainRoleIndex >= fightHeroNum) {
				break;
			}
		}

		BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
		pos.setHeroId(client.getUserId());
		pos.setPos(0);
		msg.addHeroList(pos);
		if (!isOk) {
			RobotLog.fail("进攻秘境只有一个英雄，没有多余的雇佣兵；当前所有英雄加雇佣兵个数是 =" + heroIds.size());
		}
		req.setAttackStartReq(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceierTmp(command, functionName, "发起进攻"));
	}

	/**
	 * 完成攻击
	 * 
	 * @param client
	 */
	public boolean attackEndEnemyGroupSecret(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return true;
		}

		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.ATTACK_ENEMY_END);
		AttackEnemyEndReqMsg.Builder msg = AttackEnemyEndReqMsg.newBuilder();
		msg.setIndex(GroupSecretIndex.MAIN);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		if (userHerosDataHolder.getTableUserHero() == null) {
			RobotLog.fail("groupattack.end.获取的自己英雄数据失败");
			return false;
		}

		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		int mainRoleIndex = 0;
		for (Iterator<String> iterator = heroIds.iterator(); iterator.hasNext();) {
			String heroId = (String) iterator.next();
			HeroLeftInfo.Builder info = HeroLeftInfo.newBuilder();
			info.setId(heroId);
			info.setLeftLife(100);
			info.setLeftEnergy(100);
			msg.addMyLeft(info);
			if (++mainRoleIndex > 1) {
				break;
			}
		}

		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		ArmyInfo armyInfo = groupSecretBaseInfoSynDataHolder.getArmyInfo();
		if (armyInfo != null) {
			List<ArmyHero> heroList = armyInfo.getHeroList();
			if (heroList == null || heroList.size() == 0) {
				return true;
			}
			int length = heroList.size();
			for (int i = 0; i < length; i++) {
				String heroId = (String) heroList.get(i).getRoleBaseInfo().getId();
				HeroLeftInfo.Builder info = HeroLeftInfo.newBuilder();
				info.setId(heroId);
				info.setLeftLife(0);
				info.setLeftEnergy(0);
				msg.addEnemyLeft(info);
			}
		}

		req.setAttackEndReq(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceier(command, functionName, "完成进攻"));
	}

	/**
	 * 秘境奖励
	 * 
	 * @param client
	 */
	public boolean getGroupSecretReward(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return true;
		}

		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		List<SecretBaseInfoSynData> defendSecretIdList = groupSecretBaseInfoSynDataHolder.getDefanceList();
		if (defendSecretIdList == null) {
			return true;
		}
		boolean result = true;
		for (int i = 0; i < defendSecretIdList.size(); i++) {
			if (defendSecretIdList.get(i).getMainPos() != 0) {
				continue;// 掠夺的数据，不在此处发送
			}
			if (!defendSecretIdList.get(i).isFinish()) {
				continue;
			}
			GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
			req.setReqType(MatchRequestType.GET_REWARD);
			result = client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceier(command, functionName, "进攻奖励"));
			if (!result) {
				break;
			}
		}
		return result;
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
					if (resp.getTipMsg().indexOf("战前数据发送完毕") != -1) {
						AttackEnemyStartRspMsg startInfo = resp.getAttackStartRsp();
						String armyInfoStr = startInfo.getArmyInfo();
						GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
						ArmyInfo armyInfo = new ArmyInfo();
						armyInfo = ArmyInfo.fromJson(armyInfoStr);
						groupSecretBaseInfoSynDataHolder.setArmyInfo(armyInfo);
					}
					return true;
				} else {
					if (resp.getTipMsg().indexOf("当前您没有可以领取") != -1) {
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if (resp.getTipMsg().indexOf("没有可以挑战的秘境") != -1) {
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if (resp.getTipMsg().indexOf("找不到可掠夺") != -1) {
						RobotLog.fail(resp.getTipMsg());
						return false;
					}
					if (resp.getTipMsg().indexOf("对方已被其他玩家挑战，搜索秘境费用返回") != -1) {
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if (resp.getTipMsg().indexOf("当前您没有可以挑战的秘境") != -1) {
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

	/** 进攻秘境需要用反馈的rsp.string来初始化数据，但原有方法是多个功能的综合，不能保证其他功能不发进攻秘境一样的空的string回来 */
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
					GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
					ArmyInfo armyInfo = new ArmyInfo();
					armyInfo = ArmyInfo.fromJson(armyInfoStr);
					groupSecretBaseInfoSynDataHolder.setArmyInfo(armyInfo);
					return true;
				} else {

					if (resp.getTipMsg().indexOf("没有可以挑战的秘境") != -1) {
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if (resp.getTipMsg().indexOf("对方已被其他玩家挑战，搜索秘境费用返回") != -1) {
						RobotLog.fail(resp.getTipMsg());
						return true;
					}
					if (resp.getTipMsg().indexOf("当前您没有可以挑战的秘境") != -1) {
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

	@Override
	public boolean executeMethod(Client client) {
		if(StringUtils.isBlank(client.getGroupBaseDataHolder().getGroupId())){
			RobotLog.fail(functionName + "--玩家[" + client.getAccountId() + "]没有帮派...");
			return true;
		}
		Integer stage = funcStageMap.get(client.getAccountId());
		if(null == stage){
			stage = new Integer(0);
			funcStageMap.put(client.getAccountId(), stage);
		}
		client.getGroupBaseDataHolder().getGroupId();
		switch (stage) {
		case 0:
			funcStageMap.put(client.getAccountId(), 1);
			client.getRateHelper().addActionToQueue(ActionEnum.GroupSecretMatch);
			return searchGroupSecret(client);
		case 1:
			funcStageMap.put(client.getAccountId(), 2);
			client.getRateHelper().addActionToQueue(ActionEnum.GroupSecretMatch);
			return attackEnemyGroupSecret(client);
		case 2:
			funcStageMap.put(client.getAccountId(), 3);
			client.getRateHelper().addActionToQueue(ActionEnum.GroupSecretMatch);
			return attackEndEnemyGroupSecret(client);
		case 3:
			funcStageMap.put(client.getAccountId(), 0);
			return getGroupSecretReward(client);
		default:
			return true;
		}
	}
}
