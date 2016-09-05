package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;

public class DailyActivityCfgDAO extends CfgCsvDao<DailyActivityCfg> {
	public static DailyActivityCfgDAO getInstance() {
		return SpringContextUtil.getBean(DailyActivityCfgDAO.class);
	}

	/**key=id*/
	private HashMap<Integer, DailyActivityCfgEntity> entityMap;
	private List<DailyActivityCfgEntity> allEntitys;
	
//	private HashSet<Integer> entryKeys = new HashSet<Integer>();
	
	/**key=type(任务类型)*/
	private HashMap<Integer, List<DailyActivityCfgEntity>> allEntrisMap = new HashMap<Integer, List<DailyActivityCfgEntity>>();

	@Override
	public Map<String, DailyActivityCfg> initJsonCfg() {
		
		cfgCacheMap = CfgCsvHelper.readCsv2Map("dailyActivity/dailyActivity.csv", DailyActivityCfg.class);
		HashMap<Integer, DailyActivityCfgEntity> entityMap = new HashMap<Integer, DailyActivityCfgEntity>(this.cfgCacheMap.size());
		for (Map.Entry<String, DailyActivityCfg> entry : this.cfgCacheMap.entrySet()) {
			DailyActivityCfg cfg = entry.getValue();
			DailyActivityCfgEntity entity = new DailyActivityCfgEntity(cfg);
			if(entityMap.containsKey(cfg.getId())){
				//检查配置表数据，id是否重复
				throw new ExceptionInInitializerError("校验dailyActivity.csv表，发现重复的id数据:" + cfg.getId());
			}
			
			entityMap.put(cfg.getId(), entity);
			
			List<DailyActivityCfgEntity> list = allEntrisMap.get(cfg.getTaskType());
			if(list == null){
				list = new ArrayList<DailyActivityCfgEntity>();
				allEntrisMap.put(cfg.getTaskClassify(), list);
			}
			list.add(entity);
			
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

	
	/**
	 * 获取所有任务类型.
	 * <p><font color=red>注意，此方法是copy一份新的集合出来进行操作。</font></p>
	 * @return
	 */
	public HashSet<Integer> getAllTaskType(){
		return new HashSet<Integer>(allEntrisMap.keySet());
	}
	
	/**
	 * 获取相同类型任务
	 * @param type
	 * @return
	 */
	public List<DailyActivityCfgEntity> getCfgEntrisByType(int type){
		return allEntrisMap.get(type);
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
