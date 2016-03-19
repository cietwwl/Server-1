package com.rwbase.dao.friend;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.friend.readonly.TableFriendIF;
import com.rwbase.dao.friend.vo.FriendGiveState;
import com.rwbase.dao.friend.vo.FriendItem;
import com.rwbase.dao.friend.vo.FriendVo;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, FriendItem> getFriendList() {
		return friendList;
	}

	public void setFriendList(ConcurrentHashMap<String, FriendItem> friendList) {
		this.friendList = friendList;
	}

	public Map<String, FriendItem> getRequestList() {
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

	public Map<String, FriendGiveState> getFriendGiveList() {
		return friendGiveList;
	}

	public void setFriendGiveList(ConcurrentHashMap<String, FriendGiveState> friendGiveList) {
		this.friendGiveList = friendGiveList;
	}

	public Enumeration<FriendItem> getFriendMap() {
		return friendList.elements();
	}

	public FriendItem getFriendItem(String key) {
		return friendList.get(key);
	}

	public Enumeration<FriendItem> getRequestMap() {
		return requestList.elements();
	}

	public FriendItem getRequestItem(String key) {
		return requestList.get(key);
	}

	public Enumeration<FriendItem> getBlackMap() {
		return blackList.elements();
	}

	public FriendItem getBlackItem(String key) {
		return blackList.get(key);
	}

	public Enumeration<FriendGiveState> getFriendGiveMap() {
		return friendGiveList.elements();
	}

	public FriendGiveState getFriendGiveState(String key) {
		return friendGiveList.get(key);
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
			if (giveState.isReceiveState()) {
				giveState.setReceiveState(false);
				changed = true;
			}
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
}