package com.rw.service.copy.copyHandler;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.CopyRecordMgr;
import com.playerdata.Player;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.playerdata.activity.retrieve.userFeatures.UserFeaturesEnum;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.copy.PvECommonHelper;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rw.service.pve.PveHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleClearingResult;
import com.rwproto.CopyServiceProtos.TagBattleData;

public class LQSGHandler {

	private static LQSGHandler instance = new LQSGHandler();

	public static LQSGHandler getInstance() {
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

		int levelId = copyRequest.getTagBattleData().getLevelId();

		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);

		CopyRecordMgr copyRecordMgr = player.getCopyRecordMgr();
		CopyLevelRecordIF copyRecord = copyRecordMgr.getLevelRecord(levelId);
		// 合法性检查
		EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, 1);
		if (type != EResultType.NONE) {
			return copyResponse.setEResultType(type).build().toByteString();
		}

		List<? extends ItemInfo> dropItems = null;
		try {
			dropItems = DropItemManager.getInstance().extractDropPretreatment(player, levelId, isWin);
		} catch (DataAccessTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String rewardInfoActivity = "";
		List<BilogItemInfo> list = BilogItemInfo.fromItemList(dropItems);
		rewardInfoActivity = BILogTemplateHelper.getString(list);

		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.COPY_TYPE_TRIAL_LQSG, copyCfg.getLevelID(), isWin, fightTime, rewardInfoActivity, 0);
		UserFeatruesMgr.getInstance().doFinish(player, UserFeaturesEnum.lxsg);
		if (!isWin) {
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}

		// 铜钱 经验 体力 结算
		PvECommonHelper.addPlayerAttr4Battle(player, copyCfg);

		// TODO HC @Modify 2015-11-30 bug fix 没有把掉落物品放进去发送给玩家
		PvECommonHelper.addCopyRewards(player, copyCfg, dropItems);

		// 英雄经验
		List<String> listUpHero = PvECommonHelper.addHerosExp(player, copyRequest, copyCfg);

		player.getCopyDataMgr().subCopyCount(String.valueOf(levelId));

		TagBattleClearingResult.Builder tagBattleClearingResult = TagBattleClearingResult.newBuilder(); // 战斗结算返回的信息...
		tagBattleClearingResult.addAllUpHeroId(listUpHero);// 升级英雄ID...
		copyResponse.setTagBattleClearingResult(tagBattleClearingResult.build());
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.BATTLE_CLEAR);

		// 练气山谷、日常任务
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Trial_LQSG, 1);

		// 战斗结束，推送pve消息给前端
		PveHandler.getInstance().sendPveInfo(player);
		return copyResponse.build().toByteString();
	}

}
