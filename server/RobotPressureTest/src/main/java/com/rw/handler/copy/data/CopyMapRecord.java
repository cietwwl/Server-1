package com.rw.handler.copy.data;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

/**
 * 
 * @author 地图记录
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class CopyMapRecord implements SynItem{
	private String id;  //地图记录唯一id
	private int mapId; //地图id
	private String userId; // 用户ID
	
	/*
	 * 获取当前用户的副本地图的星级奖励记录,以"0,0,0"记录下"是否领取1,是否领取2,是否领取3"，
	 */
	private String giftStates; 
	
	//-----------------------   新增关卡宝箱及天尊锦囊奖励记录   --by Alex  8.30.2016  -------------------------------------------//

	private boolean receiveChapterReward = false;
	
	private List<Integer> rewardLvList = new ArrayList<Integer>();
	
	@Override
	public String getId() {
		return id;
	}

	public int getMapId() {
		return mapId;
	}

	public String getUserId() {
		return userId;
	}

	public String getGiftStates() {
		return giftStates;
	}

	public boolean isReceiveChapterReward() {
		return receiveChapterReward;
	}

	public List<Integer> getRewardLvList() {
		return rewardLvList;
	}
}
