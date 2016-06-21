package com.rw.service.item.useeffect.impl;

import java.util.Comparator;

public class UseItemDataComparator {
	
	private static Comparator<UseItemTempData> comparator = new Comparator<UseItemTempData>() {
		
		@Override
		public int compare(UseItemTempData o1, UseItemTempData o2) {
			if(o1.getValue() < o2.getValue()){
				return -1;
			}
			if(o1.getValue() > o2.getValue()){
				return 1;
			}
			return 0;
		}
	};
	
	public static Comparator<UseItemTempData> getInstance(){
		return comparator;
				
	}

}
