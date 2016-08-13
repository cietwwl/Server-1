package com.gm.task;

import java.util.HashMap;

import com.bm.rank.RankType;
import com.bm.rank.group.base.GroupBaseRankExtAttribute;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.SocketHelper;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingFactory;

public class GmQueryGroupRankingInfo implements IGmTask{

	@SuppressWarnings("rawtypes")
	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
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
		return response;
	}
	
}
