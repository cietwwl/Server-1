package com.playerdata.groupcompetition;

import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.stageimpl.GCompEmptyStage;
import com.playerdata.groupcompetition.stageimpl.GCompEventsStage;
import com.playerdata.groupcompetition.stageimpl.GCompRestStage;
import com.playerdata.groupcompetition.stageimpl.GCompSelectionStage;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

public class GCompStageFactory {

	public static IGCompStage createStageByType(int stageType, GroupCompetitionStageCfg cfg) {
		GCompStageType eStageType = GCompStageType.getBySign(stageType);
		switch (eStageType) {
		case SELECTION:
			return new GCompSelectionStage(cfg);
		case EVENTS:
			return new GCompEventsStage(cfg);
		case REST:
			return new GCompRestStage(cfg);
		case EMPTY:
			return new GCompEmptyStage(cfg);
		}
		return null;
	}
}
