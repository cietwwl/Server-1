package com.rwbase.dao.fetters;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2016年4月29日 上午9:23:08
 * @Description 英雄羁绊数据Holder
 */
public class HeroFettersDataHolder {
	private static final eSynType synType = eSynType.HERO_FETTERS;

	/**
	 * 同步单个英雄的羁绊信息
	 * 
	 * @param player
	 * @param heroModelId
	 */
	public static void syn(Player player, int heroModelId) {
		SynFettersData heroFetters = player.getHeroFettersByModelId(heroModelId);
		if (heroFetters == null) {
			return;
		}

		ClientDataSynMgr.synData(player, heroFetters, synType, eSynOpType.UPDATE_SINGLE);
	}

	/**
	 * 同步全部英雄的羁绊信息
	 * 
	 * @param player
	 */
	public static void synAll(Player player) {
		List<SynFettersData> allHeroFetters = player.getAllHeroFetters();
		if (allHeroFetters == null || allHeroFetters.isEmpty()) {
			return;
		}

		ClientDataSynMgr.synDataList(player, allHeroFetters, synType, eSynOpType.UPDATE_LIST);
	}
}