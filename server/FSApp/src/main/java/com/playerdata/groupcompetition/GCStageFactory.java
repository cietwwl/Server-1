package com.playerdata.groupcompetition;

import com.playerdata.groupcompetition.data.IGCStage;
import com.playerdata.groupcompetition.stageimpl.GCEmptyStage;
import com.playerdata.groupcompetition.stageimpl.GCEventsStage;
import com.playerdata.groupcompetition.stageimpl.GCRestStage;
import com.playerdata.groupcompetition.stageimpl.GCSelectionStage;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

import static com.playerdata.groupcompetition.GCConstance.CompetitionStageType;

public class GCStageFactory {

	public static IGCStage createStageByType(int stageType, GroupCompetitionStageCfg cfg) {
		switch (stageType) {
		case CompetitionStageType.COMPETITION_STAGE_TYPE_SELECTION:
			return new GCSelectionStage(cfg);
		case CompetitionStageType.COMPETITION_STAGE_TYPE_EVENTS:
			return new GCEventsStage(cfg);
		case CompetitionStageType.COMPETITION_STAGE_TYPE_REST:
			return new GCRestStage(cfg);
		case CompetitionStageType.COMPETITION_STAGE_TYPE_EMPTY:
			return new GCEmptyStage(cfg);
		}
		return null;
	}
}
