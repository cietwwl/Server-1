package com.rw.service.groupCopy;

import com.bm.group.GroupMemberMgr;
import com.bm.groupCopy.GroupCopyResult;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwproto.GroupCommonProto.GroupPost;
import com.rwproto.GroupCopyAdminProto.ApplyRewardInfo;
import com.rwproto.GroupCopyAdminProto.ChaterDamageReqMsg;
import com.rwproto.GroupCopyAdminProto.ChoseDistRewardData;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComRspMsg;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminOpenCopyReqMsg;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminResetCopyReqMsg;
import com.rwproto.GroupCopyAdminProto.MemberDamageInfo;
import com.rwproto.GroupCopyAdminProto.RequestType;

/*
 * @author HC
 * @date 2016年2月18日 下午3:16:30
 * @Description 帮派的基础处理
 */
public class GroupCopyAdminHandler {
	private static GroupCopyAdminHandler instance = new GroupCopyAdminHandler();

	public static GroupCopyAdminHandler getInstance() {
		return instance;
	}

	protected GroupCopyAdminHandler() {
	}

	/**
	 * 开启副本地图
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString open(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.OPEN_COPY);

		GroupCopyAdminOpenCopyReqMsg openReqMsg = req.getOpenReqMsg();

		Group group = GroupHelper.getInstance().getGroup(player);
		boolean success = false;
		GroupCopyResult openResult;
		String mapId = openReqMsg.getMapId();

		if (group != null) {
			// 检查是不是管理员
			GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(player.getUserId(), false);
			if (memberData.getPost() != GroupPost.LEADER.getNumber() && memberData.getPost() != GroupPost.ASSISTANT_LEADER.getNumber()) {
				commonRsp.setTipMsg("你不是帮派管理员，无法进行此操作！");
				commonRsp.setIsSuccess(success);
				return commonRsp.build().toByteString();
			}
			// group.getGroupBaseDataMgr().setGroupSupplier(100000);
			// group.getGroupBaseDataMgr().updateAndSynGroupData(player);
			int supplies = group.getGroupBaseDataMgr().getGroupData().getSupplies();
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);

			if (cfg.getOpenCost() > supplies) {
				GameLog.error(LogModule.GroupCopy, "GroupCopyAdminHandler[open]", "角色尝试开启帮派副本，物资不足，开启消耗[" + cfg.getOpenCost() + "],目前物资：" + supplies, null);
				commonRsp.setTipMsg("帮派物资不足");
				commonRsp.setIsSuccess(success);
				return commonRsp.build().toByteString();
			}
			// 检查一下帮派的等级是否可以开放
			int groupLevel = group.getGroupBaseDataMgr().getGroupData().getGroupLevel();
			if (cfg.getUnLockLv() > groupLevel) {
				commonRsp.setTipMsg("帮派升级到Lv" + cfg.getUnLockLv() + "解锁");
				commonRsp.setIsSuccess(success);
				return commonRsp.build().toByteString();
			}
			openResult = group.getGroupCopyMgr().openMap(player, mapId);
			success = openResult.isSuccess();

			if (success) {
				// 扣除帮派物资
				supplies -= cfg.getOpenCost();
				group.getGroupBaseDataMgr().setGroupSupplier(supplies);
				group.getGroupBaseDataMgr().updateAndSynGroupData(player);
			} else {
				commonRsp.setTipMsg("开启失败");
			}
		}
		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}

	/**
	 * 重置副本地图
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString reset(Player player, GroupCopyAdminComReqMsg req) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.RESET_COPY);

		GroupCopyAdminResetCopyReqMsg openReqMsg = req.getResetReqMsg();
		Group group = GroupHelper.getInstance().getGroup(player);
		boolean success = false;
		if (group != null) {
			String mapId = openReqMsg.getMapId();
			int supplies = group.getGroupBaseDataMgr().getGroupData().getSupplies();
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			if (cfg == null) {
				commonRsp.setIsSuccess(success);
				commonRsp.setTipMsg("数据异常~");
				return commonRsp.build().toByteString();
			}

			if (cfg.getOpenCost() > supplies) {
				commonRsp.setTipMsg("帮派物资不足");
			} else {
				GroupCopyResult openResult = group.getGroupCopyMgr().resetMap(player, mapId);
				success = openResult.isSuccess();
				if (success) {
					// 扣除帮派物资
					supplies -= cfg.getOpenCost();
					group.getGroupBaseDataMgr().setGroupSupplier(supplies);
					group.getGroupBaseDataMgr().updateAndSynGroupData(player);
					commonRsp.setTipMsg("重置成功");
				} else {
					commonRsp.setTipMsg("开启失败");
				}

			}

		}
		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}

	/**
	 * 获取所有章节的帮派副本奖励申请数据
	 * 
	 * @param player
	 * @return
	 */
	public ByteString getAllRewardApplyInfo(Player player) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.GET_APPLY_REWARD_INFO);

		Group group = GroupHelper.getInstance().getGroup(player);
		boolean success = false;
		if (group != null) {
			// 检查角色的可分配次数
			GroupMemberMgr memberMgr = group.getGroupMemberMgr();
			GroupMemberDataIF memberData = memberMgr.getMemberData(player.getUserId(), false);
			if (memberData.getPost() != GroupPost.LEADER_VALUE && memberData.getPost() != GroupPost.ASSISTANT_LEADER_VALUE) {
				commonRsp.setTipMsg("您不是帮派管理员，无此操作权限！");
			} else if (memberData.getAllotRewardCount() <= 0) {
				commonRsp.setTipMsg("今天已没有手动分配次数");
			} else {
				// 可以分配，进行获取数据
				GroupCopyResult result = group.getGroupCopyMgr().applyAllRewardInfo(player);
				if (result.isSuccess()) {
					success = true;
					commonRsp.setApplyInfo((ApplyRewardInfo.Builder) result.getItem());
				}
			}
		} else {
			commonRsp.setTipMsg("请先加入帮派!");
		}

		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}

	/**
	 * 获取成员章节伤害信息
	 * 
	 * @param player
	 * @param reqMsg
	 * @return
	 */
	public ByteString getAllMemberChaterDamage(Player player, GroupCopyAdminComReqMsg reqMsg) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		boolean success = false;

		commonRsp.setReqType(RequestType.GET_CHATER_DAMAGE);

		Group group = GroupHelper.getInstance().getGroup(player);
		if (group != null) {

			// 检查角色的可分配次数
			GroupMemberMgr memberMgr = group.getGroupMemberMgr();
			GroupMemberDataIF memberData = memberMgr.getMemberData(player.getUserId(), false);
			if (memberData.getPost() != GroupPost.LEADER_VALUE && memberData.getPost() != GroupPost.ASSISTANT_LEADER_VALUE) {
				commonRsp.setTipMsg("您不是帮派管理员，无此操作权限！");
			} else {
				// 可以分配，进行获取数据
				ChaterDamageReqMsg msgData = reqMsg.getDamageReqMsg();
				String mapID = msgData.getMapId();
				int itemID = msgData.getItemID();
				GroupCopyResult result = group.getGroupCopyMgr().applyAllRoleDamageInfo(group, mapID, itemID);
				if (result.isSuccess()) {
					success = true;
					commonRsp.setDamageInfo((MemberDamageInfo.Builder) result.getItem());
				}
			}
		} else {
			commonRsp.setTipMsg("请先加入帮派!");
		}

		commonRsp.setIsSuccess(success);
		return commonRsp.build().toByteString();
	}

	/**
	 * 选择手动分配奖励成员
	 * 
	 * @param player
	 * @param commonReq
	 * @return
	 */
	public ByteString choseDistRole(Player player, GroupCopyAdminComReqMsg reqMsg) {
		GroupCopyAdminComRspMsg.Builder commonRsp = GroupCopyAdminComRspMsg.newBuilder();
		boolean success = false;

		commonRsp.setReqType(RequestType.CHOSE_DIST_ROLE);
		commonRsp.setIsSuccess(success);
		Group group = GroupHelper.getInstance().getGroup(player);
		if (group == null) {
			commonRsp.setTipMsg("请先加入帮派!");
			return commonRsp.build().toByteString();
		}
		// 检查角色的可分配次数
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(player.getUserId(), false);
		if (memberData.getPost() != GroupPost.LEADER_VALUE && memberData.getPost() != GroupPost.ASSISTANT_LEADER_VALUE) {
			commonRsp.setTipMsg("您不是帮派管理员，无此操作权限！");
			return commonRsp.build().toByteString();
		}
		if (memberData.getAllotRewardCount() <= 0) {
			commonRsp.setTipMsg("今天已没有手动分配次数");
			return commonRsp.build().toByteString();
		}

		// 可以分配，进行获取数据
		ChoseDistRewardData msgData = reqMsg.getChoseDistReward();
		ChaterDamageReqMsg itemData = msgData.getItemData();

		String mapID = itemData.getMapId();
		int itemID = itemData.getItemID();
		String selectRoleID = msgData.getRoleID();

		// 检查一下选择的角色是否还是帮派里
		Player role = PlayerMgr.getInstance().find(selectRoleID);
		Group group2 = GroupHelper.getInstance().getGroup(role);

		if (group2 == null || !group.getGroupBaseDataMgr().getGroupData().getGroupId().equals(group2.getGroupBaseDataMgr().getGroupData().getGroupId())) {
			commonRsp.setTipMsg("角色已经离开帮派");
			return commonRsp.build().toByteString();
		}

		GroupCopyResult result = group.getGroupCopyMgr().distReward2Role(group, role, mapID, itemID, player.getUserName());
		if (result.isSuccess()) {
			// 扣掉角色分配次数
			int count = memberData.getAllotRewardCount() - 1;
			group.getGroupMemberMgr().resetAllotGroupRewardCount(player.getUserId(), count, false);
			commonRsp.setIsSuccess(true);
			commonRsp.setTipMsg("分配成功");
		}

		return commonRsp.build().toByteString();
	}

}