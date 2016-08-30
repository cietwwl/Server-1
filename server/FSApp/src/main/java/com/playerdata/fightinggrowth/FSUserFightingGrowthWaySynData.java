package com.playerdata.fightinggrowth;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

/**
 * 
 * 战力提升途径同步类
 * 
 * @author CHEN.P
 *
 */
@SynClass
public class FSUserFightingGrowthWaySynData {

	public String key;
	/**
	 * 战力提升途径的名字
	 */
	public String name;
	/**
	 * 玩家在该项拥有的战力
	 */
	public int currentValue;
	/**
	 * 该项当前等级所能达到的最大战力
	 */
	public int maxValue;
	/**
	 * 获取途径
	 */
	public List<Integer> gainWays;
	/**
	 * 打开的功能类型
	 */
	public int gotoType;
}
