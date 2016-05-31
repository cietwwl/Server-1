package com.rw.service.PeakArena;

import java.util.ArrayList;

import com.log.GameLog;
import com.rwbase.dao.arena.ArenaRobotCfgDAO;
import com.rwbase.dao.arena.pojo.ArenaRobotCfg;

public class PeakArenaRobotManager {
	public void createRobots() {
		int count = 3;//PeakArenaBM.getInstance().getRanks().getRankingSize();
		ArenaRobotCfg robotCfg = ArenaRobotCfgDAO.getInstance().getCfgById("7");
		String[] arrName = robotCfg.getData().split(",");
		int len = arrName.length;
		if (len < count) {//TODO 这次不管 count 点样修改？
			GameLog.error("巅峰竞技场","","当前机器名字人数量少于竞技场名次");
			return;
		}
		
		ArrayList<String> nameList = new ArrayList<String>(len - count);
		for (; count < len; count++) {
			nameList.add(arrName[count]);
		}

	}
}
