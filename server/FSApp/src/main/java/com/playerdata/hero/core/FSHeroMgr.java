package com.playerdata.hero.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.TaskItemMgr;
import com.playerdata.eRoleType;
import com.playerdata.hero.IHeroConsumer;
import com.playerdata.hero.core.consumer.FSAddExpToAllHeroConsumer;
import com.playerdata.hero.core.consumer.FSCountMatchTargetStarConsumer;
import com.playerdata.hero.core.consumer.FSCountQualityConsumer;
import com.playerdata.hero.core.consumer.FSGetAllHeroConsumer;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwproto.HeroServiceProtos.MsgHeroResponse;
import com.rwproto.HeroServiceProtos.eHeroResultType;
import com.rwproto.MsgDef.Command;

public class FSHeroMgr implements HeroMgr {
	
	public static final FSHeroMgr _INSTANCE = new FSHeroMgr();
	
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
		String userId = player.getTableUser().getUserId();
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
			mapItemStore = FSHeroDAO.getInstance().getOtherHeroMapItemStore(player.getTableUser().getUserId());
		} else {
			mapItemStore = FSHeroDAO.getInstance().getMainHeroMapItemStore(player.getTableUser().getUserId());
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
		hero.syn(-1);
		FSHeroThirdPartyDataMgr.getInstance().fireHeroAddedEvent(player, hero);
		return hero;
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
		return FSHeroDAO.getInstance().getOtherHeroMapItemStore(player.getTableUser().getUserId()).getSize() + 1;
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
		String userId = player.getTableUser().getUserId();
		MapItemStore<FSHero> mapItemStore = FSHeroDAO.getInstance().getOtherHeroMapItemStore(userId);
		List<String> idsOfOtherHeros = mapItemStore.getReadOnlyKeyList();
		List<String> list = new ArrayList<String>(idsOfOtherHeros);
		list.add(userId);
		return list;
	}
	
	@Override
	public void AddAllHeroExp(PlayerIF player, long exp) {
		this.loopAll(player.getTableUser().getUserId(), new FSAddExpToAllHeroConsumer(exp));
	}
	
	@Override
	public FSHero getHeroByModerId(PlayerIF player, int moderId) {
		Enumeration<FSHero> itr = FSHeroDAO.getInstance().getEnumeration(player.getTableUser().getUserId());
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
		String playerUserId = player.getTableUser().getUserId();
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
	
	public int getFightingTeam(PlayerIF player) {
		List<Hero> list = getMaxFightingHeros(player);
		int result = 0;
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).getFighting();
		}
		return result;
	}
	
	@Override
	public int getFightingAll(PlayerIF player) {
//		FSCalculateAllFightingConsumer consumer = new FSCalculateAllFightingConsumer();
//		this.loop(player.getTableUser().getUserId(), consumer);
//		return consumer.getTotalFighting();
		// 总战斗力改为储存在userGameData里面
		return player.getTableUserOther().getFightingAll();
	}

	@Override
	public int getStarAll(PlayerIF player) {
//		FSCountTotalStarLvConsumer consumer = new FSCountTotalStarLvConsumer();
//		this.loop(player.getTableUser().getUserId(), consumer);
//		return consumer.getTotalStarLv();
		return player.getTableUserOther().getStarAll();
	}

	@Override
	public int isHasStar(PlayerIF player, int star) {
		FSCountMatchTargetStarConsumer consumer = new FSCountMatchTargetStarConsumer(star);
		this.loopAll(player.getTableUser().getUserId(), consumer);
		return consumer.getCountResult();
	}

	@Override
	public int checkQuality(PlayerIF player, int quality) {
		FSCountQualityConsumer consumer = new FSCountQualityConsumer(quality);
		this.loopAll(player.getTableUser().getUserId(), consumer);
		return consumer.getCountResult();
	}
	
	public List<Hero> getMaxFightingHeros(PlayerIF player) {
		FSGetAllHeroConsumer consumer = new FSGetAllHeroConsumer(false);
		this.loopAll(player.getTableUser().getUserId(), consumer);
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
	public Enumeration<FSHero> getHerosEnumeration(PlayerIF player) {
		return FSHeroDAO.getInstance().getEnumeration(player.getTableUser().getUserId());
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
			hero.syn(-1);
			fightingAll += hero.getFighting();
			starAll += hero.getStarLevel();
		}
		player.getUserGameDataMgr().setFightingAll(fightingAll);
		player.getUserGameDataMgr().setStarAll(starAll);
	}
}
