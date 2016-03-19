package com.playerdata.readonly;

import java.util.List;

public interface CopyRewardsIF {
	public boolean checkLevelId(int levelIdP);
	
	public List<ItemInfoIF> getRewards();
}
