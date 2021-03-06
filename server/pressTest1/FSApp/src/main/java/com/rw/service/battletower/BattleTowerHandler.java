package com.rw.service.battletower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.log.GameLog;
import com.playerdata.BattleTowerMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.battletower.pojo.BattleTowerHeroInfo;
import com.rwbase.dao.battletower.pojo.BattleTowerRoleInfo;
import com.rwbase.dao.battletower.pojo.BossCacheInfo;
import com.rwbase.dao.battletower.pojo.BossInfo;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerBossCfg;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerConfigCfg;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerFloorCfg;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerRewardCfg;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerBossCfgDao;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerConfigCfgDao;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerFloorCfgDao;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerRewardCfgDao;
import com.rwbase.dao.battletower.pojo.db.TableBattleTower;
import com.rwbase.dao.battletower.pojo.db.TableBattleTowerRank;
import com.rwbase.dao.battletower.pojo.db.TableBattleTowerStrategy;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerRankDao;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerStrategyDao;
import com.rwbase.dao.battletower.pojo.readonly.BattleTowerHeroInfoIF;
import com.rwbase.dao.battletower.pojo.readonly.BattleTowerRoleInfoIF;
import com.rwbase.dao.battletower.pojo.readonly.TableBattleTowerRankIF;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonRspMsg;
import com.rwproto.BattleTowerServiceProtos.BossInfoMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.EKeyType;
import com.rwproto.BattleTowerServiceProtos.ERequestType;
import com.rwproto.BattleTowerServiceProtos.EResponseState;
import com.rwproto.BattleTowerServiceProtos.GetFriendBattleTowerRankInfoReqMsg;
import com.rwproto.BattleTowerServiceProtos.GetFriendBattleTowerRankInfoRspMsg;
import com.rwproto.BattleTowerServiceProtos.GetStrategyListReqMsg;
import com.rwproto.BattleTowerServiceProtos.GetStrategyListRspMsg;
import com.rwproto.BattleTowerServiceProtos.OpenChallengeViewRspMsg;
import com.rwproto.BattleTowerServiceProtos.OpenMainViewRspMsg;
import com.rwproto.BattleTowerServiceProtos.OpenTryLuckViewRspMsg;
import com.rwproto.BattleTowerServiceProtos.OverFriendInfoMsg;
import com.rwproto.BattleTowerServiceProtos.RankingHeroInfoMsg;
import com.rwproto.BattleTowerServiceProtos.RankingRoleInfoMsg;
import com.rwproto.BattleTowerServiceProtos.RewardInfoMsg;
import com.rwproto.BattleTowerServiceProtos.SweepEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.SweepStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.SweepStartRspMsg;
import com.rwproto.BattleTowerServiceProtos.UseLuckyKeyReqMsg;
import com.rwproto.BattleTowerServiceProtos.UseLuckyKeyRspMsg;

/*
 * @author HC
 * @date 2015年9月1日 上午11:05:18
 * @Description 试练塔处理的Handler
 */
public class BattleTowerHandler {

	/**
	 * 打开角色的试练塔主界面
	 * 
	 * @param player
	 * @param commonRsp
	 */
	public static void openBattleTowerMainView(Player player, BattleTowerCommonRspMsg.Builder commonRsp) {
		OpenMainViewRspMsg.Builder rsp = OpenMainViewRspMsg.newBuilder();// 响应消息
		String userId = player.getUserId();// 角色Id
		// 消息
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();// 试练塔数据管理
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();// 试练塔的存储数据
		if (tableBattleTower == null) {
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int vipLevel = player.getVip();
		PrivilegeCfg cfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel);
		if (cfg == null) {
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int battleTowerResetTimes = cfg.getBattleTowerResetTimes();

		// 填充消息
		rsp.setHighestFloor(tableBattleTower.getHighestFloor());
		rsp.setLeftResetTimes(battleTowerResetTimes - tableBattleTower.getResetTimes());
		BattleTowerConfigCfg uniqueCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();// 唯一的配置

		int curFloor = tableBattleTower.getCurFloor();// 当前层数
		boolean result = tableBattleTower.getResult();// 是否有了战斗结果

		int theSweepTime4PerFloor = uniqueCfg.getTheSweepTime4PerFloor();// 每层扫荡的用时
		long now = System.currentTimeMillis();
		// 扫荡信息
		long sweepStartTime = tableBattleTower.getSweepStartTime();// 开始时间
		boolean sweepState = tableBattleTower.getSweepState();// 扫荡状态
		if (sweepState && sweepStartTime > 0) {
			int sweepStartFloor = tableBattleTower.getSweepStartFloor();
			int highestFloor = tableBattleTower.getHighestFloor();
			int needTime = (int) TimeUnit.SECONDS.toMillis((highestFloor - sweepStartFloor + 1) * theSweepTime4PerFloor);// 扫荡完成需要的时间
			if (sweepStartTime + needTime < now) {// 已经完成了，发送奖励 
				List<Integer> groupIdList = new ArrayList<Integer>();
				List<RewardInfoMsg> reward = reward(player, sweepStartFloor, highestFloor, tableBattleTower, groupIdList);// 收到的奖励信息

				// 更新数据
				tableBattleTower.setSweepStartTime(0);
				tableBattleTower.setSweepState(false);
				tableBattleTower.setSweepStartFloor(0);
				tableBattleTower.setCurFloor(highestFloor);
				tableBattleTower.setResult(true);
				tableBattleTower.setRewardGroupId(groupIdList.get(groupIdList.size() - 1));

				rsp.addAllRewardInfoMsg(reward);
			} else {
				int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(now - sweepStartTime);// 经历的时间
				int addFloor = seconds / theSweepTime4PerFloor;// 要增加的层数
				int leftSecond = theSweepTime4PerFloor - (seconds % theSweepTime4PerFloor);// 剩余的时间

				rsp.setSweepFloor(sweepStartFloor + addFloor);
				rsp.setSweepFloorLeftTime(leftSecond);
				rsp.setSweepLeftTotalTime((int)TimeUnit.MILLISECONDS.toSeconds(needTime) - seconds);
			}
		} else {
			if (result) {
				rsp.setSweepFloor(curFloor);
			} else {
				rsp.setSweepFloor(curFloor > 0 ? curFloor - 1 : 0);// 如果都还没开始挑战就是0，开始挑战了但是没有被通知到结果就是当前层-1
			}
		}

		// 查找前一位好友信息
		BattleTowerRoleInfoIF beforeFriendInfo = null;
		List<TableBattleTowerRankIF> friendRankList = player.getBattleTowerMgr().getFriendRankList();
		for (int i = 0, size = friendRankList.size(); i < size; i++) {
			TableBattleTowerRankIF battleTowerRank = friendRankList.get(i);
			if (userId.equals(battleTowerRank.getUserId())) {
				continue;
			}

			BattleTowerRoleInfoIF roleInfo = battleTowerRank.getRoleInfo();
			if (roleInfo == null) {
				continue;
			}

			int f = roleInfo.getFloor();
			if (f <= curFloor) {
				continue;
			}

			beforeFriendInfo = roleInfo;
		}

		// 好友信息
		if (beforeFriendInfo != null) {
			OverFriendInfoMsg.Builder friendInfoMsg = OverFriendInfoMsg.newBuilder();
			friendInfoMsg.setHeadIcon(beforeFriendInfo.getHeadIcon());
			friendInfoMsg.setLevel(beforeFriendInfo.getLevel());
			friendInfoMsg.setName(beforeFriendInfo.getName());
			friendInfoMsg.setFloorGap(beforeFriendInfo.getFloor() - curFloor);
			rsp.setOverFriendInfoMsg(friendInfoMsg);
		}

		// 检查Boss的显示状态
		long showTime = TimeUnit.MINUTES.toMillis(uniqueCfg.getBossShowTime());
		tableBattleTower.checkBossShowState(now, showTime);// 检查超时Boss

		// 产生的Boss的信息
		List<BossInfo> bossInfoList = tableBattleTower.getBossInfoList();
		if (!bossInfoList.isEmpty()) {
			for (int i = 0, size = bossInfoList.size(); i < size; i++) {
				BossInfo bossInfo = bossInfoList.get(i);
				BossInfoMsg.Builder bossInfoMsg = BossInfoMsg.newBuilder();
				bossInfoMsg.setBossId(bossInfo.getBossUniqueId());
				bossInfoMsg.setBossCfgId(bossInfo.getBossId());
				long hasShowSecond = TimeUnit.MILLISECONDS.toSeconds((now - bossInfo.getBossStartTime()));
				bossInfoMsg.setBossRemainTime((TimeUnit.MILLISECONDS.toSeconds(showTime) - hasShowSecond));
				rsp.addBossInfoMsg(bossInfoMsg);
			}
		}

		// System.err.println(rsp.build());
		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		// 更新数据到数据库
		TableBattleTowerDao.getDao().update(tableBattleTower);
	}

	/**
	 * 进入试练塔挑战界面
	 * 
	 * 
	 * @param player
	 * @param commonRsp
	 */
	public static void openBattleTowerChallengeView(Player player, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();// 试练塔数据管理
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();// 试练塔的存储数据
		if (tableBattleTower == null) {
			GameLog.error("打开试练塔挑战界面", userId, "没有找到个人的试练塔数据");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		OpenChallengeViewRspMsg.Builder rsp = OpenChallengeViewRspMsg.newBuilder();
		int curFloor = tableBattleTower.getCurFloor();// 如果是0的话就要把当前的层设为1
		if (curFloor == 0) {
			curFloor += 1;
		}

		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		BattleTowerFloorCfg floorCfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor));
		if (floorCfg == null) {
			GameLog.error("打开试练塔挑战界面", userId, String.format("没有找到对应%s层的配置表信息", curFloor));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int groupId = floorCfg.getGroupId();// 组Id

		// 检查是不是最后一层
		List<Integer> floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
		boolean isLast = floorList.get(floorList.size() - 1) == curFloor;// 是否是最后一层
		if (isLast) {// 是最后一层
			BattleTowerFloorCfg nextFloorCfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor + 1));
			if (nextFloorCfg != null) {
				groupId = nextFloorCfg.getGroupId();// 设置组Id
			} else {
				GameLog.error("打开试练塔挑战界面", userId, String.format("已经挑战到了最高层"));
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				commonRsp.setTips("已经挑战到最高层，请重置");
				return;
			}
		}

		// 奖励信息
		BattleTowerRewardCfg rewardCfg = (BattleTowerRewardCfg) BattleTowerRewardCfgDao.getCfgDao().getCfgById(String.valueOf(groupId));
		if (rewardCfg == null) {
			GameLog.error("打开试练塔挑战界面", userId, String.format("没有找到对应%s层，对应组%s的奖励配置信息配置表没找到", curFloor, groupId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		rsp.setGroupId(groupId);
		int highestFloor = tableBattleTower.getHighestFloor();
		//modify by franky 将要打的层次(curFloor+1)比历史最高层次大才是首次打
		if (curFloor+1 > highestFloor || highestFloor == 0) {
			rsp.setIsFirst(true);
			List<ItemInfo> firstRewardList = rewardCfg.getFirstRewardList();
			for (int i = 0, size = firstRewardList.size(); i < size; i++) {
				ItemInfo itemInfo = firstRewardList.get(i);
				RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
				rewardInfoMsg.setType(itemInfo.getItemID());
				rewardInfoMsg.setCount(itemInfo.getItemNum());
				rsp.addRewardInfoMsg(rewardInfoMsg);
			}
		} else {
			rsp.setIsFirst(false);
			rsp.addAllRewardInfoMsg(rewardCfg.getUnfirstRewardInfoMsg());
		}

		rsp.setCopyId(rewardCfg.getCopyId());

		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
	}

	/**
	 * 获取某个里程碑中的战略中的角色信息
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void getStrategyRoleInfoList(Player player, GetStrategyListReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		GetStrategyListRspMsg.Builder rsp = GetStrategyListRspMsg.newBuilder();

		int floor = req.getFloor();// 层数

		BattleTowerFloorCfg floorCfg = (BattleTowerFloorCfg) BattleTowerFloorCfgDao.getCfgDao().getCfgById(String.valueOf(floor));
		if (floorCfg == null) {
			GameLog.error("获取试练塔战略", userId, String.format("获取战略获取不到第%s层的BattleTowerFloorCfg配置", floor));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int groupId = floorCfg.getGroupId();
		TableBattleTowerStrategyDao strategyDao = TableBattleTowerStrategyDao.getDao();
		TableBattleTowerStrategy tableBattleTowerStrategy = strategyDao.getStrategy(groupId);// 获取战略数据
		if (tableBattleTowerStrategy == null) {
			GameLog.error("获取试练塔战略", userId, String.format("获取战略第%s组的数据失败，没找到TableBattleTowerStrategy数据", groupId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		List<BattleTowerRoleInfo> roleInfoList = tableBattleTowerStrategy.getRoleInfoList();
		int size = roleInfoList.size();

		for (int i = 0; i < size; i++) {
			BattleTowerRoleInfo roleInfo = roleInfoList.get(i);
			// 转换成正式协议对象
			RankingRoleInfoMsg.Builder rankingRoleInfo = RankingRoleInfoMsg.newBuilder();
			rankingRoleInfo.setName(roleInfo.getName());
			rankingRoleInfo.setHeadIcon(roleInfo.getHeadIcon());
			rankingRoleInfo.setLevel(roleInfo.getLevel());
			rankingRoleInfo.setMagicIcon(roleInfo.getMagicIcon());
			// rankingRoleInfo.setHighestFloor(roleInfo.getFloor());
			// // 获取使用的角色信息
			String rankingUserId = roleInfo.getUserId();
			// rankingRoleInfo.setIsMyself(userId.equals(rankingUserId));// 是否是自己

			List<BattleTowerHeroInfo> heroInfoList = roleInfo.getHeroInfoList();
			for (int j = 0, hSize = heroInfoList.size(); j < hSize; j++) {
				BattleTowerHeroInfo heroInfo = heroInfoList.get(j);
				// 转换成正式协议对象
				RankingHeroInfoMsg.Builder rankingHeroInfo = RankingHeroInfoMsg.newBuilder();
				rankingHeroInfo.setHeroId(heroInfo.getHeroId());
				rankingHeroInfo.setLevel(heroInfo.getLevel());
				rankingHeroInfo.setQuality(heroInfo.getQuality());
				rankingHeroInfo.setStarNum(heroInfo.getStarNum());
				if (rankingUserId.equals(heroInfo.getHeroId())) {
					rankingHeroInfo.setIsMainRole(true);
				}
				// 添加到RankingRoleInfo中
				rankingRoleInfo.addRankingHeroInfoMsg(rankingHeroInfo);
			}

			// 添加到响应协议中
			rsp.addRankingRoleInfoMsg(rankingRoleInfo);
		}

		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
	}

	/**
	 * 获取对应好友的试练塔排行信息
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void getFriendBattleTowerRankList(Player player, GetFriendBattleTowerRankInfoReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();

		int pageIndex = req.getPageIndex();// 请求查看第几页的数据
		List<TableBattleTowerRankIF> friendRankList = battleTowerMgr.getFriendRankList(pageIndex);
		int size = friendRankList.size();

		// 填充消息
		GetFriendBattleTowerRankInfoRspMsg.Builder rsp = GetFriendBattleTowerRankInfoRspMsg.newBuilder();
		for (int i = 0; i < size; i++) {
			TableBattleTowerRankIF rankInfo = friendRankList.get(i);

			BattleTowerRoleInfoIF roleInfo = rankInfo.getRoleInfo();
			// 填充消息
			RankingRoleInfoMsg.Builder rankingRoleInfo = RankingRoleInfoMsg.newBuilder();
			rankingRoleInfo.setName(roleInfo.getName());
			rankingRoleInfo.setHeadIcon(roleInfo.getHeadIcon());
			rankingRoleInfo.setLevel(roleInfo.getLevel());
			rankingRoleInfo.setMagicIcon(roleInfo.getMagicIcon());
			rankingRoleInfo.setRankIndex(i + 1);
			rankingRoleInfo.setHighestFloor(roleInfo.getFloor());
			String friendUserId = roleInfo.getUserId();
			rankingRoleInfo.setIsMyself(userId.equals(friendUserId));// 是否是自己
			// 获取使用的角色信息
			List<? extends BattleTowerHeroInfoIF> heroInfoList = roleInfo.getHeroInfoList();
			for (int j = 0, hSize = heroInfoList.size(); j < hSize; j++) {
				BattleTowerHeroInfoIF heroInfo = heroInfoList.get(j);
				// 转换成正式协议对象
				RankingHeroInfoMsg.Builder rankingHeroInfo = RankingHeroInfoMsg.newBuilder();
				rankingHeroInfo.setHeroId(heroInfo.getHeroId());
				rankingHeroInfo.setLevel(heroInfo.getLevel());
				rankingHeroInfo.setQuality(heroInfo.getQuality());
				rankingHeroInfo.setStarNum(heroInfo.getStarNum());
				if (friendUserId.equals(heroInfo.getHeroId())) {
					rankingHeroInfo.setIsMainRole(true);
				}
				// 添加到RankingRoleInfo中
				rankingRoleInfo.addRankingHeroInfoMsg(rankingHeroInfo);
			}

			rsp.addRankingRoleInfoMsg(rankingRoleInfo);
		}

		// 设置好友数量
		rsp.setFriendSize(size);
		rsp.setPageIndex(pageIndex);

		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		commonRsp.setRspBody(rsp.build().toByteString());
	}

	/**
	 * 打开试手气界面
	 * 
	 * @param player
	 * @param commonRsp
	 */
	public static void openTryLuckView(Player player, BattleTowerCommonRspMsg.Builder commonRsp) {
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("打开试练塔试手气界面", player.getUserId(), "没有找到个人的试练塔数据");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		OpenTryLuckViewRspMsg.Builder rsp = OpenTryLuckViewRspMsg.newBuilder();
		rsp.setCopperKeyNum(tableBattleTower.getCopper_key());
		rsp.setSilverKeyNum(tableBattleTower.getSilver_key());
		rsp.setGoldKeyNum(tableBattleTower.getGold_key());

		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		commonRsp.setRspBody(rsp.build().toByteString());
	}

	/**
	 * 扫荡开始
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void sweepStart(Player player, SweepStartReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		SweepStartRspMsg.Builder rsp = SweepStartRspMsg.newBuilder();// 扫荡开始的响应消息

		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("试练塔扫荡", userId, "获取个人的试练塔数据为null");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		if (tableBattleTower.getSweepState()) {// 还在继续扫荡
			GameLog.error("试练塔扫荡", userId, "当前的试练塔处于扫荡的状态中");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int highestFloor = tableBattleTower.getHighestFloor();
		int startFloor = req.getFloor();
		if (startFloor <= 0 || startFloor >= highestFloor) {
			GameLog.error("试练塔扫荡", userId, String.format("请求扫荡的开始层是%s，历史最高层是%s", startFloor, highestFloor));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();

		int curFloor = tableBattleTower.getCurFloor();// 当前层

		if (startFloor - curFloor != 1) {
			GameLog.error("试练塔扫荡", userId, String.format("请求开始的层%s，当前层是%s，请求开始层低于或者超过当前层1层", startFloor, curFloor));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		curFloor = curFloor == 0 ? 1 : curFloor;
		BattleTowerFloorCfg cfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor));
		if (cfg == null) {
			GameLog.error("试练塔扫荡", userId, String.format("当前层%s获取BattleTowerFloorCfg的配置表为null", curFloor));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int groupId = cfg.getGroupId();// 组Id
		List<Integer> floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
		// 检查一下是不是最后一层
		int lastFloor = floorList.get(floorList.size() - 1);
		if (curFloor == lastFloor) {
			curFloor++;// 下一层加一
			cfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor));
			if (cfg == null) {
				GameLog.error("试练塔模块-战斗开始", userId, "挑战的新层，没有找到对应的" + curFloor + "层的配置表");
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}

			groupId = cfg.getGroupId();
			floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
			if (floorList.isEmpty()) {
				GameLog.error("试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId));
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}
		}

		if (!floorList.contains(startFloor)) {// 当前里程碑不包含这个等级
			GameLog.error("试练塔扫荡", userId, String.format("组%s的数据中没有包含请求开始的层", groupId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		long now = System.currentTimeMillis();
		// 次数没有超过上限，可以随机产生Boss
		Random r = new Random();
		BattleTowerConfigCfg uniqueCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();
		int perDayBossSize = uniqueCfg.getPerDayBossSize();// 每天产生Boss的上限数量
		int theSweepTime4PerFloor = uniqueCfg.getTheSweepTime4PerFloor();// 每层扫荡需要的时间（秒）

		if (tableBattleTower.getCurBossTimes() < perDayBossSize) {
			int leftBossSize = perDayBossSize - tableBattleTower.getCurBossTimes();// 剩下产生几个Boss

			BattleTowerBossCfgDao bossCfgDao = BattleTowerBossCfgDao.getCfgDao();

			List<BattleTowerFloorCfg> allCfg = cfgDao.getAllCfg();
			Collections.sort(allCfg, comparator);// 排序

			for (int i = 0, size = allCfg.size(); i < size; i++) {
				if (leftBossSize <= 0) {// 最后没有次数了，就直接退出了
					break;
				}

				BattleTowerFloorCfg cfg0 = allCfg.get(i);
				int floor = cfg0.getFloor();
				if (floor >= startFloor && floor <= highestFloor && cfg0.getBossPro() > 0) {
					int rNum = r.nextInt(100);// 100中随机
					if (rNum >= 0 && rNum < cfg0.getBossPro()) {// 随机到了
						BattleTowerBossCfg boss = bossCfgDao.ranBossInfo(player.getLevel());
						if (boss != null) {
							// Boss信息
							BossInfo bossInfo = new BossInfo();
							bossInfo.setBossId(boss.getBossId());// Boss的模版Id
							bossInfo.setBossStartTime(now + TimeUnit.SECONDS.toMillis((floor - curFloor) * theSweepTime4PerFloor));// Boss出现的时间
							bossInfo.setBossInGroupId(cfg0.getGroupId());// Boss所在的组
							bossInfo.setBossInFloor(floor);// Boss出现的层

							int bossId = tableBattleTower.addBossInfo(bossInfo);
							// 当次产生Boss的缓存信息
							BossCacheInfo bossCacheInfo = new BossCacheInfo(boss.getBossId(), cfg0.getMarkId());
							tableBattleTower.addBossCacheInfo(bossCacheInfo);
							// 剩余boss的数量减1
							leftBossSize--;

							// Boss信息协议
							BossInfoMsg.Builder bossInfoMsg = BossInfoMsg.newBuilder();
							bossInfoMsg.setBossId(bossId);
							bossInfoMsg.setBossCfgId(bossInfo.getBossId());
							bossInfoMsg.setBossInFloor(bossInfo.getBossInFloor());
							// bossInfoMsg.setBossStartTime(bossInfo.getBossStartTime());
							long hasShowSecond = TimeUnit.MILLISECONDS.toSeconds((now - bossInfo.getBossStartTime()));
							bossInfoMsg.setBossRemainTime((TimeUnit.MINUTES.toSeconds(uniqueCfg.getBossShowTime()) - hasShowSecond));
							rsp.addBossInfoMsg(bossInfoMsg);
						}
					}

					// 如果是报底层
					if (cfg0.getBossBreakEvenNum() > 0) {// 确定是保底层，已经产出了一个
						if (!tableBattleTower.hasBossInfoInMark(cfg0.getMarkId())) {// 已经有了
							BattleTowerBossCfg boss = bossCfgDao.ranBossInfo(player.getLevel());
							if (boss != null) {
								// Boss信息
								BossInfo bossInfo = new BossInfo();
								bossInfo.setBossId(boss.getBossId());// Boss的模版Id
								bossInfo.setBossStartTime(now + TimeUnit.SECONDS.toMillis((floor - curFloor) * theSweepTime4PerFloor));// Boss出现的时间
								bossInfo.setBossInGroupId(cfg0.getGroupId());// Boss所在的组
								bossInfo.setBossInFloor(floor);// Boss出现的层

								int bossId = tableBattleTower.addBossInfo(bossInfo);
								// 当次产生Boss的缓存信息
								BossCacheInfo bossCacheInfo = new BossCacheInfo(boss.getBossId(), cfg0.getMarkId());
								tableBattleTower.addBossCacheInfo(bossCacheInfo);
								// 剩余boss的数量减1
								leftBossSize--;

								// Boss信息协议
								BossInfoMsg.Builder bossInfoMsg = BossInfoMsg.newBuilder();
								bossInfoMsg.setBossId(bossId);
								bossInfoMsg.setBossCfgId(bossInfo.getBossId());
								bossInfoMsg.setBossInFloor(bossInfo.getBossInFloor());
								// bossInfoMsg.setBossStartTime(bossInfo.getBossStartTime());
								long hasShowSecond = TimeUnit.MILLISECONDS.toSeconds((now - bossInfo.getBossStartTime()));
								bossInfoMsg.setBossRemainTime((TimeUnit.MINUTES.toSeconds(uniqueCfg.getBossShowTime()) - hasShowSecond));
								rsp.addBossInfoMsg(bossInfoMsg);
							}
						}
					}
				}
			}
		}

		// 扫荡数据处理
		tableBattleTower.setSweepStartTime(now);// 扫荡开始时间
		tableBattleTower.setSweepStartFloor(startFloor);// 开始扫荡的层数
		tableBattleTower.setSweepState(true);// 扫荡的状态设置
		dao.update(tableBattleTower);// 更新数据

		// 发送协议
		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
	}

	/**
	 * 扫荡结束
	 * 
	 * @param player
	 * @param commonRsp
	 */
	public static void sweepEnd(Player player, BattleTowerCommonRspMsg.Builder commonRsp) {
		SweepEndRspMsg.Builder rsp = SweepEndRspMsg.newBuilder();
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("试练塔扫荡结束", userId, "获取个人的试练塔数据为null");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 没有在扫荡，就不用领取奖励
		if (!tableBattleTower.getSweepState()) {
			GameLog.error("试练塔扫荡结束", userId, "当前不是扫荡状态");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 单层扫荡时间
		BattleTowerConfigCfg uniqueCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();
		int theSweepTime4PerFloor = uniqueCfg.getTheSweepTime4PerFloor();

		// 时间验证
		long now = System.currentTimeMillis();
		int sweepStartFloor = tableBattleTower.getSweepStartFloor();
		int highestFloor = tableBattleTower.getHighestFloor();
		long needTime = TimeUnit.SECONDS.toMillis((highestFloor - sweepStartFloor + 1) * theSweepTime4PerFloor);

		// 检查时间是否已经到了完成时间
		if (tableBattleTower.getSweepStartTime() + needTime > now) {
			GameLog.error("试练塔扫荡结束", userId, "扫荡结束时间未到");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		List<Integer> groupIdList = new ArrayList<Integer>();// 可以获取奖励的组Id
		List<RewardInfoMsg> reward = reward(player, sweepStartFloor, highestFloor, tableBattleTower, groupIdList);
		rsp.addAllRewardInfoMsg(reward);

		// 更新数据
		tableBattleTower.setSweepStartTime(0);
		tableBattleTower.setSweepState(false);
		tableBattleTower.setSweepStartFloor(0);
		tableBattleTower.setCurFloor(highestFloor);
		tableBattleTower.setResult(true);
		tableBattleTower.setRewardGroupId(groupIdList.get(groupIdList.size() - 1));

		dao.update(tableBattleTower);

		// 发送协议
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		commonRsp.setRspBody(rsp.build().toByteString());
	}

	/**
	 * 使用金银铜钥匙
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void useLuckyKey(Player player, UseLuckyKeyReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		UseLuckyKeyRspMsg.Builder rsp = UseLuckyKeyRspMsg.newBuilder();

		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("试练塔试手气", userId, "没有找到个人的试练塔数据");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		EKeyType keyType = req.getKeyType();
		int useNum = req.getUseNum();
		if (useNum <= 0) {
			GameLog.error("试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，使用数量<=0", keyType, useNum));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		BattleTowerConfigCfgDao configCfgDao = BattleTowerConfigCfgDao.getCfgDao();
		BattleTowerConfigCfg uniqueCfg = configCfgDao.getUniqueCfg();// 唯一的配置

		String[] dropArr;
		Map<Integer, Integer> rewardItemMap = new HashMap<Integer, Integer>();// 奖励的Map
		if (keyType == EKeyType.KEY_COPPER) {// 铜
			if (useNum > tableBattleTower.getCopper_key()) {
				GameLog.error("试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，现有数量%s，数量不足", keyType, useNum, tableBattleTower.getCopper_key()));
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}

			tableBattleTower.modifyCopperKey(-useNum);
			dropArr = uniqueCfg.getCopperKeyDropIdArr();
		} else if (keyType == EKeyType.KEY_SILVER) {// 银
			if (useNum > tableBattleTower.getSilver_key()) {
				GameLog.error("试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，现有数量%s，数量不足", keyType, useNum, tableBattleTower.getSilver_key()));
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}

			tableBattleTower.modifySilverKey(-useNum);
			dropArr = uniqueCfg.getSilverKeyDropIdArr();
		} else if (keyType == EKeyType.KEY_GOLD) {// 金
			if (useNum > tableBattleTower.getGold_key()) {
				GameLog.error("试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，现有数量%s，数量不足", keyType, useNum, tableBattleTower.getGold_key()));
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}

			tableBattleTower.modifyGoldKey(-useNum);
			dropArr = uniqueCfg.getGoldKeyDropIdArr();
		} else {
			GameLog.error("试练塔试手气", userId, "客户端发送了一个未知的钥匙类型");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		BattleTowerRewardCfgDao rewardCfgDao = BattleTowerRewardCfgDao.getCfgDao();
		// 奖励的物品
		for (int i = 0; i < useNum; i++) {
			List<ItemInfo> ranRewardItem = rewardCfgDao.getRanRewardItem(dropArr, player);
			for (int j = 0, itemSize = ranRewardItem.size(); j < itemSize; j++) {
				ItemInfo itemInfo = ranRewardItem.get(j);
				Integer hasValue = rewardItemMap.get(itemInfo.getItemID());
				if (hasValue == null) {
					rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum());
				} else {
					rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum() + hasValue.intValue());
				}
			}
		}

		// 发送奖励
		for (Entry<Integer, Integer> e : rewardItemMap.entrySet()) {
			int key = e.getKey();
			int num = e.getValue();
			if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
				tableBattleTower.modifyCopperKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
				tableBattleTower.modifySilverKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
				tableBattleTower.modifyGoldKey(num);
			} else {
				player.getItemBagMgr().addItem(key, num);
			}

			RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
			rewardInfoMsg.setType(key);
			rewardInfoMsg.setCount(num);
			rsp.addRewardInfoMsg(rewardInfoMsg);
		}

		// 消息填充
		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);

		// 更新数据
		dao.update(tableBattleTower);
	}

	/**
	 * 重置试练塔数据
	 * 
	 * @param player
	 * @param commonRsp
	 */
	public static void resetBattleTowerData(Player player, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("重置试练塔次数", userId, "没有个人的试练塔数据");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		if (tableBattleTower.getSweepState()) {// 还在扫荡
			GameLog.error("重置试练塔次数", userId, "当前角色都还在扫荡的过程中");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int vipLevel = player.getVip();
		PrivilegeCfg cfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel);
		if (cfg == null) {
			GameLog.error("重置试练塔次数", userId, String.format("角色是Vip%s，并没有找到PrivilegeCfg的配置表", vipLevel));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int battleTowerResetTimes = cfg.getBattleTowerResetTimes();
		if (tableBattleTower.getResetTimes() >= battleTowerResetTimes) {// 重置次数满了
			GameLog.error("重置试练塔次数", userId, String.format("今天的重置次数已经用完了"));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 重置数据
		tableBattleTower.resetBattleTowerData();
		dao.update(tableBattleTower);

		commonRsp.setRspState(EResponseState.RSP_SUCESS);
	}

	/**
	 * 挑战某层开始
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void battleTowerChallengeStart(Player player, ChallengeStartReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("试练塔模块-战斗开始", userId, "没有找到角色对应的试练塔数据信息");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int floor = req.getFloor();// 要挑战的层数
		int curFloor = tableBattleTower.getCurFloor();// 当前层
		curFloor = curFloor == 0 ? 1 : curFloor;

		// 检测当前层是否是被中断的
		boolean isBreak = tableBattleTower.isBreak();// 是否被中断
		if (isBreak) {
			GameLog.error("试练塔模块-战斗开始", userId, "当前某层挑战失败，不能继续进行挑战");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		BattleTowerFloorCfg floorCfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor));
		if (floorCfg == null) {
			GameLog.error("试练塔模块-战斗开始", userId, "没有找到对应的" + curFloor + "层的配置表");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int groupId = floorCfg.getGroupId();
		List<Integer> floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
		if (floorList.isEmpty()) {
			GameLog.error("试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 检查一下是不是最后一层
		int lastFloor = floorList.get(floorList.size() - 1);
		if (curFloor == lastFloor) {
			curFloor++;// 下一层加一
			floorCfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor));
			if (floorCfg == null) {
				GameLog.error("试练塔模块-战斗开始", userId, "挑战的新层，没有找到对应的" + curFloor + "层的配置表");
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}

			groupId = floorCfg.getGroupId();
			floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
			if (floorList.isEmpty()) {
				GameLog.error("试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId));
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}
		}

		if (floor != curFloor) {
			GameLog.error("试练塔模块-战斗开始", userId, String.format("请求层和应该打的层数不一致，请求%s层,应该打第%s层", floor, curFloor));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		if (floor != floorList.get(0)) {
			GameLog.error("试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，请求打的不是该组第一层数据", curFloor, floorCfg.getGroupId()));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		tableBattleTower.setCurFloor(floor);// 设置当前要打的层
		tableBattleTower.setResult(false);// 当前还没有结果
		// 更新数据
		dao.update(tableBattleTower);

		commonRsp.setReqType(ERequestType.CHALLENGE_START);
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
	}

	/**
	 * 挑战某一层结束
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void battleTowerChallengeEnd(Player player, ChallengeEndReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("试练塔模块-战斗结束", userId, "没有找到个人的试练塔数据");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int floor = req.getFloor();// 打到了多少层
		int curFloor = tableBattleTower.getCurFloor();// 当前层
		boolean result = req.getResult();// 结果
		curFloor = curFloor == 0 ? 1 : curFloor;

		// 检测当前层是否是被中断的
		boolean isBreak = tableBattleTower.isBreak();// 是否被中断
		if (isBreak) {
			GameLog.error("试练塔模块-战斗结束", userId, "当前某层已经挑战失败而导致整个挑战中断，不能继续进行挑战，必须要重置");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 层信息
		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		BattleTowerFloorCfg floorCfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor));
		if (floorCfg == null) {
			GameLog.error("试练塔模块-战斗结束", userId, "没有找到对应" + curFloor + "层的数据");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 组信息
		int groupId = floorCfg.getGroupId();// 当前层属于的组
		List<Integer> floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
		if (floorList.isEmpty()) {
			GameLog.error("试练塔模块-战斗结束", userId, String.format("对应的层%s，所属的组%s，包含层的信息列表是空", curFloor, groupId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		if (!floorList.contains(floor)) {// 服务器存储的组信息没有包含完成的信息，有作弊嫌疑
			GameLog.error("试练塔模块-战斗结束", userId, String.format("发送完成的层是%s，所属组是%s，这个组并没有包含请求完成的层信息", floor, groupId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 响应消息
		ChallengeEndRspMsg.Builder rsp = ChallengeEndRspMsg.newBuilder();

		if (tableBattleTower.getResult()) {
			GameLog.error("试练塔模块-战斗结束", userId, "当前层已经有了战斗结果，不能重复发送结果");
			return;
		}

		tableBattleTower.setResult(true);// 设置已经拿到战斗结果的标记
		if (!result) {// 失败了
			tableBattleTower.setBreak(true);
		} else {// 成功
			if (floor < curFloor) {
				GameLog.error("试练塔模块-战斗结束", userId, String.format("请求胜利%s层，存储的是%s层，请求胜利的层<=当前存储的层", floor, curFloor));
				commonRsp.setRspState(EResponseState.RSP_FAIL);
				return;
			}

			// 角色信息
			BattleTowerRoleInfo roleInfo = new BattleTowerRoleInfo(userId);
			roleInfo.setFloor(floor);
			roleInfo.setHeadIcon(player.getHeadImage());
			roleInfo.setLevel(player.getLevel());
			roleInfo.setName(player.getUserName());

			// 法宝
			ItemData magic = player.getMagic();
			if (magic != null) {
				MagicCfg magicCfg = ItemCfgHelper.getMagicCfg(magic.getModelId());
				if (magicCfg != null) {
					roleInfo.setMagicIcon(magicCfg.getIcon());
				}
			}

			// 阵容中的英雄信息
			List<RankingHeroInfoMsg> rankingHeroInfoMsgList = req.getRankingHeroInfoMsgList();
			int size = rankingHeroInfoMsgList.size();

			List<BattleTowerHeroInfo> heroInfoList = new ArrayList<BattleTowerHeroInfo>(size);
			for (int i = 0; i < size; i++) {
				RankingHeroInfoMsg heroInfoMsg = rankingHeroInfoMsgList.get(i);

				BattleTowerHeroInfo heroInfo = new BattleTowerHeroInfo();
				heroInfo.setHeroId(heroInfoMsg.getHeroId());
				heroInfo.setLevel(heroInfoMsg.getLevel());
				heroInfo.setQuality(heroInfoMsg.getQuality());
				heroInfo.setStarNum(heroInfoMsg.getStarNum());
			}

			roleInfo.setHeroInfoList(heroInfoList);

			// 奖励模版
			BattleTowerRewardCfgDao rewardCfgDao = BattleTowerRewardCfgDao.getCfgDao();
			BattleTowerRewardCfg rewardCfg = (BattleTowerRewardCfg) rewardCfgDao.getCfgById(String.valueOf(groupId));

			boolean isLastFloor = false;
			if (floor == floorList.get(floorList.size() - 1)) {// 是最后一层
				isLastFloor = true;
			}

			// 设置当前新的层
			tableBattleTower.setCurFloor(floor);// 设置当前新的层

			// 奖励信息
			List<ItemInfo> itemInfoList = null;
			int highestFloor = tableBattleTower.getHighestFloor();

			// 是否是历史最高层次
			if (floor > highestFloor) {
				tableBattleTower.setHighestFloor(floor);// 设置历史最高层
				// 将自己的新记录刷新到库里
				TableBattleTowerRank battleTowerRank = TableBattleTowerRankDao.getRankByKey(userId);
				if (battleTowerRank == null) {
					battleTowerRank = new TableBattleTowerRank(userId);
				}

				battleTowerRank.setRoleInfo(roleInfo);
				TableBattleTowerRankDao.updateValue(battleTowerRank);// 刷新排行榜中记录的数据

				// 发送的是首次奖励
				if (isLastFloor) {
					itemInfoList = rewardCfg.getFirstRewardList();
				}
			} else if (isLastFloor) {
				itemInfoList = rewardCfgDao.getRanRewardItem(rewardCfg.getDropIdArr(), player);
			}

			// 刷新战略中的数据
			if (isLastFloor) {// 两层所在的组不一样，就刷新攻略表中的记录,是最后一层，才会刷入数据
				TableBattleTowerStrategyDao strategyDao = TableBattleTowerStrategyDao.getDao();
				TableBattleTowerStrategy tableBattleTowerStrategy = strategyDao.getStrategy(groupId);
				tableBattleTowerStrategy.addBattleTowerRoleInfo(roleInfo);
				strategyDao.update(tableBattleTowerStrategy);
			}

			// 发送奖励数据
			if (itemInfoList != null) {
				for (int i = 0, size0 = itemInfoList.size(); i < size0; i++) {
					ItemInfo itemInfo = itemInfoList.get(i);
					int key = itemInfo.getItemID();
					int num = itemInfo.getItemNum();
					if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
						tableBattleTower.modifyCopperKey(num);
					} else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
						tableBattleTower.modifySilverKey(num);
					} else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
						tableBattleTower.modifyGoldKey(num);
					} else {
						player.getItemBagMgr().addItem(key, num);
					}

					RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
					rewardInfoMsg.setType(key);
					rewardInfoMsg.setCount(num);
					rsp.addRewardInfoMsg(rewardInfoMsg);
				}
			}
		}

		// 到这里就算成功了
		commonRsp.setRspState(EResponseState.RSP_SUCESS);

		// Boss信息
		BattleTowerConfigCfg configCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();
		int perDayBossSize = configCfg.getPerDayBossSize();// 当天可以产生的Boss数量

		Integer startFloor = floorList.get(0);
		for (int i = 0, end = floorList.size(); i < end; i++) {
			boolean canBoss = false;// 是否可以产生Boss
			floorCfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(startFloor + i));
			if (floorCfg.getBossBreakEvenNum() > 0 && !tableBattleTower.hasBossInfoInMark(floorCfg.getMarkId())) {// 保底层
				canBoss = true;
			} else {
				if (floorCfg.getBossPro() <= 0) {// 不产生
					continue;
				}

				Random r = new Random();
				int rNum = r.nextInt(100);
				if (rNum >= 0 && rNum < floorCfg.getBossPro()) {
					canBoss = true;
				}
			}

			int curBossTimes = tableBattleTower.getCurBossTimes();

			if (curBossTimes >= perDayBossSize) {// 超过当天产生Boss的数量
				continue;
			}

			if (tableBattleTower.getBossInfoList().size() >= configCfg.getBossNumInTheSameTime()) {// 超过同时可以拥有Boss的数量
				continue;
			}

			// 产生Boss
			if (canBoss) {
				BattleTowerBossCfg ranBossInfo = BattleTowerBossCfgDao.getCfgDao().ranBossInfo(player.getLevel());
				if (ranBossInfo != null) {
					BossInfo bossInfo = new BossInfo();
					bossInfo.setBossId(ranBossInfo.getBossId());// Boss的模版Id
					bossInfo.setBossStartTime(System.currentTimeMillis());// Boss出现的时间
					bossInfo.setBossInGroupId(floorCfg.getGroupId());// Boss所在的组
					bossInfo.setBossInFloor(startFloor + i);// Boss出现的层

					int bossId = tableBattleTower.addBossInfo(bossInfo);

					BossInfoMsg.Builder bossInfoMsg = BossInfoMsg.newBuilder();
					bossInfoMsg.setBossCfgId(bossInfo.getBossId());
					bossInfoMsg.setBossInFloor(bossInfo.getBossInFloor());
					bossInfoMsg.setBossRemainTime(TimeUnit.MINUTES.toSeconds(configCfg.getBossShowTime()));
					bossInfoMsg.setBossId(bossId);
					rsp.addBossInfoMsg(bossInfoMsg);
				}
			}
		}

		dao.update(tableBattleTower);
		commonRsp.setRspBody(rsp.build().toByteString());
	}

	/**
	 * 挑战Boss开始
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void challengeBossStart(Player player, ChallengeBossStartReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("挑战Boss开始", userId, "获取个人的试练塔数据为null");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int bossId = req.getBossId();
		BossInfo bossInfo = tableBattleTower.getBoss(bossId);
		if (bossInfo == null) {
			GameLog.error("挑战Boss开始", userId, String.format("挑战Boss的Id为%s，没有找到对应数据", bossId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		tableBattleTower.setChallengeBossId(bossId);
		dao.update(tableBattleTower);

		commonRsp.setRspState(EResponseState.RSP_SUCESS);
	}

	/**
	 * 挑战Boss结束
	 * 
	 * @param player
	 * @param req
	 * @param commonRsp
	 */
	public static void challengeBossEnd(Player player, ChallengeBossEndReqMsg req, BattleTowerCommonRspMsg.Builder commonRsp) {
		String userId = player.getUserId();
		TableBattleTowerDao dao = TableBattleTowerDao.getDao();
		BattleTowerMgr battleTowerMgr = player.getBattleTowerMgr();
		TableBattleTower tableBattleTower = battleTowerMgr.getTableBattleTower();
		if (tableBattleTower == null) {
			GameLog.error("挑战Boss结束", userId, "获取个人的试练塔数据为null");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		int challengeBossId = tableBattleTower.getChallengeBossId();
		if (challengeBossId <= 0) {// 如果当前缓存的BossId是0,就证明根本没缓存Boss数据
			GameLog.error("挑战Boss结束", userId, "角色可能作弊了，因为缓存的挑战Boss数据是没有的");
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		boolean result = req.getResult();
		if (!result) {// 失败了，就直接清空缓存数据
			tableBattleTower.setChallengeBossId(0);
			dao.update(tableBattleTower);

			commonRsp.setRspState(EResponseState.RSP_SUCESS);
			return;
		}

		int bossId = req.getBossId();
		if (challengeBossId != bossId) {// 缓存的数据和传递的比一样，就说明客户端采用非法手段发送协议，直接拒绝处理
			GameLog.error("挑战Boss结束", userId, String.format("客户端请求的挑战BossId为%s，服务器缓存的是%s", bossId, challengeBossId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		BossInfo bossInfo = tableBattleTower.getBoss(bossId);
		if (bossInfo == null) {
			GameLog.error("挑战Boss结束", userId, String.format("挑战Boss的Id为%s，没有找到对应数据", bossId));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 开始准备处理Boss的奖励
		BattleTowerBossCfgDao bossCfgDao = BattleTowerBossCfgDao.getCfgDao();
		BattleTowerBossCfg bossCfg = (BattleTowerBossCfg) bossCfgDao.getCfgById(String.valueOf(bossInfo.getBossId()));
		if (bossCfg == null) {
			GameLog.error("挑战Boss结束", userId, String.format("对应的Boss模版Id为%s的BossCfg模版不存在", bossInfo.getBossId()));
			commonRsp.setRspState(EResponseState.RSP_FAIL);
			return;
		}

		// 清除Boss数据
		tableBattleTower.setChallengeBossId(0);
		tableBattleTower.removeBoss(bossId);
		dao.update(tableBattleTower);

		BattleTowerRewardCfgDao rewardCfgDao = BattleTowerRewardCfgDao.getCfgDao();
		List<ItemInfo> itemInfoList = rewardCfgDao.getRanRewardItem(bossCfg.getDropIdArr(), player);

		// 响应协议
		ChallengeBossEndRspMsg.Builder rsp = ChallengeBossEndRspMsg.newBuilder();

		for (int i = 0, size0 = itemInfoList.size(); i < size0; i++) {
			ItemInfo itemInfo = itemInfoList.get(i);
			int key = itemInfo.getItemID();
			int num = itemInfo.getItemNum();
			if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
				tableBattleTower.modifyCopperKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
				tableBattleTower.modifySilverKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
				tableBattleTower.modifyGoldKey(num);
			} else {
				player.getItemBagMgr().addItem(key, num);
			}

			RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
			rewardInfoMsg.setType(key);
			rewardInfoMsg.setCount(num);
			rsp.addRewardInfoMsg(rewardInfoMsg);
		}

		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
	}

	/**
	 * 模版排序
	 */
	private static Comparator<BattleTowerFloorCfg> comparator = new Comparator<BattleTowerFloorCfg>() {

		@Override
		public int compare(BattleTowerFloorCfg o1, BattleTowerFloorCfg o2) {
			return o1.getFloor() - o2.getFloor();
		}
	};

	/**
	 * 奖励物品
	 * 
	 * @param player
	 * @param sweepStartFloor
	 * @param highestFloor
	 * @param tableBattleTower
	 * @return
	 */
	private static List<RewardInfoMsg> reward(Player player, int sweepStartFloor, int highestFloor, TableBattleTower tableBattleTower, List<Integer> groupIdList) {
		// 获取要奖励的物品
		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		// List<Integer> groupIdList = new ArrayList<Integer>();// 可以获取奖励的组Id
		for (int i = sweepStartFloor; i <= highestFloor; i++) {
			BattleTowerFloorCfg floorCfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(i));// 模版
			if (floorCfg == null) {
				continue;
			}

			int floor = floorCfg.getFloor();
			int groupId = floorCfg.getGroupId();
			List<Integer> groupFloorList = cfgDao.getContainFloorList(String.valueOf(groupId));
			if (groupFloorList.isEmpty()) {
				continue;
			}

			int endFloor = groupFloorList.get(groupFloorList.size() - 1);// 结束的层
			if (floor == endFloor) {// 已经是后的层数
				groupIdList.add(groupId);// 添加可以发送奖励的组
			}
		}

		// 奖励的组信息
		BattleTowerRewardCfgDao rewardCfgDao = BattleTowerRewardCfgDao.getCfgDao();

		Map<Integer, Integer> rewardItemMap = new HashMap<Integer, Integer>();// 奖励的Map
		for (int i = 0, size = groupIdList.size(); i < size; i++) {
			BattleTowerRewardCfg cfg = (BattleTowerRewardCfg) rewardCfgDao.getCfgById(String.valueOf(groupIdList.get(i)));
			if (cfg == null) {
				continue;
			}

			List<ItemInfo> ranRewardItem = rewardCfgDao.getRanRewardItem(cfg.getDropIdArr(), player);
			for (int j = 0, itemSize = ranRewardItem.size(); j < itemSize; j++) {
				ItemInfo itemInfo = ranRewardItem.get(j);
				Integer hasValue = rewardItemMap.get(itemInfo.getItemID());
				if (hasValue == null) {
					rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum());
				} else {
					rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum() + hasValue.intValue());
				}
			}
		}

		List<RewardInfoMsg> rewardList = new ArrayList<RewardInfoMsg>();
		// 发送奖励
		for (Entry<Integer, Integer> e : rewardItemMap.entrySet()) {
			int key = e.getKey();
			int num = e.getValue();
			if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
				tableBattleTower.modifyCopperKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
				tableBattleTower.modifySilverKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
				tableBattleTower.modifyGoldKey(num);
			} else {
				player.getItemBagMgr().addItem(key, num);
			}

			RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
			rewardInfoMsg.setType(key);
			rewardInfoMsg.setCount(num);
			rewardList.add(rewardInfoMsg.build());
		}

		return rewardList;
	}
}