package com.rw.service.PeakArena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.log.GameLog;
import com.rw.dataaccess.attachment.PlayerExtPropertyType;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;

public class PeakArenaRecordHolder {

	public static PeakArenaRecordHolder _instance = new PeakArenaRecordHolder();
	
	public static PeakArenaRecordHolder getInstance() {
		return _instance;
	}

	public List<PeakRecordInfo> getRecordList(String ownerId) {
		RoleExtPropertyStore<PeakRecordInfo> itemStore = this.getMapItemStroe(ownerId);
		int size;
		if((size = itemStore.getSize()) > 0) {
			List<PeakRecordInfo> list = new ArrayList<PeakRecordInfo>(size);
			Enumeration<PeakRecordInfo> enm = itemStore.getExtPropertyEnumeration();
			while(enm.hasMoreElements()) {
				list.add(enm.nextElement());
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	RoleExtPropertyStore<PeakRecordInfo> getMapItemStroe(String ownerId) {
		RoleExtPropertyStoreCache<PeakRecordInfo> playerExtCache = RoleExtPropertyFactory.getPlayerExtCache(PlayerExtPropertyType.PEAK_ARENA_RECORD, PeakRecordInfo.class);

		RoleExtPropertyStore<PeakRecordInfo> store;
		try {
			store = playerExtCache.getStore(ownerId);
			return store;
		} catch (InterruptedException e) {
			e.printStackTrace();
			GameLog.error("PeakArenaRecordHolder InterruptedException", ownerId, e.getMessage());
		} catch (Throwable e) {
			GameLog.error("PeakArenaRecordHolder Throwable", ownerId, e.getMessage());
		}
		return null;
	}
}
