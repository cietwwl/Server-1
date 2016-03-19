package com.rw.handler.magic;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.itembag.ItemData;
import com.rwproto.MagicServiceProtos.MagicItemData;
import com.rwproto.MagicServiceProtos.MsgMagicRequest;
import com.rwproto.MagicServiceProtos.MsgMagicResponse;
import com.rwproto.MagicServiceProtos.eMagicResultType;
import com.rwproto.MagicServiceProtos.eMagicType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 * @author HC
 * @date 2016年3月16日 上午11:13:19
 * @Description 法宝
 */
public class MagicHandler {
	private static final Command command = Command.MSG_MAGIC;
	private static final String functionName = "法宝模块";

	private static MagicHandler handler = new MagicHandler();

	public static MagicHandler getHandler() {
		return handler;
	}

	private class MagicMsgReceiver extends PrintMsgReciver {

		public MagicMsgReceiver(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
		}

		@Override
		public boolean execute(Client client, Response response) {
			ByteString bs = response.getSerializedContent();
			try {
				MsgMagicResponse rsp = MsgMagicResponse.parseFrom(bs);
				if (rsp == null) {
					RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
					return false;
				} else if (rsp.getEMagicResultType() != eMagicResultType.SUCCESS) {// 不等于成功
					RobotLog.info(parseFunctionDesc() + "失败");
					return false;
				}

				RobotLog.info(parseFunctionDesc() + "成功");
				return true;
			} catch (InvalidProtocolBufferException e) {
				RobotLog.fail(parseFunctionDesc() + "失败", e);
			}
			return false;
		}

		private String parseFunctionDesc() {
			return functionName + "[" + protoType + "] ";
		}
	}

	private MagicHandler() {
	}

	/**
	 * <pre>
	 * 法宝强化，以雷光锤为例，不穿在身上的为准 ，雷光锤模版Id是602003
	 * 强化用到的材料是1级晶石，模版Id是801001，每次只用两个
	 * </pre>
	 * 
	 * @param client
	 * @return
	 */
	public boolean magicForge(Client client) {
		ItemData itemData = client.getItembagHolder().getByModelId(602003);
		if (itemData == null) {
			RobotLog.info("MagicHandler[magicForge] 失败，雷光锤602003在背包里没有，需要添加");
			return false;
		}

		ItemData mate = client.getItembagHolder().getByModelId(801001);
		if (mate == null) {
			RobotLog.info("MagicHandler[magicForge] 失败，雷光锤602003需要的材料是801001，背包中没有，需要添加");
			return false;
		}

		MsgMagicRequest.Builder req = MsgMagicRequest.newBuilder();
		req.setMagicType(eMagicType.Magic_FORGE);
		req.setState(0);
		req.setId(itemData.getId());

		MagicItemData.Builder magicItemData = MagicItemData.newBuilder();
		magicItemData.setId(mate.getId());
		magicItemData.setCount(2);
		req.addMagicItemData(magicItemData);

		client.getMsgHandler().sendMsg(Command.MSG_MAGIC, req.build().toByteString(), new MagicMsgReceiver(command, functionName, "法宝强化"));
		return true;
	}
}