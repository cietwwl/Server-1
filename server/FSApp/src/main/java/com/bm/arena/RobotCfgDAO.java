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

	enum RobotType {
		Arena(1), Angel(2), OnlyHeros(3), PeakArena(4);

		public final int type;

		private RobotType(int type) {
			this.type = type;
		}
	}

	private TreeMap<Integer, RobotEntryCfg> arenaRobots;
	private TreeMap<Integer, RobotEntryCfg> peakArenaRobots;

	private Map<String, Integer> robotId2TypeMap;// 机器人的Id对应的机器人类型

	private Map<Integer, Map<String, RobotEntryCfg>> robotMap;// 机器人

	@Override
	public Map<String, RobotCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arenaRobot/RobotCfg.csv", RobotCfg.class);
		TreeMap<Integer, RobotEntryCfg> arenaRobots_ = new TreeMap<Integer, RobotEntryCfg>();
		TreeMap<Integer, RobotEntryCfg> peakArenaRobots_ = new TreeMap<Integer, RobotEntryCfg>();
		Map<Integer, Map<String, RobotEntryCfg>> robotMap_ = new HashMap<Integer, Map<String, RobotEntryCfg>>();
		Map<String, Integer> robotId2TypeMap_ = new HashMap<String, Integer>();

		for (Entry<String, RobotCfg> e : cfgCacheMap.entrySet()) {
			RobotCfg cfg = e.getValue();
			if (cfg == null) {
				continue;
			}

			int robotType = cfg.getRobotType();
			if (robotType == RobotType.Arena.type || robotType == RobotType.PeakArena.type) {// 竞技场
				String ranking = cfg.getRanking();
				int[] arrayArray = parseIntArray(ranking, "~");
				int start = arrayArray[0];
				int end = arrayArray[1];
				for (int i = start; i <= end; i++) {
					RobotEntryCfg entry = new RobotEntryCfg(i, cfg);
					if (robotType == RobotType.Arena.type) {
						arenaRobots_.put(i, entry);
					} else {
						peakArenaRobots_.put(i, entry);
					}
				}
			} else {
				Map<String, RobotEntryCfg> map = robotMap_.get(robotType);
				if (map == null) {
					map = new HashMap<String, RobotEntryCfg>();
					robotMap_.put(robotType, map);
				}

				map.put(e.getKey(), new RobotEntryCfg(0, cfg));
			}

			robotId2TypeMap_.put(e.getKey(), robotType);
		}

		arenaRobots = arenaRobots_;
		peakArenaRobots = peakArenaRobots_;
		robotMap = robotMap_;
		robotId2TypeMap = robotId2TypeMap_;

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

	public TreeMap<Integer, RobotEntryCfg> getAllPeakArenaRobets() {
		if (this.peakArenaRobots == null) {
			initJsonCfg();
		}
		return this.peakArenaRobots;
	}

	/**
	 * 获取要用的机器人，不包含竞技场机器人
	 * 
	 * @param robotId
	 * @return
	 */
	public RobotEntryCfg getRobotCfg(String robotId) {
		if (robotId2TypeMap == null || robotId2TypeMap.isEmpty()) {
			return null;
		}

		Map<String, RobotEntryCfg> map = robotMap.get(robotId2TypeMap.get(robotId));
		if (map == null || map.isEmpty()) {
			return null;
		}

		return map.get(robotId);
	}
}