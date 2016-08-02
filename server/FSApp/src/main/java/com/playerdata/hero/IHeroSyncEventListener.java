package com.playerdata.hero;

import com.playerdata.Hero;

public interface IHeroSyncEventListener {

	/**
	 * sync回调
	 * 
	 * @param hero
	 * @param version
	 */
	public void notifySync(Hero hero, int version);
}
