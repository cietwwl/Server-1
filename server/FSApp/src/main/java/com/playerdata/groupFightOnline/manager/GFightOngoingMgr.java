package com.playerdata.groupFightOnline.manager;

/**
 * 在线帮战，战斗阶段管理类
 * @author aken
 *
 */
public class GFightOngoingMgr {
	
	private static class InstanceHolder{
		private static GFightOngoingMgr instance = new GFightOngoingMgr();
	}
	
	public static GFightOngoingMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightOngoingMgr() { }
}
