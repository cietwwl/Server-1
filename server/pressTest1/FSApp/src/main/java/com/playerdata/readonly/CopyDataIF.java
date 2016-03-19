package com.playerdata.readonly;

import java.util.Enumeration;
import java.util.Map;

import com.rwbase.dao.copy.pojo.CopyLevelRecord;

/*
 * 副本数据接口
 * @author Luther
 */

public interface CopyDataIF {
	/*
	 * 获取InfoID
	 */
	public int getInfoId();
	
	/*
	 * 获取副本类型
	 */
	public int getCopyType();
	
	/*
	 * 获取副本次数
	 */
	public int getCopyCount();
	
	/*
	 * 获取已重置次数
	 */
	public int getResetCount();
	
	public Integer getPassMap(String key);

	public Enumeration<String> getPassMapKeysEnumeration();
	
	public Enumeration<Integer> getPassMapValuesEnumeration();

	/**
	 * 获取上次挑战时间
	 * @return
	 */
	public long getLastChallengeTime();
}
