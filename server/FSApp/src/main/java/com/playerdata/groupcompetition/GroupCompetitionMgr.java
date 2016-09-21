package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompStartType;
import com.playerdata.groupcompetition.util.GCompUtil;
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
		GCompStageController controller = new GCompStageController(stageList);
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
	
	void notifyStageChange(IGCompStage currentStage) {
		GroupCompetitionGlobalData saveData = _dataHolder.get();
		if (currentStage.getStageType() == GCompStageType.SELECTION) {
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
		currentEventsData.setCurrentStatus(eventsType);
		currentEventsData.setCurrentStatusFinished(false);
		currentEventsData.addRelativeGroups(eventsType, relativeGroupIds);
		this._dataHolder.update();
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
}
