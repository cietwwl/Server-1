package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 海选阶段的数据holder
 * 
 * @author CHEN.P
 *
 */
public class GCompSelectionDataHolder {

	private static final GCompSelectionDataHolder _instance = new GCompSelectionDataHolder();
	
	public static final GCompSelectionDataHolder getInstance() {
		return _instance;
	}
	
	private final List<String> _selectedGroupIds = new ArrayList<String>();
	
	protected GCompSelectionDataHolder() {
		
	}
	
	void setSelectedGroupIds(List<String> groupIds) {
		this._selectedGroupIds.clear();
		this._selectedGroupIds.addAll(groupIds);
	}
	
	List<String> getSelectedGroupIds() {
		return Collections.unmodifiableList(_selectedGroupIds);
	}
}
