package com.rwbase.dao.friend.vo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.FriendItemIF;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.group.helper.GroupMemberHelper;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwbase.dao.user.readonly.TableUserOtherIF;

@JsonIgnoreProperties(ignoreUnknown = true)
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
	private int vip;
	private int sex;
	private long createTime;

	public static FriendItem newInstance(String userId) {
		Player player = PlayerMgr.getInstance().find(userId);
		if (player == null) {
			return null;
		}

		TableUserIF tableUser = player.getTableUser();
		if (tableUser == null) {
			return null;
		}

		Hero mainRoleHero = player.getMainRoleHero();
		if (mainRoleHero == null) {
			return null;
		}

		TableUserOtherIF tableUserOther = player.getTableUserOther();
		if (tableUserOther == null) {
			return null;
		}

		FriendItem newItem = new FriendItem();
		newItem.setUserId(tableUser.getUserId());
		newItem.setUserName(tableUser.getUserName());
		newItem.setLevel(tableUser.getLevel());
		newItem.setUserHead(tableUser.getHeadImageWithDefault());
		newItem.setCareer(mainRoleHero.getCareerType());
		newItem.setLastLoginTime(tableUser.getLastLoginTime());
		newItem.setHeadFrame(player.getUserGameDataMgr().getHeadBox());
		// TODO 帮派获取名字后再提供
		newItem.setUnionName(GroupMemberHelper.getGroupName(player));
		newItem.setFighting(player.getHeroMgr().getFightingAll(player));
		newItem.setVip(player.getVip());
		newItem.setSex(player.getSex());
		newItem.createTime = DateUtils.getSecondLevelMillis();
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

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public long getCreateTime() {
		return createTime;
	}
}
