package com.rw.service.PeakArena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bm.arena.ArenaConstant;
import com.bm.rank.ListRankingType;
import com.common.HPCUtil;
import com.common.RefInt;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.IStreamListner;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.ranking.exception.RankingCapacityNotEougthException;
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
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.skill.pojo.TableSkill;

public class PeakArenaBM implements IStreamListner<Pair<Player, Integer>> {

	private static PeakArenaBM instance;
	private TablePeakArenaDataDAO tablePeakArenaDataDAO = TablePeakArenaDataDAO.getInstance();
	private static int RESULT_COUNT = 3; // 随机后的结果人数
	private static long MILLIS_PER_HOUR = TimeUnit.HOURS.toMillis(1);

	public static PeakArenaBM getInstance() {
		if (instance == null) {
			instance = new PeakArenaBM();
		}
		return instance;
	}

	private RandomCombination[][] randomArray;
	private Comparator<ListRankingEntry<String, PeakArenaExtAttribute>> comparator = 
			new Comparator<ListRankingEntry<String,PeakArenaExtAttribute>>() {
		@Override
		public int compare(ListRankingEntry<String, PeakArenaExtAttribute> o1,
				ListRankingEntry<String, PeakArenaExtAttribute> o2) {
			return o1.getRanking() - o2.getRanking();
		}
	};
	private PeakArenaBM() {
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

	private int convertPlace(int pivot, int percentage){
		int result = (pivot * percentage+99) / 100;
		if (result <= 0) result = 1;
		return result;
	}
	
	private int getPlace(ListRanking<String, PeakArenaExtAttribute> wholeRank,Player player){
		String userId = player.getUserId();
		ListRankingEntry<String, PeakArenaExtAttribute> entry = wholeRank.getRankingEntry(userId);
		int playerPlace = ListRankingType.PEAK_ARENA.getMaxCapacity();
		if (entry != null) {
			playerPlace = entry.getRanking();
		}else{
			if (!wholeRank.isFull()){
				getOrAddPeakArenaData(player);
				entry = wholeRank.getRankingEntry(userId);
			}
			if (entry == null){
				playerPlace = wholeRank.getMaxCapacity();
			}
			playerPlace = entry.getRanking();
		}
		return playerPlace;
	}

	private boolean addEntry(String userId, List<ListRankingEntry<String, PeakArenaExtAttribute>> list, 
			ListRanking<String, PeakArenaExtAttribute> ranking, int place) {
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
	
	private boolean fillInRange(String userId, int start, int end, ListRanking<String, PeakArenaExtAttribute> ranking,
			List<ListRankingEntry<String, PeakArenaExtAttribute>> list) {
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
				//i -= distance;
				break;
			}
			if (addEntry(userId, list, ranking, i)) {
				return true;
			}
		}
		for (int i= random; i>= start ; i--){
			if (addEntry(userId, list, ranking, i)) {
				return true;
			}
		}
		return fillByTenSteps(userId, end, 1, list, ranking);
	}
	
	private boolean fillByTenSteps(String userId, int offset, int expectCount,
			List<ListRankingEntry<String, PeakArenaExtAttribute>> list,
			ListRanking<String, PeakArenaExtAttribute> ranking) {
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
	
	// 玩家筛选
	public List<ListRankingEntry<String, PeakArenaExtAttribute>> SelectPeakArenaInfos(TablePeakArenaData data, Player player) {
		ListRanking<String, PeakArenaExtAttribute> wholeRank = getRanks();
		//获取玩家排名
		int playerPlace = getPlace(wholeRank,player);
		ArrayList<ListRankingEntry<String, PeakArenaExtAttribute>> result = new ArrayList<ListRankingEntry<String, PeakArenaExtAttribute>>();
		peakArenaMatchRule cfg = peakArenaMatchRuleHelper.getInstance().getBestMatch(playerPlace,true);
		if (cfg != null){
			String userId = player.getUserId();
			int configEnemyCount = cfg.getEnemyCount();
			for(int i =0;i< RESULT_COUNT;i++){
				int cfgEnemyIndex = i < configEnemyCount ? i : configEnemyCount -1;
				IReadOnlyPair<Integer, Integer> range = cfg.getEnemyRange(cfgEnemyIndex);
				int min = convertPlace(playerPlace,range.getT1());
				int max = convertPlace(playerPlace,range.getT2());
				//TODO 这次不管 有更高效的选择算法，赶时间暂时放下不实现：搜索的时候返回最后搜索完毕的位置，可以用于调整下一次的开始范围；每个对手的搜索只需要一个随机数就够了
				boolean added = fillInRange(userId,min,max,wholeRank,result);
				if (!added){
					System.out.println("userId=" + userId + ",min=" + min + ",max=" + max + ",playerPlace="
							+ playerPlace + ",range.low=" + range.getT1() + ",range.high=" + range.getT2());
				}
			}
		}else{
			GameLog.error("巅峰竞技场", player.getUserId(), "找不到匹配规则,排名:"+playerPlace);
		}
		
		/*
		if (result.size() < RESULT_COUNT){
		//TODO 这次不管 应该用机器人填充!
		}*/
		
		//排序
		Collections.sort(result, comparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public ListRanking<String, PeakArenaExtAttribute> getRanks() {
		ListRanking<String, PeakArenaExtAttribute> wholeRank = RankingFactory.getSRanking(ListRankingType.PEAK_ARENA);
		return wholeRank;
	}
	
	public TablePeakArenaData getOrAddPeakArenaData(Player player) {
		if (player.getLevel() < ArenaConstant.PEAK_ARENA_OPEN_LEVEL) {
			return null;
		}
		return getOrAddPeakArenaData(player, null);
	}
	
	public TablePeakArenaData getOrAddPeakArenaDataForRobot(Player player){
		return getOrAddPeakArenaData(player, null);
	}

	private TablePeakArenaData getOrAddPeakArenaData(Player player, RefInt temp) {
		String userId = player.getUserId();
		TablePeakArenaData data = tablePeakArenaDataDAO.get(userId);
		if (data == null) {
			data = createPeakData(player);
			tablePeakArenaDataDAO.commit(data);
		}
		getOrAddRankEntry(player,data,temp);
		
		return data;
	}
	
	// 在排行榜创建记录
	private ListRankingEntry<String, PeakArenaExtAttribute> getOrAddRankEntry(Player player,TablePeakArenaData data,
			RefInt temp){
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
			} catch (RankingCapacityNotEougthException e) {
				e.printStackTrace();
				place = ranking.getMaxCapacity();
				data.setMaxPlace(place,false);
			}
		}else{
			place = entry.getRanking();
		}
		if (temp != null){
			temp.value = place;
		}
		return entry;
	}

	public PeakArenaExtAttribute createExtData(Player player) {
		PeakArenaExtAttribute arenaExt = new PeakArenaExtAttribute(player.getCareer(), player.getHeroMgr().getFightingAll(), player.getUserName(), player.getHeadImage(), player.getLevel());
		arenaExt.setModelId(player.getModelId());
		arenaExt.setSex(player.getSex());
		arenaExt.setFightingTeam(player.getHeroMgr().getFightingTeam());
		return arenaExt;
	}

	protected TablePeakArenaData createPeakData(Player player, int place) {
		TablePeakArenaData data = createPeakData(player);
		data.setMaxPlace(place);
		return data;
	}
	
	private TablePeakArenaData createPeakData(Player player) {
		TablePeakArenaData data = new TablePeakArenaData();
		String userId = player.getUserId();
		data.setUserId(userId);
		data.setLastGainCurrencyTime(System.currentTimeMillis());

		ItemData magic = player.getMagic();
		for (int i = 0; i < data.getTeamCount(); i++) {
			TeamData team = new TeamData();
			team.setTeamId(i);
			if (magic == null) {
				team.setMagicId(0);
				team.setMagicLevel(0);
			} else {
				team.setMagicId(magic.getModelId());
				team.setMagicLevel(magic.getMagicLevel());
			}
			team.setHeros(new ArrayList<String>());
			team.setHeroSkills(new ArrayList<TableSkill>());
			data.setTeam(team, i);
		}
		data.setRecordList(new ArrayList<PeakRecordInfo>());
		tablePeakArenaDataDAO.commit(data);
		return data;
	}

	public TablePeakArenaData getPeakArenaData(String userId) {
		return tablePeakArenaDataDAO.get(userId);
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
		for(int i = 0;i<count;i++){
			teams[i] = data.getTeam(i);
		}
		boolean isChanged =false;
		for(int i = 0; i<list.size();i++){
			TeamData team = search(list,i,teams);
			if(team == null){
				return false;
			}
			if (team != teams[i]){
				data.setTeam(team, i);
				isChanged = true;
			}
		}
		if (isChanged){
			tablePeakArenaDataDAO.commit(data);
		}
		return true;
	}

	private TeamData search(List<Integer> list,int index, TeamData[] teams) {
		int teamId = list.get(index);
		return TablePeakArenaData.search(teamId, teams);
	}

	public void addOthersRecord(TablePeakArenaData table, PeakRecordInfo record) {
		List<PeakRecordInfo> list = table.getRecordList();
		// 需要限制战报的数量
		int removeCount = list.size()- 9;
		if (removeCount > 0){
			for (int i = 0; i<removeCount; i++){
				list.remove(list.size()-1);
			}
		}
		list.add(record);
		tablePeakArenaDataDAO.commit(table);
	}

	public List<PeakRecordInfo> getArenaRecordList(String userId) {
		TablePeakArenaData tablePeakArenaData = tablePeakArenaDataDAO.get(userId);
		if (tablePeakArenaData == null) {
			return Collections.emptyList();
		}
		return tablePeakArenaData.getRecordList();
	}

	public int getPlace(Player player) {
		if (player.getLevel() < ArenaConstant.PEAK_ARENA_OPEN_LEVEL) {
			return -1;
		}
		return getPlace(getRanks(),player);
	}

	public int getEnemyPlace(String userId) {
		ListRanking<String, PeakArenaExtAttribute> ranking = getRanks();
		ListRankingEntry<String, PeakArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null) return -1;
		return entry.getRanking();
	}
	
	public int gainExpectCurrency(TablePeakArenaData data, int gainPerHour,long currentTime) {
		long lastTime = data.getLastGainCurrencyTime();
		if (lastTime <= 0) {
			data.setLastGainCurrencyTime(currentTime);
			TablePeakArenaDataDAO.getInstance().update(data);
			return 0;
		}
/*
确定奖励的时间间隔
读取对应排名的配置奖励（可能为零！）
奖励数量＝时间（小时）＊单位小时的奖励 （向下取整！）
 * */
		int expectCurrency = data.getExpectCurrency();
		long passTime = currentTime - lastTime;
		long millisPerCurrency = 0;
		int addedCurrency = 0;
		if (gainPerHour > 0){
			// 每一点奖励数需要的毫秒值: ((毫秒/小时) / (奖励数/小时) = 毫秒/奖励数
			millisPerCurrency = MILLIS_PER_HOUR / gainPerHour;
			if (millisPerCurrency > 0){
				addedCurrency = (int)(passTime / millisPerCurrency);
			}
		}
		if (addedCurrency <= 0) {
			return expectCurrency;
		}
		//已经保证millisPerCurrency>0,因为millisPerCurrency<=0会导致addedCurrency==0
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
		return gainExpectCurrency(data,gainPerHour,currentTime);
	}

	public int getAndAdjustGain(TablePeakArenaData data, long currentTime, int expectCurrency, long passTime,
			long millisPerCurrency, int addedCurrency) {
		/*
保留时间损耗
保存奖励
保存最后领奖时间
		 * 
		 * */
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
		int level = player.getLevel();
		if (level < ArenaConstant.PEAK_ARENA_OPEN_LEVEL) {
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
		ListRankingEntry<String, PeakArenaExtAttribute> result= getOrAddRankEntry(player,data,null);
		return result;
	}

	public ListRankingEntry<String, PeakArenaExtAttribute> getEnemyEntry(String enemyId) {
		ListRanking<String, PeakArenaExtAttribute> ranking = getRanks();
		return ranking.getRankingEntry(enemyId);
	}

	/**
	 * 根据排名结算一次巅峰竞技场可以领取的货币
	 * @param player
	 * @param peakBM
	 * @param playerArenaData
	 * @param playerPlace
	 */
	public void addPeakArenaCoin(Player player,
			TablePeakArenaData playerArenaData, int playerPlace, long replaceTime) {
		int gainPerHour = peakArenaPrizeHelper.getInstance().getBestMatchPrizeCount(playerPlace);
		int addCount = gainExpectCurrency(playerArenaData,gainPerHour,replaceTime);
		if (addCount > 0){
			if (player.getItemBagMgr().addItem(eSpecialItemId.PeakArenaCoin.getValue(), addCount)){
				playerArenaData.setExpectCurrency(0);
			}else{
				GameLog.error("巅峰竞技场", player.getUserId(), "增加巅峰竞技场货币失败");
			}
		}
	}
	
	@Override
	public void onChange(Pair<Player, Integer> newValue) {
		if (newValue != null && newValue.getT1() != null 
				&& newValue.getT2() != null && newValue.getT2() == eStoreType.PeakStore.getOrder()){
			Player user = newValue.getT1();
			TablePeakArenaData data = tablePeakArenaDataDAO.get(user.getUserId());
			if (data == null) return;
			int place = getPlace(user);
			addPeakArenaCoin(user,data,place,System.currentTimeMillis());
		}
	}
	@Override
	public void onClose(IStream<Pair<Player, Integer>> whichStream) {
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