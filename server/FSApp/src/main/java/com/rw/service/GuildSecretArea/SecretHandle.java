package com.rw.service.GuildSecretArea;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.bm.secretArea.SecretAreaInfoGMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SecretAreaMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.PlayerIF;
import com.rw.service.chat.ChatHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.guildSecretArea.SecretAreaBattleInfo;
import com.rwbase.dao.guildSecretArea.SecretAreaDefRecord;
import com.rwbase.dao.guildSecretArea.SecretAreaInfo;
import com.rwbase.dao.guildSecretArea.SecretAreaUserInfo;
import com.rwbase.dao.guildSecretArea.SecretAreaUserRecord;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;
import com.rwbase.dao.guildSecretArea.projo.SecretArmy;
import com.rwbase.dao.guildSecretArea.projo.SecretUserChange;
import com.rwbase.dao.guildSecretArea.projo.SecretUserSource;
import com.rwbase.dao.guildSecretArea.projo.SourceType;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.secretArea.SecretAreaCfgDAO;
import com.rwbase.dao.secretArea.SecretBuyCoinCfgDAO;
import com.rwbase.dao.secretArea.SecretFindExpendCfgDAO;
import com.rwbase.dao.secretArea.pojo.SecretAreaCfg;
import com.rwbase.dao.secretArea.pojo.SecretBuyCoinCfg;
import com.rwbase.dao.secretArea.pojo.SecretFindExpendCfg;
import com.rwbase.dao.tower.pojo.TowerHeroChange;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.DataSynProtos.SynData;
import com.rwproto.SecretAreaProtos.EAttrackType;
import com.rwproto.SecretAreaProtos.EMterialType;
import com.rwproto.SecretAreaProtos.EReqSecretType;
import com.rwproto.SecretAreaProtos.ESuccessType;
import com.rwproto.SecretAreaProtos.MsgSecretRequest;
import com.rwproto.SecretAreaProtos.MsgSecretResponse;
import com.rwproto.SecretAreaProtos.TagChat;
import com.rwproto.SecretAreaProtos.TagFightRoleInfo;
import com.rwproto.SecretAreaProtos.TagPlayerHeroInfo;
import com.rwproto.TowerServiceProtos.TagTowerHeroChange;

public class SecretHandle {
	private static SecretHandle instance = new SecretHandle();

	private SecretHandle() {
		// initMap();
	};

	public static SecretHandle getInstance() {
		return instance;
	}

	/****
	 * 请求玩家秘境基础信息
	 * ****/
	public ByteString getSecretMember(Player player, MsgSecretRequest msgHeroRequest) {// 获得人物秘境基本信息
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_PLAYER_BASE);
		player.getSecretMgr().synData();// 同步所有数据
		return res.build().toByteString();
	}

	/****
	 * //请求盟友面板秘境数据
	 * ****/
	public ByteString getGuildSecretArea(Player player, MsgSecretRequest msgHeroRequest) {
		String secretId = msgHeroRequest.getSecretId();// 请求秘境id
		// 请求盟友面板秘境数据
		SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);// 根据id查找到对应秘境信息
		return updateGuildSecret(player, areaInfo);
	}

	/**
	 * 请求秘境信息返回（只是盟友秘境用到 ：请求返回,状态更新返回 ）
	 * 
	 * @param player 玩家
	 * @param areaInfo 请求的秘境信息
	 * @return
	 */
	private ByteString updateGuildSecret(Player player, SecretAreaInfo areaInfo) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_INFO);
		if (areaInfo == null) {// 已过期
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}
		if (areaInfo.getClosed()) {// 关闭的秘境
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}
		if (areaInfo.getSecretArmyMap().size() <= 0) {// 不合理的备用秘境
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}
		if (System.currentTimeMillis() - areaInfo.getEndTime() >= 1000 * 60 * 5) {// 剩余不足5分钟的秘境
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}
		try {
			res.setResultType(ESuccessType.SUCCESS);// 成功
			// 把秘境信息通过protobuf传到消息号接受（客户端对应独立转换）
			SynData.Builder enemySecretInfoClient = ClientDataSynMgr.transferToClientData(areaInfo);// 转为json格式数据
			res.setSecretAreaInfo(enemySecretInfoClient);// 写入proto传给客户端
		} catch (Exception e) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "服务器繁忙，请稍后重试");
			res.setResultType(ESuccessType.FAIL);
		}
		return res.build().toByteString();
	}

	/****
	 * //添加新秘境
	 * ****/
	public ByteString addSecretArea(Player player, MsgSecretRequest msgHeroRequest) {// 玩家所有秘境数据
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_ADD_AREA);
		ESecretType secretType = ESecretType.valueOf(msgHeroRequest.getSecretType());// 秘境类型
		TagPlayerHeroInfo tagPlayerHeroList = msgHeroRequest.getUpdatePlayerHeroInfo();// 改变的驻守佣兵

		SecretAreaUserInfo userInfo = player.getSecretMgr().getSecretAreaUserInfo();// 获得玩家秘境基本信息
		PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());// vip配置
		int totalSecretCount = privilegeCfg.getSecretCopyCount();// 当前可创建秘境数量
		if (userInfo.getCurrentAreaList().size() >= totalSecretCount) {// 是否可创建秘境
			res.setResultType(ESuccessType.FAIL);
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "探索秘境已达上限");
			return res.build().toByteString();
		}
		SecretAreaInfo newSecret = addSecretAreaInfo(player, secretType, tagPlayerHeroList.getHeroIdListList());// 添加秘境
		SecretAreaInfo oldSecret = getClosedSecretInfo(userInfo);// 获取可重复利用的秘境数据表（已到时秘境重复利用）
		if (oldSecret == null) {
			SecretAreaInfoGMgr.getInstance().add(player, newSecret);// 玩家添加秘境更新通知，及保存数据库
			userInfo.getCurrentAreaList().add(newSecret.getId());
		} else {// 重复利用表
			userInfo.getOwnAreaIdList().remove(oldSecret.getId());
			updateSecretInfo(oldSecret, newSecret);// 旧表更新为新表
			SecretAreaInfoGMgr.getInstance().add(player, oldSecret);// 更新后 更新表和刷新到客户端
			userInfo.getCurrentAreaList().add(newSecret.getId());// 玩家秘境基本信息记录
		}

		player.getSecretMgr().updateAreaUserInfo();// 更新玩家基本信息到数据库 及通知客户端
		res.setResultType(ESuccessType.SUCCESS);
		return res.build().toByteString();
	}

	private SecretAreaInfo getClosedSecretInfo(SecretAreaUserInfo userInfo) {// 获得已经关闭的秘境
		List<String> idList = userInfo.getOwnAreaIdList();// 获得重复利用的秘境id
		for (int i = 0; i < idList.size(); i++) {
			String id = idList.get(i);
			SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(id);// 获取对应id秘境信息
			if (areaInfo == null)
				continue;
			if (areaInfo.getClosed()) {// 是无用秘境
				return areaInfo;// 返回可利用秘境信息表
			}
		}
		return null;
	}

	// 老表更新为最新秘境数据
	private void updateSecretInfo(SecretAreaInfo oldSecret, SecretAreaInfo newSecret) {
		oldSecret.setId(newSecret.getId());
		oldSecret.setOwnerId(newSecret.getOwnerId());
		oldSecret.setSecretArmyMap(newSecret.getSecretArmyMap());
		oldSecret.setBegainTime(newSecret.getBegainTime());
		oldSecret.seteAttrackType(EAttrackType.DEFEND);
		oldSecret.setClosed(newSecret.getClosed());
		oldSecret.setProtect(newSecret.getProtect());
		oldSecret.setEndTime(newSecret.getEndTime());
		oldSecret.setFightTime(newSecret.getFightTime());
		oldSecret.setSecretType(newSecret.getSecretType());
	}

	/**
	 * 添加秘境
	 * 
	 * @param secretType 秘境类型
	 * @param heroIdList 派遣驻守佣兵
	 */
	private SecretAreaInfo addSecretAreaInfo(Player player, ESecretType secretType, List<String> heroIdList) {
		SecretAreaInfo secretAreaInfo = new SecretAreaInfo();
		secretAreaInfo.setId(UUID.randomUUID().toString());// 随即给一个uuid
		secretAreaInfo.setOwnerId(player.getUserId());// 创建秘境人物id
		secretAreaInfo.setSecretArmyMap(new ConcurrentHashMap<String, SecretArmy>());// 初始驻守佣兵
		ConcurrentHashMap<String, SecretArmy> armyMap = secretAreaInfo.getSecretArmyMap();// 驻守佣兵引用
		secretAreaInfo.setBegainTime(System.currentTimeMillis());// 创建时间
		secretAreaInfo.seteAttrackType(EAttrackType.DEFEND);// 自己
		secretAreaInfo.setClosed(false);// 是否关闭
		secretAreaInfo.setProtect(false);// 保护状态
		SecretArmy secretArmy = new SecretArmy();
		updateArmy(secretArmy, secretAreaInfo.getId(), player, heroIdList);// 刷新驻守佣兵信息
		armyMap.put(player.getUserId(), secretArmy);// 【对应人物id,驻守信息]
		long endTime = 0;
		switch (secretType) {// 秘境开采总时间
		case GOLD_TYPE_ONE:
		case EXP_TYPE_ONE:
		case STONG_TYPE_ONE:
			endTime = System.currentTimeMillis() + 60 * 60 * 1000;// 1小时
			break;
		case GOLD_TYPE_THREE:
		case EXP_TYPE_THREE:
		case STONG_TYPE_THREE:
			endTime = System.currentTimeMillis() + 3 * 60 * 60 * 1000;// 3小时
			break;
		case GOLD_TYPE_TEN:
		case EXP_TYPE_TEN:
		case STONG_TYPE_TEN:
			endTime = System.currentTimeMillis() + 10 * 60 * 60 * 1000;// 10小时
			break;
		}
		secretAreaInfo.setEndTime(endTime);// 结束时间
		secretAreaInfo.setFightTime(0);// 战斗时间
		secretAreaInfo.setSecretType(secretType);// 秘境类型
		return secretAreaInfo;
	}

	/**
	 * 刷新驻守佣兵信息
	 * 
	 * @param secretArmy 刷新对象
	 * @param secretId //对应秘境id
	 * @param player
	 * @param heroIdList //驻守佣兵uuid
	 */
	private void updateArmy(SecretArmy secretArmy, String secretId, Player player, List<String> heroIdList) {
		secretArmy.setSecretId(secretId);
		secretArmy.setUserId(player.getUserId());
		secretArmy.setHeroIdList(heroIdList);
		int forceNum = getFightByPlayer(player.getUserId(), heroIdList);// 获取主角和佣兵的战斗力
		secretArmy.setBattleForce(forceNum);
		secretArmy.setSourceChangeTime(System.currentTimeMillis());// 当前驻守改变时间
		secretArmy.setSourceNum(0);// 获取资源
		secretArmy.setAllDead(false);// 是否全部死亡

	}

	/**
	 * // 获取主角人物加传入佣兵的总战斗力
	 * 
	 * @param playerId 玩家userId
	 * @param heroIdIdList 佣兵UUId集
	 * @return 总战斗力
	 */
	private int getFightByPlayer(String playerId, List<String> heroIdIdList) {
		int totalFight = 0;
		Player targetPlayer = PlayerMgr.getInstance().find(playerId);// 获取玩家数据

		if (targetPlayer == null) {
			return 0;
		}
		totalFight += targetPlayer.getMainRoleHero().getFighting();// 添加主角战斗力
		if (heroIdIdList != null) {
			for (int i = 0; i < heroIdIdList.size(); i++) {
				String heroId = heroIdIdList.get(i);
				HeroIF hero = targetPlayer.getHeroMgr().getHeroById(heroId);// 获取对应uuid的佣兵
				if (hero == null) {
					continue;
				}
				totalFight += hero.getFighting();// 添加佣兵战斗力
			}
		}
		return totalFight;// 返回总战斗力
	}

	/**
	 * 转为服务端玩家血量能量变化记录
	 * 
	 * @param userChange 玩家血量能量变化记录
	 * @param changeList 客户端传来的血量变化
	 * @return 更新后的血量变化
	 */
	private SecretUserChange updatePlayerChange(SecretUserChange userChange, List<TowerHeroChange> changeList) {// 玩家自己血量更新保存;
		List<TowerHeroChange> oldChangeList = null;
		oldChangeList = userChange.getChangeList();// 老的血量改变记录

		// 无改变记录 直接构建
		if (oldChangeList == null) {// 无则 直接构建
			userChange.setUserId(userChange.getUserId());
			userChange.setChangeList(changeList);
			return userChange;
		}
		// 有改变数据 则添加没有的记录，修改有的记录
		for (int i = 0; i < changeList.size(); i++) {// 原来血量数据更新
			TowerHeroChange newChange = changeList.get(i);
			boolean isHave = false;
			for (int j = 0; j < oldChangeList.size(); j++) {// 是否已保存过血量
				TowerHeroChange oldChange = oldChangeList.get(j);
				if (newChange.getRoleId().equals(oldChange.getRoleId())) {// 有对应记录更新为最新
					oldChange.setReduceLife(newChange.getReduceLife());
					oldChange.setReduceEnegy(newChange.getReduceEnegy());
					// oldChange.setIsDead(newChange.getIsDead());
					isHave = true;
					break;
				}
			}
			if (!isHave) {// 无则 添加
				oldChangeList.add(newChange);
			}
		}
		return userChange;
	}

	/**
	 * //把客户端血量变化数据类 转为服务端的血量变化类
	 * 
	 * @param changeList protobuf客户端 血量变化数据
	 * @return 服务端保存累
	 */
	private List<TowerHeroChange> returnTableChangeList(List<TagTowerHeroChange> changeList) {
		List<TowerHeroChange> tableHeroChangeList = new ArrayList<TowerHeroChange>();
		for (int i = 0; i < changeList.size(); i++) {
			TowerHeroChange tableHeroChange = new TowerHeroChange();
			tableHeroChange.setRoleId(changeList.get(i).getUserId());// uuid
			tableHeroChange.setReduceLife(changeList.get(i).getReduceLife());// 当前血量
			tableHeroChange.setReduceEnegy(changeList.get(i).getReduceEnegy());// 当前能量
			// tableHeroChange.setIsDead(changeList.get(i).getIsDead());// 是否死亡
			tableHeroChangeList.add(tableHeroChange);
		}
		return tableHeroChangeList;
	}

	/**
	 * 更新驻守佣兵信息
	 * 
	 * @param player 玩家
	 * @param msgHeroRequest 客户端传来信息
	 * @return 返回客户端
	 */
	public ByteString updatePlayerHeroList(Player player, MsgSecretRequest msgHeroRequest) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.UPDATE_SECRET_ROLE_INFO);
		String secretId = msgHeroRequest.getSecretId();
		String playerId = player.getUserId();

		SecretAreaInfo secretInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);
		if (secretInfo == null) {// 无秘境信息 可能已被回收重复利用了
			return null;
		}
		TagPlayerHeroInfo tagPlayerHeroList = msgHeroRequest.getUpdatePlayerHeroInfo();// 改变的驻守佣兵
		SecretArmy secretArmy = secretInfo.getSecretArmyMap().get(playerId);// 对应userId的玩家驻守信息
		List<String> heroIdList = tagPlayerHeroList.getHeroIdListList();// 更新后的驻守佣兵uuId
		if (secretArmy != null) {// 拥有自己的驻守信息
			player.getSecretMgr().updateArmySource(secretInfo, playerId);// 调整前之前资源更新
			updateArmy(secretArmy, secretInfo.getId(), player, heroIdList); // 刷新驻守佣兵信息
			SecretAreaInfoGMgr.getInstance().update(player, secretInfo);// 更新保存秘境修改
			return res.build().toByteString();
		} else {// @@ 盟友驻守（已经有这个秘境了 但没自己驻守信息 可以判断为驻守盟友的秘境！）
			SecretAreaUserInfo userInfo = player.getSecretMgr().getSecretAreaUserInfo();// 玩家秘境基本信息
			PrivilegeCfg privilegeCfg = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());// vip配置
			int totalSecretCount = privilegeCfg.getSecretCopyCount();
			if (userInfo.getCurrentAreaList().size() >= totalSecretCount) {
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "探索秘境已达上限");
				return null;
			}
			if (secretInfo.getSecretArmyMap().size() >= totalSecretCount) {// 玩家同步驻守情况加锁
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "秘境驻守人员已满");
				return null;
			}
			SecretArmy newArmy = new SecretArmy();
			updateArmy(newArmy, secretInfo.getId(), player, heroIdList);// 构建玩家驻守信息
			secretInfo.getSecretArmyMap().put(playerId, newArmy);// 更新到秘境信息里
			if (userInfo.getCurrentAreaList().indexOf(secretInfo.getId()) == -1) {
				userInfo.getCurrentAreaList().add(secretInfo.getId());// 加入
			}
			player.getSecretMgr().updateAreaUserInfo();// 更新玩家有秘境状态
			SecretAreaInfoGMgr.getInstance().update(player, secretInfo);// 刷新秘境信息
			player.getSecretMgr().updateSecretRank(secretInfo);// 秘境排行更新
			return updateGuildSecret(player, secretInfo);// （特别）盟友秘境 protobuf操作数据改变
		}
	}

	/****
	 * 
	 * 领取相关矿点奖励
	 * ****/
	public ByteString getGift(Player player, MsgSecretRequest msg) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_SECRET_GIFT);
		String userRecordId = msg.getSecretId();// 奖励的id
		SecretAreaUserRecord userRecord = player.getSecretMgr().getAreaUserRecord(userRecordId);// 获取奖励信息
		if (userRecord != null) {
			List<SecretUserSource> sourceNumList = userRecord.getUserSourceList();// 玩家奖励资源信息
			boolean isSucess = addTypeNum(player, sourceNumList);// 获得奖励
			if (isSucess) {// 成功领取则删除奖励
				player.getSecretMgr().delectAreaUserRecord(userRecordId);
			}

		}
		return res.build().toByteString();
	}

	/**
	 * 获得奖励
	 * 
	 * @param m_Player
	 * @param userSourceList 玩家奖励集合
	 * @return 成功或失败
	 */
	private boolean addTypeNum(Player m_Player, List<SecretUserSource> userSourceList) {// 获得奖励;
		if (userSourceList == null)
			return false;
		for (SecretUserSource userSouce : userSourceList) {
			SourceType sourceType = null;
			List<SourceType> sourceList = userSouce.getSourceList();// 奖励
			for (int i = 0; i < sourceList.size(); i++) {
				sourceType = sourceList.get(i);
				int type = sourceType.getSecretType().getNumber();// 奖励类型
				int addNum = sourceType.getNum();// 奖励数量
				if (addNum <= 0) {
					continue;
				}
				if (type > 0) {
					// 帮派材料
					if (sourceType.getMaterialType() == EMterialType.guildMaterial_VALUE) {
						m_Player.getGuildUserMgr().addGuildMaterial(addNum);// 帮派材料添加
						m_Player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "获得帮派材料" + " +" + addNum);
						continue;
					}

					// 秘境资源
					SecretAreaCfg secretCfg = SecretAreaCfgDAO.getInstance().getSecretCfg(type);
					if (secretCfg == null) {
						continue;
					}
					String itemId = secretCfg.getGiftId();
					m_Player.getItemBagMgr().addItem(Integer.valueOf(itemId), addNum);// 背包添加物品
					m_Player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "获得 " + itemId + " +" + addNum);

				} else {// 特殊钻石
					int marterType = sourceType.getMaterialType();
					if (marterType == EMterialType.strone_VALUE) {
						m_Player.getItemBagMgr().addItem(eSpecialItemId.Gold.getValue(), addNum);// 金币和钻石可以通过此接口 添加
						m_Player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "获得 钻石" + " +" + addNum);
					}
				}
			}
		}
		return true;
	}

	/****
	 * 
	 * 查找敌方对象
	 * ****/
	public ByteString findEnemy(Player player, MsgSecretRequest msgHeroRequest) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_FIND_ENEMY);
		SecretAreaInfo enemySecretInfo = SecretAreaInfoGMgr.getInstance().findSecretArea(player);// 查找的敌方秘境信息
		boolean isSucess = false;
		if (enemySecretInfo != null) {
			isSucess = true;
			// 清理之前战斗信息
			player.getSecretMgr().restBattleInfo();
			SecretAreaUserInfo secretUser = player.getSecretMgr().getSecretAreaUserInfo();
			SecretFindExpendCfg findCfg = SecretFindExpendCfgDAO.getInstance().getSecretCfg(secretUser.getAttrackCount() + 1);// 查找敌方消费
			if (findCfg != null) {// 扣除消费
				player.getUserGameDataMgr().addCoin(-findCfg.getUseCoin());
			}

			SynData.Builder enemySecretInfoClient;
			try {
				enemySecretInfoClient = ClientDataSynMgr.transferToClientData(enemySecretInfo);// 敌方信息转为 protobuf传给客户端（同理于盟友信息）
				res.setSecretAreaInfo(enemySecretInfoClient);
				res.setEAttrackType(EAttrackType.Attack);// 代表敌方
			} catch (Exception e) {
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "服务器繁忙，请稍后重试");
				res.setResultType(ESuccessType.FAIL);
				isSucess = false;
			}
		} else {
			isSucess = false;
			// player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "找不到可掠夺秘境，掠夺搜索秘境费用返回");
			res.setResultType(ESuccessType.FAIL);// 查找失败
		}
		return res.build().toByteString();
	}

	/****
	 * 获取驻守玩家具体信息
	 * ****/
	public ByteString getEnenmyInfo(Player player, MsgSecretRequest msgHeroRequest) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_ROLE_INFO);
		String targetId = msgHeroRequest.getUserId();// 敌方人物数据
		String secretId = msgHeroRequest.getSecretId();// 敌方人物数据

		SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);// 对应秘境信息
		ConcurrentHashMap<String, SecretArmy> armyMap = areaInfo.getSecretArmyMap();// 驻守信息
		List<String> heroIdList = null;
		if (armyMap.containsKey(targetId)) {
			heroIdList = armyMap.get(targetId).getHeroIdList();// 驻守佣兵
		}
		EAttrackType eAttrackType = msgHeroRequest.getEAttrackType();// 驻守类型（敌方 盟友 自己）
		ArmyInfo tagFightInfo = switchRoleInfo(player, targetId, eAttrackType, heroIdList);// 构建驻守的玩家具体信息 eAttrackType自己和对方人物数据
		try {
			res.setArmyInfo(tagFightInfo.toJson());// 玩家具体信息 这个特别大！！！
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.setEAttrackType(eAttrackType);
		return res.build().toByteString();
	}

	/**
	 * 构建驻守的玩家具体信息
	 * 
	 * @param player 玩家
	 * @param targetPlayerId 驻守玩家userId
	 * @param eAttrackType自己和对方人物数据
	 * @param heroIdList 驻守的佣兵
	 * @return 具体驻守的玩家信息
	 */
	private ArmyInfo switchRoleInfo(Player player, String targetPlayerId, EAttrackType eAttrackType, List<String> heroIdList) {
		SecretAreaBattleInfo battleInfo = player.getSecretMgr().getAreaBattleInfo();// 战斗血量变化信息
		if (battleInfo == null)
			return null;
		TagFightRoleInfo.Builder tagRoleInfo = TagFightRoleInfo.newBuilder();
		PlayerIF targetPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(targetPlayerId);// 对应userId 玩家
		List<TowerHeroChange> heroChangeList = null;// 血量变化
		if (player.getUserId().equals(targetPlayerId)) {// 玩家自己血量变化数据
			heroChangeList = battleInfo.getPlayerChange().getChangeList();
		} else {// 敌人血量变化数据
			SecretUserChange enemyChang = getEnemyChangeById(battleInfo, targetPlayerId);
			if (enemyChang != null)
				heroChangeList = enemyChang.getChangeList();

		}
		ArmyInfo enemyInfo = getSecretEnemy(targetPlayer, heroChangeList, heroIdList);// 人物及佣兵相关属性转化保存血量
		// tagRoleInfo.get
		List<TowerHeroChange> palyerChangeMap = battleInfo.getPlayerChange().getChangeList();// 玩家自己血量变化
		setTagChangeList(tagRoleInfo, palyerChangeMap);// 服务端血量 转为客户端血量数据

		return enemyInfo;
	}

	/**
	 * 服务端血量 转为客户端血量数据
	 * 
	 * @param tagRoleInfo 血量数据保存类
	 * @param palyerChangeMap 服务端血量保存
	 * @return 保存在TagFightRoleInfo里传递
	 */
	private TagFightRoleInfo setTagChangeList(TagFightRoleInfo.Builder tagRoleInfo, List<TowerHeroChange> palyerChangeMap) {
		List<TagTowerHeroChange> tagPlayerChangeList = new ArrayList<TagTowerHeroChange>();
		if (palyerChangeMap != null) {
			for (int i = 0; i < palyerChangeMap.size(); i++) {
				TagTowerHeroChange.Builder heroChange = TagTowerHeroChange.newBuilder();
				TowerHeroChange towerHeroChange = palyerChangeMap.get(i);
				heroChange.setUserId(towerHeroChange.getRoleId());
				heroChange.setReduceLife(towerHeroChange.getReduceLife());
				heroChange.setReduceEnegy(towerHeroChange.getReduceEnegy());
				heroChange.setIsDead(towerHeroChange.getIsDead());
				tagPlayerChangeList.add(heroChange.build());
			}
			tagRoleInfo.addAllHeroChangeList(tagPlayerChangeList);
		}
		return tagRoleInfo.build();
	}

	/**
	 * // 人物及佣兵相关属性转化保存血量
	 * 
	 * @param targetPlayer 更新的玩家对象
	 * @param heroChangeList 之前保存的血量
	 * @param heroIdList 驻守的佣兵uuid
	 * @return 具体人物信息（包含血量能量变化）
	 */
	private ArmyInfo getSecretEnemy(PlayerIF targetPlayer, List<TowerHeroChange> heroChangeList, List<String> heroIdList) {// player
																															// 信息转为TagTowerEnemyInfo
		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(targetPlayer.getTableUserOther().getUserId(), heroIdList);// 构建人物驻守具体信息

		// 佣兵
		List<ArmyHero> heroList = armyInfo.getHeroList();
		for (ArmyHero hero : heroList) {
			RoleBaseInfo heroBase = hero.getRoleBaseInfo();
			TowerHeroChange heroChang = getHeroChangeById(String.valueOf(heroBase.getModeId()), heroChangeList);// 对应id的血量变化数据
			switchHeroData(targetPlayer, hero, heroChang);// 更新佣兵血量变化
		}

		// 主角
		ArmyHero palyerArmy = armyInfo.getPlayer();
		CurAttrData playerCurrAttrData = palyerArmy.getCurAttrData();// 获取当前血量变化数据
		if (playerCurrAttrData == null) {// 无生成过
			playerCurrAttrData = new CurAttrData();
			palyerArmy.setCurAttrData(playerCurrAttrData);// 保存
		}

		TowerHeroChange roleChang = getHeroChangeById(targetPlayer.getTableUser().getUserId(), heroChangeList);// 查找对应id的血量变化数据
		updateAttrData(playerCurrAttrData, roleChang);// 更新主角血量变化

		return armyInfo;
	}

	// 查找对应id的血量变化数据
	private TowerHeroChange getHeroChangeById(String id, List<TowerHeroChange> heroChangeList) {
		if (heroChangeList == null) {
			return null;
		}
		for (int i = 0; i < heroChangeList.size(); i++) {
			if (id.equals(heroChangeList.get(i).getRoleId())) {
				return heroChangeList.get(i);
			}
		}
		return null;
	}

	/**
	 * 更新佣兵血量变化
	 * 
	 * @param targetPlayer 更新的玩家对象
	 * @param aymyHero 更新的佣兵
	 * @param heroChange 当前佣兵改变的血量
	 * @return
	 */
	private ArmyHero switchHeroData(PlayerIF targetPlayer, ArmyHero aymyHero, TowerHeroChange heroChange)// 佣兵数据
	{
		// 改变的血量
		CurAttrData currAttrData = aymyHero.getCurAttrData();// 总属性
		if (currAttrData == null) {// 之前血量改变
			currAttrData = new CurAttrData();
			aymyHero.setCurAttrData(currAttrData);
		}
		if (heroChange != null) {// 有老数据血量变化
			updateAttrData(currAttrData, heroChange);
		} else {// 默认满血 0能量
			currAttrData.setCurEnergy(0);
			currAttrData.setCurLife(aymyHero.getAttrData().getLife());
		}

		return aymyHero;
	}

	// 更新血量变化到人物血量变化属性
	private void updateAttrData(CurAttrData currAttrData, TowerHeroChange heroChange) {
		if (currAttrData == null)
			return;
		if (heroChange != null) {
			currAttrData.setCurEnergy(heroChange.getReduceEnegy());
			currAttrData.setCurLife(heroChange.getReduceLife());
		}
		// else {
		// currAttrData.setCurEnergy(0);
		// currAttrData.setCurLife(totalAttrData.getLife());
		// }

	}

	/****
	 * 
	 * 请求开始战斗
	 * ****/
	public ByteString startFightSecret(Player player, MsgSecretRequest req) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_BEGAIN_FIGHT);
		String secretId = req.getSecretId();// 挑战的秘境
		SecretAreaUserInfo areaUserInfo = player.getSecretMgr().getSecretAreaUserInfo();

		SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);
		ESecretType type = areaInfo.getSecretType();
		SecretAreaCfg secretCfg = SecretAreaCfgDAO.getInstance().getSecretCfg(type.getNumber());
		if (areaInfo.getRobcount() >= secretCfg.getRobCount()) {// 秘境可以被掠夺次数上线
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "掠夺次数已达上线");
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}

		// 消耗秘境币
		if (areaUserInfo.getSecretKey() == SecretAreaMgr.SECRET_KEY_RECOVER_LIMIT) {// 触发回复密钥时间
			// 下点恢复时间
			long endTime = areaUserInfo.getKeyUseTime();
			if (endTime < System.currentTimeMillis()) {// 结束时间
				areaUserInfo.setKeyUseTime((System.currentTimeMillis() + SecretAreaMgr.SecretkeyTime));// 更新密钥恢复时间
			}
		}

		// 挑战消耗密钥
		int newKeyNum = areaUserInfo.getSecretKey() - SecretAreaMgr.KEY_COST_PER_FIGHT;
		newKeyNum = newKeyNum < 0 ? 0 : newKeyNum;
		areaUserInfo.setSecretKey(newKeyNum);
		// 刷新秘境被进攻次数
		int attrackCount = areaUserInfo.getAttrackCount();
		attrackCount++;
		areaUserInfo.setAttrackCount(attrackCount);
		player.getSecretMgr().updateAreaUserInfo();

		// 进攻后的保护时间
		areaInfo.setFightTime(System.currentTimeMillis() + secretCfg.getProtectTime() * 60 * 1000);// 保护结束时间

		// 同步
		player.getSecretMgr().updateAreaUserInfo();
		SecretAreaInfoGMgr.getInstance().update(player, areaInfo);
		res.setResultType(ESuccessType.SUCCESS);
		return res.build().toByteString();
	}

	/****
	 * 
	 * 更新战斗后数据
	 * ****/
	public ByteString endFightSecret(Player player, MsgSecretRequest req) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_END_FIGHT);
		String secretId = req.getSecretId();// 挑战的秘境
		String enemyId = req.getUserId();// 挑战的敌方userId
		TagFightRoleInfo roleHeroChange = req.getTagFightRoleInfo();
		List<TagTowerHeroChange> playerChangeList = roleHeroChange.getHeroChangeListList();// 当局挑战的 玩家的血量改变
		List<TagTowerHeroChange> enemyChangeList = req.getEnemyHeroChangeListList();// 敌方数据改变
		List<TowerHeroChange> playerList = returnTableChangeList(playerChangeList);// 玩家客户端血量变化类 转为服务端血量变化类
		List<TowerHeroChange> enemyList = returnTableChangeList(enemyChangeList);// 敌方客户端血量变化类 转为服务端血量变化类
		res.setIsWin(req.getIsWin());// 胜利或失败
		res.addAllEnemyHeroChangeList(enemyChangeList);// 原样返回（可除去）
		// 更新战斗后数据
		SecretAreaBattleInfo battleInfo = player.getSecretMgr().getAreaBattleInfo();// 战斗信息
		SecretUserChange playerUserChange = updatePlayerChange(battleInfo.getPlayerChange(), playerList);// 对应战斗信息保存的 玩家血量变化
		SecretUserChange enemyUserChange = getEnemyChangeById(battleInfo, enemyId);// 对应战斗信息保存的 敌方血量变化
		if (enemyUserChange == null) {// 无数据则构建
			enemyUserChange = new SecretUserChange();
			battleInfo.getEnemyChangeList().add(enemyUserChange);
			enemyUserChange.setUserId(enemyId);
			enemyUserChange.setChangeList(enemyList);
		} else {// 有数据则更新
			updatePlayerChange(enemyUserChange, enemyList);
		}
		player.getSecretMgr().BattleInfoToClient();// 更新保存发送

		// 挑战后 结余，记录生成，奖励生成
		SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);
		if (areaInfo == null) {// 已无数据
			return res.build().toByteString();
		}

		ESecretType type = areaInfo.getSecretType();
		ConcurrentHashMap<String, SecretArmy> armyMap = areaInfo.getSecretArmyMap();// 驻守信息
		SecretArmy fightArmy = armyMap.get(enemyId);// 对应挑战的驻守点
		SecretAreaCfg secretCfg = SecretAreaCfgDAO.getInstance().getSecretCfg(type.getNumber());
		int getTypeNum = 0;
		if (secretCfg != null) {
			getTypeNum = fightArmy.getSourceNum() * secretCfg.getRobCount() / 10000;// //掠夺资源数量@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@无
		}

		SecretAreaDefRecord oldBattle = player.getSecretMgr().getSecretRecordBySecretID(secretId);// 同一秘境攻打则更新相关记录
		SecretAreaDefRecord newDefRecord = new SecretAreaDefRecord();// 掠夺记录信息生成
		newDefRecord.setId(UUID.randomUUID().toString());
		newDefRecord.setSecretId(secretId);
		newDefRecord.setUserId(enemyId);
		newDefRecord.setAttrackUserId(player.getUserId());
		newDefRecord.setAttrackTime(String.valueOf(System.currentTimeMillis()));
		newDefRecord.setAttrackCount(1);
		Boolean isWin = req.getIsWin();
		newDefRecord.setIsWin(isWin ? 1 : 0);
		newDefRecord.setSecretType(type);
		newDefRecord.setAttrackUserName(player.getUserName());
		newDefRecord.setKeyNum(1);
		newDefRecord.setRegiondName("无");
		// newDefRecord.setGuildName(player.getGuildUserMgr().getGuildName());

		// 秘境奖励
		List<SourceType> defRecordList = new ArrayList<SourceType>();// 记录奖励信息
		SourceType source = new SourceType();// 默认会掠夺钻石
		source.setMaterialType(EMterialType.strone_VALUE);// 钻石
		source.setSecretType(ESecretType.EXP_TYPE_ONE);// 无用 只是使它不为空
		source.setNum(secretCfg.getRobGold());// 掠夺的钻石奖励数量（ 配置 )
		defRecordList.add(source);
		SourceType sourceArea = new SourceType();
		sourceArea.setMaterialType(getMaterialBySecretType(type).getNumber());// 掠夺资源类型
		sourceArea.setSecretType(type);// 秘境类型
		sourceArea.setNum(getTypeNum);// 被掠夺数量
		defRecordList.add(sourceArea);
		newDefRecord.setSourceNumList(defRecordList);// 被掠夺的资源集
		updateDefendRecord(enemyId, oldBattle, newDefRecord);// 有同一场战斗的记录 则刷新相关记录
		if (req.getIsAll()) {// 所有敌人全部死去（3个驻点情况）
			// 更新奖励获取信息
			SecretAreaUserRecord userRecord = new SecretAreaUserRecord();
			userRecord.setUserId(player.getUserId());
			userRecord.setId(secretId);
			userRecord.setGetGiftTime("" + System.currentTimeMillis());
			userRecord.setSecretType(null);// 敌方类型
			userRecord.setStatus(0);
			userRecord.setSecretType(type);
			List<SecretUserSource> userSourceList = new ArrayList<SecretUserSource>();// 资源奖励记录信息
			if (secretCfg != null) {
				for (SecretArmy army : armyMap.values()) {// 驻点集
					SecretUserSource userSource = new SecretUserSource();
					List<SourceType> sourceList = new ArrayList<SourceType>();
					// 被掠夺秘境资源
					int lostNum = army.getSourceNum() * secretCfg.getRobCount() / 10000;// 万比配置
					lostNum = lostNum > army.getSourceNum() ? army.getSourceNum() : lostNum;
					int currNum = army.getSourceNum() - lostNum;
					army.setSourceNum(currNum);
					SourceType getSourceArea = new SourceType();// 生成资源类保存
					getSourceArea.setMaterialType(getMaterialBySecretType(type).getNumber());
					getSourceArea.setSecretType(type);
					getSourceArea.setNum(lostNum);
					sourceList.add(getSourceArea);

					// 被掠夺帮派材料资源
					int lostMaterNum = army.getGuildMaterial() * secretCfg.getRobCount() / 10000;
					lostMaterNum = lostMaterNum > army.getGuildMaterial() ? army.getGuildMaterial() : lostMaterNum;
					int currMaterNum = army.getGuildMaterial() - lostMaterNum;
					army.setGuildMaterial(currMaterNum);
					SourceType getSourceMater = new SourceType();
					getSourceMater.setMaterialType(EMterialType.guildMaterial.getNumber());
					getSourceMater.setSecretType(type);
					getSourceMater.setNum(lostMaterNum);
					sourceList.add(getSourceMater);
					userSource.setUserId(army.getUserId());
					userSource.setSourceList(sourceList);// 添加奖励到人物资源里
					userSourceList.add(userSource);// 被掠夺的玩家 资源集合
				}
				int robCount = areaInfo.getRobcount();// 秘境掠夺次数
				robCount++;
				areaInfo.setRobcount(robCount);
				if (robCount >= secretCfg.getRobCount()) {
					areaInfo.setProtect(true);// 掠夺次数上限
				}
			}
			userRecord.setUserSourceList(userSourceList);// 秘境奖励更新资源
			player.getSecretMgr().addAreaUserRecord(userRecord);// 保存奖励更新
			SecretAreaInfoGMgr.getInstance().update(null, areaInfo);// 更新所有关联秘境的玩家 秘境信息
		}
		return res.build().toByteString();
	}

	/**
	 * 战斗信息中获取 对应id的玩家数据改变
	 * 
	 * @param battleInfo 战斗信息
	 * @param enemyId 获取的玩家userId
	 * @return
	 */
	private SecretUserChange getEnemyChangeById(SecretAreaBattleInfo battleInfo, String enemyId) {
		if (battleInfo == null)
			return null;
		List<SecretUserChange> enemyList = battleInfo.getEnemyChangeList();// 驻守的玩家列表
		for (int i = 0; i < enemyList.size(); i++) {
			SecretUserChange enemyChange = enemyList.get(i);
			if (enemyChange.getUserId().equals(enemyId)) {// 获取对应id对象
				return enemyChange;
			}
		}
		return null;
	}

	/**
	 * 同一场战斗数据合并 在旧掠夺记录里更新合并新记录
	 * 
	 * @param targetPlayerId 更新的玩家id
	 * @param oldBattle 旧的记录
	 * @param updateInfo 新的记录
	 */
	private void updateDefendRecord(String targetPlayerId, final SecretAreaDefRecord oldBattle, final SecretAreaDefRecord updateInfo) {// 是否是同同一秘境记录，则更新相关记录
		GameWorldFactory.getGameWorld().asyncExecute(targetPlayerId, new PlayerTask() {
			@Override
			public void run(Player player) {
				if (oldBattle == null) {
					player.getSecretMgr().addDefRecord(updateInfo);// 添加记录
					return;
				}
				if (oldBattle.getIsWin() == updateInfo.getIsWin()) {// 同一场战斗数据合并
					int count = oldBattle.getAttrackCount();// 攻击次数
					count += updateInfo.getAttrackCount();// 攻击次数增加
					oldBattle.setAttrackCount(count);
					int canGetNum = 3;
					int keyNum = updateInfo.getKeyNum();
					keyNum += updateInfo.getKeyNum();
					if (keyNum > canGetNum) {// 可获得密钥最大数量
						keyNum = canGetNum;
					}
					oldBattle.setKeyNum(keyNum);
					player.getSecretMgr().updateDefRecord(oldBattle);// 更新保存掠夺记录
				} else {
					player.getSecretMgr().addDefRecord(updateInfo);// 添加记录
				}
			}
		});
	}

	// 服务端秘境资源类型
	private EMterialType getMaterialBySecretType(ESecretType secretType) {
		EMterialType type = EMterialType.goldType;
		switch (secretType) {
		case GOLD_TYPE_ONE:
		case GOLD_TYPE_THREE:
		case GOLD_TYPE_TEN:
			type = EMterialType.goldType;// 金币
			break;
		case EXP_TYPE_ONE:
		case EXP_TYPE_THREE:
		case EXP_TYPE_TEN:
			type = EMterialType.expType;// 经验丹
			break;
		case STONG_TYPE_ONE:
		case STONG_TYPE_THREE:
		case STONG_TYPE_TEN:
			type = EMterialType.strenthType;// 强化石
			break;
		}
		return type;
	}

	/****
	 * 
	 * 盟友邀请
	 * ****/
	public ByteString chatInvite(Player player, MsgSecretRequest req) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_CHAT_INVITE);
		TagChat chatMessage = req.getChatMessage();// 请求驻守信息
		String secretId = chatMessage.getSecretId();// 秘境id
		String message = chatMessage.getMessage();// 请求话
		List<String> guildMemIdList = chatMessage.getUserIdListList();// 邀请的盟友列表UserId

		SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);
		if (areaInfo == null)
			return null;
		int type = areaInfo.getSecretType().getNumber();// 资源类型
		Boolean isSucess = ChatHandler.getInstance().chatTreasure(player, secretId, type, 3, message, guildMemIdList);// 发送请求信息
		if (!isSucess) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, "邀请失败");
		}
		return res.build().toByteString();
	}

	/****
	 * 
	 * 购买密钥
	 * ****/
	public ByteString buySecretKey(Player player, MsgSecretRequest req) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_BUY_KEY);
		SecretAreaUserInfo areaUserInfo = player.getSecretMgr().getSecretAreaUserInfo();
		int currKeyNum = areaUserInfo.getSecretKey();// 当前拥有密钥
		if (currKeyNum > SecretAreaMgr.SECRET_KEY_RECOVER_LIMIT) {// 可购买的密钥上限
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "秘境钥匙已满");
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}

		SecretBuyCoinCfg secretCfg = SecretBuyCoinCfgDAO.getInstance().getSecretCfg(areaUserInfo.getBuyKeyCount() + 1);// 下次购买配置的花费
		if (secretCfg == null) {
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}

		int cost = secretCfg.getUseGold();// 消耗金币
		if (player.getUserGameDataMgr().addGold(-cost) == -1) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "余额不足");
			res.setResultType(ESuccessType.FAIL);
			return res.build().toByteString();
		}
		addKeyNum(areaUserInfo, secretCfg.getAddSecretCoin());// 添加密钥到人物秘境基本信息里
		areaUserInfo.setBuyKeyCount(areaUserInfo.getBuyKeyCount() + 1);// 购买次数+1(每天有上限)

		player.getSecretMgr().updateAreaUserInfo();// 同步人物秘境基本信息
		res.setResultType(ESuccessType.SUCCESS);
		return res.build().toByteString();
	}

	/****
	 * 
	 * 获取进攻记录奖励
	 * ****/
	public ByteString getAttackGift(Player player, MsgSecretRequest msgHeroRequest) {
		MsgSecretResponse.Builder res = MsgSecretResponse.newBuilder();
		res.setRequireType(EReqSecretType.SECRET_ATTRACK_GIFT);
		String recordId = msgHeroRequest.getUserId();// 记录id
		boolean isAll = msgHeroRequest.getIsAll();// 是否领一键领取
		SecretAreaUserInfo areaUserInfo = player.getSecretMgr().getSecretAreaUserInfo();
		int keyNum = 0;
		int attrack = 0;
		if (areaUserInfo.getSecretKey() >= SecretAreaMgr.SECRET_KEY_LIMIT) {// 密钥可获得最大数量限制
			res.setResultType(ESuccessType.SUCCESS);
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "秘境钥匙已达上限");
			return res.build().toByteString();
		}
		if (!isAll) {// 领取单个奖励
			SecretAreaDefRecord defRecord = player.getSecretMgr().getSecretDefRecord(recordId);
			if (defRecord == null) {
				res.setResultType(ESuccessType.FAIL);
				return res.build().toByteString();
			}
			keyNum = defRecord.getKeyNum();
			attrack = defRecord.getAttrackCount();
			player.getSecretMgr().removeRecordGift(recordId);
		} else {// 一键领取
			List<SecretAreaDefRecord> defRecordList = player.getSecretMgr().getSecretDefRecordList();// 所有记录
			List<String> removeIdList = new ArrayList<String>();
			for (SecretAreaDefRecord defRecord : defRecordList) {
				keyNum += defRecord.getKeyNum();// 密钥累加
				attrack += defRecord.getAttrackCount();// 进攻次数累加
				int currCount = areaUserInfo.getSecretKey() + keyNum;
				removeIdList.add(defRecord.getId());
				if (currCount > SecretAreaMgr.SECRET_KEY_LIMIT) {// 上限
					break;
				}
			}
			player.getSecretMgr().removeDefRecordList(removeIdList);// 删除领取的掠夺记录
		}
		if (keyNum > 0) {
			addKeyNum(areaUserInfo, keyNum);// 添加密钥到人物秘境基本信息里
			player.getSecretMgr().updateAreaUserInfo();// 更新秘境基本信息
		}
		res.setResultType(ESuccessType.SUCCESS);
		res.setKeyNum(keyNum);// 获得密钥数量
		res.setTypeNum(attrack);// 攻击次数
		return res.build().toByteString();
	}

	/**
	 * 添加密钥到人物秘境基本信息里
	 * 
	 * @param areaUserInfo 人物秘境基本信息
	 * @param addNum 添加数量
	 */
	private void addKeyNum(SecretAreaUserInfo areaUserInfo, int addNum) {
		int currCount = areaUserInfo.getSecretKey() + addNum;
		currCount = currCount > SecretAreaMgr.SECRET_KEY_LIMIT ? SecretAreaMgr.SECRET_KEY_LIMIT : currCount;
		areaUserInfo.setSecretKey(currCount);
	}
}
