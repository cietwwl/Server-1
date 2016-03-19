package com.rwbase.dao.task;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.task.pojo.DailyActivityCfg;

public class DailyActivityCfgDAO extends CfgCsvDao<DailyActivityCfg>
{
	private static DailyActivityCfgDAO m_instance;
	public static DailyActivityCfgDAO getInstance()
	{
		if(m_instance == null)
		{
			m_instance = new DailyActivityCfgDAO();
		}
		return m_instance;
	}

	@Override
	public Map<String, DailyActivityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("dailyActivity/dailyActivity.csv",DailyActivityCfg.class);
		return cfgCacheMap;
	}
	
	//根据任务id获得配置文件中的数据
	public DailyActivityCfg GetTaskCfgById(int taskId)
	{
		List<DailyActivityCfg> taskCfgList = getAllCfg();
		for(DailyActivityCfg taskCfg : taskCfgList)
		{
			if(taskCfg.getId() == taskId)
			{
				return taskCfg;
			}
		}
		return null;
	}

}
