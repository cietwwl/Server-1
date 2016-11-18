package com.rw.handler.giftcode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GiftCodeProto.RequestType;
import com.rwproto.GiftCodeProto.ResultType;
import com.rwproto.GiftCodeProto.UseGiftCodeReqMsg;
import com.rwproto.GiftCodeProto.UseGiftCodeRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * @Author HC
 * @date 2016年11月16日 下午8:36:18
 * @desc
 **/

public class GiftCodeHandler {
	private static GiftCodeHandler handler = new GiftCodeHandler();

	public static GiftCodeHandler getHandler() {
		return handler;
	}

	private static final Command command = Command.MSG_GIFT_CODE;

	private GiftCodeHandler() {
	}

	/**
	 * 获取申请列表
	 * 
	 * @param client
	 * @return
	 */
	public boolean useGiftCodeHandler(Client client, String code) {
		if (code == null || code.isEmpty()) {
			RobotLog.info("输入的兑换码不能为空！！");
			return true;
		}

		UseGiftCodeReqMsg.Builder req = UseGiftCodeReqMsg.newBuilder();
		req.setReqType(RequestType.USE_CODE);
		req.setCode(code);

		return client.getMsgHandler().sendMsg(command, req.build().toByteString(), receiver);
	}

	/**
	 * 接受消息
	 */
	private MsgReciver receiver = new MsgReciver() {

		@Override
		public Command getCmd() {
			return command;
		}

		@Override
		public boolean execute(Client client, Response response) {
			ByteString bs = response.getSerializedContent();
			try {
				UseGiftCodeRspMsg rsp = UseGiftCodeRspMsg.parseFrom(bs);
				if (rsp == null) {
					RobotLog.fail("兑换码转换响应消息为null");
					return false;
				}

				ResultType resultType = rsp.getResultType();
				if (resultType == ResultType.FAIL) {
					RobotLog.fail("兑换码正常响应消息，返回来的结果是失败。失败的原因是：" + rsp.getTipMsg());
					return true;
				}
			} catch (InvalidProtocolBufferException e) {
				RobotLog.fail("兑换码失败", e);
			}
			return true;
		}
	};
}