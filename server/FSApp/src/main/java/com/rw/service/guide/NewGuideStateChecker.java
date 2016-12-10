package com.rw.service.guide;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.service.guide.datamodel.GuidanceClosure;
import com.rw.service.guide.datamodel.NewGuideClosure;
import com.rwbase.common.playerext.PlayerTempAttribute;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class NewGuideStateChecker {

	private static NewGuideStateChecker instance = new NewGuideStateChecker();

	public static NewGuideStateChecker getInstance() {
		return instance;
	}

	public void check(Player player, boolean foreceSync) {
		try {
			String userId = player.getUserId();
			int level = player.getLevel();
			// 计算出此时是关还是开
			GuidanceClosure guidanceClosure = GuidanceClosureCfgDAO.getInstance().getOrPreSearch(level);
			boolean closed;
			if (guidanceClosure == null) {
				closed = false;
			} else {
				int progress = guidanceClosure.getProgress();
				if (progress < 0) {
					closed = false;
				} else {
					UserGuideProgress guideProgress = GuideProgressDAO.getInstance().get(userId, true);
					if (guideProgress == null) {
						closed = false;
					} else if (guideProgress.getProgressMap().containsKey(level)) {
						closed = true;
					} else {
						closed = false;
					}
				}
			}
			PlayerTempAttribute tempAttribute = player.getTempAttribute();
			if (!foreceSync) {
				NewGuideClosure lastClosure = player.getTempAttribute().getLastClosure();
				if (lastClosure != null && lastClosure.isClose() == closed) {
					return;
				}
			}
			NewGuideClosure guideClosure = new NewGuideClosure(closed);
			tempAttribute.setLastClosure(guideClosure);
			ClientDataSynMgr.synData(player, guideClosure, eSynType.NewGuideClosure, eSynOpType.UPDATE_SINGLE);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
