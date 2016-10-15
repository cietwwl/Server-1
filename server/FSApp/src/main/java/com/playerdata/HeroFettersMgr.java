package com.playerdata;

import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.concurrent.DataInitEntity;
import com.rwbase.dao.fetters.HeroFettersData;
import com.rwbase.dao.fetters.HeroFettersDataInitFactory;

/**
 * @Author HC
 * @date 2016年10月12日 下午7:56:42
 * @desc
 **/

class HeroFettersMgr implements PlayerEventListener {

	/** 羁绊的数据封装 */
	private DataInitEntity<String, HeroFettersData> heroFettersData = new DataInitEntity<String, HeroFettersData>(new HeroFettersDataInitFactory());

	@Override
	public void notifyPlayerCreated(Player player) {
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		// 羁绊数据获取
		HeroFettersData data = heroFettersData.get(player.getUserId());
		if (data != null) {
			data.checkAllHeroFetters(player);
		}
	}

	@Override
	public void init(Player player) {
	}

	/**
	 * 获取羁绊的数据
	 * 
	 * @param userId
	 * @return
	 */
	public HeroFettersData get(String userId) {
		return heroFettersData.get(userId);
	}
}