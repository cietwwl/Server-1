package com.rwbase.dao.targetSell;

public class BenefitAttrCfg {
	private String id;
	private String attrName;
	private String param;
	private int processType;

	public String getId() {
		return id;
	}

	public String getAttrName() {
		return attrName;
	}

	public String getParam() {
		return param;
	}

	public int getProcessType() {
		return processType;
	}

	@Override
	public String toString() {
		return "BenefitAttrCfg [id=" + id + ", attrName=" + attrName + ", param=" + param + ", processType=" + processType + "]";
	}

}
