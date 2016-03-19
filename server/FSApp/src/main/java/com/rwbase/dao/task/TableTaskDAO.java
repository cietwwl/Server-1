package com.rwbase.dao.task;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.task.pojo.TableTask;

public class TableTaskDAO extends DataKVDao<TableTask> {
	private static TableTaskDAO instance = new TableTaskDAO();
	public static TableTaskDAO getInstance(){
		return instance;
	}
}
