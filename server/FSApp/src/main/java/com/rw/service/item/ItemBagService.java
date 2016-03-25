package com.rw.service.item;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ItemBagProtos.EItemBagEventType;
import com.rwproto.ItemBagProtos.MsgItemBagRequest;
import com.rwproto.ItemBagProtos.TagCompose;
import com.rwproto.RequestProtos.Request;

public class ItemBagService implements FsService {

	private ItemBagHandler itemBagHandler = ItemBagHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString responseData = null;
		try {
			MsgItemBagRequest itembagRequest = MsgItemBagRequest.parseFrom(request.getBody().getSerializedContent());
			EItemBagEventType requestType = itembagRequest.getRequestType();
			switch (requestType) {
			// case ItemBag_Index:
			// itemBagHandler.PlayerOnLogin(player);
			// break;
			case ItemBag_Sell:
				// for (TagItemData itemdata : itembagRequest.getItemUpdateDataList()) {
				// itemBagHandler.SellItemItemData(player, itemdata.getModelId(), itemdata.getCount(), itemdata.getDbId());
				// }
				responseData = itemBagHandler.sellItemItemData(player, itembagRequest.getItemUpdateDataList());
				break;
			case ItemBag_Compose:
				for (TagCompose tag : itembagRequest.getComposeList()) {
					itemBagHandler.ComposeItem(player, tag.getMateId(), tag.getComposeCount());
				}
				break;
			case UseItem:
				responseData = itemBagHandler.useItem(player, itembagRequest.getUseItemInfo());
				break;
			case ItemBag_Buy:
				responseData = itemBagHandler.buyItem(player, itembagRequest.getComposeList());
				break;
			case ItemBag_MagicForgeMat_Buy:
				responseData = itemBagHandler.buyMagicForgeMaterial(player, itembagRequest.getBuyItemInfo());
				break;
			case ItemBag_MagicWeapon_Decompose:
				responseData = itemBagHandler.decomposeMagicItem(player, itembagRequest.getUseItemInfo());
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return responseData;
	}
}
