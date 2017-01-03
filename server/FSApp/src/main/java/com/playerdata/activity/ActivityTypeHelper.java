package com.playerdata.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rw.fsutil.util.DateUtils;

public class ActivityTypeHelper {
	
	private static final int fiveAm = 5;
	
	private static ActivityTypeHelper  instance = new ActivityTypeHelper();
	/**
	 * 
	 * @return  针对通用活动很多方法通用的特点，且活动及填表的一些特殊需求，在业务逻辑和dateutils中封装一层处理
	 */
	public static ActivityTypeHelper getinstance (){
		
		return instance;
	}
	
	/**
	 * 
	 * @param time   传入界定时间，获取当前时间和界定时间之间间隔的天数；此天数不是绝对时间长度，而是以5点为标准；当天4.59和5.01即算间隔一天，同时策划配表第一天和第二天会生效
	 * 如果界定时间比传入时间要晚，说明活动未激活，返回0
	 * @return
	 */
	public static int getDayBy5Am(long time){
		long now = System.currentTimeMillis();
		if(time > now){
			return 0;
		}
		int day=0;		
		day=DateUtils.getDayLimitHour(fiveAm, time);
		day++;		
		return day;
	}
	
	/**
	 * 将加载配置文件时的多个模块的缓存化数据使用统一方法处理
	 * @param cfgItem
	 */
	@SuppressWarnings("unchecked")
	public static <T> void add(Object cfg, String id, HashMap<String, List<T>> enumIdCfgMapping_){
		List<T> list = enumIdCfgMapping_.get(id);
		if (list == null) {
			list = new ArrayList<T>();
			enumIdCfgMapping_.put(id, list);
		}
		list.add((T) cfg);
	}
}
