package com.rwbase.dao.fetters.pojo.cfg.dao;

/**
 * 法宝及对应英雄modelId
 * @author Alex
 * 2016年12月22日 下午8:27:30
 */
public class MagicHeroModelKey {

	private int itemModelId;
	
	private int heroModelId;

	public MagicHeroModelKey(int itemModelId, int heroModelId) {
		super();
		this.itemModelId = itemModelId;
		this.heroModelId = heroModelId;
	}

	public int getItemModelId() {
		return itemModelId;
	}

	public int getHeroModelId() {
		return heroModelId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + heroModelId;
		result = prime * result + itemModelId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MagicHeroModelKey other = (MagicHeroModelKey) obj;
		if (heroModelId != other.heroModelId)
			return false;
		if (itemModelId != other.itemModelId)
			return false;
		return true;
	}

	
	
}
