package com.rw.service.groupsecret;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.chat.ChatBM;
import com.bm.chat.ChatInteractiveType;
import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.common.RefParam;
import com.common.Utils;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.groupsecret.GroupSecretDefendRecordDataMgr;
import com.playerdata.groupsecret.GroupSecretTeamDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.groupsecret.GroupSecretHelper;
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretLevelGetResTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretDiamondDropCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretLevelGetResCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretMemberAdditionCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendRecord;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.GroupSecretDataSynData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.BattleCommon;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.GroupSecretProto.ChangeDefendTeamReqMsg;
import com.rwproto.GroupSecretProto.CreateGroupSecretReqMsg;
import com.rwproto.GroupSecretProto.CreateGroupSecretRspMsg;
import com.rwproto.GroupSecretProto.GetDefendRecordRewardReqMsg;
import com.rwproto.GroupSecretProto.GetDefendRecordRewardRspMsg;
import com.rwproto.GroupSecretProto.GetGroupSecretRewardReqMsg;
import com.rwproto.GroupSecretProto.GetInviteSecretInfoReqMsg;
import com.rwproto.GroupSecretProto.GetInviteSecretInfoRspMsg;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.GroupSecretProto.InviteGroupMemberDefendReqMsg;
import com.rwproto.GroupSecretProto.JoinSecretDefendReqMsg;
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
		int level = player.getLevel();

		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.OPEN_MAIN_VIEW);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 个人的秘境数据
		UserGroupSecretBaseData userGroupSecretData = UserGroupSecretBaseDataMgr.getMgr().get(userId);

		// 同步秘境基础数据
		List<SecretBaseInfoSynData> baseInfoList = new ArrayList<SecretBaseInfoSynData>();

		// 检查密境列表
		// List<String> defendSecretIdList = userGroupSecretData.getDefendSecretIdList();
		Map<Integer, String> defendSecretMap = userGroupSecretData.getDefendSecretIdMap();
		// for (int i = 0, size = defendSecretIdList.size(); i < size; i++) {
		for (Iterator<Map.Entry<Integer, String>> itr = defendSecretMap.entrySet().iterator(); itr.hasNext();) {
			// String[] idArr = GroupSecretHelper.parseString2UserIdAndSecretId(defendSecretIdList.get(i));
			Map.Entry<Integer, String> entry = itr.next();
			String[] idArr = GroupSecretHelper.parseString2UserIdAndSecretId(entry.getValue());
			UserCreateGroupSecretData userCreateGroupSecretData = UserCreateGroupSecretDataMgr.getMgr().get(idArr[0]);
			if (userCreateGroupSecretData == null) {
				continue;
			}

			GroupSecretData data = userCreateGroupSecretData.getGroupSecretData(Integer.parseInt(idArr[1]));
			if (data == null) {
				continue;
			}

			// GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(data, userId, level);
			GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(entry.getKey(), data, userId, level);
			if (synData == null) {
				continue;
			}

			SecretBaseInfoSynData base = synData.getBase();
			if (base != null) {
				baseInfoList.add(base);
			}
		}

		// 检查匹配到的人
		GroupSecretDataSynData matchSecretInfo = GroupSecretHelper.fillMatchSecretInfo(player, -2);
		if (matchSecretInfo != null) {
			SecretBaseInfoSynData base = matchSecretInfo.getBase();
			if (base != null) {
				baseInfoList.add(base);
			}
		}

		player.getBaseHolder().synAllData(player, baseInfoList);

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

		int mainPos = req.getMainPos();
		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("请求创建秘境", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("请求创建秘境", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("请求创建秘境", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = baseDataMgr.get(userId);
		if (!userGroupSecretBaseData.isPosEmpty(mainPos)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "该位置已经有防守队伍！");
			return rsp.build().toByteString();
		}
		// List<String> defendSecretIdList = userGroupSecretBaseData.getDefendSecretIdList();// 当前的秘境列表
		Map<Integer, String> defendSecretIdMap = userGroupSecretBaseData.getDefendSecretIdMap();// 当前的秘境列表
		// TODO HC 这里可能要从特权加，检查秘境创建的数量是不是超出了上限
		int intPrivilege = player.getPrivilegeMgr().getIntPrivilege(GroupPrivilegeNames.mysteryChallengeCount);
		// if (defendSecretIdList.size() >= intPrivilege) {
		if (defendSecretIdMap.size() >= intPrivilege || intPrivilege < mainPos) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("您当前只能创建%s个秘境", intPrivilege));
			return rsp.build().toByteString();
		}

		int secretCfgId = req.getSecretCfgId();// 要创建的秘境的配置Id
		GroupSecretResourceCfg groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "请求创建秘境的类型不存在");
			return rsp.build().toByteString();
		}

		int level = player.getLevel();
		GroupSecretLevelGetResTemplate levelGetResTemplate = GroupSecretLevelGetResCfgDAO.getCfgDAO().getLevelGetResTemplate(groupSecretResTmp.getLevelGroupId(), level);
		if (levelGetResTemplate == null) {
			GameLog.error("请求创建秘境", userId, String.format("找不到等级组[%s],角色等级[%s]的配置表", groupSecretResTmp.getLevelGroupId(), level));
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境等级组对应的配置");
			return rsp.build().toByteString();
		}

		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(userId);
		if (userCreateGroupSecretData == null) {
			GameLog.error("请求创建秘境", userId, "找不到角色对应的秘境存储数据");
			GroupSecretHelper.fillRspInfo(rsp, false, "暂无对应的秘境数据");
			return rsp.build().toByteString();
		}

		List<BattleHeroPosition> teamHeroIdList = req.getTeamHeroIdList();
		if (teamHeroIdList == null || teamHeroIdList.isEmpty()) {
			GameLog.error("请求创建秘境", userId, "从客户端传递过来的防守阵容信息是空的");
			GroupSecretHelper.fillRspInfo(rsp, false, "防守阵容不能为空");
			return rsp.build().toByteString();
		}

		GroupSecretTeamDataMgr teamMgr = GroupSecretTeamDataMgr.getMgr();
		GroupSecretTeamData teamData = teamMgr.get(userId);
		List<String> defendHeroList = teamData.getDefendHeroList();

		int totalFighting = 0;
		boolean containsMainRole = false;

		int size = teamHeroIdList.size();
		List<String> canAddDefendList = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			BattleHeroPosition heroPos = teamHeroIdList.get(i);
			String teamUserId = heroPos.getHeroId();
			// Hero hero = player.getHeroMgr().getHeroById(teamUserId);
			Hero hero = player.getHeroMgr().getHeroById(player, teamUserId);
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

		// if (canAddDefendList.size() < 2) {
		// GroupSecretHelper.fillRspInfo(rsp, false, "主角无法单独驻守秘境");
		// return rsp.build().toByteString();
		// }

		if (canAddDefendList.size() > 5) {
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
		userInfoData.setDropDiamond(GroupSecretDiamondDropCfgDAO.getCfgDAO().getDiamondDropNum(levelGetResTemplate.getDiamondDropId()));
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
		String generateCacheSecretId = GroupSecretHelper.generateCacheSecretId(userId, secretData.getId());
		baseDataMgr.addDefendSecretId(userId, generateCacheSecretId, mainPos);

		// 增加阵容
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, BattleCommon.eBattlePositionType.GroupSecretPos_VALUE, generateCacheSecretId, EmbattlePositonHelper.parseMsgHeroPos2Memery(teamHeroIdList));

		GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(mainPos, secretData, userId, level);
		SecretBaseInfoSynData base = synData.getBase();
		if (base != null) {
			player.getBaseHolder().addData(player, base);
		}

		SecretTeamInfoSynData team = synData.getTeam();
		if (team != null) {
			player.getTeamHolder().addData(player, team);
		}

		// 把秘境数据加入到排行榜
		GroupSecretMatchHelper.addGroupSecret2Rank(player, secretData);

		// 通知角色日常任务 by Alex
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.GROUPSECRET_EXPLORE, 1);

		// 回应消息
		CreateGroupSecretRspMsg.Builder createRsp = CreateGroupSecretRspMsg.newBuilder();
		createRsp.setId(generateCacheSecretId);

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

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
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
		GroupSecretResourceCfg groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("找不到角色[%s]秘境[%s]对应的配置表GroupSecretResourceTemplate", secretUserId, secretCfgId));
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境对应的类型配置表");
			return rsp.build().toByteString();
		}

		int level = player.getLevel();
		GroupSecretLevelGetResTemplate levelGetResTemplate = GroupSecretLevelGetResCfgDAO.getCfgDAO().getLevelGetResTemplate(groupSecretResTmp.getLevelGroupId(), level);
		if (levelGetResTemplate == null) {
			GameLog.error("请求领取秘境奖励", userId, String.format("找不到等级组[%s],角色等级[%s]的配置表", groupSecretResTmp.getLevelGroupId(), level));
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境等级组对应的配置");
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

		// long changeTeamTime = myDefendInfo.getChangeTeamTime();// 修改阵容时间
		// int proRes = myDefendInfo.getProRes() - myDefendInfo.getRobRes();
		// int proGE = myDefendInfo.getProGE() - myDefendInfo.getRobGE();
		// int proGS = myDefendInfo.getProGS() - myDefendInfo.getRobGS();
		// int dropDiamond = myDefendInfo.getDropDiamond();
		// if (changeTeamTime > 0) {
		// long minutes = TimeUnit.MILLISECONDS.toMinutes((isFinish ? (createTime + needTimeMillis) : now) - changeTeamTime);
		// int fighting = myDefendInfo.getFighting();
		// proRes += (int) (fighting * levelGetResTemplate.getProductRatio() * minutes);
		// proGE += (int) (levelGetResTemplate.getGroupExpRatio() * minutes);
		// proGS += (int) (levelGetResTemplate.getGroupSupplyRatio() * minutes);
		// }

		// 2016-08-12 By PERRY，新需求是直接使用模板的产出数量 BEGIN >>>>>>>>>>
		int pct = GroupSecretMemberAdditionCfgDAO.getCfgDAO().getAdditional(groupSecretData.getDefendSize());
		int proRes = levelGetResTemplate.getTotalProduct() - myDefendInfo.getRobRes();
		int proGE = levelGetResTemplate.getTotalGroupExp() - myDefendInfo.getRobGE();
		int proGS = levelGetResTemplate.getTotalGroupSupply() - myDefendInfo.getRobGS();
		int dropDiamond = myDefendInfo.getDropDiamond();
		if (pct > 0) {
			proRes += Utils.calculateTenThousandRatio(proRes, pct);
			proGE += Utils.calculateTenThousandRatio(proGE, pct);
			proGS += Utils.calculateTenThousandRatio(proGS, pct);
		}
		// 2016-08-12 END <<<<<<<<<

		// 增加帮派经验物资
		boolean hasGroupAdd = false;
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (!StringUtils.isEmpty(groupId)) {
			if (groupId.equals(groupSecretData.getGroupId())) {
				Group group = GroupBM.get(groupId);
				if (group != null) {
					hasGroupAdd = true;
					group.getGroupBaseDataMgr().updateGroupDonate(player, null, proGS, proGE, 0, true);
				}
			}
		}

		// 增加资源
		if (proRes > 0) {
			ItemBagMgr.getInstance().addItem(player, groupSecretResTmp.getReward(), proRes);
		}

		// 钻石
		if (dropDiamond > 0) {
			ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Gold.getValue(), dropDiamond);
		}

		// 把自己的驻守信息移除
		userSecretDataMgr.removeDefendSecretId(userId, getRewardSecretId);

		// 删除秘境中自己的防守信息
		mgr.removeDefendInfoData(secretUserId, myDefendInfo.getIndex(), id);

		// 清除自己防守阵容中使用到的
		GroupSecretTeamDataMgr.getMgr().removeTeamHeroList(player, myDefendInfo.getHeroList());

		// 从排行榜移除
		GroupSecretMatchHelper.removeGroupSecretMatchEntry(player, getRewardSecretId);

		// 移除阵容
		EmbattleInfoMgr.getMgr().removeEmbattleInfo(player, eBattlePositionType.GroupSecretPos_VALUE, getRewardSecretId);

		// 通知客户端删除
		player.getBaseHolder().removeData(player, new SecretBaseInfoSynData(getRewardSecretId, 0, true, 0, 0, 0, 0, 0, 0, 0, ""));
		player.getTeamHolder().removeData(player, new SecretTeamInfoSynData(getRewardSecretId, null, 0));

		rsp.setIsSuccess(true);
		if (!hasGroupAdd) {
			rsp.setTipMsg("因退出原帮派，不能获得该秘境帮派物资和帮派经验");
		}
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

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
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

		if (!groupSecretData.getGroupId().equals(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "不在同一帮派，无法进行该操作");
			return rsp.build().toByteString();
		}

		long now = System.currentTimeMillis();
		int secretCfgId = groupSecretData.getSecretId();// 秘境的模版Id
		GroupSecretResourceCfg groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("找不到角色[%s]秘境[%s]对应的配置表GroupSecretResourceTemplate", secretUserId, secretCfgId));
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境对应的类型配置表");
			return rsp.build().toByteString();
		}

		int level = player.getLevel();
		GroupSecretLevelGetResTemplate levelGetResTemplate = GroupSecretLevelGetResCfgDAO.getCfgDAO().getLevelGetResTemplate(groupSecretResTmp.getLevelGroupId(), level);
		if (levelGetResTemplate == null) {
			GameLog.error("请求更换秘境阵容", userId, String.format("找不到等级组[%s],角色等级[%s]的配置表", groupSecretResTmp.getLevelGroupId(), level));
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境等级组对应的配置");
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

		List<BattleHeroPosition> teamHeroIdList = req.getTeamHeroIdList();
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

		List<String> teamIdList = new ArrayList<String>(size);

		boolean containsMainRole = false;
		for (int i = 0; i < size; i++) {
			BattleHeroPosition heroPos = teamHeroIdList.get(i);
			String teamUserId = heroPos.getHeroId();
			// Hero hero = player.getHeroMgr().getHeroById(teamUserId);
			Hero hero = player.getHeroMgr().getHeroById(player, teamUserId);
			if (hero == null) {
				GameLog.error("请求更换秘境阵容", userId, String.format("Id为[%s]的英雄在服务器查找不到对应的Hero对象", teamUserId));
				GroupSecretHelper.fillRspInfo(rsp, false, "英雄不存在");
				return rsp.build().toByteString();
			}

			teamIdList.add(teamUserId);// 增加阵容用的Id

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

		// if (size < 2) {
		// GroupSecretHelper.fillRspInfo(rsp, false, "主角无法单独驻守秘境");
		// return rsp.build().toByteString();
		// }

		if (size > 5) {
			GroupSecretHelper.fillRspInfo(rsp, false, "驻守阵容不能超过5个人");
			return rsp.build().toByteString();
		}
		//
		// // 计算资源产出
		// int proRes = myDefendInfo.getProRes();
		// int proGS = myDefendInfo.getProGS();
		// int proGE = myDefendInfo.getProGE();
		// // 上次更换阵容时间
		// long proTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(now - myDefendInfo.getChangeTeamTime());
		//
		// proRes += (int) (myDefendInfo.getFighting() * levelGetResTemplate.getProductRatio() * proTimeMinutes);
		// proGE += (int) (levelGetResTemplate.getGroupExpRatio() * proTimeMinutes);
		// proGS += (int) (levelGetResTemplate.getGroupSupplyRatio() * proTimeMinutes);
		//
		// 2016-08-12 By PERRY 新需求：产出不会随着防守战队的变化而变化 BEGIN >>>>>>>>>>
		int proRes = 0;
		int proGE = 0;
		int proGS = 0;
		// 2016-08-12 END <<<<<<<<<<
		// 可以去更新阵容了
		List<String> changeList = mgr.changeDefendTeamInfo(secretUserId, myDefendInfo.getIndex(), id, totalFighting, now, proRes, proGS, proGE, teamIdList);
		// 更新使用的阵容
		if (!changeList.isEmpty()) {
			teamMgr.changeTeamHeroList(player, changeList);
		}

		// 增加阵容
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, BattleCommon.eBattlePositionType.GroupSecretPos_VALUE, changeTeamSecretId, EmbattlePositonHelper.parseMsgHeroPos2Memery(teamHeroIdList));

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}

	/**
	 * 领取秘境防守记录中的钥石
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString getDefendRecordRewardHandler(Player player, GetDefendRecordRewardReqMsg req) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.GET_DEFEDN_REWARD);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = baseDataMgr.get(userId);

		int getKeyLimit = uniqueCfg.getGetKeyLimit();
		int maxKeyLimit = uniqueCfg.getMaxKeyLimit();
		if (userGroupSecretBaseData.getReceiveKeyCount() >= getKeyLimit) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("每天只能领取%s块钥石", getKeyLimit));
			return rsp.build().toByteString();
		}

		int keyCount = userGroupSecretBaseData.getKeyCount();
		if (keyCount >= maxKeyLimit) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境钥石数量已达上限");
			return rsp.build().toByteString();
		}

		GroupSecretDefendRecordDataMgr mgr = GroupSecretDefendRecordDataMgr.getMgr();
		int rewardKeys = 0;
		int defenceTimes = 0;

		List<Integer> idList = new ArrayList<Integer>();
		if (req.hasId()) {
			int id = req.getId();
			DefendRecord defendRecord = mgr.getDefendRecord(userId, id);
			if (defendRecord == null) {
				GameLog.error("请求领取防守记录奖励", userId, String.format("请求领取的防守记录Id是[%s],在防守记录列表没有这个数据", id));
				GroupSecretHelper.fillRspInfo(rsp, false, "防守记录不存在");
				return rsp.build().toByteString();
			}

			if (!defendRecord.isHasKey()) {
				GroupSecretHelper.fillRspInfo(rsp, false, "该防守记录奖励已领取");
				return rsp.build().toByteString();
			}

			idList.add(id);
			defenceTimes = defendRecord.getDefenceTimes();
			rewardKeys = defenceTimes * uniqueCfg.getRewardKeyCount();
		} else {
			List<DefendRecord> list = mgr.getSortDefendRecordList(userId);
			for (int i = list.size() - 1; i >= 0; --i) {
				if (keyCount >= maxKeyLimit) {
					break;
				}

				DefendRecord defendRecord = list.get(i);
				if (defendRecord == null || !defendRecord.isHasKey()) {
					continue;
				}

				idList.add(defendRecord.getId());
				int times = defendRecord.getDefenceTimes();
				defenceTimes += times;

				int reward = times * uniqueCfg.getRewardKeyCount();
				rewardKeys += reward;
				keyCount += reward;
			}
		}

		if (rewardKeys <= 0) {
			GroupSecretHelper.fillRspInfo(rsp, false, "已经没有可以领取的钥石了");
			return rsp.build().toByteString();
		}

		baseDataMgr.updateReceiveKeyCount(player, rewardKeys, false);
		// 更新记录数据
		for (int i = 0, size = idList.size(); i < size; i++) {
			mgr.updateDefendRecordKeyState(player, idList.get(i));
		}

		GetDefendRecordRewardRspMsg.Builder getDefendRewardRsp = GetDefendRecordRewardRspMsg.newBuilder();
		getDefendRewardRsp.setDefendTimes(defenceTimes);
		getDefendRewardRsp.setGetDefendRewardKeyNum(rewardKeys);

		rsp.setIsSuccess(true);
		rsp.setGetDefendRewardRspMsg(getDefendRewardRsp);
		return rsp.build().toByteString();
	}

	/**
	 * 购买钥石数量
	 * 
	 * @param player
	 * @return
	 */
	public ByteString buySecretKeyHandler(Player player) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.BUY_SECRET_KEY);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = baseDataMgr.get(userId);
		int buyKeyTimes = userGroupSecretBaseData.getBuyKeyTimes();

		int price = uniqueCfg.getBuyKeyPrice(buyKeyTimes);
		if (price == -1) {
			GroupSecretHelper.fillRspInfo(rsp, false, "今天购买钥石次数已经用完");
			return rsp.build().toByteString();
		}

		int add = uniqueCfg.getBuyKeyAdd(buyKeyTimes);

		long reward = player.getReward(eSpecialItemId.Gold);
		if (price > reward) {
			GroupSecretHelper.fillRspInfo(rsp, false, "钻石不足");
			return rsp.build().toByteString();
		}

		baseDataMgr.updateBuyKeyData(player, add);

		ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Gold.getValue(), -price);

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}

	/**
	 * 请求别人协助秘境
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString inviteMemberDefendSecretHandler(Player player, InviteGroupMemberDefendReqMsg req) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.INVITE_MEMBER_DEFEND);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("邀请协助驻守成员", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("邀请协助驻守成员", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("邀请协助驻守成员", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		String reqId = req.getId();
		String[] arr = GroupSecretHelper.parseString2UserIdAndSecretId(reqId);

		if (!arr[0].equals(userId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "请求协助的秘境不是您创建的");
			return rsp.build().toByteString();
		}

		int id = Integer.parseInt(arr[1]);
		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(userId);
		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境不存在");
			return rsp.build().toByteString();
		}

		if (!groupSecretData.getGroupId().equals(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "不在同一帮派，无法进行该操作");
			return rsp.build().toByteString();
		}

		int cfgId = groupSecretData.getSecretId();
		GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
		if (cfg == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境类型不存在");
			return rsp.build().toByteString();
		}

		long now = System.currentTimeMillis();
		long createTime = groupSecretData.getCreateTime();
		long needTimeMillis = TimeUnit.MINUTES.toMillis(cfg.getNeedTime());
		if (now - createTime > needTimeMillis) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境已经完成，请领取奖励");
			return rsp.build().toByteString();
		}

		long joinLimitMillis = TimeUnit.MINUTES.toMillis(cfg.getJoinLimitTime());
		if (now - createTime > joinLimitMillis) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("创建时间已经超过%d分钟，不能邀请！", cfg.getJoinLimitTime()));
			return rsp.build().toByteString();
		}

		List<String> inviteList = req.getMemberIdList();
		if (inviteList.isEmpty()) {
			GroupSecretHelper.fillRspInfo(rsp, false, "邀请列表不能为空");
			return rsp.build().toByteString();
		}

		for (int i = 0, size = inviteList.size(); i < size; i++) {
			String heroId = inviteList.get(i);
			GroupMemberDataIF memberData = memberMgr.getMemberData(heroId, false);
			if (memberData == null) {
				GroupSecretHelper.fillRspInfo(rsp, false, "不能邀请非帮派成员");
				return rsp.build().toByteString();
			}
		}

		mgr.updateInviteHeroList(player, id, inviteList);

		// 发送聊天邀请
		String message = "";
		if (req.hasMessage()) {
			message = req.getMessage();
		}

		// 秘境要传递到聊天部分的信息
		String format = "邀请防守：[%s](人数：%s/%s)\n%s\n";
		// message = String.format(format, cfg.getName(), inviteList.size(), memberMgr.getGroupMemberSize() - 1, message);
		message = String.format(format, cfg.getName(), groupSecretData.getDefendMap().size(), GroupSecretIndex.values().length, message);

		// 设置邀请时间
		StringBuilder sb = new StringBuilder();
		sb.append("1").append(":").append(groupSecretData.getCreateTime());

		ChatBM.getInstance().sendInteractiveMsg(player, ChatInteractiveType.TREASURE, message, reqId, sb.toString(), inviteList);

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}

	/**
	 * 请求加入秘境驻守
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString joinScretDefendHandler(Player player, JoinSecretDefendReqMsg req) {
		String userId = player.getUserId();
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.JOIN_SECRET_DEFEND);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("接受邀请驻守成员", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("接受邀请驻守成员", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("接受邀请驻守成员", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = baseDataMgr.get(userId);
		// List<String> defendSecretIdList = userGroupSecretBaseData.getDefendSecretIdList();// 当前的秘境列表
		Map<Integer, String> defendSecretIdMap = userGroupSecretBaseData.getDefendSecretIdMap();// 当前的秘境列表
		// TODO HC 这里可能要从特权加，检查秘境创建的数量是不是超出了上限
		int intPrivilege = player.getPrivilegeMgr().getIntPrivilege(GroupPrivilegeNames.mysteryChallengeCount);
		// if (defendSecretIdList.size() >= intPrivilege) {
		if (defendSecretIdMap.size() >= intPrivilege) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("探索秘境已达上限", intPrivilege));
			return rsp.build().toByteString();
		}

		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		String reqId = req.getId();

		if (userGroupSecretBaseData.hasDefendSecretId(reqId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "您不能重复驻守同一秘境");
			return rsp.build().toByteString();
		}

		String[] arr = GroupSecretHelper.parseString2UserIdAndSecretId(reqId);

		String createUserId = arr[0];
		if (createUserId.equals(userId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "自己创建的秘境不能邀请自己");
			return rsp.build().toByteString();
		}

		int id = Integer.parseInt(arr[1]);
		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(createUserId);
		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境已消失");
			return rsp.build().toByteString();
		}

		// 获取是否邀请了这个人，并且这个人是不是该帮派成员
		if (!groupSecretData.getInviteList().contains(userId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "此秘境并未邀请您来驻守");
			return rsp.build().toByteString();
		}

		if (!groupSecretData.getGroupId().equals(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "您不是秘境所属帮派的成员，不能驻守");
			return rsp.build().toByteString();
		}

		int cfgId = groupSecretData.getSecretId();
		GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
		if (cfg == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境类型不存在");
			return rsp.build().toByteString();
		}

		long now = System.currentTimeMillis();
		long createTime = groupSecretData.getCreateTime();
		long needTimeMillis = TimeUnit.MINUTES.toMillis(cfg.getNeedTime());
		long passTimeMillis = now - createTime;
		if (passTimeMillis > needTimeMillis) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境已消失");
			return rsp.build().toByteString();
		}

		long minAssistTimeMillis = TimeUnit.MINUTES.toMillis(uniqueCfg.getMinAssistTime());
		long leftTimeMillis = needTimeMillis - passTimeMillis;
		if (leftTimeMillis < minAssistTimeMillis) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("秘境剩余不到%s分钟，不能驻守", uniqueCfg.getMinAssistTime()));
			return rsp.build().toByteString();
		}

		long joinLimitMillis = TimeUnit.MINUTES.toMillis(cfg.getJoinLimitTime());
		if (now - createTime > joinLimitMillis) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("创建时间已经超过%d分钟，不能邀请！", cfg.getJoinLimitTime()));
			return rsp.build().toByteString();
		}

		// 检查某个矿点上有没有人驻守
		int index = req.getIndex().getNumber();
		DefendUserInfoData defendUserInfoData = groupSecretData.getDefendUserInfoData(index);
		if (defendUserInfoData != null) {
			// 同步数据到前台
			// GroupSecretDataSynData synMsg = GroupSecretHelper.parseGroupSecretData2Msg(groupSecretData, userId, player.getLevel());
			GroupSecretDataSynData synMsg = GroupSecretHelper.parseGroupSecretData2Msg(0, groupSecretData, userId, player.getLevel());
			if (synMsg != null) {
				player.getBaseHolder().updateSingleData(player, synMsg.getBase());
				player.getTeamHolder().updateSingleData(player, synMsg.getTeam());
			}

			GroupSecretHelper.fillRspInfo(rsp, false, "据点已被其他成员派驻");
			return rsp.build().toByteString();
		}

		List<BattleHeroPosition> teamHeroIdList = req.getHeroIdList();
		if (teamHeroIdList.isEmpty()) {
			GroupSecretHelper.fillRspInfo(rsp, false, "驻守阵容不能空");
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
			BattleHeroPosition heroPos = teamHeroIdList.get(i);
			String teamUserId = heroPos.getHeroId();
			// Hero hero = player.getHeroMgr().getHeroById(teamUserId);
			Hero hero = player.getHeroMgr().getHeroById(player, teamUserId);
			if (hero == null) {
				GameLog.error("接受邀请驻守成员", userId, String.format("Id为[%s]的英雄在服务器查找不到对应的Hero对象", teamUserId));
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
				GameLog.error("接受邀请驻守成员", userId, String.format("Id为[%s]的英雄已经被其他驻守队伍使用,或者客户端一个英雄ID多用", teamUserId));
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

		if (canAddDefendList.size() > 5) {
			GroupSecretHelper.fillRspInfo(rsp, false, "驻守阵容不能超过5个人");
			return rsp.build().toByteString();
		}

		// 更新参与防守的阵容信息
		teamMgr.addDefendHeroIdList(player, canAddDefendList);

		int level = player.getLevel();
		// 获取对应的掉落
		int dropId = GroupSecretLevelGetResCfgDAO.getCfgDAO().getDropIdBasedOnJoinTime(cfg.getLevelGroupId(), level, (int) TimeUnit.MILLISECONDS.toMinutes(leftTimeMillis));
		int diamondDropNum = GroupSecretDiamondDropCfgDAO.getCfgDAO().getDiamondDropNum(dropId);

		UserGroupSecretBaseData baseData = baseDataMgr.get(userId);
		int intP = player.getPrivilegeMgr().getIntPrivilege(GroupPrivilegeNames.mysteryChallengeCount);
		int mainPos = -1;
		for (int i = 0; i < intP; i++) {
			int pos = i + 1;
			if (baseData.isPosEmpty(pos)) {
				mainPos = pos; // mainPos从1开始
				break;
			}
		}

		if (mainPos == -1) {
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到合适的防守位置");
			return rsp.build().toByteString();
		}

		// 防守的信息
		DefendUserInfoData userInfoData = new DefendUserInfoData();
		userInfoData.setDefTime(now);
		userInfoData.setChangeTeamTime(now);
		userInfoData.setHeroList(canAddDefendList);
		userInfoData.setIndex(index);
		userInfoData.setUserId(userId);
		userInfoData.setDropDiamond(diamondDropNum);
		userInfoData.setFighting(totalFighting);

		// 增加秘境防守阵容
		if (!mgr.addDefendTeamInfo(createUserId, id, index, userInfoData)) {
			// 同步数据到前台
			// GroupSecretDataSynData synMsg = GroupSecretHelper.parseGroupSecretData2Msg(groupSecretData, userId, player.getLevel());
			GroupSecretDataSynData synMsg = GroupSecretHelper.parseGroupSecretData2Msg(0, groupSecretData, userId, player.getLevel());
			if (synMsg != null) {
				player.getBaseHolder().updateSingleData(player, synMsg.getBase());
				player.getTeamHolder().updateSingleData(player, synMsg.getTeam());
			}

			GameLog.error("接受邀请驻守成员", userId, String.format("请求的秘境[%s],驻守点为[%s],已经有人驻守了", reqId, req.getIndex()));
			GroupSecretHelper.fillRspInfo(rsp, false, "据点已被其他成员派驻");
			return rsp.build().toByteString();
		}

		// 更新一下防守阵容
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, BattleCommon.eBattlePositionType.GroupSecretPos_VALUE, reqId, EmbattlePositonHelper.parseMsgHeroPos2Memery(teamHeroIdList));

		// 更新目前防守的秘境列表
		baseDataMgr.addDefendSecretId(userId, reqId, mainPos);

		GroupSecretDataSynData synData = GroupSecretHelper.parseGroupSecretData2Msg(mainPos, groupSecretData, userId, level);
		SecretBaseInfoSynData base = synData.getBase();
		if (base != null) {
			player.getBaseHolder().addData(player, base);
		}

		SecretTeamInfoSynData team = synData.getTeam();
		if (team != null) {
			player.getTeamHolder().addData(player, team);
		}

		rsp.setIsSuccess(true);
		return rsp.build().toByteString();
	}

	/**
	 * 获取邀请秘境的信息
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString getInviteSecretInfoHandler(Player player, GetInviteSecretInfoReqMsg req) {
		String userId = player.getUserId();
		// 检查是否有帮派
		GroupSecretCommonRspMsg.Builder rsp = GroupSecretCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.GET_INVITE_SECRET_INFO);

		// 检查当前角色的等级有没有达到可以使用帮派秘境功能
		RefParam<String> outTip = new RefParam<String>();
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SECRET_AREA, player, outTip)) {
			GroupSecretHelper.fillRspInfo(rsp, false, outTip.value);
			return rsp.build().toByteString();
		}

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("查看邀请驻守秘境信息", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("查看邀请驻守秘境信息", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("查看邀请驻守秘境信息", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillRspInfo(rsp, false, "加入帮派才能进行该操作");
			return rsp.build().toByteString();
		}

		// 检查秘境是否存在或者完成
		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		String reqId = req.getId();
		// UserGroupSecretBaseDataMgr baseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		// UserGroupSecretBaseData userGroupSecretBaseData = baseDataMgr.get(userId);
		// if (userGroupSecretBaseData.hasDefendSecretId(reqId)) {
		// GroupSecretHelper.fillRspInfo(rsp, false, "您不能重复驻守同一秘境");
		// return rsp.build().toByteString();
		// }

		String[] arr = GroupSecretHelper.parseString2UserIdAndSecretId(reqId);
		String createUserId = arr[0];
		if (createUserId.equals(userId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "自己创建的秘境不能被邀请");
			return rsp.build().toByteString();
		}

		int id = Integer.parseInt(arr[1]);
		UserCreateGroupSecretDataMgr mgr = UserCreateGroupSecretDataMgr.getMgr();
		UserCreateGroupSecretData userCreateGroupSecretData = mgr.get(createUserId);
		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境已消失");
			return rsp.build().toByteString();
		}

		long createTime = groupSecretData.getCreateTime();

		if (req.hasTime()) {
			long reqTime = req.getTime();
			if (reqTime > 0 && reqTime != createTime) {
				GroupSecretHelper.fillRspInfo(rsp, false, "秘境已消失");
				return rsp.build().toByteString();
			}
		}

		// 获取是否邀请了这个人，并且这个人是不是该帮派成员
		if (!groupSecretData.getInviteList().contains(userId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "此秘境并未邀请您来驻守");
			return rsp.build().toByteString();
		}

		if (!groupSecretData.getGroupId().equals(groupId)) {
			GroupSecretHelper.fillRspInfo(rsp, false, "您不是秘境所属帮派的成员，不能驻守");
			return rsp.build().toByteString();
		}

		int cfgId = groupSecretData.getSecretId();
		GroupSecretResourceCfg cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(cfgId);
		if (cfg == null) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境类型不存在");
			return rsp.build().toByteString();
		}

		long now = System.currentTimeMillis();
		long needTimeMillis = TimeUnit.MINUTES.toMillis(cfg.getNeedTime());
		long passTimeMillis = now - createTime;
		if (passTimeMillis > needTimeMillis) {
			GroupSecretHelper.fillRspInfo(rsp, false, "秘境已消失");
			return rsp.build().toByteString();
		}

		long minAssistTimeMillis = TimeUnit.MINUTES.toMillis(uniqueCfg.getMinAssistTime());
		long leftTimeMillis = needTimeMillis - passTimeMillis;
		if (leftTimeMillis < minAssistTimeMillis) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("秘境剩余不到%s分钟，不能驻守", uniqueCfg.getMinAssistTime()));
			return rsp.build().toByteString();
		}

		long joinLimit = TimeUnit.MINUTES.toMillis(cfg.getJoinLimitTime());
		if (passTimeMillis > joinLimit) {
			GroupSecretHelper.fillRspInfo(rsp, false, String.format("秘境创建时间已经超过%d分钟，不能驻守", cfg.getJoinLimitTime()));
			return rsp.build().toByteString();
		}

		GroupSecretDataSynData synMsg = GroupSecretHelper.parseGroupSecretData2Msg(0, groupSecretData, userId, player.getLevel());
		if (synMsg != null) {
			player.getBaseHolder().updateSingleData(player, synMsg.getBase());
			player.getTeamHolder().updateSingleData(player, synMsg.getTeam());
		}

		GetInviteSecretInfoRspMsg.Builder inviteRsp = GetInviteSecretInfoRspMsg.newBuilder();
		inviteRsp.setId(reqId);

		rsp.setIsSuccess(true);
		rsp.setInviteSecretInfoRspMsg(inviteRsp);
		return rsp.build().toByteString();
	}
}