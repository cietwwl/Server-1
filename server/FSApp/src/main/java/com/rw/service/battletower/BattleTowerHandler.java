package com.rw.service.battletower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.BattleTowerMgr;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.battletower.pojo.BattleTowerHeroInfo;
import com.rwbase.dao.battletower.pojo.BattleTowerRoleInfo;
import com.rwbase.dao.battletower.pojo.BossCacheInfo;
import com.rwbase.dao.battletower.pojo.BossInfo;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerBossTemplate;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerConfigCfg;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerFloorCfg;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerRewardCfg;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerBossCfgDao;
import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerCommonCfgConfig;
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
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonRspMsg;
import com.rwproto.BattleTowerServiceProtos.BattleTowerConfig;
import com.rwproto.BattleTowerServiceProtos.BossInfoMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeStartRspMsg;
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
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;

/*
 * @author HC
 * @date 2015年9月1日 上午11:05:18
 * @Description 试练塔处理的Handler
 */
public class BattleTowerHandler {
	private static final int BOSS_RANDOM_RATE = 10000;

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
			SetFail(commonRsp, "打开试练塔界面", userId, "没有找到角色对应的试练塔数据", "数据异常");
			return;
		}

		int vipLevel = player.getVip();
		PrivilegeCfg cfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel);
		if (cfg == null) {
			SetFail(commonRsp, "打开试练塔界面", userId, String.format("Vip[%s]没有找打对应的配置表", vipLevel), "数据异常");
			return;
		}

		// int battleTowerResetTimes = cfg.getBattleTowerResetTimes();
		// by franky
		int battleTowerResetTimes = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.maxResetCount);

		// 填充消息
		rsp.setHighestFloor(tableBattleTower.getHighestFloor());
		rsp.setLeftResetTimes(battleTowerResetTimes - tableBattleTower.getResetTimes());
		final BattleTowerConfigCfg uniqueCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();// 唯一的配置

		final int curFloor = tableBattleTower.getCurFloor();// 当前层数
		boolean result = tableBattleTower.getResult();// 是否有了战斗结果
		final int highestFloor = tableBattleTower.getHighestFloor();

		// by franky 每层扫荡的用时
		final int theSweepTime4PerFloor = getSweepTimePerFloor(player, tableBattleTower, uniqueCfg);

		long now = System.currentTimeMillis();
		// 扫荡信息
		long sweepStartTime = tableBattleTower.getSweepStartTime();// 开始时间
		boolean sweepState = tableBattleTower.getSweepState();// 扫荡状态
		if (sweepState && sweepStartTime > 0) {
			int sweepStartFloor = tableBattleTower.getSweepStartFloor();
			int needTime = (int) TimeUnit.SECONDS.toMillis((highestFloor - sweepStartFloor + 1) * theSweepTime4PerFloor);// 扫荡完成需要的时间
			if (sweepStartTime + needTime < now) {// 已经完成了，发送奖励
				List<Integer> groupIdList = new ArrayList<Integer>();
				List<RewardInfoMsg> reward = reward(player, sweepStartFloor, highestFloor, tableBattleTower, groupIdList);// 收到的奖励信息

				// 更新数据
				tableBattleTower.setSweepStartTime(0);
				// by franky 扫荡结束时需要重置每层扫荡时间，下次开始扫荡就按照新的特权值进行设置
				tableBattleTower.setSweepTimePerFloor(0);
				tableBattleTower.setSweepState(false);
				tableBattleTower.setSweepStartFloor(0);
				tableBattleTower.setCurFloor(highestFloor);
				tableBattleTower.setResult(true);
				tableBattleTower.setRewardGroupId(groupIdList.get(groupIdList.size() - 1));
				rsp.setSweepFloor(highestFloor);// 扫荡结束了，必须设置当前扫荡层数为最高层

				rsp.addAllRewardInfoMsg(reward);
			} else {
				int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(now - sweepStartTime);// 经历的时间
				int addFloor = seconds / theSweepTime4PerFloor;// 要增加的层数
				int leftSecond = theSweepTime4PerFloor - (seconds % theSweepTime4PerFloor);// 剩余的时间

				rsp.setSweepFloor(sweepStartFloor + addFloor);
				rsp.setSweepFloorLeftTime(leftSecond);
				rsp.setSweepLeftTotalTime((int) TimeUnit.MILLISECONDS.toSeconds(needTime) - seconds);
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
			if (f <= highestFloor) {
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
			friendInfoMsg.setFloorGap(beforeFriendInfo.getFloor() - highestFloor);
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
				bossInfoMsg.setBossInFloor(bossInfo.getBossInFloor());
				rsp.addBossInfoMsg(bossInfoMsg);
			}
		}

		// 发送宝匣配置给客户端
		BattleTowerCommonCfgConfig commonCfgHelper = BattleTowerCommonCfgConfig.getInstance();
		rsp.setOpenBoxTip(commonCfgHelper.getOpenboxtip());
		rsp.setUseKeyCount(commonCfgHelper.getUsekeycount());

		// System.err.println(rsp.build());
		BattleTowerConfig.Builder config = BattleTowerConfig.newBuilder();
		config.setEveryFloorSweepTime(theSweepTime4PerFloor);
		commonRsp.setConfig(config);
		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		// 更新数据到数据库
		TableBattleTowerDao.getDao().update(tableBattleTower);
	}

	/**
	 * 获取扫荡的时间
	 * 
	 * @param player
	 * @param tableBattleTower
	 * @param uniqueCfg
	 * @return
	 */
	public static int getSweepTimePerFloor(Player player, TableBattleTower tableBattleTower, BattleTowerConfigCfg uniqueCfg) {
		int theSweepTime4PerFloor = tableBattleTower.getSweepTimePerFloor();
		if (theSweepTime4PerFloor <= 0) {
			theSweepTime4PerFloor = uniqueCfg.getTheSweepTime4PerFloor();
			theSweepTime4PerFloor -= player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.sweepTimeDec);
		}
		return theSweepTime4PerFloor;
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
			SetFail(commonRsp, "打开试练塔挑战界面", userId, "没有找到个人的试练塔数据", "数据异常");
			return;
		}

		OpenChallengeViewRspMsg.Builder rsp = OpenChallengeViewRspMsg.newBuilder();
		int curFloor = tableBattleTower.getCurFloor();// 如果是0的话就要把当前的层设为1
		if (curFloor == 0) {
			curFloor += 1;
		}

		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		BattleTowerFloorCfg floorCfg = cfgDao.getCfgById(String.valueOf(curFloor));
		if (floorCfg == null) {
			SetFail(commonRsp, "打开试练塔挑战界面", userId, String.format("没有找到对应%s层的配置表信息", curFloor), "数据异常");
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
				// bug fix : 封神台的交互处理有问题，告诉客户端处理最高层的问题
				rsp.setGroupId(-1);
				rsp.setIsFirst(false);
				rsp.setCopyId(0);
				commonRsp.setRspBody(rsp.build().toByteString());
				commonRsp.setRspState(EResponseState.RSP_SUCESS);
				// SetFail(commonRsp, "打开试练塔挑战界面", userId, "已经挑战到了最高层,只能重置", "已经挑战到最高层，请重置");
				return;
			}
		}

		// 奖励信息
		BattleTowerRewardCfg rewardCfg = (BattleTowerRewardCfg) BattleTowerRewardCfgDao.getCfgDao().getCfgById(String.valueOf(groupId));
		if (rewardCfg == null) {
			SetFail(commonRsp, "打开试练塔挑战界面", userId, String.format("没有找到对应%s层，对应组%s的奖励配置信息配置表没找到", curFloor, groupId), "数据异常");
			return;
		}

		rsp.setGroupId(groupId);
		int highestFloor = tableBattleTower.getHighestFloor();
		// modify by franky 将要打的层次(curFloor+1)比历史最高层次大才是首次打
		if (curFloor + 1 > highestFloor || highestFloor == 0) {
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
		boolean isBreak = tableBattleTower.isBreak();// 是否被中断
		rsp.setIsBreak(isBreak);

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
			SetFail(commonRsp, "获取试练塔战略", userId, String.format("获取战略获取不到第%s层的BattleTowerFloorCfg配置", floor), "没有攻略数据");
			return;
		}

		int groupId = floorCfg.getGroupId();
		TableBattleTowerStrategyDao strategyDao = TableBattleTowerStrategyDao.getDao();
		TableBattleTowerStrategy tableBattleTowerStrategy = strategyDao.getStrategy(groupId);// 获取战略数据
		if (tableBattleTowerStrategy == null) {
			SetFail(commonRsp, "获取试练塔战略", userId, String.format("获取战略第%s组的数据失败，没找到TableBattleTowerStrategy数据", groupId), "没有攻略数据");
			return;
		}

		List<BattleTowerRoleInfo> roleInfoList = tableBattleTowerStrategy.getRoleInfoList();
		int size = roleInfoList.size();

		ItemData playerMagic = player.getMagic();
		for (int i = 0; i < size; i++) {
			BattleTowerRoleInfo roleInfo = roleInfoList.get(i);
			// 转换成正式协议对象
			RankingRoleInfoMsg.Builder rankingRoleInfo = RankingRoleInfoMsg.newBuilder();
			rankingRoleInfo.setName(roleInfo.getName());
			rankingRoleInfo.setHeadIcon(roleInfo.getHeadIcon());
			rankingRoleInfo.setLevel(roleInfo.getLevel());
			if (StringUtils.isNotBlank(roleInfo.getHeadFrame())) {
				rankingRoleInfo.setHeadFrame(roleInfo.getHeadFrame());
			}
			String magicId = roleInfo.getMagicIcon();
			if (StringUtils.isNotBlank(magicId)) {
				// 正常玩家一定有法宝的！
				rankingRoleInfo.setMagicIcon(magicId);
			} else {
				// 装假狗：兼容旧玩家的数据，不去查询好友的法宝，太耗时，直接用玩家自己的
				rankingRoleInfo.setMagicIcon(String.valueOf(playerMagic.getModelId()));
			}
			rankingRoleInfo.setMagicLevel(roleInfo.getMagicLevel());
			String roleQualityId = roleInfo.getQualityId();
			if (StringUtils.isNotBlank(roleQualityId)) {
				rankingRoleInfo.setQualityId(roleQualityId);
			}
			rankingRoleInfo.setHighestFloor(roleInfo.getFloor());
			// 获取使用的角色信息
			String rankingUserId = roleInfo.getUserId();
			rankingRoleInfo.setIsMyself(userId.equals(rankingUserId));// 是否是自己

			List<BattleTowerHeroInfo> heroInfoList = roleInfo.getHeroInfoList();
			for (int j = 0, hSize = heroInfoList.size(); j < hSize; j++) {
				BattleTowerHeroInfo heroInfo = heroInfoList.get(j);
				// 转换成正式协议对象
				RankingHeroInfoMsg.Builder rankingHeroInfo = RankingHeroInfoMsg.newBuilder();
				rankingHeroInfo.setHeroId(heroInfo.getHeroId());
				rankingHeroInfo.setLevel(heroInfo.getLevel());
				rankingHeroInfo.setQuality(heroInfo.getQuality());
				String playerQualityId = heroInfo.getQualityId();
				if (StringUtils.isNotBlank(playerQualityId)) {
					rankingHeroInfo.setQualityId(playerQualityId);
				}
				rankingHeroInfo.setStarNum(heroInfo.getStarNum());
				rankingHeroInfo.setIsMainRole(heroInfo.isMainRole());
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
		int perPageSize = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg().getPerPageFriendSize();
		int offset = (pageIndex - 1) * perPageSize;
		offset = offset < 0 ? 0 : offset;

		// 填充消息
		ItemData playerMagic = player.getMagic();
		GetFriendBattleTowerRankInfoRspMsg.Builder rsp = GetFriendBattleTowerRankInfoRspMsg.newBuilder();
		for (int i = 0; i < size; i++) {
			TableBattleTowerRankIF rankInfo = friendRankList.get(i);

			BattleTowerRoleInfoIF roleInfo = rankInfo.getRoleInfo();
			// 填充消息
			RankingRoleInfoMsg.Builder rankingRoleInfo = RankingRoleInfoMsg.newBuilder();
			rankingRoleInfo.setName(roleInfo.getName());
			rankingRoleInfo.setHeadIcon(roleInfo.getHeadIcon());
			rankingRoleInfo.setLevel(roleInfo.getLevel());
			String magicId = roleInfo.getMagicIcon();
			if (StringUtils.isNotBlank(magicId)) {
				// 正常玩家一定有法宝的！
				rankingRoleInfo.setMagicIcon(magicId);
			} else {
				// 装假狗：兼容旧玩家的数据，不去查询好友的法宝，太耗时，直接用玩家自己的
				rankingRoleInfo.setMagicIcon(String.valueOf(playerMagic.getModelId()));
			}
			rankingRoleInfo.setMagicLevel(roleInfo.getMagicLevel());
			String roleQualityId = roleInfo.getQualityId();
			if (StringUtils.isNotBlank(roleQualityId)) {
				rankingRoleInfo.setQualityId(roleQualityId);
			}
			rankingRoleInfo.setRankIndex(offset + i + 1);
			rankingRoleInfo.setHighestFloor(roleInfo.getFloor());
			String friendUserId = roleInfo.getUserId();
			rankingRoleInfo.setIsMyself(userId.equals(friendUserId));// 是否是自己
			rankingRoleInfo.setStarNum(roleInfo.getStartNum());
			String headFrame = roleInfo.getHeadFrame();
			if (!StringUtils.isBlank(headFrame)) {
				rankingRoleInfo.setHeadFrame(headFrame);
			}

			// 获取使用的角色信息
			List<? extends BattleTowerHeroInfoIF> heroInfoList = roleInfo.getHeroInfoList();
			for (int j = 0, hSize = heroInfoList.size(); j < hSize; j++) {
				BattleTowerHeroInfoIF heroInfo = heroInfoList.get(j);
				// 转换成正式协议对象
				RankingHeroInfoMsg.Builder rankingHeroInfo = RankingHeroInfoMsg.newBuilder();
				rankingHeroInfo.setHeroId(heroInfo.getHeroId());
				rankingHeroInfo.setLevel(heroInfo.getLevel());
				rankingHeroInfo.setQuality(heroInfo.getQuality());
				String playerQualityId = heroInfo.getQualityId();
				if (StringUtils.isNotBlank(playerQualityId)) {
					rankingHeroInfo.setQualityId(playerQualityId);
				}
				rankingHeroInfo.setStarNum(heroInfo.getStarNum());
				rankingHeroInfo.setIsMainRole(heroInfo.isMainRole());
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
			SetFail(commonRsp, "打开试练塔试手气界面", player.getUserId(), "没有找到个人的试练塔数据", "数据异常");
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
			SetFail(commonRsp, "试练塔扫荡", userId, "没有个人的试练塔数据", "数据异常");
			return;
		}

		if (tableBattleTower.getSweepState()) {// 还在继续扫荡
			SetFail(commonRsp, "试练塔扫荡", userId, "当前的试练塔处于扫荡的状态中", "当前处于扫荡状态");
			return;
		}

		int highestFloor = tableBattleTower.getHighestFloor();
		int startFloor = req.getFloor();
		if (startFloor == highestFloor) {
			SetFail(commonRsp, "试练塔扫荡", userId, String.format("请求扫荡的开始层是%s，历史最高层是%s", startFloor, highestFloor), "已经扫荡到最高层，请重置！");
			return;
		} else if (startFloor <= 0 || startFloor > highestFloor) {
			SetFail(commonRsp, "试练塔扫荡", userId, String.format("请求扫荡的开始层是%s，历史最高层是%s", startFloor, highestFloor), "不能扫荡未通关层");
			return;
		}

		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();

		int curFloor = tableBattleTower.getCurFloor();// 当前层
		if (!tableBattleTower.getResult() && curFloor > 0) {// 还没有任何结果
			curFloor -= 1;
			tableBattleTower.setCurFloor(curFloor);
			tableBattleTower.setResult(false);
			// 更新数据
			dao.update(tableBattleTower);
		}

		if (startFloor - curFloor != 1) {
			SetFail(commonRsp, "试练塔扫荡", userId, String.format("请求开始的层%s，当前层是%s，请求开始层低于或者超过当前层1层", startFloor, curFloor), "请求扫荡数据异常");
			return;
		}

		curFloor = curFloor == 0 ? 1 : curFloor;
		BattleTowerFloorCfg cfg = (BattleTowerFloorCfg) cfgDao.getCfgById(String.valueOf(curFloor));
		if (cfg == null) {
			SetFail(commonRsp, "试练塔扫荡", userId, String.format("当前层%s获取BattleTowerFloorCfg的配置表为null", curFloor), "数据异常");
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
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, "挑战的新层，没有找到对应的" + curFloor + "层的配置表", "数据异常");
				return;
			}

			groupId = cfg.getGroupId();
			floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
			if (floorList.isEmpty()) {
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId), "数据异常");
				return;
			}
		}

		if (!floorList.contains(startFloor)) {// 当前里程碑不包含这个等级
			SetFail(commonRsp, "试练塔扫荡", userId, String.format("组%s的数据中没有包含请求开始的层", groupId), "数据异常");
			return;
		}

		long now = System.currentTimeMillis();
		// 次数没有超过上限，可以随机产生Boss
		Random r = new Random();
		BattleTowerConfigCfg uniqueCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();
		int perDayBossSize = uniqueCfg.getPerDayBossSize();// 每天产生Boss的上限数量
		int theSweepTime4PerFloor = uniqueCfg.getTheSweepTime4PerFloor();// 每层扫荡需要的时间（秒）
		// by franky
		theSweepTime4PerFloor -= player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.sweepTimeDec);

		if (tableBattleTower.getCurBossTimes() < perDayBossSize) {
			int leftBossSize = perDayBossSize - tableBattleTower.getCurBossTimes();// 剩下产生几个Boss

			BattleTowerBossCfgDao bossCfgDao = BattleTowerBossCfgDao.getCfgDao();

			List<BattleTowerFloorCfg> allCfg = cfgDao.getAllCfg();
			Collections.sort(allCfg, comparator);// 排序

			ArrayList<Integer> generatedBossIdList = new ArrayList<Integer>();
			List<BossInfo> lst = tableBattleTower.getBossInfoList();
			for (BossInfo bossInfo : lst) {
				generatedBossIdList.add(bossInfo.getBossId());
			}

			for (int i = 0, size = allCfg.size(); i < size; i++) {
				if (leftBossSize <= 0) {// 最后没有次数了，就直接退出了
					break;
				}

				BattleTowerFloorCfg cfg0 = allCfg.get(i);
				int floor = cfg0.getFloor();
				if (floor >= startFloor && floor <= highestFloor && cfg0.getBossPro() > 0) {
					int rNum = r.nextInt(BOSS_RANDOM_RATE);// 100中随机
					if (rNum >= 0 && rNum < cfg0.getBossPro()) {// 随机到了
						BattleTowerBossTemplate boss = bossCfgDao.ranBossInfo(player.getLevel(), generatedBossIdList, false);
						if (boss != null) {
							// Boss信息
							BossInfo bossInfo = new BossInfo();
							bossInfo.setBossId(boss.getBossId());// Boss的模版Id
							bossInfo.setBossStartTime(now + TimeUnit.SECONDS.toMillis((floor - curFloor) * theSweepTime4PerFloor));// Boss出现的时间
							bossInfo.setBossInGroupId(cfg0.getGroupId());// Boss所在的组
							bossInfo.setBossInFloor(floor);// Boss出现的层

							int bossId = tableBattleTower.addBossInfo(bossInfo);
							generatedBossIdList.add(boss.getBossId());
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
							BattleTowerBossTemplate boss = bossCfgDao.ranBossInfo(player.getLevel(), generatedBossIdList, true);
							if (boss != null) {
								// Boss信息
								BossInfo bossInfo = new BossInfo();
								bossInfo.setBossId(boss.getBossId());// Boss的模版Id
								bossInfo.setBossStartTime(now + TimeUnit.SECONDS.toMillis((floor - curFloor) * theSweepTime4PerFloor));// Boss出现的时间
								bossInfo.setBossInGroupId(cfg0.getGroupId());// Boss所在的组
								bossInfo.setBossInFloor(floor);// Boss出现的层

								int bossId = tableBattleTower.addBossInfo(bossInfo);
								generatedBossIdList.add(boss.getBossId());
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
		tableBattleTower.setSweepTimePerFloor(theSweepTime4PerFloor);
		dao.update(tableBattleTower);// 更新数据
		// 发送协议
		BattleTowerConfig.Builder config = BattleTowerConfig.newBuilder();
		config.setEveryFloorSweepTime(theSweepTime4PerFloor);
		rsp.setTotalNeedSeconds(theSweepTime4PerFloor * (highestFloor - curFloor + 1)); // 告诉客户端需要的时间
		commonRsp.setConfig(config);
		commonRsp.setRspBody(rsp.build().toByteString());
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		// 封神台通知日常，这里假设可扫荡层数大于1层，开始扫荡马上通知一次，扫荡完成的时候可造成通知日常总次数-1(结果保证>0)
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CHALLEGE_BATTLETOWER, 1);
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
			SetFail(commonRsp, "试练塔扫荡结束", userId, "没有个人的试练塔数据为", "数据异常");
			return;
		}

		// 没有在扫荡，就不用领取奖励
		if (!tableBattleTower.getSweepState()) {
			SetFail(commonRsp, "试练塔扫荡结束", userId, "当前不是扫荡状态", "当前不是扫荡状态");
			return;
		}

		// 单层扫荡时间
		// by franky
		BattleTowerConfigCfg uniqueCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();// 唯一的配置
		int theSweepTime4PerFloor = getSweepTimePerFloor(player, tableBattleTower, uniqueCfg);

		// 时间验证
		long now = System.currentTimeMillis();
		int sweepStartFloor = tableBattleTower.getSweepStartFloor();
		int highestFloor = tableBattleTower.getHighestFloor();
		long needTime = TimeUnit.SECONDS.toMillis((highestFloor - sweepStartFloor + 1) * theSweepTime4PerFloor);

		// 检查时间是否已经到了完成时间
		if (tableBattleTower.getSweepStartTime() + needTime > now) {
			SetFail(commonRsp, "试练塔扫荡结束", userId, "扫荡结束时间未到", "扫荡结束时间未到");
			return;
		}

		List<Integer> groupIdList = new ArrayList<Integer>();// 可以获取奖励的组Id
		List<RewardInfoMsg> reward = reward(player, sweepStartFloor, highestFloor, tableBattleTower, groupIdList);
		rsp.addAllRewardInfoMsg(reward);

		// 更新数据
		tableBattleTower.setSweepStartTime(0);
		// by franky 扫荡结束时需要重置每层扫荡时间，下次开始扫荡就按照新的特权值进行设置
		tableBattleTower.setSweepTimePerFloor(0);
		tableBattleTower.setSweepState(false);
		tableBattleTower.setSweepStartFloor(0);
		tableBattleTower.setCurFloor(highestFloor);
		tableBattleTower.setResult(true);
		tableBattleTower.setRewardGroupId(groupIdList.get(groupIdList.size() - 1));

		UserEventMgr.getInstance().BattleTower(player, highestFloor);
		dao.update(tableBattleTower);

		// 发送协议
		BattleTowerConfig.Builder config = BattleTowerConfig.newBuilder();
		config.setEveryFloorSweepTime(theSweepTime4PerFloor);
		commonRsp.setConfig(config);
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		commonRsp.setRspBody(rsp.build().toByteString());
		int dis = highestFloor - sweepStartFloor;
		// 封神台通知日常，这里假设可扫荡层数大于1层，开始扫荡马上通知一次，扫荡完成的时候可造成通知日常总次数-1(结果保证>0)
		if (dis > 0) {
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CHALLEGE_BATTLETOWER, dis);
		}
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
			SetFail(commonRsp, "试练塔试手气", userId, "没有个人的试练塔数据", "数据异常");
			return;
		}

		EKeyType keyType = req.getKeyType();
		int useNum = req.getUseNum();
		if (useNum <= 0) {
			SetFail(commonRsp, "试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，使用数量<=0", keyType, useNum), "至少使用一把钥匙");
			return;
		}

		BattleTowerConfigCfgDao configCfgDao = BattleTowerConfigCfgDao.getCfgDao();
		BattleTowerConfigCfg uniqueCfg = configCfgDao.getUniqueCfg();// 唯一的配置

		String[] dropArr;
		// Map<Integer, Integer> rewardItemMap = new HashMap<Integer, Integer>();// 奖励的Map
		if (keyType == EKeyType.KEY_COPPER) {// 铜
			if (useNum > tableBattleTower.getCopper_key()) {
				SetFail(commonRsp, "试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，现有数量%s，数量不足", keyType, useNum, tableBattleTower.getCopper_key()), "钥匙数量不足");
				return;
			}

			tableBattleTower.modifyCopperKey(-useNum);
			dropArr = uniqueCfg.getCopperKeyDropIdArr();
		} else if (keyType == EKeyType.KEY_SILVER) {// 银
			if (useNum > tableBattleTower.getSilver_key()) {
				SetFail(commonRsp, "试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，现有数量%s，数量不足", keyType, useNum, tableBattleTower.getSilver_key()), "钥匙数量不足");
				return;
			}

			tableBattleTower.modifySilverKey(-useNum);
			dropArr = uniqueCfg.getSilverKeyDropIdArr();
		} else if (keyType == EKeyType.KEY_GOLD) {// 金
			if (useNum > tableBattleTower.getGold_key()) {
				SetFail(commonRsp, "试练塔试手气", userId, String.format("钥匙类型是%s，钥匙数量是%s，现有数量%s，数量不足", keyType, useNum, tableBattleTower.getGold_key()), "钥匙数量不足");
				return;
			}

			tableBattleTower.modifyGoldKey(-useNum);
			dropArr = uniqueCfg.getGoldKeyDropIdArr();
		} else {
			SetFail(commonRsp, "试练塔试手气", userId, "客户端发送了一个未知的钥匙类型", "钥匙数量不足");
			return;
		}
		UserEventMgr.getInstance().UseSilverKeyVitality(player, keyType, useNum);
		BattleTowerRewardCfgDao rewardCfgDao = BattleTowerRewardCfgDao.getCfgDao();
		// 奖励的物品
		// for (int i = 0; i < useNum; i++) {
		// List<ItemInfo> ranRewardItem = rewardCfgDao.getRanRewardItem(dropArr, player);
		// for (int j = 0, itemSize = ranRewardItem.size(); j < itemSize; j++) {
		// ItemInfo itemInfo = ranRewardItem.get(j);
		// Integer hasValue = rewardItemMap.get(itemInfo.getItemID());
		// if (hasValue == null) {
		// rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum());
		// } else {
		// rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum() + hasValue.intValue());
		// }
		// }
		// }
		//
		// // 发送奖励
		// for (Entry<Integer, Integer> e : rewardItemMap.entrySet()) {
		// int key = e.getKey();
		// int num = e.getValue();
		// if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
		// tableBattleTower.modifyCopperKey(num);
		// } else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
		// tableBattleTower.modifySilverKey(num);
		// } else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
		// tableBattleTower.modifyGoldKey(num);
		// } else {
		// player.getItemBagMgr().addItem(key, num);
		// }
		//
		// RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
		// rewardInfoMsg.setType(key);
		// rewardInfoMsg.setCount(num);
		// rsp.addRewardInfoMsg(rewardInfoMsg);
		// }
		List<ItemInfo> rewardItems = new ArrayList<ItemInfo>();
		for (int i = 0; i < useNum; i++) {
			List<ItemInfo> ranRewardItem = rewardCfgDao.getRanRewardItem(dropArr, player);
			rewardItems.addAll(ranRewardItem);
		}
		List<ItemInfo> combine = combine(rewardItems);
		List<RewardInfoMsg> rewardInfoMsgList = sendReward(player, tableBattleTower, combine);
		rsp.addAllRewardInfoMsg(rewardInfoMsgList);

		// 开服活动通知：
		player.getFresherActivityMgr().doCheck(eActivityType.A_OpenBox);

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
			SetFail(commonRsp, "重置试练塔次数", userId, "没有个人的试练塔数据", "数据异常");
			return;
		}

		if (tableBattleTower.getSweepState()) {// 还在扫荡
			SetFail(commonRsp, "重置试练塔次数", userId, "当前还在扫荡的过程中", "扫荡中不能重置数据");
			return;
		}

		/*
		 * int vipLevel = player.getVip(); PrivilegeCfg cfg = PrivilegeCfgDAO.getInstance().getCfg(vipLevel); if (cfg == null) { SetFail(commonRsp, "重置试练塔次数", userId,
		 * String.format("角色是Vip%s，并没有找到PrivilegeCfg的配置表", vipLevel), "数据异常"); return; } int battleTowerResetTimes = cfg.getBattleTowerResetTimes();
		 */
		// by franky
		int battleTowerResetTimes = player.getPrivilegeMgr().getIntPrivilege(PvePrivilegeNames.maxResetCount);
		if (tableBattleTower.getResetTimes() >= battleTowerResetTimes) {// 重置次数满了
			SetFail(commonRsp, "重置试练塔次数", userId, "今天的重置次数已经用完了", "今日重置次数已经用完");
			return;
		}

		// 重置数据
		tableBattleTower.resetBattleTowerData();
		dao.update(tableBattleTower);
		UserFeatruesMgr.getInstance().doFinish(player, UserFeaturesEnum.battleTower);
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
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, "没有找到角色对应的试练塔数据信息", "数据异常");
			return;
		}

		// 检测当前层是否是被中断的
		boolean isBreak = tableBattleTower.isBreak();// 是否被中断
		if (isBreak) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, "当前某层挑战失败，不能继续进行挑战", "挑战中断");
			return;
		}

		final int floor = req.getFloor();// 要挑战的层数
		int curFloor = tableBattleTower.getCurFloor();// 当前层
		if (!tableBattleTower.getResult() && curFloor > 0) {// 还没有任何结果
			curFloor -= 1;
			tableBattleTower.setCurFloor(curFloor);
			tableBattleTower.setResult(false);
			// 更新数据
			dao.update(tableBattleTower);
		}

		curFloor = curFloor == 0 ? 1 : curFloor;

		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		BattleTowerFloorCfg floorCfg = cfgDao.getCfgById(String.valueOf(curFloor));
		if (floorCfg == null) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, "没有找到对应的" + curFloor + "层的配置表", "数据异常");
			return;
		}

		int groupId = floorCfg.getGroupId();
		List<Integer> floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
		if (floorList.isEmpty()) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId), "数据异常");
			return;
		}

		// 检查一下是不是最后一层
		int lastFloor = floorList.get(floorList.size() - 1);
		if (curFloor == lastFloor) {
			curFloor++;// 下一层加一
			floorCfg = cfgDao.getCfgById(String.valueOf(curFloor));
			if (floorCfg == null) {
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, "挑战的新层，没有找到对应的" + curFloor + "层的配置表", "数据异常");
				return;
			}

			groupId = floorCfg.getGroupId();
			floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
			if (floorList.isEmpty()) {
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，包含的层数列表是空的", curFloor, groupId), "数据异常");
				return;
			}
		}

		if (floor != curFloor) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("请求层和应该打的层数不一致，请求%s层,应该打第%s层", floor, curFloor), "数据异常");
			return;
		}

		if (floor != floorList.get(0)) {
			SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，请求打的不是该组第一层数据", curFloor, floorCfg.getGroupId()), "请求数据异常");
			return;
		}

		// TODO 验证客户端的copyID
		if (req.hasCopyId()) {// 为兼容旧的客户端，没有设置为required字段
			int clientCopyId = req.getCopyId();
			BattleTowerRewardCfg rewardCfg = BattleTowerRewardCfgDao.getCfgDao().getCfgById(String.valueOf(groupId));
			if (rewardCfg != null && rewardCfg.getCopyId() != clientCopyId) {
				ChallengeStartRspMsg.Builder rsp = ChallengeStartRspMsg.newBuilder();
				rsp.setCopyId(rewardCfg.getCopyId());
				commonRsp.setRspBody(rsp.build().toByteString());
				SetFail(commonRsp, "试练塔模块-战斗开始", userId, String.format("第%s层，对应的是第%s组，请求打的不是该组的copyID，客户端的是:%s,服务器计算的是:%s", curFloor, floorCfg.getGroupId(), clientCopyId, rewardCfg.getCopyId()), "请求数据异常，请重试");
				return;
			}
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
			SetFail(commonRsp, "试练塔模块-战斗结束", userId, "没有找到个人的试练塔数据", "数据异常");
			return;
		}

		int floor = req.getFloor();// 打到了多少层
		int curFloor = tableBattleTower.getCurFloor();// 当前层
		boolean result = req.getResult();// 结果
		curFloor = curFloor == 0 ? 1 : curFloor;

		// 检测当前层是否是被中断的
		boolean isBreak = tableBattleTower.isBreak();// 是否被中断
		if (isBreak) {
			SetFail(commonRsp, "试练塔模块-战斗结束", userId, "当前某层已经挑战失败而导致整个挑战中断，不能继续进行挑战，必须要重置", "挑战中断");
			return;
		}

		// 层信息
		BattleTowerFloorCfgDao cfgDao = BattleTowerFloorCfgDao.getCfgDao();
		BattleTowerFloorCfg floorCfg = cfgDao.getCfgById(String.valueOf(curFloor));
		if (floorCfg == null) {
			SetFail(commonRsp, "试练塔模块-战斗结束", userId, "没有找到对应" + curFloor + "层的数据", "数据异常");
			return;
		}

		// 组信息
		final int groupId = floorCfg.getGroupId();// 当前层属于的组
		List<Integer> floorList = cfgDao.getContainFloorList(String.valueOf(groupId));
		if (floorList.isEmpty()) {
			SetFail(commonRsp, "试练塔模块-战斗结束", userId, String.format("对应的层%s，所属的组%s，包含层的信息列表是空", curFloor, groupId), "数据异常");
			return;
		}

		if (!floorList.contains(floor)) {// 服务器存储的组信息没有包含完成的信息，有作弊嫌疑
			SetFail(commonRsp, "试练塔模块-战斗结束", userId, String.format("发送完成的层是%s，所属组是%s，这个组并没有包含请求完成的层信息", floor, groupId), "当前组没有请求层信息");
			return;
		}

		// 响应消息
		ChallengeEndRspMsg.Builder rsp = ChallengeEndRspMsg.newBuilder();

		if (tableBattleTower.getResult()) {
			SetFail(commonRsp, "试练塔模块-战斗结束", userId, "当前层已经有了战斗结果，不能重复发送结果", "不能重复发送战斗结果");
			return;
		}

		tableBattleTower.setResult(true);// 设置已经拿到战斗结果的标记
		if (!result) {// 失败了
			// by frnaky 战败了允许再次挑战
			tableBattleTower.setCurFloor(floorList.get(0));// 设置当前新的层
			// tableBattleTower.setBreak(true);
		} else {// 成功
			if (floor < curFloor) {
				SetFail(commonRsp, "试练塔模块-战斗结束", userId, String.format("请求胜利%s层，存储的是%s层，请求胜利的层<=当前存储的层", floor, curFloor), "不能跳过当前层进行挑战");
				return;
			}

			// 角色信息
			BattleTowerRoleInfo roleInfo = new BattleTowerRoleInfo(userId);
			roleInfo.setFloor(floor);
			String playerHeadImage = player.getHeadImage();
			roleInfo.setHeadIcon(playerHeadImage);
			roleInfo.setLevel(player.getLevel());
			roleInfo.setName(player.getUserName());
			roleInfo.setStartNum(player.getStarLevel());
			String playerHeadFrame = player.getHeadFrame();
			if (!StringUtils.isBlank(playerHeadFrame)) {
				roleInfo.setHeadFrame(playerHeadFrame);
			}
			Hero playerMainHero = player.getMainRoleHero();
			// RoleBaseInfoIF playerMainInfo = playerMainHero.getRoleBaseInfoMgr().getBaseInfo();
			roleInfo.setQualityId(playerMainHero.getQualityId());

			// 法宝
			ItemData magic = player.getMagic();
			if (magic != null) {
				roleInfo.setMagicIcon(String.valueOf(magic.getModelId()));
				roleInfo.setMagicLevel(magic.getMagicLevel());
			}

			// 阵容中的英雄信息
			List<RankingHeroInfoMsg> rankingHeroInfoMsgList = req.getRankingHeroInfoMsgList();
			int size = rankingHeroInfoMsgList.size();

			HeroMgr playerHeroMgr = player.getHeroMgr();
			List<BattleTowerHeroInfo> heroInfoList = new ArrayList<BattleTowerHeroInfo>(size);
			for (int i = 0; i < size; i++) {
				RankingHeroInfoMsg heroInfoMsg = rankingHeroInfoMsgList.get(i);
				BattleTowerHeroInfo heroInfo = new BattleTowerHeroInfo();

				if (heroInfoMsg.hasHeroUUID()) {
					// Hero hero = playerHeroMgr.getHeroById(heroInfoMsg.getHeroUUID());
					Hero hero = playerHeroMgr.getHeroById(player, heroInfoMsg.getHeroUUID());
					if (hero != null) {
						RoleQualityCfg qualityCfg = RoleQualityCfgDAO.getInstance().getCfgById(hero.getQualityId());
						heroInfo.setHeroId(heroInfoMsg.getHeroId());
						heroInfo.setLevel(hero.getLevel());
						heroInfo.setQualityId(hero.getQualityId());
						heroInfo.setQuality(qualityCfg != null ? qualityCfg.getQuality() : 0);
						heroInfo.setStarNum(hero.getStarLevel());
						heroInfo.setMainRole(hero.isMainRole());
						heroInfoList.add(heroInfo);
						continue;
					}
				}
				// 兼容旧的客户端
				heroInfo.setHeroId(heroInfoMsg.getHeroId());
				heroInfo.setLevel(heroInfoMsg.getLevel());
				heroInfo.setQuality(heroInfoMsg.getQuality());
				heroInfo.setStarNum(heroInfoMsg.getStarNum());
				heroInfo.setMainRole(heroInfoMsg.getIsMainRole());
				heroInfoList.add(heroInfo);
			}

			roleInfo.setHeroInfoList(heroInfoList);

			// 奖励模版
			BattleTowerRewardCfgDao rewardCfgDao = BattleTowerRewardCfgDao.getCfgDao();
			BattleTowerRewardCfg rewardCfg = rewardCfgDao.getCfgById(String.valueOf(groupId));

			boolean isLastFloor = false;
			if (floor == floorList.get(floorList.size() - 1)) {// 是最后一层
				isLastFloor = true;
			}

			// 设置当前新的层
			tableBattleTower.setCurFloor(floor);// 设置当前新的层

			if (result) {// TODO 获取新的当前层，即是下一层的copyID，并返回给客户端
				BattleTowerFloorCfg nextFloorCfg = cfgDao.getCfgById(String.valueOf(floor + 1));
				if (nextFloorCfg != null) {
					int nextGroupId = nextFloorCfg.getGroupId();
					BattleTowerRewardCfg nextRewoardCfg = rewardCfgDao.getCfgById(String.valueOf(nextGroupId));
					if (nextRewoardCfg != null) {
						rsp.setCopyId(nextRewoardCfg.getCopyId());
					}
				}
			}

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
				// for (int i = 0, size0 = itemInfoList.size(); i < size0; i++) {
				// ItemInfo itemInfo = itemInfoList.get(i);
				// int key = itemInfo.getItemID();
				// int num = itemInfo.getItemNum();
				// if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
				// tableBattleTower.modifyCopperKey(num);
				// } else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
				// tableBattleTower.modifySilverKey(num);
				// } else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
				// tableBattleTower.modifyGoldKey(num);
				// } else {
				// player.getItemBagMgr().addItem(key, num);
				// }
				//
				// RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
				// rewardInfoMsg.setType(key);
				// rewardInfoMsg.setCount(num);
				// rsp.addRewardInfoMsg(rewardInfoMsg);
				// }
				List<RewardInfoMsg> rewardInfoMsgList = sendReward(player, tableBattleTower, itemInfoList);
				rsp.addAllRewardInfoMsg(rewardInfoMsgList);
			}

			// Boss信息
			BattleTowerConfigCfg configCfg = BattleTowerConfigCfgDao.getCfgDao().getUniqueCfg();
			int perDayBossSize = configCfg.getPerDayBossSize();// 当天可以产生的Boss数量

			Random r = new Random();
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

					int rNum = r.nextInt(BOSS_RANDOM_RATE);
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
					ArrayList<Integer> generatedBossIdList = new ArrayList<Integer>(tableBattleTower.getBossInfoList().size());
					List<BossInfo> lst = tableBattleTower.getBossInfoList();
					for (BossInfo bossInfo : lst) {
						generatedBossIdList.add(bossInfo.getBossId());
					}
					BattleTowerBossTemplate ranBossInfo = BattleTowerBossCfgDao.getCfgDao().ranBossInfo(player.getLevel(), generatedBossIdList, false);
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
			UserEventMgr.getInstance().BattleTower(player, floor);
		}

		dao.update(tableBattleTower);
		// 开服活动通知
		player.getFresherActivityMgr().doCheck(eActivityType.A_Tower);

		// 通知角色日常任务 by Alex
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.CHALLEGE_BATTLETOWER, 1);

		// 通知角色任务by Alex
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Challage_BattleTower);

		// 到这里就算成功了
		commonRsp.setRspState(EResponseState.RSP_SUCESS);
		commonRsp.setRspBody(rsp.build().toByteString());

	}

	private static void SetFail(BattleTowerCommonRspMsg.Builder commonRsp, String module, String userId, String reason, String tipMsg) {
		GameLog.error(module, userId, reason);
		commonRsp.setRspState(EResponseState.RSP_FAIL);
		if (!StringUtils.isBlank(tipMsg))
			commonRsp.setTips(tipMsg);
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
			SetFail(commonRsp, "挑战Boss开始", userId, "获取个人的试练塔数据为null", "数据异常");
			return;
		}

		int bossId = req.getBossId();
		BossInfo bossInfo = tableBattleTower.getBoss(bossId);
		if (bossInfo == null) {
			SetFail(commonRsp, "挑战Boss开始", userId, String.format("挑战Boss的Id为%s，没有找到对应数据", bossId), "Boss不存在");
			return;
		}

		boolean hasChange = false;
		if (!bossInfo.isHasFight()) {
			bossInfo.setHasFight(true);// 设置已经打过这个Boss
			hasChange = true;
		}

		if (tableBattleTower.getChallengeBossId() != bossId) {
			tableBattleTower.setChallengeBossId(bossId);
			hasChange = true;
		}

		if (hasChange) {
			dao.update(tableBattleTower);
		}

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
			SetFail(commonRsp, "挑战Boss结束", userId, "获取个人的试练塔数据为null", "数据异常");
			return;
		}

		int challengeBossId = tableBattleTower.getChallengeBossId();
		if (challengeBossId <= 0) {// 如果当前缓存的BossId是0,就证明根本没缓存Boss数据
			SetFail(commonRsp, "挑战Boss结束", userId, "角色可能作弊了，因为缓存的挑战Boss数据是没有的", "当前没有Boss数据");
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
			SetFail(commonRsp, "挑战Boss结束", userId, String.format("客户端请求的挑战BossId为%s，服务器缓存的是%s", bossId, challengeBossId), "请求数据异常");
			return;
		}

		BossInfo bossInfo = tableBattleTower.getBoss(bossId);
		if (bossInfo == null) {
			SetFail(commonRsp, "挑战Boss结束", userId, String.format("挑战Boss的Id为%s，没有找到对应数据", bossId), "Boss不存在");
			return;
		}

		// 开始准备处理Boss的奖励
		BattleTowerBossCfgDao bossCfgDao = BattleTowerBossCfgDao.getCfgDao();
		BattleTowerBossTemplate bossCfg = bossCfgDao.getBossTemplate(bossInfo.getBossId());
		if (bossCfg == null) {
			SetFail(commonRsp, "挑战Boss结束", userId, String.format("对应的Boss模版Id为%s的BossCfg模版不存在", bossInfo.getBossId()), "Boss不存在");
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

		// for (int i = 0, size0 = itemInfoList.size(); i < size0; i++) {
		// ItemInfo itemInfo = itemInfoList.get(i);
		// int key = itemInfo.getItemID();
		// int num = itemInfo.getItemNum();
		// if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
		// tableBattleTower.modifyCopperKey(num);
		// } else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
		// tableBattleTower.modifySilverKey(num);
		// } else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
		// tableBattleTower.modifyGoldKey(num);
		// } else {
		// player.getItemBagMgr().addItem(key, num);
		// }
		//
		// RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
		// rewardInfoMsg.setType(key);
		// rewardInfoMsg.setCount(num);
		// rsp.addRewardInfoMsg(rewardInfoMsg);
		// }

		List<RewardInfoMsg> rewardInfoMsgList = sendReward(player, tableBattleTower, itemInfoList);
		rsp.addAllRewardInfoMsg(rewardInfoMsgList);
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

		// Map<Integer, Integer> rewardItemMap = new HashMap<Integer, Integer>();// 奖励的Map
		// for (int i = 0, size = groupIdList.size(); i < size; i++) {
		// BattleTowerRewardCfg cfg = (BattleTowerRewardCfg) rewardCfgDao.getCfgById(String.valueOf(groupIdList.get(i)));
		// if (cfg == null) {
		// continue;
		// }
		//
		// List<ItemInfo> ranRewardItem = rewardCfgDao.getRanRewardItem(cfg.getDropIdArr(), player);
		// for (int j = 0, itemSize = ranRewardItem.size(); j < itemSize; j++) {
		// ItemInfo itemInfo = ranRewardItem.get(j);
		// Integer hasValue = rewardItemMap.get(itemInfo.getItemID());
		// if (hasValue == null) {
		// rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum());
		// } else {
		// rewardItemMap.put(itemInfo.getItemID(), itemInfo.getItemNum() + hasValue.intValue());
		// }
		// }
		// }
		//
		// List<RewardInfoMsg> rewardList = new ArrayList<RewardInfoMsg>();
		// // 发送奖励
		// for (Entry<Integer, Integer> e : rewardItemMap.entrySet()) {
		// int key = e.getKey();
		// int num = e.getValue();
		// if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
		// tableBattleTower.modifyCopperKey(num);
		// } else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
		// tableBattleTower.modifySilverKey(num);
		// } else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
		// tableBattleTower.modifyGoldKey(num);
		// } else {
		// player.getItemBagMgr().addItem(key, num);
		// }
		//
		// RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
		// rewardInfoMsg.setType(key);
		// rewardInfoMsg.setCount(num);
		// rewardList.add(rewardInfoMsg.build());
		// }
		//
		// return rewardList;
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		for (int i = 0, size = groupIdList.size(); i < size; i++) {
			BattleTowerRewardCfg cfg = rewardCfgDao.getCfgById(String.valueOf(groupIdList.get(i)));
			if (cfg == null) {
				continue;
			}
			list.addAll(rewardCfgDao.getRanRewardItem(cfg.getDropIdArr(), player));
		}
		List<ItemInfo> combinList = combine(list);
		return sendReward(player, tableBattleTower, combinList);
	}

	private static List<ItemInfo> combine(List<ItemInfo> list) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0, size = list.size(); i < size; i++) {
			ItemInfo itemInfo = list.get(i);
			Integer hasValue = map.get(itemInfo.getItemID());
			if (hasValue == null) {
				map.put(itemInfo.getItemID(), itemInfo.getItemNum());
			} else {
				map.put(itemInfo.getItemID(), itemInfo.getItemNum() + hasValue.intValue());
			}
		}
		List<ItemInfo> copyList = new ArrayList<ItemInfo>(map.size());
		for (Iterator<Map.Entry<Integer, Integer>> itr = map.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<Integer, Integer> entry = itr.next();
			copyList.add(new ItemInfo(entry.getKey(), entry.getValue()));
		}
		return copyList;
	}

	private static List<RewardInfoMsg> sendReward(Player player, TableBattleTower tableBattleTower, List<ItemInfo> rewardItems) {
		List<RewardInfoMsg> rewardInfoMsgList = new ArrayList<RewardInfoMsg>();
		List<ItemInfo> addItems = new ArrayList<ItemInfo>();
		for (int i = 0, size0 = rewardItems.size(); i < size0; i++) {
			ItemInfo itemInfo = rewardItems.get(i);
			int key = itemInfo.getItemID();
			int num = itemInfo.getItemNum();
			if (key == eSpecialItemId.BATTLE_TOWER_COPPER_KEY.getValue()) {
				tableBattleTower.modifyCopperKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_SILVER_KEY.getValue()) {
				tableBattleTower.modifySilverKey(num);
			} else if (key == eSpecialItemId.BATTLE_TOWER_GOLD_KEY.getValue()) {
				tableBattleTower.modifyGoldKey(num);
			} else {
				addItems.add(new ItemInfo(key, num));
			}

			RewardInfoMsg.Builder rewardInfoMsg = RewardInfoMsg.newBuilder();
			rewardInfoMsg.setType(key);
			rewardInfoMsg.setCount(num);
			rewardInfoMsgList.add(rewardInfoMsg.build());
		}
		if (addItems.size() > 1) {
			ItemBagMgr.getInstance().addItem(player, addItems);
		} else if (addItems.size() > 0) {
			ItemInfo temp = addItems.get(0);
			ItemBagMgr.getInstance().addItem(player, temp.getItemID(), temp.getItemNum());
		}
		return rewardInfoMsgList;
	}
}