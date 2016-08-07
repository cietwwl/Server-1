package com.rwbase.dao.fashion;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.common.RefInt;
import com.playerdata.FashionMgr;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.NonSave;

/**
 * 潜规则：wingId,suitId,petId,specialPlanId用 -1 表示空！
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "fashion_being_using")
@SynClass
public class FashionBeingUsed implements FashionUsedIF {
	@Id
	private String userId;
	private int wingId = -1;
	private int suitId = -1;
	private int petId = -1;
	@NonSave
	private int totalEffectPlanId = -1;

	/**
	 * 时装信息：按照顺序是--->套装，翅膀，宠物
	 * 
	 * @return
	 */
	public int[] getUsingList() {
		int[] result = new int[3];
		result[0] = suitId;
		result[1] = wingId;
		result[2] = petId;
		return result;
	}
	
	public void setUsing(int index,int fid){
		switch(index){
		case 0:
			suitId = fid;
			break;
		case 1:
			wingId = fid;
			break;
		case 2:
			petId = fid;
			break;
		}
	}
	
	public boolean UpgradeOldData(){
		boolean result = false;
		RefInt newFid = new RefInt();
		if (FashionMgr.UpgradeIdLogic(wingId, newFid)){
			wingId = newFid.value;
			result = true;
		}
		if (FashionMgr.UpgradeIdLogic(suitId, newFid)){
			suitId = newFid.value;
			result = true;
		}
		if (FashionMgr.UpgradeIdLogic(petId, newFid)){
			petId = newFid.value;
			result = true;
		}
		return result;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getWingId() {
		return wingId;
	}

	public void setWingId(int wingId) {
		this.wingId = wingId;
	}

	public int getSuitId() {
		return suitId;
	}

	public void setSuitId(int suitId) {
		this.suitId = suitId;
	}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public int getTotalEffectPlanId() {
		return totalEffectPlanId;
	}

	public void setTotalEffectPlanId(int totalEffectPlanId) {
		this.totalEffectPlanId = totalEffectPlanId;
	}
}
