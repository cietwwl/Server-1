package com.playerdata.groupcompetition.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bm.group.GroupBM;
import com.playerdata.Player;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.holder.data.GCompMember;
import com.playerdata.groupcompetition.util.GCEventsType;
import com.playerdata.groupcompetition.util.GCompUtil;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.groupcompetition.UserGroupCompetitionDataCreator;
import com.rwbase.dao.groupcompetition.UserGroupCompetitionDataDAO;
import com.rwbase.dao.groupcompetition.pojo.UserGroupCompetitionData;
import com.rwbase.dao.groupcompetition.pojo.UserGroupCompetitionScoreRecord;

public class GCompMemberMgr {

	
	private static GCompMemberMgr _instance = new GCompMemberMgr();
	
	private Map<String, Map<String, GCompMember>> _allMembers = new HashMap<String, Map<String, GCompMember>>();
	private Map<String, List<GCompMember>> _sorted = new HashMap<String, List<GCompMember>>();
	private UserGroupCompetitionDataDAO kvDataDAO = UserGroupCompetitionDataDAO.getInstance();
	private GCEventsType _currentType;
	
	public static GCompMemberMgr getInstance() {
		return _instance;
	}
	
	private void addGroupMember(GCEventsType type, GCompMember gcompMember, GroupBaseDataIF baseData, List<GCompMember> list, Map<String, GCompMember> map) {
		map.put(gcompMember.getUserId(), gcompMember);
		list.add(gcompMember);
		UserGroupCompetitionData kvData = kvDataDAO.get(gcompMember.getUserId());
		if (kvData == null) {
			kvData = UserGroupCompetitionDataCreator.createData(gcompMember.getUserId());
			kvDataDAO.commit(kvData);
		} else {
			kvData.clear();
		}
		kvData.addRecord(this.create(type, baseData));
	}
	
	private void checkAndAddGroupMember(Player player, String groupId) {
		if (groupId != null && groupId.length() > 0) {
			Map<String, GCompMember> map = _allMembers.get(groupId);
			if (map != null) {
				String userId = player.getUserId();
				if (!map.containsKey(userId)) {
					GCompMember member = new GCompMember(userId, player.getUserName(), player.getLevel(), player.getHeadImage());
					List<GCompMember> list = _sorted.get(groupId);
					this.addGroupMember(_currentType, member, GroupBM.get(groupId).getGroupBaseDataMgr().getGroupData(), list, map);
				}
			}
		}
	}
	
	private UserGroupCompetitionScoreRecord create(GCEventsType type, GroupBaseDataIF data) {
		UserGroupCompetitionScoreRecord record = new UserGroupCompetitionScoreRecord();
		record.setGroupId(data.getGroupId());
		record.setGroupName(data.getGroupName());
		record.setScore(0);
		record.setMaxContinueWins(0);
		record.setTotalWinTimes(0);
		record.setEventsType(type);
		return record;
	}
	
	public void notifyEventsStart(GCEventsType type, List<String> groupIds) {
		_currentType = type;
		this._allMembers.clear();
		long startTime = GroupCompetitionMgr.getInstance().getEndTimeOfSelection();
		for (String groupId : groupIds) {
			if (groupId == null || groupId.length() == 0) {
				continue;
			}
			Map<String, GCompMember> map = new ConcurrentHashMap<String, GCompMember>();
			List<GCompMember> list = new ArrayList<GCompMember>();
			_allMembers.put(groupId, map);
			_sorted.put(groupId, list);
			Group group = GroupBM.get(groupId);
			if (group == null) {
				GCompUtil.log("帮派：{}，不存在，可能已经解散！", groupId);
				continue;
			}
			GroupBaseDataIF baseData = group.getGroupBaseDataMgr().getGroupData();
			List<? extends GroupMemberDataIF> allMembers = group.getGroupMemberMgr().getMemberSortList(null);
			for (GroupMemberDataIF member : allMembers) {
				if (member.getReceiveTime() > startTime) {
					GCompUtil.log("成员：{}，加入帮派的时间：{}，比海选结束的时间要晚，不能参与帮战！帮派id：{}", member.getName(), member.getReceiveTime(), groupId);
					continue;
				}
				GCompMember gcompMember = new GCompMember(member.getUserId(), member.getName(), member.getLevel(), member.getHeadId());
				this.addGroupMember(type, gcompMember, baseData, list, map);
			}
			Collections.sort(list);
		}
	}
	
	public void notifyEventsEnd() {
		UserGroupCompetitionScoreRecord record;
		for (Iterator<String> keyItr = _allMembers.keySet().iterator(); keyItr.hasNext();) {
			String key = keyItr.next();
			Map<String, GCompMember> map = _allMembers.get(key);
			for (Iterator<String> memberMapKeyItr = map.keySet().iterator(); memberMapKeyItr.hasNext();) {
				String memberKey = memberMapKeyItr.next();
				record = this.getRecordOfCurrent(memberKey);
				if (record != null && record.getUpdateTimes() > 0) {
					record.setUpdateTimes(0);
					kvDataDAO.update(memberKey);
				}
			}
		}
	}

	public void onPlayerEnterPrepareArea(Player player) {
		String groupId = GroupHelper.getGroupId(player);
		this.checkAndAddGroupMember(player, groupId);
		GCompMember member = this.getGCompMember(groupId, player.getUserId());
		GCompMemberHolder.getInstance().syn(player, member);
	}
	
	public GCompMember getGCompMember(String groupId, String userId) {
		return _allMembers.get(groupId).get(userId);
	}
	
	public void removeGCompMember(String groupId, String userId) {
		Map<String, GCompMember> map = _allMembers.get(groupId);
		if(map != null) {
			GCompMember member = map.remove(userId);
			if(member != null) {
				_sorted.get(groupId).remove(member);
			}
		}
	}
	
	public List<GCompMember> getArrayCopyOfAllMembers(String groupId) {
		List<GCompMember> arrayList = new ArrayList<GCompMember>();
		getCopyOfAllMembers(groupId, arrayList);
		return arrayList;
	}
	
	public UserGroupCompetitionScoreRecord getRecordOfCurrent(String userId) {
		return this.getRecord(userId, _currentType);
	}
	
	public UserGroupCompetitionScoreRecord getRecord(String userId, GCEventsType type) {
		UserGroupCompetitionData kvData = kvDataDAO.get(userId);
		List<UserGroupCompetitionScoreRecord> recordList = kvData.getRecords();
		UserGroupCompetitionScoreRecord record;
		for(int i = 0, size = recordList.size(); i < size; i++) {
			record = recordList.get(i);
			if(record.getEventsType() == type) {
				return record;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * copy所有的帮派成员数据到指定的集合中
	 * 
	 * @param groupId
	 * @param targetList
	 */
	public void getCopyOfAllMembers(String groupId, Collection<GCompMember> targetList) {
		List<GCompMember> members = _sorted.get(groupId);
		targetList.addAll(members);
	}
	
	/**
	 * 
	 * 获取groupMember的大小
	 * 
	 * @param groupId
	 * @return
	 */
	public int getSizeOfGroupMember(String groupId) {
		List<GCompMember> members = _sorted.get(groupId);
		if(members != null) {
			return members.size();
		} else {
			return 0;
		}
	}
}
