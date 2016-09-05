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

	private String userId;	//奖励所属的角色
	
	@CombineSave
	private String rewardOwner;	// rewardOwner = resourceID_userID
	
	@CombineSave
	private int resourceID;	//奖励所属于的资源点
	
	@CombineSave
	private int rewardType;  //奖励的类型
	
	@CombineSave
	private List<ItemInfo> rewardContent;	//奖励的具体内容
	
	@CombineSave
	private String rewardDesc;	//奖励的描述
	
	@CombineSave
	private String emailIconPath;	//邮件图标路径
	
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
		return userId;
	}

	public void setUserID(String userID) {
		this.userId = userID;
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
