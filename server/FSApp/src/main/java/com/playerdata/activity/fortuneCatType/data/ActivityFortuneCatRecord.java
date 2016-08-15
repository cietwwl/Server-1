package com.playerdata.activity.fortuneCatType.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;

import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityFortuneCatRecord {

	@Id
	private String id;
	
	@CombineSave
	private String userId;
	
	@CombineSave
	private String memPos;	//为前端保存成员上阵顺序
	
	@CombineSave
	private int score;

	@CombineSave
	private int tbGold; // 组队战货币
	
	@CombineSave
	private List<Integer> finishedLoops = new ArrayList<Integer>();	//假如一个难度（即章节）三个节点，这个是已经完成的节点id号，如果已经有完成的（并且没有全部完成），就不能更换难度（章节）
	
	
	
	
	@NonSave
	private HashMap<String, String> enimyMap = new HashMap<String, String>();	//每个难度里的，怪物组（每天不同的怪物组，前端用）
	
	@NonSave
	@IgnoreSynField
	private boolean isSynTeam = false;
	
	
	
	
}
