package com.playerdata.activity.growthFund.service;

import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.growthFund.ActivityGrowthFundMgr;
import com.playerdata.activity.growthFund.GrowthFundTips;
import com.playerdata.activity.growthFund.GrowthFundType;
import com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfg;
import com.playerdata.activity.growthFund.cfg.GrowthFundBasicCfgDAO;
import com.playerdata.activity.growthFund.cfg.GrowthFundRewardAbsCfg;
import com.playerdata.activity.growthFund.cfg.GrowthFundSubCfgDAO;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType;
import com.rwproto.GrowthFundServiceProto.EGrowthFundResultType;
import com.rwproto.GrowthFundServiceProto.GrowthFundRequest;
import com.rwproto.GrowthFundServiceProto.GrowthFundResponse;

public class GrowthFundHandler {

	private static GrowthFundHandler _instance = new GrowthFundHandler();
	
	private GrowthFundSubCfgDAO _growthFundSubCfgDAO;
	private GrowthFundBasicCfgDAO _basicCfgDAO;
	
	protected GrowthFundHandler() {
		_growthFundSubCfgDAO = GrowthFundSubCfgDAO.getInstance();
		_basicCfgDAO = GrowthFundBasicCfgDAO.getInstance();
	}

	public static GrowthFundHandler getInstance() {
		return _instance;
	}
	
	private ByteString setFail(GrowthFundResponse.Builder respBuilder, String tips) {
		respBuilder.setResultType(EGrowthFundResultType.FAIL);
		respBuilder.setTips(tips);
		return respBuilder.build().toByteString();
	}
	
	public ByteString requestBuyGrowthFundGift(Player player) {
		GrowthFundResponse.Builder respBuilder = GrowthFundResponse.newBuilder();
		respBuilder.setReqType(EGrowthFundRequestType.BUY_GROWTH_FUND);
		ActivityGrowthFundItem growthFundItem = ActivityGrowthFundMgr.getInstance().getByType(player.getUserId(), GrowthFundType.GIFT);
		if (growthFundItem.isBought()) {
			return setFail(respBuilder, GrowthFundTips.getTipsAlreadyBought());
		}
		GrowthFundBasicCfg cfg = _basicCfgDAO.getCfgById(growthFundItem.getCfgId());
		if (cfg.getVipLv() > player.getVip()) {
			return setFail(respBuilder, GrowthFundTips.getTipsVipLvNotReach(cfg.getVipLv()));
		}
		if (cfg.getLevelLimit() > player.getLevel()) {
			return setFail(respBuilder, GrowthFundTips.getTipsLvNotReach(cfg.getLevelLimit()));
		}
		if (cfg.getPrice() > player.getUserGameDataMgr().getGold()) {
			return setFail(respBuilder, GrowthFundTips.getTipsDiamondNotEnough());
		}
		if (player.getUserGameDataMgr().deductCurrency(eSpecialItemId.Gold, cfg.getPrice())) {
			ActivityGrowthFundMgr.getInstance().onPlayerBuyGrowthFundGift(player);
			respBuilder.setResultType(EGrowthFundResultType.SUCCESS);
			respBuilder.setBoughtCount(ActivityGrowthFundMgr.getInstance().getBoughtCount());
		} else {
			return setFail(respBuilder, GrowthFundTips.getTipsDiamondNotEnough());
		}
		return respBuilder.build().toByteString();
	}
	
	private ByteString processGet(Player player, GrowthFundRequest request, GrowthFundType type) {
		GrowthFundResponse.Builder respBuilder = GrowthFundResponse.newBuilder();
		respBuilder.setReqType(request.getReqType());
		ActivityGrowthFundItem growthFundItem = ActivityGrowthFundMgr.getInstance().getByType(player.getUserId(), type);
		if (type == GrowthFundType.GIFT && !growthFundItem.isBought()) {
			return setFail(respBuilder, GrowthFundTips.getTipsYouHaveNotBought());
		}
		String cfgId = String.valueOf(request.getRequestId());
		GrowthFundRewardAbsCfg giftCfg = (GrowthFundRewardAbsCfg) _growthFundSubCfgDAO.getCfgById(cfgId);
		if (giftCfg == null) {
			return setFail(respBuilder, GrowthFundTips.getTipsNoSuchItem());
		}
		List<ActivityGrowthFundSubItem> subItemList = growthFundItem.getSubItemList();
		ActivityGrowthFundSubItem subItem = null;
		for (int i = 0, size = subItemList.size(); i < size; i++) {
			subItem = subItemList.get(i);
			if (subItem.getCfgId().equals(cfgId)) {
				if (subItem.isGet()) {
					return setFail(respBuilder, GrowthFundTips.getTipsAlreadyGot());
				}
				break;
			} else {
				subItem = null;
			}
		}
		if (subItem == null) {
			return setFail(respBuilder, GrowthFundTips.getTipsNoSuchItem());
		}
		IReadOnlyPair<Boolean, String> checkResult = type.getConditionCheckStrage().checkConditionReached(giftCfg, player);
		if (checkResult.getT1().booleanValue()) {
			respBuilder.setResultType(EGrowthFundResultType.SUCCESS);
			ActivityGrowthFundMgr.getInstance().onPlayerGetReward(player, growthFundItem, subItem);
			player.getItemBagMgr().addItem(giftCfg.getRewardItemInfos());
		} else {
			return setFail(respBuilder, checkResult.getT2());
		}
		return respBuilder.build().toByteString();
	}
	
	public ByteString requestGetGrowthFundGift(Player player, GrowthFundRequest request) {
		return this.processGet(player, request, GrowthFundType.GIFT);
	}
	
	public ByteString requestGetGrowthFundReward(Player player, GrowthFundRequest request) {
		return this.processGet(player, request, GrowthFundType.REWARD);
	}
}
