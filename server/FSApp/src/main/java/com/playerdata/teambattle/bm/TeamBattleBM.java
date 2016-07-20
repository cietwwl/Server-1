package com.playerdata.teambattle.bm;


/**
 * 组队战
 * @author aken
 *
 */
public class TeamBattleBM {
	
	private static class InstanceHolder{
		private static TeamBattleBM instance = new TeamBattleBM();
	}
	
	public static TeamBattleBM getInstance(){
		return InstanceHolder.instance;
	}
	
	
}
