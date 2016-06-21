package com.playerdata.groupFightOnline.manager;

/**
 * 在线帮战，最终结算阶段管理类
 * @author aken
 *
 */
public class GFightFinalMgr {
	
	private static class InstanceHolder{
		private static GFightFinalMgr instance = new GFightFinalMgr();
	}
	
	public static GFightFinalMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightFinalMgr() { }
}
