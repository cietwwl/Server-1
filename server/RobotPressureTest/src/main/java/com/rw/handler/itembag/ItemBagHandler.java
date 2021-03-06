package com.rw.handler.itembag;

import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.ItemBagProtos.EItemBagEventType;
import com.rwproto.ItemBagProtos.MsgItemBagRequest;
import com.rwproto.ItemBagProtos.MsgItemBagResponse;
import com.rwproto.ItemBagProtos.TagItemData;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class ItemBagHandler implements RandomMethodIF{

	private static ItemBagHandler instance = new ItemBagHandler();

	public static ItemBagHandler instance() {
		return instance;
	}

	/**
	 * 随机出售
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean sellRandom(Client client) {

		ItemData target = client.getItembagHolder().getRandom();

		boolean success = sellItem(client, target);
		return success;
	}

	/**
	 * 出售指定物品
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean sellItem(Client client, int modelId) {

		ItemData target = client.getItembagHolder().getByModelId(modelId);
		boolean success = false;
		if (target != null) {
			success = sellItem(client, target);
		}
		return success;
	}

	private boolean sellItem(Client client, ItemData target) {
		MsgItemBagRequest.Builder req = MsgItemBagRequest.newBuilder();
		req.setRequestType(EItemBagEventType.ItemBag_Sell);
		int count = target.getCount();
		if(count > 1){
			count = 1 + new Random().nextInt(count);
		}
		TagItemData itemData = TagItemData.newBuilder().setModelId(target.getModelId()).setDbId(target.getId()).setCount(count).build();
		req.addItemUpdateData(itemData);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_ItemBag, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_ItemBag;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgItemBagResponse rsp = MsgItemBagResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("ItemBagHandler[sellRandom] 转换响应消息为null");
						return false;
					} else {
						RobotLog.info("ItemBagHandler[sellRandom] 成功");
						return true;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("StoreHandler[buyRandom] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

	@Override
	public boolean executeMethod(Client client) {
		return sellRandom(client);
	}
}
