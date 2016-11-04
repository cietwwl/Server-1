package com.rw.handler.groupFight.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

/**
 * 帮战奖励类
 * @author aken
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFFinalRewardItem implements SynItem{
	private String rewardID;  // rewardID = resourceID_userID_rewardType

	private String rewardOwner;	// rewardOwner = resourceID_userID
	
	private int resourceID;	//奖励所属于的资源点
	
	private String userID;	//奖励所属的角色
	
	private int rewardType;  //奖励的类型
	
	private List<ItemInfo> rewardContent;	//奖励的具体内容
	
	private String rewardDesc;	//奖励的描述
	
	private String emailIconPath;	//邮件图标路径
	
	private long rewardGetTime;	//获取该奖励的时间

	@Override
	public String getId() {
		return rewardID;
	}

	public String getRewardID() {
		return rewardID;
	}

	public void setRewardID(String rewardID) {
		this.rewardID = rewardID;
	}

	public String getRewardOwner() {
		return rewardOwner;
	}

	public void setRewardOwner(String rewardOwner) {
		this.rewardOwner = rewardOwner;
	}

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getRewardType() {
		return rewardType;
	}

	public void setRewardType(int rewardType) {
		this.rewardType = rewardType;
	}

	public List<ItemInfo> getRewardContent() {
		return rewardContent;
	}

	public void setRewardContent(List<ItemInfo> rewardContent) {
		this.rewardContent = rewardContent;
	}
	
	public String getEmailIconPath() {
		return emailIconPath;
	}

	public void setEmailIconPath(String emailIconPath) {
		this.emailIconPath = emailIconPath;
	}

	public long getRewardGetTime() {
		return rewardGetTime;
	}

	public void setRewardGetTime(long rewardGetTime) {
		this.rewardGetTime = rewardGetTime;
	}

	public String getRewardDesc() {
		return rewardDesc;
	}

	public void setRewardDesc(String rewardDesc) {
		this.rewardDesc = rewardDesc;
	}
}
