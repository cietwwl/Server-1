package com.playerdata.groupcompetition;

import com.playerdata.groupcompetition.data.IGCStage;
import com.playerdata.groupcompetition.stageimpl.GCompEmptyStage;
import com.playerdata.groupcompetition.stageimpl.GCompEventsStage;
import com.playerdata.groupcompetition.stageimpl.GCRestStage;
import com.playerdata.groupcompetition.stageimpl.GCSelectionStage;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

public class GCompStageFactory {

	public static IGCStage createStageByType(int stageType, GroupCompetitionStageCfg cfg) {
		GCompStageType eStageType = GCompStageType.getBySign(stageType);
		switch (eStageType) {
		case SELECTION:
			return new GCSelectionStage(cfg);
		case EVENTS:
			return new GCompEventsStage(cfg);
		case REST:
			return new GCRestStage(cfg);
		case EMPTY:
			return new GCompEmptyStage(cfg);
		}
		return null;
	}
}
