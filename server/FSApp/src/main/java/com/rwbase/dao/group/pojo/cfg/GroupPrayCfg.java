package com.rwbase.dao.group.pojo.cfg;

/**
 * @Author HC
 * @date 2016年12月22日 下午8:15:26
 * @desc 帮派祈福的配置类
 **/

public class GroupPrayCfg {
	private int targetSoulItemId;// 魂石的Id
	private int exchangeLimit;// 每天可以获取的上限

	/**
	 * 获取魂石的模板Id
	 * 
	 * @return
	 */
	public int getTargetSoulItemId() {
		return targetSoulItemId;
	}

	/**
	 * 每天可以获取的上限数量
	 * 
	 * @return
	 */
	public int getExchangeLimit() {
		return exchangeLimit;
	}
}