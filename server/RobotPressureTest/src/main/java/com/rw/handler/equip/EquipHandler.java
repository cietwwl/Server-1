package com.rw.handler.equip;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.itembag.ItemData;
import com.rwproto.EquipProtos.EquipEventType;
import com.rwproto.EquipProtos.EquipRequest;
import com.rwproto.EquipProtos.EquipResponse;
import com.rwproto.EquipProtos.TagMate;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class EquipHandler {

	private static EquipHandler instance = new EquipHandler();

	public static EquipHandler instance() {
		return instance;
	}

	public boolean compose(Client client, int modelId) {

		EquipRequest.Builder req = EquipRequest.newBuilder().setEventType(EquipEventType.Equip_Compose).setEquipId(modelId);

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
						if (error == ErrorType.SUCCESS) {
							RobotLog.info("EquipHandler[compose] 成功");
							return true;

						} else {
							RobotLog.fail("EquipHandler[compose] 服务器返回结果为失败" + error);

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

	/**
	 * 穿装备
	 * 
	 * @param client
	 * @return
	 */
	public boolean wearEquip(Client client) {
		EquipRequest.Builder req = EquipRequest.newBuilder().setEventType(EquipEventType.Wear_Equip).setEquipIndex(1).setRoleId(client.getUserId());
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
						RobotLog.fail("EquipHandler[wear] 转换响应消息为null");
						return false;
					} else {
						ErrorType error = rsp.getError();
						if (error == ErrorType.SUCCESS) {
							RobotLog.info("EquipHandler[wear] 成功");
							return true;
						} else {
							RobotLog.fail("EquipHandler[wear] 服务器返回结果为失败" + error);
							return false;
						}
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("EquipHandler[wear] 失败", e);
					return false;
				}
			}
		});

		return success;
	}

	/**
	 * 装备附灵，消耗804001 每次消耗两个
	 * 
	 * @param client
	 * @return
	 */
	public boolean equipAttach(Client client) {

		EquipRequest.Builder req = EquipRequest.newBuilder().setEventType(EquipEventType.Equip_Attach).setEquipIndex(1).setRoleId(client.getUserId());
		ItemData itemData = client.getItembagHolder().getByModelId(804001);
		if (itemData == null) {
			RobotLog.info("EquipHandler[attach] 失败，材料804001数量不足!");
			return false;
		}

		TagMate.Builder mate = TagMate.newBuilder();
		mate.setId(itemData.getId());
		mate.setCount(2);
		req.addMate(mate);

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
						RobotLog.fail("EquipHandler[attach] 转换响应消息为null");
						return false;
					} else {
						ErrorType error = rsp.getError();
						if (error == ErrorType.SUCCESS) {
							RobotLog.info("EquipHandler[attach] 成功");
							return true;

						} else {
							RobotLog.fail("EquipHandler[attach] 服务器返回结果为失败" + error);

							return false;
						}
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("EquipHandler[attach] 失败", e);
					return false;
				}
			}
		});
		return success;
	}

	/**
	 * 英雄进阶，只以主角为准
	 * 
	 * @param client
	 * @return
	 */
	public boolean heroAdvance(Client client) {
		EquipRequest.Builder req = EquipRequest.newBuilder();
		req.setEventType(EquipEventType.Advance);
		req.setRoleId(client.getUserId());

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
						RobotLog.fail("EquipHandler[heroAdvance] 转换响应消息为null");
						return false;
					} else {
						ErrorType error = rsp.getError();
						if (error == ErrorType.SUCCESS) {
							RobotLog.info("EquipHandler[heroAdvance] 成功");
							return true;
						} else {
							RobotLog.fail("EquipHandler[heroAdvance] 服务器返回结果为失败" + error);
							return false;
						}
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("EquipHandler[heroAdvance] 失败", e);
					return false;
				}
			}
		});
		return success;
	}
}