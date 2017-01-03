package com.rw.service.copy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.common.IHeroSynHandler;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.CopyRecordMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.eSpecialItemIDUserInfo;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.copy.copyHandler.ArenaHandler;
import com.rw.service.copy.copyHandler.CelestialHandler;
import com.rw.service.copy.copyHandler.EliteCopyHandler;
import com.rw.service.copy.copyHandler.JBZDHandler;
import com.rw.service.copy.copyHandler.LQSGHandler;
import com.rw.service.copy.copyHandler.NormalCopyHandler;
import com.rw.service.copy.copyHandler.TowerHandler;
import com.rw.service.copy.copyHandler.WarFareHandler;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.eLog.eBILogCopyEntrance;
import com.rw.service.log.template.BIActivityCode;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.herosynhandler.CommonHeroSynHandler;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.copypve.CopyType;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.GodGiftRequest;
import com.rwproto.CopyServiceProtos.MapAnimationState;
import com.rwproto.CopyServiceProtos.MapGiftRequest;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;

public class CopyHandler {
	private static CopyHandler instance = new CopyHandler();

	private IHeroSynHandler _synHandler;
	
	protected CopyHandler() {
		_synHandler = new CommonHeroSynHandler();
	}
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
		ByteString result = null;
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
			// 练气山谷战斗结算
			result = LQSGHandler.getInstance().battleClear(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_TRIAL_JBZD:
			// 聚宝之地战斗结算
			result = JBZDHandler.getInstance().battleClear(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_ARENA:
			result = ArenaHandler.getInstance().battleClear(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_TOWER:
			result = TowerHandler.getInstance().battleClear(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_ELITE:
			result = EliteCopyHandler.getInstance().battleClear(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_NORMAL:
			result = NormalCopyHandler.getInstance().battleClear(player, copyRequest);
			break;

		default:
			// result = copyBattleClear(player, copyRequest);
			break;
		}

		return result;
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
		// // 删除之前的奖励记录
		// copyRecordMgr.setCopyRewards(null);

		CopyLevelRecordIF copyRecord = copyRecordMgr.getLevelRecord(levelId);
		// 合法性检查
		EResultType type = PvECommonHelper.checkLimit(player, copyRecord, copyCfg, 1);
		if (type != EResultType.NONE) {
			return copyResponse.setEResultType(type).build().toByteString();
		}

		// 物品掉落
		List<String> itemList = new ArrayList<String>();
		List<? extends ItemInfo> dropItems = null;
		try {
			DropItemManager dropManager = DropItemManager.getInstance();
			List<? extends ItemInfo> pretreatDrop = dropManager.getPretreatDrop(player, copyCfg);
			if (pretreatDrop == null || pretreatDrop.isEmpty()) {
				dropItems = dropManager.pretreatDrop(player, copyCfg);
			} else {
				dropItems = pretreatDrop;
			}
			// 设置最后一次掉落id
			// copyRecordMgr.setCalculateState(levelId);
		} catch (DataAccessTimeoutException e) {
			GameLog.error("copyHandler", "battleItemsBack", "生成掉落列表异常：" + player.getUserId() + "," + levelId, e);
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

		ItemBagMgr.getInstance().addItem(player, eSpecialItemId.Power.getValue(), -copyCfg.getFailSubPower());
		//
		copyResponse.addAllTagItemList(itemList);
		copyResponse.setLevelId(copyCfg.getLevelID());
		copyResponse.setEResultType(EResultType.ITEM_BACK);

		BILogMgr.getInstance().logCopyBegin(player, copyCfg.getLevelID(), copyCfg.getLevelType(), copyRecord.isFirst(), eBILogCopyEntrance.Empty);

		eBattlePositionType targetType = eBattlePositionType.Normal;
		if (copyCfg.getLevelType() == CopyType.COPY_TYPE_TRIAL_JBZD) {
			targetType = eBattlePositionType.Jbzd;
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_TRIAL_JBZD, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_TRIAL_LQSG) {
			targetType = eBattlePositionType.Lxsg;
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_TRIAL_LQSG, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_CELESTIAL) {
			targetType = eBattlePositionType.Schj;
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_CELESTIAL, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_WARFARE) {
//			targetType = eBattlePositionType.MagicSecret;
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_WARFARE, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_TOWER) {
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_TOWER, copyCfg.getLevelID(), 0);
		}

		_synHandler.synHeroData(player, targetType, "");
		return copyResponse.build().toByteString();

	}

	/*
	 * 扫荡关卡... 掉落------>[{"itemID":700108,"itemNum":1},{"itemID":803002,"itemNum":1}]
	 */
	public ByteString sweep(Player player, MsgCopyRequest copyRequest) {
		ByteString result = null;
		int levelId = copyRequest.getTagBattleData().getLevelId();

		CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(levelId);
		int copyType = copyCfg.getLevelType();

		switch (copyType) {
		case CopyType.COPY_TYPE_NORMAL:
			result = NormalCopyHandler.getInstance().copySweep(player, copyRequest);
			break;
		case CopyType.COPY_TYPE_ELITE:
			result = EliteCopyHandler.getInstance().copySweep(player, copyRequest);
			break;
		// case CopyType.COPY_TYPE_CELESTIAL:
		// // 生存幻境战斗结算
		// result = CelestialHandler.getInstance().sweep(player, copyRequest);
		// break;
		// case CopyType.COPY_TYPE_TRIAL_JBZD:
		// result = null;
		// break;
		// case CopyType.COPY_TYPE_TRIAL_LQSG:
		// // 聚宝之地、练气山谷扫荡结算
		// result = null;
		// break;

		default:
			// 副本战斗结算
			// result = copySweep(player, copyRequest);
			break;
		}

		return result;

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

	public ByteString updateMapAnimation(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		MapAnimationState state = copyRequest.getMapAnima();
		if (null != state) {
			com.playerdata.MapAnimationState aniState = new com.playerdata.MapAnimationState();
			aniState.setEliteAnimState(state.getEliteAnimState());
			aniState.setEliteMapId(state.getEliteMapId());
			aniState.setNormalAnimState(state.getNormalAnimState());
			aniState.setNormalMapId(state.getNormalMapId());
			player.getUserGameDataMgr().setMapAnimationState(aniState);
			copyResponse.setEResultType(EResultType.Success);
		} else {
			copyResponse.setEResultType(EResultType.NONE);
		}
		return copyResponse.build().toByteString();
	}

	/**
	 * 请求获取关卡宝箱
	 * 
	 * @param player
	 * @param copyRequest
	 * @return
	 */
	public ByteString getCopyBox(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		MapGiftRequest mapGiftRequest = copyRequest.getMapGiftRequest();
		int mapId = mapGiftRequest.getMapId();
		int copy = mapGiftRequest.getIndex();
		boolean suc = player.getCopyRecordMgr().getCopyBox(mapId, copy);
		if (suc) {
			copyResponse.setEResultType(EResultType.Success);
		} else {
			copyResponse.setEResultType(EResultType.NONE);
		}
		return copyResponse.build().toByteString();
	}

	/**
	 * 请求领取天尊锦囊
	 * 
	 * @param player
	 * @param copyRequest
	 * @return
	 */
	public ByteString getGodBox(Player player, MsgCopyRequest copyRequest) {
		MsgCopyResponse.Builder copyResponse = MsgCopyResponse.newBuilder();
		GodGiftRequest godGift = copyRequest.getGodGift();
		int mapID = godGift.getMapID();
		boolean suc = player.getCopyRecordMgr().getGodGiftBox(mapID);
		if (suc) {
			copyResponse.setEResultType(EResultType.Success);
		} else {
			copyResponse.setEResultType(EResultType.NONE);
		}
		return copyResponse.build().toByteString();
	}

}
