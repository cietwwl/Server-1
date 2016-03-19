package com.rwbase.dao.worship;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.worship.pojo.WorshipItem;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_worship")
public class TableWorship {
	@Id
	private int career;
	private List<WorshipItem> worshipItemList = new ArrayList<WorshipItem>();
	private List<String> worshippersList = new ArrayList<String>();
	
	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}
	/**前50膜拜者 带数据*/
	public List<WorshipItem> getWorshipItemList() {
		return worshipItemList;
	}

	public void setWorshipItemList(List<WorshipItem> worshipList) {
		this.worshipItemList = worshipList;
	}
	/**所有膜拜者列表，只存ＩＤ*/
	public List<String> getWorshippersList() {
		return worshippersList;
	}

	public void setWorshippersList(List<String> worshippersList) {
		this.worshippersList = worshippersList;
	}
}
