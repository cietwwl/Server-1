package com.rw.service.groupsecret;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankAttribute;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.groupsecret.GroupSecretMatchEnemyDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.groupsecret.GroupSecretHelper;
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper;
import com.rwbase.dao.groupsecret.GroupSecretMatchHelper.IUpdateSecretStateCallBack;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwproto.GroupSecretMatchProto.GroupSecretMatchCommonRspMsg;
import com.rwproto.GroupSecretMatchProto.RequestType;

/*
 * @author HC
 * @date 2016年6月1日 上午11:03:26
 * @Description 匹配的Handler
 */
public class GroupSecretMatchHandler {
	private static GroupSecretHandler handler = new GroupSecretHandler();

	public static GroupSecretHandler getHandler() {
		return handler;
	}

	public ByteString getSerchingEnemyHandler(Player player) {
		// 检查个人的帮派数据
		String userId = player.getUserId();

		GroupSecretMatchCommonRspMsg.Builder rsp = GroupSecretMatchCommonRspMsg.newBuilder();
		rsp.setReqType(RequestType.SEARCHING_ENEMY);

		// 检查个人的帮派数据
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupAttributeData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "您当前暂无帮派，不能进入秘境");
			return rsp.build().toByteString();
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到Group数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到基础数据", groupId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "帮派不存在");
			return rsp.build().toByteString();
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		GroupMemberDataIF selfMemberData = memberMgr.getMemberData(userId, false);
		if (selfMemberData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("帮派Id[%s]没有找到角色[%s]对应的MemberData的记录", groupId, userId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "您不是帮派成员");
			return rsp.build().toByteString();
		}

		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到秘境的基础配置表");
			return rsp.build().toByteString();
		}

		UserGroupSecretBaseDataMgr userSecretBaseDataMgr = UserGroupSecretBaseDataMgr.getMgr();
		UserGroupSecretBaseData userGroupSecretBaseData = userSecretBaseDataMgr.get(userId);

		GroupSecretMatchEnemyData groupSecretMatchEnemyData = GroupSecretMatchEnemyDataMgr.getMgr().get(userId);
		if (groupSecretMatchEnemyData != null) {
			if (groupSecretMatchEnemyData.isBeat()) {
				GroupSecretHelper.fillMatchRspInfo(rsp, false, "请先领取奖励");
				return rsp.build().toByteString();
			}

			// 如果没有打败，现在就要切换下状态
			IUpdateSecretStateCallBack call = new IUpdateSecretStateCallBack() {

				@Override
				public boolean call(GroupSecretMatchRankAttribute attr) {
					return attr.setNonBattleState();
				}
			};
			GroupSecretMatchHelper.updateGroupSecretState(GroupSecretHelper.generateCacheSecretId(groupSecretMatchEnemyData.getMatchUserId(), groupSecretMatchEnemyData.getId()), call);
			userSecretBaseDataMgr.updateMatchSecretId(player, null);// 更新匹配的数据
		}

		int matchTimes = userGroupSecretBaseData.getMatchTimes();
		int matchPrice = uniqueCfg.getMatchPrice(matchTimes);

		long coin = player.getReward(eSpecialItemId.Coin);
		if (matchPrice > coin) {// 金币数量不足
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "金币不足");
			return rsp.build().toByteString();
		}

		String matchId = GroupSecretMatchHelper.getGroupSecretMatchData(player);
		if (StringUtils.isEmpty(matchId)) {
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境，掠夺搜索秘境费用返回");
			return rsp.build().toByteString();
		}

		String[] matchInfoArr = GroupSecretHelper.parseString2UserIdAndSecretId(matchId);
		String matchUserId = matchInfoArr[0];
		int id = Integer.parseInt(matchInfoArr[1]);

		UserCreateGroupSecretData useCreateData = UserCreateGroupSecretDataMgr.getMgr().get(matchUserId);
		GroupSecretData groupSecretData = useCreateData.getGroupSecretData(id);
		if (groupSecretData == null) {
			GameLog.error("搜索秘境敌人", userId, String.format("匹配到的记录Id是[%s],查不着相应的秘境数据", matchId));
			GroupSecretHelper.fillMatchRspInfo(rsp, false, "找不到可掠夺秘境，掠夺搜索秘境费用返回");
			return rsp.build().toByteString();
		}

		// 获取可以掠夺的资源数量
		ConcurrentHashMap<Integer, DefendUserInfoData> defendMap = groupSecretData.getDefendMap();
		GroupSecretMatchEnemyData enemyData = new GroupSecretMatchEnemyData();
		enemyData.setId(id);
		enemyData.setMatchUserId(matchUserId);
		enemyData.setUserId(userId);
		return rsp.build().toByteString();
	}
}