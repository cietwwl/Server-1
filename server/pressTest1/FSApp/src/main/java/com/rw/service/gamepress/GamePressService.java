package com.rw.service.gamepress;

import com.google.protobuf.ByteString;
import com.rwproto.GamePressProtos.GamePressRequest;
import com.rwproto.GamePressProtos.GamePressResponse;
import com.rwproto.RequestProtos.Request;
import com.rwproto.ResponseProtos.Response;

public class GamePressService {
	public static ByteString pressGamePressProto(Request request){
		GamePressResponse.Builder response = GamePressResponse.newBuilder();
		try {
			Response result;
			GamePressRequest pressRequest = GamePressRequest
					.parseFrom(request.getBody().getSerializedContent());
			
			String content = pressRequest.getContent();
			response.setContent(content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response.build().toByteString();
	}
}
