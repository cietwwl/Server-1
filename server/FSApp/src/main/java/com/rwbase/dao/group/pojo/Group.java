package com.rwbase.dao.group.pojo;

import java.util.concurrent.TimeUnit;

import com.bm.group.GroupBaseDataMgr;
import com.bm.group.GroupLogMgr;
import com.bm.group.GroupMemberMgr;
import com.groupCopy.bm.groupCopy.GroupCopyMgr;
import com.playerdata.Player;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

/*
 * @author HC
 * @date 2016年1月19日 下午2:27:29
 * @Description 内存中使用的帮派的信息
 */
public final class Group {
	private final GroupMemberMgr groupMemberMgr;// 帮派成员的Mgr
	private final GroupBaseDataMgr groupBaseDataMgr;// 帮派基础属性Mgr
	private final GroupLogMgr groupLogMgr;// 帮派日志的Mgr
	private final GroupCopyMgr groupCopyMgr;// 帮派副本Mgr

	public Group(String groupId) {
		this.groupMemberMgr = new GroupMemberMgr(groupId);
		this.groupBaseDataMgr = new GroupBaseDataMgr(groupId);
		this.groupLogMgr = new GroupLogMgr(groupId);
		this.groupCopyMgr = new GroupCopyMgr(groupId);
	}

	/**
	 * 获取帮派的基础属性，可以直接操纵Mgr去更新数据到前端，更新
	 *
	 * @return
	 */
	public GroupBaseDataMgr getGroupBaseDataMgr() {
		return groupBaseDataMgr;
	}

	/**
	 * 获取帮派的成员Mgr
	 * 
	 * @return
	 */
	public GroupMemberMgr getGroupMemberMgr() {
		return groupMemberMgr;
	}

	/**
	 * 获取帮派日志的Mgr
	 * 
	 * @return
	 */
	public GroupLogMgr getGroupLogMgr() {
		return groupLogMgr;
	}	
	

	public GroupCopyMgr getGroupCopyMgr() {
		return groupCopyMgr;
	}

	/**
	 * <pre>
	 * 检查帮派帮主离线时间有没有超过限定转让天数
	 * 如果还没超过就在内存里缓存一下下一次检查的时间
	 * 这个就依赖第一个来检查的人的时间了
	 * 另外如果帮主的离线时间是0，就说明现在还是在线的，不需要检查了
	 * </pre>
	 * 
	 * @param group
	 */
	public void checkGroupLeaderLogoutTime() {
		// 获取配置表
		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			return;
		}

		if (groupBaseDataMgr.getGroupData() == null) {// 没有帮派
			return;
		}

		// 检测版本数据
		long lastCheckTime = groupBaseDataMgr.getLastCheckTime();
		long distanceTransferTime = groupBaseDataMgr.getDistanceTransferTime();
		long now = System.currentTimeMillis();

		// 如果当前时间距离上一次检查还没有达到帮主转换的点不进行检查
		if (lastCheckTime > 0 && now - lastCheckTime < distanceTransferTime) {
			return;
		}

		// 获取帮主
		GroupMemberDataIF groupLeader = groupMemberMgr.getGroupLeader();
		if (groupLeader == null) {
			return;
		}

		long logoutTime = groupLeader.getLogoutTime();
		if (logoutTime <= 0) {// 在线
			return;
		}

		long delayTimeMillis = TimeUnit.DAYS.toMillis(gbct.getAutoTransGroupLeaderDay());
		if (now - logoutTime >= delayTimeMillis) {// 超过限定转让帮主的离线时间
			String canTransferLeaderMemberId = groupMemberMgr.getCanTransferLeaderMemberId(GroupMemberHelper.transferLeaderComparator);
			if (canTransferLeaderMemberId == null) {
				return;
			}

			if (canTransferLeaderMemberId.equals(groupLeader.getUserId())) {// 是同一个人
				return;
			}

			// 把帮派帮主修改给排行第一的成员
			groupMemberMgr.transferGroupLeader(groupLeader.getUserId(), canTransferLeaderMemberId);
			// 检查帮派成员的
			groupBaseDataMgr.updateCheckTransferLeaderTime(now, delayTimeMillis);
		} else {// 没有超越限定离线时间
			groupBaseDataMgr.updateCheckTransferLeaderTime(now, delayTimeMillis - (now - logoutTime));
		}
	}

	public void flush() {
		groupBaseDataMgr.flush();
		groupMemberMgr.flush();
		groupLogMgr.flush();
		
	}

	/**
	 * 推送下帮派的数据，帮派基础数据和帮派的正式成员
	 * 
	 * @param player
	 */
	public void synGroupDataAndMemberData(Player player) {
		synGroupData(player, -1);
		synGroupMemberData(player, false, -1);
	}

	/**
	 * 推送帮派基础数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synGroupData(Player player, int version) {
		groupBaseDataMgr.synGroupData(player, version);
	}

	/**
	 * 推送帮派的成员信息
	 * 
	 * @param player
	 * @param isApply
	 * @param version
	 */
	public void synGroupMemberData(Player player, boolean isApply, int version) {
		groupMemberMgr.synMemberData(player, isApply, version);
	}

	/**
	 * 推送帮派研究技能数据
	 * 
	 * @param player
	 * @param version
	 */
	public void synGroupSkillData(Player player, int version) {
		groupBaseDataMgr.synGroupSkillData(player, version);
	}
	
	public void synGroupMapData(Player player, int version) {
		groupCopyMgr.synMapData(player, version);
	}

	public void synGroupLevelData(Player player, int version) {
		groupCopyMgr.synLevelData(player, version);
	}
}