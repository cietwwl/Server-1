package com.rwbase.dao.skill.optlisten;

import java.util.List;

import com.rwbase.dao.skill.pojo.SkillListenOptTemplate;

/**
 * @Author HC
 * @date 2016年8月23日 下午5:44:02
 * @desc
 **/

public class SkillListenOptUtils {

	private static SkillListenOptUtils utils = new SkillListenOptUtils();

	public static SkillListenOptUtils getInstance() {
		return utils;
	}

	private IOptListen[] listenArr;

	SkillListenOptUtils() {
		listenArr = new IOptListen[3];
		listenArr[0] = new AddListenIdOpt();
		listenArr[1] = new DeleteListenIdOpt();
		listenArr[2] = new ReplaceListenIdOpt();
	}

	/**
	 * 获取监听的处理类
	 * 
	 * @param optType
	 * @return
	 */
	private IOptListen getOptListen(int optType) {
		for (int i = 0, len = listenArr.length; i < len; i++) {
			IOptListen listen = listenArr[i];
			if (listen.getOptType() == optType) {
				return listen;
			}
		}

		return null;
	}

	/**
	 * 获取处理的结果
	 * 
	 * @param optType
	 * @param listenerIdList
	 * @param tmp
	 * @return
	 */
	public OptResult getOptResult(int optType, String checkSkillId, List<String> listenerIdList, SkillListenOptTemplate tmp) {
		IOptListen optListen = getOptListen(optType);
		if (optListen == null) {
			return null;
		}

		return optListen.checkOptListen(checkSkillId, listenerIdList, tmp);
	}
}