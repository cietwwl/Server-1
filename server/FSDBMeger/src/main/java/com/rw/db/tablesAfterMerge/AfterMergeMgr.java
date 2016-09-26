package com.rw.db.tablesAfterMerge;

import java.util.ArrayList;
import java.util.List;

import com.rw.db.DBInfo;
import com.rw.db.tablesAfterMerge.arena.ArenaAfterMergeProcess;
import com.rw.db.tablesAfterMerge.battleTower.BattleTowerAfterMergeProcess;
import com.rw.db.tablesAfterMerge.email.EmailAfterMergeProcess;
import com.rw.db.tablesAfterMerge.groupSecret.GroupSecretAfterMergeProcess;
import com.rw.db.tablesAfterMerge.worship.WorshipAfterMergeProcess;

public class AfterMergeMgr {
	
	private static AfterMergeMgr instance = new AfterMergeMgr();
	
	private List<AbsAfterMergeProcess> progressList = new ArrayList<AbsAfterMergeProcess>();
	
	public static AfterMergeMgr getInstance(){
		if(instance== null){
			instance = new AfterMergeMgr();
		}
		return instance;
	}
	
	public void init(){
		progressList.add(new ArenaAfterMergeProcess());
		progressList.add(new EmailAfterMergeProcess());
		progressList.add(new GroupSecretAfterMergeProcess());
		progressList.add(new WorshipAfterMergeProcess());
		progressList.add(new BattleTowerAfterMergeProcess());
	}
	
	public void processAfterMerge(DBInfo dbInfo){
		for (AbsAfterMergeProcess process : progressList) {
			process.exec(dbInfo);
		}
	}
}
