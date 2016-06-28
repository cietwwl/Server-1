package com.groupCopy.bm.groupCopy;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.common.Utils;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyLevelRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyMapRecordHolder;
import com.groupCopy.rwbase.dao.groupCopy.db.GroupCopyProgress;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
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
	public static GroupCopyResult  openMap(Player player, GroupCopyMapRecordHolder mapRecordHolder,
			GroupCopyLevelRecordHolder lvRecordHolder, String mapId){
		boolean suc = false;
		GroupCopyResult result = GroupCopyResult.newResult();
		try {
			GroupCopyMapRecord mapRecord = mapRecordHolder.getItem(mapId);
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
	
	
	
//	/**
//	 * 重置帮派副本
//	 * @param groupCopyMapRecordHolder
//	 * @param mapId
//	 * @param player TODO
//	 * @return
//	 */
//	public static GroupCopyResult resetMap(GroupCopyMapRecordHolder groupCopyMapRecordHolder,String mapId, Player player){
//		
//		GroupCopyResult result = GroupCopyResult.newResult();
//		boolean success = false;
//		GroupCopyMapRecord mapRecord = groupCopyMapRecordHolder.getItem(mapId);
//		if(mapRecord != null){			
//			mapRecord.cleanData();
//			groupCopyMapRecordHolder.updateItem(player, mapRecord);
//			success = true;
//		}
//		result.setSuccess(success);
//		return result;
//	}

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
		mapRecordHolder.updateMapProgress(mapCfg.getId(), p);
		
	}
	
	
	
}
