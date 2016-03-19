package com.rwbase.dao.setting.pojo;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.setting.HeadTypeList;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_setting")
@SynClass
public class TableSettingData 
{
	@Id
	private String userID;							//用户ID
	private long lastRenameTimeInMill;				//上次重命名时间
	
	private List<HeadTypeList> ownHeadPic;	//类型_数值$类型_数值
	private List<HeadTypeList> ownHeadBox;	//类型_数值
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public long getLastRenameTimeInMill() {
		return lastRenameTimeInMill;
	}

	public void setLastRenameTimeInMill(long lastRenameTimeInMill) {
		this.lastRenameTimeInMill = lastRenameTimeInMill;
	}

	public List<HeadTypeList> getOwnHeadPic() {
		return ownHeadPic;
	}

	public void setOwnHeadPic(List<HeadTypeList> ownHeadPic) {
		this.ownHeadPic = ownHeadPic;
	}

	public List<HeadTypeList> getOwnHeadBox() {
		return ownHeadBox;
	}

	public void setOwnHeadBox(List<HeadTypeList> ownHeadBox) {
		this.ownHeadBox = ownHeadBox;
	}


	
	
	
}
