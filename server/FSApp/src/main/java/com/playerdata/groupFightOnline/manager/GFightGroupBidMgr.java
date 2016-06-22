package com.playerdata.groupFightOnline.manager;

import java.util.List;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.playerdata.groupFightOnline.dataForClient.GFGroupSimpleInfo;
import com.playerdata.groupFightOnline.dataForClient.GFResourceInfo;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

/**
 * 在线帮战，用户竞标阶段管理类
 * @author aken
 *
 */
public class GFightGroupBidMgr {
	
	private static class InstanceHolder{
		private static GFightGroupBidMgr instance = new GFightGroupBidMgr();
	}
	
	public static GFightGroupBidMgr getInstance(){
		return InstanceHolder.instance;
	}
	
	private GFightGroupBidMgr() { }
	
	public void getResourceInfo(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		gfRsp.setSystemTime(System.currentTimeMillis());
		List<GFightOnlineResourceCfg> resCfgs = GFightOnlineResourceDAO.getInstance().getAllCfg();
		for(GFightOnlineResourceCfg cfg : resCfgs){
			GFightOnlineResourceData resData = GFightOnlineResourceHolder.getInstance().get(String.valueOf(cfg.getResID()));
			if(resData == null) continue;
			GroupBaseDataIF groupData = GroupBM.get(resData.getOwnerGroupID()).getGroupBaseDataMgr().getGroupData();
			if(groupData == null); //TODO 
			String leaderName = GroupBM.get(resData.getOwnerGroupID()).getGroupMemberMgr().getGroupLeader().getName();
			GFGroupSimpleInfo groupSimple = new GFGroupSimpleInfo();
			groupSimple.setGroupID(groupData.getGroupId());
			groupSimple.setIcon(groupData.getIconId());
			groupSimple.setLevel(groupData.getGroupLevel());
			groupSimple.setName(groupData.getGroupName());
			groupSimple.setLeaderName(leaderName);
			GFResourceInfo resInfo = new GFResourceInfo();
			resInfo.setResourceID(resData.getResourceID());
			resInfo.setGroupInfo(groupSimple);
			gfRsp.addGfResourceInfo(ClientDataSynMgr.toClientData(resInfo));
		}
		gfRsp.setRstType(GFResultType.SUCCESS);
	}
	
	public void groupBidding(Player player, GroupFightOnlineRspMsg.Builder gfRsp){
		
	}
}
