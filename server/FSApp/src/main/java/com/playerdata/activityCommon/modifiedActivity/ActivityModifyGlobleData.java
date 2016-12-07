package com.playerdata.activityCommon.modifiedActivity;

import java.util.HashMap;

public class ActivityModifyGlobleData {
	
	private HashMap<Integer, ActivityModifyItem> items = new HashMap<Integer, ActivityModifyItem>();

	public HashMap<Integer, ActivityModifyItem> getItems() {
		return items;
	}

	public void setItems(HashMap<Integer, ActivityModifyItem> items) {
		this.items = items;
	}
}
