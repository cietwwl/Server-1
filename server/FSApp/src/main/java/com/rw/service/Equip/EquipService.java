package com.rw.service.Equip;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.EquipProtos.EquipEventType;
import com.rwproto.EquipProtos.EquipRequest;
import com.rwproto.RequestProtos.Request;

public class EquipService implements FsService<EquipRequest, EquipEventType> {
	private EquipHandler handler = EquipHandler.getInstance();

	@Override
	public ByteString doTask(EquipRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			switch (request.getEventType()) {
			case Advance:
				result = handler.advance(player, request.getRoleId());
				break;
			case Equip_Attach:
				result = handler.equipAttach(player, request.getRoleId(), request.getEquipIndex(), request.getMateList());
				break;
			case Equip_Compose:
				result = handler.equipCompose(player, request.getEquipId());
				break;
			case Equip_OnekeyAttach:
				result = handler.equipOnekeyAttach(player, request.getRoleId(), request.getEquipIndex());
				break;
			case Wear_Equip:
				result = handler.wearEquip(player, request.getRoleId(), request.getEquipIndex());
				break;
			case OneKeyWearEquip:
				result = handler.oneKeyWearEquip(player, request.getRoleId());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public EquipRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		EquipRequest equipRequest = EquipRequest.parseFrom(request.getBody().getSerializedContent());
		return equipRequest;
	}

	@Override
	public EquipEventType getMsgType(EquipRequest request) {
		// TODO Auto-generated method stub
		return request.getEventType();
	}
}
