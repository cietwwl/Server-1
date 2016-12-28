package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bm.rank.arena.ArenaExtAttribute;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.common.EquipHelper;
import com.common.Utils;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerCreateParam;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.hero.core.FSHero;
import com.playerdata.randomname.RandomNameMgr;
import com.rw.dataaccess.GameOperationFactory;
import com.rw.dataaccess.PlayerParam;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.service.PeakArena.PeakArenaBM;
import com.rw.service.PeakArena.datamodel.PeakArenaExtAttribute;
import com.rw.service.arena.ArenaHandler;
import com.rw.service.skill.SkillConstant;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.arena.ArenaRobotCfgDAO;
import com.rwbase.dao.arena.pojo.ArenaRobotCfg;
import com.rwbase.dao.arena.pojo.ArenaRobotData;
import com.rwbase.dao.arena.pojo.HeroFettersRobotInfo;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.setting.HeadCfgDAO;
import com.rwbase.dao.skill.pojo.SkillItem;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwproto.ArenaServiceProtos.ArenaEmbattleType;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.ItemBagProtos.EItemAttributeType;

public class RobotManager {

	private static RobotManager instance = new RobotManager();

	public static RobotManager getInstance() {
		return instance;
	}

	private static void printHeroSkill(Hero hero) {
		// StringBuilder sb = new StringBuilder();
		// sb.append(hero.getUUId());
		// List<Skill> list = hero.getSkillMgr().getSkillList();
		// for (Skill skill : list) {
		// sb.append("id = "+skill.getId()).append("\r\n");
		// sb.append("skillId = "+skill.getSkillId()).append("\r\n");
		// sb.append("level = "+skill.getLevel()).append("\r\n");
		// sb.append("order = "+skill.getOrder()).append("\r\n");
		// sb.append("\r\n");
		// }
		// System.out.println(sb.toString());
	}

	public void createRobots() {
		ECareer[] carerrs = ECareer.values();
		ArrayList<Integer> carerrList = new ArrayList<Integer>();
		for (ECareer c : carerrs) {
			if (c != ECareer.None) {
				int carerr = c.getValue();
				carerrList.add(carerr);
			}
		}
		int count = ArenaBM.getInstance().getRanking().getRankingSize();
		ArenaRobotCfg robotCfg = ArenaRobotCfgDAO.getInstance().getCfgById("7");
		String[] arrName = robotCfg.getData().split(",");
		int len = arrName.length;
		if (len < count) {
			GameLog.error("RobotManager", "createRobots", "当前机器名字人数量少于竞技场名次");
			return;
		}
		ArrayList<String> nameList = new ArrayList<String>(len - count);
		for (; count < len; count++) {
			nameList.add(arrName[count]);
		}
		TreeMap<Integer, RobotEntryCfg> robots = RobotCfgDAO.getInstance().getAllArenaRobets();
		ArenaBM arenaBM = ArenaBM.getInstance();
		int carerrSize = carerrList.size();
		// 只用于存储。。哈哈
		HashMap<Future<?>, ProductionCompletionTask> futures = new HashMap<Future<?>, ProductionCompletionTask>();
		ListRanking<String, ArenaExtAttribute> listRanking = arenaBM.getRanking();
		if (listRanking == null) {
			GameLog.error("RobotManager", "createRobots", "不存在竞技场排行榜");
			return;
		}
		int rankingSize = listRanking.getRankingSize();
		int last = robots.lastKey();
		if (rankingSize >= last) {
			return;
		}
		Map<Integer, RobotEntryCfg> map = robots.subMap(rankingSize, false, last, true);
		ArrayList<ProductPlayerTask> productPlayerList = new ArrayList<ProductPlayerTask>(map.size());
		Random random = new Random();
		for (Map.Entry<Integer, RobotEntryCfg> entry : map.entrySet()) {
			// 随机名字
			String userName = nameList.remove(nameList.size() - 1);
			if (userName != null && !userName.isEmpty()) {
				productPlayerList.add(new ProductPlayerTask(carerrList.get(random.nextInt(carerrSize)), entry.getKey(), entry.getValue(), userName));
				RandomNameMgr.getInstance().destroyName(userName);
			}
		}
		if (!productPlayerList.isEmpty()) {
			ProductionCompletionTask productionCompletionTask = new ProductionCompletionTask(10, productPlayerList);
			productionCompletionTask.run();
			productionCompletionTask.close();
		}
	}

	public void createPeakArenaRobot() {
		PeakArenaBM peakHandler = PeakArenaBM.getInstance();
		ListRanking<String, PeakArenaExtAttribute> peakRanking = peakHandler.getRanks();
		int count = peakRanking.getRankingSize();
		// TODO 临时定的数量，少于100进行创建
		if (count > 100) {
			return;
		}

		ArenaRobotCfg robotCfg = ArenaRobotCfgDAO.getInstance().getCfgById("7");
		String[] arrName = robotCfg.getData().split(",");
		int len = arrName.length;
		if (len < count) {
			GameLog.error("RobotManager", "createPeakArenaRobot", "当前机器名字人数量少于竞技场名次");
			return;
		}
		ArrayList<String> nameList = new ArrayList<String>();
		for (; count < len; count++) {
			nameList.add(arrName[count]);
		}
		ECareer[] carerrs = ECareer.values();
		int totalCarerrs = carerrs.length;
		TreeMap<Integer, RobotEntryCfg> map = RobotCfgDAO.getInstance().getAllPeakArenaRobets();
		ArrayList<ProductPlayerTask> productPlayerList = new ArrayList<ProductPlayerTask>(map.size());
		ArrayList<Future<RankingPlayer>> futures = new ArrayList<Future<RankingPlayer>>(map.size());
		Random random = new Random();
		int index = 0;
		for (Map.Entry<Integer, RobotEntryCfg> entry : map.entrySet()) {
			// 随机名字
			String userName = nameList.remove(index++);
			if (userName != null && !userName.isEmpty()) {
				productPlayerList.add(new ProductPlayerTask(carerrs[random.nextInt(totalCarerrs - 1) + 1].getValue(), entry.getKey(), entry.getValue(), userName));
			}
		}
		ExecutorService e = Executors.newFixedThreadPool(10);
		try {
			for (ProductPlayerTask task : productPlayerList) {
				futures.add(e.submit(task));
			}
			for (Future<RankingPlayer> f : futures) {
				try {
					RankingPlayer r = f.get();
					if (r == null) {
						continue;
					}
					Player p = r.getPlayer();
					if (p != null) {
						peakHandler.getOrAddPeakArenaData(p);
					}
				} catch (Throwable t) {
					GameLog.error("RobotManager", "createPeakArenaRobot", "创建巅峰机器人异常", t);
				}
			}
		} finally {
			e.shutdown();
		}
	}

	private static void addHero(Player player, String templateId, ArrayList<Hero> heroList, HeroMgr mgr, int[] heroLevel, int roleLevel) {
		if (templateId == null || templateId.isEmpty()) {
			return;
		}
		// Hero hero = mgr.addHeroWhenCreatUser(templateId);
		Hero hero = mgr.addHeroWhenCreatUser(player, templateId);
		if (hero == null) {
			GameLog.error("RobotManager", "#addHero", "机器人添加佣兵失败：" + templateId);
			return;
		}
		int lv = getRandom(heroLevel);
		if (lv > roleLevel) {
			lv = roleLevel;
		}
		// hero.SetHeroLevel(lv);
		// FSHeroBaseInfoMgr.getInstance().setLevel(hero, lv);
		hero.setLevel(lv);
		heroList.add(hero);
	}

	private static void changeHero(Hero hero, RobotEntryCfg cfg) {
		int startLevel = getRandom(cfg.getHeroStar());
		int quality = getRandom(cfg.getHeroQuality());
		if (startLevel == 0) {
			System.out.println();
		}
		// hero.setStarLevel(startLevel);
		// hero.setQualityId(getQualityId(hero, quality));
		// FSHeroBaseInfoMgr.getInstance().setStarLevel(hero, startLevel);
		hero.setStarLevel(startLevel);
		// FSHeroBaseInfoMgr.getInstance().setQualityId(hero, getQualityId(hero, quality));
		hero.setQualityId(getQualityId(hero, quality));
	}

	private static String getQualityId(Hero hero, int quality) {
		// return hero.getModeId() + "_" + (quality + 1);
		return Utils.computeQualityId(hero.getModeId(), (quality + 1));
	}

	private static void changeEquips(String userId, Hero hero, int[] equipments, int quality, int[] enchant) {
		TreeMap<Integer, List<HeroEquipCfg>> map = new TreeMap<Integer, List<HeroEquipCfg>>();
		int equipSize = getRandom(equipments);
		RoleQualityCfg qualityCfg = RoleQualityCfgDAO.getInstance().getConfig(getQualityId(hero, quality));
		addIntoEquip(qualityCfg.getEquip1(), map);
		addIntoEquip(qualityCfg.getEquip2(), map);
		addIntoEquip(qualityCfg.getEquip3(), map);
		addIntoEquip(qualityCfg.getEquip4(), map);
		addIntoEquip(qualityCfg.getEquip5(), map);
		addIntoEquip(qualityCfg.getEquip6(), map);
		ArrayList<HeroEquipCfg> list = new ArrayList<HeroEquipCfg>(6);
		for (List<HeroEquipCfg> container : map.descendingMap().values()) {
			int size = container.size();
			if (size < equipSize) {
				list.addAll(container);
				equipSize -= size;
			} else if (size > equipSize) {
				list.addAll(container.subList(0, equipSize));
				break;
			} else {
				list.addAll(container);
				break;
			}
		}
		ArrayList<ItemData> equipItemDataList = new ArrayList<ItemData>();
		String enhanceLevel = String.valueOf(getRandom(enchant));
		for (HeroEquipCfg heroEquip : list) {
			ItemData itemData = new ItemData();
			itemData.init(heroEquip.getId(), 1);
			itemData.setUserId(userId);
			equipItemDataList.add(itemData);
			// TODO HC @Modify 2016-04-16 装备附灵等级潜规则
			int attachLevelInit = EquipHelper.getEquipAttachInitId(heroEquip.getQuality());
			// 设置附灵等级
			itemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, attachLevelInit + enhanceLevel);
		}
		hero.getEquipMgr().addRobotEquip(hero.getUUId(), equipItemDataList);
	}

	/** 更改英雄宝石 **/
	private static void changeGem(Player player, Hero hero, int[] gemTypeArray, int[] gemCountArray, int[] gemLevelArray) {
		// 先低效随机筛选
		int gemCount = getRandom(gemCountArray);
		ArrayList<Integer> gemList = new ArrayList<Integer>();
		for (int a : gemTypeArray) {
			gemList.add(a);
		}
		Collections.shuffle(gemList);
		ArrayList<Integer> gemList_ = new ArrayList<Integer>();
		GemCfgDAO gemCfgDAO = GemCfgDAO.getInstance();
		int gemLevel = getRandom(gemLevelArray);
		for (int i = 0; i < gemCount; i++) {
			Integer gemId = gemList.remove(getRandom().nextInt(gemList.size()));
			gemList_.add(gemId);
			int nextGemId = gemId;
			for (int j = gemLevel; --j >= 0;) {
				GemCfg gemCfg = (GemCfg) gemCfgDAO.getCfgById(String.valueOf(nextGemId));
				if (gemCfg == null) {
					continue;
				}
				int n = gemCfg.getComposeItemID();
				if (n > 0) {
					nextGemId = n;
				}
			}
			gemList_.set(i, nextGemId);
		}

		// 新增宝石
		hero.getInlayMgr().addRobotGem(player, hero.getUUId(), gemList_);
	}

	/** 更改技能 **/
	private static void changeSkill(Player player, Hero hero, int[] skill1, int[] skill2, int[] skill3, int[] skill4, int[] skill5) {
		ArrayList<Integer> skillLevel = new ArrayList<Integer>(5);
		skillLevel.add(getRandom(skill1));
		skillLevel.add(getRandom(skill2));
		skillLevel.add(getRandom(skill3));
		skillLevel.add(getRandom(skill4));
		skillLevel.add(getRandom(skill5));
		SkillMgr skillMgr = hero.getSkillMgr();
		List<SkillItem> skillList = skillMgr.getSkillList(hero.getUUId());
		int skillSize = skillList.size();
		for (int i = 0; i < skillSize; i++) {
			SkillItem skill = skillList.get(i);
			RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(hero.getQualityId());
			if (cfg == null) {
				GameLog.error("RobotManager", "changeSkill", "找不到英雄品质：" + hero.getQualityId());
				continue;
			}
			if (!hero.getSkillMgr().isSkillCanActive(skill, hero.getLevel(), cfg.getQuality())) {
				continue;
			}
			int lv = skill.getLevel();
			int order = skill.getOrder();

			// 如果是普攻技能就直接不去检查等级等信息
			if (order == SkillConstant.NORMAL_SKILL_ORDER) {
				continue;
			}

			int expectLv = skillLevel.get(order);
			if (expectLv <= lv) {
				continue;
			}
			// 升级技能
			skillMgr.updateSkill(player, hero.getUUId(), skill, expectLv - lv);
		}
	}

	private static void addIntoEquip(int equipId, TreeMap<Integer, List<HeroEquipCfg>> map) {
		HeroEquipCfg cfg = HeroEquipCfgDAO.getInstance().getConfig(equipId);
		if (cfg == null) {
			throw new ExceptionInInitializerError("找不到装备配置：" + equipId);
		}
		int quality = cfg.getQuality();
		List<HeroEquipCfg> container = map.get(quality);
		if (container == null) {
			container = new ArrayList<HeroEquipCfg>(6);
			map.put(quality, container);
		}
		container.add(cfg);
	}

	private static Random getRandom() {
		return new Random();
	}

	private static int getRandom(int[] array) {
		if (array.length == 1) {
			return array[0];
		}
		try {
			return array[getRandom().nextInt(array.length)];
		} catch (Exception e) {
			e.printStackTrace();
			return array[0];
		}
	}

	private static String getRandom(List<String> array) {
		if (array.size() == 1) {
			return array.get(0);
		}
		String str = array.get(getRandom().nextInt(array.size()));
		return str;
	}

	static class ProductionCompletionTask implements Runnable {

		private final ExecutorCompletionService<RankingPlayer> executor;
		private final List<ProductPlayerTask> list;
		private final AtomicBoolean startSwitch = new AtomicBoolean(false);
		private final ExecutorService es;

		public ProductionCompletionTask(int threadCount, List<ProductPlayerTask> list) {
			ExecutorService es = Executors.newFixedThreadPool(threadCount, new SimpleThreadFactory("职业竞技场"));
			this.executor = new ExecutorCompletionService<RankingPlayer>(es);
			this.list = list;
			this.es = es;
		}

		public void close() {
			es.shutdown();
		}

		@Override
		public void run() {
			if (!startSwitch.compareAndSet(false, true)) {
				throw new RuntimeException("the task has start");
			}
			int size = list.size();
			for (int i = 0; i < size; i++) {
				ProductPlayerTask task = list.get(i);
				executor.submit(task);
			}
			TreeSet<RankingPlayer> set = new TreeSet<RankingPlayer>();
			for (int i = 0; i < size; i++) {
				try {
					RankingPlayer rankingPlayer = executor.take().get();
					if (rankingPlayer != null) {
						set.add(rankingPlayer);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			GameLog.info("RobotManager", "run", "创建机器人完成");
			ArenaBM arenaBM = ArenaBM.getInstance();
			ListRanking<String, ArenaExtAttribute> listRanking = arenaBM.getRanking();
			ArenaHandler handler = ArenaHandler.getInstance();
			for (RankingPlayer task : set) {
				Player player = task.getPlayer();
				TableArenaData arenaData = arenaBM.addArenaData(task.getPlayer());
				handler.setArenaHero(player, arenaData,
						EmbattlePositonHelper.parseId2MsgList(player.getUserId(), eBattlePositionType.ArenaPos_VALUE, String.valueOf(ArenaEmbattleType.ARENA_DEFEND_VALUE), task.getHeroList()));
				GameLog.info("robot", "system", "机器人加入排行榜：carerr = " + player.getCareer() + ",level = " + player.getLevel() + ",ranking = "
						+ listRanking.getRankingEntry(player.getUserId()).getRanking(), null);
			}
		}
	}

	/**
	 * 获取神器信息
	 * 
	 * @param cfg
	 * @return
	 */
	public static int[] getRobotFixInfo(RobotEntryCfg cfg) {
		int[] fixInfo = new int[3];
		// 神器
		int[] fixEquipLevel = cfg.getFixEquipLevel();
		fixInfo[0] = getRandom(fixEquipLevel);
		int[] fixEquipQuality = cfg.getFixEquipQuality();
		fixInfo[1] = getRandom(fixEquipQuality);
		int[] fixEquipStar = cfg.getFixEquipStar();
		fixInfo[2] = getRandom(fixEquipStar);

		return fixInfo;
	}

	/**
	 * 获取时装数据
	 * 
	 * @param cfg
	 * @return
	 */
	public static int[] getRobotFashionInfo(RobotEntryCfg cfg) {
		List<String> fashions = cfg.getFashions();
		int[] fashionIdArr = new int[3];
		if (fashions.size() >= 3) {
			for (int i = 0; i < 3; i++) {
				fashionIdArr[i] = Integer.parseInt(fashions.get(i));
			}
		}
		return fashionIdArr;
	}

	/**
	 * 获取道术的信息
	 * 
	 * @param cfg
	 * @return
	 */
	public static int[] getRobotTaoistInfo(RobotEntryCfg cfg) {
		List<int[]> taoistLevel = cfg.getTaoistLevel();
		int[] taoistLevelArr = new int[3];
		if (taoistLevel.size() >= 3) {
			for (int i = 0; i < 3; i++) {
				taoistLevelArr[i] = getRandom(taoistLevel.get(i));
			}
		}
		return taoistLevelArr;
	}

	/**
	 * 获取英雄羁绊信息
	 * 
	 * @param cfg
	 * @return
	 */
	public static HeroFettersRobotInfo[] getRobotFettersInfo(RobotEntryCfg cfg) {
		Map<String, int[]> heroFetters = cfg.getHeroFetters();
		HeroFettersRobotInfo[] fetters = new HeroFettersRobotInfo[0];
		if (!heroFetters.isEmpty()) {
			fetters = new HeroFettersRobotInfo[heroFetters.size()];
			int i = 0;
			for (Entry<String, int[]> e : heroFetters.entrySet()) {
				HeroFettersRobotInfo info = new HeroFettersRobotInfo();
				info.setId(e.getKey());
				info.setLevel(getRandom(e.getValue()));

				fetters[i++] = info;
			}
		}

		return fetters;
	}

	static class ProductPlayerTask implements Callable<RankingPlayer> {
		private final int career;
		private final int expectRanking;
		private final RobotEntryCfg cfg;
		private final String userName;

		public ProductPlayerTask(int career, int expectRanking, RobotEntryCfg cfg, String userName) {
			this.career = career;
			this.cfg = cfg;
			this.expectRanking = expectRanking;
			this.userName = userName;
		}

		@Override
		public RankingPlayer call() throws Exception {
			long start = System.currentTimeMillis();
			if (UserDataDao.getInstance().validateName(userName)) {
				GameLog.error("robot", "product player", "arena robot already exist , continue...");
				return null;
			}

			// 创建User，并初始化基本属性
			User user = new User();
			String userId = UUID.randomUUID().toString();
			int sex = getRandom().nextInt(2);
			int level = getRandom(cfg.getLevel());
			user.setUserName(userName);
			user.setSex(sex);
			user.setAccount(userId);
			user.setUserId(userId);
			user.setZoneId(1);// 这个需要更改
			user.setLevel(level);
			user.setRobot(true);
			UserDataDao.getInstance().saveOrUpdate(user);
			int star = getRandom(cfg.getStar());
			int quality = getRandom(cfg.getQuality());

			String headImage = HeadCfgDAO.getInstance().getCareerHead(career, star, sex);
			RoleCfg playerCfg = RoleCfgDAO.getInstance().GetConfigBySexCareer(sex, career, star);
			PlayerParam param = new PlayerParam(userId, userId, userName, 1, sex, System.currentTimeMillis(), playerCfg, headImage, "");

			GameOperationFactory.getCreatedOperation().execute(param);
			GameLog.info("robot", "system", "创建机器人：" + userId + ",level = " + level, null);
			// 初始化主角
			// 初始主角英雄

			Player player = new Player(userId, false, playerCfg, new PlayerCreateParam(level));
			MapItemStoreFactory.notifyPlayerCreated(userId);

			// 品质

			// mainRoleHero.setQualityId(getQualityId(mainRoleHero, quality));
			player.initMgr();
			PlayerMgr.getInstance().putToMap(player);
			player.getUserDataMgr().setHeadId(headImage);

			player.getUserDataMgr().setUserName(userName);
			// player.SetLevel(level);

			Hero mainRoleHero = player.getHeroMgr().getMainRoleHero(player);
			mainRoleHero.setLevel(level);
			mainRoleHero.setQualityId(getQualityId(mainRoleHero, quality));
			// FSHeroBaseInfoMgr.getInstance().setQualityId(mainRoleHero, getQualityId(mainRoleHero, quality));
			// 更改装备
			changeEquips(userId, mainRoleHero, cfg.getEquipments(), quality, cfg.getEnchant());
			// 更改宝石
			changeGem(player, mainRoleHero, cfg.getGemType(), cfg.getGemCount(), cfg.getGemLevel());
			// 更改技能
			changeSkill(player, mainRoleHero, cfg.getFirstSkillLevel(), cfg.getSecondSkillLevel(), cfg.getThirdSkillLevel(), cfg.getFourthSkillLevel(), cfg.getFifthSkillLevel());

			MapItemStoreFactory.getMainHeroDataCache().getMapItemStore(userId, FSHero.class).update(userId);
			// String fashonId = getRandom(cfg.getFashions());
			// if (!fashonId.equals("0")) {
			// int fashionID = Integer.parseInt(fashonId);
			// player.getFashionMgr().giveFashionItem(fashionID, -1, true,
			// false);
			// }
			int maigcId = getRandom(cfg.getMagicId());
			int magicLevel = getRandom(cfg.getMagicLevel());
			ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
			itemBagMgr.addRobotItem(player, maigcId, 1);
			ItemData magic = itemBagMgr.getItemListByCfgId(userId, maigcId).get(0);
			magic.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(magicLevel));
			player.getMagicMgr().wearMagic(magic.getId());
			HeroMgr heroMgr = player.getHeroMgr();

			String heroGroupId = getRandom(cfg.getHeroGroupId());
			List<RobotHeroCfg> heroCfgList = RobotHeroCfgDAO.getInstance().getRobotHeroCfg(heroGroupId);
			if (heroCfgList == null) {
				GameLog.error("RobotManager", "call", "生成机器人找不到佣兵组合：" + heroGroupId);
				return null;
			}
			RobotHeroCfg heroCfg = heroCfgList.get(getRandom().nextInt(heroCfgList.size()));
			int[] heroLevel = cfg.getHeroLevel();
			ArrayList<Hero> heroList = new ArrayList<Hero>(4);
			addHero(player, heroCfg.getFirstHeroId(), heroList, heroMgr, heroLevel, level);
			addHero(player, heroCfg.getSecondHeroId(), heroList, heroMgr, heroLevel, level);
			addHero(player, heroCfg.getThirdHeroId(), heroList, heroMgr, heroLevel, level);
			addHero(player, heroCfg.getFourthHeroId(), heroList, heroMgr, heroLevel, level);
			// 装备部分
			int[] heroEnchant = cfg.getHeroEnchant();
			int[] equipments = cfg.getHeroEquipments();
			int heroQuality = getRandom(cfg.getHeroQuality());
			// 宝石部分
			int[] heroGemType = cfg.getHeroGemType();
			int[] heroGemLevel = cfg.getHeroGemLevel();
			int[] heroGemCount = cfg.getHeroGemCount();
			// 技能部分
			int[] heroSkill1 = cfg.getHeroFirstSkillLevel();
			int[] heroSkill2 = cfg.getHeroSecondSkillLevel();
			int[] heroSkill3 = cfg.getHeroThirdSkillLevel();
			int[] heroSkill4 = cfg.getHeroFourthSkillLevel();
			int[] heroSkill5 = cfg.getHeroFifthSkillLevel();
			ArrayList<String> arenaList = new ArrayList<String>();
			for (Hero hero : heroList) {
				changeHero(hero, cfg);
				changeEquips(userId, hero, equipments, heroQuality, heroEnchant);
				changeGem(player, hero, heroGemType, heroGemCount, heroGemLevel);
				changeSkill(player, hero, heroSkill1, heroSkill2, heroSkill3, heroSkill4, heroSkill5);
				String id = hero.getUUId();
				arenaList.add(id);
			}

			// 增加其他的扩展内容
			ArenaRobotData robotData = new ArenaRobotData();
			robotData.setUserId(userId);
			// 神器
			robotData.setFixEquip(getRobotFixInfo(cfg));
			// 时装
			robotData.setFashionId(getRobotFashionInfo(cfg));
			// 道术
			robotData.setTaoist(getRobotTaoistInfo(cfg));
			// 羁绊
			robotData.setFetters(getRobotFettersInfo(cfg));
			// 额外属性
			robotData.setExtraAttrId(cfg.getExtraAttrId());
			// 把机器人这个配置数据放到数据库
			ArenaRobotDataMgr.getMgr().addArenaRobotData(robotData);

			player.getAttrMgr().reCal();
			for (Hero hero : heroList) {
				hero.getAttrMgr().reCal();
			}
			// player.save(true);
			printHeroSkill(mainRoleHero);
			for (Hero hero : heroList) {
				printHeroSkill(hero);
			}

			// 检查机器人数据并加入到万仙阵阵容排行榜
			List<Integer> heroModelList = new ArrayList<Integer>();
			int mainRoleModelId = mainRoleHero.getModeId();
			heroModelList.add(mainRoleModelId);

			int fighting = mainRoleHero.getFighting();
			MapItemStore<FSHero> store = MapItemStoreFactory.getHeroDataCache().getMapItemStore(userId, FSHero.class);
			for (Hero hero : heroList) {
				if (hero == null) {
					continue;
				}

				int modelId = hero.getModeId();
				if (modelId == mainRoleModelId) {
					continue;
				}

				heroModelList.add(modelId);
				fighting += hero.getFighting();
				store.update(hero.getId());
			}

			AngelArrayTeamInfoHelper.checkAndUpdateTeamInfo(player, heroModelList, fighting);
			GameLog.info("robot", "system", "成功生成机器人：carerr = " + career + ",level = " + player.getLevel() + ",消耗时间:" + (System.currentTimeMillis() - start) + "ms", null);
			return new RankingPlayer(player, arenaList, expectRanking);
		}
	}

	/** 有期望名次的玩家对象 **/
	static class RankingPlayer implements Comparable<RankingPlayer> {
		private final Player player;
		private final List<String> heroList;
		private final int expectRanking;

		public RankingPlayer(Player player, List<String> heroList, int expectRanking) {
			this.player = player;
			this.heroList = heroList;
			this.expectRanking = expectRanking;
		}

		public Player getPlayer() {
			return player;
		}

		public List<String> getHeroList() {
			return heroList;
		}

		public int getExpectRanking() {
			return expectRanking;
		}

		@Override
		public int compareTo(RankingPlayer o) {
			return expectRanking - o.expectRanking;
		}

	}

}
