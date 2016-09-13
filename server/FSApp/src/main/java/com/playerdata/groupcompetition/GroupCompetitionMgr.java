package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.playerdata.groupcompetition.data.IGCStage;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageControlCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageControlCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GroupCompetitionMgr {

	private static final GroupCompetitionMgr _instance = new GroupCompetitionMgr();
	
	protected GroupCompetitionMgr() {}
	
	public static final GroupCompetitionMgr getInstance() {
		return _instance;
	}
	
	private void loadGroupCompetitionSaveData() {
		String attrData = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_COMPETITION);
		GroupCompetitionSaveData.initDataFromDB(attrData);
	}
	
	private long getServerStartTime() {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.WEEK_OF_YEAR, -3);
		instance.set(Calendar.HOUR_OF_DAY, 11);
		instance.set(Calendar.MINUTE, 0);
		return instance.getTimeInMillis();
	}
	
	private void checkStartGroupCompetition() {
		if(GroupCompetitionSaveData.getInstance().getHeldTimes() > 0) {
			
		} else {
			// 没有举办过
			GroupCompetitionStageCfgDAO groupCompetitionStageCfgDAO = GroupCompetitionStageCfgDAO.getInstance();
			long serverStartTime = this.getServerStartTime();
			Calendar instance = Calendar.getInstance();
			instance.setTimeInMillis(serverStartTime);
			GroupCompetitionStageControlCfg cfg = GroupCompetitionStageControlCfgDAO.getInstance().getByType(GCConstance.CompetitionStartType.START_TYPE_SERVER_TIME_OFFSET);
			IReadOnlyPair<Integer, Integer> time = cfg.getStartTimeInfo();
			instance.add(Calendar.WEEK_OF_YEAR, cfg.getStartWeeks());
			instance.set(Calendar.DAY_OF_WEEK, cfg.getStartDayOfWeek());
			instance.set(Calendar.HOUR_OF_DAY, time.getT1());
			if(time.getT2() > 0) {
				instance.set(Calendar.MINUTE, time.getT2());	
			}
			List<Integer> stageDetail = cfg.getStageDetailList();
			List<IGCStage> stageList = new ArrayList<IGCStage>();
			for (int i = 0, size = stageDetail.size(); i < size; i++) {
				GroupCompetitionStageCfg stageCfg = groupCompetitionStageCfgDAO.getCfgById(String.valueOf(stageDetail.get(i)));
				stageList.add(GCStageFactory.createStageByType(stageCfg.getStageType(), stageCfg));
			}
			GCStageController controller = new GCStageController(stageList, stageList);
			controller.start(instance.getTimeInMillis()); // controller开始
		}
	}
	
	/**
	 * 服务器启动完毕的通知
	 */
	public void serverStartComplete() {
		this.loadGroupCompetitionSaveData();
		this.checkStartGroupCompetition();
	}
}
