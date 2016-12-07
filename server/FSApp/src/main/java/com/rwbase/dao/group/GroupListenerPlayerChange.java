package com.rwbase.dao.group;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

/*
 * @author HC
 * @date 2016年2月1日 下午2:39:44
 * @Description 帮派监听角色信息修改
 */
public class GroupListenerPlayerChange extends PlayerChangePopertySubscribe {

	public GroupListenerPlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(final Player p) {
		ModifyMemberDataCallback call = new ModifyMemberDataCallback() {

			@Override
			public void call(GroupMemberMgr memberMgr, String userId) {
				memberMgr.updateMemberName(userId, p.getUserName());
			}
		};

		modifyGroupMemberInfo(p, call);
	}

	@Override
	public void playerChangeLevel(final Player p) {
		ModifyMemberDataCallback call = new ModifyMemberDataCallback() {

			@Override
			public void call(GroupMemberMgr memberMgr, String userId) {
				memberMgr.updateMemberLevel(userId, p.getLevel());
			}
		};

		modifyGroupMemberInfo(p, call);
	}

	@Override
	public void playerChangeVipLevel(final Player p) {
		ModifyMemberDataCallback call = new ModifyMemberDataCallback() {

			@Override
			public void call(GroupMemberMgr memberMgr, String userId) {
				memberMgr.updateMemberVipLevel(userId, p.getVip());
			}
		};

		modifyGroupMemberInfo(p, call);
	}

	@Override
	public void playerChangeTemplateId(final Player p) {
		ModifyMemberDataCallback call = new ModifyMemberDataCallback() {

			@Override
			public void call(GroupMemberMgr memberMgr, String userId) {
				memberMgr.updateMemberTemplateId(userId, p.getTemplateId());
			}
		};

		modifyGroupMemberInfo(p, call);
	}

	@Override
	public void playerChangeHeadIcon(final Player p) {
		ModifyMemberDataCallback call = new ModifyMemberDataCallback() {

			@Override
			public void call(GroupMemberMgr memberMgr, String userId) {
				memberMgr.updateMemberHeadIcon(userId, p.getHeadImage());
			}
		};

		modifyGroupMemberInfo(p, call);
	}

	/**
	 * 获取帮派成员信息
	 * 
	 * @param p
	 * @return
	 */
	private void modifyGroupMemberInfo(Player p, ModifyMemberDataCallback call) {
		if (p == null || call == null) {
			return;
		}

		String userId = p.getUserId();
		UserGroupAttributeDataIF baseData = p.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		// 角色如果没有帮派，这里会没有数据,所以要在这里加多个判断 ---------by Alex
		if (baseData == null) {
			return;
		}
		String groupId = baseData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return;
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			return;
		}

		if (group.getGroupBaseDataMgr().getGroupData() == null) {
			return;
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF memberData = memberMgr.getMemberData(userId, false);
		if (memberData == null) {
			return;
		}

		call.call(memberMgr, userId);
	}

	interface ModifyMemberDataCallback {
		public void call(GroupMemberMgr memberMgr, String userId);
	}

	@Override
	public void playerChangeHeadBox(final Player p) {
		// TODO Auto-generated method stub
		ModifyMemberDataCallback call = new ModifyMemberDataCallback() {

			@Override
			public void call(GroupMemberMgr memberMgr, String userId) {
				memberMgr.updateMemberHeadbox(userId, p.getHeadFrame());
			}
		};

		modifyGroupMemberInfo(p, call);
	}
}