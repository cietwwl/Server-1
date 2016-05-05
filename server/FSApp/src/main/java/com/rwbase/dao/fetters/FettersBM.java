package com.rwbase.dao.fetters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Player;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrDataHelper;
import com.rwbase.common.attrdata.AttrDataType;
import com.rwbase.dao.fetters.pojo.IFettersCheckForceUseHeroId;
import com.rwbase.dao.fetters.pojo.IFettersSubCondition;
import com.rwbase.dao.fetters.pojo.IFettersSubRestrictCondition;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersBaseCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersSubConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.HeroFettersCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersBaseTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.HeroFettersTemplate;
import com.rwbase.dao.fetters.pojo.impl.checkforceuse.FettersForceDirectHeroIdImpl;
import com.rwbase.dao.fetters.pojo.impl.condition.ForceDirectHeroIdConditionImpl;
import com.rwbase.dao.fetters.pojo.impl.condition.HeroNumConditionImpl;
import com.rwbase.dao.fetters.pojo.impl.condition.WeakDirectHeroIdConditionImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroFightingCheckImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroLevelCheckImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroQualityCheckImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroStarCheckImpl;

/*
 * @author HC
 * @date 2016年4月27日 上午10:43:34
 * @Description 
 */
public class FettersBM {

	/** 条件强制限定类型 */
	public static enum SubConditionRestrictType {
		WEAK_DIRCET_HERO_MODEL_ID(1, "弱指定英雄Id"), FORCE_DIRECT_HERO_MODEL_ID(2, "强指定英雄Id"), HERO_NUM(3, "英雄数量");

		public final int type;// 类型
		public final String desc;// 描述

		SubConditionRestrictType(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}
	}

	/** 子条件类型 */
	public static enum SubConditionType {
		HERO_QUALITY(1, "英雄品质"), HERO_STAR(2, "英雄星数"), HERO_LEVEL(3, "英雄等级"), HERO_FIGHTING(4, "英雄战力");

		public final int type;// 类型
		public final String desc;// 描述

		private SubConditionType(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}
	}

	/** <变化的英雄ModelId,List<羁绊相关的英雄ModelId>> */
	private static Map<Integer, List<Integer>> fettersLinkHeroModelIdMap = new HashMap<Integer, List<Integer>>();
	/** <子条件限定类型,检查强制占用的实现> */
	private static Map<Integer, IFettersCheckForceUseHeroId> checkForceUseHeroIdMap = new HashMap<Integer, IFettersCheckForceUseHeroId>();
	/** <子条件类型,子条件检查的实现> */
	private static Map<Integer, IFettersSubCondition> checkSubConditionMap = new HashMap<Integer, IFettersSubCondition>();
	/** <子条件限定类型,限定类型检查的实现> */
	private static Map<Integer, IFettersSubRestrictCondition> checkSubRestrictConditionMap = new HashMap<Integer, IFettersSubRestrictCondition>();

	/**
	 * 初始化羁绊模块需要用到的各种检查
	 */
	public static void init() {
		// 检查强指占用英雄Id的检查
		initCheckForceUserHeroId();
		// 检查子条件限制类型是否达成
		initCheckRestrictCondition();
		// 检查子条件是否可以达成
		initCheckSubCondition();
		// 初始化羁绊关联
		initHeroFetterLink();
	}

	/**
	 * 检查羁绊模块子条件中强制占用的英雄Id
	 */
	private static void initCheckForceUserHeroId() {
		// 强指定英雄Id的检查注册
		IFettersCheckForceUseHeroId fettersForceDirectHeroIdImpl = new FettersForceDirectHeroIdImpl();
		checkForceUseHeroIdMap.put(fettersForceDirectHeroIdImpl.getCheckConditionType(), fettersForceDirectHeroIdImpl);
	}

	/**
	 * 初始化检查子限定条件
	 */
	private static void initCheckRestrictCondition() {
		// 强指定英雄Id
		IFettersSubRestrictCondition forceHeroId = new ForceDirectHeroIdConditionImpl();
		checkSubRestrictConditionMap.put(forceHeroId.getSubRestrictConditionType(), forceHeroId);
		// 弱指定英雄Id
		IFettersSubRestrictCondition weakHeroId = new WeakDirectHeroIdConditionImpl();
		checkSubRestrictConditionMap.put(weakHeroId.getSubRestrictConditionType(), weakHeroId);
		// 指定英雄数量
		IFettersSubRestrictCondition heroNum = new HeroNumConditionImpl();
		checkSubRestrictConditionMap.put(heroNum.getSubRestrictConditionType(), heroNum);
	}

	/**
	 * 初始化检查子条件
	 */
	private static void initCheckSubCondition() {
		// 英雄品质
		IFettersSubCondition heroQualityCheck = new HeroQualityCheckImpl();
		checkSubConditionMap.put(heroQualityCheck.getSubConditionType(), heroQualityCheck);
		// 英雄星级
		IFettersSubCondition heroStartCheck = new HeroStarCheckImpl();
		checkSubConditionMap.put(heroStartCheck.getSubConditionType(), heroStartCheck);
		// 英雄等级
		IFettersSubCondition heroLevelCheck = new HeroLevelCheckImpl();
		checkSubConditionMap.put(heroLevelCheck.getSubConditionType(), heroLevelCheck);
		// 英雄战力
		IFettersSubCondition heroFightingCheck = new HeroFightingCheckImpl();
		checkSubConditionMap.put(heroFightingCheck.getSubConditionType(), heroFightingCheck);
	}

	/**
	 * 获取检查强制引用的英雄Id接口实现
	 * 
	 * @param checkType
	 * @return
	 */
	private static IFettersCheckForceUseHeroId getCheckForceUserHeroId(int checkType) {
		if (checkForceUseHeroIdMap == null) {
			return null;
		}

		return checkForceUseHeroIdMap.get(checkType);
	}

	/**
	 * 获取检查下子条件的接口实现
	 * 
	 * @param subConditionType
	 * @return
	 */
	public static IFettersSubCondition getCheckSubCondition(int subConditionType) {
		if (checkSubConditionMap == null) {
			return null;
		}

		return checkSubConditionMap.get(subConditionType);
	}

	/**
	 * 检查子限定条件的接口实现
	 * 
	 * @param subRestrictConditionType
	 * @return
	 */
	private static IFettersSubRestrictCondition getCheckSubRestrictCondition(int subRestrictConditionType) {
		if (checkSubRestrictConditionMap == null) {
			return null;
		}

		return checkSubRestrictConditionMap.get(subRestrictConditionType);
	}

	/**
	 * 初始化羁绊关联
	 */
	private static void initHeroFetterLink() {
		HeroFettersCfgDAO fettersCfgDAO = HeroFettersCfgDAO.getCfgDAO();
		List<HeroFettersTemplate> heroFettersTemplateList = fettersCfgDAO.getHeroFettersTemplateList();
		if (heroFettersTemplateList == null || heroFettersTemplateList.isEmpty()) {
			return;
		}

		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();

		FettersBaseCfgDAO fettersBaseCfgDAO = FettersBaseCfgDAO.getCfgDAO();
		for (int i = 0, size = heroFettersTemplateList.size(); i < size; i++) {
			HeroFettersTemplate tmp = heroFettersTemplateList.get(i);
			if (tmp == null) {
				continue;
			}

			List<Integer> fettersIdList = tmp.getFettersIdList();
			if (fettersIdList == null || fettersIdList.isEmpty()) {
				continue;
			}

			int heroModelId = tmp.getHeroModelId();

			for (int j = 0, idSize = fettersIdList.size(); j < idSize; j++) {
				int fettersId = fettersIdList.get(j);
				FettersBaseTemplate fettersBaseTmp = fettersBaseCfgDAO.getFettersBaseTemplateById(fettersId);
				if (fettersBaseTmp == null) {
					continue;
				}

				List<Integer> fettersHeroIdList = fettersBaseTmp.getFettersHeroIdList();
				if (fettersHeroIdList == null || fettersHeroIdList.isEmpty()) {
					continue;
				}

				for (int k = 0, heroSize = fettersHeroIdList.size(); k < heroSize; k++) {
					int fettersHeroModelId = fettersHeroIdList.get(k);
					List<Integer> list = map.get(fettersHeroModelId);
					if (list == null) {
						list = new ArrayList<Integer>();
						map.put(fettersHeroModelId, list);
					}

					if (!list.contains(heroModelId)) {
						list.add(heroModelId);
					}
				}
			}
		}

		fettersLinkHeroModelIdMap = Collections.unmodifiableMap(map);
	}

	/**
	 * 检查或者更新角色羁绊
	 * 
	 * @param player 角色
	 * @param heroModelId 英雄ModelId
	 */
	public static void checkOrUpdateHeroFetters(Player player, int heroModelId, boolean canSyn) {
		HeroFettersTemplate heroFettersTemplate = HeroFettersCfgDAO.getCfgDAO().getHeroFettersTemplateByModelId(heroModelId);
		if (heroFettersTemplate == null) {
			return;
		}

		List<Integer> fettersIdList = heroFettersTemplate.getFettersIdList();// 羁绊Id列表
		if (fettersIdList == null || fettersIdList.isEmpty()) {
			return;
		}

		// 完成的羁绊列表
		Map<Integer, List<FettersConditionTemplate>> matchFetters = new HashMap<Integer, List<FettersConditionTemplate>>();

		int fettersIdSize = fettersIdList.size();
		for (int i = 0; i < fettersIdSize; i++) {
			int fettersId = fettersIdList.get(i);
			List<FettersConditionTemplate> matchList = checkFettersMatchCondition(player, fettersId);
			if (matchList == null || matchList.isEmpty()) {
				continue;
			}

			matchFetters.put(fettersId, matchList);
		}

		// 没有任何羁绊
		if (matchFetters.isEmpty()) {
			return;
		}

		Map<Integer, SynConditionData> openFettersMap = new HashMap<Integer, SynConditionData>();// 已经开启的羁绊列表

		for (Entry<Integer, List<FettersConditionTemplate>> e : matchFetters.entrySet()) {
			List<FettersConditionTemplate> list = e.getValue();
			if (list == null || list.isEmpty()) {
				continue;
			}

			List<Integer> openList = new ArrayList<Integer>();// 开启列表

			for (int i = 0, size = list.size(); i < size; i++) {
				FettersConditionTemplate tmp = list.get(i);
				if (tmp == null) {
					continue;
				}

				openList.add(tmp.getUniqueId());
			}

			if (openList != null && !openList.isEmpty()) {
				int fettersId = e.getKey();
				SynConditionData synCondition = new SynConditionData();
				synCondition.setConditionList(openList);

				openFettersMap.put(fettersId, synCondition);
			}
		}

		// 推送到前台
		/**
		 * <pre>
		 * 推送数据包含
		 * 英雄模版Id
		 * 激活羁绊的列表
		 * {
		 * 羁绊Id
		 * {
		 * 条件唯一Id
		 * }
		 * }
		 * </pre>
		 */
		if (openFettersMap.isEmpty()) {
			return;
		}

		// 推送数据
		SynFettersData syn = player.getHeroFettersByModelId(heroModelId);
		if (syn == null) {
			syn = new SynFettersData();
			syn.setHeroModelId(heroModelId);
		}
		syn.setOpenList(openFettersMap);
		player.addOrUpdateHeroFetters(heroModelId, syn, canSyn);
	}

	/**
	 * 获取羁绊的属性
	 * 
	 * @param openList
	 * @return
	 */
	public static Map<Integer, AttrData> calcHeroFettersAttr(Map<Integer, SynConditionData> openList) {
		Map<Integer, Float> attrDataMap = new HashMap<Integer, Float>();// 固定值
		Map<Integer, Float> precentAttrDataMap = new HashMap<Integer, Float>();// 百分比值

		FettersConditionCfgDAO cfgDAO = FettersConditionCfgDAO.getCfgDAO();

		for (Entry<Integer, SynConditionData> e : openList.entrySet()) {
			SynConditionData value = e.getValue();
			if (value == null) {
				continue;
			}

			List<Integer> list = value.getConditionList();
			if (list == null || list.isEmpty()) {
				continue;
			}

			for (int i = 0, size = list.size(); i < size; i++) {
				FettersConditionTemplate tmp = cfgDAO.getFettersConditionTemplateByUniqueId(list.get(i));
				if (tmp == null) {
					continue;
				}

				// 固定值
				Map<Integer, Float> attrData = tmp.getFettersAttrDataMap();
				if (attrData != null && !attrData.isEmpty()) {
					for (Entry<Integer, Float> entry : attrData.entrySet()) {
						Integer key = entry.getKey();
						Float hasValue = attrDataMap.get(key);
						if (hasValue == null) {
							attrDataMap.put(key, entry.getValue());
						} else {
							attrDataMap.put(key, entry.getValue() + hasValue);
						}
					}
				}

				// 百分比值
				Map<Integer, Float> precentAttrData = tmp.getFettersPrecentAttrDataMap();
				if (precentAttrData != null && !precentAttrData.isEmpty()) {
					for (Entry<Integer, Float> entry : precentAttrData.entrySet()) {
						Integer key = entry.getKey();
						Float hasValue = precentAttrDataMap.get(key);
						if (hasValue == null) {
							precentAttrDataMap.put(key, entry.getValue());
						} else {
							precentAttrDataMap.put(key, entry.getValue() + hasValue);
						}
					}
				}
			}
		}

		if (attrDataMap.isEmpty() && precentAttrDataMap.isEmpty()) {
			return null;
		}

		Map<Integer, AttrData> map = new HashMap<Integer, AttrData>(2);
		map.put(AttrDataType.ATTR_DATA_TYPE.type, AttrDataHelper.parseMap2AttrData(attrDataMap));
		map.put(AttrDataType.ATTR_DATA_PRECENT_TYPE.type, AttrDataHelper.parseMap2AttrData(precentAttrDataMap));

		return map;
	}

	/**
	 * 检查羁绊达成的条件列表
	 * 
	 * @param player
	 * @param fettersId
	 * @return
	 */
	private static List<FettersConditionTemplate> checkFettersMatchCondition(Player player, int fettersId) {
		FettersBaseCfgDAO fettersBaseCfgDAO = FettersBaseCfgDAO.getCfgDAO();// 羁绊基础配置DAO

		// 模版信息
		FettersBaseTemplate fettersBaseTemplate = fettersBaseCfgDAO.getFettersBaseTemplateById(fettersId);
		if (fettersBaseTemplate == null) {
			return null;
		}

		// 条件列表
		List<Integer> fettersConditionList = fettersBaseTemplate.getFettersConditionList();
		if (fettersConditionList == null || fettersConditionList.isEmpty()) {
			return null;
		}

		// 可以构成羁绊的英雄Id列表
		List<Integer> fettersHeroIdList = fettersBaseTemplate.getFettersHeroIdList();// 要用到的佣兵Id
		if (fettersHeroIdList == null || fettersHeroIdList.isEmpty()) {
			return null;
		}

		List<FettersConditionTemplate> tmpList = null;
		// 检查包含的条件信息
		int fettersConditionSize = fettersConditionList.size();
		for (int i = 0; i < fettersConditionSize; i++) {
			int conditionId = fettersConditionList.get(i);
			FettersConditionTemplate tmp = checkConditionMatch(player, conditionId, fettersHeroIdList);
			if (tmp == null) {// 有任何一个没达到就是失败
				return null;
			}

			if (tmpList == null) {
				tmpList = new ArrayList<FettersConditionTemplate>();
			}

			tmpList.add(tmp);
		}

		return tmpList;
	}

	/**
	 * 
	 * @param conditionId　条件Id
	 * @param fettersHeroIdList 可以构成羁绊的英雄Id列表
	 * @return
	 */
	private static FettersConditionTemplate checkConditionMatch(Player player, int conditionId, List<Integer> fettersHeroIdList) {
		FettersConditionCfgDAO fettersConditionCfgDAO = FettersConditionCfgDAO.getCfgDAO();// 羁绊条件配置DAO
		List<FettersConditionTemplate> fettersCondition = fettersConditionCfgDAO.getFettersConditionListById(conditionId);
		if (fettersCondition == null || fettersCondition.isEmpty()) {
			return null;
		}

		FettersConditionTemplate returnTmp = null;
		// 检查强制占用的英雄Id
		int size = fettersCondition.size();
		for (int i = 0; i < size; i++) {
			FettersConditionTemplate fettersConditionTemplate = fettersCondition.get(i);
			if (fettersConditionTemplate == null) {
				continue;
			}

			if (!checkSubConditionMatch(player, fettersConditionTemplate, fettersHeroIdList)) {
				continue;
			}

			returnTmp = fettersConditionTemplate;
		}

		return returnTmp;
	}

	/**
	 * 检查某个条件被强制引用的英雄Id列表
	 * 
	 * @param fettersConditionTemplate
	 * @return
	 */
	private static boolean checkSubConditionMatch(Player player, FettersConditionTemplate fettersConditionTemplate, List<Integer> fettersHeroIdList) {
		if (fettersConditionTemplate == null) {
			return false;
		}

		FettersSubConditionCfgDAO cfgDAO = FettersSubConditionCfgDAO.getCfgDAO();

		List<Integer> subConditionIdList = fettersConditionTemplate.getSubConditionIdList();

		// 检查强制占用的英雄列表
		List<Integer> forceUseHeroIdList = new ArrayList<Integer>();// 强制占用的英雄Id列表

		int subConditionSize = subConditionIdList.size();
		for (int i = 0; i < subConditionSize; i++) {
			int subConditionId = subConditionIdList.get(i);
			FettersSubConditionTemplate subConditionTmp = cfgDAO.getFettersSubConditionTemplateById(subConditionId);
			if (subConditionTmp == null) {
				continue;
			}

			IFettersCheckForceUseHeroId checkForceUserHeroId = getCheckForceUserHeroId(subConditionTmp.getSubConditionRestrictType());
			if (checkForceUserHeroId == null) {
				continue;
			}

			int checkForceUseHeroId = checkForceUserHeroId.checkForceUseHeroId(subConditionId);
			if (checkForceUseHeroId > 0) {
				forceUseHeroIdList.add(checkForceUseHeroId);
			}
		}

		// 检查每个条件是否可以达成
		for (int i = 0; i < subConditionSize; i++) {
			int subConditionId = subConditionIdList.get(i);
			FettersSubConditionTemplate subConditionTmp = cfgDAO.getFettersSubConditionTemplateById(subConditionId);
			if (subConditionTmp == null) {
				return false;
			}

			int subConditionRestrictType = subConditionTmp.getSubConditionRestrictType();
			IFettersSubRestrictCondition check = getCheckSubRestrictCondition(subConditionRestrictType);
			if (check == null) {
				return false;
			}

			if (!check.match(player, fettersHeroIdList, forceUseHeroIdList, subConditionId)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 当相关的英雄某个属性修改的时候，通知去检查相关联的羁绊
	 * 
	 * @param player
	 * @param changeHeroModelId
	 */
	public static void whenHeroChange(Player player, int changeHeroModelId) {
		List<Integer> list = fettersLinkHeroModelIdMap.get(changeHeroModelId);
		if (list == null || list.isEmpty()) {
			return;
		}

		for (int i = 0, size = list.size(); i < size; i++) {
			checkOrUpdateHeroFetters(player, list.get(i), true);
		}
	}
}