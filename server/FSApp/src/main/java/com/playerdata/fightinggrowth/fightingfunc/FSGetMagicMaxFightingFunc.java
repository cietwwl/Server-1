package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.MagicLevelFightingCfgDAO;
import com.rwbase.dao.fighting.MagicQualityFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;

/**
 * 
 * 获取当前等级下，玩家法宝所能达到的最大战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetMagicMaxFightingFunc implements IFunction<Player, Integer>{
	
	private static final FSGetMagicMaxFightingFunc _instance = new FSGetMagicMaxFightingFunc();

	private MagicLevelFightingCfgDAO magicLevelFightingCfgDAO;
	private MagicQualityFightingCfgDAO magicQualityFightingCfgDAO;
	
	protected FSGetMagicMaxFightingFunc() {
		magicLevelFightingCfgDAO = MagicLevelFightingCfgDAO.getInstance();
		magicQualityFightingCfgDAO = MagicQualityFightingCfgDAO.getInstance();
	}
	
	public static final FSGetMagicMaxFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		OneToOneTypeFightingCfg levelFightingCfg = magicLevelFightingCfgDAO.getByRequiredLv(player.getLevel());
		OneToOneTypeFightingCfg qualityFightingCfg = magicQualityFightingCfgDAO.getByRequiredLv(player.getLevel());
		return levelFightingCfg.getFighting() + qualityFightingCfg.getFighting();
	}

}
