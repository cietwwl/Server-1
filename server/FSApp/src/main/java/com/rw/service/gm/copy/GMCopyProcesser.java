package com.rw.service.gm.copy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.readonly.CopyCfgIF;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.cfg.MapCfg;
import com.rwbase.dao.copy.cfg.MapCfgDAO;
import com.rwbase.dao.copy.pojo.CopyLevelRecord;
import com.rwproto.BattleTowerServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MsgDef.Command;

public class GMCopyProcesser {
	
	private static List<CopyCfgIF> getAllCopyCfg(int endMapId) {
		MapCfgDAO mapCfgDAO = MapCfgDAO.getInstance();
		CopyCfgDAO cfgDAO = CopyCfgDAO.getInstance();
		List<CopyCfgIF> list = new ArrayList<CopyCfgIF>();
		for (int i = 1001; i <= endMapId; i++) {
			MapCfg mapCfg = mapCfgDAO.getCfg(i);
			if (mapCfg != null) {
				int start = mapCfg.getStartLevelId();
				int end = mapCfg.getEndLevelId();
				for (int levelId = start; levelId <= end; levelId++) {
					CopyCfg copyCfg = cfgDAO.getCfg(levelId);
					if (copyCfg != null) {
						list.add(copyCfg);
					}
				}
			}
		}
		return list;
	}
	
	private static MapItemStore<CopyLevelRecord> getCopyLevelRecord(String userId){
		MapItemStoreCache<CopyLevelRecord> cache = MapItemStoreFactory.getCopyLevelRecordCache();
		return cache.getMapItemStore(userId, CopyLevelRecord.class);
	}
	
	private static CopyLevelRecord getLevelRecord(Player player, int levelID, MapItemStore<CopyLevelRecord> levelRecordStore, List<CopyLevelRecord> newList, List<String> updateList) {
		String userId = player.getUserId();
		String recordId = userId + "_" + levelID;
		CopyLevelRecord copyLevelRecord = levelRecordStore.getItem(recordId);
		if (copyLevelRecord == null) {
			copyLevelRecord = new CopyLevelRecord();

			copyLevelRecord.setId(recordId);
			copyLevelRecord.setLevelId(levelID);
			copyLevelRecord.setUserId(userId);
			copyLevelRecord.setFirst(true);
			newList.add(copyLevelRecord);
		} else {
			updateList.add(copyLevelRecord.getId());
		}
		return copyLevelRecord;
	}
	
	private static String updateLevelRecord(Player player, CopyCfgIF copyCfg, int nPassStar, int times, List<CopyLevelRecord> list, List<String> updateList,
			MapItemStore<CopyLevelRecord> levelRecordStore) {

		String strRecord = null;
		if (copyCfg != null) {
			CopyLevelRecord copyLevelRecord = getLevelRecord(player, copyCfg.getLevelID(), levelRecordStore, list, updateList);
			if (copyLevelRecord != null) {
				if (copyLevelRecord.isFirst()) {
					copyLevelRecord.setFirst(false);
				}

				if (nPassStar > copyLevelRecord.getPassStar()) {
					copyLevelRecord.setPassStar(nPassStar);
				}
				copyLevelRecord.setCurrentCount(copyLevelRecord.getCurrentCount() + times);

				strRecord = copyLevelRecord.toClientData();
			}
		}

		return strRecord;
	}

	public static boolean processSetMap(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			player.NotifyCommonMsg("命令有误，请重新输入");
			return false;
		}
		String id = arrCommandContents[0];
		if (id == null) {
			player.NotifyCommonMsg("mapid有错");
			return false;
		}
		MapCfg map = (MapCfg) MapCfgDAO.getInstance().getCfgById(id);
		if (map == null) {
			player.NotifyCommonMsg("mapid有错");
			return false;
		}

		int nMapID = map.getId();
		List<CopyCfgIF> allCopyCfgs = getAllCopyCfg(nMapID);
		List<CopyLevelRecord> list = new ArrayList<CopyLevelRecord>();
		List<String> updateList = new ArrayList<String>();
		MapItemStore<CopyLevelRecord> copyLevelRecord = getCopyLevelRecord(player.getUserId());
		for (int i = 0, size = allCopyCfgs.size(); i < size; i++) {
			updateLevelRecord(player, allCopyCfgs.get(i), 3, 1, list, updateList, copyLevelRecord);
		}
		try {
			copyLevelRecord.updateItems(list, updateList);
		} catch (DuplicatedKeyException e) {
			e.printStackTrace();
		}
		List<CopyLevelRecord> synList = new ArrayList<CopyLevelRecord>();
		Enumeration<CopyLevelRecord> enumeration = copyLevelRecord.getEnum();
		while(enumeration.hasMoreElements()) {
			synList.add(enumeration.nextElement());
		}
		ClientDataSynMgr.synDataList(player, synList, eSynType.COPY_LEVEL_RECORD, eSynOpType.UPDATE_LIST);
		player.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		copyResponse.setEResultType(EResultType.GM_SETSUCCESS);
		player.SendMsg(Command.MSG_CopyService, copyResponse.build().toByteString());
		return true;
	}
}
