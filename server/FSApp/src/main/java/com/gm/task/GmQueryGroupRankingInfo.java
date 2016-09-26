package com.gm.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.bm.rank.RankType;
import com.bm.rank.group.base.GroupBaseRankExtAttribute;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceData;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.group.helper.GroupRankHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupLevelCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

public class GmQueryGroupRankingInfo implements IGmTask{

	@SuppressWarnings("rawtypes")
	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		Map<String, Object> args = request.getArgs();
		String requestgroupId = GmUtils.parseString(args, "teamId");
		String requestgroupName = GmUtils.parseString(args, "teamName");
		
		if (StringUtils.isEmpty(requestgroupId) && StringUtils.isEmpty(requestgroupName)) {
			queryGroupRank(response);
		} else {
			try{
			if (!StringUtils.isEmpty(requestgroupId)) {
				queryGroupById(requestgroupId, response);
			}
			if (!StringUtils.isEmpty(requestgroupName)) {
				queryGroupByName(requestgroupName, response);
			}
			}catch(Exception ex){
				SocketHelper.processException(ex, response);
			}
		}
		
		return response;
	}

	@SuppressWarnings("rawtypes")
	private void queryGroupRank(GmResponse response) {
		try {
			
			Ranking ranking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);
			EnumerateList enumeration = ranking.getEntriesEnumeration(1, 20);
			while (enumeration.hasMoreElements()) {
				MomentRankingEntry entry = (MomentRankingEntry) enumeration.nextElement();
				GroupBaseRankExtAttribute attr = (GroupBaseRankExtAttribute) entry.getEntry().getExtendedAttribute();

				int rank = entry.getRanking();
				String groupId = attr.getGroupId();
				String groupName = attr.getGroupName();
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("rank", rank);
				map.put("teamId", groupId);
				map.put("teamName", groupName);
				response.addResult(map);
			}
			
			
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
	}
	
	private void queryGroupById(String groupId, GmResponse response) throws Exception{
		Group group = GroupBM.get(groupId);
		if (group == null) {
			throw new Exception(String.valueOf(GmResultStatusCode.STATUS_NOT_FIND_GROUP.getStatus()));
		}
		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();

		String localgroupName = groupData.getGroupName();
		
		int rankIndex = GroupRankHelper.getGroupRankIndex(groupId);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rank", rankIndex);
		map.put("teamId", groupId);
		map.put("teamName", localgroupName);

		response.addResult(map);
	}

	private void queryGroupByName(String groupName, GmResponse response)throws Exception{
		String groupId = GroupBM.getGroupId(groupName);
		
		queryGroupById(groupId, response);
	}
}
