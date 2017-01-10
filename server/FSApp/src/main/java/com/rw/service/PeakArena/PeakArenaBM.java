package com.rw.service.PeakArena;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bm.rank.ListRankingType;
import com.common.HPCUtil;
import com.common.RefInt;
import com.common.RefParam;
import com.common.RobotListRankingImpl;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroFightPowerComparator;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStore;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.IStreamListner;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.ranking.exception.RankingCapacityNotEougthException;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.PeakArena.datamodel.PeakArenaExtAttribute;
import com.rw.service.PeakArena.datamodel.PeakRecordInfo;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rw.service.PeakArena.datamodel.TeamData;
import com.rw.service.PeakArena.datamodel.peakArenaMatchRule;
import com.rw.service.PeakArena.datamodel.peakArenaMatchRuleHelper;
import com.rw.service.PeakArena.datamodel.peakArenaPrizeHelper;
import com.rw.service.store.StoreHandler;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.skill.pojo.TableSkill;

public class PeakArenaBM implements IStreamListner<Pair<Player, Integer>> {

	private static PeakArenaBM instance = new PeakArenaBM();
	private TablePeakArenaDataDAO tablePeakArenaDataDAO = TablePeakArenaDataDAO.getInstance();
	private static final Comparator<PeakRecordInfo> recordComparator = new PeakRecordComparator();
	private static int RESULT_COUNT = 3; // 随机后的结果人数
	private static long MILLIS_PER_HOUR = TimeUnit.HOURS.toMillis(1);
	private static final int MAX_RECORD_COUNT = 30;
	public static final int MAX_DISPLAY_COUNT = 20;
	protected ListRanking<String, PeakArenaExtAttribute> robotRankingList;

	public static PeakArenaBM getInstance() {
		return instance;
	}

	private RandomCombination[][] randomArray;
	private Comparator<ListRankingEntry<String, PeakArenaExtAttribute>> comparator = new Comparator<ListRankingEntry<String, PeakArenaExtAttribute>>() {
		@Override
		public int compare(ListRankingEntry<String, PeakArenaExtAttribute> o1, ListRankingEntry<String, PeakArenaExtAttribute> o2) {
			return o1.getRanking() - o2.getRanking();
		}
	};

	protected PeakArenaBM() {
		RandomCombination[] singleDigitArray = new RandomCombination[10];
		for (int i = 1; i <= 10; i++) {
			singleDigitArray[i - 1] = new RandomCombination(i);
		}

		// 初始化10个数的两数组合和三数组合
		ArrayList<RandomCombination> doubleList = new ArrayList<RandomCombination>();
		for (int i = 1; i <= 10; i++) {
			for (int j = i + 1; j <= 10; j++) {
				doubleList.add(new RandomCombination(i, j));
			}
		}

		RandomCombination[] doubleValueArray = new RandomCombination[doubleList.size()];
		doubleList.toArray(doubleValueArray);

		ArrayList<RandomCombination> tripleList = new ArrayList<RandomCombination>();
		for (int i = 1; i <= 10; i++) {
			for (int j = i + 1; j <= 10; j++) {
				for (int k = j + 1; k <= 10; k++) {
					tripleList.add(new RandomCombination(i, j, k));
				}
			}
		}

		RandomCombination[] tripleValueArray = new RandomCombination[tripleList.size()];
		tripleList.toArray(tripleValueArray);

		randomArray = new RandomCombination[4][];
		randomArray[1] = singleDigitArray;
		randomArray[2] = doubleValueArray;
		randomArray[3] = tripleValueArray;

		StoreHandler.getInstance().getOpenStoreNotification().subscribe(this);
	}

	private int convertPlace(int pivot, int percentage) {
		int result = (pivot * percentage + 99) / 100;
		if (result <= 0)
			result = 1;
		return result;
	}

	private int getPlace(ListRanking<String, PeakArenaExtAttribute> wholeRank, Player player) {
		String userId = player.getUserId();
		ListRankingEntry<String, PeakArenaExtAttribute> entry = wholeRank.getRankingEntry(userId);
		int playerPlace = ListRankingType.PEAK_ARENA.getMaxCapacity();
		if (entry != null) {
			playerPlace = entry.getRanking();
		} else {
			if (!wholeRank.isFull()) {
				getOrAddPeakArenaData(player);
				entry = wholeRank.getRankingEntry(userId);
			}
			if (entry == null) {
				playerPlace = wholeRank.getMaxCapacity();
			}
			playerPlace = entry.getRanking();
		}
		return playerPlace;
	}

	private boolean addEntry(String userId, List<ListRankingEntry<String, PeakArenaExtAttribute>> list, ListRanking<String, PeakArenaExtAttribute> ranking, int place) {
		ListRankingEntry<String, PeakArenaExtAttribute> entry = ranking.getRankingEntry(place);
		if (entry == null) {
			return false;
		}
		String key = entry.getKey();
		if (key.equals(userId)) {
			return false;
		}
		// 检查是被锁定
		if (entry.getExtension().adjustTimeOutState()) {
			return false;
		}
		boolean contain = false;
		for (int j = list.size(); --j >= 0;) {
			ListRankingEntry<String, PeakArenaExtAttribute> existEntry = list.get(j);
			if (existEntry.getKey().equals(key)) {
				contain = true;
				break;
			}
		}
		if (!contain) {
			list.add(entry);
			return true;
		} else {
			return false;
		}
	}

	private boolean fillInRange(String userId, int start, int end, ListRanking<String, PeakArenaExtAttribute> ranking, List<ListRankingEntry<String, PeakArenaExtAttribute>> list) {
		int random = HPCUtil.getRandom().nextInt(end - start + 1) + start;
		// 先在范围中随机一个
		if (addEntry(userId, list, ranking, random)) {
			return true;
		}
		int distance = end - start;
		int last = random + distance;
		// 在范围中选一个
		for (int i = random; i <= last; i++) {
			if (i > end) {
				// i -= distance;
				break;
			}
			if (addEntry(userId, list, ranking, i)) {
				return true;
			}
		}
		for (int i = random; i >= start; i--) {
			if (addEntry(userId, list, ranking, i)) {
				return true;
			}
		}
		return fillByTenSteps(userId, end, 1, list, ranking);
	}

	private boolean fillByTenSteps(String userId, int offset, int expectCount, List<ListRankingEntry<String, PeakArenaExtAttribute>> list, ListRanking<String, PeakArenaExtAttribute> ranking) {
		int needCount = expectCount;
		int count = 0;
		int capacity = ranking.getMaxCapacity();// 多线程情况会返回null，因此要做判空操作
		for (;;) {
			RandomCombination[] randomCombination = randomArray[needCount];
			int random = HPCUtil.getRandom().nextInt(randomCombination.length);
			int[] array = randomCombination[random].getArray();
			for (int i = array.length; --i >= 0;) {
				if (offset + array[i] <= capacity && addEntry(userId, list, ranking, offset + array[i]) && ++count >= expectCount) {
					return true;
				}
			}
			// 随机一组后不满足条件，倒叙或者正序遍历
			int start = offset + 1;
			offset += 10;
			int end = offset > capacity ? capacity : offset;
			if ((random & 1) == 1) {
				for (int i = start; i <= end; i++) {
					if (addEntry(userId, list, ranking, i) && ++count >= expectCount) {
						return true;
					}
				}
			} else {
				for (int i = end; --i >= start;) {
					if (addEntry(userId, list, ranking, i) && ++count >= expectCount) {
						return true;
					}
				}
			}
			if (offset >= capacity) {
				break;
			}
		}
		return false;
	}
	
	public void preloadRobot() {
		ListRanking<String, PeakArenaExtAttribute> ranking = getRanks();
		List<? extends ListRankingEntry<String, PeakArenaExtAttribute>> entrysCopy = ranking.getEntrysCopy();
		List<ListRankingEntry<String, PeakArenaExtAttribute>> robotRankings = new ArrayList<ListRankingEntry<String, PeakArenaExtAttribute>>();
		for (int i = 0; i < entrysCopy.size(); i++) {
			ListRankingEntry<String, PeakArenaExtAttribute> entry = entrysCopy.get(i);
			if (PlayerMgr.getInstance().isPersistantRobot(entry.getKey())) {
				robotRankings.add(entry);
			}
		}
		Collections.sort(robotRankings, new RobotFighingComparator());
		robotRankingList = new RobotListRankingImpl<PeakArenaExtAttribute>(ListRankingType.PEAK_ARENA.getType(), robotRankings);
	}

	// 玩家筛选
	public List<ListRankingEntry<String, PeakArenaExtAttribute>> SelectPeakArenaInfos(TablePeakArenaData data, Player player) {
		ListRanking<String, PeakArenaExtAttribute> wholeRank;
		// 获取玩家排名
		int playerPlace;
		if (data == null || data.getCurrentRecordId() == 0) {
			// 首次挑战只挑战机器人
			wholeRank = robotRankingList;
			playerPlace = wholeRank.getRankingSize() + 1;
//			System.out.println("玩家首次挑战，使用机器人数据！userId：" + player.getUserId());
		} else {
			wholeRank = getRanks();
			playerPlace = getPlace(wholeRank, player);
		}
		ArrayList<ListRankingEntry<String, PeakArenaExtAttribute>> result = new ArrayList<ListRankingEntry<String, PeakArenaExtAttribute>>();
		peakArenaMatchRule cfg = peakArenaMatchRuleHelper.getInstance().getBestMatch(playerPlace, true);
		if (cfg != null) {
			String userId = player.getUserId();
			int configEnemyCount = cfg.getEnemyCount();
			for (int i = 0; i < RESULT_COUNT; i++) {
				int cfgEnemyIndex = i < configEnemyCount ? i : configEnemyCount - 1;
				IReadOnlyPair<Integer, Integer> range = cfg.getEnemyRange(cfgEnemyIndex);
				int min = convertPlace(playerPlace, range.getT1());
				int max = convertPlace(playerPlace, range.getT2());
				// TODO 这次不管 有更高效的选择算法，赶时间暂时放下不实现：搜索的时候返回最后搜索完毕的位置，可以用于调整下一次的开始范围；每个对手的搜索只需要一个随机数就够了
				boolean added = fillInRange(userId, min, max, wholeRank, result);
				if (!added) {
					System.out.println("userId=" + userId + ",min=" + min + ",max=" + max + ",playerPlace=" + playerPlace + ",range.low=" + range.getT1() + ",range.high=" + range.getT2());
				}
			}
		} else {
			GameLog.error("巅峰竞技场", player.getUserId(), "找不到匹配规则,排名:" + playerPlace);
		}

		/*
		 * if (result.size() < RESULT_COUNT){ //TODO 这次不管 应该用机器人填充! }
		 */

		// 排序
		Collections.sort(result, comparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public ListRanking<String, PeakArenaExtAttribute> getRanks() {
		ListRanking<String, PeakArenaExtAttribute> wholeRank = RankingFactory.getSRanking(ListRankingType.PEAK_ARENA);
		return wholeRank;
	}

	public TablePeakArenaData getOrAddPeakArenaData(Player player) {
		if (!isOpen(player)) {
			return null;
		}
		return getOrAddPeakArenaData(player, null);
	}

	public TablePeakArenaData getOrAddPeakArenaDataForRobot(Player player) {
		if (!player.isRobot()) {
			GameLog.error("巅峰竞技场", player.getUserId(), "这个函数仅仅可以用于机器人的创建！");
			return null;
		}
		TablePeakArenaData result = getOrAddPeakArenaData(player, null);
		initTeamInfo(result, player);
		tablePeakArenaDataDAO.commit(result);
		return result;
	}

	public void update(TablePeakArenaData data) {
		tablePeakArenaDataDAO.update(data);
	}

	private void initTeamInfo(TablePeakArenaData peakData, Player player) {
		HeroMgr heroMgr = player.getHeroMgr();
		// List<Hero> heroList = heroMgr.getAllHerosExceptMainRole(HeroFightPowerComparator.getInstance());
		List<Hero> heroList = heroMgr.getAllHerosExceptMainRole(player, HeroFightPowerComparator.getInstance());

		int count = peakData.getTeamCount();
		if (count <= 0) {
			return;
		}

		ItemData magic = player.getMagic();
		for (int i = 0; i < count; i++) {
			TeamData team = new TeamData();
			team.setTeamId(i);
			if (magic == null) {
				team.setMagicId("");
			} else {
				team.setMagicId(magic.getId());
			}
			team.setHeros(Collections.<String> emptyList());
			// team.setHeroSkills(Collections.<TableSkill>emptyList());
			peakData.setTeam(team, i);
		}

		int heroCount = heroList.size();
		if (heroCount <= 0) {
			return;
		}

		int av = heroCount / count;
		int[] distributions = new int[count];
		int remainder = heroCount % count;
		for (int i = 0; i < count; i++) {
			distributions[i] = av;
			if (remainder > 0 && i < remainder) {
				distributions[i]++;
			}
			if (distributions[i] > 4) {
				distributions[i] = 4;
			}
		}
		int heroIndex = 0;

		String playerId = player.getUserId();
		RefParam<List<String>> checkedHeroIDList = new RefParam<List<String>>();
		for (int i = 0; i < count; i++) {
			TeamData team = peakData.getTeam(i);
			List<String> heroIdsList = new ArrayList<String>();
			for (int j = heroIndex; j < heroIndex + distributions[i]; j++) {
				Hero hero = heroList.get(j);
				if (hero == null) {
					continue;
				}
				heroIdsList.add(hero.getUUId());
			}
			heroIndex += distributions[i];

			getHeroInfoList(player, heroIdsList, heroMgr, playerId, checkedHeroIDList);
			team.setHeros(checkedHeroIDList.value);
			// team.setHeroSkills(heroSkillList);
		}
	}

	public List<TableSkill> getHeroInfoList(Player player, List<String> heroIdsList, HeroMgr heroMgr, String playerId, RefParam<List<String>> checkedHeroIDList) {
		List<String> newHeroList = new ArrayList<String>();
		List<TableSkill> heroSkillList = new ArrayList<TableSkill>();
		for (String id : heroIdsList) {
			// Hero heroData = heroMgr.getHeroById(id);
			Hero heroData = heroMgr.getHeroById(player, id);
			if (heroData == null) {
				GameLog.error("巅峰竞技场", playerId, "无效佣兵ID=" + id);
				continue;
			}
			newHeroList.add(id);
			TableSkill skill = heroData.getSkillMgr().getTableSkill(player, heroData.getUUId());
			heroSkillList.add(skill);
		}
		if (checkedHeroIDList != null) {
			checkedHeroIDList.value = newHeroList;
		}
		return heroSkillList;
	}

	/* private */TablePeakArenaData getOrAddPeakArenaData(Player player, RefInt temp) {
		String userId = player.getUserId();
		TablePeakArenaData data = tablePeakArenaDataDAO.get(userId);
		if (data == null) {
			data = createPeakData(player);
			tablePeakArenaDataDAO.commit(data);
		}
		getOrAddRankEntry(player, data, temp);

		return data;
	}

	// 在排行榜创建记录
	private ListRankingEntry<String, PeakArenaExtAttribute> getOrAddRankEntry(Player player, TablePeakArenaData data, RefInt temp) {
		String userId = player.getUserId();
		ListRanking<String, PeakArenaExtAttribute> ranking = getRanks();
		ListRankingEntry<String, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		int place;
		if (entry == null) {
			PeakArenaExtAttribute extension = createExtData(player);
			try {
				entry = ranking.addLast(userId, extension);
				place = entry.getRanking();
				data.setMaxPlace(place);
				RankingMgr.getInstance().addPeakFightingRanking(player);
			} catch (RankingCapacityNotEougthException e) {
				e.printStackTrace();
				place = ranking.getMaxCapacity();
				data.setMaxPlace(place, false);
			}
		} else {
			place = entry.getRanking();
		}
		if (temp != null) {
			temp.value = place;
		}
		return entry;
	}

	public PeakArenaExtAttribute createExtData(Player player) {
		// PeakArenaExtAttribute arenaExt = new PeakArenaExtAttribute(player.getCareer(), player.getHeroMgr().getFightingAll(), player.getUserName(), player.getHeadImage(), player.getLevel());
		int magciId = player.getMagic() == null ? 0 : player.getMagic().getModelId();
		PeakArenaExtAttribute arenaExt = new PeakArenaExtAttribute(player.getCareer(), player.getHeroMgr().getFightingAll(player), player.getUserName(), player.getHeadImage(), player.getLevel(), player.getVip(), magciId);
		arenaExt.setModelId(player.getModelId());
		arenaExt.setSex(player.getSex());
		// arenaExt.setFightingTeam(player.getHeroMgr().getFightingTeam());
		arenaExt.setFightingTeam(player.getHeroMgr().getFightingTeam(player));
		return arenaExt;
	}

	private TablePeakArenaData createPeakData(Player player) {
		TablePeakArenaData data = new TablePeakArenaData();
		String userId = player.getUserId();
		data.setUserId(userId);
		data.setLastGainCurrencyTime(System.currentTimeMillis());
		data.setLastResetDayOfYear(DateUtils.getCalendar().get(Calendar.DAY_OF_YEAR));

		initTeamInfo(data, player);

		// data.setRecordList(new ArrayList<PeakRecordInfo>());
		tablePeakArenaDataDAO.commit(data);
		return data;
	}

	public TablePeakArenaData getPeakArenaData(String userId) {
		return tablePeakArenaDataDAO.get(userId);
	}

	public void commit(TablePeakArenaData data) {
		tablePeakArenaDataDAO.commit(data);
	}

	public boolean switchTeam(Player player, List<Integer> list) {
		TablePeakArenaData data = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (data == null) {
			return false;
		}
		// 队伍配置定死3个
		int count = 3;
		if (data.getTeamCount() < count) {
			return false;
		}
		TeamData[] teams = new TeamData[count];
		for (int i = 0; i < count; i++) {
			teams[i] = data.getTeam(i);
		}
		boolean isChanged = false;
		for (int i = 0; i < list.size(); i++) {
			TeamData team = search(list, i, teams);
			if (team == null) {
				return false;
			}
			if (team != teams[i]) {
				data.setTeam(team, i);
				isChanged = true;
			}
		}
		if (isChanged) {
			tablePeakArenaDataDAO.commit(data);
		}
		return true;
	}

	private TeamData search(List<Integer> list, int index, TeamData[] teams) {
		int teamId = list.get(index);
		return TablePeakArenaData.search(teamId, teams);
	}

	public void addOthersRecord(String userId, PeakRecordInfo record) {
		// List<PeakRecordInfo> list = table.getRecordList();
		// // 需要限制战报的数量
		// int removeCount = list.size() - MAX_RECORD_COUNT;
		// if (removeCount > 0){
		// for (int i = 0; i<removeCount; i++){
		// list.remove(list.size()-1);
		// }
		// }
		// list.add(record);
		// tablePeakArenaDataDAO.commit(table);
		RoleExtPropertyStore<PeakRecordInfo> mapItemStroe = PeakArenaRecordHolder.getInstance().getMapItemStroe(userId);
		synchronized (mapItemStroe) {
			if (mapItemStroe.getSize() >= MAX_RECORD_COUNT) {
				Enumeration<PeakRecordInfo> enm = mapItemStroe.getExtPropertyEnumeration();
				List<PeakRecordInfo> list = new ArrayList<PeakRecordInfo>();
				while (enm.hasMoreElements()) {
					list.add(enm.nextElement());
				}
				Collections.sort(list, recordComparator);
				List<Integer> deleteIds = new ArrayList<Integer>(10);
				for (int i = MAX_DISPLAY_COUNT, size = list.size(); i < size; i++) {
					deleteIds.add(list.get(i).getId());
				}
				mapItemStroe.removeItem(deleteIds);
			}
			mapItemStroe.addItem(record);
		}
	}

	public List<PeakRecordInfo> getArenaRecordList(String userId) {
		// TablePeakArenaData tablePeakArenaData = tablePeakArenaDataDAO.get(userId);
		// if (tablePeakArenaData == null) {
		// return Collections.emptyList();
		// }
		// return tablePeakArenaData.getRecordList();
		RoleExtPropertyStore<PeakRecordInfo> mapItemStore = PeakArenaRecordHolder.getInstance().getMapItemStroe(userId);
		List<PeakRecordInfo> list = new ArrayList<PeakRecordInfo>(mapItemStore.getSize());
		Enumeration<PeakRecordInfo> enm = mapItemStore.getExtPropertyEnumeration();
		while (enm.hasMoreElements()) {
			list.add(enm.nextElement());
		}
		Collections.sort(list, recordComparator);
		return list;
	}

	/**
	 * 机器人一定开放
	 * 
	 * @param player
	 * @return
	 */
	public boolean isOpen(Player player) {
		if (!player.isRobot()) {
			if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.PEAK_ARENA, player)) {
				return false;
			}
		}
		return true;
	}

	public int getPlace(Player player) {
		if (!isOpen(player)) {
			return -1;
		}
		return getPlace(getRanks(), player);
	}

	public int getEnemyPlace(String userId) {
		ListRanking<String, PeakArenaExtAttribute> ranking = getRanks();
		ListRankingEntry<String, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null)
			return -1;
		return entry.getRanking();
	}

	public int gainExpectCurrency(TablePeakArenaData data, int gainPerHour, long currentTime) {
		long lastTime = data.getLastGainCurrencyTime();
		if (lastTime <= 0) {
			data.setLastGainCurrencyTime(currentTime);
			TablePeakArenaDataDAO.getInstance().update(data);
			return 0;
		}
		/*
		 * 确定奖励的时间间隔 读取对应排名的配置奖励（可能为零！） 奖励数量＝时间（小时）＊单位小时的奖励 （向下取整！）
		 */
		int expectCurrency = data.getExpectCurrency();
		long passTime = currentTime - lastTime;
		long millisPerCurrency = 0;
		int addedCurrency = 0;
		if (gainPerHour > 0) {
			// 每一点奖励数需要的毫秒值: ((毫秒/小时) / (奖励数/小时) = 毫秒/奖励数
			millisPerCurrency = MILLIS_PER_HOUR / gainPerHour;
			if (millisPerCurrency > 0) {
				addedCurrency = (int) (passTime / millisPerCurrency);
			}
		}
		if (addedCurrency <= 0) {
			return expectCurrency;
		}
		// 已经保证millisPerCurrency>0,因为millisPerCurrency<=0会导致addedCurrency==0
		int result = getAndAdjustGain(data, currentTime, expectCurrency, passTime, millisPerCurrency, addedCurrency);
		return result;
	}

	/**
	 * 计算当前能获得的巅峰币，不会产生领取动作（不修改player里面的PeakArenaCoin）
	 * 
	 * @param data
	 * @param place
	 * @return
	 */
	public int gainExpectCurrency(TablePeakArenaData data, int gainPerHour) {
		long currentTime = System.currentTimeMillis();
		return gainExpectCurrency(data, gainPerHour, currentTime);
	}

	public int getAndAdjustGain(TablePeakArenaData data, long currentTime, int expectCurrency, long passTime, long millisPerCurrency, int addedCurrency) {
		/*
		 * 保留时间损耗 保存奖励 保存最后领奖时间
		 */
		long interval = passTime % millisPerCurrency;
		data.setLastGainCurrencyTime(currentTime - interval);
		int result = expectCurrency + addedCurrency;
		data.setExpectCurrency(result);
		TablePeakArenaDataDAO.getInstance().update(data);
		return result;
	}

	public boolean gainCurrency(Player player, TablePeakArenaData data) {
		int expectCurrency = data.getExpectCurrency();
		if (expectCurrency == 0) {
			return false;
		}
		if (player.getUserGameDataMgr().addPeakArenaCoin(expectCurrency)) {
			data.setExpectCurrency(0);
			this.tablePeakArenaDataDAO.update(data);
			return true;
		}
		return false;
	}

	public void resetDataInNewDay(Player player) {
		if (player.isRobot() || !isOpen(player)) {
			return;
		}
		String userId = player.getUserId();
		TablePeakArenaData peakArenaData = getPeakArenaData(userId);
		if (peakArenaData == null) {
			GameLog.error("竞技场", userId, "重置时找不到巅峰竞技场玩家：" + userId);
			return;
		}
		peakArenaData.setChallengeCount(0);
		peakArenaData.setResetCount(0);
		peakArenaData.setBuyCount(0);
		tablePeakArenaDataDAO.update(peakArenaData);
	}

	public ListRankingEntry<String, PeakArenaExtAttribute> getPlayerRankEntry(Player player, TablePeakArenaData data) {
		ListRankingEntry<String, PeakArenaExtAttribute> result = getOrAddRankEntry(player, data, null);
		return result;
	}

	public ListRankingEntry<String, PeakArenaExtAttribute> getEnemyEntry(String enemyId) {
		ListRanking<String, PeakArenaExtAttribute> ranking = getRanks();
		return ranking.getRankingEntry(enemyId);
	}

	/**
	 * 根据排名结算一次巅峰竞技场可以领取的货币
	 * 
	 * @param player
	 * @param peakBM
	 * @param playerArenaData
	 * @param playerPlace
	 */
	public void addPeakArenaCoin(Player player, TablePeakArenaData playerArenaData, int playerPlace, long replaceTime) {
		int gainPerHour = peakArenaPrizeHelper.getInstance().getBestMatchPrizeCount(playerPlace);
		int addCount = gainExpectCurrency(playerArenaData, gainPerHour, replaceTime);
		if (addCount > 0) {
			if (ItemBagMgr.getInstance().addItem(player, eSpecialItemId.PeakArenaCoin.getValue(), addCount)) {
				playerArenaData.setExpectCurrency(0);
			} else {
				GameLog.error("巅峰竞技场", player.getUserId(), "增加巅峰竞技场货币失败");
			}
		}
	}

	@Override
	public void onChange(Pair<Player, Integer> newValue) {
		// if (newValue != null && newValue.getT1() != null
		// && newValue.getT2() != null && newValue.getT2() == eStoreType.PeakStore.getOrder()){
		Player user = newValue.getT1();
		TablePeakArenaData data = tablePeakArenaDataDAO.get(user.getUserId());
		if (data == null)
			return;
		int place = getPlace(user);
		addPeakArenaCoin(user, data, place, System.currentTimeMillis());
		// }
	}

	@Override
	public void onClose(IStream<Pair<Player, Integer>> whichStream) {
	}
	
	private static class RobotFighingComparator implements Comparator<ListRankingEntry<String, PeakArenaExtAttribute>> {

		@Override
		public int compare(ListRankingEntry<String, PeakArenaExtAttribute> o1, ListRankingEntry<String, PeakArenaExtAttribute> o2) {
			return -(o1.getExtension().getFighting() - o2.getExtension().getFighting());
		}

	}

}

class RandomCombination {

	private final int[] array;

	public RandomCombination(int one) {
		array = new int[1];
		array[0] = one;
	}

	public RandomCombination(int one, int two) {
		array = new int[2];
		array[0] = one;
		array[1] = two;
	}

	public RandomCombination(int one, int two, int three) {
		array = new int[3];
		array[0] = one;
		array[1] = two;
		array[2] = three;
	}

	public int[] getArray() {
		return array;
	}

}