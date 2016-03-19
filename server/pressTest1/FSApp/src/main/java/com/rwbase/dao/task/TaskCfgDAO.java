package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.task.pojo.TaskCfg;

public class TaskCfgDAO extends CfgCsvDao<TaskCfg> {
	private static TaskCfgDAO m_instance = new TaskCfgDAO();
	public static TaskCfgDAO getInstance(){
		return m_instance;
	}

	@Override
	public Map<String, TaskCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("task/taskCfg.csv",TaskCfg.class);
		return cfgCacheMap;
	}	
	
	public List<TaskCfg> getInitList(){
		List<TaskCfg> allarr = super.getAllCfg();
		List<TaskCfg> list = new ArrayList<TaskCfg>();
		for (TaskCfg cfg : allarr) {
			if(cfg.getPreTask() < 0){
				list.add(cfg);
			}
		}
		return list;
	}
	
	public TaskCfg getCfg(int id){
		List<TaskCfg> allarr = super.getAllCfg();
		for (TaskCfg cfg : allarr) {
			if(cfg.getId() == id){
				return cfg;
			}
		}
		return null;
	}
	
	public TaskCfg getCfgByPreId(int id){
		List<TaskCfg> allarr = super.getAllCfg();
		for (TaskCfg cfg : allarr) {
			if(cfg.getPreTask() == id){
				return cfg;
			}
		}
		return null;
	}
}
