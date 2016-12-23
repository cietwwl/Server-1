package com.rw.service.group;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.service.group.helper.GroupCmdHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwproto.GroupCommonProto.GroupState;
import com.rwproto.GroupPrayProto.GroupPrayCommonReqMsg;
import com.rwproto.GroupPrayProto.GroupPrayCommonRspMsg;
import com.rwproto.GroupPrayProto.ReqType;

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
		UserGroupAttributeDataIF baseData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(userId);
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
		GroupPrayCommonRspMsg.Builder rsp = GroupPrayCommonRspMsg.newBuilder();
		rsp.setReqType(ReqType.NEED_PRAY);
		return rsp.build().toByteString();
	}

	/**
	 * 赠送某张卡给群成员的处理
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString sendPrayHandler(Player player, GroupPrayCommonReqMsg req) {
		GroupPrayCommonRspMsg.Builder rsp = GroupPrayCommonRspMsg.newBuilder();
		rsp.setReqType(ReqType.SEND_PRAY);
		return rsp.build().toByteString();
	}

	/**
	 * 获取祈福的奖励
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getPrayRewardHandler(Player player) {
		GroupPrayCommonRspMsg.Builder rsp = GroupPrayCommonRspMsg.newBuilder();
		rsp.setReqType(ReqType.GET_PRAY_REWARD);
		return rsp.build().toByteString();
	}
}