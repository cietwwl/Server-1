package com.rw.service.gm.groupcomp;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.group.GroupBaseManagerHandler;
import com.rwproto.GroupBaseMgrProto.GroupSettingReqMsg;
import com.rwproto.GroupCommonProto.GroupValidateType;

class UpdateGroupSettingTask implements Runnable {

	private String groupName;
	private String playerId;

	public UpdateGroupSettingTask(String groupNameP, String playerIdP) {
		this.groupName = groupNameP;
		this.playerId = playerIdP;
	}

	@Override
	public void run() {
		String groupId = GroupBM.getInstance().getGroupId(groupName);
		if (groupId != null && groupId.length() > 0) {
			Player player = PlayerMgr.getInstance().find(playerId);
			GroupSettingReqMsg.Builder builder = GroupSettingReqMsg.newBuilder();
			builder.setValidateType(GroupValidateType.NON_VALIDATE);
			GroupBaseManagerHandler.getHandler().groupSettingHandler(player, builder.build());
		}
	}
}
