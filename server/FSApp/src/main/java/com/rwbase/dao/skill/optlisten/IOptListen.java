package com.rwbase.dao.skill.optlisten;

import java.util.List;

import com.rwbase.dao.skill.pojo.SkillListenOptTemplate;

/**
 * @Author HC
 * @date 2016年8月23日 下午5:34:36
 * @desc
 **/

public interface IOptListen {
	/**
	 * 检查技能修改之后，影响到其他技能的修改数据
	 * 
	 * @param checkSkillId 要检查的技能Id
	 * @param listenerIdList
	 * @param tmp 技能操作的模版
	 * @return
	 */
	public OptResult checkOptListen(String checkSkillId, List<String> listenerIdList, SkillListenOptTemplate tmp);

	/**
	 * 获取检查的类型
	 * 
	 * @return
	 */
	public int getOptType();
}