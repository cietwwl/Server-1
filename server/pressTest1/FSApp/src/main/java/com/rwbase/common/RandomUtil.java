package com.rwbase.common;

import java.util.List;

import org.apache.commons.lang.math.RandomUtils;


public class RandomUtil {

	public static int getRandonIndex(List<Integer> list)
	{
		int resultId = -1;
		if(list == null) return resultId;
		int maxValue = 0;
		for(int value:list){
			maxValue += value;
		}
		
		int section = 0;
		int randomResult = RandomUtils.nextInt(maxValue);
		for(int i = 0;i < list.size();i++){
			section += list.get(i);
			if(randomResult < section){
				resultId = i;
				break;
			}
		}
		
		return resultId;
	}
	
	// 等概率随机
	public static int getRandonIndexWithoutProb(int length)
	{
		int resultId = -1;
		if(length == 0) return resultId;
		
		int section = 0;
		int randomResult = RandomUtils.nextInt(length);
		for(int i = 0;i < length;i++){
			section += 1;
			if(randomResult < section){
				resultId = i;
				break;
			}
		}
		
		return resultId;
	}
	
	
	public static int nextInt(int range){
		return RandomUtils.nextInt(range);
	}
}
