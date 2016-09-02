package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.MagicLevelFightingCfgDAO;
import com.rwbase.dao.fighting.MagicQualityFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;

/**
 * 
 * 获取玩家法宝当前的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetMagicCurrentFightingFunc implements IFunction<Player, Integer> {

	@Override
	public Integer apply(Player player) {
		ItemData magic = player.getMagicMgr().getMagic();
		if (magic != null) {
			MagicCfg cfg = MagicCfgDAO.getInstance().getCfgById(String.valueOf(magic.getModelId()));
			OneToOneTypeFightingCfg levelFightingCfg = MagicLevelFightingCfgDAO.getInstance().getCfgById(String.valueOf(magic.getMagicLevel()));
			OneToOneTypeFightingCfg qualityFightingCfg = MagicQualityFightingCfgDAO.getInstance().getCfgById(String.valueOf(cfg.getQuality()));
			return levelFightingCfg.getFighting() + qualityFightingCfg.getFighting();
		}
		return 0;
	}

}
