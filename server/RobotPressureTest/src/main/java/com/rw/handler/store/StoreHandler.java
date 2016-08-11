package com.rw.handler.store;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.StoreProtos.StoreRequest;
import com.rwproto.StoreProtos.StoreResponse;
import com.rwproto.StoreProtos.eStoreRequestType;
import com.rwproto.StoreProtos.eStoreResultType;
import com.rwproto.StoreProtos.tagCommodity;

public class StoreHandler {

	private static StoreHandler instance = new StoreHandler();

	public static StoreHandler instance() {
		return instance;
	}

	/**
	 * 随机购买物品
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean buyRandom(Client client) {

		CommodityData target = client.getStoreItemHolder().getRandom(eStoreType.General);
		StoreRequest.Builder req = StoreRequest.newBuilder();
		if (target == null) {
			req.setRequestType(eStoreRequestType.RefreshStore);
		} else {
			int commodity = target.getId();
			req.setRequestType(eStoreRequestType.BuyCommodity);
			tagCommodity vo = tagCommodity.newBuilder().setCount(1).setId(commodity).build();
			req.setCommodity(vo);
		}
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_STORE, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_STORE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					StoreResponse rsp = StoreResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("StoreHandler[buyRandom] 转换响应消息为null");
						return false;
					}

					eStoreResultType result = rsp.getReslutType();
					if (result == eStoreResultType.SUCCESS) {
						RobotLog.info("StoreHandler[buyRandom] 购买成功");
						return true;
					} else {
						if(rsp.getReslutValue().equals("商店刷新次数已上限")){
							return true;
						}else{
							RobotLog.fail("StoreHandler[buyRandom] 服务器处理消息失败 " + result + ",失败原因：" + rsp.getReslutValue());
							return false;
						}

					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("StoreHandler[buyRandom] 失败", e);
					return false;
				}
			}

		});
		return success;
	}
}