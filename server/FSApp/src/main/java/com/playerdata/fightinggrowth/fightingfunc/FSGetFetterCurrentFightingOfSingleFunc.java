package com.playerdata.fightinggrowth.fightingfunc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersBaseCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersBaseTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;
import com.rwbase.dao.fighting.FixEquipFetterFightingCfgDAO;
import com.rwbase.dao.fighting.HeroFetterFightingCfgDAO;
import com.rwbase.dao.fighting.MagicFetterFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FixEquipFetterFightingCfg;
import com.rwbase.dao.fighting.pojo.HeroFetterFightingCfg;
import com.rwbase.dao.fighting.pojo.MagicFetterFightingCfg;

public class FSGetFetterCurrentFightingOfSingleFunc implements IFunction<Hero, Integer>{
	
	private FetterMagicEquipCfgDao fetterMagicEquipCfgDao;
	private MagicFetterFightingCfgDAO magicFetterFightingCfgDao;
	private FixEquipFetterFightingCfgDAO fixEquipFetterFightingCfgDao;
	private FettersConditionCfgDAO fettersConditionCfgDao;
	private FettersBaseCfgDAO fetterBaseCfgDao;
	private HeroFetterFightingCfgDAO heroFetterFightingCfgDao;
	
	public FSGetFetterCurrentFightingOfSingleFunc() {
		fetterMagicEquipCfgDao = FetterMagicEquipCfgDao.getInstance();
		magicFetterFightingCfgDao = MagicFetterFightingCfgDAO.getInstance();
		fixEquipFetterFightingCfgDao = FixEquipFetterFightingCfgDAO.getInstnce();
		fettersConditionCfgDao = FettersConditionCfgDAO.getCfgDAO();
		fetterBaseCfgDao = FettersBaseCfgDAO.getCfgDAO();
		heroFetterFightingCfgDao = HeroFetterFightingCfgDAO.getInstance();
	}
	
	/**
	 * 
	 * 获取法宝羁绊的战斗力
	 * 
	 * @param player
	 * @param hero
	 * @return
	 */
	private int getMagicFetterFighting(Player player, Hero hero) {
		int fighting = 0;
		List<Integer> fetters = new ArrayList<Integer>();
		fetters.addAll(player.getMe_FetterMgr().getHeroFixEqiupFetter(hero.getModeId()));
		if(hero.isMainRole()) {
			fetters.addAll(player.getMe_FetterMgr().getMagicFetter());
		}
		if (fetters.size() > 0) {
			MagicEquipConditionCfg magicEquipConditionCfg;
			MagicFetterFightingCfg magicFetterFightingCfg;
			FixEquipFetterFightingCfg fixEquipFetterFightingCfg;
			for (int i = 0; i < fetters.size(); i++) {
				magicEquipConditionCfg = fetterMagicEquipCfgDao.getCfgById(String.valueOf(fetters.get(i)));
				if (magicEquipConditionCfg.isMagicFetter()) {
					// 法宝羁绊
					magicFetterFightingCfg = magicFetterFightingCfgDao.getCfgById(String.valueOf(magicEquipConditionCfg.getConditionLevel()));
					fighting += magicFetterFightingCfg.getFightingOfIndex(magicEquipConditionCfg.getSeq());
				} else {
					// 神器羁绊
					fixEquipFetterFightingCfg = fixEquipFetterFightingCfgDao.getCfgById(String.valueOf(magicEquipConditionCfg.getConditionLevel()));
					fighting += fixEquipFetterFightingCfg.getFightingOfIndex(magicEquipConditionCfg.getSeq());
				}
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
	private int getHeroFetterFighting(Player player, Hero hero) {
		int fighting = 0;
		SynFettersData fetterDatas = player.getHeroFettersByModelId(hero.getModeId());
		if (fetterDatas == null) {
			return 0;
		}
		Map<Integer, SynConditionData> openList = fetterDatas.getOpenList();
		if (openList.size() > 0) {
			FettersConditionTemplate conditionCfg;
			HeroFetterFightingCfg fightingCfg;
			FettersBaseTemplate fettersTemplate;
			SynConditionData temp;
			for (Iterator<SynConditionData> itr = openList.values().iterator(); itr.hasNext();) {
				temp = itr.next();
				List<Integer> conditionList = temp.getConditionList(); // 列表保存的是所有已经完成的英雄羁绊条件
				conditionCfg = fettersConditionCfgDao.getFettersConditionTemplateByUniqueId(conditionList.get(conditionList.size() - 1));
				fettersTemplate = fetterBaseCfgDao.getFettersBaseTemplateById(conditionCfg.getConditionId());
				fightingCfg = heroFetterFightingCfgDao.getByLevel(conditionCfg.getConditionLevel());
				fighting += fightingCfg.getFightingOfIndex(fettersTemplate.getSeq());
			}
		}
		return fighting;
	}

	@Override
	public Integer apply(Hero hero) {
		int fighting = 0;
		Player player = hero.getPlayer();
		if (!hero.isMainRole()) {
			// 不是主角才有英雄羁绊
			fighting += getHeroFetterFighting(player, hero);
		}
		fighting += getMagicFetterFighting(player, hero);
		return fighting;
	}

}
