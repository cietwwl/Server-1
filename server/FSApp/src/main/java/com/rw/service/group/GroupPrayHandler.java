package com.rw.service.group;

import java.util.List;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.group.helper.GroupCmdHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.dao.GroupPrayCfgDAO;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCommonProto.GroupState;
import com.rwproto.GroupPrayProto.GroupPrayCommonReqMsg;
import com.rwproto.GroupPrayProto.GroupPrayCommonRspMsg;
import com.rwproto.GroupPrayProto.NeedPrayReqMsg;
import com.rwproto.GroupPrayProto.OpenPrayMainViewRspMsg;
import com.rwproto.GroupPrayProto.PrayEntry;
import com.rwproto.GroupPrayProto.PrayRewardInfo;
import com.rwproto.GroupPrayProto.ReqType;
import com.rwproto.GroupPrayProto.SendPrayReqMsg;

/**
 * @Author HC
 * @date 2016年12月22日 下午5:59:30
 * @desc 帮派祈福的处理
 **/

public class GroupPrayHandler {
	private static GroupPrayHandler handler = new GroupPrayHandler();

	public static GroupPrayHandler getHandler() {
		return handler;
	}

	protected GroupPrayHandler() {
	}

	/**
	 * 打开祈福主界面的处理
	 * 
	 * @param player
	 * @return
	 */
	public ByteString openPrayMainViewHandler(Player player) {
		GroupPrayCommonRspMsg.Builder commonRsp = GroupPrayCommonRspMsg.newBuilder();
		commonRsp.setReqType(ReqType.OPEN_MAIN_VIEW);

		String userId = player.getUserId();
		// 检查个人的帮派数据
		UserGroupAttributeDataMgr mgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeData baseData = mgr.getUserGroupAttributeData(userId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("打开帮派祈福主界面", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("打开帮派祈福主界面", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		// 检查自己是否是帮派成员
		GroupMemberDataIF memberData = memberMgr.getMemberData(userId, false);
		if (memberData == null) {
			GameLog.error("打开帮派祈福主界面", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		OpenPrayMainViewRspMsg.Builder openMainViewRsp = OpenPrayMainViewRspMsg.newBuilder();

		List<String> prayList = mgr.getPrayList(userId);
		// 获取所有帮派成员的祈福数据
		List<? extends GroupMemberDataIF> memberList = memberMgr.getMemberSortList(null);
		for (int i = 0, size = memberList.size(); i < size; i++) {
			GroupMemberDataIF member = memberList.get(i);
			int prayCardId = member.getPrayCardId();
			if (prayCardId <= 0) {
				continue;
			}

			// 自己的另外发送出来
			String id = member.getUserId();
			if (id.equals(userId)) {
				continue;
			}

			UserGroupAttributeData groupAttrData = mgr.getUserGroupAttributeData(id);
			if (groupAttrData == null) {
				continue;
			}

			// 检查是否是今天祈福的，不是今天的或者上次的时间是0，辣么就直接不用发送到客户端
			long lastPrayTime = groupAttrData.getLastPrayTime();
			if (DateUtils.isResetTime(5, 0, 0, lastPrayTime)) {
				continue;
			}

			int prayProcess = member.getPrayProcess();
			PrayEntry.Builder prayEntry = PrayEntry.newBuilder();
			prayEntry.setSoulId(prayCardId);// 当前祈福的魂石Id
			prayEntry.setMemberId(id);// 设置成员的Id
			prayEntry.setProcess(prayProcess);// 设置当前的数量
			prayEntry.setHasSend(prayList != null && prayList.contains(id));// 检查是否赠送过某个人的

			openMainViewRsp.addEntry(prayEntry);
		}

		// 检查自己当前的祈福状态
		boolean resetTime = DateUtils.isResetTime(5, 0, 0, baseData.getLastPrayTime());// 是否是可以重置
		int prayCardId = memberData.getPrayCardId();
		if (prayCardId > 0) {// 祈福过
			int prayProcess = memberData.getPrayProcess();
			if (!resetTime) {// 不是重置点或者进度满了还没领取过奖励
				PrayEntry.Builder prayEntry = PrayEntry.newBuilder();
				prayEntry.setSoulId(prayCardId);// 当前祈福的魂石Id
				prayEntry.setMemberId(userId);// 设置成员的Id
				prayEntry.setProcess(prayProcess);// 设置当前的数量
				prayEntry.setHasSend(false);// 检查是否赠送过某个人的
				openMainViewRsp.addEntry(prayEntry);
			}
		}

		openMainViewRsp.setHasPray(!resetTime);// 是否已经祈福过了
		PrayRewardInfo rewardInfo = memberMgr.checkPrayCanGetReward(userId);
		if (rewardInfo != null) {
			System.err.println("获取到的奖励信息----->" + rewardInfo);
			openMainViewRsp.setPrayReward(rewardInfo);
		}

		commonRsp.setOpenPrayMainViewRsp(openMainViewRsp);
		commonRsp.setIsSuccess(true);

		return commonRsp.build().toByteString();
	}

	/**
	 * 请求祈福的处理
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString needPrayHandler(Player player, GroupPrayCommonReqMsg req) {
		GroupPrayCommonRspMsg.Builder commonRsp = GroupPrayCommonRspMsg.newBuilder();
		commonRsp.setReqType(ReqType.NEED_PRAY);

		String userId = player.getUserId();
		// 检查个人的帮派数据
		UserGroupAttributeDataMgr mgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeData baseData = mgr.getUserGroupAttributeData(userId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("请求祈福", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("请求祈福", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		// 检查自己是否是帮派成员
		GroupMemberDataIF memberData = memberMgr.getMemberData(userId, false);
		if (memberData == null) {
			GameLog.error("请求祈福", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		// 检查自己当前的祈福状态
		boolean resetTime = DateUtils.isResetTime(5, 0, 0, baseData.getLastPrayTime());// 是否是可以重置
		if (!resetTime) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "每天只能祈福一次");
		}

		NeedPrayReqMsg needPrayReq = req.getNeedPrayReq();
		int soulId = needPrayReq.getSoulId();
		if (soulId <= 0) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "请选择祈福的魂石");
		}

		int prayLimit = GroupPrayCfgDAO.getCfgDAO().getSoulLimit(soulId);
		if (prayLimit <= 0) {
			GameLog.error("请求祈福", userId, String.format("请求的魂石[%s]，配置中的祈福上限是0，就是不能用于祈福", soulId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "此魂石不能祈福");
		}

		// int prayCardId = memberData.getPrayCardId();
		// if (prayCardId > 0) {// 祈福过
		// // 检查别人赠送给自己的魂石卡是否满了，满了就只能领取了之后才能重置
		// int prayProcess = memberData.getPrayProcess();
		// if (prayProcess > 0 && baseData.getState() <= 0) {// 还没领取过
		// GameLog.error("请求祈福", userId, String.format("角色[%s]昨日祈福的卡已经足够了，但是还没领取", userId));
		// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "请先领取已经完成的祈福奖励");
		// }
		// }

		// 重置数据
		memberMgr.resetPrayData(userId, soulId);

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	/**
	 * 赠送某张卡给群成员的处理
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString sendPrayHandler(Player player, GroupPrayCommonReqMsg req) {
		GroupPrayCommonRspMsg.Builder commonRsp = GroupPrayCommonRspMsg.newBuilder();
		commonRsp.setReqType(ReqType.SEND_PRAY);

		String userId = player.getUserId();
		// 检查个人的帮派数据
		UserGroupAttributeDataMgr mgr = UserGroupAttributeDataMgr.getMgr();
		UserGroupAttributeData baseData = mgr.getUserGroupAttributeData(userId);
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您当前还没有帮派");
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("赠送魂石卡", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("赠送魂石卡", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "帮派已经是解散状态");
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		// 检查自己是否是帮派成员
		GroupMemberDataIF memberData = memberMgr.getMemberData(userId, false);
		if (memberData == null) {
			GameLog.error("赠送魂石卡", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
		}

		SendPrayReqMsg sendPrayReq = req.getSendPrayReq();
		String memberId = sendPrayReq.getMemberId();
		if (userId.equals(memberId)) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您不能对自己祈福");
		}

		List<String> prayList = mgr.getPrayList(userId);
		if (prayList.contains(memberId)) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您已经祈福过该成员");
		}

		// 检查要赠送给的成员不存在
		GroupMemberDataIF otherMemberData = memberMgr.getMemberData(memberId, false);
		if (otherMemberData == null) {
			GameLog.error("赠送魂石卡", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, memberId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "赠予的成员不是帮派成员");
		}

		UserGroupAttributeData otherUserGroupBaseData = mgr.getUserGroupAttributeData(memberId);
		if (otherUserGroupBaseData == null) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "赠予的成员不是帮派成员");
		}

		boolean resetTime = DateUtils.isResetTime(5, 0, 0, otherUserGroupBaseData.getLastPrayTime());// 是否是可以重置
		if (resetTime) {// 已经可以重置了，就不能赠予了
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "赠予的成员今日暂未请求祈福");
		}

		int prayCardId = otherMemberData.getPrayCardId();
		if (prayCardId <= 0) {// 祈福过
			GameLog.error("赠送魂石卡", userId, String.format("帮派Id[%s]中角色[%s]当前祈福卡的Id是[%s]不存在不能接受祈福", groupId, memberId, prayCardId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "赠予的成员今日暂未请求祈福");
		}

		// 检查别人赠送给自己的魂石卡是否满了，满了就只能领取了之后才能重置
		int soulLimit = GroupPrayCfgDAO.getCfgDAO().getSoulLimit(prayCardId);
		int prayProcess = otherMemberData.getPrayProcess();
		if (soulLimit > 0 && prayProcess >= soulLimit) {// 还没领取过
			GameLog.error("请求祈福", userId, String.format("角色[%s]昨日祈福的卡已经足够了，但是还没领取", memberId));
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "该玩家今日祈愿已完成");
		}

		// 检查自己身上的卡有没有数量可以赠送
		if (!ItemBagMgr.getInstance().useItemByCfgId(player, prayCardId, 1)) {
			return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您背包里暂无祈福的魂石");
		}

		mgr.addPrayUserId2List(userId, memberId);// 增加赠送的人
		// 增加对方的魂石数量
		memberMgr.addPrayProcess(memberId);// 增加自己的魂石数量

		commonRsp.setIsSuccess(true);
		return commonRsp.build().toByteString();
	}

	// /**
	// * 获取祈福的奖励
	// *
	// * @param player
	// * @return
	// */
	// public ByteString getPrayRewardHandler(Player player) {
	// GroupPrayCommonRspMsg.Builder commonRsp = GroupPrayCommonRspMsg.newBuilder();
	// commonRsp.setReqType(ReqType.GET_PRAY_REWARD);
	//
	// String userId = player.getUserId();
	// // 检查个人的帮派数据
	// UserGroupAttributeDataMgr mgr = UserGroupAttributeDataMgr.getMgr();
	// UserGroupAttributeData baseData = mgr.getUserGroupAttributeData(userId);
	// String groupId = baseData.getGroupId();
	// if (StringUtils.isEmpty(groupId)) {
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您当前还没有帮派");
	// }
	//
	// Group group = GroupBM.get(groupId);
	// if (group == null) {
	// GameLog.error("领取完成的祈福卡", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
	// }
	//
	// GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
	// if (groupData == null) {
	// GameLog.error("领取完成的祈福卡", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
	// }
	//
	// if (groupData.getGroupState() == GroupState.DISOLUTION_VALUE) {
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "帮派已经是解散状态");
	// }
	//
	// GroupMemberMgr memberMgr = group.getGroupMemberMgr();
	// // 检查自己是否是帮派成员
	// GroupMemberDataIF memberData = memberMgr.getMemberData(userId, false);
	// if (memberData == null) {
	// GameLog.error("领取完成的祈福卡", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "您还不是帮派成员");
	// }
	//
	// int prayCardId = memberData.getPrayCardId();
	// if (prayCardId <= 0) {// 祈福过
	// GameLog.error("领取完成的祈福卡", userId, String.format("帮派Id[%s]中角色[%s]当前祈福卡的Id是[%s]不存在不能领取奖励", groupId, userId, prayCardId));
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "今天暂未祈福");
	// }
	//
	// int prayProcess = memberData.getPrayProcess();// 当前的进度
	// // 检查自己当前的祈福状态
	// boolean resetTime = DateUtils.isResetTime(5, 0, 0, baseData.getLastPrayTime());// 是否是可以重置
	// if (resetTime) {// 可以重置
	// if (prayProcess <= 0) {
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "今天暂未祈福");
	// }
	// } else {
	// // 检查别人赠送给自己的魂石卡是否满了，满了就只能领取了之后才能重置
	// int soulLimit = GroupPrayCfgDAO.getCfgDAO().getSoulLimit(prayCardId);
	// if (prayProcess < soulLimit) {// 还没完成
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "祈福尚未完成，暂不能领取");
	// }
	// }
	//
	// if (baseData.getState() > 0) {// 是否领取过
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "祈福奖励不能重复领取");
	// }
	//
	// // 领取魂石
	// if (!ItemBagMgr.getInstance().addItem(player, prayCardId, prayProcess)) {
	// return GroupCmdHelper.groupPrayFillFailMsg(commonRsp, "祈福奖励领取失败");
	// }
	//
	// mgr.updatePrayGetState(userId);// 更新状态
	//
	// commonRsp.setIsSuccess(true);
	// return commonRsp.build().toByteString();
	// }
}