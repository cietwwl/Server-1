package com.rw.handler.groupsecret;

import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.group.holder.GroupNormalMemberHolder;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.GroupSecretProto.CreateGroupSecretReqMsg;
import com.rwproto.GroupSecretProto.GetGroupSecretRewardReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.GroupSecretProto.InviteGroupMemberDefendReqMsg;
import com.rwproto.GroupSecretProto.JoinSecretDefendReqMsg;
import com.rwproto.GroupSecretProto.RequestType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupSecretHandler {
	private static GroupSecretHandler handler = new GroupSecretHandler();
	private static final Command command = Command.MSG_GROUP_SECRET;
	private static final String functionName = "帮派秘境";

	public static GroupSecretHandler getInstance() {
		return handler;
	}

	/**
	 * 探索
	 * @param client
	 */
	public boolean createGroupSecret(Client client) {
		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.CREATE_GROUP_SECRET);
		CreateGroupSecretReqMsg.Builder msg = CreateGroupSecretReqMsg.newBuilder();
		msg.setSecretCfgId(1);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		
		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		GroupSecretTeamDataHolder groupSecretTeamDataHolder = client.getGroupSecretTeamDataHolder();
		GroupSecretTeamData data = groupSecretTeamDataHolder.getData();
		List<String> defendHeroList = data.getDefendHeroList();
		if(defendHeroList == null){
			defendHeroList = new ArrayList<String>();
		}
		List<String> heroPosList = new ArrayList<String>();
		int mainRoleIndex = -1;
		for (int i = 0; i < 4; i++) {
			for (Iterator iterator = heroIds.iterator(); iterator.hasNext();) {				
				String heroId = (String) iterator.next();
				if(heroId.equals(client.getUserId())){
					if(heroPosList.contains(heroId)){
						continue;
					}
					BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
					pos.setHeroId(heroId);
					pos.setPos(0);
					msg.addTeamHeroId(pos);
					mainRoleIndex = i;
					heroPosList.add(heroId);
					continue;
				}
				
				if (defendHeroList == null || !defendHeroList.contains(heroId)) {
					BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
					pos.setHeroId(heroId);
					pos.setPos(mainRoleIndex == -1 ? i++ : i);
					msg.addTeamHeroId(pos);
					defendHeroList.add(heroId);
					break;
				}
			}
		}
		req.setCreateReqMsg(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "创建秘境"));
	}
	
	public boolean inviteMemberDefend(Client client){
		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.INVITE_MEMBER_DEFEND);
		GroupNormalMemberHolder normalMemberHolder = client.getNormalMemberHolder();
		Random r = new Random();
		String randomMemberId = normalMemberHolder.getRandomMemberId(r, false);
		InviteGroupMemberDefendReqMsg.Builder msg =InviteGroupMemberDefendReqMsg.newBuilder();
		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		String defendSecretId = groupSecretBaseInfoSynDataHolder.getDefendSecretId();
		if(defendSecretId == null){
			RobotLog.fail("你当前没有秘境，邀请秘境失败");
			return false;
		}
		if(randomMemberId == null){
			RobotLog.fail("当前帮派没有成员，邀请秘境失败");
			return false;
		}
		msg.setId(defendSecretId);
		msg.addMemberId(randomMemberId);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "发送邀请"));
	}
	
	public boolean acceptMemberDefend(Client client){
		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.JOIN_SECRET_DEFEND);
		GroupSecretInviteDataHolder groupSecretInviteDataHolder = client.getGroupSecretInviteDataHolder();
		List<ChatMessageData> list = groupSecretInviteDataHolder.getList();
		if(list == null || list.size()<=0){
			RobotLog.fail("当前没有秘境邀请，接受失败");
			return false;
		}
		ChatMessageData chatMessageData = list.get(0);
		String treasureId = chatMessageData.getTreasureId();
		JoinSecretDefendReqMsg.Builder msg = JoinSecretDefendReqMsg.newBuilder();
		msg.setId(treasureId);
		msg.setIndex(GroupSecretIndex.LEFT);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		List<String> battleHeroList = new ArrayList<String>();
		int mainRoleIndex = 0;
		for (Iterator iterator = heroIds.iterator(); iterator.hasNext();) {
			String heroId = (String) iterator.next();
			BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
			pos.setHeroId(heroId);
			pos.setPos(mainRoleIndex);				
			msg.addHeroId(pos)	;
			mainRoleIndex ++;
			if(mainRoleIndex > 4){
				break;
			}
		}
		
		req.setJoinReqMsg(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "接受邀请"));
	}

	private class GroupSecretReceier extends PrintMsgReciver {

		public GroupSecretReceier(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean execute(Client client, Response response) {
			// TODO Auto-generated method stub
			ByteString bs = response.getSerializedContent();
			try {
				GroupSecretCommonRspMsg resp = GroupSecretCommonRspMsg.parseFrom(bs);
				if (resp.getIsSuccess()) {
					RobotLog.info(parseFunctionDesc() + "成功");
					return true;
				} else {
					String tips = resp.getTipMsg();
					if(tips.indexOf("当前只能创建")!= -1){
						RobotLog.fail(parseFunctionDesc() + "失败:"+tips);
						return true;
					}
					RobotLog.fail(parseFunctionDesc() + "失败:"+tips);
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

	public void getGroupSecretReward(Client client) {
	
		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		List<SecretBaseInfoSynData> defendSecretIdList = groupSecretBaseInfoSynDataHolder.getDefanceList();
		for(int i = 0;i < defendSecretIdList.size();i++){
			if(!defendSecretIdList.get(i).isFinish()){
				continue;
			}
			GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
			req.setReqType(RequestType.GET_GROUP_SECRET_REWARD);
			GetGroupSecretRewardReqMsg.Builder msg = GetGroupSecretRewardReqMsg.newBuilder();
			msg.setId(defendSecretIdList.get(i).getId());
			req.setGetRewardReqMsg(msg);			
		}		
	}

	public void openMainView(Client client) {
		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.OPEN_MAIN_VIEW);
		client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "打开界面"));
		
		
	}
}
