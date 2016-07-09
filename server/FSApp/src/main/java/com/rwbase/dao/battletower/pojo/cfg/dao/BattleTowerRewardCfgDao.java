package com.rwbase.dao.battletower.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.dropitem.DropItemManager;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerRewardCfg;
import com.rwbase.dao.copy.pojo.ItemInfo;

/*
 * @author HC
 * @date 2015年9月7日 上午11:03:58
 * @Description 试练塔奖励物品的信息
 */
public class BattleTowerRewardCfgDao extends CfgCsvDao<BattleTowerRewardCfg> {
	public static BattleTowerRewardCfgDao getCfgDao() {
		return SpringContextUtil.getBean(BattleTowerRewardCfgDao.class);
	}

	@Override
	public Map<String, BattleTowerRewardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerRewardCfg.csv", BattleTowerRewardCfg.class);
		return cfgCacheMap;
	}

	/**
	 * 获取随机产出的掉落物品
	 * 
	 * @param dropIdArr
	 * @param player
	 * @return
	 */
	public List<ItemInfo> getRanRewardItem(String[] dropIdArr, Player player) {
		if (dropIdArr == null || dropIdArr.length <= 0) {
			return new ArrayList<ItemInfo>();
		}

		// ItemProbabilityCfgDAO cfgDao = ItemProbabilityCfgDAO.getInstance();
		// return cfgDao.getListItemInfo(dropIdArr, player);
		return DropItemManager.getInstance().dropAndRecord(dropIdArr, player);
	}
	
	/**根据战斗层数返回副本id*/
	public int getCopyIdByFloor(int floor){
		int copyId = 0;
		List<BattleTowerRewardCfg> list = getAllCfg();
		for(BattleTowerRewardCfg cfg : list){
			if(cfg.getGroupId() == floor){
				copyId = cfg.getCopyId();
				break;
			}
		}
			
		return copyId;
	}
}