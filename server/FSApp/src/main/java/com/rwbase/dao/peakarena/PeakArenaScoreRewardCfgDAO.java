package com.rwbase.dao.peakarena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bm.arena.ArenaScore;
import com.bm.arena.ArenaScoreTemplate;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.IntPairValue;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class PeakArenaScoreRewardCfgDAO extends CfgCsvDao<ArenaScore> {
	
//	private HashMap<Integer, ArenaScoreTemplate> templateMap; // 积分奖励总表，按id存储
	private TreeMap<Integer, Map<Integer, ArenaScoreTemplate>> templateMapByMinLv;
	private TreeMap<Integer, TreeMap<Integer, Integer>> scoreRewardsCount; // 积分获取的奖励数
	
	public static PeakArenaScoreRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(PeakArenaScoreRewardCfgDAO.class);
	}

	
	@Override
	protected Map<String, ArenaScore> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PeakArena/peakArenaScoreReward.csv", ArenaScore.class);
		TreeMap<Integer, Map<Integer, ArenaScoreTemplate>> allTemplatesByMinLv = new TreeMap<Integer, Map<Integer, ArenaScoreTemplate>>();
		TreeMap<Integer, TreeMap<Integer, ArenaScoreTemplate>> scoreMap = new TreeMap<Integer, TreeMap<Integer, ArenaScoreTemplate>>();
		List<IntPairValue<Integer>> levelList = new ArrayList<IntPairValue<Integer>>();
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			String key = keyItr.next();
			ArenaScore arenaScore = cfgCacheMap.get(key);
			ArenaScoreTemplate template = new ArenaScoreTemplate(arenaScore.getScore(), arenaScore.getReward(), arenaScore.getMinLevel(), arenaScore.getMaxLevel());
			Map<Integer, ArenaScoreTemplate> templateMap = allTemplatesByMinLv.get(template.getMinLevel());
			if(templateMap == null) {
				templateMap = new HashMap<Integer, ArenaScoreTemplate>();
				allTemplatesByMinLv.put(template.getMinLevel(), templateMap);
			}
			templateMap.put(arenaScore.getTypeId(), template);
			TreeMap<Integer, ArenaScoreTemplate> tempScoreMap = scoreMap.get(template.getMinLevel());
			if (tempScoreMap == null) {
				tempScoreMap = new TreeMap<Integer, ArenaScoreTemplate>();
				scoreMap.put(template.getMinLevel(), tempScoreMap);
				levelList.add(new IntPairValue<Integer>(template.getMinLevel(), template.getMaxLevel()));
			}
			if (tempScoreMap.put(template.getScore(), template) != null) {
				throw new ExceptionInInitializerError("ArenaScore重复积分定义：" + template.getScore());
			}
		}
		TreeMap<Integer, TreeMap<Integer, Integer>> scoreRewardsCount = new TreeMap<Integer, TreeMap<Integer, Integer>>();
		int count;
		for (Integer minLevel : scoreMap.keySet()) {
			count = 0;
			TreeMap<Integer, Integer> scoreCountMap = new TreeMap<Integer, Integer>();
			scoreRewardsCount.put(minLevel, scoreCountMap);
			TreeMap<Integer, ArenaScoreTemplate> rewardMap = scoreMap.get(minLevel);
			for (Integer score : rewardMap.keySet()) {
				scoreCountMap.put(score, ++count);
			}
		}
		this.scoreRewardsCount = scoreRewardsCount;
		this.templateMapByMinLv = allTemplatesByMinLv;
		for (int i = 0; i < levelList.size(); i++) {
			int level = levelList.get(i).t;
			for (int j = 0; j < 40; j++) {
				System.out.println("等级：" + level + "，积分：" + j + ",奖励数：" + getRewardCount(j, level));
			}
		}
		return cfgCacheMap;
	}

	/**
	 * 根据id获取积分奖励配置
	 * @param key
	 * @return
	 */
	public ArenaScoreTemplate getScoreTemplate(int typeId, int lv) {
		Map.Entry<Integer, Map<Integer, ArenaScoreTemplate>> entry = templateMapByMinLv.floorEntry(lv);
		return entry.getValue().get(typeId);
	}
	
	public List<Integer> getAllRewardTypes(int lv) {
		Map.Entry<Integer, Map<Integer, ArenaScoreTemplate>> entry = templateMapByMinLv.floorEntry(lv);
		return new ArrayList<Integer>(entry.getValue().keySet());
	}
	
	public Map<Integer, ArenaScoreTemplate> getAllRewards(int lv) {
		Map.Entry<Integer, Map<Integer, ArenaScoreTemplate>> entry = templateMapByMinLv.floorEntry(lv);
		return new HashMap<Integer, ArenaScoreTemplate>(entry.getValue());
	}

	/**
	 * 根据积分获取奖励数
	 * @param score
	 * @return
	 */
	public int getRewardCount(int score, int level) {
		Map.Entry<Integer, TreeMap<Integer, Integer>> scoreMap = scoreRewardsCount.floorEntry(level);
		Map.Entry<Integer, Integer> count = scoreMap.getValue().floorEntry(score);
		return count == null ? 0 : count.getValue();
	}
}
