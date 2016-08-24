package com.playerdata.hero.core;

import static com.rwbase.common.MapItemStoreFactory.getHeroDataCache;
import static com.rwbase.common.MapItemStoreFactory.getMainHeroDataCache;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import com.rw.fsutil.cacheDao.mapItem.MapItemStore;

public class FSHeroDAO {
	
	private static final FSHeroDAO _INSTANCE = new FSHeroDAO();
	
	public static FSHeroDAO getInstance() {
		return _INSTANCE;
	}
	
	MapItemStore<FSHero> getOtherHeroMapItemStore(String userId) {
		return getHeroDataCache().getMapItemStore(userId, FSHero.class);
	}
	
	MapItemStore<FSHero> getMainHeroMapItemStore(String userId) {
		return getMainHeroDataCache().getMapItemStore(userId, FSHero.class);
	}
	
	Enumeration<FSHero> getEnumeration(String userId) {
		MapItemStore<FSHero> mapItemStore = getOtherHeroMapItemStore(userId);
		return new HeroEnumeration(mapItemStore.getEnum(), this.getMainHeroMapItemStore(userId).getItem(userId));
	}
	
	void notifyUpdate(String userId, String heroId) {
		MapItemStore<FSHero> mapItemStore;
		if (userId.equals(heroId)) {
			mapItemStore = getMainHeroMapItemStore(userId);
		} else {
			mapItemStore = getOtherHeroMapItemStore(userId);
		}
		mapItemStore.update(heroId);
	}
	
	static class HeroEnumeration implements Enumeration<FSHero> {
		
		private Enumeration<FSHero> _otherHeros;
		private FSHero _mainHero;
		private boolean _mainPass;
		
		 HeroEnumeration(Enumeration<FSHero> pOtherHeros, FSHero mainHero) {
			 this._otherHeros = pOtherHeros;
			 this._mainHero = mainHero;
		 }

		@Override
		public boolean hasMoreElements() {
			if (_otherHeros.hasMoreElements()) {
				return true;
			} else {
				return !_mainPass;
			}
		}

		@Override
		public FSHero nextElement() {
			if (_otherHeros.hasMoreElements()) {
				return _otherHeros.nextElement();
			} else if (!_mainPass) {
				_mainPass = true;
				return _mainHero;
			} else {
				throw new NoSuchElementException();
			}
		}
		
	}
}
