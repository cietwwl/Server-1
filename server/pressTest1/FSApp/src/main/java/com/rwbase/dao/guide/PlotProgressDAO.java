package com.rwbase.dao.guide;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.guide.pojo.UserPlotProgress;

public class PlotProgressDAO extends DataKVDao<UserPlotProgress> {

	private static PlotProgressDAO instance;

	public static PlotProgressDAO getInstance() {
		if (instance == null) {
			instance = new PlotProgressDAO();
		}
		return instance;
	}
	
}
