package com.rw.service.Equip;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.EquipProtos.EquipRequest;
import com.rwproto.RequestProtos.Request;

public class EquipService implements FsService {
	private EquipHandler handler = EquipHandler.getInstance();
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null; 
		try {
			EquipRequest equipReq = EquipRequest.parseFrom(request.getBody().getSerializedContent());
			switch ( equipReq.getEventType()) {
			case Advance:
				result = handler.advance(player,equipReq.getRoleId());
				break;
			case Equip_Attach:
				result = handler.equipAttach(player,equipReq.getRoleId(),equipReq.getEquipIndex(),equipReq.getMateList());
				break;
			case Equip_Compose:
				result = handler.equipCompose(player,equipReq.getEquipId());
				break;
			case Equip_OnekeyAttach:
				result = handler.equipOnekeyAttach(player,equipReq.getRoleId(),equipReq.getEquipIndex());
				break;
			case Wear_Equip:
				result = handler.wearEquip(player,equipReq.getRoleId(),equipReq.getEquipIndex());
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
