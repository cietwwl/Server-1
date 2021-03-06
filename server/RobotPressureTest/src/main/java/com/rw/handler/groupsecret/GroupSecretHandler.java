package com.rw.handler.groupsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rw.handler.chat.attach.AttachItemFactory;
import com.rw.handler.group.holder.GroupBaseDataHolder;
import com.rw.handler.group.holder.GroupNormalMemberHolder;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.ChatServiceProtos.ChatAttachItem;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.eAttachItemType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.GroupSecretProto.CreateGroupSecretReqMsg;
import com.rwproto.GroupSecretProto.CreateGroupSecretReqMsg.Builder;
import com.rwproto.GroupSecretProto.GetGroupSecretRewardReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;
import com.rwproto.GroupSecretProto.InviteGroupMemberDefendReqMsg;
import com.rwproto.GroupSecretProto.RequestType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GroupSecretHandler implements RandomMethodIF{
	
	private static GroupSecretHandler handler = new GroupSecretHandler();
	private static final Command command = Command.MSG_GROUP_SECRET;
	private static final String functionName = "帮派秘境";

	public static GroupSecretHandler getInstance() {
		return handler;
	}

	/**
	 * 探索
	 * 
	 * @param client
	 */
	public boolean createGroupSecret(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return true;
		}

		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.CREATE_GROUP_SECRET);
		CreateGroupSecretReqMsg.Builder msg = CreateGroupSecretReqMsg.newBuilder();
		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		List<SecretBaseInfoSynData> defendSecretIdList = groupSecretBaseInfoSynDataHolder.getDefanceList();
		msg.setSecretCfgId(3);
		setMainPos(defendSecretIdList, msg);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();

		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		GroupSecretTeamDataHolder groupSecretTeamDataHolder = client.getGroupSecretTeamDataHolder();
		GroupSecretTeamData data = groupSecretTeamDataHolder.getData();
		List<String> defendHeroList = data != null ? data.getDefendHeroList() : new ArrayList<String>(0);

		List<String> heroPosList = new ArrayList<String>();
		int fightHeroNum = 1;// 一个坑上雇佣兵数量
		boolean isOk = false;
		// for (int i = 0; i < fightHeroNum; i++) {
		int i = 0;
		String userId = client.getUserId();
		for (Iterator<String> iterator = heroIds.iterator(); iterator.hasNext();) {
			String heroId = iterator.next();
			if (heroId.equals(userId)) {
				continue;
			}

			if (defendHeroList.contains(heroId)) {
				continue;
			}

			BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
			pos.setHeroId(heroId);
			pos.setPos(++i);
			msg.addTeamHeroId(pos);
			defendHeroList.add(heroId);
			isOk = true;

			// 人数已经足够了
			if (i >= fightHeroNum) {
				break;
			}
		}
		// }

		if (!isOk) {
			int sercetNum = 0;
			if (defendSecretIdList != null) {
				sercetNum = defendSecretIdList.size();
			}
			RobotLog.fail("创建秘境只有一个英雄，没有多余的雇佣兵；当前所有英雄加雇佣兵个数是 =" + defendHeroList.size() + "     当前秘境数 = " + sercetNum);
		}

		if (!heroPosList.contains(userId)) {
			BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
			pos.setHeroId(userId);
			pos.setPos(0);
			msg.addTeamHeroId(pos);
			heroPosList.add(userId);
		}
		req.setCreateReqMsg(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "创建秘境"));
	}

	private void setMainPos(List<SecretBaseInfoSynData> defendSecretIdList, Builder msg) {
		if (defendSecretIdList == null) {
			return;
		}
		if (defendSecretIdList.size() == 0) {
			msg.setMainPos(0);
			return;
		}
		for (int i = 0; i < 5; i++) {
			boolean isThis = true;
			for (SecretBaseInfoSynData data : defendSecretIdList) {
				if (data.getMainPos() == i) {
					isThis = false;
					continue;
				}
			}
			if (isThis) {
				msg.setMainPos(i);
				return;
			}
		}
	}

	public boolean inviteMemberDefend(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return true;
		}

		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.INVITE_MEMBER_DEFEND);
		GroupNormalMemberHolder normalMemberHolder = client.getNormalMemberHolder();
		Random r = new Random();
		String randomMemberId = normalMemberHolder.getRandomMemberId(r, false);
		InviteGroupMemberDefendReqMsg.Builder msg = InviteGroupMemberDefendReqMsg.newBuilder();
		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		String defendSecretId = groupSecretBaseInfoSynDataHolder.getDefendSecretId();
		if (defendSecretId == null) {
			RobotLog.fail("你当前没有秘境，邀请秘境失败");
			return false;
		}
		if (randomMemberId == null) {
			RobotLog.fail("当前帮派没有成员，邀请秘境失败");
			return false;
		}
		msg.setId(defendSecretId);
		msg.addMemberId(randomMemberId);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "发送邀请"));
	}

	public boolean acceptMemberDefend(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return true;
		}

		List<ChatMessageData> list = client.getChatData().getMsgList(eChatType.CHAT_TREASURE);
		if (list == null || list.isEmpty()) {
			RobotLog.fail("当前没有秘境邀请，接受失败");
			return false;
		}

		ChatMessageData chatMessageData = list.get(new Random().nextInt(list.size()));
		List<ChatAttachItem> attachItemList = chatMessageData.getAttachItemList();
		if (attachItemList == null || attachItemList.isEmpty()) {
			RobotLog.fail("当前没有秘境邀请，接受失败");
			return false;
		}

		ChatAttachItem chatAttachItem = attachItemList.get(0);
		return AttachItemFactory.getInstance().attachHandler(eAttachItemType.valueOf(chatAttachItem.getType()), client, chatAttachItem.getId(), chatAttachItem.getExtraInfo());
	}

	public static class GroupSecretReceier extends PrintMsgReciver {

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
					if (tips.indexOf("当前只能创建") != -1) {
						RobotLog.fail(parseFunctionDesc() + "失败:" + tips);
						return true;
					}
					RobotLog.fail(parseFunctionDesc() + "失败:" + tips + "  client.accountId =" + client.getAccountId());
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
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return;
		}

		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		List<SecretBaseInfoSynData> defendSecretIdList = groupSecretBaseInfoSynDataHolder.getDefanceList();
		if (defendSecretIdList == null) {
			return;
		}

		for (int i = 0; i < defendSecretIdList.size(); i++) {
			if (!defendSecretIdList.get(i).isFinish()) {
				continue;
			}
			if (defendSecretIdList.get(i).getMainPos() == 0) {
				continue;// 掠夺的数据，不在此处发送
			}
			GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
			req.setReqType(RequestType.GET_GROUP_SECRET_REWARD);
			GetGroupSecretRewardReqMsg.Builder msg = GetGroupSecretRewardReqMsg.newBuilder();
			msg.setId(defendSecretIdList.get(i).getId());
			req.setGetRewardReqMsg(msg);
			client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "普通奖励"));
		}
	}

	public void openMainView(Client client) {
		GroupBaseDataHolder groupBaseDataHolder = client.getGroupBaseDataHolder();
		String groupId = groupBaseDataHolder.getGroupId();
		if (groupId == null || groupId.isEmpty()) {
			RobotLog.fail("机器人没有对应的帮派信息");
			return;
		}

		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.OPEN_MAIN_VIEW);
		client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "打开界面"));
	}

	@Override
	public boolean executeMethod(Client client) {
		if(StringUtils.isBlank(client.getGroupBaseDataHolder().getGroupId())){
			RobotLog.fail(functionName + "--玩家[" + client.getAccountId() + "]没有帮派...");
			return true;
		}
		return createGroupSecret(client);
	}
}
