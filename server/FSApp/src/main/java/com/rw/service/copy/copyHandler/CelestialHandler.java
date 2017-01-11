package com.rw.service.copy.copyHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.CopyRecordMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
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
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleClearingResult;
import com.rwproto.CopyServiceProtos.TagBattleData;

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

		String rewardInfoActivity = "";
		List<? extends ItemInfo> listItemBattle = null;
		try {
			listItemBattle = DropItemManager.getInstance().extractDropPretreatment(player, levelId, isWin);
		} catch (DataAccessTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<BilogItemInfo> list = BilogItemInfo.fromItemList(listItemBattle);
		rewardInfoActivity = BILogTemplateHelper.getString(list);

		BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.COPY_TYPE_CELESTIAL, copyCfg.getLevelID(), isWin, fightTime, rewardInfoActivity, 0);
		UserFeatruesMgr.getInstance().checkCelestial(player, copyCfg);
		if (!isWin) {
			return copyResponse.setEResultType(EResultType.NONE).build().toByteString();
		}

		// 铜钱 经验 体力 结算
		PvECommonHelper.addPlayerAttr4Battle(player, copyCfg);

		// 英雄经验
		List<String> listUpHero = PvECommonHelper.addHerosExp(player, copyRequest, copyCfg);

		// 注意 此处表格数据为单独处理的
		List<String> itemList = addCelestialRewards(player, copyRequest, levelId, listItemBattle);

		copyResponse.addAllTagItemList(itemList);
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Trial2, 1);

		//
		TagBattleClearingResult.Builder tagBattleClearingResult = TagBattleClearingResult.newBuilder(); // 战斗结算返回的信息...
		tagBattleClearingResult.addAllUpHeroId(listUpHero);// 升级英雄ID...
		copyResponse.setTagBattleClearingResult(tagBattleClearingResult.build());
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.BATTLE_CLEAR);

		// 战斗结束，推送pve消息给前端
		PveHandler.getInstance().sendPveInfo(player);
		return copyResponse.build().toByteString();
	}

	// private String getCelestialRewardsInfo(Player player, MsgCopyRequest copyRequest, int levelId){
	// StringBuilder rewardInfoActivity=new StringBuilder();
	// CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
	// List<? extends ItemInfo> listItemBattle = null;
	// try {
	// //DropItemManager.getInstance().pretreatDrop(player, copyCfg);
	// listItemBattle = DropItemManager.getInstance().extractDropPretreatment(player, levelId);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// if (listItemBattle != null) {
	// for (ItemInfo item : listItemBattle) {
	// int itemId = item.getItemID();
	// int itemNum = item.getItemNum();
	// if (player.getItemBagMgr().addItem(item.getItemID(), item.getItemNum())) {
	// String strItemInfo = itemId + "," + itemNum+";";
	// rewardInfoActivity.append(strItemInfo);
	// }
	// }
	// }
	// return rewardInfoActivity.toString();
	// }

	private List<String> addCelestialRewards(Player player, MsgCopyRequest copyRequest, int levelId, List<? extends ItemInfo> listItemBattle) {
		player.getCopyDataMgr().subCopyCount(String.valueOf(levelId));

		ArrayList<String> list_ = new ArrayList<String>();
		if (listItemBattle != null) {
			ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();

			for (ItemInfo item : listItemBattle) {
				int itemId = item.getItemID();
				int itemNum = item.getItemNum();
				if (itemBagMgr.addItem(player, item.getItemID(), item.getItemNum())) {
					String strItemInfo = itemId + "," + itemNum;
					list_.add(strItemInfo);
				}
			}
			return list_;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	// /*
	// * 扫荡关卡... 掉落------>[{"itemID":700108,"itemNum":1},{"itemID":803002,"itemNum":1}]
	// */
	// public ByteString sweep(Player player, MsgCopyRequest copyRequest) {
	// MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
	// int levelId = copyRequest.getLevelId();
	// CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId); // 地图的配置...
	// CopyLevelRecordIF copyRecord = player.getCopyRecordMgr().getLevelRecord(levelId);
	// int times = copyRequest.getTagBattleData().getBattleClearingTime();
	// // 合法性检查
	// EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, times);
	// if (type != EResultType.NONE) {
	// return copyResponse.setEResultType(type).build().toByteString();
	// }
	//
	// //
	// PvECommonHelper.deduceSweepCost(player, copyRequest, copyResponse, times);
	//
	// String strLevelID = String.valueOf(levelId);
	// player.getCopyDataMgr().subCopyCount(strLevelID);
	// copyResponse.setCopyCount(player.getCopyDataMgr().getCopyCount(strLevelID));
	//
	// copyResponse.setLevelId(levelId);
	//
	// PvECommonHelper.addPlayerAttr4Sweep(player, copyCfg, times);
	//
	// List<TagSweepInfo> listSweepInfo = PvECommonHelper.gainSweepRewards(player, times, copyCfg);
	//
	// copyResponse.addAllTagSweepInfoList(listSweepInfo);
	// return copyResponse.setEResultType(EResultType.SWEEP_SUCCESS).build().toByteString();
	// }

}
