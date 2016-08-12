package com.playerdata.groupFightOnline.dataForClient;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 帮战结束时的各类奖励信息
 * @author aken
 */
@SynClass
public class GFRewardInfo {
	private int typeID;		//奖励的类型ID
	
	private int rewardID;	//奖励的ID

	public int getTypeID() {
		return typeID;
	}

	public void setTypeID(int typeID) {
		this.typeID = typeID;
	}

	public int getRewardID() {
		return rewardID;
	}

	public void setRewardID(int rewardID) {
		this.rewardID = rewardID;
	}
}
