package com.rwbase.common.playerext;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.rwbase.common.PlayerTaskListener;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

public class PlayerAttrChecker implements PlayerTaskListener {

	@Override
	public void notifyTaskCompleted(Player player) {
		RankingMgr rankingMgr = RankingMgr.getInstance();
		PlayerTempAttribute tempAttr = player.getTempAttribute();
		boolean levelChanged = tempAttr.checkAndResetLevelChanged();
		boolean expChanged = tempAttr.checkAndResetExpChanged();
		boolean fightingChanged = tempAttr.checkAndResetFightingChanged();
		if (levelChanged || expChanged || fightingChanged) {
			rankingMgr.onLevelOrExpChanged(player);
		}
		if (fightingChanged) {
			rankingMgr.onHeroFightingChanged(player);
			updateUserGroupFight(player);
		}
	}

	private void updateUserGroupFight(Player player){
		UserGroupAttributeDataIF baseData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		if(baseData == null){
			return;
		}
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			return;
		}

		if (group.getGroupBaseDataMgr().getGroupData() == null) {
			return;
		}
		String userId = player.getUserId();
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(userId, false);
		if (memberData == null) {
			return;
		}
		memberMgr.updateMemberFight(userId, player.getHeroMgr().getFightingAll());
	}
}
