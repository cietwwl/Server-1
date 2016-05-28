package com.rwbase.dao.groupsecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.log.GameLog;
import com.playerdata.PlayerMgr;
import com.playerdata.army.CurAttrData;
import com.playerdata.groupsecret.GroupSecretMatchEnemyDataMgr;
import com.playerdata.groupsecret.UserCreateGroupSecretDataMgr;
import com.playerdata.readonly.AttrMgrIF;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.ItemDataIF;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendUserInfoData;
import com.rwproto.GroupSecretProto.DefendHeroBaseInfo;
import com.rwproto.GroupSecretProto.DefendTeamInfo;
import com.rwproto.GroupSecretProto.DefendUserInfo;
import com.rwproto.GroupSecretProto.GroupSecretCommonRspMsg;
import com.rwproto.GroupSecretProto.GroupSecretIndex;
import com.rwproto.GroupSecretProto.GroupSecretInfo;
import com.rwproto.GroupSecretProto.HeroLeftInfo;
import com.rwproto.GroupSecretProto.MatchSecretInfo;
import com.rwproto.GroupSecretProto.SecretBaseInfo;
import com.rwproto.GroupSecretProto.SecretDropInfo;

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
	 * @param defendUserInfoList
	 * @return
	 */
	public static DefendUserInfoData getMyDefendUseInfoData(GroupSecretData secretData, String userId, boolean isFinish, List<DefendUserInfo> defendUserInfoList) {
		return parseSecretData2NeedTeamInfo(secretData, userId, isFinish, null, defendUserInfoList);
	}

	/**
	 * 获取匹配到的敌人的防守信息
	 * 
	 * @param secretData
	 * @param enemyData
	 * @param defendUserInfoList
	 */
	public static void getEnemyTeamInfo(GroupSecretData secretData, GroupSecretMatchEnemyData enemyData, List<DefendUserInfo> defendUserInfoList) {
		parseSecretData2NeedTeamInfo(secretData, null, false, enemyData, defendUserInfoList);
	}

	/**
	 * 填充敌人的匹配信息
	 * 
	 * @param userId
	 * @return
	 */
	public static MatchSecretInfo.Builder fillMatchSecretInfo(String userId) {
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

		SecretDropInfo.Builder dropInfo = SecretDropInfo.newBuilder();
		dropInfo.setDiamond(robDiamondNum);
		dropInfo.setDropResource(enemyData.getRobRes());
		dropInfo.setGroupExp(enemyData.getRobGE());
		dropInfo.setGroupSupply(enemyData.getRobGS());

		MatchSecretInfo.Builder matchSecretInfo = MatchSecretInfo.newBuilder();
		matchSecretInfo.setId(secretId);
		matchSecretInfo.setSecretCfgId(secretCfgId);
		matchSecretInfo.setIsBeat(enemyData.isBeat());

		if (enemyData.isBeat()) {// 如果已经打败了
			matchSecretInfo.setDropInfo(dropInfo);
			return matchSecretInfo;
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

			matchSecretInfo.setLeftTime((int) TimeUnit.MILLISECONDS.toSeconds(protectTimeMillis - passTimeMillis));
		}

		List<DefendUserInfo> defendUserInfoList = new ArrayList<DefendUserInfo>();
		GroupSecretHelper.getEnemyTeamInfo(secretData, enemyData, defendUserInfoList);
		matchSecretInfo.addAllDefendUserInfo(defendUserInfoList);

		return matchSecretInfo;
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
			List<DefendUserInfo> defendUserInfoList) {
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
				DefendUserInfo.Builder userInfo = DefendUserInfo.newBuilder();
				userInfo.setIndex(GroupSecretIndex.valueOf(e.getKey()));

				DefendTeamInfo.Builder teamInfo = DefendTeamInfo.newBuilder();
				teamInfo.setHeadImageId(readOnlyPlayer.getHeadImage());
				teamInfo.setName(readOnlyPlayer.getUserName());
				teamInfo.setLevel(readOnlyPlayer.getLevel());
				// 法宝信息
				ItemDataIF magic = readOnlyPlayer.getMagic();
				teamInfo.setMagicId(magic.getModelId());
				teamInfo.setMagicLevel(magic.getMagicLevel());
				teamInfo.setUserId(defendUserId);

				Map<String, CurAttrData> teamAttrInfoMap = enemyData == null ? null : enemyData.getTeamAttrInfoMap(e.getKey());

				boolean isHasLife = true;

				int fighting = 0;
				List<String> heroList = value.getHeroList();
				for (int j = 0, heroSize = heroList.size(); j < heroSize; j++) {
					String heroId = heroList.get(j);
					HeroIF hero = readOnlyPlayer.getHeroMgr().getHeroById(heroId);
					if (hero == null) {
						continue;
					}

					fighting += hero.getFighting();
					DefendHeroBaseInfo.Builder heroBaseInfo = DefendHeroBaseInfo.newBuilder();
					heroBaseInfo.setHeadImageId(hero.getHeroCfg().getImageId());
					heroBaseInfo.setLevel(hero.getLevel());
					heroBaseInfo.setStarLevel(hero.getHeroData().getStarLevel());
					heroBaseInfo.setQualityId(hero.getQualityId());
					heroBaseInfo.setIsMainRole(heroId.equals(defendUserId));
					if (teamAttrInfoMap == null) {
						heroBaseInfo.setIsDie(false);
					} else {
						AttrMgrIF attrMgr = hero.getAttrMgr();
						AttrData totalData = attrMgr.getRoleAttrData().getTotalData();
						int life = totalData.getLife();
						int energy = totalData.getEnergy();

						CurAttrData curAttrData = teamAttrInfoMap.get(heroId);
						int leftLife = life;
						int leftEnergy = 0;
						if (curAttrData != null) {
							leftEnergy = curAttrData.getCurEnergy();
							leftLife = curAttrData.getCurLife();
						}

						if (leftLife > 0) {
							HeroLeftInfo.Builder leftInfo = HeroLeftInfo.newBuilder();
							leftInfo.setMaxEnergy(energy);
							leftInfo.setMaxLife(life);
							leftInfo.setEnergy(leftEnergy);
							leftInfo.setLife(leftLife);
							heroBaseInfo.setIsDie(false);
							heroBaseInfo.setHeroLeftInfo(leftInfo);
						} else {
							heroBaseInfo.setIsDie(true);
							isHasLife = false;
						}
					}
					teamInfo.setDefendFighting(fighting);
					teamInfo.addHeroBaseInfo(heroBaseInfo);
				}

				if (isHasLife) {
					userInfo.setIsBeat(false);
					userInfo.setTeamInfo(teamInfo);
				} else {
					userInfo.setIsBeat(true);
				}

				defendUserInfoList.add(userInfo.build());
			}
		}

		return myDefendInfo;
	}

	/**
	 * 转换秘境的数据到Proto消息
	 * 
	 * @param data
	 * @param userId
	 * @return
	 */
	public static GroupSecretInfo.Builder parseGroupSecretData2Msg(GroupSecretData data, String userId) {
		long now = System.currentTimeMillis();
		int secretCfgId = data.getSecretId();// 秘境的模版Id
		GroupSecretResourceTemplate groupSecretResTmp = GroupSecretResourceCfgDAO.getCfgDAO().getGroupSecretResourceTmp(secretCfgId);
		if (groupSecretResTmp == null) {
			GameLog.error("把秘境数据填充成Proto消息", userId, String.format("找不到秘境[%s]对应的配置表GroupSecretResourceTemplate", secretCfgId));
			return null;
		}

		GroupSecretInfo.Builder info = GroupSecretInfo.newBuilder();
		info.setId(data.getId());// 秘境的ID
		info.setSecretCfgId(secretCfgId);// 秘境的模版Id

		long needTimeMillis = TimeUnit.SECONDS.toMillis(groupSecretResTmp.getNeedTime());// 分钟
		long createTime = data.getCreateTime();
		long passTimeMillis = now - createTime;
		boolean isFinish = passTimeMillis >= needTimeMillis;// 是否已经完成了
		info.setIsFinish(isFinish);

		List<DefendUserInfo> defendUserInfoList = new ArrayList<DefendUserInfo>();

		DefendUserInfoData myDefendInfo = GroupSecretHelper.getMyDefendUseInfoData(data, userId, isFinish, defendUserInfoList);// 自己的驻守信息

		SecretDropInfo.Builder dropBuilder = SecretDropInfo.newBuilder();
		if (myDefendInfo != null) {
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

			dropBuilder.setDiamond(myDefendInfo.getDropDiamond());
			dropBuilder.setDropResource(proRes);
			dropBuilder.setGroupExp(proGE);
			dropBuilder.setGroupSupply(proGS);
		} else {
			dropBuilder.setDiamond(0);
			dropBuilder.setDropResource(0);
			dropBuilder.setGroupExp(0);
			dropBuilder.setGroupSupply(0);
		}
		info.setDropInfo(dropBuilder);

		if (!isFinish) {// 还没有完成
			SecretBaseInfo.Builder baseBuilder = SecretBaseInfo.newBuilder();
			baseBuilder.setLeftTime((int) TimeUnit.MILLISECONDS.toSeconds(needTimeMillis - passTimeMillis));
			// 生产速度
			int fighting = myDefendInfo == null ? 0 : myDefendInfo.getFighting();
			baseBuilder.setProductionSpeed((int) (fighting * groupSecretResTmp.getProductRatio() * 60));

			baseBuilder.addAllUserInfo(defendUserInfoList);
			info.setBaseInfo(baseBuilder);
		}

		return info;
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