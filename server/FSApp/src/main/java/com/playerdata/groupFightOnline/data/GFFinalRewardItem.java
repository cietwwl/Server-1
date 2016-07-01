package com.playerdata.groupFightOnline.data;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwbase.dao.copy.pojo.ItemInfo;

/**
 * 帮战奖励类
 * @author aken
 */
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gf_final_reward_item")
public class GFFinalRewardItem implements IMapItem{
	@Id
	private String rewardID;  // rewardID = resourceID_userID_rewardType

	private String rewardOwner;	// rewardOwner = resourceID_userID
	
	@CombineSave
	private int resourceID;	//奖励所属于的资源点
	
	@CombineSave
	private String userID;	//奖励所属的角色
	
	@CombineSave
	private int rewardType;  //奖励的类型
	
	@CombineSave
	private List<ItemInfo> rewardContent;	//奖励的具体类容
	
	@CombineSave
	@IgnoreSynField
	private int emailId;	//到期对应的邮件id
	
	@CombineSave
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

	public int getEmailId() {
		return emailId;
	}

	public void setEmailId(int emailId) {
		this.emailId = emailId;
	}

	public long getRewardGetTime() {
		return rewardGetTime;
	}

	public void setRewardGetTime(long rewardGetTime) {
		this.rewardGetTime = rewardGetTime;
	}
}
