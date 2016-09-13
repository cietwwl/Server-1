package com.playerdata.groupcompetition;

public class GroupCompetitionMgr {

	private static final GroupCompetitionMgr _instance = new GroupCompetitionMgr();
	
	protected GroupCompetitionMgr() {}
	
	public static final GroupCompetitionMgr getInstance() {
		return _instance;
	}
	
	/**
	 * 服务器启动完毕的通知
	 */
	public void serverStartComplete() {
		
	}
}
