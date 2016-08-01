package com.bm.group;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rwproto.GroupCommonProto.GroupPost;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.group.GroupMemberJoinCallback;
import com.playerdata.groupFightOnline.bm.GFOnlineListenerPlayerChange;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupMemberData;
import com.rwbase.dao.group.pojo.db.dao.GroupMemberDataHolder;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

/**
 * 帮派成员管理类
 * 
 * @author HC
 *
 */
public class GroupMemberMgr {

	private GroupMemberDataHolder holder;// 成员管理的Holder

	public GroupMemberMgr(String groupId) {
		holder = new GroupMemberDataHolder(groupId);
	}

	/**
	 * 获取成员信息
	 * 
	 * @param memberId 成员Id
	 * @param isApply 是否是申请成员
	 * @return
	 */
	public GroupMemberDataIF getMemberData(String userId, boolean isApply) {
		return holder.getMemberData(userId, isApply);
	}

	/**
	 * 获取帮派申请的成员信息列表
	 * 
	 * @param comparator 获取出来成员的排序规则<b>默认是按照申请时间晚的排前。是此需求就传入<i>null</i></b>
	 * @return 返回一个默认排序规则，或者自定义规则的申请成员信息列表
	 */
	public List<? extends GroupMemberDataIF> getApplyMemberSortList(Comparator<GroupMemberDataIF> comparator) {
		return holder.getApplyMemberSortList(comparator);
	}

	/**
	 * 获取成员的列表
	 * 
	 * @param comparator
	 * @return
	 */
	public List<? extends GroupMemberDataIF> getMemberSortList(Comparator<GroupMemberDataIF> comparator) {
		return holder.getMemberSortList(comparator);
	}

	/**
	 * 获取满足可以转让给帮主的成员信息列表
	 * 
	 * @param comparator
	 * @return
	 */
	public String getCanTransferLeaderMemberId(Comparator<GroupMemberDataIF> comparator) {
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			return null;
		}

		long now = System.currentTimeMillis();
		long logoutMaxTimeMillis = TimeUnit.DAYS.toMillis(gbct.getAutoTransGroupLeaderDay());

		// 成员列表
		List<GroupMemberData> memberList = holder.getMemberSortList(comparator);

		GroupMemberDataIF firstNonLeaderMember = null;// 排序中第一个非老大
		for (int i = 0, memberSize = memberList.size(); i < memberSize; i++) {
			GroupMemberDataIF item = memberList.get(i);
			if (item == null) {
				continue;
			}

			int post = item.getPost();
			if (post == GroupPost.LEADER_VALUE) {
				continue;
			}

			if (firstNonLeaderMember == null) {
				firstNonLeaderMember = item;
			}

			long logoutTime = item.getLogoutTime();
			if (logoutTime > 0 && now - logoutTime >= logoutMaxTimeMillis) {
				continue;
			}

			return item.getUserId();
		}

		return firstNonLeaderMember == null ? null : firstNonLeaderMember.getUserId();
	}

	/**
	 * 增加一个申请成员
	 * 
	 * @param userId 角色Id
	 * @param groupId 帮派Id
	 * @param name 角色名字
	 * @param icon 角色头像
	 * @param templateId 英雄的模版Id
	 * @param level 角色等级
	 * @param vipLevel 角色Vip等级
	 * @param job 角色职业
	 * @param post 角色职位
	 * @param fighting 战力
	 * @param applyTime 申请时间
	 * @param receiveTime 接受时间
	 * @param isAddApply 是否是增加申请成员
	 * @param alloctTime 手动分配帮派奖励次数
	 * @return
	 */
	public GroupMemberDataIF addMemberData(String userId, String groupId, String name, String icon, String templateId, int level, int vipLevel, int job, int post, int fighting, long applyTime,
			long receiveTime, boolean isAddApply, String headbox, int alloctTime) {
		GroupMemberData memberData = newGroupMemberData(userId, groupId, name, icon, templateId, level, vipLevel, job, post, fighting, applyTime, receiveTime, headbox, alloctTime);
		return holder.addMember(userId, memberData, isAddApply);
	}

	/**
	 * 获取帮派成员
	 * 
	 * @return
	 */
	public GroupMemberDataIF getGroupLeader() {
		return holder.getGroupLeader();
	}

	/**
	 * 获取某个官职在帮派中对应的数量
	 * 
	 * @param post
	 * @return
	 */
	public int getPostMemberSize(int post) {
		return holder.getPostMemberSize(post);
	}

	/**
	 * 新创建一个帮派成员
	 * 
	 * @param playerId 角色Id
	 * @param groupId 帮派Id
	 * @param name 角色名字
	 * @param icon 角色头像
	 * @param templateId 英雄的模版Id
	 * @param level 角色等级
	 * @param vipLevel 角色Vip等级
	 * @param job 角色职业
	 * @param post 角色职位
	 * @param fighting 战力
	 * @param applyTime 申请时间
	 * @param receiveTime 接受时间
	 * @param alloctTime 手动分配帮派奖励次数
	 * @return
	 */
	private GroupMemberData newGroupMemberData(String playerId, String groupId, String name, String icon, String templateId, int level, int vipLevel, int job, int post, int fighting, long applyTime,
			long receiveTime, String headbox, int alloctTime) {
		GroupMemberData memberData = new GroupMemberData();
		memberData.setId(newMemberUniqueId(playerId, groupId));
		memberData.setUserId(playerId);
		memberData.setGroupId(groupId);
		memberData.setHeadId(icon);
		memberData.setJob((byte) job);
		memberData.setLevel((short) level);
		memberData.setName(name);
		memberData.setPost(post);
		memberData.setVipLevel((byte) vipLevel);
		memberData.setFighting(fighting);
		memberData.setApplyTime(applyTime);
		memberData.setReceiveTime(receiveTime);
		memberData.setTemplateId(templateId);
		memberData.setHeadbox(headbox);
		memberData.setAllotRewardCount(alloctTime);
		return memberData;
	}

	/**
	 * 当接受到成员之后，对成员数据进行修改
	 * 
	 * @param userId
	 * @param receiveTime
	 */
	public synchronized void updateMemberDataWhenByReceive(String userId, long receiveTime, GroupMemberJoinCallback call) {
		GroupMemberData memberData = holder.getMemberData(userId, true);
		if (memberData == null) {
			return;
		}

		String memberId = memberData.getId();
		// 从申请列表中移除
		holder.removeApplyMemberDataId(userId);
		// 增加到成员列表
		holder.putNewMemberId(userId, memberId);

		// 更新下成员信息
		memberData.setReceiveTime(receiveTime);
		if (call != null) {
			call.updateGroupMemberData(memberData);
		}

		holder.updateMemberData(memberId);
	}

	/**
	 * 更新成员的职位
	 * 
	 * @param userId
	 * @param post
	 */
	public synchronized void updateMemberPost(String userId, int post) {
		GroupMemberData memberData = holder.getMemberData(userId, false);
		if (memberData == null) {
			return;
		}

		// 更新下成员信息
		memberData.setPost(post);
		holder.updateMemberData(memberData.getId());
	}

	/**
	 * 转让帮主
	 * 
	 * @param oldLeaderId 旧帮主的Id
	 * @param newLeaderId 新帮主的Id
	 */
	public synchronized void transferGroupLeader(String oldLeaderId, String newLeaderId) {
		// 旧帮主
		GroupMemberData oldLeader = holder.getMemberData(oldLeaderId, false);
		if (oldLeader == null) {
			return;
		}

		// 更新下成员信息
		oldLeader.setPost(GroupPost.MEMBER_VALUE);
		holder.updateMemberData(oldLeader.getId());

		// 新帮主
		GroupMemberData newLeader = holder.getMemberData(newLeaderId, false);
		if (newLeader == null) {
			return;
		}

		// 更新下成员信息
		newLeader.setPost(GroupPost.LEADER_VALUE);
		holder.updateMemberData(newLeader.getId());
	}

	/**
	 * 更新成员的离线时间
	 * 
	 * @param userId
	 * @param logoutTime
	 */
	public synchronized void updateMemberLogoutTime(String userId, long logoutTime) {
		GroupMemberData memberData = holder.getMemberData(userId, false);
		if (memberData == null) {
			return;
		}

		memberData.setLogoutTime(logoutTime);
		holder.updateMemberData(memberData.getId());
	}

	/**
	 * 移除所有的成员
	 * 
	 * @param applyPlayerTask 移除之后的成员任务
	 * @param memberPlayerTask 正式成员的移除任务
	 */
	public synchronized void removeAllMember(PlayerTask applyPlayerTask, PlayerTask memberPlayerTask) {
		// 删除所有的申请成员信息
		holder.clearAllApplyMemberData(applyPlayerTask);
		// 删除所有的成员信息
		holder.clearAllMemberData(memberPlayerTask);
	}

	/**
	 * 移除帮派的申请成员
	 * 
	 * @param userId
	 * @param playerTask 移除成员的任务
	 * @return
	 */
	public synchronized boolean removeApplyMemberFromDB(String userId, PlayerTask playerTask) {
		GroupMemberData memberData = holder.getMemberData(userId, true);
		if (memberData == null) {
			return false;
		}

		holder.removeMemberData(userId, true);

		// 移除之后的处理
		if (playerTask != null) {
			GameWorldFactory.getGameWorld().asyncExecute(userId, playerTask);
		}
		return true;
	}

	/**
	 * 移除帮派中的所有成员
	 * 
	 * @param call
	 */
	public synchronized void removeAllApplyMemberFromDB(PlayerTask playerTask) {
		holder.clearAllApplyMemberData(playerTask);
	}

	/**
	 * 移除帮派成员
	 * 
	 * @param kickUserId
	 */
	public synchronized void kickMember(String kickUserId) {
		String groupID = GroupHelper.getUserGroupId(kickUserId);
		holder.removeMemberData(kickUserId, false);
		GFOnlineListenerPlayerChange.userLeaveGroupHandler(kickUserId, groupID);
	}

	/**
	 * 更新成员的贡献值
	 * 
	 * @param userId
	 * @param offsetContribution 扣除是负数，增加是正值
	 * @param addDay 是否要增加到今日捐献中
	 */
	public synchronized void updateMemberContribution(String userId, int offsetContribution, boolean addDay) {
		GroupMemberData memberData = holder.getMemberData(userId, false);
		if (memberData == null) {
			return;
		}

		int contribution = memberData.getContribution();
		contribution += offsetContribution;
		contribution = contribution < 0 ? 0 : contribution;
		memberData.setContribution(contribution);
		if (offsetContribution > 0) {
			memberData.setTotalContribution(memberData.getTotalContribution() + offsetContribution);
			if (addDay) {
				memberData.setDayContribution(memberData.getDayContribution() + offsetContribution);
			}
		}
		holder.updateMemberData(memberData.getId());
		Player memberPlayer = PlayerMgr.getInstance().find(userId);
		holder.synMemberData(memberPlayer, false, -1);

		// 通知修改了个人贡献值
		memberPlayer.getUserGroupAttributeDataMgr().updateContribution(memberPlayer, contribution, memberData.getDayContribution());
	}

	/**
	 * 更新成员的名字
	 * 
	 * @param userId
	 * @param name
	 */
	public void updateMemberName(String userId, String name) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setName(name);
		holder.updateMemberData(item.getId());
	}

	/**
	 * 更新成员的等级
	 * 
	 * @param userId
	 * @param level
	 */
	public void updateMemberLevel(String userId, int level) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setLevel((short) level);
		holder.updateMemberData(item.getId());
	}

	/**
	 * 更新成员的头像图标
	 * 
	 * @param userId
	 * @param headIcon
	 */
	public synchronized void updateMemberHeadIcon(String userId, String headIcon) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setHeadId(headIcon);
		holder.updateMemberData(item.getId());
	}

	/**
	 * 更新成员的头像框
	 * 
	 * @param userId
	 * @param headbox
	 */
	public synchronized void updateMemberHeadbox(String userId, String headbox) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setHeadbox(headbox);
		holder.updateMemberData(item.getId());
	}

	/**
	 * 更新成员的Vip等级
	 * 
	 * @param userId
	 * @param vipLevel
	 */
	public void updateMemberVipLevel(String userId, int vipLevel) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setVipLevel((byte) vipLevel);
		holder.updateMemberData(item.getId());
	}

	/**
	 * 更新成员的模版Id
	 * 
	 * @param userId
	 * @param templateId
	 */
	public void updateMemberTemplateId(String userId, String templateId) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setTemplateId(templateId);
		holder.updateMemberData(item.getId());
	}

	/**
	 * 更新帮派成员的捐献时间
	 * 
	 * @param userId
	 * @param donateTimes
	 * @param lastDonateTime
	 */
	public void resetMemberDataDonateTimes(String userId, long lastDonateTime) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setDonateTimes(0);
		item.setLastDonateTime(lastDonateTime);
		item.setDayContribution(0);
		holder.updateMemberData(item.getId());

		Player memberPlayer = PlayerMgr.getInstance().find(userId);
		// 通知修改了个人贡献值
		memberPlayer.getUserGroupAttributeDataMgr().updateContribution(memberPlayer, 0, 0);
	}

	/**
	 * 更新帮派成员的捐献时间
	 * 
	 * @param userId
	 * @param donateTimes
	 * @param lastDonateTime
	 */
	public void gmResetMemberDataDonateTimes(String userId, long lastDonateTime) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setDonateTimes(0);
		item.setLastDonateTime(lastDonateTime);
		holder.updateMemberData(item.getId());

		Player memberPlayer = PlayerMgr.getInstance().find(userId);
		// 通知修改了个人贡献值
		memberPlayer.getUserGroupAttributeDataMgr().updateContribution(memberPlayer, 0, 0);
	}

	/**
	 * 捐献之后更新成员的数据
	 * 
	 * @param userId
	 * @param donateTimes
	 * @param lastDonateTime
	 * @param contribution 增加了多少贡献
	 * @param add 是否添加到当日捐献
	 */
	public void updateMemberDataWhenDonate(String userId, int donateTimes, long lastDonateTime, int contribution, boolean add) {
		GroupMemberData item = holder.getMemberData(userId, false);
		if (item == null) {
			return;
		}

		item.setDonateTimes(donateTimes);
		item.setLastDonateTime(lastDonateTime);
		item.setContribution(item.getContribution() + contribution);
		if (contribution > 0) {
			item.setTotalContribution(item.getTotalContribution() + contribution);
			if (add) {
				item.setDayContribution(item.getDayContribution() + contribution);
			}
		}
		holder.updateMemberData(item.getId());

		Player memberPlayer = PlayerMgr.getInstance().find(userId);
		// 通知修改了个人贡献值
		memberPlayer.getUserGroupAttributeDataMgr().updateContribution(memberPlayer, contribution, item.getDayContribution());
	}

	/**
	 * 是否已经申请了该帮派
	 * 
	 * @param userId
	 * @return
	 */
	public boolean isAlreadyApply(String userId) {
		return holder.isAlreadyApply(userId);
	}

	/**
	 * 获取成员的数量
	 * 
	 * @return
	 */
	public int getGroupMemberSize() {
		return holder.getGroupMemberSize();
	}

	/**
	 * 获取申请成员的数量
	 * 
	 * @return
	 */
	public int getApplyMemberSize() {
		return holder.getApplyMemberSize();
	}

	public void flush() {
		holder.flush();
	}

	/**
	 * 同步成员的数据到客户端
	 * 
	 * @param player 成员信息
	 * @param isApply 是否是申请成员信息
	 * @param version 客户端的版本号
	 */
	public void synMemberData(Player player, boolean isApply, int version) {
		holder.synMemberData(player, isApply, version);
	}

	/**
	 * 新生成一个帮派成员Id
	 * 
	 * @param userId
	 * @param groupId
	 * @return
	 */
	private String newMemberUniqueId(String userId, String groupId) {
		StringBuilder sb = new StringBuilder();
		sb.append(userId).append("_").append(groupId);
		return sb.toString();
	}

	/**
	 * 重置帮派管理员每天分配奖励次数
	 * @param userId
	 * @param maxAllotCount
	 * @param b
	 */
	public void resetAllotGroupRewardCount(String userId, int maxAllotCount,
			boolean b) {
		GroupMemberData data = holder.getMemberData(userId, b);
		data.setAllotRewardCount(maxAllotCount);
		
	}
}