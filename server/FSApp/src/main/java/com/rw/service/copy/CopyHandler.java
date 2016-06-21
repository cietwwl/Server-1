package com.rw.service.copy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.CopyRecordMgr;
import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.eSpecialItemIDUserInfo;
import com.playerdata.copy.CopyCalculateState;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.eLog.eBILogCopyEntrance;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.copypve.CopyType;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MapGiftRequest;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleClearingResult;
import com.rwproto.CopyServiceProtos.TagBattleData;
import com.rwproto.CopyServiceProtos.TagSweepInfo;

public class CopyHandler {
	private static CopyHandler instance = new CopyHandler();

	public static CopyHandler getInstance() {
		return instance;
	}

	/*
	 * 购买关卡,今天购买几次已经需要耗费多少钻石统统在客户端计算...
	 */
	public ByteString buyLevel(MsgCopyRequest copyRequest, Player player) {

		return PvECommonHelper.buyLevel(copyRequest, player);
	}

	/*
	 * 战斗结算
	 */
	public ByteString battleClear(Player player, MsgCopyRequest copyRequest) {
		ByteString result;
		int levelId = copyRequest.getTagBattleData().getLevelId();

		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
		int copyType = copyCfg.getLevelType();

		switch (copyType) {
		case CopyType.COPY_TYPE_WARFARE:
			// 无尽战火 战斗结算 WARFARE 没有扫荡
			result = WarFareHandler.getInstance().battleClear(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_CELESTIAL:
			// 生存幻境战斗结算
			result = CelestialHandler.getInstance().battleClear(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_TRIAL_LQSG:
		case CopyType.COPY_TYPE_TRIAL_JBZD:
			// 聚宝之地、练气山谷战斗结算
			result = TrailHandler.getInstance().battleClear(player, copyRequest, copyType);
			break;

		default:
			// 副本战斗结算
			result = copyBattleClear(player, copyRequest);
			break;
		}

		return result;
	}

	/*
	 * 副本战斗结算
	 */
	public ByteString copyBattleClear(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		TagBattleData tagBattleData = copyRequest.getTagBattleData();
		boolean isWin = tagBattleData.getFightResult() == EBattleStatus.WIN;
		int fightTime = tagBattleData.getFightTime();

		int levelId = tagBattleData.getLevelId();

		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
		CopyRecordMgr copyRecordMgr = player.getCopyRecordMgr();
		CopyCalculateState state = copyRecordMgr.getCalculateState();
		CopyLevelRecordIF copyRecord = copyRecordMgr.getLevelRecord(levelId);
		boolean isFirst = copyRecord.isFirst();

		if (!isWin) {
			BILogMgr.getInstance().logCopyEnd(player, copyCfg.getLevelID(), copyCfg.getLevelType(), isFirst, isWin, fightTime);

			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}

		if (state == null) {
			GameLog.error("battle", "copyBattleClear", player + "请求获取未开始的战斗结算：" + levelId, null);
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}
		int lastBattleId = state.getLastBattleId();
		if (lastBattleId != levelId) {
			GameLog.error("battle", "copyBattleClear", player + "请求获取不一致的战斗结算：" + levelId + "," + lastBattleId, null);
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}
		// 重复请求
		MsgCopyResponse.Builder lastResponse = state.getLastCopyResponse();
		if (lastResponse != null) {
			return lastResponse.build().toByteString();
		}

		// 合法性检查
		EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, 1);
		if (type != EResultType.NONE) {
			return copyResponse.setEResultType(type).build().toByteString();
		}

		// 铜钱 经验 体力 结算
		PvECommonHelper.addPlayerAttr4Battle(player, copyCfg);

		// 物品增加...
		PvECommonHelper.addCopyRewards(player, copyCfg);

		// 英雄经验
		List<String> listUpHero = PvECommonHelper.addHerosExp(player, copyRequest, copyCfg);

		// 此处专门处理副本地图的关卡记录...
		String levelRecord4Client = copyRecordMgr.updateLevelRecord(levelId, tagBattleData.getStarLevel(), 1);
		if (StringUtils.isBlank(levelRecord4Client)) {
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();

		}
		copyResponse.addTagCopyLevelRecord(levelRecord4Client);
		int levelType = copyCfg.getLevelType();
		// 任务数量 日常
		if (levelType == CopyType.COPY_TYPE_NORMAL) {
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Dup_Normal, 1);
			player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Finish_Copy_Normal);
			player.getFresherActivityMgr().doCheck(eActivityType.A_NormalCopyLv);
		} else if (levelType == CopyType.COPY_TYPE_ELITE) {
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Dup_Elite, 1);
			player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Finish_Copy_Elite);
			player.getFresherActivityMgr().doCheck(eActivityType.A_EliteCopyLv);
		}

		// 黑市或者神秘商店
		if (levelType == CopyType.COPY_TYPE_ELITE || levelType == CopyType.COPY_TYPE_NORMAL) {
			player.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
		}
		// 任务--完成副本--章节星数--完成章节
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Section_Star);
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Finish_Section);

		TagBattleClearingResult.Builder tagBattleClearingResult = TagBattleClearingResult.newBuilder(); // 战斗结算返回的信息...
		tagBattleClearingResult.addAllUpHeroId(listUpHero);// 升级英雄ID...
		copyResponse.setTagBattleClearingResult(tagBattleClearingResult.build());
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.BATTLE_CLEAR);
		// 设置已经获取
		state.setLastCopyResponse(copyResponse);

		BILogMgr.getInstance().logCopyEnd(player, copyCfg.getLevelID(), copyCfg.getLevelType(), isFirst, isWin, fightTime);
		if (copyCfg.getLevelType() == CopyType.COPY_TYPE_NORMAL) {
			UserEventMgr.getInstance().CopyWin(player, 1);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_ELITE) {
			UserEventMgr.getInstance().ElityCopyWin(player, 1);
		}

		return copyResponse.build().toByteString();
	}

	/*
	 * 副本战前物品-经验计算返回...
	 */
	public ByteString battleItemsBack(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder().setRequestType(ERequestType.BATTLE_ITEMS_BACK);
		int levelId = copyRequest.getLevelId();
		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId); // 地图的配置...
		if (copyCfg == null) {
			GameLog.error("CopyHandler", player.getUserId(), "获取副本ID失败：" + levelId);
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}
		CopyRecordMgr copyRecordMgr = player.getCopyRecordMgr();
		// 删除之前的奖励记录
		copyRecordMgr.setCopyRewards(null);

		CopyLevelRecordIF copyRecord = copyRecordMgr.getLevelRecord(levelId);
		// 合法性检查
		EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, 1);
		if (type != EResultType.NONE) {
			return copyResponse.setEResultType(type).build().toByteString();
		}

		// 物品掉落
		List<String> itemList = new ArrayList<String>();
		String pItemsID = copyCfg.getItems(); // 地图配置里所写的物品掉落组ID...
		// List<Integer> list = convertToIntList(pItemsID);
		List<? extends ItemInfo> dropItems = null;
		try {
			dropItems = DropItemManager.getInstance().pretreatDrop(player, copyCfg);
			// 设置最后一次掉落id
			copyRecordMgr.setCalculateState(levelId);
		} catch (DataAccessTimeoutException e) {
			GameLog.error("生成掉落列表异常：" + player.getUserId() + "," + levelId, e);
		}
		if (dropItems != null) {
			// TODO 这种拼接的方式浪费性能+不好维护，客户端配合一起改;经验和物品反馈信息拼接在一起
			for (int i = 0; i < dropItems.size(); i++) {
				ItemInfo itemInfo = dropItems.get(i);
				int itemId = itemInfo.getItemID();
				int itemNum = itemInfo.getItemNum();
				itemList.add(itemId + "," + itemNum);
			}
		}
		eSpecialItemIDUserInfo eSpecialItemIDUserInfo = new eSpecialItemIDUserInfo();
		ActivityRateTypeMgr.getInstance().setEspecialItemidlis(copyCfg, player, eSpecialItemIDUserInfo);
		if (eSpecialItemIDUserInfo != null) {
			String clientData = ClientDataSynMgr.toClientData(eSpecialItemIDUserInfo);
			if (StringUtils.isNotBlank(clientData)) {
				copyResponse.setESpecialItemIdList(clientData);
			}
		}

		player.getItemBagMgr().addItem(eSpecialItemId.Power.getValue(), -copyCfg.getFailSubPower());
		//
		copyResponse.addAllTagItemList(itemList);
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.ITEM_BACK);

		BILogMgr.getInstance().logCopyBegin(player, copyCfg.getLevelID(), copyCfg.getLevelType(), copyRecord.isFirst(), eBILogCopyEntrance.Empty);

		return copyResponse.build().toByteString();

	}

	public static List<Integer> convertToIntList(String str) {
		if (str == null || str.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		String[] pItemsID = str.split(",");
		int length = pItemsID.length;
		ArrayList<Integer> result = new ArrayList<Integer>(length);
		for (int i = 0; i < length; i++) {
			try {
				result.add(Integer.parseInt(pItemsID[i]));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * 扫荡关卡...
	 * 掉落------>[{"itemID":700108,"itemNum":1},{"itemID":803002,"itemNum":1}]
	 */
	public ByteString sweep(Player player, MsgCopyRequest copyRequest) {
		ByteString result;
		int levelId = copyRequest.getTagBattleData().getLevelId();

		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
		int copyType = copyCfg.getLevelType();

		switch (copyType) {
		case CopyType.COPY_TYPE_CELESTIAL:
			// 生存幻境战斗结算
			result = CelestialHandler.getInstance().sweep(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_TRIAL_JBZD:
		case CopyType.COPY_TYPE_TRIAL_LQSG:
			// 聚宝之地、练气山谷扫荡结算
			result = TrailHandler.getInstance().sweep(player, copyRequest, copyType);
			break;

		default:
			// 副本战斗结算
			result = copySweep(player, copyRequest);
			break;
		}

		return result;

	}

	/*
	 * 扫荡关卡...
	 * 掉落------>[{"itemID":700108,"itemNum":1},{"itemID":803002,"itemNum":1}]
	 * 副本扫荡经验双倍预计掉落
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
		int copyType = copyCfg.getLevelType();

		// 同步日常任务
		if (copyType == CopyType.COPY_TYPE_NORMAL)
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Dup_Normal, times);
		else if (copyType == CopyType.COPY_TYPE_ELITE)
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Dup_Elite, times);

		// 黑市或者神秘商店
		if (copyType == CopyType.COPY_TYPE_ELITE || copyType == CopyType.COPY_TYPE_NORMAL) {
			player.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
		}
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
		BILogMgr.getInstance().logSweep(player, copyCfg.getLevelID(), copyCfg.getLevelType());
		if (copyCfg.getLevelType() == CopyType.COPY_TYPE_NORMAL) {// 游戏0普通1精英，银汉日志处理为1普通2精英
			UserEventMgr.getInstance().CopyWin(player, times);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_ELITE) {
			UserEventMgr.getInstance().ElityCopyWin(player, times);
		}
		return copyResponse.setEResultType(EResultType.SWEEP_SUCCESS).build().toByteString();
	}

	public ByteString getMapGift(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		MapGiftRequest mapGiftRequest = copyRequest.getMapGiftRequest();
		int mapId = mapGiftRequest.getMapId();
		int index = mapGiftRequest.getIndex();
		String giftId = player.getCopyRecordMgr().getGift(mapId, index);
		if (StringUtils.isBlank(giftId)) {
			player.NotifyCommonMsg(CommonTip.STAR_NOT_ENOUGH);
			copyResponse.setEResultType(EResultType.NONE);
		} else {
			List<String> list = new ArrayList<String>();
			list.add(giftId);
			copyResponse.addAllTagMapRecord(list);
			copyResponse.setEResultType(EResultType.GET_GIFT_SUCCESS);
		}
		return copyResponse.build().toByteString();
	}

}
