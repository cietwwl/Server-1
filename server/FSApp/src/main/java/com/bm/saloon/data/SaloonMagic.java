package com.bm.saloon.data;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.dao.item.pojo.ItemData;

@SynClass
public class SaloonMagic {

	private int modelId;
	private int level;
	private int aptitude;
	
	public static SaloonMagic from(ItemData magicItem ){
		SaloonMagic magic = new SaloonMagic();
		magic.modelId = magicItem.getModelId();
		magic.level = magicItem.getMagicLevel();
		magic.aptitude = magicItem.getMagicAptitude();
		return magic;
	}

	public int getModelId() {
		return modelId;
	}

	public int getLevel() {
		return level;
	}

	public int getAptitude() {
		return aptitude;
	}
	
	
}
