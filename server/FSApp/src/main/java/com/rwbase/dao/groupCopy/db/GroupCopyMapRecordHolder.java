package com.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;

public class GroupCopyMapRecordHolder {

	final private String groupId;
	final private eSynType synType = eSynType.GROUP_COPY_MAP;

	final private AtomicInteger dataVersion = new AtomicInteger(1);

	public GroupCopyMapRecordHolder(String groupIdP) {
		groupId = groupIdP;
		checkAndInitData();
	}

	public void checkAndInitData() {
		List<GroupCopyMapCfg> list = GroupCopyMapCfgDao.getInstance().getAllCfg();

		List<GroupCopyMapRecord> addList = null;

		for (GroupCopyMapCfg cfg : list) {
			GroupCopyMapRecord record = getItemByID(cfg.getId());
			if (record == null) {
				record = createRecord(cfg.getId());
				if (addList == null) {
					addList = new ArrayList<GroupCopyMapRecord>();
				}
				addList.add(record);
			}
		}

		if (addList != null) {
			try {
				getItemStore().addItem(addList);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private GroupCopyMapRecord createRecord(String mapID) {
		GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapID);
		GroupCopyMapRecord record = null;
		try {
			record = new GroupCopyMapRecord();
			record.setId(getRecordID(mapID));
			record.setChaterID(mapID);
			record.setGroupId(groupId);
			record.setCurLevelID(cfg.getStartLvID());
			record.setStatus(GroupCopyMapStatus.LOCKING);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return record;
	}

	private void checkMapState(GroupCopyMapRecord record) {
		if (record.getStatus() == GroupCopyMapStatus.LOCKING) {
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(record.getChaterID());
			Group group = GroupBM.getInstance().get(groupId);
			int groupLevel = group.getGroupBaseDataMgr().getGroupData().getGroupLevel();
			if (groupLevel >= cfg.getUnLockLv()) {
				record.setStatus(GroupCopyMapStatus.NOTSTART);
				getItemStore().updateItem(record);
			}
		}
	}

	public List<GroupCopyMapRecord> getItemList() {
		List<GroupCopyMapRecord> itemList = new ArrayList<GroupCopyMapRecord>();
		List<GroupCopyMapCfg> allCfg = GroupCopyMapCfgDao.getInstance().getAllCfg();
		GroupCopyMapRecord item;
		for (GroupCopyMapCfg cfg : allCfg) {
			item = getItemByID(cfg.getId());
			if (item != null) {
				itemList.add(item);
			}
		}

		return itemList;
	}

	/**
	 * 如果player为null,则不更新到前端
	 * 
	 * @param player
	 * @param item
	 * @return
	 */
	public boolean updateItem(Player player, GroupCopyMapRecord item) {
		boolean suc = getItemStore().updateItem(item);
		update();
		if (player != null) {
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
		}
		return suc;
	}

	public GroupCopyMapRecord getItemByID(String itemId) {
		return getItemStore().getItem(getRecordID(itemId));
	}

	private String getRecordID(String itemID) {
		return itemID + "_" + groupId;
	}

	public void synAllData(Player player, int version) {
		List<GroupCopyMapRecord> itemList = getItemList();
		// 在这里做一下检查
		for (GroupCopyMapRecord record : itemList) {
			checkMapState(record);
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private void update() {
		dataVersion.incrementAndGet();
	}

	private MapItemStore<GroupCopyMapRecord> getItemStore() {
		MapItemStoreCache<GroupCopyMapRecord> itemStoreCache = MapItemStoreFactory.getGroupCopyMapRecordCache();
		return itemStoreCache.getMapItemStore(groupId, GroupCopyMapRecord.class);
	}

	public void checkDamageRank(String chaterID, GroupCopyArmyDamageInfo damageInfo) {
		boolean suc = getItemByID(chaterID).checkOrAddDamageRank(damageInfo);
		if (suc) {
			// TODO 这个数据暂时没有向前端同步，后面再考虑是否开放
			update();
		}
	}

}
