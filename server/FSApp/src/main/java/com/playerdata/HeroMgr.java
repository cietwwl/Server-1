package com.playerdata;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.user.pojo.LevelCfg;

//public class HeroMgr implements HeroMgrIF {
//	
//	private static final Map<String, Hero> _initingHeros = new ConcurrentHashMap<String, Hero>(); // 重构中临时处理
//
//	private ConcurrentHashMap<String, Hero> m_HeroMap = new ConcurrentHashMap<String, Hero>();
//	private UserHerosDataHolder userHerosDataHolder;
//
//	private Player player;
//	
//	static void addInitingHero(String heroId, Hero hero) {
//		_initingHeros.put(heroId, hero);
//	}
//	
//	static void removeInitingHero(Hero hero) {
//		_initingHeros.remove(hero.getUUId());
//	}
//
//	// 初始化
//	public void init(Player playerP, boolean initHeros) {
//		player = playerP;
//		userHerosDataHolder = new UserHerosDataHolder(playerP.getUserId());
//		if (initHeros) {
//			initHeros();
//		}
//	}
//
//	// @Override
//	public void notifyPlayerCreated(Player player) {
//		// TableUserHero userHeroTmp = new TableUserHero();
//		// userHeroTmp.setUserId(player.getUserId());
//		// UserHeroDAO.getInstance().update(userHeroTmp);
//	}
//
//	private void initHeros() {
//		List<String> heroIds = userHerosDataHolder.get().getHeroIds();
//		if (heroIds == null) {
//			heroIds = new ArrayList<String>();
//		}
//
//		for (String heroIdTmp : heroIds) {
//			eRoleType roleType = eRoleType.Hero;
//			if (StringUtils.equals(heroIdTmp, player.getUserId())) {
//				roleType = eRoleType.Player;
//			}
//			Hero hero = new Hero(player, roleType, heroIdTmp);
//			m_HeroMap.put(heroIdTmp, hero);
//		}
//	}
//
//	// @Override
//	public void notifyPlayerLogin(Player player) {
//	}
//
//	public void regAttrChangeCallBack() {
//		Collection<Hero> values = m_HeroMap.values();
//		for (Hero hero : values) {
//			hero.regAttrChangeCallBack();
//		}
//	}
//
//	public Hero getMainRoleHero() {
//		if (m_HeroMap.get(player.getUserId()) == null) {
//			System.err.println(player.getUserId());
//		}
//		return m_HeroMap.get(player.getUserId());
//	}
//
//	public boolean isMainHero(Hero hero) {
//
//		boolean isMainHero = false;
//		if (hero != null) {
//			isMainHero = StringUtils.equals(player.getUserId(), hero.getUUId());
//		}
//		return isMainHero;
//
//	}
//
//	/** 发送所用佣兵信息 **/
//	public void synAllHeroToClient(int version) {
//		userHerosDataHolder.syn(player, -1);
//		Enumeration<Hero> heroMap = getHerosEnumeration();
//		while (heroMap.hasMoreElements()) { // 佣兵信息的遍历
//			Hero hero = heroMap.nextElement();
//			hero.syn(-1);
//		}
//
//	}
//
//	/** 发送添加佣兵后 更新操作信息 **/
//	public void updateHeroIdToClient(String moderId) {// 优化需 发一个英雄数据
//		MsgHeroResponse.Builder msgHeroResponse = MsgHeroResponse.newBuilder();
//		msgHeroResponse.setEHeroResultType(eHeroResultType.UPDATE_HERO);
//		msgHeroResponse.setModerId(moderId);
//		player.SendMsg(Command.MSG_SEND_HERO_INFO, msgHeroResponse.build().toByteString());
//
//	}
//
//	public boolean save(boolean immediately) {
//		userHerosDataHolder.flush();
//		Collection<Hero> values = m_HeroMap.values();
//		for (Hero hero : values) {
//			hero.save(immediately);
//		}
//		return true;
//	}
//
//	// 返回用户拥有的佣兵信息
//	/*
//	 * public Map<String, Hero> getAllHeroMap(){ return m_HeroMap; }
//	 */
//
//	// modify:新增对m_HeroMap读操作的方法
//	public Enumeration<Hero> getHerosEnumeration() {
//		return m_HeroMap.elements();
//	}
//
//	public int getHerosSize() {
//		return m_HeroMap.size();
//	}
//
//	public Hero getHeroByTemplateId(String templateId) {
//		for (Iterator<Entry<String, Hero>> iterator = m_HeroMap.entrySet().iterator(); iterator.hasNext();) {
//			Entry<String, Hero> entry = iterator.next();
//			Hero tempHero = entry.getValue();
//			if (tempHero.getTemplateId().equals(templateId)) {
//				return tempHero;
//			}
//		}
//		return null;
//	}
//
//	// public void addEnemy(String heroId,Hero hero){
//	// m_HeroMap.put(heroId, hero);
//	// }
//
//	public List<String> getHeroIdList() {
//		List<String> list = new ArrayList<String>();
//		for (Hero data : m_HeroMap.values()) {
//			list.add(data.getHeroData().getId());
//		}
//		return list;
//	}
//
//	// public Hero getHeroByHeroId(String heroId) {
//	//
//	// for (Hero data : m_HeroMap.values()) {
//	// if (heroId.equals(data.getHeroData().getTemplateId())) {
//	// return data;
//	// }
//	// }
//	// return null;
//	// }
//
//	public void AddAllHeroExp(long exp) {
//		for (Hero data : m_HeroMap.values()) {
//			data.addHeroExp(exp);
//		}
//	}
//
//	public Hero getHeroByModerId(int moderId) {
//		for (Hero data : m_HeroMap.values()) {
//			if (moderId == data.getHeroData().getModeId()) {
//				return data;
//			}
//		}
//		return null;
//	}
//
//	public Hero getHeroById(String uuid) {
//		for (Hero data : m_HeroMap.values()) {
//			if (uuid.equals(data.getHeroData().getId())) {
//				return data;
//			}
//		}
//		return null;
//	}
//
//	/*
//	 * 增加佣兵，1-增加未拥有佣兵
//	 */
//	public Hero addMainRoleHero(Player playerP, RoleCfg playerCfg) {
//		Hero hero = new Hero(playerP, eRoleType.Player, playerCfg, playerP.getUserId());
//		m_HeroMap.put(hero.getUUId(), hero);
//		// 这里会初始化两次Hero，因为前面已经初始化一次了，需要拆开逻辑来解决
//		// Hero hero = m_HeroMap.get(playerP.getUserId());
//		// hero.getSkillMgr().initSkill(playerCfg);
//		// userHerosDataHolder.get().addHeroId(hero.getUUId());
//		// userHerosDataHolder.update(player);
//		return hero;
//	}
//
//	public Hero addHeroWhenCreatUser(String templateId) {
//		boolean isTemplateId = templateId.indexOf("_") != -1;// 是否是模版Id
//		String modelId = "";
//		if (isTemplateId) {
//			String[] strList = templateId.split("_");
//			modelId = strList[0];
//		} else {
//			modelId = templateId;
//		}
//
//		RoleCfgDAO instance = RoleCfgDAO.getInstance();
//		RoleCfg heroCfg = isTemplateId ? instance.getConfig(templateId) : instance.getCfgByModeID(modelId);
//		if (heroCfg == null) {
//			System.err.println("出现了没有的英雄模版Id：" + modelId + ",完整模版是：" + templateId);
//			return null;
//		}
//
//		Hero heroByModerId = this.getHeroByModerId(Integer.parseInt(modelId));
//		if (heroByModerId != null) {
//			player.getItemBagMgr().addItem(heroCfg.getSoulStoneId(), heroCfg.getTransform());
//			updateHeroIdToClient(modelId);
//			return null;
//		}
//
//		String roleUUId = UUID.randomUUID().toString();
//		Hero hero = new Hero(player, eRoleType.Hero, heroCfg, roleUUId);
//		m_HeroMap.put(hero.getUUId(), hero);
//
//		userHerosDataHolder.get().addHeroId(hero.getUUId());
//		userHerosDataHolder.update(player);
//		hero.syn(-1);
//		player.getTempAttribute().setHeroFightingChanged();
//		// 通知羁绊
//		FettersBM.whenHeroChange(player, hero.getModelId());
//		return hero;
//	}
//
//	/*
//	 * 增加佣兵，1-增加未拥有佣兵
//	 */
//	public Hero addHero(String templateId) {
//		Hero hero = addHeroWhenCreatUser(templateId);
//		// 任务
//		if (hero != null) {
//			TaskItemMgr taskMgr = player.getTaskMgr();
//			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Count);
//			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Star);
//			taskMgr.AddTaskTimes(eTaskFinishDef.Hero_Quality);
//			hero.regAttrChangeCallBack();
//			player.getFresherActivityMgr().doCheck(eActivityType.A_HeroNum);
//			player.getFresherActivityMgr().doCheck(eActivityType.A_HeroStar);
//			//通知神器羁绊系统
//			player.getMe_FetterMgr().notifyHeroChange(player, hero);
//			
//		}
//		return hero;
//	}
//
//	/** 获取前四个最大战力 */
//	public int getFightingTeam() {
//		List<Hero> list = getMaxFightingHeros();
//		int result = 0;
//		for (int i = 0; i < list.size(); i++) {
//			result += list.get(i).getFighting();
//		}
//		return result;
//	}
//
//	/** 获取所有用兵战力 */
//	public int getFightingAll() {
//		Enumeration<Hero> heroMap = getHerosEnumeration();
//		int result = 0;
//		while (heroMap.hasMoreElements()) { // 佣兵信息的遍历
//			Hero hero = (Hero) heroMap.nextElement();
//			result += hero.getFighting();
//		}
//		return result;
//	}
//
//	public int getStarAll() {
//		Enumeration<Hero> heroMap = getHerosEnumeration();
//		int result = 0;
//		while (heroMap.hasMoreElements()) {
//			Hero hero = (Hero) heroMap.nextElement();
//			result += hero.getHeroData().getStarLevel();
//		}
//		return result;
//	}
//
//	public int isHasStar(int star) {
//		Enumeration<Hero> heroMap = getHerosEnumeration();
//		int count = 0;
//		while (heroMap.hasMoreElements()) {
//			Hero hero = (Hero) heroMap.nextElement();
//			if (hero.getHeroData().getStarLevel() >= star) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	public int checkQuality(int quality) {
//		Enumeration<Hero> heroMap = getHerosEnumeration();
//		int count = 0;
//		while (heroMap.hasMoreElements()) {
//			Hero hero = (Hero) heroMap.nextElement();
//			RoleQualityCfg qualcfg = RoleQualityCfgDAO.getInstance().getConfig(hero.getHeroData().getQualityId());
//			if (qualcfg != null && qualcfg.getQuality() >= quality) {
//				count++;
//			}
//		}
//		return count;
//	}
//
//	/**
//	 * 获得佣兵列表最大战斗力前4个佣兵+主角
//	 * 
//	 * @return 佣兵ID列表
//	 */
//	public List<Hero> getMaxFightingHeros() {
//		ArrayList<Hero> list = new ArrayList<Hero>();
//		String userId = player.getUserId();
//		for (Hero data : m_HeroMap.values()) {
//			if (!data.getUUId().equals(userId)) {
//				list.add(data);
//			}
//		}
//		int size = list.size();
//		// 在原有逻辑上加一个判断，大于4个才进行排序
//		if (size > 4) {
//			Collections.sort(list, comparator);
//			size = 4;
//		}
//		ArrayList<Hero> result = new ArrayList<Hero>();
//		// 先加入主角
//		result.add(player.getMainRoleHero());
//		for (int i = 0; i < size; i++) {
//			result.add(list.get(i));
//		}
//		return result;
//	}
//
//	/** 自定义战力比较器，所有HeroMgr公用一个对象即可 */
//	private static Comparator<Hero> comparator = HeroFightPowerComparator.getInstance();
//
//	/**
//	 * 获取所有的佣兵数据
//	 * 
//	 * @param comparator 排序的接口,如果不需要就直接填个Null
//	 * @return
//	 */
//	public List<Hero> getAllHeros(Comparator<Hero> comparator) {
//		List<Hero> list = new ArrayList<Hero>(m_HeroMap.values());
//		if (comparator != null) {
//			Collections.sort(list, comparator);
//		}
//		return list;
//	}
//
//	/**
//	 * 获取出主角外其他所有的佣兵
//	 * 
//	 * @param comparator 排序的接口,如果不需要就直接填个Null
//	 * @return
//	 */
//	public List<Hero> getAllHerosExceptMainRole(Comparator<Hero> comparator) {
//		ArrayList<Hero> list = new ArrayList<Hero>();
//		String userId = player.getUserId();
//		for (Hero data : m_HeroMap.values()) {
//			if (!data.getUUId().equals(userId)) {
//				list.add(data);
//			}
//		}
//		if (comparator != null) {
//			Collections.sort(list, comparator);
//		}
//		return list;
//	}
//	
//	public static final IHeroAction ATTR_CHANGE_ACTION = new IHeroAction() {
//		
//		@Override
//		public void doAction(String userId, String heroId) {
//			Hero h = _initingHeros.get(heroId);
//			if(h != null) {
//				return;
//			}
//			FSHeroDAO.getInstance().getHero(userId, heroId).getAttrMgr().reCal();
//		}
//	};
//	
//	static {
//		FixNormEquipMgr.getInstance().regDataChangeCallback(ATTR_CHANGE_ACTION);
//		FixExpEquipMgr.getInstance().regDataChangeCallback(ATTR_CHANGE_ACTION);
//		InlayMgr.getInstance().regDataChangeCallback(ATTR_CHANGE_ACTION);
//		EquipMgr.getInstance().regDataChangeCallback(ATTR_CHANGE_ACTION);
//		SkillMgr.getInstance().regDataChangeCallback(ATTR_CHANGE_ACTION);
//	}
//}

public interface HeroMgr {
	/**
	 * 
	 * @param version
	 */
	public void synAllHeroToClient(Player player, int version);

	/**
	 * 
	 * @param player
	 * @param templateId
	 * @return
	 */
	public Hero addHero(Player player, String templateId);

	/**
	 * 
	 * @param p
	 * @return
	 */
	public Hero getMainRoleHero(PlayerIF player);

	/**
	 * 
	 * @param p
	 * @return
	 */
	public Hero getMainRoleHero(String userId);

	/**
	 * 
	 * @param playerP
	 * @param initHeros
	 */
	public void init(PlayerIF playerP, boolean initHeros);

	/**
	 * 
	 */
	public void regAttrChangeCallBack();

	/**
	 * 
	 * 获取玩家所有的英雄的id
	 * 
	 * @param userId
	 * @return
	 */
	public List<String> getHeroIdList(PlayerIF player);

	/**
	 * 
	 * 根据英雄的id，获取英雄的对象
	 * 
	 * @param userId
	 * @param uuid
	 * @return
	 */
	public Hero getHeroById(PlayerIF player, String uuid);

	/**
	 * 
	 * 根据模型ID，获取英雄对象
	 * 
	 * @param userId
	 * @param moderId
	 * @return
	 */
	public Hero getHeroByModerId(PlayerIF player, int moderId);

	/**
	 * 
	 * 获取前四名的英雄的战力总和
	 * 
	 * @return
	 */
	public int getFightingTeam(PlayerIF player);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public int getFightingTeam(String userId);

	/**
	 * 
	 * 获取所有英雄的总战斗力
	 * 
	 * @param player
	 * @return
	 */
	public int getFightingAll(PlayerIF player);

	/**
	 * 
	 * 获取所有英雄的总战斗力
	 * 
	 * @param userId
	 * @return
	 */
	public int getFightingAll(String userId);

	/**
	 * 
	 * 获取所有英雄的总星星数量
	 * 
	 * @param player
	 * @return
	 */
	public int getStarAll(PlayerIF player);

	/**
	 * 
	 * 获取所有英雄的总星星数量
	 * 
	 * @param userId
	 * @return
	 */
	public int getStarAll(String userId);

	/**
	 * 
	 * 检查是否有指定星级的英雄
	 * 
	 * @param userId
	 * @param star
	 * @return
	 */
	public int isHasStar(PlayerIF player, int star);

	/**
	 * 
	 * 检查是否有指定品质的英雄
	 * 
	 * @param userId
	 * @param quality
	 * @return
	 */
	public int checkQuality(PlayerIF player, int quality);

	/**
	 * 
	 * 获取战斗力最大的四个英雄
	 * 
	 * @param userId
	 * @return
	 */
	public List<Hero> getMaxFightingHeros(PlayerIF player);

	/**
	 * 获取战斗力最大的四个英雄
	 * 
	 * @param userId
	 * @return
	 */
	public List<Hero> getMaxFightingHeros(String userId);

	/**
	 * 
	 * 获取所有英雄列表
	 * 
	 * @param userId
	 * @return
	 */
	public Enumeration<? extends Hero> getHerosEnumeration(PlayerIF player);

	/**
	 * 
	 * @param player
	 * @param comparator
	 * @return
	 */
	public List<Hero> getAllHeros(PlayerIF player, Comparator<Hero> comparator);

	/**
	 * 
	 * @param player
	 * @param comparator
	 * @return
	 */
	public List<Hero> getAllHerosExceptMainRole(Player player, Comparator<Hero> comparator);

	/**
	 * 
	 * 获取heroIds里面的hero实例
	 * 
	 * @param player
	 * @param heroIds
	 * @return
	 */
	public List<Hero> getHeros(PlayerIF player, List<String> heroIds);

	/**
	 * 
	 * 根据角色id获取heroIds里面的hero实例
	 * 
	 * @param userId
	 * @param heroIds
	 * @return
	 */
	public List<Hero> getHeros(String userId, List<String> heroIds);

	/**
	 * 
	 * 获取所有英雄数量
	 * 
	 * @param userId
	 * @return
	 */
	public int getHerosSize(PlayerIF player);

	/**
	 * 
	 * @param player
	 * @param templateId
	 * @return
	 */
	public Hero addHeroWhenCreatUser(Player player, String templateId);

	/**
	 * 
	 * @param playerP
	 * @param playerCfg
	 * @return
	 */
	public Hero addMainRoleHero(Player playerP, RoleCfg playerCfg);

	/**
	 * 
	 * 为所有英雄添加经验
	 * 
	 * @param player
	 * @param exp
	 */
	public void AddAllHeroExp(Player player, long exp);

	/**
	 * 
	 * 根据模板id获取英雄
	 * 
	 * @param player
	 * @param templateId
	 * @return
	 */
	public Hero getHeroByTemplateId(Player player, String templateId);

	/**
	 * 
	 * 为英雄添加经验
	 * 
	 * @param hero
	 * @param heroExp
	 * @return
	 */
	public int addHeroExp(Player player, Hero hero, long heroExp);

	/**
	 * 
	 * @param hero
	 * @return
	 */
	public int canUpgradeStar(Hero hero);

	/**
	 * 
	 * gm編輯英雄的等級
	 * 
	 * @param hero
	 * @param pLevel
	 */
	public void gmEditHeroLevel(Hero hero, int pLevel);

	/**
	 * 
	 * gm檢查英雄是否激活技能
	 * 
	 * @param hero
	 */
	public void gmCheckActiveSkill(Hero hero);

	/**
	 * 
	 * 获取英雄的模板数据
	 * 
	 * @param hero
	 * @return
	 */
	public RoleCfg getHeroCfg(Hero hero);

	/**
	 * 
	 * 获取英雄的升级经验信息
	 * 
	 * @param hero
	 * @return
	 */
	public LevelCfg getLevelCfg(Hero hero);

	/**
	 * 
	 * <pre>
	 * 同步英雄信息到客户端
	 * </pre>
	 * 
	 * @param version
	 */
	public void synHero(Player player, Hero hero, int version);

	/**
	 * 
	 * <pre>
	 * 获取英雄的主人
	 * </pre>
	 * 
	 * @param hero
	 * @return
	 */
	public Player getOwnerOfHero(Hero hero);

	/**
	 * 获取佣兵品质
	 * 
	 * @param hero
	 * @return
	 */
	public int getHeroQuality(Hero hero);

	/**
	 * 当阵容发现改变的时候，通知进行修改队伍战力
	 * 
	 * @param userId
	 */
	public void updateFightingTeamWhenEmBattleChange(Player player);
}
