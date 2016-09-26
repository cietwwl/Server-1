package com.common.refOpt.example;

import java.util.HashMap;

import com.common.refOpt.IRefOpt;

public class BeanA implements IRefOpt{

	private String name;
	
	private int count;	
	
	private HashMap<Object,Object> ref$FNmap = new HashMap<Object,Object>();
	
	public void ref$Init(){ref$FNmap = new HashMap<Object,Object>();}

	public Object ref$Get(String fieldName) {
		
		Object index2 = (Object)ref$FNmap.get(fieldName);
		Integer indexInt = (Integer)index2;
		int index = indexInt.intValue();
		
		switch (index) {
			case 1:	return this.name;
			case 2:	return this.count;			
		}return null;}

	
	
	
}
