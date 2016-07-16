package com.playerdata.hero.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import com.playerdata.Player;
import com.playerdata.TaskItemMgr;
import com.playerdata.eRoleType;
import com.playerdata.hero.IHero;
import com.playerdata.hero.IHeroConsumer;
import com.playerdata.hero.core.consumer.FSCalculateAllFightingConsumer;
import com.playerdata.hero.core.consumer.FSCountMatchTargetStarConsumer;
import com.playerdata.hero.core.consumer.FSCountQualityConsumer;
import com.playerdata.hero.core.consumer.FSCountTotalStarLvConsumer;
import com.playerdata.hero.core.consumer.FSGetAllHeroConsumer;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwproto.HeroServiceProtos.MsgHeroResponse;
import com.rwproto.HeroServiceProtos.eHeroResultType;
import com.rwproto.MsgDef.Command;

public class FSHeroMgr {
	
	private MapItemStore<FSHero> getMapItemStore(String userId) {
		return MapItemStoreFactory.getHeroDataCache().getMapItemStore(userId, FSHero.class);
	}
	
	private Enumeration<FSHero> getEnumeration(String userId) {
		MapItemStore<FSHero> mapItemStore = this.getMapItemStore(userId);
		return mapItemStore.getEnum();
	}
	
	private void loop(String userId, IHeroConsumer consumer) {
		Enumeration<FSHero> itr = this.getEnumeration(userId);
		while (itr.hasMoreElements()) {
			consumer.apply(itr.nextElement());
		}
	}
	
	private List<IHero> getAllHeros(Player player, Comparator<IHero> comparator, boolean includeMain) {
		FSGetAllHeroConsumer consumer = new FSGetAllHeroConsumer(includeMain);
		this.loop(player.getUserId(), consumer);
		List<IHero> list = consumer.getResultList();
		if(comparator != null) {
			Collections.sort(list, comparator);
		}
		return list;
	}

	public void init(Player playerP, boolean initHeros) {

		if(initHeros) {
			this.initHeros();
		}
	}
	
	public void notifyPlayerCreated(Player player) {
		
	}
	
	private void initHeros() {
		
	}
	
	public void notifyPlayerLogin(Player player) {
		
	}
	
	public void regAttrChangeCallBack() {
		// TODO 新的HeroMgr不需要这个方法
	}
	
	public IHero getMainHero(Player player) {
		Enumeration<FSHero> itr = this.getEnumeration(player.getUserId());
		while(itr.hasMoreElements()) {
			FSHero hero = itr.nextElement();
			if(hero.getRoleType() == eRoleType.Player) {
				return hero;
			}
		}
		return null;
	}
	
	public boolean isMainHero(IHero hero) {
		return hero.getRoleType() == eRoleType.Player;
	}
	
	public void syncAllHeroToClient(Player player, int version) {
		Enumeration<FSHero> itr = this.getEnumeration(player.getUserId());
		FSHero hero;
		while(itr.hasMoreElements()) {
			hero = itr.nextElement();
			hero.sync(version);
		}
	}
	
	/** 发送添加佣兵后 更新操作信息 **/
	public void updateHeroIdToClient(Player player, String moderId) {// 优化需 发一个英雄数据
		MsgHeroResponse.Builder msgHeroResponse = MsgHeroResponse.newBuilder();
		msgHeroResponse.setEHeroResultType(eHeroResultType.UPDATE_HERO);
		msgHeroResponse.setModerId(moderId);
		player.SendMsg(Command.MSG_SEND_HERO_INFO, msgHeroResponse.build().toByteString());
	}
	
	public boolean save(Player player, boolean immediately) {
		MapItemStore<FSHero> mapItemStore = this.getMapItemStore(player.getUserId());
		mapItemStore.flush(immediately);
		return true;
	}
	
	public Enumeration<? extends IHero> getHerosEnumeration(Player player) {
		return this.getMapItemStore(player.getUserId()).getEnum();
	}
	
	public int getHerosSize(Player player) {
		return this.getMapItemStore(player.getUserId()).getSize();
	}
	
	public IHero getHeroByTemplateId(Player player, String templateId) {
		Enumeration<FSHero> itr = this.getEnumeration(player.getUserId());
		IHero temp;
		while (itr.hasMoreElements()) {
			temp = itr.nextElement();
			if(temp.getTemplateId().equals(templateId)) {
				return temp;
			}
		}
		return null;
	}
	
	public List<String> getHeroIdList(Player player) {
		List<String> list = new ArrayList<String>();
		Enumeration<FSHero> itr = this.getEnumeration(player.getUserId());
		while (itr.hasMoreElements()) {
			list.add(itr.nextElement().getId());
		}
		return list;
	}
	
	public void AddAllHeroExp(Player player, long exp) {
		Enumeration<FSHero> itr = this.getEnumeration(player.getUserId());
		while (itr.hasMoreElements()) {
			itr.nextElement().addHeroExp(exp);
		}
	}
	
	public IHero getHeroByModerId(Player player, int moderId) {
		Enumeration<FSHero> itr = this.getEnumeration(player.getUserId());
		FSHero data;
		while (itr.hasMoreElements()) {
			data = itr.nextElement();
			if (moderId == data.getModelId()) {
				return data;
			}
		}
		return null;
	}
	
	public IHero getHeroById(Player player, String uuid) {
		MapItemStore<FSHero> mapItemStore = this.getMapItemStore(player.getUserId());
		return mapItemStore.getItem(uuid);
	}
	
	/**
	 * 
	 * 添加主英雄
	 * 
	 * @param playerP
	 * @param heroCfg
	 * @return
	 */
	public IHero addMainHero(Player playerP, RoleCfg heroCfg) {
		FSHero hero = new FSHero(playerP, eRoleType.Player, heroCfg, UUID.randomUUID().toString());
		MapItemStore<FSHero> mapItemStore = this.getMapItemStore(playerP.getUserId());
		mapItemStore.addItem(hero);
		return hero;
	}
	
	public IHero addHeroWhenCreatUser(Player player, String templateId) {
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

		IHero heroByModerId = this.getHeroByModerId(player, Integer.parseInt(modelId));
		if (heroByModerId != null) {
			player.getItemBagMgr().addItem(heroCfg.getSoulStoneId(), heroCfg.getTransform());
			updateHeroIdToClient(player, modelId);
			return null;
		}

		MapItemStore<FSHero> mapItemStore = this.getMapItemStore(player.getUserId());
		String roleUUId = UUID.randomUUID().toString();
		FSHero hero = new FSHero(player, eRoleType.Hero, heroCfg, roleUUId);
		mapItemStore.addItem(hero);

		FSHeroHolder.getInstance().updateUserHeros(player, mapItemStore.getReadOnlyKeyList());
		hero.sync(-1);
		FSHeroThirdPartyDataMgr.getInstance().fireHeroAddedEvent(player, hero);
		return hero;
	}
	
	public IHero addHero(Player player, String templateId) {
		IHero hero = addHeroWhenCreatUser(player, templateId);
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
	
	public int getFightingTeam(Player player) {
		List<IHero> list = getMaxFightingHeros(player);
		int result = 0;
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).getFighting();
		}
		return result;
	}
	
	public int getFightingAll(Player player) {
		FSCalculateAllFightingConsumer consumer = new FSCalculateAllFightingConsumer();
		this.loop(player.getUserId(), consumer);
		return consumer.getTotalFighting();
	}

	public int getStarAll(Player player) {
		FSCountTotalStarLvConsumer consumer = new FSCountTotalStarLvConsumer();
		this.loop(player.getUserId(), consumer);
		return consumer.getTotalStarLv();
	}

	public int isHasStar(Player player, int star) {
		FSCountMatchTargetStarConsumer consumer = new FSCountMatchTargetStarConsumer(star);
		this.loop(player.getUserId(), consumer);
		return consumer.getCountResult();
	}

	public int checkQuality(Player player, int quality) {
		FSCountQualityConsumer consumer = new FSCountQualityConsumer(quality);
		this.loop(player.getUserId(), consumer);
		return consumer.getCountResult();
	}
	
	public List<IHero> getMaxFightingHeros(Player player) {
		FSGetAllHeroConsumer consumer = new FSGetAllHeroConsumer(false);
		this.loop(player.getUserId(), consumer);
		List<IHero> targetList = consumer.getResultList();
		int size = targetList.size();
		ArrayList<IHero> result = new ArrayList<IHero>(size > 4 ? 5 : size + 1);
		result.add(consumer.getMainHero());
		if (size > 4) {
			Collections.sort(targetList, FSHeroFightPowerComparator.INSTANCE);
			result.addAll(targetList.subList(0, 4));
		} else {
			result.addAll(targetList);
		}
		return result;
	}
	
	public List<IHero> getAllheros(Player player, Comparator<IHero> comparator) {
		return this.getAllHeros(player, comparator, true);
	}
	
	public List<IHero> getAllHerosExceptMainRole(Player player, Comparator<IHero> comparator) {
		return this.getAllHeros(player, comparator, false);
	}
}
