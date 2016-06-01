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
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper;
import com.rwbase.dao.groupsecret.pojo.SecretBaseInfoSynDataHolder;
import com.rwbase.dao.groupsecret.pojo.SecretTeamInfoSynDataHolder;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretDiamondDropCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.GroupSecretDataSynData;
import com.rwproto.GroupSecretProto.ChangeDefendTeamReqMsg;
import com.rwproto.GroupSecretProto.CreateGroupSecretReqMsg;
import com.rwproto.GroupSecretProto.GetGroupSecretRewardReqMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.GroupSecretProto.RequestType;
import com.rwproto.PrivilegeProtos.GroupPrivilegeNames;

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

		// 同步秘境基础数据
		List<SecretBaseInfoSynData> baseInfoList = new ArrayList<SecretBaseInfoSynData>();
		// 同步秘境的防守信息
		List<SecretTeamInfoSynData> teamInfoList = new ArrayList<SecretTeamInfoSynData>();

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

			GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(data, userId);
			if (synData == null) {
				continue;
			}

			SecretBaseInfoSynData base = synData.getBase();
			SecretTeamInfoSynData team = synData.getTeam();
			if (base != null) {
				baseInfoList.add(base);
			}

			if (team != null) {
				teamInfoList.add(team);
			}
		}

		// 检查匹配到的人
		GroupSecretDataSynData matchSecretInfo = GroupSecretHelper.fillMatchSecretInfo(player);
		if (matchSecretInfo != null) {
			SecretBaseInfoSynData base = matchSecretInfo.getBase();
			SecretTeamInfoSynData team = matchSecretInfo.getTeam();
			if (base != null) {
				baseInfoList.add(base);
			}

			if (team != null) {
				teamInfoList.add(team);
			}
		}

		SecretBaseInfoSynDataHolder.getHolder().synAllData(player, baseInfoList);
		SecretTeamInfoSynDataHolder.getHolder().synAllData(player, teamInfoList);

		rsp.setIsSuccess(true);
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
		int intPrivilege = player.getPrivilegeMgr().getIntPrivilege(GroupPrivilegeNames.mysteryChallengeCount);
		if (defendSecretIdList.size() >= intPrivilege) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("您当前只能创建%s个秘境", intPrivilege));
			return rsp.build().toByteString();
		}

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

			if (canAddDefendList.contains(teamUserId) || defendHeroList.contains(teamUserId)) {
				GameLog.error("请求创建秘境", userId, String.format("Id为[%s]的英雄已经被其他驻守队伍使用,或者客户端一个英雄ID多用", teamUserId));
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

		if (canAddDefendList.size() < 2) {
			GroupSecretHelper.fillRspInfo(rsp, false, "驻守阵容不能超过5个人");
			return rsp.build().toByteString();
		}

		// 更新参与防守的阵容信息
		teamMgr.addDefendHeroIdList(player, canAddDefendList);

		// 防守的信息
		long now = System.currentTimeMillis();
		DefendUserInfoData userInfoData = new DefendUserInfoData();
		userInfoData.setDefTime(now);
		userInfoData.setChangeTeamTime(now);
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
		mgr.addGroupSecretData(userId, secretData);

		// 更新目前防守的秘境列表
		baseDataMgr.addDefendSecretId(userId, GroupSecretHelper.generateCacheSecretId(userId, secretData.getId()));

		GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(secretData, userId);
		SecretBaseInfoSynData base = synData.getBase();
		if (base != null) {
			SecretBaseInfoSynDataHolder.getHolder().addData(player, base);
		}

		SecretTeamInfoSynData team = synData.getTeam();
		if (team != null) {
			SecretTeamInfoSynDataHolder.getHolder().addData(player, team);
		}

		// 把秘境数据加入到排行榜
		GroupSecretMatchHelper.addGroupSecret2Rank(player, secretData);

		rsp.setIsSuccess(true);
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
		userSecretDataMgr.removeDefendSecretId(userId, getRewardSecretId);

		// 删除秘境中自己的防守信息
		mgr.removeDefendInfoData(secretUserId, myDefendInfo.getIndex(), id);

		// 清除自己防守阵容中使用到的
		GroupSecretTeamDataMgr.getMgr().removeTeamHeroList(player, myDefendInfo.getHeroList());

		// 通知客户端删除
		SecretBaseInfoSynDataHolder.getHolder().removeData(player, new SecretBaseInfoSynData(getRewardSecretId, 0, true, 0, 0, 0, 0, 0, 0));
		SecretTeamInfoSynDataHolder.getHolder().removeData(player, new SecretTeamInfoSynData(getRewardSecretId, null));

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}

	/**
	 * 更换角色的防守阵容
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString changeDefendTeamHandler(Player player, ChangeDefendTeamReqMsg req) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.CHANGE_DEFEND_TEAM);
		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前暂无帮派，不能进入秘境");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您不是帮派成员");
			return rsp.build().toByteString();
		}

		String changeTeamSecretId = req.getId();
		UserGroupSecretBaseDataMgr userSecretDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData mySecretBaseData = userSecretDataMgr.get(userId);
		if (!mySecretBaseData.hasDefendSecretId(changeTeamSecretId)) {
			GameLog.error("请求更换秘境阵容", userId, String.format("找不到角色驻守的秘境[%s]对应存储数据", changeTeamSecretId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前并未驻守该秘境");
			return rsp.build().toByteString();
		}

		String[] idArr = GroupSecretHelper.parseString2UserIdAndSecretId(changeTeamSecretId);

		String secretUserId = idArr[0];
		int id = Integer.parseInt(idArr[1]);

		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(secretUserId);
		if (userCreateGroupSecretData == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("找不到角色[%s]对应的秘境[%s]存储数据", secretUserId, id));
			GroupSecretHelper.fillRspInfo(rsp, false, "暂无对应的秘境数据");
			return rsp.build().toByteString();
		}

		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("找不到角色[%s]对应的[%s]秘境存储数据", secretUserId, id));
			GroupSecretHelper.fillRspInfo(rsp, false, "暂无对应的秘境数据");
			return rsp.build().toByteString();
		}

		long now = System.currentTimeMillis();
		int secretCfgId = groupSecretData.getSecretId();// 秘境的模版Id
		GroupSecretResourceTemplate groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("找不到角色[%s]秘境[%s]对应的配置表GroupSecretResourceTemplate", secretUserId, secretCfgId));
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
			GameLog.error("请求更换秘境阵容", userId, String.format("找不到角色驻守的秘境[%s]中自己的驻守阵容信息", changeTeamSecretId));
			GroupSecretHelper.fillRspInfo(rsp, false, "您当前并未驻守该秘境");
			return rsp.build().toByteString();
		}

		long needTimeMillis = TimeUnit.MINUTES.toMillis(groupSecretResTmp.getNeedTime());// 分钟
		long createTime = groupSecretData.getCreateTime();
		long passTimeMillis = now - createTime;
		boolean isFinish = passTimeMillis >= needTimeMillis;// 是否已经完成了
		if (isFinish) {
			GameLog.error("请求更换秘境阵容", userId, String.format("角色[%s]秘境的Id[%s]这个秘境已经完成了", secretUserId, id));
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境已完成不能改变阵容");
			return rsp.build().toByteString();
		}

		List<String> teamHeroIdList = req.getTeamHeroIdList();
		if (teamHeroIdList.isEmpty()) {
			GameLog.error("请求更换秘境阵容", userId, "从客户端传递过来的防守阵容信息是空的");
			GroupSecretHelper.fillRspInfo(rsp, false, "更换的防守阵容不能为空");
			return rsp.build().toByteString();
		}

		GroupSecretTeamDataMgr teamMgr = GroupSecretTeamDataMgr.getMgr();
		GroupSecretTeamData groupSecretTeamData = teamMgr.get(userId);
		List<String> defendHeroList = groupSecretTeamData.getDefendHeroList();// 自己的防守阵容

		List<String> hasDefendHeroIdList = myDefendInfo.getHeroList();// 已经在该秘境驻守的英雄Id列表

		int size = teamHeroIdList.size();

		int totalFighting = 0;

		List<String> checkList = new ArrayList<String>(size);

		boolean containsMainRole = false;
		for (int i = 0; i < size; i++) {
			String teamUserId = teamHeroIdList.get(i);
			Hero hero = player.getHeroMgr().getHeroById(teamUserId);
			if (hero == null) {
				GameLog.error("请求更换秘境阵容", userId, String.format("Id为[%s]的英雄在服务器查找不到对应的Hero对象", teamUserId));
				GroupSecretHelper.fillRspInfo(rsp, false, "英雄不存在");
				return rsp.build().toByteString();
			}

			totalFighting += hero.getFighting();

			if (userId.equals(teamUserId)) {
				containsMainRole = true;
				continue;
			}

			if (hasDefendHeroIdList.contains(teamUserId)) {// 当前驻守的已经包含了，不检查
				continue;
			}

			if (checkList.contains(teamUserId) || defendHeroList.contains(teamUserId)) {// 检查
				GameLog.error("请求更换秘境阵容", userId, String.format("Id为[%s]的英雄已经被其他秘境占用,或者客户端发送的英雄Id重复使用", teamUserId));
				GroupSecretHelper.fillRspInfo(rsp, false, "英雄状态错误");
				return rsp.build().toByteString();
			}

			checkList.add(teamUserId);
		}

		if (!containsMainRole) {
			GroupSecretHelper.fillRspInfo(rsp, false, "主角必须是防守阵容的一员");
			return rsp.build().toByteString();
		}

		if (size < 2) {
			GroupSecretHelper.fillRspInfo(rsp, false, "主角无法单独驻守秘境");
			return rsp.build().toByteString();
		}

		if (size > 5) {
			GroupSecretHelper.fillRspInfo(rsp, false, "驻守阵容不能超过5个人");
			return rsp.build().toByteString();
		}

		// 计算资源产出
		int proRes = myDefendInfo.getProRes();
		int proGS = myDefendInfo.getProGS();
		int proGE = myDefendInfo.getProGE();
		// 上次更换阵容时间
		long proTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(now - myDefendInfo.getChangeTeamTime());

		proRes += (int) (myDefendInfo.getFighting() * groupSecretResTmp.getProductRatio() * proTimeMinutes);
		proGE += (int) (groupSecretResTmp.getGroupExpRatio() * proTimeMinutes);
		proGS += (int) (groupSecretResTmp.getGroupSupplyRatio() * proTimeMinutes);

		// 可以去更新阵容了
		List<String> changeList = mgr.changeDefendTeamInfo(secretUserId, myDefendInfo.getIndex(), id, totalFighting, now, proRes, proGS, proGE, teamHeroIdList);
		// 更新使用的阵容
		if (!changeList.isEmpty()) {
			teamMgr.changeTeamHeroList(player, changeList);
		}

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}
}