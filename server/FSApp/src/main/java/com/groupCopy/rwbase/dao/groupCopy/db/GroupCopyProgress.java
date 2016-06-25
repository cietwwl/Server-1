package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GroupCopyProgress {

	private int totalHp;
	private int currentHp;
	
	private double progress;//0-1
	
	private List<GroupCopyMonsterSynStruct> mDatas = new ArrayList<GroupCopyMonsterSynStruct>();
	
	
	

	public GroupCopyProgress(List<GroupCopyMonsterSynStruct> mData) {
		this.mDatas.clear();
		this.mDatas.addAll(mData);
		initProgress();
	}

	private void initProgress() {

		totalHp = 0;
		currentHp = 0;
		for (GroupCopyMonsterSynStruct struct : mDatas) {
			totalHp += struct.getTotalHP();
			currentHp += struct.getCurHP();
		}
		
		progress = (totalHp - currentHp) / totalHp;
	}

	public int getTotalHp() {
		return totalHp;
	}

	public void setTotalHp(int totalHp) {
		this.totalHp = totalHp;
	}

	public int getCurrentHp() {
		return currentHp;
	}

	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public List<GroupCopyMonsterSynStruct> getmDatas() {
		return mDatas;
	}

	public void setmDatas(List<GroupCopyMonsterSynStruct> mDatas) {
		this.mDatas = mDatas;
	}

	public void reset() {
		
		synchronized (mDatas) {
			for (GroupCopyMonsterSynStruct struct : mDatas) {
				struct.setCurHP(struct.getTotalHP());
				struct.setCurMP(struct.getTotalMP());
			}
			initProgress();
		}
	}

	
	
}
