package com.playerdata.groupcompetition;

import java.util.Calendar;

import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GroupCompetitionMgr {

	private static final GroupCompetitionMgr _instance = new GroupCompetitionMgr();
	
	protected GroupCompetitionMgr() {}
	
	public static final GroupCompetitionMgr getInstance() {
		return _instance;
	}
	
	private void loadGroupCompetitionSaveData() {
		String attrData = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION);
		GroupCompetitionSaveData.initDataFromDB(attrData);
	}
	
	private long getServerStartTime() {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.MONTH, 9);
		return instance.getTimeInMillis();
	}
	
	private void checkStartGroupCompetition() {
		if(GroupCompetitionSaveData.getInstance().getHeldTimes() > 0) {
			
		} else {
			// 没有举办过
		}
	}
	
	/**
	 * 服务器启动完毕的通知
	 */
	public void serverStartComplete() {
		this.loadGroupCompetitionSaveData();
		this.checkStartGroupCompetition();
	}
}
