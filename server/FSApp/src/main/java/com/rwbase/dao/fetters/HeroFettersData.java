package com.rwbase.dao.fetters;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.playerdata.AttrMgr;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.dao.fetters.pojo.SynFettersData;

/**
 * @Author HC
 * @date 2016年10月12日 下午5:49:05
 * @desc
 **/

public class HeroFettersData {
	/** 羁绊的缓存数据<英雄的ModelId,List<羁绊的推送数据>> */
	private ConcurrentHashMap<Integer, SynFettersData> fettersMap = new ConcurrentHashMap<Integer, SynFettersData>();

	/**
	 * 通过英雄的ModelId获取英雄的羁绊
	 * 
	 * @param modelId
	 * @return
	 */
	public SynFettersData getHeroFettersByModelId(int modelId) {
		return fettersMap.get(modelId);
	}

	/**
	 * 获取所有的英雄羁绊
	 * 
	 * @return
	 */
	public List<SynFettersData> getAllHeroFetters() {
		return new ArrayList<SynFettersData>(fettersMap.values());
	}

	/**
	 * 增加英雄羁绊数据
	 * 
	 * @param heroModelId
	 * @param fettersData
	 * @param canSyn 是否可以同步数据
	 */
	public void addOrUpdateHeroFetters(Player player, int heroModelId, SynFettersData fettersData, boolean canSyn) {
		if (fettersData == null) {
			return;
		}

		fettersMap.put(heroModelId, fettersData);

		if (canSyn) {
			// 同步到前端
			HeroFettersDataHolder.syn(player, heroModelId);

			// 重新计算属性
			Hero hero = FSHeroMgr.getInstance().getHeroByModerId(player, heroModelId);
			if (hero != null) {
				AttrMgr attrMgr = hero.getAttrMgr();
				if (attrMgr != null) {
					attrMgr.reCal();
				}
			}
		}
	}

	/**
	 * 检查所有英雄的羁绊
	 * 
	 * @param player
	 */
	public void checkAllHeroFetters(Player player) {
		Enumeration<? extends Hero> herosEnumeration = FSHeroMgr.getInstance().getHerosEnumeration(player);
		while (herosEnumeration.hasMoreElements()) {
			Hero hero = herosEnumeration.nextElement();
			if (hero == null) {
				continue;
			}

			FettersBM.checkOrUpdateHeroFetters(player, hero.getModeId(), false, null);
		}
	}
}