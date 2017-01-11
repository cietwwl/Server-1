package com.rw.service.copy.copyHandler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.randomBoss.RandomBossMgr;
import com.google.protobuf.ByteString;
import com.playerdata.CopyRecordMgr;
import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.eSpecialItemIDUserInfo;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.copy.PvECommonHelper;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.behavior.GameBehaviorMgr;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleClearingResult;
import com.rwproto.CopyServiceProtos.TagBattleData;
import com.rwproto.CopyServiceProtos.TagSweepInfo;

public class NormalCopyHandler {

	private static NormalCopyHandler instance = new NormalCopyHandler();

	public static NormalCopyHandler getInstance() {
		return instance;
	}

	/*
	 * 试炼之境 战斗结算
	 */
	public ByteString battleClear(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		TagBattleData tagBattleData = copyRequest.getTagBattleData();
		boolean isWin = tagBattleData.getFightResult() == EBattleStatus.WIN;
		int fightTime = tagBattleData.getFightTime();

		int levelId = tagBattleData.getLevelId();

		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
		CopyRecordMgr copyRecordMgr = player.getCopyRecordMgr();

		CopyLevelRecordIF copyRecord = copyRecordMgr.getLevelRecord(levelId);
		boolean isFirst = copyRecord.isFirst();

		String rewardInfoActivity = "";
		if (!isWin) {
			List<BilogItemInfo> list = BilogItemInfo.fromItemList(DropItemManager.getInstance().getPretreatDrop(player, copyCfg));
			rewardInfoActivity = BILogTemplateHelper.getString(list);

			BILogMgr.getInstance().logCopyEnd(player, copyCfg.getLevelID(), copyCfg.getLevelType(), isFirst, isWin, fightTime, rewardInfoActivity);
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}

		List<? extends ItemInfo> dropItems = null;
		try {
			dropItems = DropItemManager.getInstance().extractDropPretreatment(player, levelId, isWin);
		} catch (DataAccessTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BilogItemInfo> list = BilogItemInfo.fromItemList(dropItems);
		rewardInfoActivity = BILogTemplateHelper.getString(list);
		GameBehaviorMgr.getInstance().setMapId(player.getUserId(), copyCfg.getLevelID());

		// 合法性检查
		EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, 1);
		if (type != EResultType.NONE) {
			return copyResponse.setEResultType(type).build().toByteString();
		}

		// 铜钱 经验 体力 结算
		PvECommonHelper.addPlayerAttr4Battle(player, copyCfg);

		// 物品增加...
		PvECommonHelper.addCopyRewards(player, copyCfg, dropItems);

		// 英雄经验
		List<String> listUpHero = PvECommonHelper.addHerosExp(player, copyRequest, copyCfg);

		// 此处专门处理副本地图的关卡记录...
		String levelRecord4Client = copyRecordMgr.updateLevelRecord(levelId, tagBattleData.getStarLevel(), 1);
		// 日志打印需要最新的关卡记录数据，此句必须放在update之后，否则获取的通关数据部包括当前关卡进度
		BILogMgr.getInstance().logCopyEnd(player, copyCfg.getLevelID(), copyCfg.getLevelType(), isFirst, isWin, fightTime, rewardInfoActivity);

		if (StringUtils.isBlank(levelRecord4Client)) {
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();

		}
		copyResponse.addTagCopyLevelRecord(levelRecord4Client);

		player.getDailyActivityMgr().getTaskList();
		// 任务数量 日常
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Dup_Normal, 1);
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Finish_Copy_Normal);
		player.getFresherActivityMgr().doCheck(eActivityType.A_NormalCopyLv);

		// 黑市或者神秘商店
		player.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
		// 任务--完成副本--章节星数--完成章节
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Section_Star);
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Finish_Section);

		// 随机boss
		RandomBossMgr.getInstance().findBossBorn(player, true);

		TagBattleClearingResult.Builder tagBattleClearingResult = TagBattleClearingResult.newBuilder(); // 战斗结算返回的信息...
		tagBattleClearingResult.addAllUpHeroId(listUpHero);// 升级英雄ID...
		copyResponse.setTagBattleClearingResult(tagBattleClearingResult.build());
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.BATTLE_CLEAR);

		UserEventMgr.getInstance().CopyWin(player, 1);

		return copyResponse.build().toByteString();
	}

	/*
	 * 扫荡关卡...掉落------>[{"itemID":700108,"itemNum":1},{"itemID":803002,"itemNum":1}]副本扫荡经验双倍预计掉落
	 */
	public ByteString copySweep(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		int levelId = copyRequest.getLevelId();
		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId); // 地图的配置...
		CopyLevelRecordIF copyRecord = player.getCopyRecordMgr().getLevelRecord(levelId);
		int times = copyRequest.getTagBattleData().getBattleClearingTime();
		// 合法性检查
		EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, times);
		if (type != EResultType.NONE) {
			return copyResponse.setEResultType(type).build().toByteString();
		}
		if (!player.getCopyRecordMgr().IsCanSweep(copyRecord, copyCfg, times, copyRequest.getRequestType())) {
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}
		//
		PvECommonHelper.deduceSweepCost(player, copyRequest, copyResponse, times);

		// 同步日常任务
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Dup_Normal, times);

		// 随机boss
		RandomBossMgr.getInstance().findBossBorn(player, true);

		// 黑市或者神秘商店
		player.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
		copyResponse.setLevelId(levelId);

		String levelRecord4Client = player.getCopyRecordMgr().updateLevelRecord(levelId, 3, times);

		PvECommonHelper.addPlayerAttr4Sweep(player, copyCfg, times);

		List<TagSweepInfo> listSweepInfo = PvECommonHelper.gainSweepRewards(player, times, copyCfg);

		/** 扫荡处发送经验双倍字段给客户端显示 */
		eSpecialItemIDUserInfo eSpecialItemIDUserInfo = new eSpecialItemIDUserInfo();
		ActivityRateTypeMgr.getInstance().setEspecialItemidlis(copyCfg, player, eSpecialItemIDUserInfo);
		if (eSpecialItemIDUserInfo != null) {
			String clientData = ClientDataSynMgr.toClientData(eSpecialItemIDUserInfo);
			if (StringUtils.isNotBlank(clientData)) {
				copyResponse.setESpecialItemIdList(clientData);
			}
		}

		copyResponse.addAllTagSweepInfoList(listSweepInfo);
		if (levelRecord4Client != null) {
			copyResponse.addTagCopyLevelRecord(levelRecord4Client);
		}

		UserEventMgr.getInstance().CopyWin(player, times);
		return copyResponse.setEResultType(EResultType.SWEEP_SUCCESS).build().toByteString();
	}

}
