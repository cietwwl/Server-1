package com.rwbase.dao.fetters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.playerdata.Player;
import com.rwbase.dao.fetters.pojo.IFettersCheckForceUseHeroId;
import com.rwbase.dao.fetters.pojo.IFettersSubCondition;
import com.rwbase.dao.fetters.pojo.IFettersSubRestrictCondition;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.fetters.pojo.cfg.FettersBaseCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersBaseCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersBaseTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;
import com.rwbase.dao.fetters.pojo.impl.checkforceuse.FettersForceDirectHeroIdImpl;
import com.rwbase.dao.fetters.pojo.impl.condition.ForceDirectHeroIdConditionImpl;
import com.rwbase.dao.fetters.pojo.impl.condition.HeroNumConditionImpl;
import com.rwbase.dao.fetters.pojo.impl.condition.WeakDirectHeroIdConditionImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroFightingCheckImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroLevelCheckImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroQualityCheckImpl;
import com.rwbase.dao.fetters.pojo.impl.subcondition.HeroStarCheckImpl;
import com.rwproto.HeroFetterProto.HeroFetterInfo;
import com.rwproto.HeroFetterProto.HeroFetterNotify;
import com.rwproto.HeroFetterProto.HeroFetterType;
import com.rwproto.MsgDef.Command;

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
		QUALITY(1, "英雄品质"), STAR(2, "英雄星数"), LEVEL(3, "英雄等级"), FIGHTING(4, "英雄战力");

		public final int type;// 类型
		public final String desc;// 描述

		private SubConditionType(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public static SubConditionType getEnum(int type) {
			for (SubConditionType s : SubConditionType.values()) {
				if (s.type == type) {
					return s;
				}
			}
			return null;
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
		List<FettersBaseTemplate> fettersBaseTemplateList = FettersBaseCfgDAO.getCfgDAO().getFettersBaseTemplateList();
		if (fettersBaseTemplateList == null || fettersBaseTemplateList.isEmpty()) {
			return;
		}

		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		for (int i = 0, size = fettersBaseTemplateList.size(); i < size; i++) {
			FettersBaseTemplate fettersBaseTmp = fettersBaseTemplateList.get(i);
			if (fettersBaseTmp == null) {
				continue;
			}

			List<Integer> fettersHeroIdList = fettersBaseTmp.getFettersHeroIdList();
			if (fettersHeroIdList == null || fettersHeroIdList.isEmpty()) {
				continue;
			}

			int heroModelId = fettersBaseTmp.getHeroModelId();
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

		fettersLinkHeroModelIdMap = Collections.unmodifiableMap(map);
	}

	/**
	 * 检查或者更新角色羁绊
	 * 
	 * @param player 角色
	 * @param heroModelId 英雄ModelId
	 * @param canSyn
	 * @param newIdList
	 */
	public static void checkOrUpdateHeroFetters(Player player, int heroModelId, boolean canSyn, List<Integer> newIdList) {
		List<FettersBaseTemplate> fettersList = FettersBaseCfgDAO.getCfgDAO().getFettersBaseTemplateListByHeroModelId(heroModelId);
		if (fettersList == null || fettersList.isEmpty()) {
			return;
		}

		// 完成的羁绊列表
		Map<Integer, List<FettersConditionTemplate>> matchFetters = new HashMap<Integer, List<FettersConditionTemplate>>();

		int fettersSize = fettersList.size();
		for (int i = 0; i < fettersSize; i++) {
			FettersBaseTemplate fettersBaseTemplate = fettersList.get(i);
			if (fettersBaseTemplate == null) {
				continue;
			}

			List<FettersConditionTemplate> matchList = checkFettersMatchCondition(player, fettersBaseTemplate);
			if (matchList == null || matchList.isEmpty()) {
				continue;
			}

			matchFetters.put(fettersBaseTemplate.getFettersId(), matchList);
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
		Set<Integer> currentSet = null;
		if (syn == null) {
			syn = new SynFettersData();
			syn.setHeroModelId(heroModelId);
		} else {
			currentSet = new HashSet<Integer>(syn.getOpenList().keySet());
		}
		syn.setOpenList(openFettersMap);
		if (newIdList != null) {
			if (currentSet != null) {
				Integer newId;
				for (Iterator<Integer> keyItr = openFettersMap.keySet().iterator(); keyItr.hasNext();) {
					newId = keyItr.next();
					if (!currentSet.contains(newId)) {
						newIdList.add(newId);
					}
				}
			} else {
				newIdList.addAll(openFettersMap.keySet());
			}
		}
		player.addOrUpdateHeroFetters(heroModelId, syn, canSyn);
	}

	/**
	 * 检查羁绊达成的条件列表
	 * 
	 * @param player
	 * @param fettersId
	 * @return
	 */
	private static List<FettersConditionTemplate> checkFettersMatchCondition(Player player, FettersBaseTemplate fettersBaseTemplate) {
		// 模版信息
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

		List<FettersSubConditionTemplate> subConditionList = fettersConditionTemplate.getSubConditionList();
		if (subConditionList == null || subConditionList.isEmpty()) {
			return true;
		}

		// 检查强制占用的英雄列表
		List<Integer> forceUseHeroIdList = new ArrayList<Integer>();// 强制占用的英雄Id列表

		int subConditionSize = subConditionList.size();
		for (int i = 0; i < subConditionSize; i++) {
			FettersSubConditionTemplate subConditionTmp = subConditionList.get(i);
			if (subConditionTmp == null) {
				continue;
			}

			IFettersCheckForceUseHeroId checkForceUserHeroId = getCheckForceUserHeroId(subConditionTmp.getSubConditionRestrictType());
			if (checkForceUserHeroId == null) {
				continue;
			}

			int checkForceUseHeroId = checkForceUserHeroId.checkForceUseHeroId(subConditionTmp);
			if (checkForceUseHeroId > 0) {
				forceUseHeroIdList.add(checkForceUseHeroId);
			}
		}

		// 检查每个条件是否可以达成
		for (int i = 0; i < subConditionSize; i++) {
			FettersSubConditionTemplate subConditionTmp = subConditionList.get(i);
			if (subConditionTmp == null) {
				return false;
			}

			int subConditionRestrictType = subConditionTmp.getSubConditionRestrictType();
			IFettersSubRestrictCondition check = getCheckSubRestrictCondition(subConditionRestrictType);
			if (check == null) {
				return false;
			}

			if (!check.match(player, fettersHeroIdList, forceUseHeroIdList, subConditionTmp)) {
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
		List<Integer> newIdList = new ArrayList<Integer>();
		for (int i = 0, size = list.size(); i < size; i++) {
			checkOrUpdateHeroFetters(player, list.get(i), true, newIdList);
		}
		if (newIdList.size() > 0) {
			// 过滤一下不相关的
			// 只发新增的
			FettersBaseCfgDAO dao = FettersBaseCfgDAO.getCfgDAO();
			for (Iterator<Integer> itr = newIdList.iterator(); itr.hasNext();) {
				FettersBaseCfg fettersBaseCfg = dao.getCfgById(String.valueOf(itr.next()));
				if (!fettersBaseCfg.getFettersHeroIdList().contains(changeHeroModelId)) {
					itr.remove();
				}
			}
		}
		sendFetterNotifyMsg(player, newIdList, HeroFetterType.HeroFetter);
	}
	
	public static void sendFetterNotifyMsg(Player player, Iterable<Integer> fetterIds, HeroFetterType type) {
		List<HeroFetterInfo> fetterInfoList = new ArrayList<HeroFetterInfo>();
		HeroFetterInfo.Builder builder = HeroFetterInfo.newBuilder();
		for (Integer fetterId : fetterIds) {
			builder.setFetterId(fetterId);
			builder.setType(type);
			fetterInfoList.add(builder.build());
		}
		if (fetterInfoList.size() > 0) {
			System.out.println("发送仙缘激活列表到客户端！" + fetterIds);
			HeroFetterNotify.Builder notifyBuilder = HeroFetterNotify.newBuilder();
			notifyBuilder.addAllFetterInfo(fetterInfoList);
			player.SendMsg(Command.MSG_FETTER_ACTIVITY_NOTIFY, notifyBuilder.build().toByteString());
		}
	}
}