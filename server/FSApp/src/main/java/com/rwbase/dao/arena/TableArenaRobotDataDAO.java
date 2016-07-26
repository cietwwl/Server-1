package com.rwbase.dao.arena;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.arena.pojo.ArenaRobotData;

/*
 * @author HC
 * @date 2016年7月14日 下午8:09:32
 * @Description 
 */
public class TableArenaRobotDataDAO extends DataKVDao<ArenaRobotData> {

	private static TableArenaRobotDataDAO dao = new TableArenaRobotDataDAO();

	public static TableArenaRobotDataDAO getDAO() {
		return dao;
	}

	TableArenaRobotDataDAO() {
	}
}