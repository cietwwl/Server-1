package com.rwbase.dao.copy.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


/**
 * 
 * @author 地图记录
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "copy_map_record")
@SynClass
public class CopyMapRecord implements IMapItem
{
	@Id
	private String id;  //地图记录唯一id
	private int mapId; //地图id
	private String userId; // 用户ID
	
	/*
	 * 获取当前用户的副本地图的星级奖励记录,以"0,0,0"记录下"是否领取1,是否领取2,是否领取3"，
	 */
	private String giftStates; 
	
	
	//-----------------------   新增关卡宝箱及天尊锦囊奖励记录   --by Alex  8.30.2016  -------------------------------------------//

	//是否已经领取天尊锦囊奖励
	@CombineSave
	private boolean receiveChapterReward = false;
	
	
	//已经领取宝箱奖励对应的的关卡ID
	@CombineSave
	private List<Integer> rewardLvList = new ArrayList<Integer>();
	
	
	
	
	@Override
	public String getId() {
		return id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isGiftCanTake(int index){
		return MapGiftHelper.getInstance().isGiftCanTake(giftStates, index);
	}

	public String getGiftStates() {
		return giftStates;
	}
	
	public void setGiftStates(String giftStates) {
		this.giftStates = giftStates;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
	public boolean takeGift(int index){
		boolean success = false;
		if(isGiftCanTake(index)){
			giftStates = MapGiftHelper.getInstance().takeGift(giftStates, index);
			success = true;
		}
		return success;
	}
	
	public String toClientData() {
		StringBuilder dataBuilder = new StringBuilder();
		dataBuilder.append(mapId)
					.append(",")
					.append(giftStates);
		
		return dataBuilder.toString();
	}
	

	
	public boolean isReceiveChapterReward() {
		return receiveChapterReward;
	}
	public void setReceiveChapterReward(boolean receiveChapterReward) {
		this.receiveChapterReward = receiveChapterReward;
	}
	public List<Integer> getRewardLvList() {
		return rewardLvList;
	}
	public void setRewardLvList(List<Integer> rewardLvList) {
		this.rewardLvList = rewardLvList;
	}
	public boolean takeCopyBox(int copyID) {
		return rewardLvList.add(copyID);
	}
	

	
	
	
	
}
