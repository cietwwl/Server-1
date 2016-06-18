package com.rw.handler.magicSecret;

import java.util.ArrayList;
import java.util.List;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MagicChapterInfo implements  SynItem {

	private String id;	//数据库主键
	

	private String chapterId;  //章节id
	
	private String userId;// 对应的角色Id
	

	
	private List<Integer> finishedStages = new ArrayList<Integer>();  //完成的关卡
	

	private int selectedDungeonIndex = -1;  //对应的是，selectableDungeons数组中的下标（-1表示未选中）

	private List<Integer> selectedBuff = new ArrayList<Integer>(); //已选择的Buff

	private List<Integer> unselectedBuff = new ArrayList<Integer>(); //可选择的buff
	

	private int starCount; //星星数量
	
	private String version;


	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getChapterId() {
		return chapterId;
	}

	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<Integer> getFinishedStages() {
		return finishedStages;
	}

	public void setFinishedStages(List<Integer> finishedStages) {
		this.finishedStages = finishedStages;
	}
	
	public int getSelectedDungeonIndex() {
		return selectedDungeonIndex;
	}

	public void setSelectedDungeonIndex(int selectedDungeonIndex) {
		this.selectedDungeonIndex = selectedDungeonIndex;
	}



	public List<Integer> getSelectedBuff() {
		return selectedBuff;
	}

	public void setSelectedBuff(List<Integer> selectedBuff) {
		this.selectedBuff = selectedBuff;
	}

	public List<Integer> getUnselectedBuff() {
		return unselectedBuff;
	}

	public void setUnselectedBuff(List<Integer> unselectedBuff) {
		this.unselectedBuff = unselectedBuff;
	}

	public int getStarCount() {
		return starCount;
	}

	public void setStarCount(int starCount) {
		this.starCount = starCount;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
