package com.playerdata;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.CopyLevelRecord;
import com.rwbase.dao.copypve.CopyType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
//import com.rwbase.common.MapItemStoreCache
public class CopyLevelRecordHolder {

	private String userId;
	//private MapItemStore<CopyLevelRecord> levelRecordStore;

	public CopyLevelRecordHolder(String roleIdP) {
		userId = roleIdP;
	}
	
	/*
	 * 同步关卡记录所有数据
	 */
	public void SynAll(Player player)
	{
		Enumeration<CopyLevelRecord> copyLevelEnum = getCopyLevelRecord().getEnum();
		List<CopyLevelRecord> updatedList = new ArrayList<CopyLevelRecord>();
		boolean isModified = false;
		while (copyLevelEnum.hasMoreElements()) {
			updatedList.add((CopyLevelRecord) copyLevelEnum.nextElement());
			isModified = true;
		}
		if(isModified){
			ClientDataSynMgr.updateDataList(player, updatedList, eSynType.COPY_LEVEL_RECORD, eSynOpType.UPDATE_LIST);			
		}else{			
			ClientDataSynMgr.synDataList(player, updatedList, eSynType.COPY_LEVEL_RECORD, eSynOpType.UPDATE_LIST);
		}
	}

	/*
	 * 获取当前用户的副本关卡记录
	 */
	public List<CopyLevelRecord> getLevelRecordList() {
		List<CopyLevelRecord> userLevelRecords = new ArrayList<CopyLevelRecord>();

		Enumeration<CopyLevelRecord> copyLevelEnum = getCopyLevelRecord().getEnum();

		while (copyLevelEnum.hasMoreElements()) {
			CopyLevelRecord copyRecord = (CopyLevelRecord) copyLevelEnum.nextElement();
			userLevelRecords.add(copyRecord);
		}
		return userLevelRecords;
	}

	/**
	 * 更新关卡记录
	 * 
	 * @param strLevelID
	 * @param nPassStar
	 * @param times
	 * @return
	 */
	public String updateLevelRecord(Player player, int levelID, int nPassStar, int times) {

		String strRecord = null;
		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelID);
		if (copyCfg != null) {
			CopyLevelRecord copyLevelRecord = getLevelRecord(player, levelID);
			if (copyLevelRecord != null) {
				if(copyLevelRecord.isFirst()){
					copyLevelRecord.setFirst(false);
				}
				
				if (nPassStar > copyLevelRecord.getPassStar()) {
					copyLevelRecord.setPassStar(nPassStar);
				}
				copyLevelRecord.setCurrentCount(copyLevelRecord.getCurrentCount() + times);

				getCopyLevelRecord().updateItem(copyLevelRecord);

				strRecord = copyLevelRecord.toClientData();
				ClientDataSynMgr.updateData(player, copyLevelRecord, eSynType.COPY_LEVEL_RECORD, eSynOpType.UPDATE_SINGLE);
			}
		}

		return strRecord;
	}

	public String buyLevel(Player player, int levelID) {
		CopyLevelRecord copyLevelRecord = getLevelRecord(player, levelID);
		copyLevelRecord.setCurrentCount(0);
		copyLevelRecord.setBuyCount(copyLevelRecord.getBuyCount() + 1);
		getCopyLevelRecord().updateItem(copyLevelRecord);
		ClientDataSynMgr.updateData(player, copyLevelRecord, eSynType.COPY_LEVEL_RECORD, eSynOpType.UPDATE_SINGLE);
		return copyLevelRecord.toClientData();
	}

	public CopyLevelRecord getLevelRecord(Player player, int levelID) {
		String recordId = getCopyLevelRecordId(levelID);
		MapItemStore<CopyLevelRecord> levelRecordStore = getCopyLevelRecord();
		CopyLevelRecord copyLevelRecord = levelRecordStore.getItem(recordId);
		boolean success = true;
		if (copyLevelRecord == null) {
			copyLevelRecord = new CopyLevelRecord();

			copyLevelRecord.setId(recordId);
			copyLevelRecord.setLevelId(levelID);
			copyLevelRecord.setUserId(userId);
			copyLevelRecord.setFirst(true);
			success = levelRecordStore.addItem(copyLevelRecord);
			if (success) {
				ClientDataSynMgr.updateData(player, copyLevelRecord, eSynType.COPY_LEVEL_RECORD, eSynOpType.ADD_SINGLE);
			}
		}
		if (success) {
			return copyLevelRecord;
		} else {
			return null;
		}
	}

	private String getCopyLevelRecordId(int levelId) {
		return userId + "_" + levelId;
	}

	/*
	 * 重置关卡记录的次数
	 */
	public void resetAllCopyRecord(Player player) {
		MapItemStore<CopyLevelRecord> levelRecordStore = getCopyLevelRecord();
		Enumeration<CopyLevelRecord> copyLevelEnum = levelRecordStore.getEnum();
		List<CopyLevelRecord> updatedList = new ArrayList<CopyLevelRecord>();
		while (copyLevelEnum.hasMoreElements()) {
			CopyLevelRecord copyRecord = (CopyLevelRecord) copyLevelEnum.nextElement();

			int levelId = copyRecord.getLevelId();
			CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
			if (copyCfg == null) {

				GameLog.error(LogModule.COPY.getName(), userId, "CopyLevelRecordHolder[resetAllCopyRecord] CopyCfg not found, copyId:" + levelId, null);
				continue;
			}
			if (IsCanBuyTimes(copyCfg)) {
				copyRecord.setBuyCount(0);
			}
			copyRecord.setCurrentCount(0);
			levelRecordStore.updateItem(copyRecord);
			updatedList.add(copyRecord);
		}

		ClientDataSynMgr.updateDataList(player, updatedList, eSynType.COPY_LEVEL_RECORD, eSynOpType.UPDATE_LIST);

	}

	private boolean IsCanBuyTimes(CopyCfg copyCfg) {
		boolean result;
		switch (copyCfg.getLevelType()) {
		case CopyType.COPY_TYPE_ELITE:
			result = true;
			break;
		default:
			result = false;
			break;
		}
		return result;
	}
	
	public void flush(){
		getCopyLevelRecord().flush();
	}

	private MapItemStore<CopyLevelRecord> getCopyLevelRecord(){
		MapItemStoreCache<CopyLevelRecord> cache = MapItemStoreFactory.getCopyLevelRecordCache();
		return cache.getMapItemStore(userId, CopyLevelRecord.class);
	}
}


