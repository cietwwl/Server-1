package com.rw.service.groupsecret;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.groupsecret.GroupSecretTeamDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretDataMgr;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.groupsecret.GroupSecretHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretDiamondDropCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwproto.GroupSecretProto.CreateGroupSecretReqMsg;
import com.rwproto.GroupSecretProto.CreateGroupSecretRspMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.GroupSecretProto.GroupSecretInfo;
import com.rwproto.GroupSecretProto.MatchSecretInfo;
import com.rwproto.GroupSecretProto.OpenGroupSecretMainViewRspMsg;
import com.rwproto.GroupSecretProto.RequestType;

/*
 * @author HC
 * @date 2016年5月26日 下午9:49:14
 * @Description 
 */
public class GroupSecretHandler {
	private static GroupSecretHandler handler = new GroupSecretHandler();

	/**
	 * 获取Handler实例
	 * 
	 * @return
	 */
	public static GroupSecretHandler getHandler() {
		return handler;
	}

	/**
	 * 打开秘境主界面
	 * 
	 * @param player
	 * @return
	 */
	public ByteString openGroupSecretMainViewHandler(Player player) {
		String userId = player.getUserId();

		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.OPEN_MAIN_VIEW);

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前暂无帮派，不能进入秘境");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("打开秘境界面", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("打开秘境界面", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("打开秘境界面", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您不是帮派成员");
			return rsp.build().toByteString();
		}

		// 个人的秘境数据
		UserGroupSecretBaseData userGroupSecretData = UserGroupSecretDataMgr.getMgr().get(userId);

		OpenGroupSecretMainViewRspMsg.Builder openRsp = OpenGroupSecretMainViewRspMsg.newBuilder();
		// 检查密境列表
		List<String> defendSecretIdList = userGroupSecretData.getDefendSecretIdList();
		for (int i = 0, size = defendSecretIdList.size(); i < size; i++) {
			String[] idArr = GroupSecretHelper.parseString2UserIdAndSecretId(defendSecretIdList.get(i));
			UserCreateGroupSecretData userCreateGroupSecretData = UserCreateGroupSecretDataMgr.getMgr().get(idArr[0]);
			if (userCreateGroupSecretData == null) {
				continue;
			}

			GroupSecretData data = userCreateGroupSecretData.getGroupSecretData(Integer.parseInt(idArr[1]));
			if (data == null) {
				continue;
			}

			GroupSecretInfo.Builder info = GroupSecretHelper.parseGroupSecretData2Msg(data, userId);
			if (info == null) {
				continue;
			}

			openRsp.addGroupSecretInfo(info);
		}

		// 检查匹配到的人
		MatchSecretInfo.Builder matchSecretInfo = GroupSecretHelper.fillMatchSecretInfo(userId);
		if (matchSecretInfo != null) {
			openRsp.setMatchSecretInfo(matchSecretInfo);
		}

		rsp.setIsSuccess(true);
		rsp.setOpenMainView(openRsp);
		return rsp.build().toByteString();
	}

	/**
	 * 创建秘境
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString createGroupSecretHandler(Player player, CreateGroupSecretReqMsg.Builder req) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前暂无帮派，不能进入秘境");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("请求创建秘境", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("请求创建秘境", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("请求创建秘境", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您不是帮派成员");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseData userGroupSecretData = UserGroupSecretDataMgr.getMgr().get(userId);
		List<String> defendSecretIdList = userGroupSecretData.getDefendSecretIdList();// 当前的秘境列表
		// TODO HC 这里可能要从特权加，检查秘境创建的数量是不是超出了上限

		int secretCfgId = req.getSecretCfgId();// 要创建的秘境的配置Id
		GroupSecretResourceTemplate groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "请求创建秘境的类型不存在");
			return rsp.build().toByteString();
		}

		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(userId);
		if (userCreateGroupSecretData == null) {
			GameLog.error("请求创建秘境", userId, "找不到角色对应的秘境存储数据");
			GroupSecretHelper.fillRspInfo(rsp, false, "暂无对应的秘境数据");
			return rsp.build().toByteString();
		}

		List<String> teamHeroIdList = req.getTeamHeroIdList();
		if (teamHeroIdList == null || teamHeroIdList.isEmpty()) {
			GameLog.error("请求创建秘境", userId, "从客户端传递过来的防守阵容信息是空的");
			GroupSecretHelper.fillRspInfo(rsp, false, "防守阵容不能为空");
			return rsp.build().toByteString();
		}

		GroupSecretTeamData teamData = GroupSecretTeamDataMgr.getMgr().get(userId);
		List<String> defendHeroList = teamData.getDefendHeroList();

		int size = teamHeroIdList.size();
		List<String> canAddDefendList = new ArrayList<String>(size);

		int totalFighting = 0;

		boolean containsMainRole = false;
		for (int i = 0; i < size; i++) {

			String teamUserId = teamHeroIdList.get(i);
			Hero hero = player.getHeroMgr().getHeroById(teamUserId);
			if (hero == null) {
				GameLog.error("请求创建秘境", userId, String.format("Id为[%s]的英雄在服务器查找不到对应的Hero对象", teamUserId));
				GroupSecretHelper.fillRspInfo(rsp, false, "英雄不存在");
				return rsp.build().toByteString();
			}

			totalFighting += hero.getFighting();

			if (userId.equals(teamUserId)) {
				containsMainRole = true;
				canAddDefendList.add(teamUserId);
				continue;
			}

			if (defendHeroList.contains(teamUserId)) {
				GameLog.error("请求创建秘境", userId, String.format("Id为[%s]的英雄已经被其他驻守队伍使用", teamUserId));
				GroupSecretHelper.fillRspInfo(rsp, false, "防守阵容不能为空");
				return rsp.build().toByteString();
			} else {
				canAddDefendList.add(teamUserId);
			}
		}

		if (!containsMainRole) {
			GroupSecretHelper.fillRspInfo(rsp, false, "主角必须是防守阵容的一员");
			return rsp.build().toByteString();
		}

		// 防守的信息
		long now = System.currentTimeMillis();
		DefendUserInfoData userInfoData = new DefendUserInfoData();
		userInfoData.setDefTime(now);
		userInfoData.setHeroList(canAddDefendList);
		userInfoData.setIndex(GroupSecretIndex.MAIN_VALUE);
		userInfoData.setUserId(userId);
		userInfoData.setDropDiamond(GroupSecretDiamondDropCfgDAO.getCfgDAO().getDiamondDropNum(groupSecretResTmp.getDiamondDropId()));
		userInfoData.setFighting(totalFighting);

		GroupSecretData secretData = new GroupSecretData();
		secretData.setCreateTime(now);
		secretData.setGroupId(groupId);
		secretData.setSecretId(secretCfgId);
		secretData.setUserId(userId);
		secretData.addDefendUserInfoData(GroupSecretIndex.MAIN_VALUE, userInfoData);

		userCreateGroupSecretData.addGroupSecretData(secretData);
		mgr.updateData(userId);

		CreateGroupSecretRspMsg.Builder createRsp = CreateGroupSecretRspMsg.newBuilder();
		createRsp.setGroupSecretInfo(GroupSecretHelper.parseGroupSecretData2Msg(secretData, userId));

		rsp.setIsSuccess(true);
		rsp.setCreateRspMsg(createRsp);
		return rsp.build().toByteString();
	}
}