package com.rwbase.dao.skill.optlisten;

import java.util.Collections;
import java.util.List;

/**
 * @Author HC
 * @date 2016年8月23日 下午5:35:20
 * @desc
 **/

public class OptResult {
	private final List<String> deleteIdList;
	private final List<String> addIdList;

	public OptResult(List<String> deleteIdList, List<String> addIdList) {
		if (deleteIdList != null && !deleteIdList.isEmpty()) {
			this.deleteIdList = Collections.unmodifiableList(deleteIdList);
		} else {
			this.deleteIdList = Collections.emptyList();
		}

		if (addIdList != null && !addIdList.isEmpty()) {
			this.addIdList = Collections.unmodifiableList(addIdList);
		} else {
			this.addIdList = Collections.emptyList();
		}
	}

	/**
	 * 获取要删除的技能Id列表
	 * 
	 * @return
	 */
	public List<String> getDeleteIdList() {
		return deleteIdList;
	}

	/**
	 * 获取要增加的技能Id列表
	 * 
	 * @return
	 */
	public List<String> getAddIdList() {
		return addIdList;
	}
}