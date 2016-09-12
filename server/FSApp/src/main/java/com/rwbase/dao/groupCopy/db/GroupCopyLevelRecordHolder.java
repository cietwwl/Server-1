package com.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.bm.groupCopy.GroupCopyLevelBL;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GroupCopyLevelRecordHolder {

	final private String groupId;
	final private eSynType synType = eSynType.GROUP_COPY_LEVEL;

	final private AtomicInteger dataVersion = new AtomicInteger(0);

	public GroupCopyLevelRecordHolder(String groupIdP) {
		groupId = groupIdP;
		checkAndInitData();
	}

	
	
	/**
	 * 检查副本配置并初始化
	 */
	public void checkAndInitData() {
		List<GroupCopyLevelCfg> list = GroupCopyLevelCfgDao.getInstance().getAllCfg();
		ArrayList<GroupCopyLevelRecord> addList = null;
		for (GroupCopyLevelCfg cfg : list) {
			GroupCopyLevelRecord record = getByLevel(cfg.getId());
			if (record == null) {
				record = createLevelRecord(cfg.getId());
				if (addList == null) {
					addList = new ArrayList<GroupCopyLevelRecord>();
				}
				addList.add(record);
			} else if (record.getProgress() == null) {
				record.setProgress(GroupCopyLevelBL.createProgress(cfg.getId()));
				getItemStore().updateItem(record);
				
			}else{
				//检查已经存在怪物的关卡，怪物数据是否与配置表一致，如果不匹配，则全部怪物重新生成   ----by Alex 8.30.2016
				GroupCopyProgress progress = record.getProgress();
				double percent = progress.getProgress();
				if(percent == 1.0){
					continue;
				}
				List<GroupCopyMonsterSynStruct> datas = progress.getmDatas();
				boolean reset = false;
				for (GroupCopyMonsterSynStruct struct : datas) {
					MonsterCfg monster = MonsterCfgDao.getInstance().getConfig(struct.getId());
					if(monster == null){
						reset = true;
						break;
					}
				}
				
				if(reset){
					GameLog.info(LogModule.GroupCopy.name(), "GroupCopyLevelRecordHolder", "帮派副本发现副本[" + cfg.getId() +"]里的怪物与配置不匹配，进行重新生成");
					GroupCopyProgress p = GroupCopyLevelBL.createProgress(cfg.getId());
					p.calculateMonsterFromProgress(percent);//TODO 是否还要处理一下章节的进度？
					record.setProgress(p);
					getItemStore().updateItem(record);
				}
			}
		}
		if (addList != null) {
			try {
				getItemStore().addItem(addList);
			} catch (DuplicatedKeyException e) {
				GameLog.error(LogModule.GroupCopy, GroupCopyLevelRecordHolder.class.getName(), "检查副本数据时发现异常", e);
				e.printStackTrace();
			}
		}
	}

	private String getRecordID(String levelID) {
		return groupId + "_" + levelID;
	}

	private GroupCopyLevelRecord createLevelRecord(String level) {
		GroupCopyLevelRecord lvData = null;
		try {
			lvData = new GroupCopyLevelRecord();
			lvData.setId(getRecordID(level));
			lvData.setLevelID(level);
			lvData.setGroupId(groupId);
			// 这里还要初始化一下怪物信息
			GroupCopyProgress p = GroupCopyLevelBL.createProgress(level);

			lvData.setProgress(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lvData;
	}

	/*
	 * 获取帮派副本关卡数据
	 */
	public List<GroupCopyLevelRecord> getItemList() {
		List<GroupCopyLevelRecord> itemList = new ArrayList<GroupCopyLevelRecord>();
		MapItemStore<GroupCopyLevelRecord> itemStore = getItemStore();
		List<GroupCopyLevelCfg> allCfg = GroupCopyLevelCfgDao.getInstance().getAllCfg();
		GroupCopyLevelRecord item;
		for (GroupCopyLevelCfg cfg : allCfg) {
			item = itemStore.getItem(getRecordID(cfg.getId()));
			if (item != null) {
				itemList.add(item);
			}
		}

		return itemList;
	}

	/**
	 * 更新帮派副本
	 * 
	 * @param player
	 * @param item
	 * @return
	 */
	public boolean updateItem(Player player, GroupCopyLevelRecord item) {
		boolean success = getItemStore().updateItem(item);
		if (success) {
			updateVersion();
			ClientDataSynMgr.synData(player, item, synType, eSynOpType.UPDATE_SINGLE, dataVersion.get());
		}
		return success;
	}

	public GroupCopyLevelRecord getByLevel(String level) {
		return getItemStore().getItem(getRecordID(level));
	}

	public void synSingleData(Player player, String level) {
		GroupCopyLevelRecord groupRecord = getByLevel(level);
		ClientDataSynMgr.synData(player, groupRecord, synType, eSynOpType.UPDATE_SINGLE, dataVersion.get());
	}

	public void synAllData(Player player, int version) {

		List<GroupCopyLevelRecord> groupRecordList = getItemList();
		ClientDataSynMgr.synDataList(player, groupRecordList, synType, eSynOpType.UPDATE_LIST, dataVersion.get());
	}

	private void updateVersion() {
		dataVersion.incrementAndGet();
	}

	private MapItemStore<GroupCopyLevelRecord> getItemStore() {
		MapItemStoreCache<GroupCopyLevelRecord> itemStoreCache = MapItemStoreFactory.getGroupCopyLevelRecordCache();
		return itemStoreCache.getMapItemStore(groupId, GroupCopyLevelRecord.class);
	}

	public void resetLevelData(Player player, Set<String> set) {

		List<GroupCopyLevelRecord> list = new ArrayList<GroupCopyLevelRecord>();
		GroupCopyLevelRecord record;
		for (String id : set) {
			record = getByLevel(id);
			record.resetLevelData();
			getItemStore().updateItem(record);
			list.add(record);
		}
		updateVersion();
		ClientDataSynMgr.synDataList(player, list, synType, eSynOpType.UPDATE_LIST, dataVersion.get());
	}

}
