package com.rw.service.magic;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MagicServiceProtos.MsgMagicRequest;
import com.rwproto.MagicServiceProtos.eMagicType;
import com.rwproto.RequestProtos.Request;

public class MagicService implements FsService<MsgMagicRequest, eMagicType> {

	private MagicHandler magicHandler = MagicHandler.getInstance();

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(MsgMagicRequest request, Player player) {
		ByteString result = null;
		try {
			eMagicType magicType = request.getMagicType();
			switch (magicType) {
			case Magic_TAKE: 			//法宝装备
				result = magicHandler.wearMagicWeapon(player, request);
				break;
			case Magic_Upgrade:			//法宝升级
				result = magicHandler.forgeMagicWeapon(player, request);
				break;
			case Magic_Inherit:			//法宝继承
				
				break;
			case Magic_Evolution:		//法宝进化
				result = magicHandler.upgradeMagicWeapon(player, request);
				break;
			case Magic_Smelt:  			//法宝熔炼
				result = magicHandler.smeltMagicWeapon(player, request);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	@Override
	public MsgMagicRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		MsgMagicRequest msgMagicRequest = MsgMagicRequest.parseFrom(request.getBody().getSerializedContent());
		return msgMagicRequest;
	}

	@Override
	public eMagicType getMsgType(MsgMagicRequest request) {
		return request.getMagicType();
	}
}