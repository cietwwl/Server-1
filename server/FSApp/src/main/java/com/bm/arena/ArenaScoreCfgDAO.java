package com.bm.arena;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ArenaScoreCfgDAO extends CfgCsvDao<ArenaScore> {

	private HashMap<Integer, ArenaScoreTemplate> templateMap; // 积分奖励总表，按id存储
	private TreeMap<Integer, Integer> scoreRewardsCount; // 积分获取的奖励数

	@Override
	protected Map<String, ArenaScore> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaScore.csv", ArenaScore.class);
		HashMap<Integer, ArenaScoreTemplate> templateMap = new HashMap<Integer, ArenaScoreTemplate>(cfgCacheMap.size());
		TreeMap<Integer, ArenaScoreTemplate> scoreMap = new TreeMap<Integer, ArenaScoreTemplate>();
		for (Map.Entry<String, ArenaScore> entry : cfgCacheMap.entrySet()) {
			String key = entry.getKey();
			ArenaScore arenaScore = entry.getValue();
			ArenaScoreTemplate template = new ArenaScoreTemplate(arenaScore.getScore(), arenaScore.getReward());
			templateMap.put(Integer.parseInt(key), template);
			if (scoreMap.put(template.getSocre(), template) != null) {
				throw new ExceptionInInitializerError("ArenaScore重复积分定义：" + template.getSocre());
			}
		}
		TreeMap<Integer, Integer> scoreRewardsCount = new TreeMap<Integer, Integer>();
		int count = 0;
		for (Integer score : scoreMap.keySet()) {
			scoreRewardsCount.put(score, ++count);
		}
		this.scoreRewardsCount = scoreRewardsCount;
		this.templateMap = templateMap;
		for (int i = 0; i < 40; i++) {
			System.out.println("积分：" + i + ",奖励数：" + getRewardCount(i));
		}
		return cfgCacheMap;
	}

	public static ArenaScoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaScoreCfgDAO.class);
	}

	/**
	 * 根据id获取积分奖励配置
	 * @param key
	 * @return
	 */
	public ArenaScoreTemplate getScoreTemplate(int key) {
		return templateMap.get(key);
	}

	/**
	 * 根据积分获取奖励数
	 * @param score
	 * @return
	 */
	public int getRewardCount(int score) {
		Map.Entry<Integer, Integer> count = scoreRewardsCount.floorEntry(score);
		return count == null ? 0 : count.getValue();
	}
}
