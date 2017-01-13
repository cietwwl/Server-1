package com.rwbase.dao.group.pojo.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.bm.GFOnlineListenerPlayerChange;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.GroupCommonProto.GroupPost;

/*
 * @author HC
 * @date 2016年1月18日 下午2:31:11
 * @Description 帮派成员的Holder管理
 */
public class GroupMemberDataHolder {

	/** 同步数据的类型 */
	private eSynType memberSynType = eSynType.GroupMemberData;// 正式成员
	private eSynType applyMemberSynType = eSynType.GroupApplyMemberData;// 申请成员

	/** 成员数据的版本号 */
	private AtomicInteger memberVersion = new AtomicInteger(1);// 正式成员版本号
	private AtomicInteger applyMemberVersion = new AtomicInteger(1);// 申请成员版本号

	/** 帮派成员管理的存储 */
	// private MapItemStore<GroupMemberData> mapItemStore;
	private ConcurrentHashMap<String, String> memberIdMap;// 成员的Id列表
	private ConcurrentHashMap<String, String> applyMemberIdMap;// 申请成员的信息
	private String groupId;

	public GroupMemberDataHolder(String groupId) {
		// mapItemStore = new MapItemStore<GroupMemberData>("groupId", groupId, GroupMemberData.class);
		this.groupId = groupId;
		memberIdMap = new ConcurrentHashMap<String, String>();
		applyMemberIdMap = new ConcurrentHashMap<String, String>();

		// 获取帮派的基础配置
		GroupBaseConfigTemplate baseConfig = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		final int maxApplySize = baseConfig.getGroupApplyMemberSize();
		MapItemStore<GroupMemberData> groupMemberStore = getGroupMemberStore();
		if (groupMemberStore.getSize() <= 0) {// 没有任何成员
			return;
		}

		// 成员信息处理
		Enumeration<GroupMemberData> memberEnum = groupMemberStore.getEnum();// 帮派中所有成员的信息

		// 成员信息
		List<GroupMemberData> applyList = new ArrayList<GroupMemberData>(maxApplySize);// 申请的成员列表
		while (memberEnum.hasMoreElements()) {
			GroupMemberData member = memberEnum.nextElement();
			if (member.getReceiveTime() != 0) {// 有被通过
				memberIdMap.put(member.getUserId(), member.getId());
				continue;
			}

			applyList.add(member);// 临时记录还没被通过的人
		}

		Collections.sort(applyList, GroupMemberHelper.applyMemberComparator);// 排序

		// 初始化帮派成员
		int size = applyList.size();
		for (int i = size - 1; i >= 0; --i) {
			GroupMemberData memberData = applyList.get(i);
			applyMemberIdMap.put(memberData.getUserId(), memberData.getId());
		}
	}

	/**
	 * 获取帮派成员信息
	 * 
	 * @param userId
	 * @param isApply
	 * @return
	 */
	public GroupMemberData getMemberData(String userId, boolean isApply) {
		String memberId = null;
		if (isApply) {
			memberId = applyMemberIdMap.get(userId);
		} else {
			memberId = memberIdMap.get(userId);
		}

		if (memberId == null) {
			return null;
		}

		return getGroupMemberStore().getItem(memberId);
	}

	/**
	 * 获取帮派申请的成员信息列表
	 * 
	 * @param comparator 获取出来成员的排序规则<b>默认是按照申请时间晚的排前。是此需求就传入<i>null</i></b>
	 * @return 返回一个默认排序规则，或者自定义规则的申请成员信息列表
	 */
	public List<GroupMemberData> getApplyMemberSortList(Comparator<GroupMemberDataIF> comparator) {
		if (applyMemberIdMap == null || applyMemberIdMap.isEmpty()) {
			return Collections.emptyList();
		}

		int size = applyMemberIdMap.size();
		MapItemStore<GroupMemberData> mapItemStore = getGroupMemberStore();
		List<GroupMemberData> memberList = new ArrayList<GroupMemberData>(size);
		for (Entry<String, String> e : applyMemberIdMap.entrySet()) {
			String memberId = e.getValue();
			GroupMemberData item = mapItemStore.getItem(memberId);
			if (item == null) {
				continue;
			}

			memberList.add(item);
		}

		if (comparator != null) {
			Collections.sort(memberList, comparator);
		}

		return memberList;
	}

	/**
	 * 获取成员的列表
	 * 
	 * @param comparator
	 * @return
	 */
	public List<GroupMemberData> getMemberSortList(Comparator<GroupMemberDataIF> comparator) {
		if (memberIdMap == null || memberIdMap.isEmpty()) {
			return Collections.emptyList();
		}

		int size = memberIdMap.size();
		MapItemStore<GroupMemberData> mapItemStore = getGroupMemberStore();
		List<GroupMemberData> memberList = new ArrayList<GroupMemberData>(size);
		for (Entry<String, String> e : memberIdMap.entrySet()) {
			String memberId = e.getValue();
			GroupMemberData item = mapItemStore.getItem(memberId);
			if (item == null) {
				continue;
			}

			memberList.add(item);
		}

		if (comparator != null) {
			Collections.sort(memberList, comparator);
		}

		return memberList;
	}

	/**
	 * 增加帮派成员，申请或者正式成员
	 * 
	 * @param userId
	 * @param memberData
	 * @param isAddApply
	 * @return
	 */
	public GroupMemberData addMember(String userId, GroupMemberData memberData, boolean isAddApply) {
		if (isAddApply) {
			if (applyMemberIdMap.containsKey(userId)) {
				return null;
			}

			// 添加到请求列表
			applyMemberIdMap.put(userId, memberData.getId());
			// 增加一个版本号
			applyMemberVersion.incrementAndGet();
		} else {
			if (memberIdMap.containsKey(userId)) {
				return null;
			}

			// 添加到请求列表
			memberIdMap.put(userId, memberData.getId());
			// 增加一个版本号
			memberVersion.incrementAndGet();
		}

		// 放入到数据库
		getGroupMemberStore().addItem(memberData);
		return memberData;
	}

	/**
	 * 获取帮派成员
	 * 
	 * @return
	 */
	public GroupMemberData getGroupLeader() {
		if (memberIdMap == null || memberIdMap.isEmpty()) {
			return null;
		}

		MapItemStore<GroupMemberData> mapItemStore = getGroupMemberStore();
		for (Entry<String, String> e : memberIdMap.entrySet()) {
			String memberId = e.getValue();
			GroupMemberData item = mapItemStore.getItem(memberId);
			if (item == null) {
				continue;
			}

			if (item.getPost() == GroupPost.LEADER_VALUE) {// 职位是帮主
				return item;
			}
		}

		return null;
	}

	/**
	 * 获取某个官职在帮派中对应的数量
	 * 
	 * @param post
	 * @return
	 */
	public int getPostMemberSize(int post) {
		if (memberIdMap == null) {
			return 0;
		}

		MapItemStore<GroupMemberData> mapItemStore = getGroupMemberStore();
		int postNum = 0;
		for (Entry<String, String> e : memberIdMap.entrySet()) {
			String memberId = e.getValue();
			GroupMemberData item = mapItemStore.getItem(memberId);
			if (item == null) {
				continue;
			}

			if (item.getPost() == post) {
				postNum++;
			}
		}

		return postNum;
	}

	/**
	 * 删除成员Id
	 * 
	 * @param userId
	 */
	public void removeApplyMemberDataId(String userId) {
		applyMemberIdMap.remove(userId);
		// 增加一个版本号
		applyMemberVersion.incrementAndGet();
	}

	/**
	 * 删除申请成员
	 * 
	 * @param userId
	 * @param isApply
	 */
	public void removeMemberData(String userId, boolean isApply) {
		String memberId = null;
		if (isApply) {
			memberId = applyMemberIdMap.remove(userId);
			// 增加一个版本号
			applyMemberVersion.incrementAndGet();
		} else {
			memberId = memberIdMap.remove(userId);
			// 增加一个版本号
			memberVersion.incrementAndGet();
		}

		if (memberId == null) {
			return;
		}

		getGroupMemberStore().removeItem(memberId);
	}

	/**
	 * 清除所有正式成员信息
	 * 
	 * @param playerTask
	 */
	public void clearAllMemberData(PlayerTask playerTask) {
		MapItemStore<GroupMemberData> mapItemStore = getGroupMemberStore();
		// 删除所有的成员信息
		for (Entry<String, String> e : memberIdMap.entrySet()) {
			mapItemStore.removeItem(e.getValue());
			GFOnlineListenerPlayerChange.userLeaveGroupHandler(e.getValue(), groupId);
			// 处理下个人的数据
			if (playerTask != null) {
				GameWorldFactory.getGameWorld().asyncExecute(e.getKey(), playerTask);
			}
		}

		memberIdMap.clear();

		// 增加一个版本号
		memberVersion.incrementAndGet();
	}

	/**
	 * 清除所有的申请成员信息
	 * 
	 * @param playerTask
	 */
	public void clearAllApplyMemberData(PlayerTask playerTask) {
		MapItemStore<GroupMemberData> mapItemStore = getGroupMemberStore();
		for (Entry<String, String> e : applyMemberIdMap.entrySet()) {
			mapItemStore.removeItem(e.getValue());

			if (playerTask != null) {
				GameWorldFactory.getGameWorld().asyncExecute(e.getKey(), playerTask);
			}
		}

		applyMemberIdMap.clear();

		// 增加一个版本号
		applyMemberVersion.incrementAndGet();
	}

	/**
	 * 增加一个新的成员Id到缓存
	 * 
	 * @param userId
	 * @param memberId
	 */
	public void putNewMemberId(String userId, String memberId) {
		memberIdMap.put(userId, memberId);
		// 增加一个版本号
		memberVersion.incrementAndGet();
	}

	/**
	 * 更新成员数据
	 * 
	 * @param memberId
	 */
	public void updateMemberData(String memberId) {
		getGroupMemberStore().update(memberId);
		// 增加一个版本号
		memberVersion.incrementAndGet();
	}

	/**
	 * 是否已经申请了该帮派
	 * 
	 * @param userId
	 * @return
	 */
	public boolean isAlreadyApply(String userId) {
		if (applyMemberIdMap == null || applyMemberIdMap.isEmpty()) {
			return false;
		}

		return applyMemberIdMap.containsKey(userId);
	}

	/**
	 * 获取成员的数量
	 * 
	 * @return
	 */
	public int getGroupMemberSize() {
		return memberIdMap.size();
	}

	/**
	 * 获取申请成员的数量
	 * 
	 * @return
	 */
	public int getApplyMemberSize() {
		return applyMemberIdMap.size();
	}

	/**
	 * 刷新成员信息
	 */
	public void flush() {
		getGroupMemberStore().flush();
	}

	/**
	 * 同步成员信息
	 * 
	 * @param player 角色
	 * @param isApply 是否申请成员信息
	 * @param version 客户端版本号
	 */
	public void synMemberData(Player player, boolean isApply, int version) {
		if (isApply) {// 发送申请成员的信息
			int newVersion = applyMemberVersion.get();
			if (newVersion != version) {
				ClientDataSynMgr.synDataList(player, getApplyMemberSortList(GroupMemberHelper.applyMemberComparator), applyMemberSynType, eSynOpType.UPDATE_LIST, newVersion);
			}
		} else {// 正式成员
			int newVersion = memberVersion.get();
			if (newVersion != version) {
				ClientDataSynMgr.synDataList(player, getMemberSortList(GroupMemberHelper.memberComparator), memberSynType, eSynOpType.UPDATE_LIST);
			}
		}
	}

	/**
	 * 获取帮派成员的Cache
	 * 
	 * @return
	 */
	private MapItemStore<GroupMemberData> getGroupMemberStore() {
		MapItemStoreCache<GroupMemberData> cache = MapItemStoreFactory.getGroupMemberCache();
		return cache.getMapItemStore(groupId, GroupMemberData.class);
	}
}