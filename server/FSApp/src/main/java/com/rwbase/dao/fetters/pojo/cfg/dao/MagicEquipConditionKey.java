package com.rwbase.dao.fetters.pojo.cfg.dao;

public class MagicEquipConditionKey {

	private final int type;
	private final int subType;
	private final int hash;

	public MagicEquipConditionKey(int type, int subType) {
		super();
		this.type = type;
		this.subType = subType;
		final int prime = 31;
		int result = 1;
		result = prime * result + subType;
		result = prime * result + type;
		hash = result;
	}

	public int getType() {
		return type;
	}

	public int getSubType() {
		return subType;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MagicEquipConditionKey other = (MagicEquipConditionKey) obj;
		if (subType != other.subType)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
