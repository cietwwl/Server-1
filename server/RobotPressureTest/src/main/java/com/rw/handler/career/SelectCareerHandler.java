package com.rw.handler.career;

import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.RoleServiceProtos.RoleRequest;
import com.rwproto.RoleServiceProtos.RoleRequestType;
import com.rwproto.RoleServiceProtos.RoleResponse;
import com.rwproto.RoleServiceProtos.RoleResultType;

public class SelectCareerHandler implements RandomMethodIF{

	private static SelectCareerHandler instance = new SelectCareerHandler();

	public static SelectCareerHandler instance() {
		return instance;
	}

	/**
	 * 选择职业
	 * @param client
	 * @param friendUserId
	 * @return
	 */
	public boolean selectCareer(Client client) {
		RoleRequest.Builder req = RoleRequest.newBuilder();
		req.setRequestType(RoleRequestType.SELECT_CAREER);
		req.setCareerType(new Random().nextInt(4) + 1);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_ROLE, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_ROLE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					RoleResponse rsp = RoleResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("SelectCareerHandler[selectCareer] 转换响应消息为null");
						return false;
					}

					RoleResultType result = rsp.getResult();
					if (result == RoleResultType.SUCCESS) {
						RobotLog.info("SelectCareerHandler[selectCareer] 成功");
						return true;
					} else {
						RobotLog.fail("SelectCareerHandler[selectCareer] 服务器处理消息失败:" + rsp.getResultReason());
						return true;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("SelectCareerHandler[selectCareer] 失败", e);
					return false;
				}
			}
		});
		return success;
	}

	@Override
	public boolean executeMethod(Client client) {
		return selectCareer(client);
	}
}