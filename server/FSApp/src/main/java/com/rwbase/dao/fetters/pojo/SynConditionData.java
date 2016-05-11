package com.rwbase.dao.fetters.pojo;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年5月5日 下午2:46:59
 * @Description 
 */
@SynClass
public class SynConditionData {
	private List<Integer> conditionList;// 完成的条件列表

	public SynConditionData() {
		this.conditionList = new ArrayList<Integer>();
	}

	public List<Integer> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<Integer> conditionList) {
		this.conditionList = conditionList;
	}
}