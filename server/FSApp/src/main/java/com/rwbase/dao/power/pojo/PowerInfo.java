package com.rwbase.dao.power.pojo;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年4月23日 下午4:45:17
 * @Description 体力信息
 */
@SynClass
public class PowerInfo {
	private int nTime;// 恢复一个体力需要的时间
	private int buyCount;// 购买体力的次数
	private int tTime;// 恢复全部体力需要的时间
	private final int speed;// 速度（秒）

	public PowerInfo(int speed) {
		this.speed = speed;
	}

	/**
	 * 获取恢复一个体力需要的时间
	 * 
	 * @return
	 */
	public int getnTime() {
		return nTime;
	}

	/**
	 * 设置恢复一个体力需要的时间
	 * 
	 * @param nTime
	 */
	public void setnTime(int nTime) {
		this.nTime = nTime;
	}

	/**
	 * 获取已经购买的次数
	 * 
	 * @return
	 */
	public int getBuyCount() {
		return buyCount;
	}

	/**
	 * 设置已经购买体力的次数
	 * 
	 * @param buyCount
	 */
	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	/**
	 * 获取恢复满需要的时间
	 * 
	 * @return
	 */
	public int gettTime() {
		return tTime;
	}

	/**
	 * 设置恢复满体力需要的时间
	 * 
	 * @param tTime
	 */
	public void settTime(int tTime) {
		this.tTime = tTime;
	}

	/**
	 * 获取速度
	 * 
	 * @return
	 */
	public int getSpeed() {
		return speed;
	}
}