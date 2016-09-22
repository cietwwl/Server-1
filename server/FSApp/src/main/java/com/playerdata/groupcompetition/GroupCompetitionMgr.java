package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.holder.GCOnlineMemberMgr;
import com.playerdata.groupcompetition.holder.GCTeamDataMgr;
import com.playerdata.groupcompetition.holder.GCompBaseInfoMgr;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.holder.GCompMatchDataMgr;
import com.playerdata.groupcompetition.holder.data.GCompBaseInfo;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompStartType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageControlCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageControlCfg;

/**
 * 
 * 帮派争霸管理器
 * 
 * @author CHEN.P
 *
 */
public class GroupCompetitionMgr {

	private static final GroupCompetitionMgr _instance = new GroupCompetitionMgr();
	
	protected GroupCompetitionMgr() {}
	
	public static final GroupCompetitionMgr getInstance() {
		return _instance;
	}
	
	private GroupCompetitionDataHolder _dataHolder = GroupCompetitionDataHolder.getInstance();
	private final AtomicInteger _againstIdGenerator = new AtomicInteger();
	
	private long getServerStartTime() {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.WEEK_OF_YEAR, -3);
		instance.set(Calendar.HOUR_OF_DAY, 11);
		instance.set(Calendar.MINUTE, 0);
		return instance.getTimeInMillis();
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
	
	private void createAndStartController(List<IGCompStage> stageList, long startTime) {
		GroupCompetitionGlobalData data = _dataHolder.get();
		int heldTimes = data.getHeldTimes();
		GCompStageController controller = new GCompStageController(stageList, heldTimes > 0 ? heldTimes : 1);
		controller.start(startTime); // controller开始
	}
	
	private void startStageController(GCompStartType startType, long relativeTime) {
		long startTimeMillis = GCompUtil.calculateGroupCompetitionStartTime(startType, relativeTime);
		List<IGCompStage> stageList = this.getStageList(startType);
		this.createAndStartController(stageList, startTimeMillis);
	}
	
	private void continueOldStageController(GroupCompetitionGlobalData data) {
		if (data.getHeldTimes() == 1) {
			// 还是处于首次的状态
			this.startStageController(GCompStartType.SERVER_TIME_OFFSET, getServerStartTime());
		} else {
			List<IGCompStage> stageList = this.getStageList(GCompStartType.NUTRAL_TIME_OFFSET);
			this.createAndStartController(stageList, System.currentTimeMillis());
		}
	}
	
	private void checkStartGroupCompetition() {
		GroupCompetitionGlobalData data = _dataHolder.get();
		if(data.getHeldTimes() > 0) {
			// 有举办过赛事
			switch (data.getCurrentStageType()) {
			case SELECTION:
				this.continueOldStageController(data);
				break;
			case EVENTS:
				// TODO 处理赛事阶段期间停服，然后起服
				break;
			default:
				break;
			}
		} else {
			// 没有举办过
			this.startStageController(GCompStartType.SERVER_TIME_OFFSET, this.getServerStartTime());
		}
	}
	
	void allStageEndOfCurrentRound() {
		this.startStageController(GCompStartType.NUTRAL_TIME_OFFSET, 0);
	}
	
	void notifyStageChange(IGCompStage currentStage, int sessionId) {
		GroupCompetitionGlobalData saveData = _dataHolder.get();
		if (currentStage.getStageType() == GCompStageType.SELECTION && sessionId != saveData.getHeldTimes()) {
			// 有可能是停服再起服的时候开始的
			saveData.increaseHeldTimes();
			saveData.updateLastHeldTime(System.currentTimeMillis());
			GCompEventsGlobalData currentData = saveData.getCurrentEventsData();
			if (currentData != null) {
				currentData.reset();
			}
		}
		saveData.setCurrentStageEndTime(currentStage.getStageEndTime());
		saveData.setCurrentStageType(currentStage.getStageType());
		this._dataHolder.update();
		GCompBaseInfoMgr.getInstance().sendBaseInfoToAll();
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
				int matchId = GCompMatchDataMgr.getInstance().getMatchIdOfGroup(GroupHelper.getGroupId(player), globalData.getCurrentEventsData().getCurrentEventsType());
				if (matchId > 0) {
					GCTeamDataMgr.getInstance().sendTeamData(matchId, player);
					GCOnlineMemberMgr.getInstance().addToOnlineMembers(player);
					GCOnlineMemberMgr.getInstance().sendOnlineMembers(player);
					GCompDetailInfoMgr.getInstance().sendDetailInfo(matchId, player);
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
			GCOnlineMemberMgr.getInstance().removeOnlineMembers(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 服务器启动完毕的通知
	 */
	public void serverStartComplete() {
		this._dataHolder.loadGroupCompetitionGlobalData();
		this._againstIdGenerator.set(this._dataHolder.get().getAgainstIdRecord());
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
		return _dataHolder.get().getCurrentEventsData().getFirstEventsType();
	}
	
	/**
	 * <pre>
	 * 获取本次帮派争霸的当前赛事类型
	 * </pre>
	 * @return
	 */
	public GCEventsType getCurrentEventsType() {
		return _dataHolder.get().getCurrentEventsData().getCurrentEventsType();
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
		GCompEventsGlobalData currentEventsData = globalData.getCurrentEventsData();
		if(currentEventsData == null) {
			currentEventsData = new GCompEventsGlobalData();
			currentEventsData.setHeldTime(globalData.getLastHeldTimeMillis());
			currentEventsData.setFirstEventsType(eventsType);
			globalData.setCurrentData(currentEventsData);
		}
		currentEventsData.setCurrentEventsType(eventsType);
		currentEventsData.setCurrentStatusFinished(false);
		currentEventsData.addRelativeGroups(eventsType, relativeGroupIds);
		currentEventsData.setCurrentStatus(GCompEventsStatus.NONE);
		this._dataHolder.update();
	}
	
	public List<String> getCurrentRelativeGroupIds() {
		GroupCompetitionGlobalData globalData = _dataHolder.get();
		if (globalData.getCurrentStageType() == GCompStageType.EVENTS) {
			GCompEventsGlobalData eventsGlobalData = globalData.getCurrentEventsData();
			return eventsGlobalData.getCurrentRelativeGroupIds();
		}
		return Collections.emptyList();
	}
	
	public void updateEventsStatus(GCompEventsStatus status) {
		GroupCompetitionGlobalData globalData = _dataHolder.get();
		globalData.getCurrentEventsData().setCurrentStatus(status);
		GCompBaseInfoMgr.getInstance().sendBaseInfoToAll();
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
		GCompEventsGlobalData eventsData = this._dataHolder.get().getCurrentEventsData();
		if (eventsData != null && eventsData.getCurrentEventsType() != null) {
			return eventsData.getCurrentStatus();
		}
		return GCompEventsStatus.NONE;
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
				baseInfo.setCurrentStageType(globalData.getCurrentStageType());
				baseInfo.setEndTime(globalData.getCurrentStageEndTime());
				baseInfo.setStartTime(globalData.getLastHeldTimeMillis());
				baseInfo.setStart(true);
				if(globalData.getCurrentStageType() == GCompStageType.EVENTS) {
					baseInfo.setEventStatus(globalData.getCurrentEventsData().getCurrentStatus());
				}
				break;
			default:
				baseInfo.setStart(false);
			}
		}
		return baseInfo;
	}
}
