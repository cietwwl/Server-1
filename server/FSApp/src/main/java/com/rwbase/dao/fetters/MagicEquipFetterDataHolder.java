package com.rwbase.dao.fetters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rwbase.dao.fetters.pojo.SynMagicEquipFetterData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


/**
 * 法宝神器羁绊holder
 * @author Alex
 *
 * 2016年7月18日 上午9:54:58
 */
public class MagicEquipFetterDataHolder {

	
	private static final eSynType syType = eSynType.MAGICEQUIP_FETTER;
	
	private AtomicInteger dataVersion = new AtomicInteger(0);
	
	private final String userID;

	
	
	public MagicEquipFetterDataHolder(String userID) {
		this.userID = userID;
	}
	

	/**
	 * 同步所有法宝神器羁绊数据
	 * @param player
	 * @param version 版本
	 */
	public void synAllData(Player player, int version){
		if(version != 0 && version == dataVersion.get()){
			return;
		}
		MagicEquipFetterRecord item = getITemStore().getItem(userID);
		if(item.getDataMap().isEmpty()){
			return;
		}
		List<SynMagicEquipFetterData> list = new ArrayList<SynMagicEquipFetterData>();
		list.addAll(item.getDataMap().values());
		ClientDataSynMgr.synDataList(player, list, syType, eSynOpType.UPDATE_LIST);
		
	}
	
	private MapItemStore<MagicEquipFetterRecord> getITemStore(){
		MapItemStoreCache<MagicEquipFetterRecord> itemStoreCache = MapItemStoreFactory.getMagicEquipFetterCache();
		return itemStoreCache.getMapItemStore(userID, MagicEquipFetterRecord.class);
	}
	
}
