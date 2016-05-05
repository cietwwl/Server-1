package com.playerdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.readonly.HeroMgrIF;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.hero.UserHeroDAO;
import com.rwbase.dao.hero.pojo.TableUserHero;
import com.rwbase.dao.hero.pojo.UserHerosDataHolder;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwproto.HeroServiceProtos.MsgHeroResponse;
import com.rwproto.HeroServiceProtos.eHeroResultType;
import com.rwproto.MsgDef.Command;

public class HeroMgr implements HeroMgrIF {

	private ConcurrentHashMap<String, Hero> m_HeroMap = new ConcurrentHashMap<String, Hero>();
	private UserHerosDataHolder userHerosDataHolder;

	private Player player;

	// 初始化
	public void init(Player playerP) {
		player = playerP;
		userHerosDataHolder = new UserHerosDataHolder(playerP.getUserId());
		initHeros();
	}

	// @Override
	public void notifyPlayerCreated(Player player) {
		TableUserHero userHeroTmp = new TableUserHero();
		userHeroTmp.setUserId(player.getUserId());
		UserHeroDAO.getInstance().update(userHeroTmp);
	}

	private void initHeros() {
		List<String> heroIds = userHerosDataHolder.get().getHeroIds();
		if (heroIds == null) {
			heroIds = new ArrayList<String>();
		}

		for (String heroIdTmp : heroIds) {
			eRoleType roleType = eRoleType.Hero;
			if (StringUtils.equals(heroIdTmp, player.getUserId())) {
				roleType = eRoleType.Player;
			}
			Hero hero = new Hero(player, roleType, heroIdTmp);
			m_HeroMap.put(heroIdTmp, hero);
		}
	}

	// @Override
	public void notifyPlayerLogin(Player player) {
	}

	public void regAttrChangeCallBack() {
		Collection<Hero> values = m_HeroMap.values();
		for (Hero hero : values) {
			hero.regAttrChangeCallBack();
		}
	}

	public Hero getMainRoleHero() {
		if (m_HeroMap.get(player.getUserId()) == null) {
			System.err.println(player.getUserId());
		}
		return m_HeroMap.get(player.getUserId());
	}

	public boolean isMainHero(Hero hero) {

		boolean isMainHero = false;
		if (hero != null) {
			isMainHero = StringUtils.equals(player.getUserId(), hero.getUUId());
		}
		return isMainHero;

	}

	/** 发送所用佣兵信息 **/
	public void synAllHeroToClient(int version) {
		userHerosDataHolder.syn(player, -1);
		Enumeration<Hero> heroMap = getHerosEnumeration();
		while (heroMap.hasMoreElements()) { // 佣兵信息的遍历
			Hero hero = heroMap.nextElement();
			hero.syn(-1);
		}

	}

	/** 发送添加佣兵后 更新操作信息 **/
	public void updateHeroIdToClient(String moderId) {// 优化需 发一个英雄数据
		MsgHeroResponse.Builder msgHeroResponse = MsgHeroResponse.newBuilder();
		msgHeroResponse.setEHeroResultType(eHeroResultType.UPDATE_HERO);
		msgHeroResponse.setModerId(moderId);
		player.SendMsg(Command.MSG_SEND_HERO_INFO, msgHeroResponse.build().toByteString());

	}

	public boolean save(boolean immediately) {
		userHerosDataHolder.flush();
		Collection<Hero> values = m_HeroMap.values();
		for (Hero hero : values) {
			hero.save(immediately);
		}
		return true;
	}

	// 返回用户拥有的佣兵信息
	/*
	 * public Map<String, Hero> getAllHeroMap(){ return m_HeroMap; }
	 */

	// modify:新增对m_HeroMap读操作的方法
	public Enumeration<Hero> getHerosEnumeration() {
		return m_HeroMap.elements();
	}

	public int getHerosSize() {
		return m_HeroMap.size();
	}

	public Hero getHeroByTemplateId(String templateId) {
		for (Iterator<Entry<String, Hero>> iterator = m_HeroMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Hero> entry = iterator.next();
			Hero tempHero = entry.getValue();
			if (tempHero.getTemplateId().equals(templateId)) {
				return tempHero;
			}
		}
		return null;
	}

	// public void addEnemy(String heroId,Hero hero){
	// m_HeroMap.put(heroId, hero);
	// }

	public List<String> getHeroIdList() {
		List<String> list = new ArrayList<String>();
		for (Hero data : m_HeroMap.values()) {
			list.add(data.getHeroData().getId());
		}
		return list;
	}

	// public Hero getHeroByHeroId(String heroId) {
	//
	// for (Hero data : m_HeroMap.values()) {
	// if (heroId.equals(data.getHeroData().getTemplateId())) {
	// return data;
	// }
	// }
	// return null;
	// }

	public void AddAllHeroExp(long exp) {
		for (Hero data : m_HeroMap.values()) {
			data.addHeroExp(exp);
		}
	}

	public Hero getHeroByModerId(int moderId) {
		for (Hero data : m_HeroMap.values()) {
			if (moderId == data.getHeroData().getModeId()) {
				return data;
			}
		}
		return null;
	}

	public Hero getHeroById(String uuid) {
		for (Hero data : m_HeroMap.values()) {
			if (uuid.equals(data.getHeroData().getId())) {
				return data;
			}
		}
		return null;
	}

	/*
	 * 增加佣兵，1-增加未拥有佣兵
	 */
	public Hero addMainRoleHero(Player playerP, RoleCfg playerCfg) {
		Hero hero = new Hero(playerP, eRoleType.Player, playerCfg, playerP.getUserId());
		m_HeroMap.put(hero.getUUId(), hero);
		userHerosDataHolder.get().addHeroId(hero.getUUId());
		userHerosDataHolder.update(player);
		return hero;
	}

	public Hero addHeroWhenCreatUser(String templateId) {
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

		Hero heroByModerId = this.getHeroByModerId(Integer.parseInt(modelId));
		if (heroByModerId != null) {
			player.getItemBagMgr().addItem(heroCfg.getSoulStoneId(), heroCfg.getTransform());
			updateHeroIdToClient(modelId);
			return null;
		}

		String roleUUId = UUID.randomUUID().toString();
		Hero hero = new Hero(player, eRoleType.Hero, heroCfg, roleUUId);
		m_HeroMap.put(hero.getUUId(), hero);

		userHerosDataHolder.get().addHeroId(hero.getUUId());
		userHerosDataHolder.update(player);
		hero.syn(-1);
		player.getTempAttribute().setHeroFightingChanged();
		return hero;
	}

	/*
	 * 增加佣兵，1-增加未拥有佣兵
	 */
	public Hero addHero(String templateId) {
		Hero hero = addHeroWhenCreatUser(templateId);
		// 任务
		if (hero != null) {
			TaskItemMgr taskMgr = player.getTaskMgr();
			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Count);
			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Star);
			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Quality);
			hero.regAttrChangeCallBack();
			player.getFresherActivityMgr().doCheck(eActivityType.A_HeroNum);
			player.getFresherActivityMgr().doCheck(eActivityType.A_HeroStar);
		}
		return hero;
	}

	/** 获取前四个最大战力 */
	public int getFightingTeam() {
		List<Hero> list = getMaxFightingHeros();
		int result = 0;
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).getFighting();
		}
		return result;
	}

	/** 获取所有用兵战力 */
	public int getFightingAll() {
		Enumeration<Hero> heroMap = getHerosEnumeration();
		int result = 0;
		while (heroMap.hasMoreElements()) { // 佣兵信息的遍历
			Hero hero = (Hero) heroMap.nextElement();
			result += hero.getFighting();
		}
		return result;
	}

	public int getStarAll() {
		Enumeration<Hero> heroMap = getHerosEnumeration();
		int result = 0;
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			result += hero.getHeroData().getStarLevel();
		}
		return result;
	}

	public int isHasStar(int star) {
		Enumeration<Hero> heroMap = getHerosEnumeration();
		int count = 0;
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			if (hero.getHeroData().getStarLevel() >= star) {
				count++;
			}
		}
		return count;
	}

	public int checkQuality(int quality) {
		Enumeration<Hero> heroMap = getHerosEnumeration();
		int count = 0;
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			RoleQualityCfg qualcfg = RoleQualityCfgDAO.getInstance().getConfig(hero.getHeroData().getQualityId());
			if (qualcfg != null && qualcfg.getQuality() >= quality) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 获得佣兵列表最大战斗力前4个佣兵+主角
	 * 
	 * @return 佣兵ID列表
	 */
	public List<Hero> getMaxFightingHeros() {
		ArrayList<Hero> list = new ArrayList<Hero>();
		String userId = player.getUserId();
		for (Hero data : m_HeroMap.values()) {
			if (!data.getUUId().equals(userId)) {
				list.add(data);
			}
		}
		int size = list.size();
		// 在原有逻辑上加一个判断，大于4个才进行排序
		if (size > 4) {
			Collections.sort(list, comparator);
			size = 4;
		}
		ArrayList<Hero> result = new ArrayList<Hero>();
		// 先加入主角
		result.add(player.getMainRoleHero());
		for (int i = 0; i < size; i++) {
			result.add(list.get(i));
		}
		return result;
	}

	/** 自定义战力比较器，所有HeroMgr公用一个对象即可 */
	private static Comparator<Hero> comparator = new Comparator<Hero>() {

		public int compare(Hero o1, Hero o2) {
			if (o1.getFighting() < o2.getFighting())
				return 1;
			if (o1.getFighting() > o2.getFighting())
				return -1;
			return 0;
		}
	};

	/**
	 * 获取所有的佣兵数据
	 * 
	 * @param comparator 排序的接口,如果不需要就直接填个Null
	 * @return
	 */
	public List<Hero> getAllHeros(Comparator<Hero> comparator) {
		List<Hero> list = new ArrayList<Hero>(m_HeroMap.values());
		if (comparator != null) {
			Collections.sort(list, comparator);
		}
		return list;
	}

}
