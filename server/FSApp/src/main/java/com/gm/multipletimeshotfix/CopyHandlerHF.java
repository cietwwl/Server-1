package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.CopyRecordMgr;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.rateType.eSpecialItemIDUserInfo;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.copy.CopyHandler;
import com.rw.service.copy.PvECommonHelper;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.eLog.eBILogCopyEntrance;
import com.rw.service.log.template.BIActivityCode;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.copypve.CopyType;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class CopyHandlerHF extends CopyHandler {

	private void synHeroInfo(Player player) {
		EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.Normal_VALUE, "");
		if (positionInfo != null) {
			List<EmbattleHeroPosition> posList = positionInfo.getPos();
			List<String> heroIds = new ArrayList<String>(posList.size());
			for (EmbattleHeroPosition heroPos : posList) {
				heroIds.add(heroPos.getId());
			}
			List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
			for (Hero h : heros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		} else {
			List<Hero> allHeros = FSHeroMgr.getInstance().getAllHeros(player, null);
			for (Hero h : allHeros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		}
	}
	
	/**
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
			GameLog.error("", "", "生成掉落列表异常：" + player.getUserId() + "," + levelId, e);
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

		if (copyCfg.getLevelType() == CopyType.COPY_TYPE_TRIAL_JBZD) {
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_TRIAL_JBZD, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_TRIAL_LQSG) {
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_TRIAL_LQSG, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_CELESTIAL) {
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_CELESTIAL, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_WARFARE) {
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_WARFARE, copyCfg.getLevelID(), 0);
		} else if (copyCfg.getLevelType() == CopyType.COPY_TYPE_TOWER) {
			BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.COPY_TYPE_TOWER, copyCfg.getLevelID(), 0);
		}

		synHeroInfo(player);
		return copyResponse.build().toByteString();

	}
}
