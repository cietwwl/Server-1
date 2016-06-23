package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFDefendArmyItemHolder;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupHolder;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，战斗阶段管理类
 * @author aken
 *
 */
public class GFightOngoingMgr {
	
	private static class InstanceHolder{
		private static GFightOngoingMgr instance = new GFightOngoingMgr();
	}
	
	public static GFightOngoingMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightOngoingMgr() { }
	
	public void getEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID) {
		GFDefendArmyItemHolder.getInstance().selectEnimyItem(player, groupID, false);
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	public void changeEnimyDefender(Player player, GroupFightOnlineRspMsg.Builder gfRsp, String groupID){
		GFDefendArmyItemHolder.getInstance().changeEnimyItem(player, groupID);
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
}
