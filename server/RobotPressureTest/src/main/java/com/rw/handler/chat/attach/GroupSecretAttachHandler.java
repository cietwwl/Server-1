package com.rw.handler.chat.attach;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rw.Client;
import com.rw.handler.groupsecret.GroupSecretHandler.GroupSecretReceier;
import com.rw.handler.hero.UserHerosDataHolder;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.GroupSecretProto.GroupSecretCommonReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.GroupSecretProto.JoinSecretDefendReqMsg;
import com.rwproto.GroupSecretProto.RequestType;
import com.rwproto.MsgDef.Command;

/**
 * @Author HC
 * @date 2016年8月8日 下午5:38:59
 * @desc
 **/

public class GroupSecretAttachHandler implements IAttachParse {

	@Override
	public boolean attachHandler(Client client, String id, String extraInfo) {
		GroupSecretCommonReqMsg.Builder req = GroupSecretCommonReqMsg.newBuilder();
		req.setReqType(RequestType.JOIN_SECRET_DEFEND);

		JoinSecretDefendReqMsg.Builder msg = JoinSecretDefendReqMsg.newBuilder();
		msg.setId(id);
		msg.setIndex(GroupSecretIndex.LEFT);
		UserHerosDataHolder userHerosDataHolder = client.getUserHerosDataHolder();
		List<String> heroIds = new ArrayList<String>(userHerosDataHolder.getTableUserHero().getHeroIds());
		// List<String> battleHeroList = new ArrayList<String>();
		int mainRoleIndex = 0;
		for (Iterator<String> iterator = heroIds.iterator(); iterator.hasNext();) {
			String heroId = (String) iterator.next();
			BattleHeroPosition.Builder pos = BattleHeroPosition.newBuilder();
			pos.setHeroId(heroId);
			pos.setPos(mainRoleIndex);
			msg.addHeroId(pos);
			mainRoleIndex++;
			if (mainRoleIndex > 1) {
				break;
			}
		}

		req.setJoinReqMsg(msg);
		return client.getMsgHandler().sendMsg(Command.MSG_GROUP_SECRET, req.build().toByteString(), new GroupSecretReceier(Command.MSG_GROUP_SECRET, "帮派秘境", "接受邀请"));
	}
}