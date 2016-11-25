package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.attribute.param.MagicParam.MagicBuilder;
import com.rwbase.dao.item.pojo.ItemData;

/**
 * 
 * 获取玩家法宝当前的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetMagicCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {

	private static FSGetMagicCurrentFightingFunc _instance = new FSGetMagicCurrentFightingFunc();

	// private MagicCfgDAO magicCfgDAO;
	// private MagicLevelFightingCfgDAO magicLevelFightingCfgDAO;
	// private MagicQualityFightingCfgDAO magicQualityFightingCfgDAO;
	// private MagicAptitudeFightingCfgDAO magicAptitudeFightingCfgDAO;

	protected FSGetMagicCurrentFightingFunc() {
		// magicCfgDAO = MagicCfgDAO.getInstance();
		// magicLevelFightingCfgDAO = MagicLevelFightingCfgDAO.getInstance();
		// magicQualityFightingCfgDAO = MagicQualityFightingCfgDAO.getInstance();
		// magicAptitudeFightingCfgDAO = MagicAptitudeFightingCfgDAO.getInstance();
	}

	public static FSGetMagicCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		ItemData magic = player.getMagicMgr().getMagic();
		if (magic == null) {
			return 0;
		}

		MagicBuilder mb = new MagicBuilder();
		mb.setHeroTemplateId(player.getMainRoleHero().getTemplateId());
		mb.setMagicId(String.valueOf(magic.getModelId()));
		mb.setMagicLevel(magic.getMagicLevel());
		mb.setMagicAptitude(magic.getMagicAdvanceLevel());
		mb.setIsMainRole(true);

		int fighting = FightingCalcComponentType.MAGIC.calc.calc(mb.build());

		int size = teamHeros.size() - 1;
		if (size > 0) {
			mb.setIsMainRole(false);
			int heroFighting = FightingCalcComponentType.MAGIC.calc.calc(mb.build());
			fighting += heroFighting * size;
		}
		
		return fighting;

		// MagicCfg cfg = magicCfgDAO.getCfgById(String.valueOf(magic.getModelId()));
		// if (cfg == null) {
		// GameLog.error("FSGetMagicCurrentFightingFunc", player.getUserId(), "找不到对应的法宝配置，modelId：" + magic.getModelId());
		// return 0;
		// }
		// OneToOneTypeFightingCfg levelFightingCfg = magicLevelFightingCfgDAO.getCfgById(String.valueOf(magic.getMagicLevel()));
		// OneToOneTypeFightingCfg qualityFightingCfg = magicQualityFightingCfgDAO.getCfgById(String.valueOf(cfg.getQuality()));
		// OneToOneTypeFightingCfg magicAptitudeCfg = magicAptitudeFightingCfgDAO.getCfgById(String.valueOf(magic.getMagicAptitude()));
		// return levelFightingCfg.getFighting() + qualityFightingCfg.getFighting() + magicAptitudeCfg.getFighting();
		// return 0;
	}

}
