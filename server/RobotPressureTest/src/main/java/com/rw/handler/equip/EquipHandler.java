package com.rw.handler.equip;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.EquipProtos.EquipEventType;
import com.rwproto.EquipProtos.EquipRequest;
import com.rwproto.EquipProtos.EquipResponse;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class EquipHandler {

	private static EquipHandler instance = new EquipHandler();

	public static EquipHandler instance() {
		return instance;
	}


	public boolean compose(Client client, int modelId) {

		EquipRequest.Builder req = EquipRequest.newBuilder()
											.setEventType(EquipEventType.Equip_Compose)
											.setEquipId(modelId);
		

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_EQUIP, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_EQUIP;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					EquipResponse rsp = EquipResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("EquipHandler[compose] 转换响应消息为null");
						return false;
					} else {
						ErrorType error = rsp.getError();
						if(error==ErrorType.SUCCESS){
							RobotLog.info("EquipHandler[compose] 成功");
							return true;
							
						}else{
							RobotLog.fail("EquipHandler[compose] 服务器返回结果为失败"+error);
							
							return false;
						}
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("EquipHandler[compose] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

}
