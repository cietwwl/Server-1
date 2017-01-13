package com.rw.service.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupBaseDataMgr;
import com.bm.group.GroupMemberMgr;
import com.bm.rank.RankType;
import com.bm.rank.group.GroupSimpleExtAttribute;
import com.bm.rank.group.base.GroupBaseRankExtAttribute;
import com.bm.rank.groupCompetition.groupRank.GroupFightingRefreshTask;
import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.group.helper.GroupCmdHelper;
import com.rw.service.group.helper.GroupHelper;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rw.service.group.helper.GroupRankHelper;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.group.GroupCheckDismissTask;
import com.rwbase.dao.group.GroupUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.GroupDonateCfg;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupDonateCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupFunctionCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupLog;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.item.SpecialItemCfgDAO;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.SpecialItemCfg;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GroupCommonProto;
import com.rwproto.GroupCommonProto.GroupFunction;
import com.rwproto.GroupCommonProto.GroupLogType;
import com.rwproto.GroupCommonProto.GroupPost;
import com.rwproto.GroupCommonProto.GroupRecommentType;
import com.rwproto.GroupCommonProto.GroupState;
import com.rwproto.GroupCommonProto.GroupValidateType;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupPersonalProto.ApplyJoinGroupReqMsg;
import com.rwproto.GroupPersonalProto.FindGroupReqMsg;
import com.rwproto.GroupPersonalProto.FindGroupRspMsg;
import com.rwproto.GroupPersonalProto.GetGroupInfoRspMsg;
import com.rwproto.GroupPersonalProto.GetGroupRankRspMsg;
import com.rwproto.GroupPersonalProto.GroupDonateReqMsg;
import com.rwproto.GroupPersonalProto.GroupDonateRspMsg;
import com.rwproto.GroupPersonalProto.GroupPersonalCommonRspMsg;
import com.rwproto.GroupPersonalProto.GroupRankEntryInfo;
import com.rwproto.GroupPersonalProto.GroupRecommentReqMsg;
import com.rwproto.GroupPersonalProto.GroupRecommentRspMsg;
import com.rwproto.GroupPersonalProto.GroupSimpleInfo;
import com.rwproto.GroupPersonalProto.OpenDonateViewRspMsg;
import com.rwproto.GroupPersonalProto.TransferGroupLeaderPostReqMsg;
import com.rwproto.PrivilegeProtos.GroupPrivilegeNames;

/*
 * @author HC
 * @date 2016年1月20日 上午11:47:18
 * @Description 帮派协议处理类
 */
public class GroupPersonalHandler {

	private static final String QUIT_GROUP_TIME_TIP_FOR_JOIN = "%s后才可再次加入帮派";
	private static final String JOIN_COOLING_TIME_FOR_DONATE = "%s后才可以捐献";

	private static GroupPersonalHandler handler = new GroupPersonalHandler();

	public static GroupPersonalHandler getHandler() {
		return handler;
	}

	protected GroupPersonalHandler() {
	}

	/**
	 * 获取帮派的信息【检查帮主离线信息】
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getGrouoInfo(Player player) {
		String playerId = player.getUserId();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GET_GROUP_INFO_TYPE);

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("获取帮派信息", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		// 检查当前角色有没有帮派
		UserGroupAttributeDataIF baseData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(playerId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前没有帮派");
		}

		// 检查帮派是否存在
		if (!GroupBM.groupIsExist(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派不存在");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("获取帮派信息", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("获取帮派信息", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(playerId, false);
		if (selfMemberData == null) {
			GameLog.error("获取帮派信息", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您不是帮派成员");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// // 推送帮派数据
		// group.synGroupDataAndMemberData(player);

		// 排行数据
		int rankIndex = -1;
		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);
		if (ranking != null) {
			rankIndex = ranking.getRanking(groupId);
		}

		// 设置回应消息
		GetGroupInfoRspMsg.Builder rsp = GetGroupInfoRspMsg.newBuilder();
		if (rankIndex != -1) {
			rsp.setRankIndex(rankIndex);
		}
		commonRsp.setGetGroupInfoRsp(rsp);
		commonRsp.setIsSuccess(true);
		commonRsp.setGetGroupInfoRsp(rsp);
		return commonRsp.build().toByteString();
	}

	/**
	 * 获取帮派的排行信息
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getGroupRankInfo(Player player) {
		String playerId = player.getUserId();

		String groupId = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(playerId).getGroupId();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GET_GROUP_RANK_INFO_TYPE);

		// 检查当前角色的等级有没有达到可以使用帮派功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.GROUP, player, outTip)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, outTip.value);
		}

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("帮派排行", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);
		if (ranking == null) {
			GameLog.error("帮派排行", playerId, "没有查找到类型为：" + RankType.GROUP_BASE_RANK.getType() + "的排行榜");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "排行榜暂无数据");
		}

		GetGroupRankRspMsg.Builder rsp = GetGroupRankRspMsg.newBuilder();

		int showRankMaxIndex = gbct.getUiShowRankGroupSize();
		EnumerateList enumeration = ranking.getEntriesEnumeration(1, showRankMaxIndex);
		while (enumeration.hasMoreElements()) {
			MomentRankingEntry entry = (MomentRankingEntry) enumeration.nextElement();
			GroupBaseRankExtAttribute attr = (GroupBaseRankExtAttribute) entry.getEntry().getExtendedAttribute();

			GroupRankEntryInfo.Builder rankEntry = GroupRankEntryInfo.newBuilder();
			rankEntry.setRankIndex(entry.getRanking());
			rankEntry.setGroupExp(attr.getGroupExp());
			rankEntry.setGroupLevel(attr.getGroupLevel());
			rankEntry.setGroupIcon(attr.getGroupIcon());
			rankEntry.setGroupName(attr.getGroupName());
			rankEntry.setGroupMemberNum(attr.getGroupMemberNum());
			rsp.addGroupRankEntryInfo(rankEntry);

			int rankIndex = GroupRankHelper.getGroupRankIndex(groupId);
			if (rankIndex != -1) {
				rsp.setRankIndex(rankIndex);
			}

		}

		commonRsp.setIsSuccess(true);
		commonRsp.setGetGroupRankRsp(rsp);
		return commonRsp.build().toByteString();
	}

	/**
	 * 查找帮派
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString findGroupHandler(Player player, FindGroupReqMsg req) {
		String playerId = player.getUserId();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIND_GROUP_TYPE);

		// 检查当前角色的等级有没有达到可以使用帮派功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.GROUP, player, outTip)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, outTip.value);
		}

		// 检查是否有帮派
		UserGroupAttributeDataIF baseData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(playerId);
		String selfGroupId = baseData.getGroupId();
		if (!StringUtils.isEmpty(selfGroupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前已经是帮派成员");
		}

		String groupId = req.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派Id不能为空");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("帮派查照", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("帮派查找", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派不存在");
		}

		// 消息回应
		FindGroupRspMsg.Builder rsp = FindGroupRspMsg.newBuilder();
		GroupSimpleInfo.Builder groupSimpleInfo = GroupSimpleInfo.newBuilder();
		groupSimpleInfo.setGroupId(groupId);
		groupSimpleInfo.setGroupName(groupData.getGroupName());
		groupSimpleInfo.setHeadIcon(groupData.getIconId());
		groupSimpleInfo.setGroupLevel(groupData.getGroupLevel());
		groupSimpleInfo.setGroupMemberNum(group.getGroupMemberMgr().getGroupMemberSize());
		groupSimpleInfo.setGroupDeclaration(groupData.getDeclaration());
		int rankIndex = GroupRankHelper.getGroupRankIndex(groupId);
		if (rankIndex != -1) {
			groupSimpleInfo.setRankIndex(rankIndex);
		}
		rsp.setGroupSimpleInfo(groupSimpleInfo);

		commonRsp.setIsSuccess(true);
		commonRsp.setFindGroupRsp(rsp);
		return commonRsp.build().toByteString();
	}

	/**
	 * 申请加入帮派
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString applyJoinGroupHandler(Player player, ApplyJoinGroupReqMsg req) {
		String playerId = player.getUserId();
		int playerLevel = player.getLevel();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.APPLY_JOIN_GROUP_TYPE);

		// 检查当前角色的等级有没有达到可以使用帮派功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.GROUP, player, outTip)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, outTip.value);
		}

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("申请加入帮派", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		// 检查帮派存在不存在
		UserGroupAttributeDataMgr userGroupAttributeDataMgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeDataIF baseData = userGroupAttributeDataMgr.getUserGroupAttributeData(playerId);
		String groupId = baseData.getGroupId();
		if (!StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前拥有帮派");
		}

		long nowTime = System.currentTimeMillis();
		// 检查冷却时间
		long quitGroupTime = baseData.getQuitGroupTime();
		long needCoolingMillisTime = TimeUnit.SECONDS.toMillis(gbct.getJoinGroupCoolingTime());
		if (quitGroupTime > 0 && (nowTime - quitGroupTime) < needCoolingMillisTime) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, String.format(QUIT_GROUP_TIME_TIP_FOR_JOIN, GroupUtils.coolingTimeTip(nowTime, quitGroupTime, needCoolingMillisTime)));
		}

		// 检查一下次数
		if (DateUtils.isResetTime(5, 0, 0, baseData.getLastResetApplyTime())) {
			userGroupAttributeDataMgr.updateAndCheckApplyTimes(playerId, nowTime);
		}

		if (baseData.getGroupApplySize() >= gbct.getPerDayMaxApplyGroupSize()) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "今天申请已满，请明日再来");
		}

		// 检查申请的帮派Id
		final String applyGroupId = req.getGroupId();
		if (StringUtils.isEmpty(applyGroupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "申请加入的帮派Id不能为空");
		}

		// 检查帮派是否存在
		Group group = GroupBM.get(applyGroupId);
		if (group == null) {
			GameLog.error("申请加入帮派", playerId, String.format("帮派Id[%s]没有找到Group数据", applyGroupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("申请加入帮派", playerId, String.format("帮派Id[%s]没有找到基础数据", applyGroupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派不存在");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派解散中，无法申请加入");
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		if (memberMgr.isAlreadyApply(playerId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您已经申请过该帮派");
		}

		// int applyMemberSize = memberMgr.getApplyMemberSize();
		// if (applyMemberSize >= gbct.getGroupApplyMemberSize()) {
		// return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "该帮派的申请队列已满，暂不能申请");
		// }

		// 检查帮派等级中对应官职个数的信息
		GroupLevelCfg levelTemplate = GroupLevelCfgDAO.getDAO().getLevelCfg(groupData.getGroupLevel());
		if (levelTemplate == null) {
			GameLog.error("申请加入帮派", playerId, String.format("帮派Id为[%s]的帮派等级为[%s]没有找到对应的GroupLevelTemplate配置表", applyGroupId, groupData.getGroupLevel()));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		// 检查帮派的验证类型
		int validateType = groupData.getValidateType();
		int applyLevel = groupData.getApplyLevel();// 允许加入的最低等级
		if (validateType == GroupValidateType.JOIN_REFUSED_VALUE) {// 拒绝所有人添加
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派拒绝所有人申请加入");
		}

		// 验证等级
		if (playerLevel < applyLevel) {// 小于申请等级
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, applyLevel + "及以上等级可加入");
		}

		// 判断人数是否满了
		int maxMemberLimit = levelTemplate.getMaxMemberLimit();

		int groupMemberSize = memberMgr.getGroupMemberSize();
		if (groupMemberSize >= maxMemberLimit) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "该帮派已经满员");
		}

		// 战力之和
		// List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros();
		List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros(player);
		int fighting = player.getMainRoleHero().getFighting();
		for (int i = 0, size = maxFightingHeros.size(); i < size; i++) {
			Hero hero = maxFightingHeros.get(i);
			fighting += hero.getFighting();
		}

		// 要验证后才能加入
		if (validateType == GroupValidateType.FIRST_VALIDATE_VALUE) {
			memberMgr.addMemberData(playerId, applyGroupId, player.getUserName(), player.getHeadImage(), player.getTemplateId(), player.getLevel(), player.getVip(), player.getCareer(), GroupPost.MEMBER_VALUE, fighting, nowTime, 0, true, player.getHeadFrame(), 0);
			// 帮派扩展属性增加一个申请的帮派Id
			userGroupAttributeDataMgr.updateApplyGroupData(player, applyGroupId);
			// 检查一下是否超出了上限
			memberMgr.checkAndRemoveOldestApplyMember(gbct.getGroupApplyMemberSize(), new PlayerTask() {

				@Override
				public void run(Player p) {
					UserGroupAttributeDataMgr mgr = UserGroupAttributeDataMgr.getMgr();
					UserGroupAttributeData userGroupBaseData = mgr.getUserGroupAttributeData(p.getUserId());
					if (userGroupBaseData == null) {
						return;
					}

					mgr.updateDataWhenRefuseByGroup(p, applyGroupId);
				}
			});
		} else {
			memberMgr.addMemberData(playerId, applyGroupId, player.getUserName(), player.getHeadImage(), player.getTemplateId(), player.getLevel(), player.getVip(), player.getCareer(), GroupPost.MEMBER_VALUE, fighting, nowTime, nowTime, false, player.getHeadFrame(), 0);

			// 记录一个日志
			GroupLog log = new GroupLog();
			log.setLogType(GroupLogType.NEW_JOIN_GROUP_VALUE);
			log.setTime(nowTime);
			log.setName(player.getUserName());
			group.getGroupLogMgr().addLog(player, log);

			// 加入之后，设置加入的信息
			String groupName = groupData.getGroupName();
			userGroupAttributeDataMgr.updateDataWhenHasGroup(player, applyGroupId, groupName);
			// 发送邮件
			GroupHelper.sendJoinGroupMail(playerId, groupName);

			// 更新下排行榜成员
			GroupRankHelper.addOrUpdateGroup2MemberNumRank(group);
			// 更新下基础排行榜中记录的数据
			GroupRankHelper.updateBaseRankExtension(groupData, memberMgr);
			// 帮派争霸
			GroupCompetitionMgr.getInstance().notifyGroupInfoChange(group);
			GameWorldFactory.getGameWorld().executeAccountTask(applyGroupId, new GroupFightingRefreshTask(applyGroupId));
		}

		commonRsp.setIsSuccess(true);
		commonRsp.setTipMsg("申请成功");
		return commonRsp.build().toByteString();
	}

	/**
	 * 打开捐献界面【检查帮主离线信息】
	 * 
	 * @param player
	 * @return
	 */
	public ByteString openDonateViewHandler(Player player) {
		String playerId = player.getUserId();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.OPEN_DONATE_VIEW_TYPE);

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("打开捐献界面", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		UserGroupAttributeDataMgr userGroupAttributeDataMgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeDataIF baseData = userGroupAttributeDataMgr.getUserGroupAttributeData(playerId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("打开捐献界面", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("打开捐献界面", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 获取自己的成员信息
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("打开捐献界面", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		// 检查次数
		long now = System.currentTimeMillis();
		// 检查是否过了加入帮派N秒的冷却
		long joinTime = baseData.getJoinTime();
		int canDonateCoolingTime = gbct.getCanDonateCoolingTime();
		if (canDonateCoolingTime > 0 && joinTime > 0) {
			long coolingTimeMillis = TimeUnit.SECONDS.toMillis(canDonateCoolingTime);
			String tip = GroupUtils.coolingTimeTip(now, joinTime, coolingTimeMillis);
			if (!StringUtils.isEmpty(tip)) {
				return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, String.format(JOIN_COOLING_TIME_FOR_DONATE, tip));
			}
		}

		if (DateUtils.isResetTime(5, 0, 0, baseData.getLastDonateTime())) {// 到了重置时间
			userGroupAttributeDataMgr.resetMemberDataDonateTimes(playerId, now);
			// memberMgr.resetMemberDataDonateTimes(playerId, now);
		}

		// 每天可以捐献的次数
		// int perDayDonateTimes = gbct.getPerDayDonateTimes();
		// by franky
		int perDayDonateTimes = player.getPrivilegeMgr().getIntPrivilege(GroupPrivilegeNames.donateCount);

		// 角色当天捐献的次数
		int donateTimes = baseData.getDonateTimes();

		OpenDonateViewRspMsg.Builder rsp = OpenDonateViewRspMsg.newBuilder();
		rsp.setLeftDonateTimes(perDayDonateTimes - donateTimes);
		rsp.setPrivateContribution(baseData.getContribution());
		rsp.setTotalDonateTimes(perDayDonateTimes);

		int vipLevel = player.getVip();// Vip等级

		List<GroupDonateCfg> allCfg = GroupDonateCfgDAO.getDAO().getAllCfg();
		for (int i = 0, size = allCfg.size(); i < size; i++) {
			GroupDonateCfg cfg = allCfg.get(i);
			if (vipLevel >= cfg.getVipLevelLimit()) {
				rsp.addOpenDonateId(Integer.valueOf(cfg.getDonateId()));
			}
		}

		commonRsp.setIsSuccess(true);
		commonRsp.setOpenDonateViewRsp(rsp);
		return commonRsp.build().toByteString();
	}

	/**
	 * 帮派捐献【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString groupDonateHandler(Player player, GroupDonateReqMsg req) {
		String userId = player.getUserId();
		String playerId = userId;

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GROUP_DONATE_TYPE);

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("帮派捐献", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		UserGroupAttributeDataMgr userGroupAttributeDataMgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeDataIF baseData = userGroupAttributeDataMgr.getUserGroupAttributeData(playerId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("帮派捐献", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("帮派捐献", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 获取自己的成员信息
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("帮派捐献", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		int donateId = req.getDonateId();
		GroupDonateCfg donateCfg = (GroupDonateCfg) GroupDonateCfgDAO.getDAO().getCfgById(String.valueOf(donateId));
		if (donateCfg == null) {
			GameLog.error("帮派捐献", playerId, String.format("捐献Id[%s]无法找到对应的GroupDonateCfg的配置表", donateId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		if (player.getVip() < donateCfg.getVipLevelLimit()) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, String.format("VIP%s及以上才能使用", donateCfg.getVipLevelLimit()));
		}

		// 检查次数
		long now = System.currentTimeMillis();
		// 检查是否过了加入帮派N秒的冷却
		long joinTime = baseData.getJoinTime();
		int canDonateCoolingTime = gbct.getCanDonateCoolingTime();
		if (canDonateCoolingTime > 0 && joinTime > 0) {
			long coolingTimeMillis = TimeUnit.SECONDS.toMillis(canDonateCoolingTime);
			String tip = GroupUtils.coolingTimeTip(now, joinTime, coolingTimeMillis);
			if (!StringUtils.isEmpty(tip)) {
				return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, String.format(JOIN_COOLING_TIME_FOR_DONATE, tip));
			}
		}

		if (DateUtils.isResetTime(5, 0, 0, baseData.getLastDonateTime())) {// 到了重置时间
			// memberMgr.resetMemberDataDonateTimes(playerId, now);
			userGroupAttributeDataMgr.resetMemberDataDonateTimes(playerId, now);
		}

		// by franky
		int perDayDonateTimes = player.getPrivilegeMgr().getIntPrivilege(GroupPrivilegeNames.donateCount);

		// 角色当天捐献的次数
		int donateTimes = baseData.getDonateTimes();
		int donateType = donateCfg.getDonateType();
		boolean isTokenDonate = donateType == GroupCommonProto.GroupDonateType.TOKEN_DONATE_VALUE;

		if (!isTokenDonate) {
			// 捐献次数已经用完了
			if (donateTimes >= perDayDonateTimes) {
				return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "捐献次数已用完");
			}
		}

		// 检查需要使用的金钱数量
		int donateItemType = donateCfg.getDonateItemType();
		int donateVal = donateCfg.getDonateVal();
		long hasCount = 0;
		String name = "";

		List<IUseItem> useItemList = null;
		Map<Integer, Integer> useMoney = null;

		int rewardToken = 0;
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		if (donateItemType < eSpecialItemId.eSpecial_End.getValue()) {
			eSpecialItemId def = eSpecialItemId.getDef(donateItemType);
			if (def == null) {
				GameLog.error("帮派捐献", playerId, String.format("捐献Id[%s]使用的货币类型[%s]不存在", donateId, donateItemType));
				return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
			}

			SpecialItemCfg sic = (SpecialItemCfg) SpecialItemCfgDAO.getDAO().getCfgById(String.valueOf(donateItemType));
			if (sic == null) {
				GameLog.error("帮派捐献", playerId, String.format("捐献Id[%s]使用的货币类型[%s]对应的SpecialItemCfg不存在", donateId, donateItemType));
				return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
			}

			hasCount = player.getReward(def);
			name = sic.getName();

			useMoney = new HashMap<Integer, Integer>(1);
			useMoney.put(donateItemType, -donateVal);
		} else {
			ItemBaseCfg cfg = ItemCfgHelper.GetConfig(donateItemType);
			if (cfg == null) {
				GameLog.error("帮派捐献", playerId, String.format("捐献Id[%s]使用的物品[%s]对应的ItemBaseCfg不存在", donateId, donateItemType));
				return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
			}

			List<ItemData> itemList = itemBagMgr.getItemListByCfgId(userId, donateItemType);
			if (itemList == null || itemList.isEmpty()) {
				GameLog.error("帮派捐献", playerId, String.format("捐献Id[%s]使用的物品[%s]对应的在背包中的数据为空", donateId, donateItemType));
				return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, name + "不足");
			}

			hasCount = itemBagMgr.getItemCountByModelId(userId, donateItemType);
			name = cfg.getName();

			useItemList = new ArrayList<IUseItem>(1);
			useItemList.add(new UseItem(itemList.get(0).getId(), donateVal));

			rewardToken = donateVal;
		}

		if (donateVal > hasCount) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, name + "不足");
		}

		if (!itemBagMgr.useLikeBoxItem(player, useItemList, null, useMoney)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "扣费失败");
		}

		int rewardContribution = donateCfg.getRewardContribution();
		if (isTokenDonate) {
			int perDayLimit = gbct.getMaxContributionLimitPerDay();
			int dayContribution = memberData.getDayContribution();
			if (dayContribution < perDayLimit) {
				int leftContribution = perDayLimit - dayContribution;
				rewardContribution = leftContribution >= rewardContribution ? rewardContribution : leftContribution;
			} else {
				rewardContribution = 0;
			}
		}

		// 更新数据
		memberMgr.updateMemberDataWhenDonate(playerId, baseData.getDonateTimes() + (isTokenDonate ? 0 : 1), now, rewardContribution, isTokenDonate);// 只有令牌捐献才会增加到今日

		// 更新捐献后的帮派数据
		groupBaseDataMgr.updateGroupDonate(player, group.getGroupLogMgr(), donateCfg.getRewardGroupSupply(), donateCfg.getRewardGroupExp(), rewardToken, true);
		// 更新帮派排行榜属性
		GroupRankHelper.addOrUpdateGroup2BaseRank(group);
		UserEventMgr.getInstance().factionDonateVitality(player, 1);

		// 通知日常任务
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.GROUP_DONATE, 1);

		// 设置回应消息
		GroupDonateRspMsg.Builder rsp = GroupDonateRspMsg.newBuilder();
		rsp.setLeftDonateTimes(perDayDonateTimes - baseData.getDonateTimes());
		rsp.setPrivateContribution(baseData.getContribution());
		rsp.setTotalDonateTimes(perDayDonateTimes);

		commonRsp.setIsSuccess(true);
		commonRsp.setGroupDonateRsp(rsp);
		return commonRsp.build().toByteString();
	}

	/**
	 * 帮主转让
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString transferGroupLeaderPostHandler(Player player, TransferGroupLeaderPostReqMsg req) {
		String playerId = player.getUserId();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.TRANSFER_LEADER_POST_TYPE);

		// 帮派数据是不是存在
		UserGroupAttributeDataIF baseData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(playerId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("转让帮主", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("转让帮主", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// 获取自己的成员信息
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("转让帮主", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		String transferMemberId = req.getMemberId();
		GroupMemberDataIF transferMemberData = memberMgr.getMemberData(transferMemberId, false);
		if (transferMemberData == null) {
			GameLog.error("转让帮主", playerId, String.format("帮派Id[%s]要转让给的成员Id[%s]没有找到数据", groupId, transferMemberId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "帮派中无此成员");
		}

		int post = memberData.getPost();
		// 检查个人权限能不能转让帮主
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.TRANSFER_LEADER_POST_VALUE, post, groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, tip);
		}

		if (playerId.equals(transferMemberId)) {// 转让给自己
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您已是帮主");
		}

		// 准备换职位
		memberMgr.transferGroupLeader(playerId, transferMemberId);
		// 改变帮派的信息
		GroupCompetitionMgr.getInstance().notifyGroupInfoChange(group);
		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 退出帮派【检查帮主离线信息】
	 * 
	 * @param player
	 * @return
	 */
	public ByteString quitGroupHandler(Player player) {
		String playerId = player.getUserId();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.QUIT_GROUP_TYPE);

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("退出出帮派", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		// 判断帮派是否存在
		UserGroupAttributeDataMgr userGroupAttributeDataMgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeDataIF baseData = userGroupAttributeDataMgr.getUserGroupAttributeData(playerId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("退出帮派", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("退出帮派", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (GroupCompetitionMgr.getInstance().isGroupInCompetition(groupId)) {
			GameLog.error("退出帮派", playerId, String.format("帮派Id[%s]处于帮派争霸赛事中", groupId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您的帮派在本届赛事还有比赛未完成，不能退出帮派");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 获取自己的成员信息
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("退出帮派", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		// 检查踢出成员的时间
		long now = System.currentTimeMillis();
		long receiveTime = memberData.getReceiveTime();
		if (now - receiveTime < gbct.getQuitGroupLimitTime()) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, String.format("您加入帮派时间不足%s，无法退出", gbct.getQuitGroupLimitTimeTip()));
		}

		int post = memberData.getPost();// 成员职位
		if (post == GroupPost.LEADER_VALUE) {// 是帮主
			String canTransferLeaderMemberId = memberMgr.getCanTransferLeaderMemberId(GroupMemberHelper.transferLeaderComparator);
			if (!StringUtils.isEmpty(canTransferLeaderMemberId)) {
				GroupMemberDataIF transferMemberData = memberMgr.getMemberData(canTransferLeaderMemberId, false);
				if (playerId.equals(canTransferLeaderMemberId) || transferMemberData == null) {// 是自己，进入解散帮派倒计时
					groupBaseDataMgr.updateGroupDismissState(player, now, GroupState.DISOLUTION);
					// 添加到需要解散的列表
					GroupCheckDismissTask.addDismissGroupInfo(groupId, now);
				} else {
					// 帮派移除成员
					memberMgr.kickMember(playerId);// 踢出成员
					// 自己退出帮派
					userGroupAttributeDataMgr.updateDataWhenQuitGroup(player, now);
					// 把帮派帮主修改给排行第一的成员
					memberMgr.updateMemberPost(canTransferLeaderMemberId, GroupPost.LEADER_VALUE);

					// 记录一个帮派日志
					GroupLog log = new GroupLog();
					log.setLogType(GroupLogType.LOG_LEADER_QUIT_VALUE);
					log.setTime(now);
					log.setOpName(player.getUserName());
					log.setName(transferMemberData.getName());
					log.setPost(GroupPost.LEADER_VALUE);
					group.getGroupLogMgr().addLog(player, log);
				}
			} else {// 没有其他成员，帮派进入解散倒计时
				groupBaseDataMgr.updateGroupDismissState(player, now, GroupState.DISOLUTION);
				// 添加到需要解散的列表
				GroupCheckDismissTask.addDismissGroupInfo(groupId, now);
			}
		} else {
			// 帮派移除成员
			memberMgr.kickMember(playerId);// 踢出成员
			// 自己退出帮派
			userGroupAttributeDataMgr.updateDataWhenQuitGroup(player, now);

			// 记录一个帮派日志
			GroupLog log = new GroupLog();
			log.setLogType(GroupLogType.QUIT_GROUP_VALUE);
			log.setTime(now);
			log.setName(player.getUserName());
			group.getGroupLogMgr().addLog(player, log);
		}

		// 排行榜人数减少，检查放入榜
		GroupRankHelper.addOrUpdateGroup2MemberNumRank(group);
		// 更新下基础排行榜中记录的数据
		GroupRankHelper.updateBaseRankExtension(groupData, memberMgr);

		commonRsp.setIsSuccess(true);
		GroupCompetitionMgr.getInstance().notifyGroupInfoChange(group);
		GroupCompetitionMgr.getInstance().notifyGroupMemberLeave(group, playerId);
		return commonRsp.build().toByteString();
	}

	/**
	 * 推荐帮派的处理
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString groupRecommendHandler(Player player, GroupRecommentReqMsg req) {
		String playerId = player.getUserId();

		GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GROUP_RECOMMENT_TYPE);

		// 检查配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("推荐帮派", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "数据异常");
		}

		// 判断个人帮派数据
		UserGroupAttributeDataIF baseData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(playerId);
		String groupId = baseData.getGroupId();
		if (!StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您当前拥有帮派");
		}

		long now = System.currentTimeMillis();
		// 推荐帮派的类型
		GroupRecommentRspMsg.Builder rsp = GroupRecommentRspMsg.newBuilder();
		GroupRecommentType recommentType = req.getRecommentType();
		long logoutTimeNoneRecomment = TimeUnit.DAYS.toMillis(gbct.getLeaderLogoutTimeNoneRecommend());// 帮主离线多少时间之后不推荐
		int recommendSize = gbct.getRecommendSize();// 可以推荐的总数量

		GroupLevelCfgDAO groupLevelDAO = GroupLevelCfgDAO.getDAO();
		// 基础排行榜
		Ranking baseRanking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);

		if (recommentType == GroupRecommentType.RANK_RECOMMENT) {// 排行榜推荐
			if (baseRanking != null) {
				List<GroupSimpleInfo> recommentList = new ArrayList<GroupSimpleInfo>();
				List entryList = baseRanking.getReadOnlyRankingEntries();
				// 打乱索引的
				int size = entryList.size();
				List<Integer> indexArr = GroupUtils.getShuffleIndexList(size);

				for (int i = 0; i < size; i++) {
					if (recommentList.size() >= recommendSize) {
						break;
					}

					MomentRankingEntry rankEntry = (MomentRankingEntry) entryList.get(indexArr.get(i));
					RankingEntry entry = rankEntry.getEntry();

					GroupBaseRankExtAttribute gbrea = (GroupBaseRankExtAttribute) entry.getExtendedAttribute();
					if (gbrea == null) {
						continue;
					}

					// 获取帮派的数据
					Group group = GroupBM.get(gbrea.getGroupId());
					if (group == null) {
						continue;
					}

					if (!canRecomment(group, groupLevelDAO)) {
						continue;
					}

					GroupSimpleInfo.Builder groupSimpleInfo = fillGroupSimpleInfo(group, rankEntry.getRanking());
					if (groupSimpleInfo == null) {
						continue;
					}

					recommentList.add(groupSimpleInfo.build());
				}

				Collections.sort(recommentList, recommentComparator);
				rsp.addAllGroupSimpleInfo(recommentList);
			}
		} else if (recommentType == GroupRecommentType.RANDOM_RECOMMENT) {// 随机推荐
			List<String> hasGroupIdList = new ArrayList<String>();
			int middleRecommentSize = recommendSize / 2;// 推荐数量的中间值
			List<GroupSimpleInfo> simpleInfoList = new ArrayList<GroupSimpleInfo>(recommendSize);// 推荐的列表
			// 创建时间最短推荐
			Ranking ranking = RankingFactory.getRanking(RankType.GROUP_CREATE_TIME_RANK);
			if (ranking != null) {
				List entryList = ranking.getReadOnlyRankingEntries();
				// 打乱索引的
				int size = entryList.size();
				List<Integer> indexArr = GroupUtils.getShuffleIndexList(size);

				for (int i = 0; i < size; i++) {
					if (simpleInfoList.size() >= recommendSize) {
						break;
					}

					MomentRankingEntry rankEntry = (MomentRankingEntry) entryList.get(indexArr.get(i));
					RankingEntry entry = rankEntry.getEntry();

					GroupSimpleExtAttribute gsea = (GroupSimpleExtAttribute) entry.getExtendedAttribute();
					if (gsea == null) {
						continue;
					}
					// 检查是否已经申请该帮派
					String key = gsea.getGroupId();

					// 获取帮派的数据
					Group group = GroupBM.get(key);
					if (group == null) {
						continue;
					}

					if (!canRecomment(group, groupLevelDAO)) {
						continue;
					}

					GroupSimpleInfo.Builder groupSimpleInfo = fillGroupSimpleInfo(group, baseRanking == null ? -1 : baseRanking.getRanking(key));
					if (groupSimpleInfo == null) {
						continue;
					}

					simpleInfoList.add(groupSimpleInfo.build());
					hasGroupIdList.add(key);
				}
			}

			// 人数最少的推荐
			if (simpleInfoList.size() < recommendSize) {
				Ranking ranking0 = RankingFactory.getRanking(RankType.GROUP_MEMBER_NUM_RANK);
				if (ranking0 != null) {
					// 帮主成员离线多久之后不推荐
					List entryList = ranking0.getReadOnlyRankingEntries();
					// 打乱索引的
					int size = entryList.size();
					List<Integer> indexArr = GroupUtils.getShuffleIndexList(size);

					for (int i = 0; i < size; i++) {
						if (simpleInfoList.size() >= recommendSize) {
							break;
						}

						MomentRankingEntry rankEntry = (MomentRankingEntry) entryList.get(indexArr.get(i));
						RankingEntry entry = rankEntry.getEntry();

						GroupSimpleExtAttribute gsea = (GroupSimpleExtAttribute) entry.getExtendedAttribute();
						if (gsea == null) {
							continue;
						}
						// 检查是否已经申请该帮派
						String key = gsea.getGroupId();

						// 防止推荐到相同的帮派
						if (hasGroupIdList.contains(key)) {
							continue;
						}

						// 获取帮派的数据
						Group group = GroupBM.get(key);
						if (group == null) {
							continue;
						}

						if (!canRecomment(group, groupLevelDAO)) {
							continue;
						}

						GroupSimpleInfo.Builder groupSimpleInfo = fillGroupSimpleInfo(group, baseRanking == null ? -1 : baseRanking.getRanking(key));
						if (groupSimpleInfo == null) {
							continue;
						}

						simpleInfoList.add(groupSimpleInfo.build());
					}
				}
			}

			rsp.addAllGroupSimpleInfo(simpleInfoList);
		} else {
			return GroupCmdHelper.groupPersonalFillFailMsg(commonRsp, "您请求的推荐类型不存在");
		}

		commonRsp.setIsSuccess(true);
		commonRsp.setGroupRecommentRsp(rsp);
		return commonRsp.build().toByteString();
	}

	/**
	 * 填充简单的帮派信息
	 * 
	 * @param group 帮派数据
	 * @param rankIndex 排行榜数据
	 * @return
	 */
	private GroupSimpleInfo.Builder fillGroupSimpleInfo(Group group, int rankIndex) {
		// 帮派的基础数据
		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return null;
		}

		GroupSimpleInfo.Builder groupSimpleInfo = GroupSimpleInfo.newBuilder();
		groupSimpleInfo.setGroupId(groupData.getGroupId());
		groupSimpleInfo.setGroupName(groupData.getGroupName());
		groupSimpleInfo.setHeadIcon(groupData.getIconId());
		groupSimpleInfo.setGroupLevel(groupData.getGroupLevel());
		groupSimpleInfo.setGroupMemberNum(group.getGroupMemberMgr().getGroupMemberSize());
		groupSimpleInfo.setGroupDeclaration(groupData.getDeclaration());
		groupSimpleInfo.setRankIndex(rankIndex);
		return groupSimpleInfo;
	}

	private static Comparator<GroupSimpleInfo> recommentComparator = new Comparator<GroupSimpleInfo>() {

		@Override
		public int compare(GroupSimpleInfo o1, GroupSimpleInfo o2) {
			int rIndex1 = o1.getRankIndex();
			int rIndex2 = o2.getRankIndex();
			if (rIndex1 == -1) {
				return 1;
			}

			if (rIndex2 == -1) {
				return 1;
			}

			return rIndex1 - rIndex2;
		}
	};

	/**
	 * 检查成员数量是否已经满了
	 * 
	 * @param group
	 * @param groupLevelDAO
	 * @return
	 */
	private boolean canRecomment(Group group, GroupLevelCfgDAO groupLevelDAO) {
		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return false;
		}

		int groupLevel = groupData.getGroupLevel();
		GroupLevelCfg levelCfg = groupLevelDAO.getLevelCfg(groupLevel);
		if (levelCfg == null) {
			return false;
		}

		int groupMemberSize = group.getGroupMemberMgr().getGroupMemberSize();// 成員數量
		if (groupMemberSize >= levelCfg.getMaxMemberLimit()) {
			return false;
		}

		return true;
	}

	// ======================TEMPLATE======================
	// String playerId = player.getUserId();
	//
	// GroupPersonalCommonRspMsg.Builder commonRsp = GroupPersonalCommonRspMsg.newBuilder();
	// commonRsp.setReqType(RequestType.APPLY_JOIN_GROUP_TYPE);
	//
	// return commonRsp.build().toByteString();
}