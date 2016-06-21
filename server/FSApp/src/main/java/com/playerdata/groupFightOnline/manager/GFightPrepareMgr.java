package com.playerdata.groupFightOnline.manager;

/**
 * 在线帮战，准备阶段管理类
 * @author aken
 *
 */
public class GFightPrepareMgr {
	
	private static class InstanceHolder{
		private static GFightPrepareMgr instance = new GFightPrepareMgr();
	}
	
	public static GFightPrepareMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightPrepareMgr() { }
}
