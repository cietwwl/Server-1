package com.rw.service.magic;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.MagicServiceProtos.MsgMagicRequest;
import com.rwproto.MagicServiceProtos.eMagicType;
import com.rwproto.RequestProtos.Request;

public class MagicService implements FsService {

	private MagicHandler magicHandler = MagicHandler.getInstance();

	@SuppressWarnings("finally")
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgMagicRequest msgMagicRequest = MsgMagicRequest.parseFrom(request.getBody().getSerializedContent());
			eMagicType magicType = msgMagicRequest.getMagicType();
			switch (magicType) {
			case Magic_TAKE:
				result = magicHandler.wearMagicWeapon(player, msgMagicRequest);
				break;
			case Magic_FORGE:
				result = magicHandler.forgeMagicWeapon(player, msgMagicRequest);
				break;
			case Magic_SMELT:
				result = magicHandler.smeltMagicWeapon(player, msgMagicRequest);
				break;
			case Magic_Upgrade:
				result = magicHandler.upgradeMagicWeapon(player, msgMagicRequest);
				break;
			case Magic_Random:
				result = magicHandler.getRandomSeed(player, msgMagicRequest);
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
}