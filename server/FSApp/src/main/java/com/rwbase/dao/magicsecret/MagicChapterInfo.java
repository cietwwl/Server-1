package com.rwbase.dao.magicsecret;

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
	private String id;
	
	private String userId;// 对应的角色Id
	
	@CombineSave
	private String maxStageID;	//关卡历史最高纪录
	
	@CombineSave
	private List<ItemInfo> canOpenBoxes = new ArrayList<ItemInfo>();  //可以打开的箱子(对应箱子ID和数量)
	
	@CombineSave
	private List<Integer> finishedStages = new ArrayList<Integer>();  //完成的关卡
	
	@CombineSave
	private List<MSStageInfo> selectableStages = new ArrayList<MSStageInfo>(); //可挑选的关卡
	
	@CombineSave
	private List<Integer> selectedBuff = new ArrayList<Integer>(); //已选择的Buff
	
	@CombineSave
	private List<ItemInfo> unselectedBuff = new ArrayList<ItemInfo>(); //可选择的buff
	
	@CombineSave
	private int starCount; //星星数量
	
	@CombineSave
	private String version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getMaxStageID() {
		return maxStageID;
	}

	public void setMaxStageID(String maxStageID) {
		this.maxStageID = maxStageID;
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

	public List<MSStageInfo> getSelectableStages() {
		return selectableStages;
	}

	public void setSelectableStages(List<MSStageInfo> selectableStages) {
		this.selectableStages = selectableStages;
	}

	public List<Integer> getSelectedBuff() {
		return selectedBuff;
	}

	public void setSelectedBuff(List<Integer> selectedBuff) {
		this.selectedBuff = selectedBuff;
	}

	public List<ItemInfo> getUnselectedBuff() {
		return unselectedBuff;
	}

	public void setUnselectedBuff(List<ItemInfo> unselectedBuff) {
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
