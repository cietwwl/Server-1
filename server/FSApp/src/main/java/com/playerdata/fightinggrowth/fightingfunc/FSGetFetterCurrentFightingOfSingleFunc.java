package com.playerdata.fightinggrowth.fightingfunc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;
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

public class FSGetFetterCurrentFightingOfSingleFunc implements IFunction<Hero, Integer>{
	
	private FetterMagicEquipCfgDao fetterMagicEquipCfgDao;
	private FettersConditionCfgDAO fettersConditionCfgDao;
	private FettersBaseCfgDAO fetterBaseCfgDao;
	private IBIFunction<Integer, Integer, Integer> getFixEquipFetterFightingFunc;
	private IBIFunction<Integer, Integer, Integer> getMagicFetterFightingFunc;
	private IBIFunction<Integer, Integer, Integer> getHeroFetterFightingFunc;
	
	private static final FSGetFetterCurrentFightingOfSingleFunc _instance = new FSGetFetterCurrentFightingOfSingleFunc();
	
	public static final FSGetFetterCurrentFightingOfSingleFunc getInstnce() {
		return _instance;
	}
	
	protected FSGetFetterCurrentFightingOfSingleFunc() {
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
		if (fetters.size() > 0) {
			MagicEquipConditionCfg fetterMagicEquipCfg;
			for (int i = 0; i < fetters.size(); i++) {
				fetterMagicEquipCfg = fetterMagicEquipCfgDao.getCfgById(String.valueOf(fetters.get(i)));
				fighting += getFightingFunc.apply(fetterMagicEquipCfg.getConditionLevel(), fetterMagicEquipCfg.getSeq());
			}
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
	private int getHeroFetterFighting(Player player, Hero hero, IBIFunction<Integer, Integer, Integer> getFightingFunc) {
		int fighting = 0;
		SynFettersData fetterDatas = player.getHeroFettersByModelId(hero.getModeId());
		if (fetterDatas == null) {
			return 0;
		}
		Map<Integer, SynConditionData> fettersMap = fetterDatas.getOpenList();
		if (fettersMap.size() > 0) {
			FettersConditionTemplate conditionCfg;
			FettersBaseTemplate fettersTemplate;
			SynConditionData temp;
			for (Iterator<Integer> itr = fettersMap.keySet().iterator(); itr.hasNext();) {
				temp = fettersMap.get(itr.next());
				List<Integer> conditionList = temp.getConditionList(); // 列表保存的是所有已经完成的英雄羁绊条件
				conditionCfg = fettersConditionCfgDao.getFettersConditionTemplateByUniqueId(conditionList.get(conditionList.size() - 1)); // 获取最后一个已经完成的条件
				fettersTemplate = fetterBaseCfgDao.getFettersBaseTemplateById(conditionCfg.getConditionId());
				fighting += getFightingFunc.apply(conditionCfg.getConditionLevel(), fettersTemplate.getSeq());
			}
		}
		return fighting;
	}

	@Override
	public Integer apply(Hero hero) {
		Player player = hero.getPlayer();
		int fighting = 0;
		if (hero.isMainRole()) {
			// 主角才有法宝羁绊
			fighting += this.getEquipFetterFighting(player.getMe_FetterMgr().getMagicFetter(), getMagicFetterFightingFunc);
		} else {
			// 英雄才有英雄羁绊
			fighting += getHeroFetterFighting(player, hero, getHeroFetterFightingFunc);
		}
		// 神器羁绊
		fighting += this.getEquipFetterFighting(player.getMe_FetterMgr().getHeroFixEqiupFetter(hero.getModeId()), getFixEquipFetterFightingFunc);
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
}
