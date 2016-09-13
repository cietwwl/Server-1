package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.task.pojo.TaskCfg;

public class TaskCfgDAO extends CfgCsvDao<TaskCfg> {
	public static TaskCfgDAO getInstance() {
		return SpringContextUtil.getBean(TaskCfgDAO.class);
	}

	private List<TaskCfg> initTask;
	private HashMap<Integer, TaskCfg> map;
	private HashMap<Integer, TaskCfg> preIdMap;

	@Override
	public Map<String, TaskCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("task/taskCfg.csv", TaskCfg.class);
		// 初始化好运行时要使用的集合
		List<TaskCfg> list = new ArrayList<TaskCfg>();
		HashMap<Integer, TaskCfg> map = new HashMap<Integer, TaskCfg>();
		HashMap<Integer, TaskCfg> preIdMap = new HashMap<Integer, TaskCfg>();
		for (Map.Entry<String, TaskCfg> entry : cfgCacheMap.entrySet()) {
			TaskCfg cfg = entry.getValue();
			//TODO 这里可能有未初始化的preId
			map.put(cfg.getId(), cfg);
			preIdMap.put(cfg.getPreTask(), cfg);
			if (cfg.getPreTask() < 0) {
				list.add(cfg);
			}
		}
		this.map = map;
		this.preIdMap = preIdMap;
		this.initTask = Collections.unmodifiableList(list);
		return cfgCacheMap;
	}

	public List<TaskCfg> getInitList() {
		return initTask;
	}

	public TaskCfg getCfg(int id) {
		return this.map.get(id);
	}

	public TaskCfg getCfgByPreId(int id) {
		return preIdMap.get(id);
	}
}
