package com.rw.service.groupsecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.groupsecret.GroupSecretTeamDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.rwbase.common.enu.eSpecialItemId;
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
import com.rwproto.GroupSecretProto.GetGroupSecretRewardReqMsg;
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
		UserGroupSecretBaseData userGroupSecretData = UserGroupSecretBaseDataMgr.getMgr().get(userId);

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
	public ByteString createGroupSecretHandler(Player player, CreateGroupSecretReqMsg req) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.CREATE_GROUP_SECRET);
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

		UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = baseDataMgr.get(userId);
		List<String> defendSecretIdList = userGroupSecretBaseData.getDefendSecretIdList();// 当前的秘境列表
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

		GroupSecretTeamDataMgr teamMgr = GroupSecretTeamDataMgr.getMgr();
		GroupSecretTeamData teamData = teamMgr.get(userId);
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
				GroupSecretHelper.fillRspInfo(rsp, false, "英雄状态错误");
				return rsp.build().toByteString();
			} else {
				canAddDefendList.add(teamUserId);
			}
		}

		if (!containsMainRole) {
			GroupSecretHelper.fillRspInfo(rsp, false, "主角必须是防守阵容的一员");
			return rsp.build().toByteString();
		}

		if (canAddDefendList.size() < 2) {
			GroupSecretHelper.fillRspInfo(rsp, false, "主角无法单独驻守秘境");
			return rsp.build().toByteString();
		}

		// 更新参与防守的阵容信息
		teamData.addDefendHeroIdList(canAddDefendList);
		teamMgr.update(userId);

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

		// 添加创建秘境的数据
		userCreateGroupSecretData.addGroupSecretData(secretData);
		mgr.updateData(userId);

		// 更新目前防守的秘境列表
		userGroupSecretBaseData.addDefendSecretId(GroupSecretHelper.generateCacheSecretId(userId, secretData.getId()));
		baseDataMgr.update(userId);

		CreateGroupSecretRspMsg.Builder createRsp = CreateGroupSecretRspMsg.newBuilder();
		createRsp.setGroupSecretInfo(GroupSecretHelper.parseGroupSecretData2Msg(secretData, userId));

		rsp.setIsSuccess(true);
		rsp.setCreateRspMsg(createRsp);
		return rsp.build().toByteString();
	}

	/**
	 * 获取秘境的奖励
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString getGroupSecretRewardHandler(Player player, GetGroupSecretRewardReqMsg req) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.GET_GROUP_SECRET_REWARD);
		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前暂无帮派，不能进入秘境");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您不是帮派成员");
			return rsp.build().toByteString();
		}

		String getRewardSecretId = req.getId();
		UserGroupSecretBaseDataMgr userSecretDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData mySecretBaseData = userSecretDataMgr.get(userId);
		if (!mySecretBaseData.hasDefendSecretId(getRewardSecretId)) {
			GameLog.error("请求领取秘境奖励", userId, String.format("找不到角色驻守的秘境[%s]对应存储数据", getRewardSecretId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前并未驻守该秘境");
			return rsp.build().toByteString();
		}

		String[] idArr = GroupSecretHelper.parseString2UserIdAndSecretId(getRewardSecretId);

		String secretUserId = idArr[0];
		int id = Integer.parseInt(idArr[1]);

		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(secretUserId);
		if (userCreateGroupSecretData == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("找不到角色[%s]对应的秘境[%s]存储数据", secretUserId, id));
			GroupSecretHelper.fillRspInfo(rsp, false, "暂无对应的秘境数据");
			return rsp.build().toByteString();
		}

		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("找不到角色[%s]对应的[%s]秘境存储数据", secretUserId, id));
			GroupSecretHelper.fillRspInfo(rsp, false, "暂无对应的秘境数据");
			return rsp.build().toByteString();
		}

		long now = System.currentTimeMillis();
		int secretCfgId = groupSecretData.getSecretId();// 秘境的模版Id
		GroupSecretResourceTemplate groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("找不到角色[%s]秘境[%s]对应的配置表GroupSecretResourceTemplate", secretUserId, secretCfgId));
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境对应的类型配置表");
			return rsp.build().toByteString();
		}

		DefendUserInfoData myDefendInfo = null;
		Map<Integer, DefendUserInfoData> defendMap = groupSecretData.getDefendMap();
		for (Entry<Integer, DefendUserInfoData> e : defendMap.entrySet()) {
			DefendUserInfoData value = e.getValue();
			if (value == null) {
				continue;
			}

			if (value.getUserId().equals(userId)) {
				myDefendInfo = value;
				break;
			}
		}

		if (myDefendInfo == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("找不到角色驻守的秘境[%s]中自己的驻守阵容信息", getRewardSecretId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前并未驻守该秘境");
			return rsp.build().toByteString();
		}

		long needTimeMillis = TimeUnit.MINUTES.toMillis(groupSecretResTmp.getNeedTime());// 分钟
		long createTime = groupSecretData.getCreateTime();
		long passTimeMillis = now - createTime;
		boolean isFinish = passTimeMillis >= needTimeMillis;// 是否已经完成了
		if (!isFinish) {
			GameLog.error("请求领取秘境奖励", userId, String.format("角色[%s]秘境的Id[%s]还没有完成就来请求领奖", secretUserId, id));
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境还未完成不能领奖");
			return rsp.build().toByteString();
		}

		long changeTeamTime = myDefendInfo.getChangeTeamTime();// 修改阵容时间
		int proRes = myDefendInfo.getProRes() - myDefendInfo.getRobRes();
		int proGE = myDefendInfo.getProGE() - myDefendInfo.getRobGE();
		int proGS = myDefendInfo.getProGS() - myDefendInfo.getRobGS();
		if (changeTeamTime > 0) {
			long minutes = TimeUnit.MILLISECONDS.toMinutes((isFinish ? (createTime + needTimeMillis) : now) - changeTeamTime);
			int fighting = myDefendInfo.getFighting();
			proRes += (int) (fighting * groupSecretResTmp.getProductRatio() * minutes);
			proGE += (int) (groupSecretResTmp.getGroupExpRatio() * minutes);
			proGS += (int) (groupSecretResTmp.getGroupSupplyRatio() * minutes);
		}

		// 增加帮派经验物资
		group.getGroupBaseDataMgr().updateGroupDonate(player, null, proGS, proGE);
		// 增加资源
		if (proRes > 0) {
			player.getItemBagMgr().addItem(groupSecretResTmp.getReward(), proRes);
		}

		// 钻石
		if (groupSecretResTmp.getRobGold() > 0) {
			player.getItemBagMgr().addItem(eSpecialItemId.Gold.getValue(), groupSecretResTmp.getRobGold());
		}

		// 把自己的驻守信息移除
		mySecretBaseData.removeDefendSecretId(getRewardSecretId);
		userSecretDataMgr.update(userId);

		// 删除秘境中自己的防守信息
		groupSecretData.removeDefendUserInfoData(myDefendInfo.getIndex());
		if (groupSecretData.getDefendMap().isEmpty()) {
			userCreateGroupSecretData.deleteGroupSecretDataById(id);
		}
		mgr.updateData(userId);

		// 清除自己防守阵容中使用到的
		List<String> heroList = myDefendInfo.getHeroList();
		GroupSecretTeamDataMgr teamMgr = GroupSecretTeamDataMgr.getMgr();
		GroupSecretTeamData groupSecretTeamData = teamMgr.get(userId);
		groupSecretTeamData.removeDefendHeroIdList(heroList, userId);
		teamMgr.update(userId);

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}
}