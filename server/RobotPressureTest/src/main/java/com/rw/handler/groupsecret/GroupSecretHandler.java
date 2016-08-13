package com.rw.handler.groupsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.chat.attach.AttachItemFactory;
import com.rw.handler.group.holder.GroupNormalMemberHolder;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.ChatServiceProtos.ChatAttachItem;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.eAttachItemType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.GroupSecretProto.CreateGroupSecretReqMsg;
import com.rwproto.GroupSecretProto.GetGroupSecretRewardReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;
import com.rwproto.GroupSecretProto.InviteGroupMemberDefendReqMsg;
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
	 * 
	 * @param client
	 */
	public boolean createGroupSecret(Client client) {
		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.CREATE_GROUP_SECRET);
		CreateGroupSecretReqMsg.Builder msg = CreateGroupSecretReqMsg.newBuilder();
		msg.setSecretCfgId(3);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();

		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		GroupSecretTeamDataHolder groupSecretTeamDataHolder = client.getGroupSecretTeamDataHolder();
		GroupSecretTeamData data = groupSecretTeamDataHolder.getData();
		List<String> defendHeroList = data.getDefendHeroList();
		if (defendHeroList == null) {
			defendHeroList = new ArrayList<String>();
		}
		List<String> heroPosList = new ArrayList<String>();
		int mainRoleIndex = -1;
		for (int i = 0; i < 1; i++) {
			for (Iterator iterator = heroIds.iterator(); iterator.hasNext();) {
				String heroId = (String) iterator.next();
				if (heroId.equals(client.getUserId())) {
					if (heroPosList.contains(heroId)) {
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

	public boolean inviteMemberDefend(Client client) {
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

		GroupSecretBaseInfoSynDataHolder groupSecretBaseInfoSynDataHolder = client.getGroupSecretBaseInfoSynDataHolder();
		List<SecretBaseInfoSynData> defendSecretIdList = groupSecretBaseInfoSynDataHolder.getDefanceList();
		if (defendSecretIdList == null) {
			return;
		}
		for (int i = 0; i < defendSecretIdList.size(); i++) {
			if (!defendSecretIdList.get(i).isFinish()) {
				continue;
			}
			GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
			req.setReqType(RequestType.GET_GROUP_SECRET_REWARD);
			GetGroupSecretRewardReqMsg.Builder msg = GetGroupSecretRewardReqMsg.newBuilder();
			msg.setId(defendSecretIdList.get(i).getId());
			req.setGetRewardReqMsg(msg);
			client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "领取奖励"));

		}
	}

	public void openMainView(Client client) {
		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.OPEN_MAIN_VIEW);
		client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(command, functionName, "打开界面"));

	}
}
