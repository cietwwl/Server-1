package com.rwbase.dao.friend.vo;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.FriendItemIF;
import com.playerdata.readonly.HeroIF;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwbase.dao.user.readonly.TableUserOtherIF;

public class FriendItem implements FriendItemIF {
	private String userId;
	private String userName;
	private String headImage;
	private int level;
	private int career;
	private String unionName;
	private long lastLoginTime;
	private String headFrame;
	private int fighting;

	public static FriendItem newInstance(String userId) {
		Player player = PlayerMgr.getInstance().find(userId);
		FriendItem newItem = new FriendItem();
		if (player == null) {
			return newItem;
		}

		TableUserIF tableUser = player.getTableUser();
		if (tableUser == null) {
			return newItem;
		}

		HeroIF mainRoleHero = player.getMainRoleHero();
		if (mainRoleHero == null) {
			return newItem;
		}

		TableUserOtherIF tableUserOther = player.getTableUserOther();
		if (tableUserOther == null) {
			return newItem;
		}

		newItem.setUserId(tableUser.getUserId());
		newItem.setUserName(tableUser.getUserName());
		newItem.setLevel(tableUser.getLevel());
		newItem.setUserHead(tableUser.getHeadImageWithDefault());
		newItem.setCareer(mainRoleHero.getCareer());
		newItem.setLastLoginTime(tableUserOther.getLastLoginTime());
		newItem.setHeadFrame(player.getUserGameDataMgr().getHeadBox());
		//TODO 帮派获取名字后再提供
		newItem.setUnionName(GroupMemberHelper.getGroupName(player));
		newItem.setFighting(player.getHeroMgr().getFightingAll());
		return newItem;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserHead() {
		return headImage;
	}

	public void setUserHead(String headImage) {
		this.headImage = headImage;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getUnionName() {
		return unionName == null ? "" : unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public String getHeadFrame() {
		return headFrame;
	}

	public void setHeadFrame(String headFrame) {
		this.headFrame = headFrame;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

}
