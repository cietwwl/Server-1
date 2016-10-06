package com.rwbase.dao.magicweapon.pojo;

/**
 * @Author HC
 * @date 2016年10月6日 下午5:25:54
 * @desc
 **/

public class MagicAptitudeCfg {
	private int id;// 对应的资质属性Id
	private int aptitude;// 自己的类型
	private String growUp;// 属性的成长

	/**
	 * 获取资质的技能Id
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 获取资质
	 * 
	 * @return
	 */
	public int getAptitude() {
		return aptitude;
	}

	/**
	 * 获取资质对应的增长属性
	 * 
	 * @return
	 */
	public String getGrowUp() {
		return growUp;
	}
}