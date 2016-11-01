package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.MagicParam.MagicBuilder;
import com.rwbase.dao.item.pojo.ItemData;

/**
 * 
 * 获取玩家法宝当前的战斗力
 * 
 * @author CHEN.P
 *
 */
public class FSGetMagicCurrentFightingFunc implements IFunction<Player, Integer> {

	private static final FSGetMagicCurrentFightingFunc _instance = new FSGetMagicCurrentFightingFunc();

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

	public static final FSGetMagicCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		ItemData magic = player.getMagicMgr().getMagic();
		if (magic == null) {
			return 0;
		}

		MagicBuilder mb = new MagicBuilder();
		mb.setHeroId(player.getMainRoleHero().getTemplateId());
		mb.setMagicId(String.valueOf(magic.getModelId()));
		mb.setMagicLevel(magic.getMagicLevel());
		mb.setMagicAptitude(magic.getMagicAdvanceLevel());

		return FightingCalcComponentType.MAGIC.calc.calc(mb.build());

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
