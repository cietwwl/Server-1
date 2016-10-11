package com.playerdata.hero.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.TaskItemMgr;
import com.playerdata.eRoleType;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.IHeroConsumer;
import com.playerdata.hero.core.consumer.FSAddExpToAllHeroConsumer;
import com.playerdata.hero.core.consumer.FSCountMatchTargetStarConsumer;
import com.playerdata.hero.core.consumer.FSCountQualityConsumer;
import com.playerdata.hero.core.consumer.FSGetAllHeroConsumer;
import com.playerdata.hero.core.consumer.FSGetMultipleHerosConsumer;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.hero.FSUserHeroGlobalDataDAO;
import com.rwbase.dao.hero.pojo.FSUserHeroGlobalData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.pojo.LevelCfg;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.HeroServiceProtos.MsgHeroResponse;
import com.rwproto.HeroServiceProtos.eHeroResultType;
import com.rwproto.MsgDef.Command;

public class FSHeroMgr implements HeroMgr {
	
	private static final FSHeroMgr _INSTANCE = new FSHeroMgr();
	
	public static final FSHeroMgr getInstance() {
		return _INSTANCE;
	}
	
	private void loopAll(String userId, IHeroConsumer consumer) {
		Enumeration<FSHero> itr = FSHeroDAO.getInstance().getEnumeration(userId);
		while (itr.hasMoreElements()) {
			consumer.apply(itr.nextElement());
		}
	}
	
	private List<Hero> getAllHeros(PlayerIF player, Comparator<Hero> comparator, boolean includeMain) {
		String userId = player.getUserId();
		FSGetAllHeroConsumer consumer = new FSGetAllHeroConsumer(includeMain);
		this.loopAll(userId, consumer);
		List<Hero> list = consumer.getResultList();
		if(comparator != null) {
			Collections.sort(list, comparator);
		}
		return list;
	}
	
	private FSHero createAndAddHeroToItemStore(Player player, eRoleType heroType, RoleCfg cfg, String uuid) {
		MapItemStore<FSHero> mapItemStore = null;
		if (heroType == eRoleType.Hero) {
			mapItemStore = FSHeroDAO.getInstance().getOtherHeroMapItemStore(player.getUserId());
		} else {
			mapItemStore = FSHeroDAO.getInstance().getMainHeroMapItemStore(player.getUserId());
		}
		FSHero hero = new FSHero(player, heroType, cfg, uuid);
		mapItemStore.addItem(hero);
		FSHeroThirdPartyDataMgr.getInstance().afterHeroInitAndAddedToCache(player, hero, cfg);
		return hero;
	}
	
	private FSHero addHeroInternal(Player player, String templateId) {
		boolean isTemplateId = templateId.indexOf("_") != -1;// 是否是模版Id
		String modelId = "";
		if (isTemplateId) {
			String[] strList = templateId.split("_");
			modelId = strList[0];
		} else {
			modelId = templateId;
		}

		RoleCfgDAO instance = RoleCfgDAO.getInstance();
		RoleCfg heroCfg = isTemplateId ? instance.getConfig(templateId) : instance.getCfgByModeID(modelId);
		if (heroCfg == null) {
			System.err.println("出现了没有的英雄模版Id：" + modelId + ",完整模版是：" + templateId);
			return null;
		}

		Hero heroByModerId = this.getHeroByModerId(player, Integer.parseInt(modelId));
		if (heroByModerId != null) {
			player.getItemBagMgr().addItem(heroCfg.getSoulStoneId(), heroCfg.getTransform());
			updateHeroIdToClient(player, modelId);
			return null;
		}

		String roleUUId = UUID.randomUUID().toString();
		FSHero hero = this.createAndAddHeroToItemStore(player, eRoleType.Hero, heroCfg, roleUUId);

		FSHeroHolder.getInstance().syncUserHeros(player, this.getHeroIdList(player));
		this.synHero(hero, -1);
		FSHeroThirdPartyDataMgr.getInstance().fireHeroAddedEvent(player, hero);
		return hero;
	}
	
	void syncFighting(Hero hero, int preFighting) {
		Player owner = this.getOwnerOfHero(hero);
		FSHeroHolder.getInstance().syncAttributes(hero, FSHero.CURRENT_SYNC_ATTR_VERSION);
		int nowFighting = hero.getFighting();
		if (preFighting != nowFighting) {
			// 保持那边的战斗力一致
			owner.getUserGameDataMgr().notifySingleFightingChange(nowFighting, preFighting);
			// 通知同步
			owner.getTempAttribute().setHeroFightingChanged();
			owner.getUserTmpGameDataFlag().setSynFightingAll(true);
			FSUserHeroGlobalDataMgr.getInstance().notifySingleFightingChange(owner.getUserId(), hero.getId(), nowFighting, preFighting);
		}
	}

	@Override
	public void init(PlayerIF playerP, boolean initHeros) {

//		if(initHeros) {
//			this.initHeros();
//		}
	}
	
//	private void initHeros() {
//		
//	}
	
	@Override
	public void regAttrChangeCallBack() {
		// TODO 新的HeroMgr不需要这个方法
	}
	
	/** 发送添加佣兵后 更新操作信息 **/
	public void updateHeroIdToClient(Player player, String moderId) {// 优化需 发一个英雄数据
		MsgHeroResponse.Builder msgHeroResponse = MsgHeroResponse.newBuilder();
		msgHeroResponse.setEHeroResultType(eHeroResultType.UPDATE_HERO);
		msgHeroResponse.setModerId(moderId);
		player.SendMsg(Command.MSG_SEND_HERO_INFO, msgHeroResponse.build().toByteString());
	}
	
	public boolean save(Player player, boolean immediately) {
		MapItemStore<FSHero> mapItemStore = FSHeroDAO.getInstance().getOtherHeroMapItemStore(player.getUserId());
		mapItemStore.flush(immediately);
		FSHeroDAO.getInstance().getMainHeroMapItemStore(player.getUserId()).flush(immediately);
		return true;
	}
	
	@Override
	public int getHerosSize(PlayerIF player) {
		return FSHeroDAO.getInstance().getOtherHeroMapItemStore(player.getUserId()).getSize() + 1;
	}
	
	@Override
	public Hero getHeroByTemplateId(Player player, String templateId) {
		Enumeration<FSHero> itr = FSHeroDAO.getInstance().getEnumeration(player.getUserId());
		Hero temp;
		while (itr.hasMoreElements()) {
			temp = itr.nextElement();
			if(temp.getTemplateId().equals(templateId)) {
				return temp;
			}
		}
		return null;
	}
	
	@Override
	public List<String> getHeroIdList(PlayerIF player) {
		String userId = player.getUserId();
		MapItemStore<FSHero> mapItemStore = FSHeroDAO.getInstance().getOtherHeroMapItemStore(userId);
		List<String> idsOfOtherHeros = mapItemStore.getReadOnlyKeyList();
		List<String> list = new ArrayList<String>(idsOfOtherHeros);
		list.add(userId);
		return list;
	}
	
	@Override
	public void AddAllHeroExp(PlayerIF player, long exp) {
		this.loopAll(player.getUserId(), new FSAddExpToAllHeroConsumer(exp));
	}
	
	@Override
	public FSHero getHeroByModerId(PlayerIF player, int moderId) {
		Enumeration<FSHero> itr = FSHeroDAO.getInstance().getEnumeration(player.getUserId());
		FSHero data;
		while (itr.hasMoreElements()) {
			data = itr.nextElement();
			if (moderId == data.getModeId()) {
				return data;
			}
		}
		return null;
	}
	
	@Override
	public FSHero getHeroById(PlayerIF player, String uuid) {
		MapItemStore<FSHero> mapItemStore = null;
		String playerUserId = player.getUserId();
		if (playerUserId.equals(uuid)) {
			mapItemStore = FSHeroDAO.getInstance().getMainHeroMapItemStore(playerUserId);
		} else {
			mapItemStore = FSHeroDAO.getInstance().getOtherHeroMapItemStore(playerUserId);
		}
		return mapItemStore.getItem(uuid);
	}
	
	
	public FSHero getHeroById(String userId, String uuid) {
		MapItemStore<FSHero> mapItemStore = null;
		if (userId.equals(uuid)) {
			mapItemStore = FSHeroDAO.getInstance().getMainHeroMapItemStore(userId);
		} else {
			mapItemStore = FSHeroDAO.getInstance().getOtherHeroMapItemStore(userId);
		}
		return mapItemStore.getItem(uuid);
	}
	
	public Hero addHeroWhenCreatUser(Player player, String templateId) {
		return this.addHeroInternal(player, templateId);
	}
	
	@Override
	public Hero addHero(Player player, String templateId) {
		Hero hero = addHeroInternal(player, templateId);
		// 任务
		if(hero != null) {
			TaskItemMgr taskMgr = player.getTaskMgr();
			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Count);
			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Star);
			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Quality);
			player.getFresherActivityMgr().doCheck(eActivityType.A_HeroNum);
			player.getFresherActivityMgr().doCheck(eActivityType.A_HeroStar);
		}
		return hero;
	}
	
	@Override
	public int getFightingTeam(PlayerIF player) {
		List<Hero> list = getMaxFightingHeros(player);
		int result = 0;
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).getFighting();
		}
		return result;
	}
	
	@Override
	public int getFightingTeam(String userId) {
		FSUserHeroGlobalData userHeroGlobalData = FSUserHeroGlobalDataDAO.getInstance().get(userId);
		List<Hero> heroList = new ArrayList<Hero>(5);
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(userId, eBattlePositionType.Normal_VALUE, EmBattlePositionKey.posCopy.getKey());
		if (embattleInfo != null) {
			List<EmbattleHeroPosition> heroPosition = embattleInfo.getPos();
			if (heroPosition != null && !heroPosition.isEmpty()) {
				for (int i = 0, size = heroPosition.size(); i < size; i++) {
					Hero hero = getHeroById(userId, heroPosition.get(i).getId());
					if (hero != null) {
						heroList.add(hero);
					}
				}
			}
		}
		if (heroList.isEmpty()) {
			heroList = getMaxFightingHeros(userId);
		}
		boolean recal = userHeroGlobalData.getFightingTeam() == 0;
		if (!recal) {
			List<String> heroIds = userHeroGlobalData.getFightingTeamHeroIdsRO();
			if (heroIds.size() > 0 && heroList.size() == heroIds.size()) {
				for (int i = 0, size = heroList.size(); i < size; i++) {
					if (!heroIds.contains(heroList.get(i).getId())) {
						recal = true;
						break;
					}
				}
			} else {
				recal = true;
			}
		}
		if (recal) {
			int result = 0;
			List<String> heroIds = new ArrayList<String>();
			Hero hero;
			for (int i = 0; i < heroList.size(); i++) {
				hero = heroList.get(i);
				result += hero.getFighting();
				heroIds.add(hero.getId());
			}
			userHeroGlobalData.setFightingTeam(result);
			userHeroGlobalData.setFightingTeamHeroIds(heroIds);
			FSUserHeroGlobalDataDAO.getInstance().update(userHeroGlobalData);
		}
		return userHeroGlobalData.getFightingTeam();
	}
	
	@Override
	public int getFightingAll(PlayerIF player) {
//		FSCalculateAllFightingConsumer consumer = new FSCalculateAllFightingConsumer();
//		this.loop(player.getTableUser().getUserId(), consumer);
//		return consumer.getTotalFighting();
		// 总战斗力改为储存在userGameData里面
		return player.getTableUserOther().getFightingAll();
		
		// 新的内容
//		FSUserHeroGlobalData userHeroGlobalData = FSUserHeroGlobalDataDAO.getInstance().get(player.getUserId());
//		if (userHeroGlobalData.getFightingAll() == 0) {
//			FSCalculateAllFightingConsumer consumer = new FSCalculateAllFightingConsumer();
//			this.loopAll(player.getTableUser().getUserId(), consumer);
//			userHeroGlobalData.setFightingAll(consumer.getTotalFighting());
//			FSUserHeroGlobalDataDAO.getInstance().update(userHeroGlobalData);
//		}
//		return userHeroGlobalData.getFightingAll();
	}

	@Override
	public int getStarAll(PlayerIF player) {
//		FSCountTotalStarLvConsumer consumer = new FSCountTotalStarLvConsumer();
//		this.loop(player.getTableUser().getUserId(), consumer);
//		return consumer.getTotalStarLv();
		return player.getTableUserOther().getStarAll();
		
		// 新的内容
//		FSUserHeroGlobalData userHeroGlobalData = FSUserHeroGlobalDataDAO.getInstance().get(player.getUserId());
//		if (userHeroGlobalData.getStartAll() == 0) {
//			FSCountTotalStarLvConsumer consumer = new FSCountTotalStarLvConsumer();
//			this.loopAll(player.getTableUser().getUserId(), consumer);
//			userHeroGlobalData.setFightingAll(consumer.getTotalStarLv());
//			FSUserHeroGlobalDataDAO.getInstance().update(userHeroGlobalData);
//		}
//		return userHeroGlobalData.getStartAll();
	}

	@Override
	public int isHasStar(PlayerIF player, int star) {
		FSCountMatchTargetStarConsumer consumer = new FSCountMatchTargetStarConsumer(star);
		this.loopAll(player.getUserId(), consumer);
		return consumer.getCountResult();
	}

	@Override
	public int checkQuality(PlayerIF player, int quality) {
		FSCountQualityConsumer consumer = new FSCountQualityConsumer(quality);
		this.loopAll(player.getUserId(), consumer);
		return consumer.getCountResult();
	}
	
	public List<Hero> getMaxFightingHeros(PlayerIF player) {
		return this.getMaxFightingHeros(player.getUserId());
	}
	
	@Override
	public List<Hero> getMaxFightingHeros(String userId) {
		FSGetAllHeroConsumer consumer = new FSGetAllHeroConsumer(false);
		this.loopAll(userId, consumer);
		List<Hero> targetList = consumer.getResultList();
		int size = targetList.size();
		ArrayList<Hero> result = new ArrayList<Hero>(size > 4 ? 5 : size + 1);
		result.add(consumer.getMainHero());
		if (size > 4) {
			Collections.sort(targetList, FSHeroFightPowerComparator.INSTANCE);
			result.addAll(targetList.subList(0, 4));
		} else {
			result.addAll(targetList);
		}
		return result;
	}
	
	@Override
	public List<Hero> getAllHeros(PlayerIF player, Comparator<Hero> comparator) {
		return this.getAllHeros(player, comparator, true);
	}
	
	@Override
	public List<Hero> getAllHerosExceptMainRole(Player player, Comparator<Hero> comparator) {
		return this.getAllHeros(player, comparator, false);
	}
	
	@Override
	public List<Hero> getHeros(PlayerIF player, List<String> heroIds) {
		FSGetMultipleHerosConsumer consumer = new FSGetMultipleHerosConsumer(heroIds);
		this.loopAll(player.getUserId(), consumer);
		return consumer.getResultHeros();
	}

	@Override
	public Enumeration<FSHero> getHerosEnumeration(PlayerIF player) {
		return FSHeroDAO.getInstance().getEnumeration(player.getUserId());
	}

	@Override
	public Hero getMainRoleHero(PlayerIF player) {
		String userId = player.getUserId();
		MapItemStore<FSHero> mapItemStore = FSHeroDAO.getInstance().getMainHeroMapItemStore(userId);
		return mapItemStore.getItem(userId);
	}

	@Override
	public Hero addMainRoleHero(Player playerP, RoleCfg playerCfg) {
		String userId = playerP.getUserId();
		FSHero hero = this.createAndAddHeroToItemStore(playerP, eRoleType.Player, playerCfg, userId);
		return hero;
	}
	
	@Override
	public void synAllHeroToClient(Player player, int version) {
		int fightingAll = 0;
		int starAll = 0;
		List<Hero> allHeros = this.getAllHeros(player, null, true);
		List<String> allIds = new ArrayList<String>(allHeros.size());
		for (Hero h : allHeros) {
			allIds.add(h.getId());
		}
		FSHeroHolder.getInstance().syncUserHeros(player, allIds);
		for (Hero hero : allHeros) {
			this.synHero(hero, -1);
			fightingAll += hero.getFighting();
			starAll += hero.getStarLevel();
		}
		FSUserHeroGlobalData globalData = FSUserHeroGlobalDataDAO.getInstance().get(player.getUserId());
		globalData.setFightingAll(fightingAll);
		globalData.setStartAll(starAll);
		FSUserHeroGlobalDataDAO.getInstance().update(globalData);
	}
	
	@Override
	public int addHeroExp(Hero hero, long heroExp) {
		Player player = this.getOwnerOfHero(hero);
		if(hero.isMainRole()) {
			// 2016-09-05 添加主角经验不能走这个流程，因为主角和英雄的添加规则有些不一样。
			GameLog.info("FSHero", player.getUserId(), "addHeroExp不能添加主角的经验！");
			return 0;
		}
		int maxLevel = player.getLevel();
		int currentLevel = hero.getLevel();
		long currentExp = hero.getExp();
		int oldLevel = currentLevel;
		LevelCfgDAO levelCfgDAO = LevelCfgDAO.getInstance();
		for (;;) {
			LevelCfg currentCfg = levelCfgDAO.getByLevel(currentLevel);
			if (currentCfg == null) {
				GameLog.error("hero", "addExp", "获取等级配置失败：" + currentLevel, null);
				break;
			}
			int upgradeExp = currentCfg.getHeroUpgradeExp();
			if (upgradeExp > currentExp) {
				long needExp = upgradeExp - currentExp;
				if (heroExp >= needExp) {
					// 升级并消耗部分经验
					heroExp -= needExp;
					currentExp = upgradeExp;
				} else {
					// 不能升级，把剩余经验用完
					currentExp += (int) heroExp;
					heroExp = 0;
					break;
				}
			}
			// 对等级进行判断
			if (currentLevel >= maxLevel) {
				currentExp = upgradeExp;
				break;
			}
			currentLevel++;
			currentExp = 0;
		}
		FSHeroBaseInfoMgr.getInstance().updateHeroLevelAndExp(player, (FSHero)hero, currentLevel, currentExp);
		return oldLevel == currentLevel ? 0 : 1;
	}
	
	@Override
	public int canUpgradeStar(Hero hero) {
		Player player = this.getOwnerOfHero(hero);
		int result = 0;
		RoleCfg rolecfg = this.getHeroCfg(hero);
		int soulStoneCount = player.getItemBagMgr().getItemCountByModelId(rolecfg.getSoulStoneId());
		if (soulStoneCount < rolecfg.getRisingNumber()) {
			result = -1;
		} else if (player.getUserGameDataMgr().getCoin() < rolecfg.getUpNeedCoin()) {
			result = -2;
		} else if (!StringUtils.isNotBlank(rolecfg.getNextRoleId())) {
			result = -3;
		}

		return result;
	}
	
	@Override
	public void gmEditHeroLevel(Hero hero, int pLevel) {
		FSHeroBaseInfoMgr.getInstance().updateHeroLevelAndExp(this.getOwnerOfHero(hero), (FSHero)hero, pLevel, hero.getExp());
	}
	
	@Override
	public void gmCheckActiveSkill(Hero hero) {
		RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getCfgById(hero.getQualityId());
		FSHeroThirdPartyDataMgr.getInstance().activeSkill(this.getOwnerOfHero(hero), hero.getId(), hero.getLevel(), cfg.getQuality());
	}
	
	@Override
	public RoleCfg getHeroCfg(Hero hero) {
		return RoleCfgDAO.getInstance().getCfgById(hero.getTemplateId());
	}
	
	@Override
	public LevelCfg getLevelCfg(Hero hero) {
		return LevelCfgDAO.getInstance().getCfgById(String.valueOf(hero.getLevel()));
	}
	
	@Override
	public void synHero(Hero hero, int version) {
		FSHero fshero = (FSHero) hero;
		Player player = this.getOwnerOfHero(fshero);
		fshero.firstInit();
		FSHeroHolder.getInstance().synBaseInfo(player, hero);
		FSHeroThirdPartyDataMgr.getInstance().notifySync(player, fshero, version);
		FSHeroHolder.getInstance().syncAttributes(fshero, version);
	}
	
	@Override
	public Player getOwnerOfHero(Hero hero) {
		return PlayerMgr.getInstance().find(hero.getOwnerUserId());
	}
	
	@Override
	public int getHeroQuality(Hero hero) {
		RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getCfgById(hero.getQualityId());
		if (cfg != null) {
			return cfg.getQuality();
		} else {
			return 0;
		}
	}
}
