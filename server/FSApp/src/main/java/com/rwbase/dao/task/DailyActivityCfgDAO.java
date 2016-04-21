package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.store.StoreCfgDAO;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;

public class DailyActivityCfgDAO extends CfgCsvDao<DailyActivityCfg> {
	public static DailyActivityCfgDAO getInstance() {
		return SpringContextUtil.getBean(DailyActivityCfgDAO.class);
	}

	private HashMap<Integer, DailyActivityCfgEntity> entityMap;
	private List<DailyActivityCfgEntity> allEntitys;

	@Override
	public Map<String, DailyActivityCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("dailyActivity/dailyActivity.csv", DailyActivityCfg.class);
		HashMap<Integer, DailyActivityCfgEntity> entityMap = new HashMap<Integer, DailyActivityCfgEntity>(this.cfgCacheMap.size());
		for (Map.Entry<String, DailyActivityCfg> entry : this.cfgCacheMap.entrySet()) {
			DailyActivityCfg cfg = entry.getValue();
			DailyActivityCfgEntity entity = new DailyActivityCfgEntity(cfg);
			entityMap.put(cfg.getId(), entity);
		}
		this.entityMap = entityMap;
		ArrayList<DailyActivityCfgEntity> list = new ArrayList<DailyActivityCfgEntity>(entityMap.values());
		this.allEntitys = Collections.unmodifiableList(list);
		return cfgCacheMap;
	}

	/**
	 * 获取任务配置实体
	 * 
	 * @param taskId
	 * @return
	 */
	public DailyActivityCfgEntity getCfgEntity(int taskId) {
		return this.entityMap.get(taskId);
	}

	public List<DailyActivityCfgEntity> getAllReadOnlyEntitys() {
		return this.allEntitys;
	}

	public DailyActivityCfg getTaskCfgById(int taskId) {
		DailyActivityCfgEntity entity = this.entityMap.get(taskId);
		return entity == null ? null : entity.getCfg();
	}

	// 根据任务id获得配置文件中的数据
	// public DailyActivityCfg GetTaskCfgById(int taskId) {
	// List<DailyActivityCfg> taskCfgList = getAllCfg();
	// for (DailyActivityCfg taskCfg : taskCfgList) {
	// if (taskCfg.getId() == taskId) {
	// return taskCfg;
	// }
	// }
	// return null;
	// }

}
