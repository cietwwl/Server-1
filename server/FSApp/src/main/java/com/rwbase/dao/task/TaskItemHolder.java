package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.task.pojo.TaskCfg;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class TaskItemHolder {

	final private String userId;
	final private eSynType dataSynType = eSynType.TASK_DATA;

	public TaskItemHolder(String userIdP) {
		userId = userIdP;
	}

	public boolean containsTask(int taskId) {
		Enumeration<TaskItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			TaskItem item = (TaskItem) mapEnum.nextElement();
			if (item.getTaskId() == taskId) {
				return true;
			}
		}
		return false;
	}

	/*
	 * 获取用户已经拥有
	 */
	public List<TaskItem> getItemList() {
		List<TaskItem> itemList = new ArrayList<TaskItem>();
		Enumeration<TaskItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			TaskItem item = (TaskItem) mapEnum.nextElement();
			itemList.add(item);
		}
		// System.out.println("TASK = "+itemList.size());
		return itemList;
	}

	public void updateItem(Player player, TaskItem item) {
		getItemStore().updateItem(item);
		// taskIdMap.put(item.getTaskId(), item);
		ClientDataSynMgr.updateData(player, item, dataSynType, eSynOpType.UPDATE_SINGLE);
	}
	
	public void updateItem(Player player, TaskItem item, boolean isSyn) {
		getItemStore().updateItem(item);
		if(isSyn) ClientDataSynMgr.updateData(player, item, dataSynType, eSynOpType.UPDATE_SINGLE);
	}

	public TaskItem getItem(String itemId) {
		return getItemStore().getItem(itemId);
	}

	public TaskItem getByTaskId(int taskId) {
		Enumeration<TaskItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			TaskItem item = (TaskItem) mapEnum.nextElement();
			if (item.getTaskId() == taskId) {
				return item;
			}
		}
		return null;
	}

	public boolean removeItem(Player player, TaskItem item) {
		boolean success = getItemStore().removeItem(item.getId());
		
		if (success) {
			// taskIdMap.remove(item.getTaskId());
			ClientDataSynMgr.updateData(player, item, dataSynType, eSynOpType.REMOVE_SINGLE);
		}
		return success;
	}
	
	public boolean removeItem(Player player, List<TaskItem> items) {
		if(null == items || items.isEmpty()) return false;
		List<String> ids = new ArrayList<String>();
		for(TaskItem item : items){
			ids.add(item.getId());
		}
		List<String> successIds = getItemStore().removeItem(ids);
		if(null == successIds || successIds.isEmpty()) return false;
		return true;
	}

	public boolean addItem(Player player, TaskItem item, boolean doSyn) {
		item.setId(buildId(item));
		item.setUserId(userId);
		boolean addSuccess = getItemStore().addItem(item);
		if (addSuccess && doSyn) {
			// taskIdMap.put(item.getTaskId(), item);
			ClientDataSynMgr.updateData(player, item, dataSynType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}

	public boolean addItemList(Player player, List<TaskItem> itemList, boolean doSyn) {
		int size = itemList.size();
		for (int i = 0; i < size; i++) {
			TaskItem item = itemList.get(i);
			item.setId(buildId(item));
			item.setUserId(userId);
		}
		boolean addSuccess = false;
		try {
			addSuccess = getItemStore().addItem(itemList);
		} catch (DuplicatedKeyException e) {
			GameLog.error("TaskItemHolder", "#addItemList()", "批量添加任务出现重复主键", e);
		}
		if (addSuccess) {
			// taskIdMap.put(item.getTaskId(), item);
			ClientDataSynMgr.updateDataList(player, getItemList(), dataSynType, eSynOpType.UPDATE_LIST);
		}
		return addSuccess;
	}

	private String buildId(TaskItem item) {
		return userId + "_" + item.getTaskId();
	}

	public void synAllData(Player player, int version) {
		List<TaskItem> itemList = getItemList();
		List<TaskItem> removeList = new ArrayList<TaskItem>();
		TaskCfgDAO cfgDAO = TaskCfgDAO.getInstance();
		for (TaskItem taskItem : itemList) {
			//检查数据库数据及配置表是否对应,配置表里没有的不发送到前端
			TaskCfg cfg = cfgDAO.getCfg(taskItem.getTaskId());
			if(cfg == null){
				removeList.add(taskItem);
				GameLog.error(LogModule.COMMON, "TaskItemHolder[synAllData]", "同步任务数据到客户端，发现任务不存在，任务id:" + taskItem.getTaskId(), null);
			}
//			System.out.println("+++++++++，id:" + taskItem.getTaskId() + ",desc:" + cfg.getDesc() );
		}
		itemList.removeAll(removeList);
		ClientDataSynMgr.synDataList(player, itemList, dataSynType, eSynOpType.UPDATE_LIST);
	}

	public void flush() {
		getItemStore().flush();
	}

	private MapItemStore<TaskItem> getItemStore() {
		MapItemStoreCache<TaskItem> taskItemCache = MapItemStoreFactory.getTaskItemCache();
		return taskItemCache.getMapItemStore(userId, TaskItem.class);
	}

}