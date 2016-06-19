package com.rwbase.dao.chat.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/*
 * @author HC
 * @date 2016年6月27日 下午5:12:12
 * @Description 聊天人的基础信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatUserInfo {
	private String userId;// 角色Id
	private String userName;// 角色名字
	private String headImage;// 头像
	private int level;// 角色等级
	private String groupId;// 帮会Id
	private String groupName;// 帮会名字
	private String headbox;// 头像品质框

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setHeadbox(String headbox) {
		this.headbox = headbox;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getHeadImage() {
		return headImage;
	}

	public int getLevel() {
		return level;
	}

	public String getHeadbox() {
		return headbox;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}
}