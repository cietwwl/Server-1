package com.rwbase.dao.groupsecret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.log.GameLog;
import com.playerdata.PlayerMgr;
import com.playerdata.groupsecret.GroupSecretMatchEnemyDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.ItemDataIF;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwbase.dao.groupsecret.syndata.SecretBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.SecretTeamInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.DefendHeroBaseInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.DefendTeamInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.DefendUserInfoSynData;
import com.rwbase.dao.groupsecret.syndata.base.GroupSecretDataSynData;
import com.rwbase.dao.groupsecret.syndata.base.HeroLeftInfoSynData;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;

/*
 * @author HC
 * @date 2016年5月26日 下午5:17:29
 * @Description 
 */
public class GroupSecretHelper {
	/**
	 * 填充回应状态消息
	 * 
	 * @param rsp
	 * @param isSuccess
	 * @param tipMsg
	 */
	public static void fillRspInfo(GroupSecretCommonRspMsg.Builder rsp, boolean isSuccess, String tipMsg) {
		rsp.setIsSuccess(isSuccess);
		if (!StringUtils.isEmpty(tipMsg)) {
			rsp.setTipMsg(tipMsg);
		}
	}

	/**
	 * 获取自己的防守信息，并填充一下阵容的信息
	 * 
	 * @param defendMap
	 * @param userId
	 * @param isFinish
	 * @param defendUserInfoMap
	 * @return
	 */
	public static DefendUserInfoData getMyDefendUseInfoData(GroupSecretData secretData, String userId, boolean isFinish, Map<Integer, DefendUserInfoSynData> defendUserInfoMap) {
		return parseSecretData2NeedTeamInfo(secretData, userId, isFinish, null, defendUserInfoMap);
	}

	/**
	 * 获取匹配到的敌人的防守信息
	 * 
	 * @param secretData
	 * @param enemyData
	 * @param defendUserInfoList
	 */
	public static void getEnemyTeamInfo(GroupSecretData secretData, GroupSecretMatchEnemyData enemyData, Map<Integer, DefendUserInfoSynData> defendUserInfoMap) {
		parseSecretData2NeedTeamInfo(secretData, null, false, enemyData, defendUserInfoMap);
	}

	/**
	 * 通过秘境的数据转换成需要阵容信息
	 * 
	 * @param secretData
	 * @param userId
	 * @param isFinish
	 * @param enemyData
	 * @param defendUserInfoList
	 * @return
	 */
	private static DefendUserInfoData parseSecretData2NeedTeamInfo(GroupSecretData secretData, String userId, boolean isFinish, GroupSecretMatchEnemyData enemyData,
			Map<Integer, DefendUserInfoSynData> defendUserInfoMap) {
		DefendUserInfoData myDefendInfo = null;
		// 找出自己驻守的秘境
		Map<Integer, DefendUserInfoData> defendMap = secretData.getDefendMap();
		for (Entry<Integer, DefendUserInfoData> e : defendMap.entrySet()) {
			DefendUserInfoData value = e.getValue();
			if (value == null) {
				continue;
			}

			String defendUserId = value.getUserId();
			PlayerIF readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(defendUserId);
			if (readOnlyPlayer == null) {
				continue;
			}

			if (defendUserId.equals(userId)) {
				myDefendInfo = value;
			}

			if (!isFinish) {
				// 法宝信息
				ItemDataIF magic = readOnlyPlayer.getMagic();

				Integer index = e.getKey();
				Map<String, HeroLeftInfoSynData> teamAttrInfoMap = enemyData == null ? null : enemyData.getTeamAttrInfoMap(index);

				boolean isHasLife = true;
				int fighting = 0;
				List<String> heroList = value.getHeroList();
				int heroSize = heroList.size();

				List<DefendHeroBaseInfoSynData> baseInfoList = new ArrayList<DefendHeroBaseInfoSynData>(heroSize);
				for (int j = 0; j < heroSize; j++) {
					String heroId = heroList.get(j);
					HeroIF hero = readOnlyPlayer.getHeroMgr().getHeroById(heroId);
					if (hero == null) {
						continue;
					}

					fighting += hero.getFighting();

					boolean isDie = false;
					HeroLeftInfoSynData heroLeftInfo = null;
					if (teamAttrInfoMap != null) {
						heroLeftInfo = teamAttrInfoMap.get(heroId);
						int leftLife = heroLeftInfo != null ? heroLeftInfo.getLife() : 0;
						if (leftLife <= 0) {
							isDie = true;
						} else {
							isHasLife = true;
						}
					} else {
						isHasLife = true;
					}

					baseInfoList.add(new DefendHeroBaseInfoSynData(hero.getHeroCfg().getBattleIcon(), hero.getQualityId(), hero.getHeroData().getStarLevel(), hero.getLevel(), heroId
							.equals(defendUserId), isDie, heroLeftInfo));
				}

				DefendUserInfoSynData userInfo = isHasLife ? new DefendUserInfoSynData(index, false, new DefendTeamInfoSynData(defendUserId, readOnlyPlayer.getHeadImage(),
						readOnlyPlayer.getUserName(), readOnlyPlayer.getLevel(), fighting, magic.getModelId(), magic.getMagicLevel(), baseInfoList, 0, "", readOnlyPlayer
								.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupName())) : new DefendUserInfoSynData(index, true, null);
				defendUserInfoMap.put(index, userInfo);
			}
		}

		return myDefendInfo;
	}

	/**
	 * 填充敌人的匹配信息
	 * 
	 * @param userId
	 * @return
	 */
	public static GroupSecretDataSynData fillMatchSecretInfo(String userId) {
		GroupSecretMatchEnemyDataMgr mgr = GroupSecretMatchEnemyDataMgr.getMgr();
		GroupSecretMatchEnemyData enemyData = mgr.get(userId);
		if (enemyData == null) {
			return null;
		}

		String matchUserId = enemyData.getMatchUserId();
		int secretId = enemyData.getId();

		if (StringUtils.isEmpty(matchUserId)) {
			return null;
		}

		UserCreateGroupSecretData userCreateGroupSecretData = UserCreateGroupSecretDataMgr.getMgr().get(matchUserId);
		if (userCreateGroupSecretData == null) {
			GameLog.error("填充搜索到的秘境信息", userId, String.format("匹配到角色[%s]的秘境[%s]，没有找到对应的UserCreateGroupSecretData，做删除处理", matchUserId, secretId));
			mgr.delete(userId);
			return null;
		}

		GroupSecretData secretData = userCreateGroupSecretData.getGroupSecretData(secretId);
		if (secretData == null) {
			GameLog.error("填充搜索到的秘境信息", userId, String.format("匹配到角色[%s]的秘境[%s]，没有找到对应的GroupSecretData，做删除处理", matchUserId, secretId));
			mgr.delete(userId);
			return null;
		}

		int secretCfgId = secretData.getSecretId();
		GroupSecretResourceTemplate cfg = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		long protectTimeMillis = 0;
		int robDiamondNum = 0;
		if (cfg == null) {
			GameLog.error("填充搜索到的秘境信息", userId, String.format("找不到秘境[%s]对应的配置表GroupSecretResourceTemplate", secretCfgId));
		} else {
			protectTimeMillis = TimeUnit.MINUTES.toMillis(cfg.getProtectTime());
			robDiamondNum = cfg.getRobGold();
		}

		String id = generateCacheSecretId(matchUserId, secretId);
		boolean beat = enemyData.isBeat();
		SecretBaseInfoSynData baseInfo = new SecretBaseInfoSynData(id, secretCfgId, beat, enemyData.getAtkTime(), 0, robDiamondNum, enemyData.getAllRobResValue(), enemyData.getAllRobGEValue(),
				enemyData.getAllRobGSValue());

		if (beat) {// 如果已经打败了
			return new GroupSecretDataSynData(baseInfo, null);
		}

		if (protectTimeMillis > 0) {
			long now = System.currentTimeMillis();
			long atkTime = enemyData.getAtkTime();
			long passTimeMillis = now - atkTime;// 已经流过的时间
			if (passTimeMillis >= protectTimeMillis) {// 超出了时间
				GameLog.error("填充搜索到的秘境信息", userId, String.format("匹配到角色[%s]的秘境[%s]，在规定的攻击时间倒计时中没有打败对手，做删除处理", matchUserId, secretId));
				mgr.delete(userId);
				return null;
			}
		}

		HashMap<Integer, DefendUserInfoSynData> defendUserInfoMap = new HashMap<Integer, DefendUserInfoSynData>();
		GroupSecretHelper.getEnemyTeamInfo(secretData, enemyData, defendUserInfoMap);
		return new GroupSecretDataSynData(baseInfo, new SecretTeamInfoSynData(id, defendUserInfoMap));
	}

	/**
	 * 转换秘境的数据到Proto消息
	 * 
	 * @param data
	 * @param userId
	 * @return
	 */
	public static GroupSecretDataSynData parseGroupSecretData2Msg(GroupSecretData data, String userId) {
		long now = System.currentTimeMillis();
		int secretCfgId = data.getSecretId();// 秘境的模版Id
		GroupSecretResourceTemplate groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GameLog.error("把秘境数据填充成Proto消息", userId, String.format("找不到秘境[%s]对应的配置表GroupSecretResourceTemplate", secretCfgId));
			return null;
		}

		long needTimeMillis = TimeUnit.MINUTES.toMillis(groupSecretResTmp.getNeedTime());// 分钟
		long createTime = data.getCreateTime();
		long passTimeMillis = now - createTime;
		boolean isFinish = passTimeMillis >= needTimeMillis;// 是否已经完成了

		Map<Integer, DefendUserInfoSynData> defendUserInfoMap = new HashMap<Integer, DefendUserInfoSynData>();
		DefendUserInfoData myDefendInfo = GroupSecretHelper.getMyDefendUseInfoData(data, userId, isFinish, defendUserInfoMap);// 自己的驻守信息

		String id = generateCacheSecretId(data.getUserId(), data.getId());

		long changeTeamTime = myDefendInfo.getChangeTeamTime();// 修改阵容时间
		int getRes = myDefendInfo.getProRes() - myDefendInfo.getRobRes();
		int getGE = myDefendInfo.getProGE() - myDefendInfo.getRobGE();
		int getGS = myDefendInfo.getProGS() - myDefendInfo.getRobGS();
		int dropDiamond = myDefendInfo.getDropDiamond();
		if (changeTeamTime > 0) {
			long minutes = TimeUnit.MILLISECONDS.toMinutes((isFinish ? (createTime + needTimeMillis) : now) - changeTeamTime);
			int fighting = myDefendInfo.getFighting();
			getRes += (int) (fighting * groupSecretResTmp.getProductRatio() * minutes);
			getGE += (int) (groupSecretResTmp.getGroupExpRatio() * minutes);
			getGS += (int) (groupSecretResTmp.getGroupSupplyRatio() * minutes);
		}

		SecretBaseInfoSynData base = new SecretBaseInfoSynData(id, secretCfgId, isFinish, data.getCreateTime(), myDefendInfo.getIndex(), dropDiamond, getRes, getGE, getGS);
		return isFinish ? new GroupSecretDataSynData(base, null) : new GroupSecretDataSynData(base, new SecretTeamInfoSynData(id, defendUserInfoMap));
	}

	/**
	 * 生成缓存的秘境Id
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	public static String generateCacheSecretId(String userId, int id) {
		StringBuilder sb = new StringBuilder();
		return sb.append(userId).append("_").append(id).toString();
	}

	/**
	 * 解析秘境的拼接Id到对应的数据
	 * 
	 * @param id
	 * @return
	 */
	public static String[] parseString2UserIdAndSecretId(String id) {
		return HPCUtil.parseStringArray(id, "_");
	}
}