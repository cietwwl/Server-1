package com.playerdata.hero.core;

import static com.rwbase.common.MapItemStoreFactory.getHeroDataCache;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;

public class FSHeroDAO {
	
	private static final FSHeroDAO _INSTANCE = new FSHeroDAO();
	
	public static FSHeroDAO getInstance() {
		return _INSTANCE;
	}
	
	void notifyUpdate(String userId, String heroId) {
		MapItemStore<FSHero> mapItemStore = getHeroDataCache().getMapItemStore(userId, FSHero.class);
		mapItemStore.update(heroId);
	}
	
	public FSHero getHeroNew(String userId, String heroId) {
		MapItemStore<FSHero> mapItemStore = getHeroDataCache().getMapItemStore(userId, FSHero.class);
		return mapItemStore.getItem(heroId);
	}
	
	public Hero getHero(String userId, String heroId) {
		Player player = PlayerMgr.getInstance().find(userId);
		return player.getHeroMgr().getHeroById(heroId);
	}
}
