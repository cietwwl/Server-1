package com.rw.service.group;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupBaseDataMgr;
import com.bm.rank.groupCompetition.groupRank.GroupFightingRefreshTask;
import com.bm.rank.groupFightOnline.GFGroupBiddingRankMgr;
import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.rw.service.group.helper.GroupCmdHelper;
import com.rw.service.group.helper.GroupRankHelper;
import com.rw.support.FriendSupportFactory;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.group.GroupCheckDismissTask;
import com.rwbase.dao.group.GroupUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupFunctionCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.item.SpecialItemCfgDAO;
import com.rwbase.dao.item.pojo.SpecialItemCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GroupBaseMgrProto.CreateGroupReqMsg;
import com.rwproto.GroupBaseMgrProto.CreateGroupRspMsg;
import com.rwproto.GroupBaseMgrProto.GroupBaseMgrCommonRspMsg;
import com.rwproto.GroupBaseMgrProto.GroupSettingReqMsg;
import com.rwproto.GroupBaseMgrProto.ModifyAnnouncementReqMsg;
import com.rwproto.GroupBaseMgrProto.ModifyGroupNameReqMsg;
import com.rwproto.GroupCommonProto.GroupFunction;
import com.rwproto.GroupCommonProto.GroupState;
import com.rwproto.GroupCommonProto.RequestType;

/*
 * @author HC
 * @date 2016年2月18日 下午3:16:30
 * @Description 帮派的基础处理
 */
public class GroupBaseManagerHandler {
	private static final String QUIT_GROUP_TIME_TIP_FOR_CREATE = "%s后才可创建帮派";
	private static GroupBaseManagerHandler handler = new GroupBaseManagerHandler();

	public static GroupBaseManagerHandler getHandler() {
		return handler;
	}

	protected GroupBaseManagerHandler() {
	}

	/**
	 * 创建帮派的处理
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString createGroupHandler(Player player, CreateGroupReqMsg req) {
		GroupBaseMgrCommonRspMsg.Builder commonRsp = GroupBaseMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.CREATE_GROUP_TYPE);

		String playerId = player.getUserId();// 角色的Id
		RefParam<String> outTip = new RefParam<String>();
		// 检查当前角色的等级有没有达到可以使用帮派功能
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.GROUP, player, outTip)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, outTip.value);
		}

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("创建帮派", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 检查货币类型
		int[] createGroupPriceArr = gbct.getCreateGroupPriceArr();
		int type = createGroupPriceArr[0];
		SpecialItemCfg sicfg = (SpecialItemCfg) SpecialItemCfgDAO.getDAO().getCfgById(String.valueOf(type));
		if (sicfg == null) {
			GameLog.error("创建帮派", playerId, String.format("找不到对应的货币类型[%s]的配置表", type));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 检查当前角色有没有帮派
		UserGroupAttributeDataMgr mgr = player.getUserGroupAttributeDataMgr();
		UserGroupAttributeDataIF baseData = mgr.getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (!StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您当前拥有帮派,id=" + groupId);
		}

		long now = System.currentTimeMillis();
		// 检查冷却时间
		long quitGroupTime = baseData.getQuitGroupTime();
		long needCoolingTime = TimeUnit.SECONDS.toMillis(gbct.getJoinGroupCoolingTime());
		if (quitGroupTime > 0 && (now - quitGroupTime) < needCoolingTime) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, String.format(QUIT_GROUP_TIME_TIP_FOR_CREATE, GroupUtils.coolingTimeTip(now, quitGroupTime, needCoolingTime)));
		}

		// 检查金钱足不足够
		long count = player.getReward(eSpecialItemId.getDef(type));
		if (createGroupPriceArr[1] > count) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, String.format("%s不足,数量为" + count + " 需要 " + createGroupPriceArr[1], sicfg.getName()));
		}

		// 检查传递过来的帮派名字
		String groupName = req.getGroupName();
		if (StringUtils.isEmpty(groupName)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字不能为空");
		}

		int nameLimit = gbct.getGroupNameCharLimit() * 2;
		// 检查名字的合法性和长度
		int nameLength = GroupUtils.getChineseNumLimitLength(groupName);
		if (nameLength == -1) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字仅支持中文，英文和数字");
		} else if (nameLength > nameLimit) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字过长");
		} else if (CharFilterFactory.getCharFilter().checkWords(groupName, true, true, true, true)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字包含非法字符");
		}

		// TODO HC 客户端传递的帮派头像ID的时数据验证，现在先暂时不验证
		String icon = req.getIcon();

		Group group = GroupBM.getInstance().create(player, groupName, icon, gbct.getDefaultValidateType(), gbct.getDefaultApplyLevel());
		if (group == null) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字已存在，名字为=" + groupName);
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派创建失败");
		}

		// 扣除金钱
		if (!ItemBagMgr.getInstance().addItem(player, type, -createGroupPriceArr[1])) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "扣费失败");
		}

		// 个人的某些帮派信息
		String newGroupId = groupData.getGroupId();
		mgr.updateDataWhenHasGroup(player, newGroupId, groupName);// 更新数据

		// 基础排行榜
		int rankIndex = GroupRankHelper.getInstance().addOrUpdateGroup2BaseRank(group);
		// 新创建榜
		GroupRankHelper.getInstance().addGroup2CreateTimeRank(group);
		// 人数榜
		GroupRankHelper.getInstance().addOrUpdateGroup2MemberNumRank(group);

		player.getStoreMgr().AddStore();
		// // 推送帮派数据
		// group.synGroupDataAndMemberData(player);

		// 设置回应消息
		CreateGroupRspMsg.Builder rsp = CreateGroupRspMsg.newBuilder();
		if (rankIndex != -1) {
			rsp.setRankIndex(rankIndex);
		}

		commonRsp.setIsSuccess(true);
		commonRsp.setCreateGroupRsp(rsp);
		GameWorldFactory.getGameWorld().executeAccountTask(newGroupId, new GroupFightingRefreshTask(newGroupId));
		return commonRsp.build().toByteString();
	}

	/**
	 * 修改群公告【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString modifyGroupAnnouncement(Player player, ModifyAnnouncementReqMsg req) {
		String playerId = player.getUserId();

		GroupBaseMgrCommonRspMsg.Builder commonRsp = GroupBaseMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.MODIFY_ANNOUNCEMENT_TYPE);

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("帮派改公告", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "数据异常");
		}

		int charLimit = gbct.getAnnouncementCharLimit() * 2;

		// 检查下公告的内容
		String announcement = req.getAnnouncement();
		if (StringUtils.isEmpty(announcement)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "公告不能为空");
		}

		// 公告内容长度过长
		int charLength = GroupUtils.getContentLength(announcement);
		if (charLength > charLimit) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "公告过长");
		}

		// 检查是否有帮派
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您当前没有帮派");
		}

		// 检查帮派是否存在
		if (!GroupBM.getInstance().groupIsExist(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			GameLog.error("帮派改公告", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("帮派改公告", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		// 是非正常状态，不能操作这个功能
		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("帮派改公告", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "权限不足");
		}

		// 检查个人能不能修改帮派公告
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.MODIFY_ANNOUNCEMENT_VALUE, memberData.getPost(), groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, tip);
		}

		// 屏蔽公告中的特殊字符
		String newAnnouncement = CharFilterFactory.getCharFilter().replaceDiryWords(announcement, "**", true, false);
		groupBaseDataMgr.updateGroupAnnouncement(player, newAnnouncement);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 修改帮派名字【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString modifyGroupNameHandler(Player player, ModifyGroupNameReqMsg req) {
		String playerId = player.getUserId();

		GroupBaseMgrCommonRspMsg.Builder commonRsp = GroupBaseMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.MODIFY_GROUP_NAME_TYPE);

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("帮派改名", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "数据异常");
		}

		int nameLimit = gbct.getGroupNameCharLimit() * 2;
		// 检查下名字的内容
		final String groupName = req.getGroupName();
		if (StringUtils.isEmpty(groupName)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "名字不能为空");
		}

		// 检查名字的合法性和长度
		int nameLength = GroupUtils.getChineseNumLimitLength(groupName);
		if (nameLength == -1) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字仅支持中文，英文和数字");
		} else if (nameLength > nameLimit) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字过长");
		} else if (CharFilterFactory.getCharFilter().checkWords(groupName, true, true, true, true)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字包含非法字符");
		}

		// 检查是否有帮派
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您当前没有帮派");
		}

		// 检查帮派是否存在
		if (!GroupBM.getInstance().groupIsExist(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			GameLog.error("帮派改名", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("帮派改名", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("帮派改名", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "权限不足");
		}

		// 检查个人权限能不能修改帮派名字
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.MODIFY_GROUP_NAME_VALUE, memberData.getPost(), groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, tip);
		}

		// 是非正常状态，不能操作这个功能
		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// 跟当前的名字没做什么修改
		if (groupData.getGroupName().equals(groupName)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字并未有改动");
		}

		// 检查帮派名字是否存在
		if (GroupBM.getInstance().hasName(groupName)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派名字已存在");
		}

		int[] price = gbct.getRenamePriceArr();
		int type = price[0];
		SpecialItemCfg sicfg = (SpecialItemCfg) SpecialItemCfgDAO.getDAO().getCfgById(String.valueOf(type));
		if (sicfg == null) {
			GameLog.error("创建帮派", playerId, "找不到对应的货币类型" + type + "的配置表");
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 检查金钱足不足够
		long count = player.getReward(eSpecialItemId.getDef(type));
		if (price[1] > count) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, sicfg.getName() + "不足");
		}

		// 扣除金钱
		if (!ItemBagMgr.getInstance().addItem(player, type, -price[1])) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "扣费失败");
		}

		groupBaseDataMgr.updateGroupName(player, groupName);

		PlayerTask task = new PlayerTask() {

			@Override
			public void run(Player player) {
				player.getUserGroupAttributeDataMgr().updateGroupName(player, groupName);

				// 通知好友修改了帮派名字
				FriendSupportFactory.getSupport().notifyFriendInfoChanged(player);
			}
		};

		// NotifyAllModifyGroupName通知所有人修改名字
		List<? extends GroupMemberDataIF> memberSortList = group.getGroupMemberMgr().getMemberSortList(null);
		for (int i = 0, size = memberSortList.size(); i < size; i++) {
			GroupMemberDataIF member = memberSortList.get(i);
			String userId = member.getUserId();
			if (!PlayerMgr.getInstance().isOnline(userId)) {
				continue;
			}

			GameWorldFactory.getGameWorld().asyncExecute(userId, task);
		}

		commonRsp.setIsSuccess(true);
		GFGroupBiddingRankMgr.updateGFBidRankInfo(groupId);
		GroupCompetitionMgr.getInstance().notifyGroupInfoChange(group);
		return commonRsp.build().toByteString();
	}

	/**
	 * 帮派设置的处理消息【检查帮主离线信息】
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString groupSettingHandler(Player player, GroupSettingReqMsg req) {
		String playerId = player.getUserId();// 角色的Id

		GroupBaseMgrCommonRspMsg.Builder commonRsp = GroupBaseMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GROUP_SETTING_TYPE);

		// 检查一下唯一的配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			GameLog.error("帮派设置", playerId, "没有找到帮派唯一基础的配置表");
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "数据异常");
		}

		// 检查是否有帮派
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您当前没有帮派");
		}

		// 检查帮派是否存在
		if (!GroupBM.getInstance().groupIsExist(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			GameLog.error("帮派设置", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("帮派设置", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派不存在");
		}

		// TODO HC 检查帮主离线时间检查
		group.checkGroupLeaderLogoutTime();

		// 是非正常状态，不能操作这个功能
		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "当前帮派在解散倒计时，不能进行任何修改操作");
		}

		// 成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("帮派设置", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "权限不足");
		}

		// 检查个人能不能设置帮派
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.GROUP_SETTING_VALUE, memberData.getPost(), groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, tip);
		}

		String newDeclaration = null;
		String newIconId = null;
		int newApplyLevel = 0;
		int newValidation = 0;
		// 检查宣言
		if (req.hasDeclaration()) {
			String declaration = req.getDeclaration();
			if (!StringUtils.isEmpty(declaration)) {// 不是空的
				// 检查长度限制
				int declarationCharLimit = gbct.getDeclarationCharLimit() * 2;
				int contentLength = GroupUtils.getContentLength(declaration);
				if (contentLength > declarationCharLimit) {
					return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "宣言长度过长");
				}
				// 替换特殊字符
				newDeclaration = CharFilterFactory.getCharFilter().replaceDiryWords(declaration, "**", true, false);
			}
		}

		// 检查验证等级
		if (req.hasApplyLevel()) {
			int applyLevel = req.getApplyLevel();
			if (!gbct.getGroupApplyLevelLimitList().contains(applyLevel)) {
				GameLog.error("帮派设置", playerId, String.format("验证的等级[%s]不存在我们的规定数据中", applyLevel));
				return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "验证等级异常");
			}

			newApplyLevel = applyLevel;
		}

		// 检查图标
		if (req.hasGroupIcon()) {
			String groupIcon = req.getGroupIcon();
			if (!StringUtils.isEmpty(groupIcon)) {
				newIconId = groupIcon;
			}
		}

		// 检查验证类型
		if (req.hasValidateType()) {
			newValidation = req.getValidateType().getNumber();
		}

		groupBaseDataMgr.updateGroupSetting(player, newIconId, newDeclaration, newValidation, newApplyLevel);

		commonRsp.setIsSuccess(true);
		commonRsp.setTipMsg("设置成功");
		GroupCompetitionMgr.getInstance().notifyGroupInfoChange(group);
		return commonRsp.build().toByteString();
	}

	/**
	 * 解散帮派
	 * 
	 * @param player
	 * @return
	 */
	public ByteString dismissTheGroupHandler(Player player) {
		String playerId = player.getUserId();

		GroupBaseMgrCommonRspMsg.Builder commonRsp = GroupBaseMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.DISMISS_THE_GROUP_TYPE);

		// 判断帮派是否存在
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			GameLog.error("解散帮派", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("解散帮派", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		// TODO HC 状态判断，某些状态中是不能解散帮派的，比如帮派副本

		// 获取自己的成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("解散帮派", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		int post = memberData.getPost();// 成员职位

		// 检查个人权限能不能解散帮派
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.DISMISS_THE_GROUP_VALUE, post, groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, tip);
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		// 处理解散帮派
		long now = System.currentTimeMillis();
		groupBaseDataMgr.updateGroupDismissState(player, now, GroupState.DISOLUTION);

		// 添加到需要解散的列表
		GroupCheckDismissTask.addDismissGroupInfo(groupId, now);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 取消解散帮派
	 * 
	 * @param player
	 * @return
	 */
	public ByteString cancelDismissTheGroupHandler(Player player) {
		String playerId = player.getUserId();

		GroupBaseMgrCommonRspMsg.Builder commonRsp = GroupBaseMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.CANCEL_DISMISS_THE_GROUP_TYPE);

		// 判断帮派是否存在
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			GameLog.error("取消解散帮派", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataMgr groupBaseDataMgr = group.getGroupBaseDataMgr();
		GroupBaseDataIF groupData = groupBaseDataMgr.getGroupData();
		if (groupData == null) {
			GameLog.error("取消解散帮派", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() != GroupState.DISOLUTION_VALUE) {// 不是解散状态
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "当前帮派不是解散状态");
		}

		// 获取自己的成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("取消解散帮派", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		int post = memberData.getPost();// 成员职位

		// 检查个人权限能不能取消解散帮派
		String tip = GroupFunctionCfgDAO.getDAO().canUseFunction(GroupFunction.CANCEL_DISMISS_THE_GROUP_VALUE, post, groupData.getGroupLevel());
		if (!StringUtils.isEmpty(tip)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, tip);
		}

		// 移除需要解散列表中的数据
		GroupCheckDismissTask.removeDismissGroupInfo(groupId);

		// 处理解散帮派
		groupBaseDataMgr.updateGroupDismissState(player, 0, GroupState.NORMAL);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 获取帮派日志
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getGroupLogHandler(Player player) {
		String playerId = player.getUserId();

		GroupBaseMgrCommonRspMsg.Builder commonRsp = GroupBaseMgrCommonRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.THE_LOG_OF_GROUP_TYPE);

		// 检查个人的帮派数据
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			GameLog.error("获取帮派日志", playerId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (group.getGroupBaseDataMgr().getGroupData() == null) {
			GameLog.error("获取帮派日志", playerId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(playerId, false);
		if (memberData == null) {
			GameLog.error("获取帮派日志", playerId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, playerId));
			return GroupCmdHelper.groupBaseMgrFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		group.getGroupLogMgr().synLogData(player, -1);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}
}