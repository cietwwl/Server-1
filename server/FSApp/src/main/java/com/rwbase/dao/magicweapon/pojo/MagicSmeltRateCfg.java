package com.rwbase.dao.magicweapon.pojo;

/**
 * @Author HC
 * @date 2016年10月6日 下午5:13:09
 * @desc 法宝熔炼的概率
 **/

public class MagicSmeltRateCfg {
	private String aptitude;// 资质分段
	private int changeValue;// 修改的分段
	private int rate;// 权重

	/**
	 * 资质分段
	 * 
	 * @return
	 */
	public String getAptitude() {
		return aptitude;
	}

	/**
	 * 资质变化的值
	 * 
	 * @return
	 */
	public int getChangeValue() {
		return changeValue;
	}

	/**
	 * 随机出来的权重
	 * 
	 * @return
	 */
	public int getRate() {
		return rate;
	}
}