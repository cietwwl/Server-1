package com.bm.arena;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RobotCfgDAO extends CfgCsvDao<RobotCfg> {
	
	public static RobotCfgDAO getInstance() {
		return SpringContextUtil.getBean(RobotCfgDAO.class);
	}	
	

	private TreeMap<Integer, RobotEntryCfg> arenaRobots;

	@Override
	public Map<String, RobotCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arenaRobot/RobotCfg.csv", RobotCfg.class);
		TreeMap<Integer, RobotEntryCfg> arenaRobots_ = new TreeMap<Integer, RobotEntryCfg>();
		for (Object o : cfgCacheMap.values()) {
			RobotCfg cfg = (RobotCfg) o;
			String ranking = cfg.getRanking();
			int[] arrayArray = parseIntArray(ranking, "~");
			int start = arrayArray[0];
			int end = arrayArray[1];
			for (int i = start; i <= end; i++) {
				RobotEntryCfg entry = new RobotEntryCfg(i, cfg);
				arenaRobots_.put(i, entry);
			}
		}
		arenaRobots = arenaRobots_;
		return cfgCacheMap;
	}

	private int[] parseIntArray(String text, String split) {
		int[] array = new int[2];
		if (!text.contains(split)) {
			int v = Integer.parseInt(text);
			array[0] = v;
			array[1] = v;
		} else {
			StringTokenizer token = new StringTokenizer(text, split);
			int index = 0;
			while (token.hasMoreTokens()) {
				array[index++] = Integer.parseInt(token.nextToken());
			}
		}
		return array;
	}

	public TreeMap<Integer, RobotEntryCfg> getAllArenaRobets() {
		if (this.arenaRobots == null) {
			initJsonCfg();
		}
		return this.arenaRobots;
	}



	

}
