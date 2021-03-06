package com.playerdata.army;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.item.pojo.ItemData;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyMagic {

	private String id;
	private int modelId;
	private int level;
	private int aptitude = 1;
	private float magicPer = -1;// 初始能量,这个默认值不要随便改

	public ArmyMagic() {
	}

	public ArmyMagic(ItemData magicItem) {
		this.id = magicItem.getId();
		this.modelId = magicItem.getModelId();
		this.level = magicItem.getMagicLevel();
		this.aptitude = magicItem.getMagicAptitude();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getMagicPer() {
		return magicPer;
	}

	public void setMagicPer(float magicPer) {
		this.magicPer = magicPer;
	}

	public int getAptitude() {
		return aptitude;
	}

	public void setAptitude(int aptitude) {
		this.aptitude = aptitude;
	}
}