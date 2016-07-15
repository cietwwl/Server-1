package com.rw.service.TaoistMagic;

import org.apache.commons.lang3.StringUtils;

import com.common.RefInt;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.UserGameDataMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistConsumeCfgHelper;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwproto.TaoistMagicProtos.ErrorCode_Taoist;
import com.rwproto.TaoistMagicProtos.TaoistInfo;
import com.rwproto.TaoistMagicProtos.TaoistRequest;
import com.rwproto.TaoistMagicProtos.TaoistResponse;
import com.rwproto.TaoistMagicProtos.TaoistResponse.Builder;

public class TaoistHandler {
	private static TaoistHandler instance;

	private TaoistHandler() {
	}

	public static TaoistHandler getInstance() {
		if (instance == null) {
			instance = new TaoistHandler();
		}
		return instance;
	}

	public ByteString upgradeTaoist(Player player, TaoistRequest req) {
		TaoistResponse.Builder response = TaoistResponse.newBuilder();
		int tid = req.getTaoistId();
		TaoistMagicCfgHelper helper = TaoistMagicCfgHelper.getInstance();
		TaoistMagicCfg cfg = helper.getCfgById(String.valueOf(tid));
		if (cfg == null) {
			return ErrorResponse("无效道术技能ID", ":" + tid, ErrorCode_Taoist.IllegalArguments, response, player, req);
		}

		int upgradeCount = req.getUpgradeCount();
		int plvl = player.getLevel();
		ITaoistMgr taoistMgr = player.getTaoistMgr();
		int currentLvl = taoistMgr.getLevel(tid);
		int newLevel = currentLvl + upgradeCount;

		UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
		TaoistConsumeCfgHelper consumeHelper = TaoistConsumeCfgHelper.getInstance();

		int consumeId = cfg.getConsumeId();
		int maxCfgLevel = consumeHelper.getMaxLevel(consumeId);
		if (newLevel > maxCfgLevel) {
			GameLog.info("道术", player.getUserId(),
					"升级道术不能超过最大配置等级,当前等级:" + currentLvl + "升级次数:" + upgradeCount + ",最大配置等级" + maxCfgLevel, null);
		}

		if (newLevel > plvl) {
			GameLog.info("道术", player.getUserId(),
					"升级道术不能超过玩家等级,当前等级:" + currentLvl + "升级次数:" + upgradeCount + ",玩家等级" + plvl, null);
		}

		int maxUpgradeCount = Math.min(maxCfgLevel, plvl) - currentLvl;
		if (upgradeCount > maxUpgradeCount) {
			upgradeCount = maxUpgradeCount;
			newLevel = currentLvl + upgradeCount;
		}

		// 验证暴击次数
		RefInt total = new RefInt();
		int[] planNums = helper.generateCriticalPlan(taoistMgr.getRandomSeed(), taoistMgr.getSeedRange(), tid,
				currentLvl,upgradeCount, maxUpgradeCount, total);
		if (planNums == null) {
			return ErrorResponse("无效道术技能ID", ":" + tid, ErrorCode_Taoist.IllegalArguments, response, player, req);
		}
		
//		System.out.print("道术暴击序列:");
//		for(int i = 0; i<planNums.length;i++){
//			System.out.print(planNums[i]+",");
//		}
//		System.out.println();
		
		int criticalCount = 0;
		if (total.value > upgradeCount) {
			criticalCount = total.value - upgradeCount;
			upgradeCount = total.value;
			newLevel = currentLvl + upgradeCount;
		}

		// 计算消耗货币值，需要跳过暴击
		int coinCount = consumeHelper.getConsumeCoin(consumeId, currentLvl, planNums);
		if (coinCount == -1) {
			return ErrorResponse("升级次数无效", ":" + upgradeCount, ErrorCode_Taoist.IllegalArguments, response, player,
					req);
		}
		
		// 扣钱
		if (!userGameDataMgr.deductCurrency(cfg.getCoinType(), coinCount)) {
			return ErrorResponse("不够钱升级", ",新等级为:" + newLevel, ErrorCode_Taoist.NotEnoughMoney, response, player, req);
		}

		if (!taoistMgr.setLevel(tid, newLevel)) {
			return ErrorResponse("升级失败，次数", ":" + upgradeCount, ErrorCode_Taoist.IllegalArguments, response, player,
					req);
		}

		// 发送更新
		TaoistInfo.Builder value = TaoistInfo.newBuilder();
		value.setTaoistID(tid);
		value.setLevel(newLevel);
		response.addTaoistInfoList(value);

		taoistMgr.RefreshSeed();
		response.setCriticalRamdom(taoistMgr.getRandomSeed());
		response.setErrorCode(ErrorCode_Taoist.Success);
		response.setResultTip("暴击增加倍数:"+criticalCount+",消耗货币:"+coinCount);

		//通知角色日常任务 by Alex
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.TAOIST_STRENGTH, 1);
		return response.build().toByteString();
	}

	public ByteString getData(Player player, TaoistRequest req) {
		TaoistResponse.Builder response = TaoistResponse.newBuilder();
		Iterable<TaoistInfo> list = player.getTaoistMgr().getMagicList();
		response.addAllTaoistInfoList(list);
		ITaoistMgr taoistMgr = player.getTaoistMgr();
		taoistMgr.RefreshSeed();
		response.setCriticalRamdom(taoistMgr.getRandomSeed());
		response.setErrorCode(ErrorCode_Taoist.Success);
		return response.build().toByteString();
	}

	public ByteString getRandom(Player player, TaoistRequest req) {
		TaoistResponse.Builder response = TaoistResponse.newBuilder();
		ITaoistMgr taoistMgr = player.getTaoistMgr();
		taoistMgr.RefreshSeed();
		response.setCriticalRamdom(taoistMgr.getRandomSeed());
		response.setErrorCode(ErrorCode_Taoist.Success);
		return response.build().toByteString();
	}

	private ByteString ErrorResponse(String userTip, String errLog, ErrorCode_Taoist errCode, Builder response,
			Player player, TaoistRequest req) {
		if (StringUtils.isNotBlank(userTip)) {
			response.setResultTip(userTip);
		}
		GameLog.error("道术", player.getUserId(), userTip + errLog);
		response.setErrorCode(errCode);
		ITaoistMgr taoistMgr = player.getTaoistMgr();
		taoistMgr.RefreshSeed();
		response.setCriticalRamdom(taoistMgr.getRandomSeed());
		return response.build().toByteString();
	}
}