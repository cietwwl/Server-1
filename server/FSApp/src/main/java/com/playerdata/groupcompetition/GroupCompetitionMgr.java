package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.playerdata.Player;
import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.holder.GCompBaseInfoMgr;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.holder.GCompEventsDataMgr;
import com.playerdata.groupcompetition.holder.GCompGroupScoreRankingMgr;
import com.playerdata.groupcompetition.holder.GCompHistoryDataMgr;
import com.playerdata.groupcompetition.holder.GCompMemberMgr;
import com.playerdata.groupcompetition.holder.GCompOnlineMemberMgr;
import com.playerdata.groupcompetition.holder.GCompTeamMgr;
import com.playerdata.groupcompetition.holder.data.GCompBaseInfo;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.stageimpl.GCompEventsData;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompEventsStartPara;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompGroupMemberLeaveTask;
import com.playerdata.groupcompetition.util.GCompRestStartPara;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompStartType;
import com.playerdata.groupcompetition.util.GCompUpdateFightingTask;
import com.playerdata.groupcompetition.util.GCompUpdateGroupInfoTask;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageControlCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageControlCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 
 * 帮派争霸管理器
 * 
 * @author CHEN.P
 *
 */
public class GroupCompetitionMgr {

	private static GroupCompetitionMgr _instance = new GroupCompetitionMgr();
	
	protected GroupCompetitionMgr() {}
	
	public static GroupCompetitionMgr getInstance() {
		return _instance;
	}
	
	private GroupCompetitionDataHolder _dataHolder = GroupCompetitionDataHolder.getInstance();
	private final AtomicInteger _againstIdGenerator = new AtomicInteger();
//	private static final SimpleDateFormat _FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	
//	private long getServerStartTime() {
//		Calendar instance = Calendar.getInstance();
////		instance.add(Calendar.WEEK_OF_YEAR, -3);
////		instance.set(Calendar.HOUR_OF_DAY, 11);
////		instance.set(Calendar.MINUTE, 0);
//		TableZoneInfo zoneInfo = ZoneBM.getInstance().getTableZoneInfo(GameManager.getZoneId());
//		if(zoneInfo == null){
//			System.out.println("---------------zoneInfo is null. zoneId:" + GameManager.getZoneId());
//		}
//		try {
//			Date date = _FORMATTER.parse(zoneInfo.getOpenTime());
//			instance.setTime(date);
//			instance.set(Calendar.HOUR_OF_DAY, 10);
//			instance.set(Calendar.MINUTE, 0);
//		} catch (Exception e) {
//			throw new IllegalArgumentException("开服时间不正确：" + zoneInfo.getOpenTime());
//		}
//		return instance.getTimeInMillis();
//	}
	
	private long getFirstStartReferenceTime() {
		String attribute = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION_REFERENCE_TIME);
		if (attribute != null && attribute.length() > 0) {
			return Long.parseLong(attribute);
		} else {
			long timeMillis = System.currentTimeMillis();
			GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_COMPETITION_REFERENCE_TIME, String.valueOf(timeMillis));
			return timeMillis;
		}
	}
	
	private List<IGCompStage> getStageList(GCompStartType startType) {
		GroupCompetitionStageCfgDAO groupCompetitionStageCfgDAO = GroupCompetitionStageCfgDAO.getInstance();
		GroupCompetitionStageControlCfg cfg = GroupCompetitionStageControlCfgDAO.getInstance().getByType(startType.sign);
		List<Integer> stageDetail = cfg.getStageDetailList();
		List<IGCompStage> stageList = new ArrayList<IGCompStage>();
		for (int i = 0, size = stageDetail.size(); i < size; i++) {
			GroupCompetitionStageCfg stageCfg = groupCompetitionStageCfgDAO.getCfgById(String.valueOf(stageDetail.get(i)));
			stageList.add(GCompStageFactory.createStageByType(stageCfg.getStageType(), stageCfg));
		}
		return stageList;
	}
	
	private void createAndStartController(List<IGCompStage> stageList, long startTime, Object firstStageStartPara, int session) {
		GCompStageController controller = new GCompStageController(stageList, session > 0 ? session : 1, firstStageStartPara);
		controller.start(startTime); // controller开始
	}
	
	private void startStageController(GCompStartType startType, long relativeTime, int session) {
		long startTimeMillis = GCompUtil.calculateGroupCompetitionStartTime(startType, relativeTime);
		List<IGCompStage> stageList = this.getStageList(startType);
		this.createAndStartController(stageList, startTimeMillis, null, session);
	}
	
	private List<IGCompStage> getStageListByHeldTimes(GroupCompetitionGlobalData data) {
		List<IGCompStage> stageList;
		if (data.getHeldTimes() == 1) {
			stageList = this.getStageList(GCompStartType.SERVER_TIME_OFFSET);
		} else {
			stageList = this.getStageList(GCompStartType.NUTRAL_TIME_OFFSET);
		}
		return stageList;
	}
	
	private IGCompStage filterStage(GCompStageType type, List<IGCompStage> stageList) {
		for (Iterator<IGCompStage> itr = stageList.iterator(); itr.hasNext();) {
			IGCompStage stage = itr.next();
			if (stage.getStageType() != type) {
				itr.remove();
			} else {
				return stage;
			}
		}
		return null;
	}
	
	// 历史数据处于Selection的阶段
	private void continueOldSelectionStageController(GroupCompetitionGlobalData data) {
//		if (data.getHeldTimes() == 1) {
//			// 还是处于首次的状态
//			this.startStageController(GCompStartType.SERVER_TIME_OFFSET, getServerStartTime());
//		} else {
////			List<IGCompStage> stageList = this.getStageList(GCompStartType.NUTRAL_TIME_OFFSET);
////			this.createAndStartController(stageList, System.currentTimeMillis(), null);
//			this.startStageController(GCompStartType.NUTRAL_TIME_OFFSET, 0);
//		}
		List<IGCompStage> stageList = this.getStageListByHeldTimes(data);
		if (data.getCurrentStageEndTime() > System.currentTimeMillis()) {
			GCompRestStartPara para = new GCompRestStartPara();
			para.setEndTime(data.getCurrentStageEndTime());
			this.createAndStartController(stageList, System.currentTimeMillis(), para, _dataHolder.get().getHeldTimes());
		} else {
			IGCompStage eventsStage = this.filterStage(GCompStageType.EVENTS, stageList);
//			GCompEventsStartPara para = new GCompEventsStartPara();
//			List<String> groupIds = GCompUtil.getTopCountGroupsFromRank();
//			List<String> loseGroupIds = Collections.emptyList();
//			GCompHistoryDataMgr.getInstance().setSelectedGroupIds(groupIds);
//			para.setEventsType(groupIds.size() > 8 ? GCEventsType.TOP_16 : GCEventsType.TOP_8);
//			para.setWinGroupIds(groupIds);
//			para.setLoseGroupIds(loseGroupIds);
			GroupCompetitionStageCfg stageCfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(eventsStage.getStageCfgId());
			IReadOnlyPair<Integer, Integer> timeInfo = stageCfg.getStartTimeInfo();
			long startTime = GCompUtil.getNearTimeMillis(timeInfo.getT1().intValue(), timeInfo.getT2().intValue(), System.currentTimeMillis());
			createAndStartController(stageList, startTime, null, _dataHolder.get().getHeldTimes());
		}
	}
	
	// 历史数据处于休整期的阶段
	private void continueOldRestStageController(GroupCompetitionGlobalData data) {
		List<IGCompStage> stageList = this.getStageListByHeldTimes(data);
		IGCompStage eventsStage = this.filterStage(GCompStageType.REST, stageList);
		if(eventsStage != null) {
			if (data.getCurrentStageEndTime() < System.currentTimeMillis()) {
				// 开始一轮新的
				this.startStageController(GCompStartType.NUTRAL_TIME_OFFSET, 0, _dataHolder.get().getHeldTimes() + 1);
			} else {
				GCompRestStartPara para = new GCompRestStartPara();
				para.setEndTime(data.getCurrentStageEndTime());
				createAndStartController(stageList, System.currentTimeMillis(), para, _dataHolder.get().getHeldTimes());
			}
		} else {
			throw new IllegalStateException("找不到休整阶段实例");
		}
	}
	
	// 历史数据处于赛事期
	private void continueEventsStageController(GroupCompetitionGlobalData data) {
		List<IGCompStage> stageList = this.getStageListByHeldTimes(data);
		IGCompStage eventsStage = this.filterStage(GCompStageType.EVENTS, stageList);
		if (eventsStage != null) {
			GCompEventsRecord record = data.getCurrentEventsRecord();
			List<String> winGroupIds;
			List<String> loseGroupIds;
			GCEventsType eventsType = record.getCurrentEventsType();
			if (eventsType == GCEventsType.FINAL) {
				if(record.isCurrentTypeFinished()) {
					IGCompStage restStage = this.filterStage(GCompStageType.REST, stageList);
					if(restStage != null) {
						GroupCompetitionStageCfg stageCfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(eventsStage.getStageCfgId());
						IReadOnlyPair<Integer, Integer> timeInfo = stageCfg.getStartTimeInfo();
						long startTime = GCompUtil.getNearTimeMillis(timeInfo.getT1().intValue(), timeInfo.getT2().intValue(), System.currentTimeMillis());
						createAndStartController(stageList, startTime, null, _dataHolder.get().getHeldTimes());
						record.setCurrentEventsStatusStartTime(startTime);
						record.setCurrentEventsStatusEndTime(startTime + TimeUnit.MINUTES.toMillis(GCompEventsStatus.getTotalLastMinutes()));
					}
					return;
				} else {
					GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(GCEventsType.QUATER);
					List<GCompAgainst> list = eventsData.getAgainsts();
					winGroupIds = new ArrayList<String>(list.size());
					loseGroupIds = new ArrayList<String>(list.size());
					for (int i = 0, size = list.size(); i < size; i++) {
						GCompAgainst against = list.get(i);
						winGroupIds.add(against.getWinGroupId());
						loseGroupIds.add(against.getWinGroup() == against.getGroupA() ? against.getGroupB().getGroupId() : against.getGroupA().getGroupId());
					}
					record.setCurrentStatus(GCompEventsStatus.NONE);
				}
			} else {
				if (record.isCurrentTypeFinished()) {
					GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(eventsType);
					winGroupIds = new ArrayList<String>();
					loseGroupIds = new ArrayList<String>();
					List<GCompAgainst> againsts = eventsData.getAgainsts();
					for (int i = 0, size = againsts.size(); i < size; i++) {
						GCompAgainst against = againsts.get(i);
						winGroupIds.add(against.getWinGroupId());
						loseGroupIds.add(against.getWinGroup() == against.getGroupA() ? against.getGroupB().getGroupId() : against.getGroupA().getGroupId());
					}
					eventsType = eventsType.getNext();
				} else {
					winGroupIds = record.getRelativeGroupIds(eventsType);
					loseGroupIds = Collections.emptyList();
					record.setCurrentStatus(GCompEventsStatus.NONE);
				}
			}
			GCompEventsStartPara para = new GCompEventsStartPara();
			para.setEventsType(eventsType);
			para.setWinGroupIds(winGroupIds);
			para.setLoseGroupIds(loseGroupIds);
			GroupCompetitionStageCfg stageCfg = GroupCompetitionStageCfgDAO.getInstance().getCfgById(eventsStage.getStageCfgId());
			IReadOnlyPair<Integer, Integer> timeInfo = stageCfg.getStartTimeInfo();
			long startTime = GCompUtil.getNearTimeMillis(timeInfo.getT1().intValue(), timeInfo.getT2().intValue(), System.currentTimeMillis());
			createAndStartController(stageList, startTime, para, _dataHolder.get().getHeldTimes());
			record.setCurrentEventsStatusStartTime(startTime);
			record.setCurrentEventsStatusEndTime(startTime + TimeUnit.MINUTES.toMillis(GCompEventsStatus.getTotalLastMinutes()));
		} else {
			throw new IllegalStateException("找不到赛事阶段实例");
		}
	}
	
	private void checkStartGroupCompetition() {
		GroupCompetitionGlobalData data = _dataHolder.get();
		if(data.getHeldTimes() > 0) {
			// 有举办过赛事
			switch (data.getCurrentStageType()) {
			case SELECTION:
				this.continueOldSelectionStageController(data);
				break;
			case EVENTS:
				this.continueEventsStageController(data);
				break;
			default:
				this.continueOldRestStageController(data);
				break;
			}
//			if (data.getCurrentEventsRecord() != null) {
//				data.getCurrentEventsRecord().reset();
//			}
//			data.setCurrentStageType(GCompStageType.SELECTION);
//			this.continueOldSelectionStageController(data); // 测试：现在先默认重新开始
		} else {
			// 没有举办过
			this.startStageController(GCompStartType.SERVER_TIME_OFFSET, this.getFirstStartReferenceTime(), 0);
		}
	}
	
	void allStageEndOfCurrentRound() {
		this.startStageController(GCompStartType.NUTRAL_TIME_OFFSET, 0, _dataHolder.get().getHeldTimes() + 1);
	}
	
	void notifyStageChange(IGCompStage currentStage, int sessionId) {
		GroupCompetitionGlobalData saveData = _dataHolder.get();
		if (currentStage.getStageType() == GCompStageType.SELECTION && saveData.getCurrentStageType() != GCompStageType.SELECTION) {
			// 有可能是停服再起服的时候开始的
			saveData.increaseHeldTimes();
			saveData.updateLastHeldTime(System.currentTimeMillis());
			saveData.setEndTimeOfSelection(0); // 重置时间
			GCompEventsRecord currentData = saveData.getCurrentEventsRecord();
			if (currentData != null) {
				currentData.reset();
			}
			GCompFightingRankMgr.refreshGroupFightingRank();
		}
		saveData.setCurrentStageEndTime(currentStage.getStageEndTime());
		saveData.setCurrentStageType(currentStage.getStageType());
		if (currentStage.getStageType() == GCompStageType.EVENTS) {
			saveData.setEndTimeOfEventsStage(currentStage.getStageEndTime());
		}
		_dataHolder.update();
		GCompBaseInfoMgr.getInstance().sendBaseInfoToAll();
	}
	
	void updateEndTimeOfCurrentSession(long endTime) {
		GroupCompetitionGlobalData saveData = _dataHolder.get();
		saveData.setEndTimeOfCurrentSession(endTime);
		_dataHolder.update();
	}
	
	/**
	 * 
	 * 玩家登录游戏
	 * 
	 * @param player
	 */
	public void onPlayerLogin(Player player) {
		try {
			GCompBaseInfoMgr.getInstance().sendBaseInfo(player); // 同步帮派争霸基础数据
			// 帮战的离开队伍事件
			GCompTeamMgr.getInstance().forcePlayerLeaveTeam(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 通知玩家进入备战区
	 * 
	 * @param player
	 */
	public void onPlayerEnterPrepareArea(Player player) {
		// 同步Team数据、在线角色数据、帮战详细数据
		GroupCompetitionGlobalData globalData = _dataHolder.get();
		if (globalData.getCurrentStageType() == GCompStageType.EVENTS) {
			try {
				int matchId = GCompEventsDataMgr.getInstance().getMatchIdOfGroup(GroupHelper.getGroupId(player), globalData.getCurrentEventsRecord().getCurrentEventsType());
				if (matchId > 0) {
					GCompOnlineMemberMgr.getInstance().addToOnlineMembers(player);
					GCompOnlineMemberMgr.getInstance().sendOnlineMembers(player);
					GCompDetailInfoMgr.getInstance().sendDetailInfo(matchId, player);
					GCompMemberMgr.getInstance().onPlayerEnterPrepareArea(player);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * 通知玩家离开备战区
	 * 
	 * @param player
	 */
	public void onPlayerLeavePrepareArea(Player player) {
		try {
			GCompOnlineMemberMgr.getInstance().removeOnlineMembers(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 判断帮派是有参与本届赛事
	 * 
	 * @param groupId
	 * @return
	 */
	public boolean isGroupInCompetition(String groupId) {
		GCompStageType currentStageType = this.getCurrentStageType();
		if(currentStageType == null) {
			return false;
		}
		switch (currentStageType) {
		case SELECTION:
			long endTime = this.getEndTimeOfSelection();
			if (endTime > 0 && endTime < System.currentTimeMillis()) {
				return GCompHistoryDataMgr.getInstance().getSelectedGroupIds().contains(groupId);
			}
			break;
		case EVENTS:
			GCompEventsRecord record = _dataHolder.get().getCurrentEventsRecord();
			GCEventsType eventsType = record.getCurrentEventsType();
			List<String> groupIds = null;
			if (record.isCurrentTypeFinished()) {
				if (eventsType == GCEventsType.FINAL) {
					return false;
				}
//				groupIds = record.getRelativeGroupIds(eventsType.getNext());
				GCompEventsData eventsData = GCompEventsDataMgr.getInstance().getEventsData(eventsType.getNext());
				groupIds = eventsData.getRelativeGroupIds();
			} else {
				groupIds = record.getRelativeGroupIds(eventsType);
			}
			return groupIds.contains(groupId);
		default:
			break;
		}
		return false;
	}
	
	/**
	 * 服务器启动完毕的通知
	 */
	public void serverStartComplete() {
		this._dataHolder.loadGroupCompetitionGlobalData();
		this._againstIdGenerator.set(this._dataHolder.get().getAgainstIdRecord());
		GCompEventsDataMgr.getInstance().loadEventsGlobalData(); // 加载赛事数据
		GCompDetailInfoMgr.getInstance().onServerStartComplete(); // 加载详情数据
		GCompGroupScoreRankingMgr.getInstance().serverStartComplete(); // 加载积分排名数据
		GCompHistoryDataMgr.getInstance().serverStartComplete(); // 加载历史数据
		GCompUpdateFightingTask.submit();
		this.checkStartGroupCompetition();
	}
	
	/**
	 * 
	 * <pre>
	 * 获取本次帮派争霸开始的比赛类型（16强、8强）
	 * 通过此返回值，可以判断本次帮派争霸是从个16强还是8强开始的
	 * </pre>
	 * 
	 * @return 本次帮派争霸开始的比赛类型
	 */
	public GCEventsType getFisrtTypeOfCurrent() {
		GCompEventsRecord record = _dataHolder.get().getCurrentEventsRecord();
		if (record != null) {
			return record.getFirstEventsType();
		} else {
			return GCEventsType.TOP_8;
		}
	}
	
	/**
	 * <pre>
	 * 获取本次帮派争霸的当前赛事类型
	 * </pre>
	 * @return
	 */
	public GCEventsType getCurrentEventsType() {
		GCompEventsRecord record = _dataHolder.get().getCurrentEventsRecord();
		if (record != null) {
			return record.getCurrentEventsType();
		} else {
			throw new IllegalStateException("当前不是赛事状态！");
		}
	}
	
	public void updateEndTimeOfSelection(long endTime) {
		GroupCompetitionGlobalData saveData = _dataHolder.get();
		saveData.setEndTimeOfSelection(endTime);
		_dataHolder.update();
	}
	
	/**
	 * 
	 * 更新当前赛事的状态
	 * 
	 * @param eventsType
	 * @param relativeGroupIds
	 */
	public void updateCurrenEventstData(GCEventsType eventsType, List<String> relativeGroupIds) {
		GroupCompetitionGlobalData globalData = _dataHolder.get();
		GCompEventsRecord currentEventsData = globalData.getCurrentEventsRecord();
		if (currentEventsData == null) {
			currentEventsData = new GCompEventsRecord();
			currentEventsData.setHeldTime(globalData.getLastHeldTimeMillis());
			currentEventsData.setFirstEventsType(eventsType);
			globalData.setCurrentRecord(currentEventsData);
		} else if (currentEventsData.getHeldTime() != globalData.getLastHeldTimeMillis()) {
			currentEventsData.reset();
			currentEventsData.setHeldTime(globalData.getLastHeldTimeMillis());
			currentEventsData.setFirstEventsType(eventsType);
		}
		currentEventsData.setCurrentEventsType(eventsType);
		currentEventsData.setCurrentEventsTypeFinished(false);
		currentEventsData.addRelativeGroups(eventsType, relativeGroupIds);
		currentEventsData.setCurrentStatus(GCompEventsStatus.NONE);
		this._dataHolder.update();
	}
	
	/**
	 * 
	 * 获取参与目前赛事的帮派id
	 * 
	 * @return
	 */
	public List<String> getCurrentRelativeGroupIds() {
		GroupCompetitionGlobalData globalData = _dataHolder.get();
		if (globalData.getCurrentStageType() == GCompStageType.EVENTS) {
			GCompEventsRecord eventsGlobalData = globalData.getCurrentEventsRecord();
			return eventsGlobalData.getCurrentRelativeGroupIds();
		}
		return Collections.emptyList();
	}
	
	public void updateEventsStatus(GCompEventsStatus status) {
		GroupCompetitionGlobalData globalData = _dataHolder.get();
		if (globalData.getCurrentStageType() != GCompStageType.EVENTS) {
			globalData.setCurrentStageType(GCompStageType.EVENTS);
		}
		GCompEventsRecord record = globalData.getCurrentEventsRecord();
		record.setCurrentStatus(status);
		record.setCurrentEventsStatusStartTime(System.currentTimeMillis());
		record.setCurrentEventsStatusEndTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(status.getLastMinutes()));
		GCompBaseInfoMgr.getInstance().sendBaseInfoToAll();
	}
	
	public void notifyEventsEnd(GCEventsType type, List<GCompAgainst> againsts) {
		GroupCompetitionGlobalData globalData = _dataHolder.get();
		if (type == GCEventsType.FINAL) {
			for (GCompAgainst against : againsts) {
				if (against.isChampionEvents()) {
					globalData.addChampion(against.getWinGroup());
					break;
				}
			}
		}
		globalData.getCurrentEventsRecord().setCurrentEventsTypeFinished(true);
		_dataHolder.update();
	}
	
	/**
	 * 
	 * 获取当前阶段的结束时间
	 * 
	 * @return
	 */
	public long getCurrentStageEndTime() {
		return this._dataHolder.get().getCurrentStageEndTime();
	}
	
	/**
	 * 
	 * 获取本届的开始时间
	 * 
	 * @return
	 */
	public long getStartTimeOfCurrentSession() {
		return this._dataHolder.get().getLastHeldTimeMillis();
	}
	
	/**
	 * 
	 * 获取本次海选的结束时间
	 * 
	 * @return
	 */
	public long getEndTimeOfSelection() {
		return this._dataHolder.get().getEndTimeOfSelection();
	}
	
	/**
	 * 
	 * 获取本届赛事的起始时间
	 * 
	 * @return
	 */
	public IReadOnlyPair<Long, Long> getCurrentSessionTimeInfo() {
		GroupCompetitionGlobalData globalData = this._dataHolder.get();
		return Pair.Create(globalData.getLastHeldTimeMillis(), globalData.getEndTimeOfEventsStage());
	}
	
	/**
	 * 
	 * 获取当前是第几届
	 * 
	 * @return
	 */
	public int getCurrentSessionId() {
		GroupCompetitionGlobalData globalData = this._dataHolder.get();
		return globalData.getHeldTimes();
	}
	
	/**
	 * 
	 * 获取当前的阶段类型
	 * 
	 * @return
	 */
	public GCompStageType getCurrentStageType() {
		return this._dataHolder.get().getCurrentStageType();
	}
	
	/**
	 * 
	 * 获取当前的赛事的阶段
	 * 
	 * @return
	 */
	public GCompEventsStatus getCurrentEventsStatus() {
		GCompEventsRecord eventsData = this._dataHolder.get().getCurrentEventsRecord();
		if (eventsData != null && eventsData.getCurrentEventsType() != null) {
			return eventsData.getCurrentStatus();
		}
		return GCompEventsStatus.NONE;
	}
	
	/**
	 * 
	 * 判断当前的比赛是否已经结束
	 * 
	 * @return
	 */
	public boolean isCurrentEventsEnd() {
		GCompEventsRecord eventsData = this._dataHolder.get().getCurrentEventsRecord();
		if(eventsData != null) {
			return eventsData.isCurrentTypeFinished();
		}
		return false;
	}
	
	/**
	 * 
	 * 获取下一个对阵id
	 * 
	 * @return
	 */
	public int getNextAgainstId() {
		int id = _againstIdGenerator.incrementAndGet();
		this._dataHolder.get().setAgainstIdRecord(id);
		this._dataHolder.update();
		return id;
	}
	
	public GCompBaseInfo createBaseInfoSynData() {
		GroupCompetitionGlobalData globalData = this._dataHolder.get();
		GCompBaseInfo baseInfo = new GCompBaseInfo();
		if (globalData.getCurrentStageType() != null) {
			switch (globalData.getCurrentStageType()) {
			case EVENTS:
			case SELECTION:
				baseInfo.setStart(true);
				break;
			default:
				baseInfo.setStart(false);
				break;
			}
			long startTime;
			long endTime;
			baseInfo.setCurrentStageType(globalData.getCurrentStageType());
			if (globalData.getCurrentStageType() == GCompStageType.EVENTS) {
				GCompEventsRecord record = globalData.getCurrentEventsRecord();
				baseInfo.setEventStatus(record.getCurrentStatus());
				startTime = record.getCurrentEventsStatusStartTime();
				endTime = record.getCurrentEventsStatusEndTime();
			} else {
				startTime = globalData.getLastHeldTimeMillis();
				endTime = globalData.getCurrentStageEndTime();
			}
			baseInfo.setEndTime(endTime);
			baseInfo.setStartTime(startTime);
		}
		baseInfo.setSession(globalData.getHeldTimes());
		return baseInfo;
	}
	
	public void notifyGroupInfoChange(Group group) {
		GameWorldFactory.getGameWorld().asynExecute(new GCompUpdateGroupInfoTask(group));
	}
	
	public void notifyGroupMemberLeave(Group group, String userId) {
		GameWorldFactory.getGameWorld().asynExecute(new GCompGroupMemberLeaveTask(group.getGroupBaseDataMgr().getGroupData().getGroupId(), userId));
	}
}
