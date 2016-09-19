package com.playerdata.groupcompetition.holder;

/**
 * 
 * 海选阶段的数据holder
 * 
 * @author CHEN.P
 *
 */
public class GCSelectionDataHolder {

	private static final GCSelectionDataHolder _instance = new GCSelectionDataHolder();
	
	public static final GCSelectionDataHolder getInstance() {
		return _instance;
	}
	
	protected GCSelectionDataHolder() {
		
	}
}
