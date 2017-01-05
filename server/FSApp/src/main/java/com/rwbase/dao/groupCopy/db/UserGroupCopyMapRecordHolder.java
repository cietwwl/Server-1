package com.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class UserGroupCopyMapRecordHolder {

	final private String userId;

	final private AtomicInteger dataVersion = new AtomicInteger(0);

	private final eSynType synType = eSynType.USE_GROUP_COPY_DATA;

	public UserGroupCopyMapRecordHolder(String userID) {
		userId = userID;
	}

	public void checkAndInitData() {
		List<GroupCopyMapCfg> allCfg = GroupCopyMapCfgDao.getInstance().getAllCfg();
		ArrayList<UserGroupCopyMapRecord> addList = null;
		MapItemStore<UserGroupCopyMapRecord> itemStore = getItemStore();
		for (int i = allCfg.size(); --i >= 0;) {
			GroupCopyMapCfg cfg = allCfg.get(i);
			UserGroupCopyMapRecord record = itemStore.getItem(getRecordID(cfg.getId()));
			if (record == null) {
				if (addList == null) {
					addList = new ArrayList<UserGroupCopyMapRecord>();
				}
				record = createRecord(cfg);
				addList.add(record);
			}
		}
		if (addList != null) {
			try {
				itemStore.addItem(addList);
			} catch (DuplicatedKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private UserGroupCopyMapRecord createRecord(GroupCopyMapCfg cfg) {
		UserGroupCopyMapRecord record = new UserGroupCopyMapRecord();
		record.setId(getRecordID(cfg.getId()));
		record.setLeftFightCount(cfg.getEnterCount());
		record.setUserId(userId);
		record.setChaterID(cfg.getId());
		return record;
	}

	public List<UserGroupCopyMapRecord> getItemList() {

		List<UserGroupCopyMapRecord> itemList = new ArrayList<UserGroupCopyMapRecord>();
		Enumeration<UserGroupCopyMapRecord> mapEnum = getItemStore().getEnum();
		while (mapEnum.hasMoreElements()) {
			UserGroupCopyMapRecord item = (UserGroupCopyMapRecord) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	public boolean updateItem(Player player, UserGroupCopyMapRecord item) {
		boolean success = getItemStore().updateItem(item);
		if (success) {
			update();
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		}
		return success;
	}

	public UserGroupCopyMapRecord getItemByID(String itemId) {
		return getItemStore().getItem(getRecordID(itemId));
	}

	/**
	 * 主键id
	 * 
	 * @param id
	 * @return
	 */
	private String getRecordID(String id) {
		return userId + "_" + id;
	}

	private void update() {
		dataVersion.incrementAndGet();
	}

	public int getVersion() {
		return dataVersion.get();
	}

	private MapItemStore<UserGroupCopyMapRecord> getItemStore() {
		MapItemStoreCache<UserGroupCopyMapRecord> itemStoreCache = MapItemStoreFactory.getUserGroupCopyLevelRecordCache();
		return itemStoreCache.getMapItemStore(userId, UserGroupCopyMapRecord.class);
	}

	public void resetFightCount() {
		List<UserGroupCopyMapRecord> list = getItemList();
		List<String> idList = new ArrayList<String>();
		GroupCopyMapCfgDao instance = GroupCopyMapCfgDao.getInstance();
		for (UserGroupCopyMapRecord record : list) {
			GroupCopyMapCfg cfg = instance.getCfgById(record.getChaterID());
			record.setLeftFightCount(cfg.getEnterCount());
			idList.add(record.getId());
		}
		getItemStore().updateItems(idList);
		update();
	}

	public void syncData(Player player) {
		List<UserGroupCopyMapRecord> list = getItemList();
		if (!list.isEmpty()) {
			ClientDataSynMgr.synDataList(player, getItemList(), synType, eSynOpType.UPDATE_LIST);
		}
	}

	public void setFigntCount(int count, Player player) {
		List<UserGroupCopyMapRecord> list = getItemList();
		for (UserGroupCopyMapRecord record : list) {
			record.setLeftFightCount(count);
		}
		update();
		syncData(player);
	}
}
