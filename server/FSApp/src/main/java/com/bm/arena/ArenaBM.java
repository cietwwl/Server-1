package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.bm.rank.ListRankingType;
import com.bm.rank.RankType;
import com.bm.rank.arena.ArenaExtAttribute;
import com.bm.rank.arena.ArenaSettleComparable;
import com.bm.rank.arena.ArenaSettlement;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.common.HPCUtil;
import com.common.RefParam;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.ranking.exception.RankingCapacityNotEougthException;
import com.rw.service.Email.EmailUtils;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.dao.arena.ArenaInfoCfgDAO;
import com.rwbase.dao.arena.ArenaPrizeCfgDAO;
import com.rwbase.dao.arena.TableArenaDataDAO;
import com.rwbase.dao.arena.TableArenaRecordDAO;
import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
import com.rwbase.dao.arena.pojo.HurtValueRecord;
import com.rwbase.dao.arena.pojo.RecordInfo;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.arena.pojo.TableArenaRecord;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.user.readonly.TableUserIF;

public class ArenaBM {

	private RandomCombination[][] randomArray;
	private static ArenaBM instance = new ArenaBM();
	private TableArenaDataDAO tableArenaDataDAO = TableArenaDataDAO.getInstance();

	protected ArenaBM() {
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
	}

	public static ArenaBM getInstance() {
		// if (instance == null) {
		// instance = new ArenaBM();
		// }
		return instance;
	}

	public TableArenaData getArenaData(String userId) {
		return tableArenaDataDAO.get(userId);
	}

	public boolean updateAtkHeroList(List<String> list, Player p) {
		TableArenaData arenaData = tableArenaDataDAO.get(p.getUserId());
		if (arenaData == null) {
			return false;
		}

		// arenaData.setAtkHeroList(list);
		arenaData.setAtkList(list);
		tableArenaDataDAO.update(arenaData);

		// TODO HC 通知万仙阵检查阵容的战力
		AngelArrayTeamInfoHelper.getInstance().checkAndUpdateTeamInfo(p, arenaData.getAtkList());
		return true;
	}

	public List<String> getAtkHeroList(String userId) {
		TableArenaData arenaData = tableArenaDataDAO.get(userId);
		if (arenaData == null) {
			return Collections.EMPTY_LIST;
		}
		// List<String> list = arenaData.getAtkHeroList();
		List<String> list = arenaData.getAtkList();
		return list == null ? Collections.EMPTY_LIST : list;
	}

	public static final int ARENA_SIZE = 3;

	public ArenaExtAttribute createArenaExt(Player player) {
		// ArenaExtAttribute arenaExt = new ArenaExtAttribute(player.getCareer(), player.getHeroMgr().getFightingAll(), player.getUserName(), player.getHeadImage(), player.getLevel());
		ArenaExtAttribute arenaExt = new ArenaExtAttribute(player.getCareer(), player.getHeroMgr().getFightingAll(player), player.getUserName(), player.getHeadImage(), player.getLevel(), player.getVip(), (player.getMagic() != null ? player.getMagic().getModelId() : 0));
		arenaExt.setModelId(player.getModelId());
		arenaExt.setSex(player.getSex());
		// arenaExt.setFightingTeam(player.getHeroMgr().getFightingTeam());
		arenaExt.setFightingTeam(player.getHeroMgr().getFightingTeam(player));
		return arenaExt;
	}

	/**
	 * 增加竞技场玩家数据 此方法相对安全，如果玩家已拥有竞技场数据，是不会执行增加操作
	 * 
	 * @param player
	 * @return
	 */
	public TableArenaData addArenaData(Player player) {
		TableUserIF tableUser = player.getTableUser();
		int career = player.getCareer();
		if (career <= 0) {
			return null;
		}
		ListRanking<String, ArenaExtAttribute> listRanking = getRanking();
		int fighting = player.getMainRoleHero().getFighting();
		// int fighting = userOther.getFighting();
		String userId = tableUser.getUserId();
		String name = tableUser.getUserName();
		String headImage = tableUser.getHeadImageWithDefault();
		int level = player.getLevel();
		ArenaExtAttribute arenaExt = createArenaExt(player);
		int place;
		ListRankingEntry<String, ArenaExtAttribute> entry;
		try {
			// 如果本身存在于排行榜，会返回原对象
			entry = listRanking.addLast(userId, arenaExt);
			place = entry.getRanking();
		} catch (RankingCapacityNotEougthException e) {
			e.printStackTrace();
			place = listRanking.getMaxCapacity();
		}
		// TODO 先get一次，不过按照现在的缓存机制，get一次也存在风险
		TableArenaData data = tableArenaDataDAO.get(userId);
		if (data != null) {
			if (place < data.getMaxPlace()) {
				data.setMaxPlace(place);
			}
			return data;
		}
		data = new TableArenaData();
		data.setUserId(userId);
		data.setCareer(career);
		data.setMaxPlace(place);
		data.setFighting(fighting);
		data.setVip(player.getVip());
		data.setSex(player.getSex());
		// data.setHeros(new ArrayList<TableHeroData>());
		// data.setHeroAtrrs(new ArrayList<TableAttr>());
		// data.setPlayerAttr(player.getAttrMgr().getTableAttr()); //
		// TableAttrDAO.getInstance().get(player.getUserId()));
		// data.setPlayerSkill(player.getSkillMgr().getTableSkill()); //
		// TableSkillDAO.getInstance().get(player.getUserId()));

		// List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros();
		List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros(player);
		ArrayList<String> defaultHeros = new ArrayList<String>(4);
		// ArrayList<String> defaultAtkHeros = new ArrayList<String>(4);
		for (Hero hero : maxFightingHeros) {
			String heroId = hero.getUUId();
			if (!heroId.equals(userId)) {
				defaultHeros.add(heroId);
				// defaultAtkHeros.add(hero.getTemplateId());
			}
		}

		data.setHeroIdList(defaultHeros);
		data.setAtkList(new ArrayList<String>(defaultHeros));
		// data.setAtkHeroList(defaultAtkHeros);

		ArenaInfoCfg infoCfg = ArenaInfoCfgDAO.getInstance().getArenaInfo();
		data.setRemainCount(infoCfg.getCount());
		data.setHeadImage(headImage);
		data.setLevel(level);
		data.setName(name);
		ItemData magic = player.getMagic();
		if (magic == null) {
			data.setMagicId(0);
			data.setMagicLevel(0);
		} else {
			data.setMagicId(magic.getModelId());
			data.setMagicLevel(magic.getMagicLevel());
		}
		data.setTempleteId(player.getTemplateId());
		tableArenaDataDAO.update(data);
		return data;
	}

	public ListRankingEntry<String, ArenaExtAttribute> getEntry(String userId, int career) {
		// ListRanking<String, ArenaExtAttribute> ranking = RankingFactory.getSRanking(ListRankingType.getListRankingType(career));
		ListRanking<String, ArenaExtAttribute> ranking = RankingFactory.getSRanking(ListRankingType.ARENA);
		if (ranking == null) {
			return null;
		}
		return ranking.getRankingEntry(userId);
	}

	@SuppressWarnings("unchecked")
	public ListRanking<String, ArenaExtAttribute> getRanking() {
		return RankingFactory.getSRanking(ListRankingType.ARENA);
	}

	public int getArenaPlace(Player player) {
		int maxCapacity = ListRankingType.ARENA.getMaxCapacity();
		int career = player.getCareer();
		if (career <= 0) {
			return maxCapacity;
		}
		ListRanking<String, ArenaExtAttribute> ranking = getRanking();
		if (ranking == null) {
			GameLog.error("找不到竞技场排行榜：" + career);
			return maxCapacity;
		}
		String userId = player.getUserId();
		ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry != null) {
			return entry.getRanking();
		}
		if (!ranking.isFull()) {
			// TODO 这里有两种做法：
			// 1)addArenaData的时候返回当前排名，但要额外封装对象
			// 2)重新获取一次排名
			addArenaData(player);
			entry = ranking.getRankingEntry(userId);
		}
		if (entry == null) {
			return ranking.getMaxCapacity();
		}
		return entry.getRanking();
	}

	public int getOtherArenaPlace(String userId, int career) {
		ListRanking<String, ArenaExtAttribute> ranking = getRanking();
		if (ranking == null) {
			GameLog.error("找不到竞技场排行榜：" + career);
			return -1;
		}
		ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null) {
			return -1;
		}
		return entry.getRanking();
	}

	public void resetDataInNewDay(Player player) {
		if (player.getLevel() < ArenaConstant.ARENA_OPEN_LEVEL) {
			return;
		}
		TableArenaData tableArenaData = TableArenaDataDAO.getInstance().get(player.getUserId());
		if (tableArenaData == null) {
			return;
		}
		ArenaInfoCfg infoCfg = ArenaInfoCfgDAO.getInstance().getArenaInfo();
		tableArenaData.setRemainCount(infoCfg.getCount());
		tableArenaData.setScore(0);
		tableArenaData.setBuyTimes(0);
		tableArenaData.getRewardList().clear();
		TableArenaDataDAO.getInstance().update(tableArenaData);
	}

	// 奖励结算
	public void arenaDailyPrize(String userId, Ranking<ArenaSettleComparable, ArenaSettlement> ranking) {
		if (ranking == null) {
			ranking = RankingFactory.getRanking(RankType.ARENA_SETTLEMENT);
		}
		RankingEntry<ArenaSettleComparable, ArenaSettlement> entry = ranking.getRankingEntry(userId);
		if (entry == null) {
			return;
		}
		ArenaSettlement settle = entry.getExtendedAttribute();
		if (settle.getGetRewardMillis() > 0) {
			return;
		}
		settle.setGetRewardMillis(System.currentTimeMillis());
		ranking.subimitUpdatedTask(entry);
		String strPrize = ArenaPrizeCfgDAO.getInstance().getArenaPrizeCfgByPlace(entry.getComparable().getRanking());
		if (StringUtils.isEmpty(strPrize)) {
			GameLog.error("ArenaBM", "#arenaDailyPrize()", "获取奖励为空：" + userId + "," + entry.getComparable().getRanking());
		}
		BILogMgr.getInstance().logActivityBegin(PlayerMgr.getInstance().find(userId), null, BIActivityCode.ARENA_REWARDS, 0, 0);
		EmailUtils.sendEmail(userId, ArenaConstant.DAILY_PRIZE_MAIL_ID, strPrize, settle.getSettleMillis());

		List<BilogItemInfo> rewardslist = BilogItemInfo.fromEmailId(ArenaConstant.DAILY_PRIZE_MAIL_ID, strPrize);
		String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);
		BILogMgr.getInstance().logActivityEnd(PlayerMgr.getInstance().find(userId), null, BIActivityCode.ARENA_REWARDS, 0, true, 0, rewardInfoActivity, 0);
		Player player = PlayerMgr.getInstance().find(userId);
		player.getTempAttribute().setRedPointChanged();
		PlayerMgr.getInstance().setRedPointForHeartBeat(userId);
	}

	// 筛选玩家
	public List<ListRankingEntry<String, ArenaExtAttribute>> selectArenaInfos(Player player) {
		int career = player.getCareer();
		if (career <= 0) {
			return Collections.EMPTY_LIST;
		}
		String userId = player.getUserId();
		ListRanking<String, ArenaExtAttribute> ranking = getRanking();
		ArrayList<ListRankingEntry<String, ArenaExtAttribute>> result = new ArrayList<ListRankingEntry<String, ArenaExtAttribute>>();
		// 排行前10名，ing，随机拉取前10名的其中三个作为对手
		// 前10名以外按照此规则拉取对手(M是名次)：
		// 第三个人区间[0.8M,1.0M)
		// 第二个人区间[0.6M,0.8M)
		// 第一个人区间[0.4M,0.6M)
		int place = getArenaPlace(player);
		if (place <= 10) {
			fillByTenSteps(userId, result, 0, ranking, 3);
		} else {
			int decreasePlace = place / 5;
			int start = place - decreasePlace * 3;
			int end = start + decreasePlace - 1;
			fillInRange(userId, start, end, ranking, result);
			start += decreasePlace;
			end += decreasePlace;
			fillInRange(userId, start, end, ranking, result);
			start += decreasePlace;
			end += decreasePlace;
			fillInRange(userId, start, end, ranking, result);
		}
		// 纠正必要时的乱序
		Collections.sort(result, comparator);
		return result;
	}

	private Comparator<ListRankingEntry<String, ArenaExtAttribute>> comparator = new Comparator<ListRankingEntry<String, ArenaExtAttribute>>() {

		@Override
		public int compare(ListRankingEntry<String, ArenaExtAttribute> o1, ListRankingEntry<String, ArenaExtAttribute> o2) {
			return o1.getRanking() - o2.getRanking();
		}

	};

	/**
	 * 获取竞技场信息列表(兼容旧有方法)
	 * 
	 * @param type
	 * @return
	 */
	public List<ListRankingEntry<String, ArenaExtAttribute>> getArenaInfoList(ListRankingType type) {
		return RankingFactory.getSRanking(type).getEntrysCopy();
	}

	/**
	 * 在范围内随机抽取一个，如果范围内没有合适的，往后10名尝试拉取，直到拉取到人为止
	 * 
	 * @param userId
	 * @param start
	 * @param end
	 * @param ranking
	 * @param list
	 * @return
	 */
	private boolean fillInRange(String userId, int start, int end, ListRanking<String, ArenaExtAttribute> ranking, List<ListRankingEntry<String, ArenaExtAttribute>> list) {
		int random = HPCUtil.getRandom().nextInt(end - start + 1) + start;
		// 先在范围中随机一个
		if (addEntry(userId, list, ranking, random)) {
			return true;
		}
		int distance = end - start;
		int last = random + distance;
		// 在范围中选一个
		for (int i = random; i <= last; i++) {
			if (addEntry(userId, list, ranking, i > end ? (i - distance) : i)) {
				return true;
			}
		}
		return fillByTenSteps(userId, list, end, ranking, 1);
	}

	/**
	 * 往后拉取10名，直到没有人为止
	 * 
	 * @param userId
	 * @param list
	 * @param offset
	 * @param ranking
	 * @param expectCount
	 * @return
	 */
	private boolean fillByTenSteps(String userId, List<ListRankingEntry<String, ArenaExtAttribute>> list, int offset, ListRanking<String, ArenaExtAttribute> ranking, int expectCount) {
		int needCount = expectCount;
		int count = 0;
		int capacity = ranking.getMaxCapacity();// 多线程情况会返回null，因此要做判空操作
		for (;;) {
			RandomCombination[] randomCombination = randomArray[needCount];
			int random = HPCUtil.getRandom().nextInt(randomCombination.length);
			int[] array = randomCombination[random].getArray();
			for (int i = array.length; --i >= 0;) {
				if (addEntry(userId, list, ranking, offset + array[i]) && ++count >= expectCount) {
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

	private boolean addEntry(String userId, List<ListRankingEntry<String, ArenaExtAttribute>> list, ListRanking<String, ArenaExtAttribute> ranking, int place) {
		ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(place);
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
			ListRankingEntry<String, ArenaExtAttribute> existEntry = list.get(j);
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

	/**
	 * 获取录像的伤害统计
	 * 
	 * @param userId
	 * @param recordId
	 * @return
	 */
	public List<HurtValueRecord> getRecordHurtValue(String userId, int recordId) {
		return getRecordHurtValue(userId, recordId, null);
	}

	public List<HurtValueRecord> getRecordHurtValue(String userId, int recordId, RefParam<String> enemyUserId) {
		List<RecordInfo> list = getArenaRecordList(userId);
		if (list == null) {
			return Collections.emptyList();
		}
		for (int i = list.size(); --i >= 0;) {
			RecordInfo info = list.get(i);
			if (info.getRecordId() == recordId) {
				if (enemyUserId != null) {
					enemyUserId.value = info.getUserId();
				}
				return info.getHurtList();
			}
		}
		return Collections.emptyList();
	}

	public List<RecordInfo> getArenaRecordList(String userId) {
		// TODO 需要判断非null
		return TableArenaRecordDAO.getInstance().get(userId).getRecordList();
	}

	public void addRecord(String userId, RecordInfo record, boolean updateDB) {
		// 此方法不是线程安全
		TableArenaRecordDAO recrodDAO = TableArenaRecordDAO.getInstance();
		TableArenaRecord arenaRecord = recrodDAO.get(userId);
		if (arenaRecord == null) {
			GameLog.error("ArenaBM", userId, "save arena record fail");
			return;
		}
		List<RecordInfo> list = arenaRecord.getRecordList();
		int size = list.size();
		if (size >= 15) {
			list.remove(0);
			size--;
		}
		if (size <= 0) {
			record.setRecordId(1);
		} else {
			RecordInfo last = list.get(size - 1);
			record.setRecordId(last.getRecordId() + 1);
		}
		list.add(record);
		if (updateDB) {
			recrodDAO.update(userId);
		}
	}

	public void onPlayerChanged(Player player) {
		String userId = player.getUserId();
		int career = player.getCareer();
		int level = player.getLevel();
		String headImage = player.getHeadImage();
		String userName = player.getUserName();
		String headBox = player.getHeadFrame();
		TableArenaData data = tableArenaDataDAO.get(userId);
		int fighting = 0;
		if (data != null) {
			fighting = getAllFighting(data);
			data.setLevel(level);
			data.setCareer(career);
			data.setFighting(fighting);
			data.setHeadImage(headImage);
			data.setHeadbox(headBox);
			data.setVip(player.getVip());
			data.setSex(player.getSex());
			ItemData magic = player.getMagic();
			if (magic != null) {
				data.setMagicId(magic.getModelId());
				data.setMagicLevel(magic.getMagicLevel());
			} else {
				data.setMagicId(0);
				data.setMagicLevel(0);
			}
			data.setName(player.getUserName());
			data.setTempleteId(player.getTemplateId());
			tableArenaDataDAO.update(data);
		}
		ListRankingEntry<String, ArenaExtAttribute> entry = getEntry(userId, career);
		if (entry != null) {
			ArenaExtAttribute arenaExt = entry.getExtension();
			arenaExt.setCareer(career);
			arenaExt.setLevel(level);
			// TODO 出问题的时候不更新战力，后面改
			arenaExt.setFighting(fighting);
			arenaExt.setHeadImage(headImage);
			arenaExt.setHeadbox(headBox);
			arenaExt.setName(userName);
			arenaExt.setModelId(player.getModelId());
			arenaExt.setVip(player.getVip());
			// arenaExt.setFightingTeam(player.getHeroMgr().getFightingTeam());
			arenaExt.setFightingTeam(player.getHeroMgr().getFightingTeam(player));
			if (player.getMagic() != null) {
				arenaExt.setMagicCfgId(player.getMagic().getModelId());
			}
			// 不主动提交属性变化的更新了
		}
	}

	public int getAllFighting(TableArenaData arenaData) {
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), arenaData.getHeroIdList());
		List<ArmyHero> armyList = armyInfo.getHeroList();
		int armySize = armyList.size();
		int fighting = 0;
		for (int i = 0; i < armySize; i++) {
			ArmyHero hero = armyList.get(i);
			fighting += hero.getFighting();
		}
		fighting += armyInfo.getPlayer().getFighting();
		return fighting;
	}

	public int getMaxPlace(TableArenaData data) {
		int maxPlace = data.getMaxPlace();
		ListRankingEntry<String, ArenaExtAttribute> entry = getEntry(data.getUserId(), data.getCareer());
		if (entry == null) {
			return maxPlace;
		} else {
			int ranking = entry.getRanking();
			if (ranking < 0) {
				return maxPlace;
			}
			return Math.min(ranking, maxPlace);
		}
	}

	public void notifyPlayerLevelUp(String userId, int career, int newLevel) {
		TableArenaData data = tableArenaDataDAO.get(userId);
		if (data != null) {
			data.setLevel(newLevel);
			tableArenaDataDAO.update(data);
		}
		ListRankingEntry<String, ArenaExtAttribute> entry = getEntry(userId, career);
		if (entry != null) {
			entry.getExtension().setLevel(newLevel);
			// 不主动提交属性变化的更新了
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
