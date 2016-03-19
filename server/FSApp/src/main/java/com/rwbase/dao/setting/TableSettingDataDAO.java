package com.rwbase.dao.setting;

import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.setting.pojo.TableSettingData;

public class TableSettingDataDAO extends DataKVDao<TableSettingData>
{
	private static TableSettingDataDAO m_instance = new TableSettingDataDAO();
	public static TableSettingDataDAO getInstance()
	{
		if(m_instance == null) 
		{
			m_instance = new TableSettingDataDAO();
		}
		return m_instance;
	}
}
