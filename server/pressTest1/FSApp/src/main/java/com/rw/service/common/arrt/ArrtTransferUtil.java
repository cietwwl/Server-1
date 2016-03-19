package com.rw.service.common.arrt;

import java.util.List;

import com.rwbase.common.enu.eAttrIdDef;

public class ArrtTransferUtil {
	
	public static void transfer(eAttrIdDef arrType,double value,List<ArrtTypeAndVale> arrtTypeAndVales){
		if(value == 0){
			return;
		}
		ArrtTypeAndVale ArrtTypeAndVale = new ArrtTypeAndVale();
		ArrtTypeAndVale.setType(arrType.getOrder());
		ArrtTypeAndVale.setValue(value);
		arrtTypeAndVales.add(ArrtTypeAndVale);
	}

}
