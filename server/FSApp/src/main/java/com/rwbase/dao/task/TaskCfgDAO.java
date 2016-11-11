package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

	private List<TaskCfg> initTask;//初始任务列表
	private HashMap<Integer, TaskCfg> map;
	private HashMap<Integer, TaskCfg> preIdMap;//key=上一个任务id
	
	@Override
	public Map<String, TaskCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("task/taskCfg.csv", TaskCfg.class);
		// 初始化好运行时要使用的集合
		List<TaskCfg> list = new ArrayList<TaskCfg>();
		HashMap<Integer, TaskCfg> map = new HashMap<Integer, TaskCfg>();
		HashMap<Integer, TaskCfg> preIdMap = new HashMap<Integer, TaskCfg>();
		for(Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			TaskCfg cfg = cfgCacheMap.get(keyItr.next());
			//TODO 这里可能有未初始化的preId
			map.put(cfg.getId(), cfg);
			preIdMap.put(cfg.getPreTask(), cfg);
			if (cfg.getPreTask() < 0) {
				list.add(cfg);
			}
			String[] allRewards = cfg.getReward().split(",");
			Map<Integer, Integer> rewardMap = new HashMap<Integer, Integer>(allRewards.length, 1.5f);
			String[] singleReward;
			for (String rewardStr : allRewards) {
				singleReward = rewardStr.split("_");
				rewardMap.put(Integer.parseInt(singleReward[0]), Integer.parseInt(singleReward[1]));
			}
			cfg.setRewardMap(rewardMap);
			
			String[] finishParams = cfg.getFinishParam().split("_");
			List<String> finishParamList = new ArrayList<String>(finishParams.length);
			for(String param : finishParams) {
				finishParamList.add(param);
			}
			cfg.setFinishParamList(finishParamList);
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
