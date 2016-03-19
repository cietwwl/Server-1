package com.rwbase.dao.gamble;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.gamble.pojo.TableGamble;

public class TableGambleDAO extends DataKVDao<TableGamble>
{
	private static TableGambleDAO m_instance = new TableGambleDAO();
	public static TableGambleDAO getInstance(){
		if(m_instance == null) {
			m_instance = new TableGambleDAO();
		}
		return m_instance;
	}
}