package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwproto.GroupCommonProto.GroupPost;

public class GmViewGroupMember implements IGmTask {

	public enum GroupPostInfo {
		GLEADER(GroupPost.LEADER, "帮主"), GASSISTANT_LEADER(GroupPost.ASSISTANT_LEADER, "副帮主"), GOFFICEHOLDER(GroupPost.OFFICEHOLDER, "官员"), GMEMBER(GroupPost.MEMBER, "成员"), ;
		private GroupPost post;
		private String desc;

		private GroupPostInfo(GroupPost post, String desc) {
			this.post = post;
			this.desc = desc;
		}

		public GroupPost getPost() {
			return post;
		}

		public String getDesc() {
			return desc;
		}

		private static GroupPostInfo[] allValues;

		public static String getPostDesc(int post) {
			if (allValues == null) {
				allValues = GroupPostInfo.values();
			}
			for (GroupPostInfo groupPostInfo : allValues) {
				if (groupPostInfo.getPost().getNumber() == post) {
					return groupPostInfo.getDesc();
				}
			}
			return GMEMBER.getDesc();
		}
	}

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try {
			Map<String, Object> args = request.getArgs();
			String groupId = GmUtils.parseString(args, "teamId");
			Group group = GroupBM.get(groupId);
			if (group == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_NOT_FIND_GROUP.getStatus()));
			}
			GroupMemberMgr groupMemberMgr = group.getGroupMemberMgr();
			PlayerMgr instance = PlayerMgr.getInstance();

			List<? extends GroupMemberDataIF> memberSortList = groupMemberMgr.getMemberSortList(null);
			for (GroupMemberDataIF groupMemberDataIF : memberSortList) {
				String roleId = groupMemberDataIF.getUserId();

				Player p = instance.find(roleId);
				UserGroupAttributeData baseData = p.getUserGroupAttributeDataMgr().getUserGroupAttributeData();

				String roleName = groupMemberDataIF.getName();
				int lv = groupMemberDataIF.getLevel();
				int fight = groupMemberDataIF.getFighting();
				int contribution = baseData == null ? 0 : baseData.getContribution();
				long lastDonateTime = (baseData == null ? 0 : baseData.getLastDonateTime()) / 1000;
				String postDesc = GroupPostInfo.getPostDesc(groupMemberDataIF.getPost());

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("roleId", roleId);
				map.put("roleName", roleName);
				map.put("lev", lv);
				map.put("fight", fight);
				map.put("jobName", postDesc);
				map.put("contribute", contribution);
				map.put("lastLoginTime", lastDonateTime);

				response.addResult(map);
			}

		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
