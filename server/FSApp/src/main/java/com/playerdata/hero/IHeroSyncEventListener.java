package com.playerdata.hero;

public interface IHeroSyncEventListener {

	/**
	 * sync回调
	 * 
	 * @param hero
	 * @param version
	 */
	public void notifySync(IHero hero, int version);
}
