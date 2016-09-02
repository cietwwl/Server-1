package com.rw.service.giftcode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GiftCodeProto.UseGiftCodeReqMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年3月18日 上午10:35:09
 * @Description 使用兑换码
 */
public class GiftCodeService implements FsService<UseGiftCodeReqMsg, Command> {

	@Override
	public ByteString doTask(UseGiftCodeReqMsg request, Player player) {
		// TODO Auto-generated method stub
		
		try {
			return GiftCodeHandler.getHandler().useGiftCodeHandler(player, request.getCode());
		} catch (Exception e) {
			GameLog.error("兑换码模块", player.getUserId(), "转换协议出现异常", e);
		}

		return null;
	}

	@Override
	public UseGiftCodeReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		UseGiftCodeReqMsg req = UseGiftCodeReqMsg.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public Command getMsgType(UseGiftCodeReqMsg request) {
		// TODO Auto-generated method stub
		return null;
	}
}