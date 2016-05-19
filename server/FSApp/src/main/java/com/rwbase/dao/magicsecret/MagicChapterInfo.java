package com.rwbase.dao.magicsecret;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "magic_chapter_info")
public class MagicChapterInfo implements  IMapItem {

	@Id
	private String id;
	
	private String userId;// 对应的角色Id
	
	@CombineSave
	private List<Integer> canOpenBoxes = new ArrayList<Integer>();  //可以打开的箱子
	
	@CombineSave
	private List<StageInfo> finishedStages = new ArrayList<StageInfo>();  //完成的关卡
	
	@CombineSave
	private List<StageInfo> selectableStages = new ArrayList<StageInfo>(); //可选的关卡
	
	@CombineSave
	private List<Integer> selectedBuff = new ArrayList<Integer>(); //已选择的Buff
	
	@CombineSave
	private List<Integer> unselectedBuff = new ArrayList<Integer>(); //可选择的buff
	
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

	public List<Integer> getCanOpenBoxes() {
		return canOpenBoxes;
	}

	public void setCanOpenBoxes(List<Integer> canOpenBoxes) {
		this.canOpenBoxes = canOpenBoxes;
	}

	public List<Integer> getFinishedStages() {
		return finishedStages;
	}

	public void setFinishedStages(List<Integer> finishedStages) {
		this.finishedStages = finishedStages;
	}

	public List<StageInfo> getSelectableStages() {
		return selectableStages;
	}

	public void setSelectableStages(List<StageInfo> selectableStages) {
		this.selectableStages = selectableStages;
	}

	public List<BuffInfo> getSelectedBuff() {
		return selectedBuff;
	}

	public void setSelectedBuff(List<BuffInfo> selectedBuff) {
		this.selectedBuff = selectedBuff;
	}

	public List<BuffInfo> getUnselectedBuff() {
		return unselectedBuff;
	}

	public void setUnselectedBuff(List<BuffInfo> unselectedBuff) {
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
