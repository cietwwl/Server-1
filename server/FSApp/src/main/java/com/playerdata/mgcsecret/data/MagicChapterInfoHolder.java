package com.playerdata.mgcsecret.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.mgcsecret.cfg.MagicChapterCfgDAO;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
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
		String itemID = userId + "_" + chapterId;
		return getItemStore(userId).getItem(itemID);
	}
	
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
	
	/**
	 * 初始化新的章节数据
	 * @param player
	 * @param chapterID
	 */
	public void initMagicChapterInfo(Player player, String chapterID){
		MagicChapterInfo mcInfo = getItem(player.getUserId(), chapterID);
		if(mcInfo == null) {
			mcInfo = new MagicChapterInfo();
			if(MagicChapterCfgDAO.getInstance().getCfgById(chapterID) == null) 
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("initMagicChapterInfo, 法宝秘境的初始化章节ID[%s]有误，数据表中招不到对应的数据", chapterID), new Exception("法宝秘境的初始化章节ID有误，数据表中招不到对应的数据"));
			mcInfo.setId(player.getUserId() + "_" + chapterID);
			mcInfo.setChapterId(chapterID);
			mcInfo.setUserId(player.getUserId());
			addItem(player, mcInfo);
			startNewChapter(player, chapterID);
			updateItem(player, mcInfo);
		}
	}
	
	/**
	 * 重置所有的章节信息
	 * @param player
	 */
	public boolean resetAllItem(Player player){
		for(MagicChapterInfo mcInfo : getItemList(player.getUserId())){
			mcInfo.resetData();
			startNewChapter(player, mcInfo.getId());
			updateItem(player, mcInfo);
		}
		return true;
	}
	
	/**
	 * 初始化新章节，为新章节生成怪物组
	 * @param player
	 * @param chapterID
	 * @return
	 */
	public boolean startNewChapter(Player player, String chapterID){
		MagicSecretMgr msMgr = player.getMagicSecretMgr();
		String first_dungeon_id = chapterID + "01_1";
		String fake_last_dungeon_id = chapterID + "00_1";
		if(msMgr.judgeDungeonsCondition(first_dungeon_id))
			msMgr.createDungeonsDataForNextStage(fake_last_dungeon_id);
		return true;
	}
	
	public void synAllData(Player player){
		List<MagicChapterInfo> itemList = getItemList(player.getUserId());			
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private MapItemStore<MagicChapterInfo> getItemStore(String userId) {
		MapItemStoreCache<MagicChapterInfo> cache = MapItemStoreFactory.getMagicChapterInfoCache();
		return cache.getMapItemStore(userId, MagicChapterInfo.class);
	}
}
