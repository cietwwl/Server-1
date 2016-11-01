package com.playerdata.fightinggrowth.calc;

import com.log.GameLog;
import com.rwbase.common.attribute.param.MagicParam;
import com.rwbase.dao.fighting.MagicAptitudeFightingCfgDAO;
import com.rwbase.dao.fighting.MagicLevelFightingCfgDAO;
import com.rwbase.dao.fighting.MagicQualityFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.MagicCfg;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:44:45
 * @desc
 **/

public class FSGetMagicFightingCalc implements IFightingCalc {
	private MagicCfgDAO magicCfgDAO;
	private MagicLevelFightingCfgDAO magicLevelFightingCfgDAO;
	private MagicQualityFightingCfgDAO magicQualityFightingCfgDAO;
	private MagicAptitudeFightingCfgDAO magicAptitudeFightingCfgDAO;

	protected FSGetMagicFightingCalc() {
		magicCfgDAO = MagicCfgDAO.getInstance();
		magicLevelFightingCfgDAO = MagicLevelFightingCfgDAO.getInstance();
		magicQualityFightingCfgDAO = MagicQualityFightingCfgDAO.getInstance();
		magicAptitudeFightingCfgDAO = MagicAptitudeFightingCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		MagicParam magicParam = (MagicParam) param;

		String magicId = magicParam.getMagicId();
		MagicCfg cfg = magicCfgDAO.getCfgById(magicId);
		if (cfg == null) {
			GameLog.error("FSMagicFightingCalc", "计算法宝战力", "找不到对应的法宝配置，modelId：" + magicId);
			return 0;
		}

		int fighting = 0;
		OneToOneTypeFightingCfg levelFightingCfg = magicLevelFightingCfgDAO.getCfgById(String.valueOf(magicParam.getMagicLevel()));
		fighting += levelFightingCfg == null ? 0 : levelFightingCfg.getFighting();

		OneToOneTypeFightingCfg qualityFightingCfg = magicQualityFightingCfgDAO.getCfgById(String.valueOf(cfg.getQuality()));
		fighting += qualityFightingCfg == null ? 0 : qualityFightingCfg.getFighting();

		OneToOneTypeFightingCfg magicAptitudeCfg = magicAptitudeFightingCfgDAO.getCfgById(String.valueOf(magicParam.getMagicAptitude()));
		fighting += magicAptitudeCfg == null ? 0 : magicAptitudeCfg.getFighting();
		return fighting;
	}
}