package com.playerdata.commonsoul;

import java.util.Arrays;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.rwbase.dao.commonsoul.CommonSoulConfigDAO;
import com.rwbase.dao.commonsoul.ExchangeRateCfgDAO;
import com.rwbase.dao.commonsoul.pojo.CommonSoulConfig;
import com.rwbase.dao.item.SoulStoneCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.SoulStoneCfg;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwproto.CommonSoulServiceProto.CommonSoulRequest;
import com.rwproto.CommonSoulServiceProto.CommonSoulResponse;
import com.rwproto.CommonSoulServiceProto.ResultType;

public class CommonSoulHandler {

	private static CommonSoulHandler _instance = new CommonSoulHandler();

	public static final CommonSoulHandler getInstance() {
		return _instance;
	}

	private CommonSoulConfigDAO _commonSoulConfigDAO;

	protected CommonSoulHandler() {
		_commonSoulConfigDAO = CommonSoulConfigDAO.getInstance();
	}

	private boolean checkExchangeEnable(Player player, int soulStoneId, CommonSoulConfig config) {
		SoulStoneCfg soulStoneCfg = SoulStoneCfgDAO.getInstance().getCfgById(String.valueOf(soulStoneId));
		if (soulStoneCfg == null) {
			return false;
		} else {
			int composeTargetId = soulStoneCfg.getComposeTargetId();
			if (composeTargetId > 0) {
				Hero hero = player.getHeroMgr().getHeroByModerId(player, composeTargetId);
				if (hero == null) {
					return config.isCanExchangeNotOwnedHeroSoul();
				} else if (hero.isMainRole()) {
					return config.isCanExchangeMainRoleSoul();
				} else {
					return config.isCanExchangeOwnedHeroSoul();
				}
			} else {
				return config.isCanExchangeMainRoleSoul();
			}
		}
	}

	private ByteString fillFail(CommonSoulResponse.Builder builder, String msg) {
		builder.setResultType(ResultType.fail);
		builder.setTips(msg);
		return builder.build().toByteString();
	}

	public ByteString processExchange(Player player, CommonSoulRequest request) {
		CommonSoulResponse.Builder responseBuilder = CommonSoulResponse.newBuilder();
		responseBuilder.setRequestType(request.getRequestType());
		int targetSoulItemId = request.getSoulItemId();
		int exchangeCount = request.getExchangeCount();
		CommonSoulConfig config = _commonSoulConfigDAO.getConfig();
		int exchangeRate = ExchangeRateCfgDAO.getInstace().getExchangeRate(targetSoulItemId);
		if (!checkExchangeEnable(player, targetSoulItemId, config) || exchangeRate <= 0) {
			return fillFail(responseBuilder, CommonSoulTips.getTipsExchangeNotOpened());
		}
		int requiredCount = exchangeCount * exchangeRate;
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		ItemData itemData = itemBagMgr.getFirstItemByModelId(player.getUserId(), config.getCommonSoulStoneCfgId());
		if (itemData == null || itemData.getCount() < requiredCount) {
			return fillFail(responseBuilder, CommonSoulTips.getTipsItemNotEnough(config.getCommonSoulStoneName(), exchangeCount));
		}
		List<IUseItem> useItemList = Arrays.asList((IUseItem) new UseItem(itemData.getId(), requiredCount));
		List<INewItem> newItemList = Arrays.asList((INewItem) new NewItem(targetSoulItemId, exchangeCount, null));
		if (itemBagMgr.useLikeBoxItem(player, useItemList, newItemList)) {
			responseBuilder.setResultCount(exchangeCount);
			responseBuilder.setResultType(ResultType.success);
			return responseBuilder.build().toByteString();
		} else {
			return fillFail(responseBuilder, CommonSoulTips.getTipsItemNotEnough(config.getCommonSoulStoneName(), exchangeCount));
		}
	}
}
