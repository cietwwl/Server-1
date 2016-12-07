package com.rw.service.group.helper;

import java.util.Comparator;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.GroupCommonProto.GroupPost;

/*
 * @author HC
 * @date 2016年3月5日 上午10:26:18
 * @Description 帮派成员管理Helper
 */
public class GroupMemberHelper {
	/**
	 * 退出或者踢出帮派的任务
	 */
	public static final PlayerTask quitGroupTask = new PlayerTask() {

		@Override
		public void run(Player player) {
			player.getUserGroupAttributeDataMgr().updateDataWhenQuitGroup(player, System.currentTimeMillis());
		}
	};

	/**
	 * 优先转让给帮主的成员比较器
	 */
	public static final Comparator<GroupMemberDataIF> transferLeaderComparator = new Comparator<GroupMemberDataIF>() {

		@Override
		public int compare(GroupMemberDataIF o1, GroupMemberDataIF o2) {
			int post1 = o1.getPost();
			int post2 = o2.getPost();

			// 是帮主的情况下就直接放后
			if (post1 == GroupPost.LEADER_VALUE) {// 1是帮主
				return 1;
			}

			if (post2 == GroupPost.LEADER_VALUE) {// 2是帮主
				return 1;
			}

			// 职位比较，职位值越小官职越大，位置越靠前
			if (post1 < post2) {
				return -1;
			} else if (post1 > post2) {
				return 1;
			}

			// 贡献比较
			int c1 = o1.getContribution();
			int c2 = o2.getContribution();
			if (c1 > c2) {
				return -1;
			} else if (c1 < c2) {
				return 1;
			}

			// 加入帮派时间比较
			long r1 = o1.getReceiveTime();
			long r2 = o2.getReceiveTime();
			if (r1 < r2) {
				return -1;
			} else if (r1 > r2) {
				return 1;
			}

			return 0;
		}
	};

	/**
	 * <pre>
	 * 排列请求队列比较器
	 * 这个是按照成员的申请时间，时间晚的靠后，早的靠前
	 * </pre>
	 */
	public static final Comparator<GroupMemberDataIF> applyMemberComparator = new Comparator<GroupMemberDataIF>() {

		@Override
		public int compare(GroupMemberDataIF o1, GroupMemberDataIF o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}

			long result = o1.getApplyTime() - o2.getApplyTime();
			return result == 0 ? 0 : (result > 0 ? -1 : 1);
		}
	};

	/**
	 * <pre>
	 * 成员排序
	 * 这个按照，职位>个人历史总贡献
	 * </pre>
	 */
	public static final Comparator<GroupMemberDataIF> memberComparator = new Comparator<GroupMemberDataIF>() {

		@Override
		public int compare(GroupMemberDataIF o1, GroupMemberDataIF o2) {
			int post1 = o1.getPost();
			int post2 = o2.getPost();
			int result = post1 - post2;
			if (result != 0) {
				return result;
			}

			int totalContribution1 = o1.getTotalContribution();
			int totalContribution2 = o2.getTotalContribution();
			return totalContribution2 - totalContribution1;
		}
	};

	private static GroupMemberHelper instance = new GroupMemberHelper();

	public static GroupMemberHelper getInstance() {
		return instance;
	}

	protected GroupMemberHelper() {
	}

	/**
	 * 当角色上线的时候检查帮派相关数据
	 * 
	 * @param player
	 */
	public void onPlayerLogin(Player player) {
		updateMemberData(player, true);
	}

	/**
	 * 当角色离线时更新离线数据
	 * 
	 * @param player
	 */
	public void onPlayerLogout(Player player) {
		updateMemberData(player, false);
	}

	/**
	 * 上下线时更新帮派成员的属性&通知相应的排行榜进行数据处理
	 * 
	 * @param player
	 * @param isLogin
	 */
	public void updateMemberData(Player player, boolean isLogin) {
		String playerId = player.getUserId();
		// 角色是否有帮派数据
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();

		if (baseData == null) {
			return;
		}
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {// 没有帮派
			return;
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			return;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(playerId, false);
		if (memberData == null) {
			return;
		}

		if (isLogin) {
			memberMgr.updateMemberLogoutTime(playerId, 0);
		} else {
			memberMgr.updateMemberLogoutTime(playerId, System.currentTimeMillis());
		}

		// 如果是帮主，就通知各种排行榜更新下数据
		int post = memberData.getPost();
		if (post == GroupPost.LEADER_VALUE) {// 帮主
			// 三个榜都要更新
			GroupRankHelper.getInstance().updateTheTypeForGroupRankExtension(groupId);
		}
	}

	/**
	 * 获取个人所在的帮派名字
	 * 
	 * @param player
	 * @return
	 */
	public String getGroupName(Player player) {
		UserGroupAttributeDataIF groupBaseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		if (groupBaseData == null) {
			return "";
		}

		String groupId = groupBaseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return "";
		}

		String groupName = groupBaseData.getGroupName();
		if (!StringUtils.isEmpty(groupName)) {// 内存命中
			return groupName;
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			return "";
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return "";
		}

		return groupData.getGroupName();
	}
}