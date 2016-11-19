package com.playerdata.fightinggrowth.calc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.playerdata.fightinggrowth.calc.param.FettersFightingParam;
import com.rwbase.common.IBIFunction;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersBaseCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersBaseTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;
import com.rwbase.dao.fighting.FightingCfgCsvDAOBase;
import com.rwbase.dao.fighting.FixEquipFetterFightingCfgDAO;
import com.rwbase.dao.fighting.HeroFetterFightingCfgDAO;
import com.rwbase.dao.fighting.MagicFetterFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FightingCfgBase;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:06:17
 * @desc 计算羁绊相关的战斗力
 **/

public class FSGetFettersFightingCalc implements IFightingCalc {

	private FetterMagicEquipCfgDao fetterMagicEquipCfgDao;
	private FettersConditionCfgDAO fettersConditionCfgDao;
	private FettersBaseCfgDAO fetterBaseCfgDao;
	private IBIFunction<Integer, Integer, Integer> getFixEquipFetterFightingFunc;
	private IBIFunction<Integer, Integer, Integer> getMagicFetterFightingFunc;
	private IBIFunction<Integer, Integer, Integer> getHeroFetterFightingFunc;

	protected FSGetFettersFightingCalc() {
		fetterMagicEquipCfgDao = FetterMagicEquipCfgDao.getInstance();
		fettersConditionCfgDao = FettersConditionCfgDAO.getCfgDAO();
		fetterBaseCfgDao = FettersBaseCfgDAO.getCfgDAO();
		getHeroFetterFightingFunc = new GetFetterFightingFunc(HeroFetterFightingCfgDAO.getInstance());
		getFixEquipFetterFightingFunc = new GetFetterFightingFunc(FixEquipFetterFightingCfgDAO.getInstnce());
		getMagicFetterFightingFunc = new GetFetterFightingFunc(MagicFetterFightingCfgDAO.getInstance());
	}

	/**
	 * 
	 * 获取神器或法宝的羁绊
	 * 
	 * @param fetters 羁绊列表
	 * @param getFightingFunc 获取的方法
	 * @return
	 */
	private int getEquipFetterFighting(List<Integer> fetters, IBIFunction<Integer, Integer, Integer> getFightingFunc) {
		int fighting = 0;
		if (fetters.isEmpty()) {
			return fighting;
		}

		MagicEquipConditionCfg fetterMagicEquipCfg;
		for (int i = 0; i < fetters.size(); i++) {
			String fetterId = fetters.get(i).toString();
			fetterMagicEquipCfg = fetterMagicEquipCfgDao.getCfgById(fetterId);
			if (fetterMagicEquipCfg == null) {
				GameLog.error("FSGetFetterCurrentFightingOfSingleFunc", "getEquipFetterFighting", "FetterMagicEquipCfgDao找不到羁绊配置，id：" + fetterId + "");
				continue;
			}
			fighting += getFightingFunc.apply(fetterMagicEquipCfg.getConditionLevel(), fetterMagicEquipCfg.getSeq());
		}
		return fighting;
	}

	/**
	 * 
	 * 获取英雄羁绊的战斗力
	 * 
	 * @param player
	 * @param hero
	 * @return
	 */
	private int getHeroFetterFighting(Map<Integer, SynConditionData> fettersMap, IBIFunction<Integer, Integer, Integer> getFightingFunc) {
		int fighting = 0;
		if (fettersMap.isEmpty()) {
			return fighting;
		}

		FettersConditionTemplate conditionCfg;
		FettersBaseTemplate fettersTemplate;
		SynConditionData temp;
		for (Iterator<Integer> itr = fettersMap.keySet().iterator(); itr.hasNext();) {
			temp = fettersMap.get(itr.next());
			List<Integer> conditionList = temp.getConditionList(); // 列表保存的是所有已经完成的英雄羁绊条件
			conditionCfg = fettersConditionCfgDao.getFettersConditionTemplateByUniqueId(conditionList.get(conditionList.size() - 1)); // 获取最后一个已经完成的条件
			if (conditionCfg == null) {
				continue;
			}

			fettersTemplate = fetterBaseCfgDao.getFettersBaseTemplateById(conditionCfg.getConditionId());
			if (fettersTemplate == null) {
				continue;
			}
			fighting += getFightingFunc.apply(conditionCfg.getConditionLevel(), fettersTemplate.getSeq());
		}
		return fighting;
	}

	private static class GetFetterFightingFunc implements IBIFunction<Integer, Integer, Integer> {

		private FightingCfgCsvDAOBase<? extends FightingCfgBase> fightingCfgDAO;

		public GetFetterFightingFunc(FightingCfgCsvDAOBase<? extends FightingCfgBase> pFightingCfgDAO) {
			fightingCfgDAO = pFightingCfgDAO;
		}

		@Override
		public Integer apply(Integer level, Integer seq) {
			FightingCfgBase fightingCfg = fightingCfgDAO.getByLevel(level);
			return fightingCfg.getFightingOfIndex(seq);
		}
	}

	@Override
	public int calc(Object param) {
		FettersFightingParam fettersParam = (FettersFightingParam) param;

		int fighting = 0;

		// 法宝羁绊
		List<Integer> magicFetters = fettersParam.getMagicFetters();
		if (magicFetters != null && !magicFetters.isEmpty()) {
			fighting += this.getEquipFetterFighting(magicFetters, getMagicFetterFightingFunc);
		}

		// 神器羁绊
		List<Integer> fixEquipFetters = fettersParam.getFixEquipFetters();
		if (fixEquipFetters != null && !fixEquipFetters.isEmpty()) {
			fighting += this.getEquipFetterFighting(fixEquipFetters, getFixEquipFetterFightingFunc);
		}

		// 英雄羁绊
		Map<Integer, SynConditionData> heroFetters = fettersParam.getHeroFetters();
		if (heroFetters != null && !heroFetters.isEmpty()) {
			fighting += this.getHeroFetterFighting(heroFetters, getHeroFetterFightingFunc);
		}

		return fighting;
	}
}