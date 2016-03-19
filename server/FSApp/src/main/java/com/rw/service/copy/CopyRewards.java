package com.rw.service.copy;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.readonly.CopyRewardsIF;
import com.playerdata.readonly.ItemInfoIF;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class CopyRewards implements CopyRewardsIF{

	private int levelId; //关卡Id
	private List<ItemInfo> rewards;
	
	public CopyRewards(int levelIdP, List<ItemInfo> rewardsP){
		
		levelId = levelIdP;
		rewards = rewardsP;
		
	}
	
	public boolean checkLevelId(int levelIdP){
		return levelId == levelIdP;
	}
	
	public List<ItemInfoIF> getRewards(){
		return new ArrayList<ItemInfoIF>(rewards);
	}
	
}
