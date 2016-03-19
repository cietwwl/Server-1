package com.rwbase.dao.fashion;

import com.rwbase.common.enu.eAttrIdDef;

/**
 * 时装类型
 * @author allen
 *
 */
public enum FashType {

	wings(0),  //翅膀
	pet(1),	//宠物
	suit(2);	//套装
	
	private int order;
	FashType(int order){
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
	
	public static FashType getDef(int value){
		FashType eAttr = null;
		for (int i = 0; i < FashType.values().length; i++) {
			eAttr = FashType.values()[i];
			if(eAttr.getOrder() == value){
				break;
			}
		}
		return eAttr;
	}
}
