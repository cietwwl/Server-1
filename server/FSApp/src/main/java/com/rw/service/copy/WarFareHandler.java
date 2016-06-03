package com.rw.service.copy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.protobuf.ByteString;
import com.playerdata.CopyRecordMgr;
import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeEnum;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.pve.PveHandler;
import com.rw.service.unendingwar.UnendingWarHandler;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.unendingwar.TableUnendingWar;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleClearingResult;

public class WarFareHandler {

	private static WarFareHandler instance = new WarFareHandler();

	public static WarFareHandler getInstance() {
		return instance;
	}

	/*
	 * 无尽战火 战斗结算 WARFARE 没有扫荡
	 */
	public ByteString battleClear(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		int levelId = copyRequest.getTagBattleData().getLevelId();

		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);

		CopyRecordMgr copyRecordMgr = player.getCopyRecordMgr();
		CopyLevelRecordIF copyRecord = copyRecordMgr.getLevelRecord(levelId);
		// 合法性检查
		EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, 1);
		if (type != EResultType.NONE) {

			return copyResponse.setEResultType(type).build().toByteString();
		}
		
		int times = copyRequest.getTagBattleData().getBattleClearingTime();
		
		UserEventMgr.getInstance().warFareDifficultyTwoKingActive(player, levelId, times);
		// 铜钱 经验 体力 结算
		PvECommonHelper.addPlayerAttr4Battle(player, copyCfg);
		
		// 英雄经验
		List<String> listUpHero = PvECommonHelper.addHerosExp(player, copyRequest, copyCfg);

		

		AtomicInteger unendingWarCoin = new AtomicInteger();
		List<? extends ItemInfo> addList = UnendingWarHandler.getInstance().getJlItem(player, times-1, copyCfg.getLevelID(),unendingWarCoin);
	
		
		
		
		
		List<String> itemList = new ArrayList<String>();
		for (int i = 0; i < addList.size(); i++) {
			int itemId = addList.get(i).getItemID();
			int itemNum = addList.get(i).getItemNum();
			String strItemInfo = itemId + "," + itemNum;
			itemList.add(strItemInfo);
		}
		
		copyResponse.addAllTagItemList(itemList);
		int zjd = UnendingWarHandler.getInstance().getJlNum(player, times, copyCfg.getLevelID());
		copyResponse.setUnendingWar(zjd);
		//
		TagBattleClearingResult.Builder tagBattleClearingResult = TagBattleClearingResult.newBuilder(); // 战斗结算返回的信息...
		tagBattleClearingResult.addAllUpHeroId(listUpHero);// 升级英雄ID...
		copyResponse.setTagBattleClearingResult(tagBattleClearingResult.build());
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setUnendingWar(unendingWarCoin.get());
		copyResponse.setEResultType(EResultType.BATTLE_CLEAR);

		// 无尽战火日常任务
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.UNENDINGWAR, 1);
		TableUnendingWar table = player.unendingWarMgr.getTable();
		table.setLastChallengeTime(System.currentTimeMillis());
		table.setNum(table.getNum() + 1);

		// 战斗结束，推送pve消息给前端
		PveHandler.getInstance().sendPveInfo(player);
		return copyResponse.build().toByteString();
	}
}
