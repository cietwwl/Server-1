package com.rwbase.dao.task;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;

public class TableDailyActivityItemDAO extends DataKVDao<DailyActivityTaskItem>
{
	private static TableDailyActivityItemDAO m_instance = new TableDailyActivityItemDAO();
	public static TableDailyActivityItemDAO getInstance()
	{
		if(m_instance == null)
		{
			m_instance = new TableDailyActivityItemDAO();
		}
		return m_instance;
	}
}
