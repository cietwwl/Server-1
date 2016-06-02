package com.playerdata.army;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemAttributeType;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmyMagic {

	private int modelId;
	private int level;

	public ArmyMagic() {
	}

	public ArmyMagic(ItemData magicItem) {
		this.modelId = magicItem.getModelId();
		String magicLevel = magicItem.getExtendAttr(EItemAttributeType.Magic_Level_VALUE);
		this.level = StringUtils.isEmpty(magicLevel) ? 0 : Integer.parseInt(magicLevel);
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

}
