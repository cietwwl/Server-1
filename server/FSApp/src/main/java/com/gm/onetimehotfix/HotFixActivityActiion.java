package com.gm.onetimehotfix;

import com.playerdata.Player;
import com.playerdata.activityCommon.ActivityMgrHelper;
import com.rw.fsutil.dao.optimize.DataValueAction;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class HotFixActivityActiion implements DataValueAction<Player> {

	private final long TODAY_FIVE_MIL;
	
	public HotFixActivityActiion(long disTime){
		TODAY_FIVE_MIL = disTime;
	}
	
	@Override
	public void execute(Player player) {
		GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {
			@Override
			public void run(Player player) {
				long createTime = player.getUserDataMgr().getCreateTime();
				if(createTime < TODAY_FIVE_MIL){
					ActivityMgrHelper.getInstance().dailyRefreshNewDaySubActivity(player);
				}
			}
		});
	}

}
