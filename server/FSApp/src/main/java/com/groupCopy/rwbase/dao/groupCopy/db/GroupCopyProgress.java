package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.List;

import com.rwproto.GroupCopyBattleProto.CopyMonsterStruct;

public class GroupCopyProgress {

	private int totalHp;
	private int currentHp;
	
	private double progress;
	
	private List<CopyMonsterStruct> mDatas = new ArrayList<CopyMonsterStruct>();
	
	public GroupCopyProgress(List<CopyMonsterStruct> mData) {
		this.mDatas.addAll(mData);
		initProgress();
	}

	private void initProgress() {

		totalHp = 0;
		currentHp = 0;
		for (CopyMonsterStruct struct : mDatas) {
			totalHp += struct.getTotalHp();
			currentHp += struct.getCurrentHp();
		}
		
		progress = (totalHp - currentHp) / totalHp * 100;
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

	public List<CopyMonsterStruct> getmDatas() {
		return mDatas;
	}

	public void setmDatas(List<CopyMonsterStruct> mDatas) {
		this.mDatas = mDatas;
	}

	
	
}
