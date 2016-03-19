package com.rwbase.dao.globalData;


import java.util.List;

import com.rw.fsutil.cacheDao.DataRdbDao;

public class GlobalDataDAO extends DataRdbDao<TableGlobalData> {
	private static GlobalDataDAO m_instance = new GlobalDataDAO();
	
	public GlobalDataDAO(){
		
	}
	
	public static GlobalDataDAO getInstance(){
		return m_instance;
	}
	
	public static TableGlobalData getTableGlobalData()
	{
		List<TableGlobalData> list=m_instance.getAll();
		if(list==null||list.size()==0)
		{
			TableGlobalData tableGlobalData=new TableGlobalData();
			tableGlobalData.setServerId(0);
			tableGlobalData.setGulidIndex(0);
			m_instance.saveOrUpdate(tableGlobalData);
			return tableGlobalData;
		}
		
		return list.get(0);
	}
	
	public static void updata(TableGlobalData tableGlobalData)
	{
		m_instance.saveOrUpdate(tableGlobalData);
	}
	
	
}
