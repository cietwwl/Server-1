package com.rwbase.dao.task;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.dao.task.pojo.DailyActivityTaskItem;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class DailyActivityHolder {

	private Player player; //
	private final String userId;
	private final eSynType synType = eSynType.DailyActivity;
	private TableDailyActivityItemDAO dao = TableDailyActivityItemDAO.getInstance();

	// private DailyActivityTaskItem m_taskItem = null;

	public DailyActivityHolder(Player playerP) {
		player = playerP;
		this.userId = playerP.getUserId();
		// m_taskItem = dao.get(player.getUserId());
		// if (m_taskItem == null) {
		// m_taskItem = new DailyActivityTaskItem();
		// m_taskItem.getRemoveTaskList().clear();
		// m_taskItem.setUserId(player.getUserId());
		// save();
		// }
	}

	public DailyActivityTaskItem getTaskItem() {
		return dao.get(player.getUserId());
	}

	public void updateItem(Player player) {
		DailyActivityTaskItem taskItem = dao.get(userId);
		if (taskItem != null) {
			ClientDataSynMgr.updateData(player, taskItem, synType, eSynOpType.UPDATE_SINGLE);
		}
	}

	/**
	 * public DailyActivityTaskItem getItem(String itemId){ return
	 * itemStore.getItem(itemId); }
	 ***/
	public boolean removeItem(Player player) {

		/***
		 * boolean success = itemStore.removeItem(item.getId()); if(success){
		 * ClientDataSynMgr.synData(player, item, inlaySynType,
		 * eSynOpType.REMOVE_SINGLE); } return success;
		 ***/

		return true;
	}

	public boolean addItem(Player player) {

		// boolean addSuccess = itemStore.addItem(item);
		// if(addSuccess){
		// ClientDataSynMgr.synData(player, item, inlaySynType,
		// eSynOpType.ADD_SINGLE);
		// }
		// return addSuccess;

		return true;
	}

	public void synAllData(Player player, int version) {
		boolean needSyn = true;
		if (needSyn) {
			// List<InlayItem> itemList = getItemList();
			// ClientDataSynMgr.synDataList(player, itemList, inlaySynType,
			// eSynOpType.UPDATE_LIST);
		}
	}

	public void save() {
		// dao.update(m_taskItem);
		// dao.update(t)
	}

}
