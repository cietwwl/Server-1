package com.rw.service.group;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupLogMgr;
import com.bm.group.GroupMemberMgr;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.group.GroupMemberJoinCallback;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.Email.EmailUtils;
import com.rw.service.group.helper.GroupCmdHelper;
import com.rw.service.group.helper.GroupHelper;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rw.service.group.helper.GroupRankHelper;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.group.GroupUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupFunctionCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupLog;
import com.rwbase.dao.group.pojo.db.GroupMemberData;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GroupCommonProto.GroupFunction;
import com.rwproto.GroupCommonProto.GroupLogType;
import com.rwproto.GroupCommonProto.GroupPost;
import com.rwproto.GroupCommonProto.GroupState;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupMemberMgrProto.GroupCancelNominatePostReqMsg;
import com.rwproto.GroupMemberMgrProto.GroupEmailForAllReqMsg;
import com.rwproto.GroupMemberMgrProto.GroupMemberMgrCommonRspMsg;
import com.rwproto.GroupMemberMgrProto.GroupMemberReceiveReqMsg;
import com.rwproto.GroupMemberMgrProto.GroupMemberReceiveRspMsg;
import com.rwproto.GroupMemberMgrProto.GroupNominatePostReqMsg;
import com.rwproto.GroupMemberMgrProto.KickMemberReqMsg;

/*
 * @author HC
 * @date 2016年2月18日 下午3:50:57
 * @Description 帮派成员管理的协议处理
 */
public class GroupMemberManagerHandler {
	private static GroupMemberManagerHandler handler = new GroupMemberManagerHandler();

	public static GroupMemberManagerHandler getHandler() {
		return handler;
	}

	protected GroupMemberManagerHandler() {
	}

	/**
	 * 获取帮派申请列表
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getApplyMemberListHandler(Player player) {
		String playerId = player.getUserId();

		GroupMemberMgrCommonRspMsg.Builder commonRsp = GroupMemberMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GET_APPLY_MEMBER_LIST_TYPE);

		// 检查个人的帮派数据
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("获取帮派申请列表", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("获取帮派申请列表", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		// 检查个人权限
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("获取帮派申请列表", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		int post = memberData.getPost();
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.MEMBER_RECEIVE_VALUE, post, groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, tip);
		}

		// 同步帮派的数据
		group.getGroupMemberMgr().synMemberData(player, true, -1);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 成员接受处理【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString groupMemberReceiveHandler(Player player, GroupMemberReceiveReqMsg req) {
		String playerId = player.getUserId();

		GroupMemberMgrCommonRspMsg.Builder commonRsp = GroupMemberMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GROUP_MEMBER_RECEIVE_TYPE);

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("帮派成员接收", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 检查帮派存在不存在
		final UserGroupAttributeDataMgr mgr = player.getUserGroupAttributeDataMgr();
		UserGroupAttributeDataIF baseData = mgr.getUserGroupAttributeData();
		final String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		// 检查帮派是否存在
		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("帮派成员接收", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		final GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("帮派成员接收", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 检查帮派等级中对应官职个数的信息
		GroupLevelCfg levelTemplate = GroupLevelCfgDAO.getDAO().getLevelCfg(groupData.getGroupLevel());
		if (levelTemplate == null) {
			GameLog.error("帮派成员接收", playerId, String.format("帮派Id为[%s]的帮派等级为[%s]没有找到对应的GroupLevelTemplate配置表", groupId, groupData.getGroupLevel()));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 检查个人的权限
		final GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("帮派成员接收", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "权限不足");
		}

		// 检查个人权限能不能接受成员
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.MEMBER_RECEIVE_VALUE, memberData.getPost(), groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, tip);
		}

		// 需要回应包含GroupMemberReceiveRsp消息
		GroupMemberReceiveRspMsg.Builder rsp = GroupMemberReceiveRspMsg.newBuilder();

		boolean needRsp = true;
		boolean isReceive = req.getIsReceive();
		final GroupLogMgr groupLogMgr = group.getGroupLogMgr();
		if (isReceive) {// 接受
			int maxMemberLimit = levelTemplate.getMaxMemberLimit();
			final long nowTime = System.currentTimeMillis();

			PlayerTask receiveTask = new PlayerTask() {

				@Override
				public void run(Player player) {
					// 更新下个人的数据
					String groupName = groupData.getGroupName();
					player.getUserGroupAttributeDataMgr().updateDataWhenHasGroup(player, groupId, groupName);
					// 发送邮件
					GroupHelper.sendJoinGroupMail(player.getUserId(), groupName);
				}
			};

			if (req.hasApplyMemberId()) {// 接收单个成员
				final String applyMemberId = req.getApplyMemberId();
				// 检查帮派是否满员
				int groupMemberSize = memberMgr.getGroupMemberSize();
				if (groupMemberSize >= maxMemberLimit) {
					return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "该帮派已经满员");
				}

				// 没有成员信息
				GroupMemberDataIF applyMemberData = memberMgr.getMemberData(applyMemberId, true);
				if (applyMemberData == null) {
					return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "没有申请人信息");
				}

				final PlayerIF p = PlayerMgr.getInstance().getReadOnlyPlayer(applyMemberId);
				// 检查是否被其他帮派接受
				UserGroupAttributeDataIF applyBaseData = p.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
				String hasGroupId = applyBaseData.getGroupId();
				if (StringUtils.isEmpty(hasGroupId)) {// 没有帮派
					GroupMemberJoinCallback joinCallback = new GroupMemberJoinCallback() {

						@Override
						public void updateGroupMemberData(GroupMemberData groupMemberData) {
							groupMemberData.setVipLevel((byte) p.getVip());
							groupMemberData.setName(p.getUserName());
							groupMemberData.setHeadId(p.getHeadImage());
							groupMemberData.setLevel((short) p.getLevel());
							groupMemberData.setTemplateId(p.getTemplateId());
							groupMemberData.setHeadbox(p.getHeadFrame());
						}
					};

					memberMgr.updateMemberDataWhenByReceive(applyMemberId, nowTime, joinCallback);// 接受成员

					// 记录一个日志
					GroupLog log = new GroupLog();
					log.setLogType(GroupLogType.NEW_JOIN_GROUP_VALUE);
					log.setTime(nowTime);
					log.setName(applyMemberData.getName());
					groupLogMgr.addLog(player, log);

					// 检查是否被其他帮派接受
					GameWorldFactory.getGameWorld().asyncExecute(applyMemberId, receiveTask);
				} else {// 已经是其他帮派成员，就删除记录
					memberMgr.removeApplyMemberFromDB(applyMemberId, null);
					commonRsp.setTipMsg("该玩家已加入其他帮派");
				}

				rsp.addRemoveMemberId(applyMemberId);
			} else {// 全部接收
				int hasGroupCount = 0;
				List<? extends GroupMemberDataIF> applyMemberList = memberMgr.getApplyMemberSortList(null);
				int size = applyMemberList.size();
				for (int i = 0; i < size; i++) {
					// 检查帮派是否满员
					int groupMemberSize = memberMgr.getGroupMemberSize();
					if (groupMemberSize >= maxMemberLimit) {
						break;
					}

					// 没有成员信息
					GroupMemberDataIF applyMemberData = applyMemberList.get(i);
					String userId = applyMemberData.getUserId();

					final PlayerIF p = PlayerMgr.getInstance().getReadOnlyPlayer(userId);

					// 检查是否被其他帮派接受
					UserGroupAttributeDataIF applyBaseData = p.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
					String hasGroupId = applyBaseData.getGroupId();
					if (StringUtils.isEmpty(hasGroupId)) {// 没有帮派
						GroupMemberJoinCallback joinCallback = new GroupMemberJoinCallback() {

							@Override
							public void updateGroupMemberData(GroupMemberData groupMemberData) {
								groupMemberData.setVipLevel((byte) p.getVip());
								groupMemberData.setName(p.getUserName());
								groupMemberData.setHeadId(p.getHeadImage());
								groupMemberData.setLevel((short) p.getLevel());
								groupMemberData.setTemplateId(p.getTemplateId());
								groupMemberData.setHeadbox(p.getHeadFrame());
							}
						};

						memberMgr.updateMemberDataWhenByReceive(userId, nowTime, joinCallback);// 接受成员
						// 记录一个日志
						GroupLog log = new GroupLog();
						log.setLogType(GroupLogType.NEW_JOIN_GROUP_VALUE);
						log.setTime(nowTime);
						log.setName(applyMemberData.getName());
						groupLogMgr.addLog(player, log);

						// 检查是否被其他帮派接受
						GameWorldFactory.getGameWorld().asyncExecute(userId, receiveTask);
					} else {// 已经是其他帮派成员，就删除记录
						memberMgr.removeApplyMemberFromDB(userId, null);
						hasGroupCount++;
					}
					rsp.addRemoveMemberId(userId);
				}

				int removeCount = rsp.getRemoveMemberIdCount();
				if (removeCount >= size) {// 全部移除了
					needRsp = false;
				}

				if (hasGroupCount > 0) {
					commonRsp.setTipMsg(hasGroupCount + "名玩家已加入其他帮派，无法接收");
				}
			}
		} else {// 拒绝
			PlayerTask pt = new PlayerTask() {

				@Override
				public void run(Player player) {
					player.getUserGroupAttributeDataMgr().updateDataWhenRefuseByGroup(player, groupId);
				}
			};

			if (req.hasApplyMemberId()) {// 拒绝单个成员
				String applyMemberId = req.getApplyMemberId();
				memberMgr.removeApplyMemberFromDB(applyMemberId, pt);
				rsp.addRemoveMemberId(applyMemberId);
			} else {// 全部拒绝
				memberMgr.removeAllApplyMemberFromDB(pt);
				needRsp = false;
			}
		}

		// 通知排行榜
		GroupRankHelper.addOrUpdateGroup2MemberNumRank(group);
		// 更新下基础排行榜中记录的数据
		GroupRankHelper.updateBaseRankExtension(groupData, memberMgr);
		// 帮派信息发生改变
		GroupCompetitionMgr.getInstance().notifyGroupInfoChange(group);

		commonRsp.setIsSuccess(true);
		if (needRsp) {
			commonRsp.setGroupMemberReceiveRsp(rsp);
		}
		return commonRsp.build().toByteString();
	}

	/**
	 * 帮派任命【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString groupNominatePostHandler(Player player, GroupNominatePostReqMsg req) {
		String playerId = player.getUserId();

		GroupMemberMgrCommonRspMsg.Builder commonRsp = GroupMemberMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.NOMINATE_POST_TYPE);

		// 检查帮派存在不存在
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("帮派官员任命", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("帮派官员任命", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		// 检查自己是不是在帮派中有数据
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(playerId, false);
		if (selfMemberData == null) {
			GameLog.error("帮派官员任命", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还不是帮派成员");
		}

		// 检查要任命的成员在不在帮派
		String memberId = req.getMemberId();
		GroupMemberDataIF memberData = memberMgr.getMemberData(memberId, false);
		if (memberData == null) {
			GameLog.error("帮派官员任命", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派中无此成员");
		}

		// 检查修改人的权限
		int selfPost = selfMemberData.getPost();

		// 检查帮派等级中对应官职个数的信息
		int groupLevel = groupData.getGroupLevel();
		GroupLevelCfg levelTemplate = GroupLevelCfgDAO.getDAO().getLevelCfg(groupLevel);
		if (levelTemplate == null) {
			GameLog.error("帮派官员任命", playerId, String.format("帮派Id为[%s]的帮派等级为[%s]没有找到对应的GroupLevelTemplate配置表", groupId, groupLevel));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "数据异常");
		}

		GroupPost post = req.getPost();// 要任命的职位

		// 检查职位
		int postNum = memberMgr.getPostMemberSize(post.getNumber());// 对应职位的成员数量

		int maxPostNum = 0;
		int functionType = 0;
		if (post == GroupPost.ASSISTANT_LEADER) {// 任命副帮主
			functionType = GroupFunction.NOMINATE_ASSISTANT_LEADER_VALUE;
			maxPostNum = levelTemplate.getAssistantGroupLeaderLimit();
		} else if (post == GroupPost.OFFICEHOLDER) {// 任命官员
			functionType = GroupFunction.NOMINATE_OFFICEHOLDER_VALUE;
			maxPostNum = levelTemplate.getOfficialLimit();
		} else {// 其他官职不能调整
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "其他官职不能调整");
		}

		if (postNum >= maxPostNum) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "该官职暂无空缺");
		}

		// 检查修改官职的权限
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(functionType, selfPost, groupLevel);
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, tip);
		}

		if (playerId.equals(memberId)) {// 转让给自己
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您不能给自己任命");
		}

		// 检查任命成员的职位是不是跟目前一样
		int nominateMemberPost = memberData.getPost();
		if (nominateMemberPost == post.getNumber()) {// 与当前的职位是一样的
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "成员已经是该官职");
		}

		// 检查个人的权限是不是高于要任命的
		if (selfPost >= nominateMemberPost) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您无权限任命跟您同官职或比您官职高的");
		}

		// TODO HC 当职位提升了之后，就记录下一个帮派日志
		if (nominateMemberPost > post.getNumber()) {// 当职位小于当前就证明是升职
			GroupLog log = new GroupLog();
			log.setLogType(GroupLogType.CHANGE_POST_VALUE);
			log.setTime(System.currentTimeMillis());
			log.setName(memberData.getName());
			log.setPost(post.getNumber());
			group.getGroupLogMgr().addLog(player, log);
		}

		memberMgr.updateMemberPost(memberId, post.getNumber());// 更新成员的官职

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 取消任命【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString cancelNominateHandler(Player player, GroupCancelNominatePostReqMsg req) {
		String playerId = player.getUserId();

		GroupMemberMgrCommonRspMsg.Builder commonRsp = GroupMemberMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.CANCEL_NOMINATE_TYPE);

		// 检查帮派存在不存在
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("帮派取消任命", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("帮派取消任命", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		// 检查自己是不是在帮派中有数据
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(playerId, false);
		if (selfMemberData == null) {
			GameLog.error("帮派取消任命", playerId, String.format("帮派Id[%s]没有找到角色对应的MemberData的记录", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还不是帮派成员");
		}

		// 检查要任命的成员在不在帮派
		String memberId = req.getMemberId();
		GroupMemberDataIF memberData = memberMgr.getMemberData(memberId, false);
		if (memberData == null) {
			GameLog.error("帮派取消任命", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, memberId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派中无此成员");
		}

		// 检查修改人的权限
		int selfPost = selfMemberData.getPost();

		// 检查个人权限能不能踢出成员
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.CANCEL_NOMINATE_VALUE, selfPost, groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, tip);
		}

		if (playerId.equals(memberId) && playerId.equals(memberMgr.getGroupLeader().getUserId())) {// 帮主不能取消自己
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您不能对自己取消任命");
		}

		int post = memberData.getPost();// 被取消任命的成员的当前职位
		if (!playerId.equals(memberId) && selfPost >= post) {// 自己的职位低于要操作的角色
			GameLog.error("帮派取消任命", playerId, String.format("自己的职位[%s]，取消任命Id[%s]的职位[%s]，", selfPost, memberId, post));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "不能对同职位或职位高的成员取消任命");
		}

		if (post != GroupPost.MEMBER_VALUE) {// 已经是成员
			// 记录日志
			GroupLog log = new GroupLog();
			log.setLogType(GroupLogType.LOG_CANCEL_NOMINATE_VALUE);
			log.setTime(System.currentTimeMillis());
			log.setOpName(selfMemberData.getName());
			log.setName(memberData.getName());
			log.setPost(memberData.getPost());
			group.getGroupLogMgr().addLog(player, log);

			memberMgr.updateMemberPost(memberId, GroupPost.MEMBER_VALUE);
		}

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 发送帮派邮件【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString groupEmailForAllHandler(Player player, GroupEmailForAllReqMsg req) {
		String playerId = player.getUserId();

		GroupMemberMgrCommonRspMsg.Builder commonRsp = GroupMemberMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GROUP_EMAIL_FOR_ALL_TYPE);

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("帮派全员邮件", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 判断帮派是否存在
		UserGroupAttributeDataMgr userGroupAttributeDataMgr = player.getUserGroupAttributeDataMgr();
		UserGroupAttributeDataIF baseData = userGroupAttributeDataMgr.getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		// 判断发送邮件的冷却时间
		long now = System.currentTimeMillis();
		long sendEmailTime = baseData.getSendEmailTime();
		if (sendEmailTime > 0 && now - sendEmailTime < TimeUnit.SECONDS.toMillis(gbct.getGroupEmailCoolingTime())) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "发送全员邮件冷却中");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("帮派全员邮件", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("帮派全员邮件", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 获取自己的成员信息
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("帮派全员邮件", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		int post = memberData.getPost();
		// 检查个人权限能不能发送全员邮件
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.GROUP_EMAIL_ALL_VALUE, post, groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, tip);
		}

		int emailTitleLength = gbct.getGroupEmailTitleCharLimit() * 2;// 邮件标题长度
		int emailContentLength = gbct.getGroupEmailContentCharLimit() * 2;// 邮件内容长度
		// 检查邮件标题
		String emailTitle = req.getEmailTitle();
		String emailContent = req.getEmailContent();
		if (StringUtils.isEmpty(emailTitle) || StringUtils.isEmpty(emailContent)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "邮件标题或内容不能为空");
		}

		int titleLen = GroupUtils.getChineseNumLimitLength(emailTitle);
		if (titleLen == -1) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "邮件标题只允许中文，英文和数字");
		} else if (titleLen > emailTitleLength) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "邮件标题过长");
		} else if (CharFilterFactory.getCharFilter().checkWords(emailTitle, true, true, true, true)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "邮件标题包含非法字符");
		}

		// 检查邮件内容
		int contentLength = GroupUtils.getContentLength(emailContent);
		if (contentLength > emailContentLength) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "邮件内容过长");
		}

		String newContent = CharFilterFactory.getCharFilter().replaceDiryWords(emailContent, "**", true, false);

		// 邮件内容
		final EmailData emailData = new EmailData();
		emailData.setTitle(emailTitle);
		emailData.setContent(newContent);
		emailData.setDeleteType(EEmailDeleteType.DELAY_TIME);
		emailData.setDelayTime((int) TimeUnit.DAYS.toSeconds(7));// 整个帮派邮件只保留7天
		emailData.setSender(player.getUserName());

		// 成员任务
		final PlayerTask playerTask = new PlayerTask() {

			@Override
			public void run(Player player) {
				EmailUtils.sendEmail(player.getUserId(), emailData);
			}
		};

		// 帮派成员列表
		List<? extends GroupMemberDataIF> memberList = memberMgr.getMemberSortList(null);
		for (int i = 0, size = memberList.size(); i < size; i++) {
			GroupMemberDataIF groupMemberData = memberList.get(i);
			String userId = groupMemberData.getUserId();// 成员对应的角色Id
			// if (playerId.equals(userId)) {// 跟发件人是同一个人
			// continue;
			// }

			GameWorldFactory.getGameWorld().asyncExecute(userId, playerTask);
		}

		// 设置个人发送邮件的时间
		userGroupAttributeDataMgr.updateSendEmailTime(player, now);

		commonRsp.setIsSuccess(true);
		commonRsp.setTipMsg("发送成功");
		return commonRsp.build().toByteString();
	}

	/**
	 * 踢出帮派成员【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString kickMemberHandler(Player player, KickMemberReqMsg req) {
		String playerId = player.getUserId();

		GroupMemberMgrCommonRspMsg.Builder commonRsp = GroupMemberMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.KICK_MEMBER_TYPE);

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("踢出帮派", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 判断帮派是否存在
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("踢出帮派", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("踢出帮派", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}
		
		if(GroupCompetitionMgr.getInstance().isGroupInCompetition(groupId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "你的帮派在本届帮派争霸中有赛事，不能踢除成员！");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 获取自己的成员信息
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("踢出帮派", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		// 踢出帮派的成员信息
		String kickMemberId = req.getMemberId();
		GroupMemberDataIF kickMemberData = memberMgr.getMemberData(kickMemberId, false);
		if (kickMemberData == null) {
			GameLog.error("踢出帮派", playerId, String.format("帮派Id[%s]要转让给的成员Id[%s]没有找到数据", groupId, kickMemberId));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "帮派中无此成员");
		}

		// 两个人的权限类比
		int post = memberData.getPost();// 自己的职位

		// 检查个人权限能不能踢出成员
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.KICK_OF_GROUP_VALUE, post, groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, tip);
		}

		int kickPost = kickMemberData.getPost();// 踢出成员的职位
		if (post >= kickPost) {// 权限低于踢出成员权限
			GameLog.error("踢出帮派", playerId, String.format("自己的职位是[%s]踢出成员的职位是[%s]，职位<=踢出成员", post, kickPost));
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "权限不足");
		}

		if (playerId.equals(kickMemberId)) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, "不能踢出自己");
		}

		// 检查踢出成员的时间
		long now = System.currentTimeMillis();
		long receiveTime = kickMemberData.getReceiveTime();
		if (now - receiveTime < gbct.getKickMemberLimitTime()) {
			return GroupCmdHelper.groupMemberMgrFillFailMsg(commonRsp, String.format("该成员进入帮派时间少于%s，无法踢出", gbct.getKickMemberLimitTimeTip()));
		}

		// 权限也有了，就踢出成员
		memberMgr.kickMember(kickMemberId);

		// 设置踢出成员的个人数据
		GameWorldFactory.getGameWorld().asyncExecute(kickMemberId, GroupMemberHelper.quitGroupTask);
		// 发送邮件
		GroupHelper.sendQuitGroupMail(kickMemberId, groupData.getGroupName());

		// 记录一个帮派日志
		GroupLog log = new GroupLog();
		log.setLogType(GroupLogType.LOG_KICK_GROUP_VALUE);
		log.setTime(now);
		log.setOpName(memberData.getName());
		log.setName(kickMemberData.getName());
		group.getGroupLogMgr().addLog(player, log);

		// 更新下排行榜数据
		GroupRankHelper.addOrUpdateGroup2MemberNumRank(group);
		// 更新下基础排行榜中记录的数据
		GroupRankHelper.updateBaseRankExtension(groupData, memberMgr);

		// 清理一下帮派成员申请奖励品的数据
		group.getGroupCopyMgr().nofityCreateRoleLeaveTask(kickMemberId);

		commonRsp.setIsSuccess(true);
		GroupCompetitionMgr.getInstance().notifyGroupInfoChange(group);
		GroupCompetitionMgr.getInstance().notifyGroupMemberLeave(group, playerId);
		return commonRsp.build().toByteString();
	}
}