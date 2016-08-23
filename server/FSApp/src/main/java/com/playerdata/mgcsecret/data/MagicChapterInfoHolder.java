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
import com.rwbase.common.MapItemStoreFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class MagicChapterInfoHolder{
	
	private static class InstanceHolder{
		private static MagicChapterInfoHolder instance = new MagicChapterInfoHolder();
	}
	
	public static MagicChapterInfoHolder getInstance(){
		return InstanceHolder.instance;
	}
	
	private MagicChapterInfoHolder() { }

	final private eSynType synType = eSynType.MagicChapterData;
	
	/*
	 * 获取已经通关的章节情况
	 */
	public List<MagicChapterInfo> getItemList(Player player)
	{
		List<MagicChapterInfo> chapterList = new ArrayList<MagicChapterInfo>();
		// 这里需要判断一下角色的等级，如果玩家等级未达到开放
		if(!MagicSecretMgr.getInstance().judgeUserLevel(player, Integer.valueOf(MagicSecretMgr.CHAPTER_INIT_ID))) return chapterList;
		if(getItemStore(player.getUserId()).getSize() == 0) initMagicChapterInfo(player, MagicSecretMgr.CHAPTER_INIT_ID, false);
		Enumeration<MagicChapterInfo> mapEnum = getItemStore(player.getUserId()).getEnum();
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
	
	public void updateItemWithoutSyn(Player player, MagicChapterInfo item){
		getItemStore(player.getUserId()).updateItem(item);
	}
	
	public void updateItem(Player player, String chapterID){
		MagicChapterInfo item = getItem(player.getUserId(), chapterID);
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
	
	/**
	 * 初始化新的章节数据
	 * @param player
	 * @param chapterID
	 */
	public void initMagicChapterInfo(Player player, String chapterID, boolean isSyn){
		MagicChapterInfo mcInfo = getItem(player.getUserId(), chapterID);
		if(mcInfo == null) {
			mcInfo = new MagicChapterInfo();
			if(MagicChapterCfgDAO.getInstance().getCfgById(chapterID) == null) {
				GameLog.error(LogModule.MagicSecret, player.getUserId(), String.format("initMagicChapterInfo, 法宝秘境的初始化章节ID[%s]有误，数据表中招不到对应的数据", chapterID), new Exception("法宝秘境的初始化章节ID有误，数据表中招不到对应的数据"));
				return;
			}
			mcInfo.setId(player.getUserId() + "_" + chapterID);
			mcInfo.setChapterId(chapterID);
			mcInfo.setUserId(player.getUserId());
			addItem(player, mcInfo);
			startNewChapter(player, chapterID);
			if(isSyn) updateItem(player, mcInfo);
			else updateItemWithoutSyn(player, mcInfo);
		}
	}
	
	/**
	 * 重置所有的章节信息
	 * @param player
	 */
	public boolean resetAllItem(Player player){
		for(MagicChapterInfo mcInfo : getItemList(player)){
			mcInfo.resetData();
			startNewChapter(player, mcInfo.getChapterId());
			updateItemWithoutSyn(player, mcInfo);
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
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		String first_dungeon_id = chapterID + "01_1";
		String fake_last_dungeon_id = chapterID + "00_1";
		if(msMgr.judgeDungeonsCondition(player, first_dungeon_id))
			msMgr.createDungeonsDataForNextStage(player, fake_last_dungeon_id);
		return true;
	}
	
	public void synAllData(Player player){
		UserMagicSecretData umsData = UserMagicSecretHolder.getInstance().get(player);
		if(umsData == null) return;
		int maxChapter = umsData.getMaxStageID()/100;
		int maxStage = umsData.getMaxStageID()%100;
		List<MagicChapterInfo> itemList = getItemList(player);
		for(MagicChapterInfo mcInfo : itemList){
			if(Integer.valueOf(mcInfo.getChapterId()) < maxChapter) {
				mcInfo.getSelectableDungeons().clear();
				mcInfo.getUnselectedBuff().clear();
			}else if(Integer.valueOf(mcInfo.getChapterId()) == maxChapter && maxStage == MagicSecretMgr.STAGE_COUNT_EACH_CHATPER){
				mcInfo.getSelectableDungeons().clear();
				mcInfo.getUnselectedBuff().clear();
			}
		}
		ClientDataSynMgr.synDataList(player, itemList, synType, eSynOpType.UPDATE_LIST);
	}

	private MapItemStore<MagicChapterInfo> getItemStore(String userId) {
		MapItemStoreCache<MagicChapterInfo> cache = MapItemStoreFactory.getMagicChapterInfoCache();
		return cache.getMapItemStore(userId, MagicChapterInfo.class);
	}
}
