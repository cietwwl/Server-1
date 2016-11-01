package com.playerdata.fightinggrowth.calc;

/**
 * @Author HC
 * @date 2016年10月25日 上午10:40:57
 * @desc 计算战斗力的接口
 **/

public interface IFightingCalc {

	/**
	 * 计算战斗力的方式
	 * 
	 * @param param 需要的参数对象
	 * @return
	 */
	public int calc(Object param);
}