package com.rw.handler.equip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.chat.GmHandler;
import com.rw.handler.itembag.ItemData;
import com.rwproto.EquipProtos.EquipEventType;
import com.rwproto.EquipProtos.EquipRequest;
import com.rwproto.EquipProtos.EquipResponse;
import com.rwproto.EquipProtos.TagMate;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class EquipHandler {

	private static final Random r = new Random();
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
	 * 装备附灵，消耗700174 每次消耗一个
	 * 
	 * @param client
	 * @return
	 */
	public boolean equipAttach(Client client) {
		Map<Integer, EquipItem> heroEquipItem = client.getHeroEquipHolder().getHeroEquipItem(client.getUserId());
		if (heroEquipItem == null || heroEquipItem.isEmpty()) {
			GmHandler.instance().send(client, "* wearequip 0");
			heroEquipItem = client.getHeroEquipHolder().getHeroEquipItem(client.getUserId());
		}

		if (heroEquipItem == null || heroEquipItem.isEmpty()) {
			RobotLog.info("EquipHandler[attach] 失败，角色身上没有任何装备!");
			return false;
		}

		List<Integer> indexList = new ArrayList<Integer>();
		for (Entry<Integer, EquipItem> e : heroEquipItem.entrySet()) {
			indexList.add(e.getKey());
		}

		int index = indexList.get(r.nextInt(indexList.size()));

		EquipRequest.Builder req = EquipRequest.newBuilder().setEventType(EquipEventType.Equip_Attach).setEquipIndex(index).setRoleId(client.getUserId());
		List<ItemData> itemList = client.getItembagHolder().getItemDataByModelId(700174);
		if (itemList == null || itemList.isEmpty()) {
			GmHandler.instance().send(client, "* additem " + 700174 + " 999");
			itemList = client.getItembagHolder().getItemDataByModelId(700174);
		}

		if (itemList == null || itemList.isEmpty()) {
			RobotLog.info("EquipHandler[attach] 失败，材料700174数量不足!");
			return false;
		}

		ItemData itemData = itemList.get(r.nextInt(itemList.size()));
		if (itemData == null) {
			RobotLog.info("EquipHandler[attach] 失败，材料700174数量不足!");
			return false;
		}

		TagMate.Builder mate = TagMate.newBuilder();
		mate.setId(itemData.getId());
		mate.setCount(1);
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