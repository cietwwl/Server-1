package com.bm.randomBoss;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.playerdata.Player;

/**
 * 随机boss角色属性改变监听器
 * @author Alex
 * 2016年12月14日 下午3:21:09
 */
public class RBPlayerChangeListener extends PlayerChangePopertySubscribe{

	public RBPlayerChangeListener(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		
	}

	@Override
	public void playerChangeLevel(Player p) {
		RandomBossMgr.getInstance().checkAndSynRandomBossData(p);
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		
	}

	@Override
	public void playerChangeTemplateId(Player p) {
		
	}

	@Override
	public void playerChangeHeadIcon(Player p) {
		
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		
	}

}
