package com.rw.handler.groupsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rwproto.GroupSecretMatchProto.AttackEnemyStartReqMsg;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonReqMsg;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonRspMsg;
import com.rwproto.GroupSecretMatchProto.MatchRequestType;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupSecretMatchHandler {
	private static GroupSecretMatchHandler handler = new GroupSecretMatchHandler();
	private static final Command command = Command.MSG_GROUP_SECRET_MATCH;
	private static final String functionName = "帮派秘境匹配";
	
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
	 * 攻击匹配秘境
	 * @param client
	 */
	public boolean attackEnemyGroupSecret(Client client){
		GroupSecretMatchCommonReqMsg.Builder req = GroupSecretMatchCommonReqMsg.newBuilder();
		req.setReqType(MatchRequestType.ATTACK_ENEMY_START);
		AttackEnemyStartReqMsg.Builder msg = AttackEnemyStartReqMsg.newBuilder();
		msg.setIndex(GroupSecretIndex.MAIN);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		List<String> battleHeroList = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			for (Iterator iterator = heroIds.iterator(); iterator.hasNext();) {
				String heroId = (String) iterator.next();
				battleHeroList.add(heroId);
				iterator.remove();
			}
		}
		msg.addAllHeroList(battleHeroList);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET_MATCH, req.build().toByteString(), new GroupSecretMatchReceier(command, functionName, "匹配秘境"));
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
				} else {
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
