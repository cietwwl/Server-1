package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.bm.login.AccoutBM;
import com.bm.rank.arena.ArenaExtAttribute;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RoleBaseInfoMgr;
import com.playerdata.SkillMgr;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.service.arena.ArenaHandler;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.arena.ArenaRobotCfgDAO;
import com.rwbase.dao.arena.pojo.ArenaRobotCfg;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.fashion.FashState;
import com.rwbase.dao.fashion.FashionItem;
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
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameDataDao;
import com.rwproto.ItemBagProtos.EItemAttributeType;

public class RobotManager {

	private static RobotManager instance = new RobotManager();

	public static RobotManager getInstance() {
		return instance;
	}

	private static class ProductPlayerTask implements Callable<RankingPlayer> {
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
			user.setSex(sex);
			user.setAccount(AccoutBM.getInstance().getGuestAccountId());
			user.setUserId(userId);
			user.setZoneId(1);// 这个需要更改
			user.setLevel(level);
			UserDataDao.getInstance().saveOrUpdate(user);
			UserGameData userOther = new UserGameData();
			userOther.setUserId(userId);
			userOther.setIphone(false);
			UserGameDataDao.getInstance().update(userOther);
			GameLog.info("robot", "system", "创建机器人：" + userId + ",level = " + level, null);
			// 初始化主角
			// 初始主角英雄
			int star = getRandom(cfg.getStar());
			int quality = getRandom(cfg.getQuality());
			Player player = new Player(userId, false);
			RoleCfg playerCfg = RoleCfgDAO.getInstance().GetConfigBySexCareer(sex, career, star);
			Hero mainRoleHero = player.getHeroMgr().addMainRoleHero(player, playerCfg);
			mainRoleHero.SetHeroLevel(level);
			// 品质
			RoleBaseInfoMgr roleBaseInfoMgr = mainRoleHero.getRoleBaseInfoMgr();
			roleBaseInfoMgr.setQualityId(getQualityId(mainRoleHero, quality));
			roleBaseInfoMgr.setLevel(level);
			String headImage = HeadCfgDAO.getInstance().getCareerHead(career, star, sex);
			player.getUserDataMgr().setHeadId(headImage);
			// 更改装备
			changeEquips(userId, mainRoleHero, cfg.getEquipments(), quality, cfg.getEnchant());
			// 更改宝石
			changeGem(mainRoleHero, cfg.getGemType(), cfg.getGemCount(), cfg.getGemLevel());
			// 更改技能
			changeSkill(mainRoleHero, cfg.getFirstSkillLevel(), cfg.getSecondSkillLevel(), cfg.getThirdSkillLevel(), cfg.getFourthSkillLevel(), cfg.getFifthSkillLevel());
			String fashonId = getRandom(cfg.getFashions());
			if (!fashonId.equals("0")) {
				FashionItem f = player.getFashionMgr().buyFash(fashonId);
				player.getFashionMgr().changeFashState(f.getId(), FashState.ON);
			}
			int maigcId = getRandom(cfg.getMagicId());
			int magicLevel = getRandom(cfg.getMagicLevel());
			ItemBagMgr itemBagMgr = player.getItemBagMgr();
			itemBagMgr.addItem(maigcId, 1);
			ItemData magic = itemBagMgr.getItemListByCfgId(maigcId).get(0);
			magic.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(magicLevel));
			player.getMagicMgr().wearMagic(magic.getId());
			HeroMgr heroMgr = player.getHeroMgr();
			String heroGroupId = getRandom(cfg.getHeroGroupId());
			List<RobotHeroCfg> heroCfgList = RobotHeroCfgDAO.getInstance().getRobotHeroCfg(heroGroupId);
			if (heroCfgList == null) {
				GameLog.error("生成机器人找不到佣兵组合：" + heroGroupId);
				return null;
			}
			RobotHeroCfg heroCfg = heroCfgList.get(getRandom().nextInt(heroCfgList.size()));
			int[] heroLevel = cfg.getHeroLevel();
			ArrayList<Hero> heroList = new ArrayList<Hero>(4);
			addHero(heroCfg.getFirstHeroId(), heroList, heroMgr, heroLevel, level);
			addHero(heroCfg.getSecondHeroId(), heroList, heroMgr, heroLevel, level);
			addHero(heroCfg.getThirdHeroId(), heroList, heroMgr, heroLevel, level);
			addHero(heroCfg.getFourthHeroId(), heroList, heroMgr, heroLevel, level);
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
				changeGem(hero, heroGemType, heroGemCount, heroGemLevel);
				changeSkill(hero, heroSkill1, heroSkill2, heroSkill3, heroSkill4, heroSkill5);
				arenaList.add(hero.getUUId());
			}
			player.initMgr();
			player.getUserDataMgr().setUserName(userName);
			player.getAttrMgr().reCal();
			for(Hero hero:heroList){
				hero.getAttrMgr().reCal();
			}
			player.save(true);
			PlayerMgr.getInstance().putToMap(player);
			printHeroSkill(mainRoleHero);
			for (Hero hero : heroList) {
				printHeroSkill(hero);
			}
			GameLog.info("robot", "system", "成功生成机器人：carerr = " + career + ",level = " + level + ",消耗时间:" + (System.currentTimeMillis() - start) + "ms", null);
			return new RankingPlayer(player, arenaList, expectRanking);
		}
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
		int count = 0;
		for (ECareer c : carerrs) {
			if (c != ECareer.None) {
				int carerr = c.getValue();
				carerrList.add(carerr);
				count += ArenaBM.getInstance().getRanking(carerr).getRankingSize();
			}
		}
		ArenaRobotCfg robotCfg = (ArenaRobotCfg) ArenaRobotCfgDAO.getInstance().getCfgById("7");
		String[] arrName = robotCfg.getData().split(",");
		int len = arrName.length;
		if (len < count) {
			GameLog.error("当前机器名字人数量少于竞技场名次");
			return;
		}
		ArrayList<String> nameList = new ArrayList<String>(len - count);
		for (; count < len; count++) {
			nameList.add(arrName[count]);
		}
		TreeMap<Integer, RobotEntryCfg> robots = RobotCfgDAO.getInstance().getAllArenaRobets();
		ArenaBM arenaBM = ArenaBM.getInstance();
		int size = carerrList.size();
		// 只用于存储。。哈哈
		HashMap<Future, ProductionCompletionTask> futures = new HashMap<Future, ProductionCompletionTask>();
		ExecutorService futureExecutor = Executors.newFixedThreadPool(size);
		for (int career : carerrList) {
			ListRanking<String, ArenaExtAttribute> listRanking = arenaBM.getRanking(career);
			if (listRanking == null) {
				GameLog.error("不存在竞技场排行榜：" + career);
				continue;
			}
			int rankingSize = listRanking.getRankingSize();
			int last = robots.lastKey();
			if (rankingSize >= last) {
				continue;
			}
			Map<Integer, RobotEntryCfg> map = robots.subMap(rankingSize, false, last, true);
			ArrayList<ProductPlayerTask> productPlayerList = new ArrayList<ProductPlayerTask>(map.size());
			for (Map.Entry<Integer, RobotEntryCfg> entry : map.entrySet()) {
				// 随机名字
				String userName = nameList.remove(nameList.size() - 1);
				if (userName != null && !userName.isEmpty()) {
					productPlayerList.add(new ProductPlayerTask(career, entry.getKey(), entry.getValue(), userName));
				}
			}
			if (!productPlayerList.isEmpty()) {
				ProductionCompletionTask productionCompletionTask = new ProductionCompletionTask(career, 5, productPlayerList);
				Future f = futureExecutor.submit(productionCompletionTask);
				futures.put(f, productionCompletionTask);
			}
		}
		for (Map.Entry<Future, ProductionCompletionTask> entry : futures.entrySet()) {
			try {
				entry.getKey().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				GameLog.error(e);
			}
			entry.getValue().close();
		}
		futureExecutor.shutdown();
	}

	private static void addHero(String templateId, ArrayList<Hero> heroList, HeroMgr mgr, int[] heroLevel, int roleLevel) {
		if (templateId == null || templateId.isEmpty()) {
			return;
		}
		Hero hero = mgr.addHeroWhenCreatUser(templateId);
		int lv = getRandom(heroLevel);
		if (lv > roleLevel) {
			lv = roleLevel;
		}
		hero.SetHeroLevel(lv);
		if (hero == null) {
			GameLog.error("机器人添加佣兵失败：" + templateId);
			return;
		}
		heroList.add(hero);
	}

	private static void changeHero(Hero hero, RobotEntryCfg cfg) {
		int startLevel = getRandom(cfg.getHeroStar());
		int quality = getRandom(cfg.getHeroQuality());
		if(startLevel == 0){
			System.out.println();
		}
		hero.setStarLevel(startLevel);
		hero.setQualityId(getQualityId(hero, quality));
	}

	private static String getQualityId(Hero hero, int quality) {
		return hero.getModelId() + "_" + (quality + 1);
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
			// 设置附灵等级
			itemData.setExtendAttr(EItemAttributeType.Equip_AttachLevel_VALUE, enhanceLevel);
		}
		hero.getEquipMgr().addRobotEquip(equipItemDataList);
	}

	/** 更改英雄宝石 **/
	private static void changeGem(Hero hero, int[] gemTypeArray, int[] gemCountArray, int[] gemLevelArray) {
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
		hero.getInlayMgr().addRobotGem(gemList_);
	}

	/** 更改技能 **/
	private static void changeSkill(Hero hero, int[] skill1, int[] skill2, int[] skill3, int[] skill4, int[] skill5) {
		ArrayList<Integer> skillLevel = new ArrayList<Integer>(5);
		skillLevel.add(getRandom(skill1));
		skillLevel.add(getRandom(skill2));
		skillLevel.add(getRandom(skill3));
		skillLevel.add(getRandom(skill4));
		skillLevel.add(getRandom(skill5));
		SkillMgr skillMgr = hero.getSkillMgr();
		List<Skill> skillList = skillMgr.getSkillList();
		int skillSize = skillList.size();
		for (int i = 0; i < skillSize; i++) {
			Skill skill = skillList.get(i);
			RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(hero.getQualityId());
			if (cfg == null) {
				GameLog.error("找不到英雄品质：" + hero.getQualityId());
				continue;
			}
			if (!hero.getSkillMgr().isSkillCanActive(skill, hero.getLevel(), cfg.getQuality())) {
				continue;
			}
			String skillId = skill.getSkillId();
			int lv = skill.getLevel();
			int expectLv = skillLevel.get(skill.getOrder());
			if (expectLv <= lv) {
				continue;
			}
			// 升级技能
			skillMgr.updateSkill(skillId, expectLv - lv);
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
		// System.out.println(str + "," + array);
		return str;
	}

	static class ProductionCompletionTask implements Runnable {

		private final ExecutorCompletionService<RankingPlayer> executor;
		private final List<ProductPlayerTask> list;
		private final AtomicBoolean startSwitch = new AtomicBoolean(false);
		private final int carerr;
		private final ExecutorService es;

		public ProductionCompletionTask(int carerr, int threadCount, List<ProductPlayerTask> list) {
			ExecutorService es = Executors.newFixedThreadPool(threadCount);
			this.executor = new ExecutorCompletionService<RankingPlayer>(es);
			this.list = list;
			this.carerr = carerr;
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
					GameLog.error("创建机器人异常", e);
				}
			}
			GameLog.error("职业[" + carerr + "]创建机器人完成");
			ArenaBM arenaBM = ArenaBM.getInstance();
			ListRanking<String, ArenaExtAttribute> listRanking = arenaBM.getRanking(carerr);
			ArenaHandler handler = ArenaHandler.getInstance();
			for (RankingPlayer task : set) {
				Player player = task.getPlayer();
				TableArenaData arenaData = arenaBM.addArenaData(task.getPlayer());
				handler.setArenaHero(player, arenaData, task.getHeroList());
				GameLog.error("robot", "system", "机器人加入排行榜：carerr = " + player.getCareer() + ",level = " + player.getLevel() + ",ranking = " + listRanking.getRankingEntry(player.getUserId()).getRanking(), null);
			}
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
