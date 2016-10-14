package com.rwbase.dao.magicweapon.pojo;

/**
 * @Author HC
 * @date 2016年10月6日 下午5:13:09
 * @desc 法宝熔炼的概率
 **/

public class MagicSmeltRateCfg {
	private int id;
	private String aptitudeGroup;	// 资质分段
	private int aptitudeChange;	// 修改的分段
	private int minAptitude;  	//资质区间的最小值
	private int maxAptitude;  	//资质区间的最大值
	private int weight;
	
	public int getId() {
		return id;
	}

	public int getMinAptitude() {
		return minAptitude;
	}

	public void setMinAptitude(int minAptitude) {
		this.minAptitude = minAptitude;
	}

	
	public int getMaxAptitude() {
		return maxAptitude;
	}

	public void setMaxAptitude(int maxAptitude) {
		this.maxAptitude = maxAptitude;
	}

	private int rate;// 权重

	/**
	 * 资质分段
	 * 
	 * @return
	 */


	/**
	 * 资质变化的值
	 * 
	 * @return
	 */
	public int getChangeValue() {
		return aptitudeChange;
	}

	public String getAptitudeGroup() {
		return aptitudeGroup;
	}

	/**
	 * 随机出来的权重
	 * 
	 * @return
	 */
	public int getRate() {
		return rate;
	}

	public int getAptitudeChange() {
		return aptitudeChange;
	}

	public int getWeight() {
		return weight;
	}
}