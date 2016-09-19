package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.groupcompetition.data.IGCStage;
import com.playerdata.groupcompetition.util.GCEventsType;
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
	
	private void checkStartGroupCompetition() {
		GroupCompetitionSaveData data = _dataHolder.get();
		if(data.getHeldTimes() > 0) {
			// 有举办过赛事
			GroupCompetitionSaveData saveData = _dataHolder.get();
		} else {
			// 没有举办过
			GroupCompetitionStageCfgDAO groupCompetitionStageCfgDAO = GroupCompetitionStageCfgDAO.getInstance();
			long startTimeMillis = GCompUtil.calculateGroupCompetitionStartTime(GCompStartType.SERVER_TIME_OFFSET, this.getServerStartTime());
			GroupCompetitionStageControlCfg cfg = GroupCompetitionStageControlCfgDAO.getInstance().getByType(GCompStartType.SERVER_TIME_OFFSET.sign);
			List<Integer> stageDetail = cfg.getStageDetailList();
			List<IGCStage> stageList = new ArrayList<IGCStage>();
			for (int i = 0, size = stageDetail.size(); i < size; i++) {
				GroupCompetitionStageCfg stageCfg = groupCompetitionStageCfgDAO.getCfgById(String.valueOf(stageDetail.get(i)));
				stageList.add(GCompStageFactory.createStageByType(stageCfg.getStageType(), stageCfg));
			}
			GCompStageController controller = new GCompStageController(stageList, stageList);
			controller.start(startTimeMillis); // controller开始
		}
	}
	
	/**
	 * 服务器启动完毕的通知
	 */
	public void serverStartComplete() {
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
		return _dataHolder.get().getCurrentData().getFirstEventsType();
	}
	
	/**
	 * 
	 * 更新最后一次举办时间
	 * 
	 * @param time
	 */
	void updateLaseHeldTime(long time) {
		GroupCompetitionSaveData saveData = _dataHolder.get();
		saveData.increaseHeldTimes();
		saveData.updateLastHeldTime(time);
		GCompCurrentData currentData = saveData.getCurrentData();
		if(currentData != null) {
			currentData.reset();
		}
		this._dataHolder.update();
	}
	
	public void updateCurrentData(GCEventsType startType, List<String> relativeGroupIds) {
		GroupCompetitionSaveData saveData = _dataHolder.get();
		GCompCurrentData currentData = saveData.getCurrentData();
		if(currentData == null) {
			currentData = new GCompCurrentData();
			currentData.setHeldTime(saveData.getLastHeldTimeMillis());
			currentData.setFirstEventsType(startType);
			saveData.setCurrentData(currentData);
		}
		currentData.setCurrentStatus(startType);
		currentData.setCurrentStatusFinished(false);
		currentData.addRelativeGroups(startType, relativeGroupIds);
		this._dataHolder.update();
	}
	
	public int getNextAgainstId() {
		int id = _againstIdGenerator.incrementAndGet();
		this._dataHolder.get().setAgainstIdRecord(id);
		this._dataHolder.update();
		return id;
	}
}
