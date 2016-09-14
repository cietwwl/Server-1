package com.playerdata.fightinggrowth.fightingfunc;

import java.util.Iterator;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.ExpectedHeroStatusCfgDAO;
import com.rwbase.dao.fighting.TaoistFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.ExpectedHeroStatusCfg;
import com.rwbase.dao.fighting.pojo.TaoistFightingCfg;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FSGetTaoistMaxFightingFunc implements IFunction<Player, Integer> {

	private static final FSGetTaoistMaxFightingFunc _INSTANCE = new FSGetTaoistMaxFightingFunc();
	
	private ExpectedHeroStatusCfgDAO _expectedHeroStatusCfgDAO;
	private TaoistFightingCfgDAO _taoistFightingCfgDAO;
	private TaoistMagicCfgHelper _taoistMagicCfgHelper;
	private CfgOpenLevelLimitDAO _cfgOpenLevelLimitDAO;
	
	protected FSGetTaoistMaxFightingFunc() {
		_expectedHeroStatusCfgDAO = ExpectedHeroStatusCfgDAO.getInstance();
		_taoistFightingCfgDAO = TaoistFightingCfgDAO.getInstance();
		_taoistMagicCfgHelper = TaoistMagicCfgHelper.getInstance();
		_cfgOpenLevelLimitDAO = CfgOpenLevelLimitDAO.getInstance();
	}
	
	public static final FSGetTaoistMaxFightingFunc getInstance() {
		return _INSTANCE;
	}
	
	@Override
	public Integer apply(Player player) {
		if (_cfgOpenLevelLimitDAO.isOpen(eOpenLevelType.TAOIST, player) || player.isRobot()) {
			// TODO 道术最大战力未知如何计算
			ExpectedHeroStatusCfg cfg = _expectedHeroStatusCfgDAO.getCfgById(String.valueOf(player.getLevel()));
			Map<Integer, Integer> map = cfg.getExpectedLevelOfTag();
			TaoistFightingCfg taoistFightingCfg;
			int fighting = 0;
			for (Iterator<Integer> itr = map.keySet().iterator(); itr.hasNext();) {
				int tag = itr.next();
				int count = _taoistMagicCfgHelper.getCountOfTag(tag);
				taoistFightingCfg = _taoistFightingCfgDAO.getByLevel(map.get(tag));
				fighting += taoistFightingCfg.getFightingOfIndex(tag) * count;
			}
			return fighting * cfg.getExpectedHeroCount();
		}
		return 0;
	}

}
