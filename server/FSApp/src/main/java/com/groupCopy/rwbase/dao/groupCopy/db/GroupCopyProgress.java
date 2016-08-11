package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.common.Utils;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GroupCopyProgress {

	private int totalHp;
	private int currentHp;
	
	private double progress;//0-1
	
	private List<GroupCopyMonsterSynStruct> mDatas = new ArrayList<GroupCopyMonsterSynStruct>();

	public GroupCopyProgress() {
		
	}

	public GroupCopyProgress(List<GroupCopyMonsterSynStruct> mData) {
		this.mDatas.clear();
		this.mDatas.addAll(mData);
		initProgress();
	}

	public void initProgress() {

		totalHp = 0;
		currentHp = 0;
		for (GroupCopyMonsterSynStruct struct : mDatas) {
			totalHp += struct.getTotalHP();
			currentHp += struct.getCurHP();
		}
		if(totalHp == 0){
			GameLog.error(LogModule.GroupCopy, "GroupCopyProgress[initProgress]", "初始化怪物"
					+ "信息，发现怪物的总hp为0", null);
			return;
		}
		progress = Utils.div((totalHp - currentHp), totalHp, 5);
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

	public void setmData(GroupCopyMonsterSynStruct m) {
		for (GroupCopyMonsterSynStruct struct : mDatas) {
			if(struct.getId().equals(m.getId())){
				struct.setCurHP(m.getCurHP());
				struct.setCurMP(m.getCurMP());
				break;
			}
		}
	}

	
	
}
