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

	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	protected int getUpdatedSeconds() {
		return 600;
	}
	
	@Override
	protected boolean forceUpdateOnEviction() {
		return false;
	}
}
