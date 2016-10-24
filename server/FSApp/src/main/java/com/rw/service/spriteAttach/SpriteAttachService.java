package com.rw.service.spriteAttach;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SpriteAttachProtos.SpriteAttachRequest;
import com.rwproto.SpriteAttachProtos.eSpriteAttachRequestType;

public class SpriteAttachService implements FsService<SpriteAttachRequest, eSpriteAttachRequestType>{

	@Override
	public ByteString doTask(SpriteAttachRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try{
			switch (request.getRequestType()) {
			case SpriteAttach:
				result = SpriteAttachHandler.getInstance().spriteAttach(player, request);
				break;

			default:
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public SpriteAttachRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		SpriteAttachRequest spriteAttachRequest =SpriteAttachRequest.parseFrom(request.getBody().getSerializedContent());
		return spriteAttachRequest;
	}

	@Override
	public eSpriteAttachRequestType getMsgType(SpriteAttachRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}


}
