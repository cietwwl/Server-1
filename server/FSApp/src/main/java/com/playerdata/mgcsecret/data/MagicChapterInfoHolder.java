package com.playerdata.mgcsecret.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class MagicChapterInfoHolder{
	
	private static MagicChapterInfoHolder instance = new MagicChapterInfoHolder();
	
	public static MagicChapterInfoHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.MagicChapterData;
	
	/*
	 * 获取已经通关的章节情况
	 */
	public List<MagicChapterInfo> getItemList(String userId)	
	{
		List<MagicChapterInfo> chapterList = new ArrayList<MagicChapterInfo>();
		Enumeration<MagicChapterInfo> mapEnum = getItemStore(userId).getEnum();
		while (mapEnum.hasMoreElements()) {
			MagicChapterInfo item = (MagicChapterInfo) mapEnum.nextElement();			
			chapterList.add(item);
		}
		
		return chapterList;
	}
	
	public void updateItem(Player player, MagicChapterInfo item){
		getItemStore(player.getUserId()).updateItem(item);
		ClientDataSynMgr.updateData(player, item, synType, eSynOpType.UPDATE_SINGLE);
	}
	
	public MagicChapterInfo getItem(String userId, String chapterId){		
		String itemId = userId + "_" + chapterId;
		return getItemStore(userId).getItem(itemId);
	}
	
//	public boolean removeItem(Player player, ActivityCountTypeItem item){
//		
//		boolean success = getItemStore(player.getUserId()).removeItem(item.getId());
//		if(success){
//			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.REMOVE_SINGLE);
//		}
//		return success;
//	}
	
	public boolean addItem(Player player, MagicChapterInfo item){
		boolean addSuccess = getItemStore(player.getUserId()).addItem(item);
		if(addSuccess){
			ClientDataSynMgr.updateData(player, item, synType, eSynOpType.ADD_SINGLE);
		}
		return addSuccess;
	}
	
	public boolean addItemList(Player player, List<MagicChapterInfo> itemList){
		try {
			boolean addSuccess = getItemStore(player.getUserId()).addItem(itemList);
			if(addSuccess){
				ClientDataSynMgr.updateDataList(player, getItemList(player.getUserId()), synType, eSynOpType.UPDATE_LIST);
			}
			return addSuccess;
		} catch (DuplicatedKeyException e) {
			//handle..
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	//TODO 删除的逻辑，每日刷新的时候，会清除掉所有的数据
//	public boolean removeitem(Player player,ActivityCountTypeEnum type){
//		
//		String uidAndId = ActivityCountTypeHelper.getItemId(player.getUserId(), type);
//		boolean addSuccess = getItemStore(player.getUserId()).removeItem(uidAndId);
//		return addSuccess;
//	}
//	
	public void synAllData(Player player){
		List<MagicChapterInfo> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	
	private MapItemStore<MagicChapterInfo> getItemStore(String userId) {
		MapItemStoreCache<MagicChapterInfo> cache = MapItemStoreFactory.getMagicChapterInfoCache();
		return cache.getMapItemStore(userId, MagicChapterInfo.class);
	}
	
}
