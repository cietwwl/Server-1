package com.rw.service.guide;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
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
			int[] progressArray = GuidanceClosureCfgDAO.getInstance().getOrPreSearch(level);
			boolean closed = true;
			if (progressArray == null) {
				closed = false;
			} else {
				int len = progressArray.length;
				if (len == 0 || (len == 1 && progressArray[0] < 0)) {
					closed = false;
				} else {
					UserGuideProgress guideProgress = GuideProgressDAO.getInstance().get(userId, true);
					if (guideProgress == null) {
						closed = false;
					} else {
						for (int i = 0; i < len; i++) {
							Integer current = guideProgress.getProgressMap().get(progressArray[i]);
							if (current == null || current.intValue() != -1) {
								closed = false;
								break;
							}
						}

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
