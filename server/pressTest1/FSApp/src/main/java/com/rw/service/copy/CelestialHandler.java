package com.rw.service.copy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.CopyRecordMgr;
import com.playerdata.Player;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.playerdata.readonly.ItemInfoIF;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.pve.PveHandler;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleClearingResult;
import com.rwproto.CopyServiceProtos.TagSweepInfo;

public class CelestialHandler {

	private static CelestialHandler instance = new CelestialHandler();

	public static CelestialHandler getInstance() {
		return instance;
	}

	/*
	 * 生存幻境战斗结算
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

		// 铜钱 经验 体力 结算
		PvECommonHelper.addPlayerAttr4Battle(player, copyCfg);

		// 英雄经验
		List<String> listUpHero = PvECommonHelper.addHerosExp(player, copyRequest, copyCfg);

		// 注意 此处表格数据为单独处理的
		List<String> itemList = addCelestialRewards(player, copyRequest, levelId);

		copyResponse.addAllTagItemList(itemList);
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Trial2, 1);

		//
		TagBattleClearingResult.Builder tagBattleClearingResult = TagBattleClearingResult.newBuilder(); // 战斗结算返回的信息...
		tagBattleClearingResult.addAllUpHeroId(listUpHero);// 升级英雄ID...
		copyResponse.setTagBattleClearingResult(tagBattleClearingResult.build());
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.BATTLE_CLEAR);
		
		//战斗结束，推送pve消息给前端
		PveHandler.getInstance().sendPveInfo(player);
		return copyResponse.build().toByteString();
	}

	private List<String> addCelestialRewards(Player player, MsgCopyRequest copyRequest, int levelId) {
		player.getCopyDataMgr().subCopyCount(String.valueOf(levelId));
		// List<ItemInfoIF> listItems =
		// player.getCopyDataMgr().checkFirstPrize(CopyType.COPY_TYPE_CELESTIAL,
		// String.valueOf(levelId));
		// if (listItems != null) {
		// addList.addAll(listItems);
		// }
		// if (copyRequest.getTagBattleData().getBattleStatus() ==
		// EBattleStatus.WIN) {
		// listItems =
		// player.getCopyDataMgr().addKillPrize(String.valueOf(levelId));
		// if (listItems != null) {
		// addList.addAll(listItems);
		// }
		// }
		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
		String pItemsID = copyCfg.getItems(); // 地图配置里所写的物品掉落组ID...
		//List<Integer> list = CopyHandler.convertToIntList(pItemsID);
		// TODO DropItemManaer可优化成一个方法调用，少一次数据库操作和减少遍历操作
		List<? extends ItemInfo> listItemBattle = null;
		try {
			DropItemManager.getInstance().pretreatDrop(player, copyCfg);
			listItemBattle = DropItemManager.getInstance().extractDropPretreatment(player, levelId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ArrayList<String> list_ = new ArrayList<String>();
		if (listItemBattle != null) {
			for (ItemInfo item : listItemBattle) {
				int itemId = item.getItemID();
				int itemNum = item.getItemNum();
				if(player.getItemBagMgr().addItem(item.getItemID(), item.getItemNum())){
					String strItemInfo = itemId + "," + itemNum;
					list_.add(strItemInfo);
				}
			}
			return list_;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/*
	 * 扫荡关卡...
	 * 掉落------>[{"itemID":700108,"itemNum":1},{"itemID":803002,"itemNum":1}]
	 */
	public ByteString sweep(Player player, MsgCopyRequest copyRequest) {
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

		//
		PvECommonHelper.deduceSweepCost(player, copyRequest, copyResponse, times);

		String strLevelID = String.valueOf(levelId);
		player.getCopyDataMgr().subCopyCount(strLevelID);
		copyResponse.setCopyCount(player.getCopyDataMgr().getCopyCount(strLevelID));

		copyResponse.setLevelId(levelId);

		PvECommonHelper.addPlayerAttr4Sweep(player, copyCfg, times);

		List<TagSweepInfo> listSweepInfo = PvECommonHelper.gainSweepRewards(player, times, copyCfg);

		copyResponse.addAllTagSweepInfoList(listSweepInfo);
		return copyResponse.setEResultType(EResultType.SWEEP_SUCCESS).build().toByteString();
	}

}
