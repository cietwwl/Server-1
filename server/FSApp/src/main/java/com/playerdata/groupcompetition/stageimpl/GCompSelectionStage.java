package com.playerdata.groupcompetition.stageimpl;

import java.util.List;

import com.bm.group.GroupBM;
import com.bm.rank.groupCompetition.groupRank.groupRankStatic.GroupStaticRankMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCompStage;
import com.playerdata.groupcompetition.holder.GCompHistoryDataMgr;
import com.playerdata.groupcompetition.util.GCompCommonConfig;
import com.playerdata.groupcompetition.util.GCompRestStartPara;
import com.playerdata.groupcompetition.util.GCompStageType;
import com.playerdata.groupcompetition.util.GCompTips;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;

/**
 * 
 * 帮派争霸海选阶段
 * 
 * @author CHEN.P
 *
 */
public class GCompSelectionStage implements IGCompStage {

	private long _stageEndTime; // 阶段结束的时间
	private String _stageCfgId; // 阶段的配置id

	public GCompSelectionStage(GroupCompetitionStageCfg cfg) {
		_stageCfgId = cfg.getCfgId();
	}

	@Override
	public String getStageCfgId() {
		return _stageCfgId;
	}

	@Override
	public GCompStageType getStageType() {
		return GCompStageType.SELECTION;
	}

	@Override
	public void onStageStart(IGCompStage preStage, Object startPara) {
		if (startPara != null && startPara instanceof GCompRestStartPara) {
			this._stageEndTime = ((GCompRestStartPara) startPara).getEndTime();
		} else {
			this._stageEndTime = GCompUtil.calculateEndTimeOfStage(_stageCfgId);
		}
		GCompUtil.sendMarquee(GCompTips.getTipsEnterSelectionStage());
	}

	@Override
	public void onStageEnd() {
		List<String> selectedGroupIds = GCompUtil.getTopCountGroupsFromRank();
		GCompHistoryDataMgr.getInstance().setSelectedGroupIds(selectedGroupIds);
		GroupCompetitionMgr.getInstance().updateEndTimeOfSelection(System.currentTimeMillis());
		GroupStaticRankMgr.getInstance().saveStaticRankData();
		this.emailNotifyAllAgainstMembers(selectedGroupIds);
	}

	@Override
	public long getStageEndTime() {
		return _stageEndTime;
	}

	/**
	 * 海选结束的时候，邮件通知所有入选帮派的成员玩家
	 * 
	 * @param groupIds
	 */
	private void emailNotifyAllAgainstMembers(List<String> groupIds) {
		for (String groupId : groupIds) {
			Group gp = GroupBM.getInstance().get(groupId);
			if (null != gp) {
				List<? extends GroupMemberDataIF> memList = gp.getGroupMemberMgr().getMemberSortList(null);
				for (GroupMemberDataIF member : memList) {
					EmailUtils.sendEmail(member.getUserId(), GCompCommonConfig.getNotifyStartEmailId());
				}
			}
		}
	}
}
