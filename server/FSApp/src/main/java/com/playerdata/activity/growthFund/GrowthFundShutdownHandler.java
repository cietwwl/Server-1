package com.playerdata.activity.growthFund;

import com.rw.fsutil.shutdown.IShutdownHandler;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GrowthFundShutdownHandler implements IShutdownHandler {

	@Override
	public void notifyShutdown() {
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROWTH_FUND, JsonUtil.writeValue(ActivityGrowthFundMgr.getInstance().getGlobalData()));
	}

}
