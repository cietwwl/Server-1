package com.rwbase.dao.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.RandomUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.pojo.ArenaRobotCfg;

public class ArenaRobotCfgDAO extends CfgCsvDao<ArenaRobotCfg> {

	private ArenaRobotCfgDAO() {
	}

	public static ArenaRobotCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaRobotCfgDAO.class);
	}

	@Override
	public Map<String, ArenaRobotCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaRobot.csv", ArenaRobotCfg.class);
		return cfgCacheMap;
	}

	public List<ArenaRobotCfg> getHeroGroupList() {
		List<ArenaRobotCfg> list = new ArrayList<ArenaRobotCfg>();
		for (int i = 1; i <= 5; i++) {
			list.add((ArenaRobotCfg) getCfgById("" + i));
		}
		return list;
	}

	public String getRandomHeroId(ArenaRobotCfg cfg) {
		String heroId = "0";
		if (cfg == null)
			return heroId;
		String[] arrHeroIds = cfg.getData().split(",");
		int randomIndex = RandomUtil.getRandonIndexWithoutProb(arrHeroIds.length);
		if (randomIndex == -1)
			return heroId;
		heroId = arrHeroIds[randomIndex];

		return heroId;
	}

	public int getRobotMagicId() {
		int magicId = 0;

		ArenaRobotCfg cfg = (ArenaRobotCfg) getCfgById("6");
		if (cfg == null)
			return magicId;
		String[] arrMagicIds = cfg.getData().split(",");
		int randomIndex = RandomUtil.getRandonIndexWithoutProb(arrMagicIds.length);
		if (randomIndex == -1)
			return magicId;
		magicId = Integer.parseInt(arrMagicIds[randomIndex]);

		return magicId;
	}

}
