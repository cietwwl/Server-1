package com.playerdata.groupFightOnline.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.dataForClient.GFFightRecord;
import com.playerdata.groupFightOnline.dataForClient.GFResourceInfo;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class GFightOnlineResourceHolder {

	private static GFightOnlineResourceHolder instance = new GFightOnlineResourceHolder();

	public static GFightOnlineResourceHolder getInstance() {
		return instance;
	}

	final private eSynType synType = eSynType.GFightOnlineResourceData;

	public GFightOnlineResourceData get(int resourceID) {
		return GFightOnlineResourceDAO.getInstance().get(String.valueOf(resourceID));
	}

	public void update(GFightOnlineResourceData data) {
		GFightOnlineResourceDAO.getInstance().update(data);
	}

	public void add(GFightOnlineResourceData data) {
		GFightOnlineResourceDAO.getInstance().update(data);
	}

	public void synData(Player player) {
		List<GFResourceInfo> gfResourceData = new ArrayList<GFResourceInfo>();
		List<GFightOnlineResourceCfg> resCfgs = GFightOnlineResourceCfgDAO.getInstance().getAllCfg();
		for (GFightOnlineResourceCfg cfg : resCfgs) {
			GFightOnlineResourceData data = get(cfg.getResID());
			if (data == null)
				continue;
			GFResourceInfo resInfo = toClientResourceData(player.getUserId(), data);
			if (resInfo != null)
				gfResourceData.add(resInfo);
		}
		if (gfResourceData.size() > 0) {
			ClientDataSynMgr.synDataList(player, gfResourceData, synType, eSynOpType.UPDATE_LIST);
		}
	}

	/**
	 * 将服务端存储的资源点信息，转成前端可用的 实际只是将帮派id转成了帮派基本信息
	 * 
	 * @param userID
	 * @param resData
	 * @return
	 */
	private GFResourceInfo toClientResourceData(String userID, GFightOnlineResourceData resData) {
		GFResourceInfo resInfo = new GFResourceInfo();
		resInfo.setResourceID(resData.getResourceID());
		resInfo.setState(resData.getState());
		resInfo.setOwnerBidAble(resData.isOwnerBidAble());
		if (StringUtils.isBlank(resData.getOwnerGroupID()))
			return resInfo;
		Group gp = GroupBM.getInstance().get(resData.getOwnerGroupID());
		if (gp == null)
			return resInfo;
		GroupBaseDataIF groupData = gp.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error(LogModule.GroupFightOnline.getName(), userID, String.format("getResourceInfo, 占领资源点[%s]的帮派[%s]信息不存在", resData.getResourceID(), resData.getOwnerGroupID()));
		}
		String leaderName = GroupBM.getInstance().get(resData.getOwnerGroupID()).getGroupMemberMgr().getGroupLeader().getName();

		GFGroupBiddingItem groupSimple = new GFGroupBiddingItem();
		groupSimple.setGroupID(groupData.getGroupId());
		groupSimple.setIconID(groupData.getIconId());
		groupSimple.setGroupName(groupData.getGroupName());
		groupSimple.setLeaderName(leaderName);
		resInfo.setGroupInfo(groupSimple);

		return resInfo;
	}

	public void addFightRecord(int resourceID, GFFightRecord record) {
		GFightOnlineResourceData resData = get(resourceID);
		if (resData != null)
			resData.addFightRecord(record);
	}

	public List<GFFightRecord> getFightRecord(int resourceID) {
		GFightOnlineResourceData resData = get(resourceID);
		if (resData != null)
			return resData.getFightRecord();
		return null;
	}
}
