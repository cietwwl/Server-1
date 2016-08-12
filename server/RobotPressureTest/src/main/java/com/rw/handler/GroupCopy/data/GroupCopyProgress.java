package com.rw.handler.GroupCopy.data;

import java.util.ArrayList;
import java.util.List;

public class GroupCopyProgress {

	private int totalHp;
	private int currentHp;
	
	private double progress;//0-1
	
	private List<GroupCopyMonsterSynStruct> mDatas = new ArrayList<GroupCopyMonsterSynStruct>();

	public int getTotalHp() {
		return totalHp;
	}

	public int getCurrentHp() {
		return currentHp;
	}

	public double getProgress() {
		return progress;
	}

	public List<GroupCopyMonsterSynStruct> getmDatas() {
		return mDatas;
	}

	
	
	
}
