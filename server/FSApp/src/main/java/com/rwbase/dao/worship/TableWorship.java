package com.rwbase.dao.worship;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.dao.worship.pojo.WorshipItem;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_worship")
public class TableWorship {
	@Id
	private int career;
	private CopyOnWriteArrayList<WorshipItem> worshipItemList = new CopyOnWriteArrayList<WorshipItem>();
	
	
	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}
	/**前50膜拜者 带数据*/
	public List<WorshipItem> getWorshipItemList() {
		return new ArrayList<WorshipItem>(worshipItemList);
	}


	public void clear() {
		worshipItemList.clear();
	}

	public void add(WorshipItem item) {
		worshipItemList.add(item);
	}

	public void remove(WorshipItem item) {
		worshipItemList.remove(item);
	}
	
}
