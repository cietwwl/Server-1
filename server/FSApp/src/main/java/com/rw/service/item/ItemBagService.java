package com.rw.service.item;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ItemBagProtos.EItemBagEventType;
import com.rwproto.ItemBagProtos.MsgItemBagRequest;
import com.rwproto.ItemBagProtos.TagCompose;
import com.rwproto.RequestProtos.Request;

public class ItemBagService implements FsService<MsgItemBagRequest, EItemBagEventType> {

	private ItemBagHandler itemBagHandler = ItemBagHandler.getInstance();

	@Override
	public ByteString doTask(MsgItemBagRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString responseData = null;
		try {
			EItemBagEventType requestType = request.getRequestType();
			switch (requestType) {
			case ItemBag_Sell:
				responseData = itemBagHandler.sellItemItemData(player, request.getItemUpdateDataList());
				break;
			case ItemBag_Compose:
				for (TagCompose tag : request.getComposeList()) {
					itemBagHandler.ComposeItem(player, tag.getMateId(), tag.getComposeCount());
				}
				break;
			case UseItem:
				responseData = itemBagHandler.useItem(player, request.getUseItemInfo());
				break;
			case ItemBag_Buy:
				responseData = itemBagHandler.buyItem(player, request.getComposeList());
				break;
			case ItemBag_MagicForgeMat_Buy:
				responseData = itemBagHandler.buyMagicForgeMaterial(player, request.getBuyItemInfo());
				break;
			case ItemBag_MagicWeapon_Decompose:
				responseData = itemBagHandler.decomposeMagicItem(player, request.getUseItemInfo());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseData;
	}

	@Override
	public MsgItemBagRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgItemBagRequest itembagRequest = MsgItemBagRequest.parseFrom(request.getBody().getSerializedContent());
		return itembagRequest;
	}

	@Override
	public EItemBagEventType getMsgType(MsgItemBagRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}
}
