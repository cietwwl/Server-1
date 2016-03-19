package com.rwbase.common.enu;

public enum eGuideStateDef {
	/**未激活*/
	unactivated(0),
	/**激活中*/
	activating(1),
	/**已激活*/
	activated(2);
	private int order;
	
	eGuideStateDef(int order){
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
	
	public static eGuideStateDef getDef(int value){
		eGuideStateDef eAttr = null;
		for (int i = 0; i < eGuideStateDef.values().length; i++) {
			eAttr = eGuideStateDef.values()[i];
			if(eAttr.getOrder() == value){
				break;
			}
		}
		return eAttr;
	}
}
