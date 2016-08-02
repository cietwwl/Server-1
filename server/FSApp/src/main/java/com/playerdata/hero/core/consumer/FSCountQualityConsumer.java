package com.playerdata.hero.core.consumer;

import com.playerdata.Hero;
import com.playerdata.hero.IHeroConsumer;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

public class FSCountQualityConsumer implements IHeroConsumer {

	private int _targetQuality;
	private int _countResult;
	
	public FSCountQualityConsumer(int targetQuality) {
		this._targetQuality = targetQuality;
	}
	
	public int getCountResult() {
		return _countResult;
	}
	
	@Override
	public void apply(Hero hero) {
		RoleQualityCfg qualcfg = RoleQualityCfgDAO.getInstance().getConfig(hero.getQualityId());
		if (qualcfg != null && qualcfg.getQuality() >= _targetQuality) {
			_countResult++;
		}
	}

}
