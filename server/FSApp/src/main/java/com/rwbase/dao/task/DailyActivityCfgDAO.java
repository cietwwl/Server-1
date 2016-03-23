package com.rwbase.dao.task;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.store.StoreCfgDAO;
import com.rwbase.dao.task.pojo.DailyActivityCfg;

public class DailyActivityCfgDAO extends CfgCsvDao<DailyActivityCfg>
{
	public static DailyActivityCfgDAO getInstance() {
		return SpringContextUtil.getBean(DailyActivityCfgDAO.class);
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
