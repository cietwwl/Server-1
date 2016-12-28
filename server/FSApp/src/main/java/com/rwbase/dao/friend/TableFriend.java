package com.rwbase.dao.friend;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.friend.readonly.TableFriendIF;
import com.rwbase.dao.friend.vo.FriendGiveState;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendVo;
import com.rwbase.dao.openLevelTiggerService.pojo.OpenLevelTiggerServiceItem;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_friend")
public class TableFriend implements TableFriendIF {
	@Id
	private String userId;
	private FriendVo friendVo = new FriendVo();
	private ConcurrentHashMap<String, FriendItem> friendList = new ConcurrentHashMap<String, FriendItem>();
	private ConcurrentHashMap<String, FriendItem> requestList = new ConcurrentHashMap<String, FriendItem>();
	private ConcurrentHashMap<String, FriendItem> blackList = new ConcurrentHashMap<String, FriendItem>();
	private ConcurrentHashMap<String, FriendGiveState> friendGiveList = new ConcurrentHashMap<String, FriendGiveState>();
	/**主动搜索到的机器人，可能会很多个，但第一个会自动送体力给用户*/
	private ConcurrentHashMap<String, FriendItem> reCommandfriendList = new ConcurrentHashMap<String, FriendItem>();
	/**被动让系统推过来的机器人，可能有多个，是否自动送体根据表格来*/
	private OpenLevelTiggerServiceItem openLevelTiggerServiceItem = new OpenLevelTiggerServiceItem();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ConcurrentHashMap<String, FriendItem> getFriendList() {
		return friendList;
	}

	public void setFriendList(ConcurrentHashMap<String, FriendItem> friendList) {
		this.friendList = friendList;
	}

	public ConcurrentHashMap<String, FriendItem> getRequestList() {
		return requestList;
	}

	public void setRequestList(ConcurrentHashMap<String, FriendItem> requestList) {
		this.requestList = requestList;
	}

	public Map<String, FriendItem> getBlackList() {
		return blackList;
	}

	public void setBlackList(ConcurrentHashMap<String, FriendItem> blackList) {
		this.blackList = blackList;
	}

	public ConcurrentHashMap<String, FriendGiveState> getFriendGiveList() {
		return friendGiveList;
	}

	public void setFriendGiveList(ConcurrentHashMap<String, FriendGiveState> friendGiveList) {
		this.friendGiveList = friendGiveList;
	}

	@JsonIgnore
	public Enumeration<FriendItem> getFriendMap() {
		return friendList.elements();
	}

	public FriendItem getFriendItem(String key) {
		return friendList.get(key);
	}

	@JsonIgnore
	public Enumeration<FriendItem> getRequestMap() {
		return requestList.elements();
	}

	public FriendItem getRequestItem(String key) {
		return requestList.get(key);
	}

	@JsonIgnore
	public Enumeration<FriendItem> getBlackMap() {
		return blackList.elements();
	}

	public FriendItem getBlackItem(String key) {
		return blackList.get(key);
	}

	@JsonIgnore
	public Enumeration<FriendGiveState> getFriendGiveMap() {
		return friendGiveList.elements();
	}

	public FriendGiveState getFriendGiveState(String key) {
		return friendGiveList.get(key);
	}

	public ConcurrentHashMap<String, FriendItem> getReCommandfriendList() {
		return reCommandfriendList;
	}

	public void setReCommandfriendList(
			ConcurrentHashMap<String, FriendItem> reCommandfriendList) {
		this.reCommandfriendList = reCommandfriendList;
	}

	public boolean resetGiveState() {
		cleanVainGiveData();
		Iterator<FriendGiveState> it = getFriendGiveList().values().iterator();
		boolean changed = false;
		while (it.hasNext()) {
			FriendGiveState giveState = it.next();
			if (!giveState.isGiveState()) {
				giveState.setGiveState(true);
				changed = true;
			}
//			if (giveState.isReceiveState()) {
//				giveState.setReceiveState(false);
//				changed = true;
//			}
		}
		return changed;
	}

	/** 重置数据时清理无效赠送数据 */
	private void cleanVainGiveData() {
		Iterator<FriendGiveState> it = getFriendGiveList().values().iterator();
		while (it.hasNext()) {
			FriendGiveState giveState = it.next();
			if (!friendList.containsKey(giveState.getUserId())) {
				it.remove();
			}
		}
	}

	public FriendVo getFriendVo() {
		return friendVo;
	}

	public void setFriendVo(FriendVo friendVo) {
		this.friendVo = friendVo;
	}

	/**
	 * 从请求列表移除
	 * 
	 * @param userId
	 * @return
	 */
	public boolean removeFromRequest(String userId) {
		return this.requestList.remove(userId) != null;
	}

	/**
	 * 从黑名单移除
	 * 
	 * @param userId
	 * @return
	 */
	public boolean removeFromBlackList(String userId) {
		return blackList.remove(userId) != null;
	}

	/**
	 * 从好友列表移除
	 * 
	 * @param userId
	 * @return
	 */
	public boolean removeFromFriendList(String userId) {
		return this.friendList.remove(userId) != null;
	}

	public OpenLevelTiggerServiceItem getOpenLevelTiggerServiceItem() {
		return openLevelTiggerServiceItem;
	}

	public void setOpenLevelTiggerServiceItem(
			OpenLevelTiggerServiceItem openLevelTiggerServiceItem) {
		this.openLevelTiggerServiceItem = openLevelTiggerServiceItem;
	}	
}