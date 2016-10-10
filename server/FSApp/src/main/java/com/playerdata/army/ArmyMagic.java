package com.playerdata.army;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemAttributeType;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyMagic {

	private String id;
	private int modelId;
	private int level;
	private float magicPer = -1;// 初始能量,这个默认值不要随便改

	public ArmyMagic() {
	}

	public ArmyMagic(ItemData magicItem) {
		this.id = magicItem.getId();
		this.modelId = magicItem.getModelId();
		String magicLevel = magicItem.getExtendAttr(EItemAttributeType.Magic_Level_VALUE);
		this.level = StringUtils.isEmpty(magicLevel) ? 0 : Integer.parseInt(magicLevel);
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
}