package com.rwbase.dao.task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class TaskItemHolder {

	final private String userId; 
	final private eSynType dataSynType = eSynType.TASK_DATA;

	public TaskItemHolder(String userIdP) {
		userId = userIdP;
	}
	
	public boolean containsTask(int taskId){
		Enumeration<TaskItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			TaskItem item = (TaskItem) mapEnum.nextElement();
			if(item.getTaskId() == taskId){
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

		return itemList;
	}

	public void updateItem(Player player, TaskItem item) {
		getItemStore().updateItem(item);
		//taskIdMap.put(item.getTaskId(), item);
		ClientDataSynMgr.updateData(player, item, dataSynType, eSynOpType.UPDATE_SINGLE);
	}

	public TaskItem getItem(String itemId) {
		return getItemStore().getItem(itemId);
	}
	
	public TaskItem getByTaskId(int taskId) {
		Enumeration<TaskItem> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			TaskItem item = (TaskItem) mapEnum.nextElement();
			if(item.getTaskId() == taskId){
				return item;
			}
		}
		return null;
	}

	public boolean removeItem(Player player, TaskItem item) {
		boolean success = getItemStore().removeItem(item.getId());
		if (success) {
			//taskIdMap.remove(item.getTaskId());
			ClientDataSynMgr.updateData(player, item, dataSynType, eSynOpType.REMOVE_SINGLE);
		}
		return success;
	}
	

	public boolean addItem(Player player, TaskItem item, boolean doSyn) {
		item.setId(buildId(item));
		item.setUserId(userId);
		boolean addSuccess = getItemStore().addItem(item);
		if (addSuccess) {
			//taskIdMap.put(item.getTaskId(), item);
			ClientDataSynMgr.updateData(player, item, dataSynType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	private String buildId(TaskItem item){
		return userId+"_"+item.getTaskId();
	}


	public void synAllData(Player player, int version) {
		List<TaskItem> itemList = getItemList();
		ClientDataSynMgr.synDataList(player, itemList, dataSynType, eSynOpType.UPDATE_LIST);
	}

	public void flush() {
		getItemStore().flush();
	}
	
	private MapItemStore<TaskItem> getItemStore(){
		MapItemStoreCache<TaskItem> taskItemCache = MapItemStoreFactory.getTaskItemCache();
		return taskItemCache.getMapItemStore(userId, TaskItem.class);
	}
	
}