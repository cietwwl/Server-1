package com.playerdata.activity.notice;

import com.playerdata.activityCommon.activityType.IndexRankJudgeIF;

public class ActivityNoticeMgr implements IndexRankJudgeIF {
	
	private static final int ACTIVITY_INDEX_BEGIN = 200000;
	private static final int ACTIVITY_INDEX_END = 210000;

	private static ActivityNoticeMgr instance = new ActivityNoticeMgr();
	
	public static ActivityNoticeMgr getInstance() {
		return instance;
	}

	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
