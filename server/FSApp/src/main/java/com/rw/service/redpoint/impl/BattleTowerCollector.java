package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.playerdata.BattleTowerMgr;
import com.playerdata.Player;
import com.rw.service.battletower.BattleTowerHandler;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerConfigCfg;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerConfigCfgDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

/*
 * @author HC
 * @date 2016年7月13日 下午1:31:17
 * @Description 封神台红点
 */
public class BattleTowerCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			return;
		}

		if (tableBattleTower.getCopper_key() > 0 || tableBattleTower.getSilver_key() > 0 || tableBattleTower.getGold_key() > 0) {
			map.put(RedPointType.BATTLE_TOWER_WINDOW_OPEN_CHEST, Collections.EMPTY_LIST);
		}

		// 检查扫荡完成没
		BattleTowerConfigCfg uniqueCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();// 唯一的配置
		int theSweepTime4PerFloor = BattleTowerHandler.getSweepTimePerFloor(player, tableBattleTower, uniqueCfg);

		// 时间验证
		long now = System.currentTimeMillis();
		int sweepStartFloor = tableBattleTower.getSweepStartFloor();
		int highestFloor = tableBattleTower.getHighestFloor();
		long needTime = TimeUnit.SECONDS.toMillis((highestFloor - sweepStartFloor + 1) * theSweepTime4PerFloor);

		// 检查时间是否已经到了完成时间
		if (tableBattleTower.getSweepState() && tableBattleTower.getSweepStartTime() + needTime <= now) {
			map.put(RedPointType.BATTLE_TOWER_SWEEP_END, Collections.EMPTY_LIST);
		}

		// // 检查Boss
		// List<String> bossIdList = null;
		// List<BossInfo> bossInfoList = tableBattleTower.getBossInfoList();
		// if (!bossInfoList.isEmpty()) {
		// int size = bossInfoList.size();
		// bossIdList = new ArrayList<String>(size);
		//
		// long showTime = TimeUnit.MINUTES.toMillis(uniqueCfg.getBossShowTime());
		//
		// for (int i = 0; i < size; i++) {
		// BossInfo bossInfo = bossInfoList.get(i);
		// if (now < bossInfo.getBossStartTime()) {
		// continue;
		// }
		//
		// long offTime = now - bossInfo.getBossStartTime();
		// if (offTime >= showTime) {
		// continue;
		// }
		//
		// if (bossInfo.isHasFight()) {
		// continue;
		// }
		//
		// bossIdList.add(String.valueOf(bossInfo.getBossUniqueId()));
		// }
		// }
		//
		// if (bossIdList != null && !bossIdList.isEmpty()) {
		// map.put(RedPointType.BATTLE_TOWER_NEW_BOSS, bossIdList);
		// }
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.BATTLETOWER;
	}
}