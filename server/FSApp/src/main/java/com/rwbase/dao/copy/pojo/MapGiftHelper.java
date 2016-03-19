package com.rwbase.dao.copy.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MapGiftHelper {
	
	private static MapGiftHelper instance = new MapGiftHelper();

	private MapGiftHelper(){};
	
	
	final private String SEPERATOR = ","; 
	
	public static MapGiftHelper getInstance(){
		return instance;
	}
	
	public  List<Integer> getGiftStateList(String startGift){
		List<Integer> stateList = new ArrayList<Integer>();
		String[] split = startGift.split(",");
		for (String statTmp : split) {
			stateList.add(Integer.valueOf(statTmp));
		}
		return stateList;
	}

	public boolean isGiftCanTake(String startGift, int index){
		String[] split = startGift.split(SEPERATOR);
		return canTake(index, split);
	}
	private boolean canTake(int index, String[] split) {
		boolean canTake = false;
		if(0 <= index  && index < split.length){
			Integer indexState = Integer.valueOf(split[index]);
			canTake = (indexState == 0);
		}
		return canTake;
	}
	
	public String takeGift(String startGift, int index){
		String[] split = startGift.split(",");
		split[index] = "1";		
		return StringUtils.join(split, SEPERATOR);
	}
	
	
	
}
