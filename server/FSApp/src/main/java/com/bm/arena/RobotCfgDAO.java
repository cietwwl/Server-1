package com.bm.arena;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

	private Map<String, RobotEntryCfg> angelRobots;// 万仙阵要用的机器人

	@Override
	public Map<String, RobotCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arenaRobot/RobotCfg.csv", RobotCfg.class);
		// for (Object o : cfgCacheMap.values()) {
		// RobotCfg cfg = (RobotCfg) o;
		// String ranking = cfg.getRanking();
		// int[] arrayArray = parseIntArray(ranking, "~");
		// int start = arrayArray[0];
		// int end = arrayArray[1];
		// for (int i = start; i <= end; i++) {
		// RobotEntryCfg entry = new RobotEntryCfg(i, cfg);
		// arenaRobots_.put(i, entry);
		// }
		// }

		TreeMap<Integer, RobotEntryCfg> arenaRobots_ = new TreeMap<Integer, RobotEntryCfg>();
		Map<String, RobotEntryCfg> angelRobots_ = new HashMap<String, RobotEntryCfg>();

		for (Entry<String, RobotCfg> e : cfgCacheMap.entrySet()) {
			RobotCfg cfg = e.getValue();
			if (cfg == null) {
				continue;
			}

			int robotType = cfg.getRobotType();
			if (robotType == 1) {// 竞技场
				String ranking = cfg.getRanking();
				int[] arrayArray = parseIntArray(ranking, "~");
				int start = arrayArray[0];
				int end = arrayArray[1];
				for (int i = start; i <= end; i++) {
					RobotEntryCfg entry = new RobotEntryCfg(i, cfg);
					arenaRobots_.put(i, entry);
				}
			} else {// 万仙阵
				angelRobots_.put(e.getKey(), new RobotEntryCfg(0, cfg));
			}
		}

		arenaRobots = arenaRobots_;
		angelRobots = angelRobots_;

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

	/**
	 * 获取万仙阵要用的机器人
	 * 
	 * @param robotId
	 * @return
	 */
	public RobotEntryCfg getAngelRobotCfg(String robotId) {
		if (angelRobots == null || angelRobots.isEmpty()) {
			return null;
		}

		return angelRobots.get(robotId);
	}
}