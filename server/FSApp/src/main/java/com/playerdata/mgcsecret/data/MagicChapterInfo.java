package com.playerdata.mgcsecret.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwbase.dao.copy.pojo.ItemInfo;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "magic_chapter_info")
public class MagicChapterInfo implements  IMapItem {

	@Id
	private String id;	//数据库主键
	
	@CombineSave
	private String chapterId;  //章节id
	
	private String userId;// 对应的角色Id
	
	@CombineSave
	private List<ItemInfo> canOpenBoxes = new ArrayList<ItemInfo>();  //可以打开的箱子(对应箱子ID和数量)
	
	@CombineSave
	private List<Integer> finishedStages = new ArrayList<Integer>();  //完成的关卡
	
	@CombineSave
	private int selectedDungeonIndex = -1;  //对应的是，selectableDungeons数组中的下标（-1表示未选中）
	
	@CombineSave
	private List<MSDungeonInfo> selectableDungeons = new ArrayList<MSDungeonInfo>(); //可挑选的关卡
	
	@CombineSave
	private List<Integer> selectedBuff = new ArrayList<Integer>(); //已选择的Buff
	
	@CombineSave
	private List<Integer> unselectedBuff = new ArrayList<Integer>(); //可选择的buff
	
	@CombineSave
	private int starCount; //星星数量
	
	@CombineSave
	private String version;

	public void resetData(){
		canOpenBoxes.clear();
		finishedStages.clear();
		selectedDungeonIndex = -1;
		if(selectableDungeons != null) selectableDungeons.clear();
		selectedBuff.clear();
		unselectedBuff.clear();
		starCount = 0;
	}
	
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

	public List<ItemInfo> getCanOpenBoxes() {
		return canOpenBoxes;
	}

	public void setCanOpenBoxes(List<ItemInfo> canOpenBoxes) {
		this.canOpenBoxes = canOpenBoxes;
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

	public List<MSDungeonInfo> getSelectableDungeons() {
		return selectableDungeons;
	}

	public void setSelectableDungeons(List<MSDungeonInfo> selectableDungeons) {
		this.selectableDungeons = selectableDungeons;
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
