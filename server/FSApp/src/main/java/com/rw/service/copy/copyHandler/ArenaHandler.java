package com.rw.service.copy.copyHandler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.CopyRecordMgr;
import com.playerdata.Player;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.copy.PvECommonHelper;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleClearingResult;
import com.rwproto.CopyServiceProtos.TagBattleData;

public class ArenaHandler {

	private static ArenaHandler instance = new ArenaHandler();

	public static ArenaHandler getInstance() {
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
		List<? extends ItemInfo> dropItems = null;
		try {
			dropItems = DropItemManager.getInstance().extractDropPretreatment(player, levelId, isWin);
		} catch (DataAccessTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BilogItemInfo> list = BilogItemInfo.fromItemList(dropItems);
		rewardInfoActivity = BILogTemplateHelper.getString(list);

		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.ARENA, copyCfg.getLevelID(), isWin, fightTime, rewardInfoActivity, 0);
		if (!isWin) {
			BILogMgr.getInstance().logCopyEnd(player, copyCfg.getLevelID(), copyCfg.getLevelType(), isFirst, isWin, fightTime, rewardInfoActivity);
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}

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

		// 任务--完成副本--章节星数--完成章节
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Section_Star);
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Finish_Section);

		TagBattleClearingResult.Builder tagBattleClearingResult = TagBattleClearingResult.newBuilder(); // 战斗结算返回的信息...
		tagBattleClearingResult.addAllUpHeroId(listUpHero);// 升级英雄ID...
		copyResponse.setTagBattleClearingResult(tagBattleClearingResult.build());
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.BATTLE_CLEAR);

		return copyResponse.build().toByteString();
	}

}
