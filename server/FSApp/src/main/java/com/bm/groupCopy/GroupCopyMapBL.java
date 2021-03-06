package com.bm.groupCopy;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.common.Utils;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;


/**
 * 
 */
public class GroupCopyMapBL {

	/**
	 * 开启帮派地图副本
	 * @param mapRecordHolder
	 * @param lvRecordHolder TODO
	 * @param mapId
	 * @return
	 */
	public static GroupCopyResult  openOrResetMap(Player player, GroupCopyMapRecordHolder mapRecordHolder,
			GroupCopyLevelRecordHolder lvRecordHolder, String mapId){
		boolean suc = false;
		GroupCopyResult result = GroupCopyResult.newResult();
		try {
			GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(mapId);
			GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(mapId);
			if(mapRecord == null){
				GameLog.error(LogModule.GroupCopy, "GroupCopyMapBL[openMap]", "玩家开启帮派副本出现异常，玩家名:" 
						+ player.getUserName() + ",地图章节id:" + mapId, null);
				result.setSuccess(suc);
				result.setTipMsg("服务器繁忙！");
				return result;
			}

			//设置额外奖励时间
			mapRecord.setRewardTime(System.currentTimeMillis() + mapCfg.getExtraRewardTime() * TimeUnit.HOURS.toMillis(1));
			mapRecord.setStatus(GroupCopyMapStatus.ONGOING);
			mapRecord.setCurLevelID(mapCfg.getStartLvID());
			mapRecord.cleanData();

			
			//重置所有关卡的进度  
			lvRecordHolder.resetLevelData(player, mapCfg.getLvList());
			
			
			suc = mapRecordHolder.updateItem(player, mapRecord);
			
		} catch (Exception e) {
			GameLog.error(LogModule.GroupCopy, "GroupCopyMapBL[openMap]", "开启帮派副本时异常", e);
		}
		result.setSuccess(suc);
		return result;
		
	}
	
	
	

	/**
	 * 内部同步副本进度
	 * @param player
	 * @param levelRecordHolder
	 * @param mapRecordHolder
	 * @param levelId
	 */
	public static void calculateMapProgress(Player player,
			GroupCopyLevelRecordHolder levelRecordHolder,
			GroupCopyMapRecordHolder mapRecordHolder, String levelId) {
		
		int totalHp = 0;
		int currentHp = 0;
		GroupCopyLevelRecord lvRecord;
		GroupCopyProgress progress;
		GroupCopyLevelCfg cfg = GroupCopyLevelCfgDao.getInstance().getCfgById(levelId);
		GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(cfg.getChaterID());
		Set<String> lvList = mapCfg.getLvList();
		for (String id : lvList) {
			lvRecord = levelRecordHolder.getByLevel(id);
			progress = lvRecord.getProgress();
			totalHp += progress.getTotalHp();
			currentHp += progress.getCurrentHp();
		}
		double p = Utils.div((totalHp - currentHp), totalHp, 5);
		p = p > 1.0 ? 1 : p;
		//检查一下当前章节副本关卡id
		GroupCopyMapRecord mapRecord = mapRecordHolder.getItemByID(cfg.getChaterID());
		lvRecord = levelRecordHolder.getByLevel(levelId);
		if(lvRecord.getProgress().getProgress() == 1){
			//已经通关，设置下一个关卡id
			if(lvList.contains(cfg.getNextLevelID())){
				mapRecord.setCurLevelID(cfg.getNextLevelID());
			}
			
		}
		
		mapRecord.setProgress(p);
		//如果全部通关
		if(p == 1){
			mapRecord.setStatus(GroupCopyMapStatus.FINISH);
		}
		mapRecordHolder.updateItem(player, mapRecord);
		
	}
	
	
	
}
