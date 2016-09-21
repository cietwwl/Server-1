package com.playerdata.groupcompetition.dao;

import com.playerdata.groupcompetition.holder.data.GCompSelectionData;

public class GCompSelectionDataDAO {

	private static final GCompSelectionDataDAO _instance = new GCompSelectionDataDAO();
	
	public static final GCompSelectionDataDAO getInstance() {
		return _instance;
	}
	
	private GCompSelectionData _data;

	public GCompSelectionData get() {
		if (_data == null) {
			synchronized (this) {
				if (_data == null) {
					_data = new GCompSelectionData();
					// 从数据库取数据
				}
			}
		}
		return _data;
	}
	
	public void update() {
		
	}
}
