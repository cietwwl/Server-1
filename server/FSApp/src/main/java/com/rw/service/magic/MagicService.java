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
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eMagicType magicType = request.getMagicType();
			switch (magicType) {
			case Magic_TAKE:
				result = magicHandler.wearMagicWeapon(player, request);
				break;
			case Magic_FORGE:
				result = magicHandler.forgeMagicWeapon(player, request);
				break;
			case Magic_SMELT:
				result = magicHandler.smeltMagicWeapon(player, request);
				break;
			case Magic_Upgrade:
				result = magicHandler.upgradeMagicWeapon(player, request);
				break;
			case Magic_Random:
				result = magicHandler.getRandomSeed(player, request);
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
		// TODO Auto-generated method stub
		MsgMagicRequest msgMagicRequest = MsgMagicRequest.parseFrom(request.getBody().getSerializedContent());
		return msgMagicRequest;
	}

	@Override
	public eMagicType getMsgType(MsgMagicRequest request) {
		// TODO Auto-generated method stub
		return request.getMagicType();
	}
}