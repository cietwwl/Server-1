package com.rw.handler.magicSecret;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;
import com.rwproto.MagicSecretProto.msRequestType;
import com.rwproto.MagicSecretProto.msResultType;
import com.rwproto.MagicSecretProto.msRewardBox;
import com.rwproto.MagicSecretProto.msRewardBox.Builder;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class MagicSecretHandler {
	private static final int GET_SELF_MS_RANK = 9;
	private static final int GET_MS_RANK = 1;// 查看
	private static final int GET_MS_SWEEP_REWARD = 4;// 扫荡

	private static final int CHANGE_ARMY = 7;
	private static final int ENTER_MS_FIGHT = 2;// 战斗

	private static final int OPEN_REWARD_BOX = 6;// 打开箱子
	private static final int GIVE_UP_REWARD_BOX = 10;

	private static final int EXCHANGE_BUFF = 5;// 换buff
	private static final int GIVE_UP_BUFF = 11;

	public final static int STAGE_COUNT_EACH_CHATPER = 8;
	private final static int MAX_CHATPER_ID = 6;

	private static MagicSecretHandler handler = new MagicSecretHandler();

	public static MagicSecretHandler getHandler() {
		return handler;
	}

	public boolean getMagicSecretRank(Client client) {
		return getMsRank(client);
	}

	public boolean playMagicSecret(Client client) {
		boolean result = changeTeam(client);
		if (!result) {
			RobotLog.fail("战斗前的设置队伍反馈结果=" + result);
			return result;
		}

		String dungeonId = getDungeonId(client);
		if (dungeonId == null) {
			return sweep(client);
		}
		RobotLog.info("------------------------------"+dungeonId);
		result = fight(client, dungeonId);
		if (!result) {
			RobotLog.fail("战斗申请反馈结果=" + result);
			return result;
		}
		result = getReward(client);
		if (!result) {
			RobotLog.fail("领取前的生成奖励反馈结果=" + result);
			return result;
		}
		result = openBox(client);
		if (!result) {
			RobotLog.fail("领取道具反馈结果=" + result);
			return result;
		}
		RobotLog.info("------------------------------"+dungeonId);
		String[] split = dungeonId.split("_");
		if(Integer.parseInt(split[0]) % 100 == STAGE_COUNT_EACH_CHATPER){
			RobotLog.info("乾坤幻境操作成功=" + result);
			return result;
		}
		result = giveUpBox(client);
		if (!result) {
			RobotLog.fail("放弃道具反馈结果=" + result);
			return result;
		}
		result = exchangeBuff(client);
		if (!result) {
			RobotLog.fail("兑换buff反馈结果=" + result);
			return result;
		}
		RobotLog.info("乾坤幻境操作成功=" + result);
		return result;
	}

	public boolean getMsRank(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.GET_MS_RANK);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send] 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send] 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private boolean changeTeam(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.CHANGE_ARMY);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send]changeTeam 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send]changeTeam 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send]changeTeam 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private String getDungeonId(Client client) {
		MagicChapterInfoHolder magicChapterInfoHolder = client.getMagicChapterInfoHolder();
		Map<String, MagicChapterInfo> map = magicChapterInfoHolder.getList();
		MagicSecretHolder magicSecretHolder = client.getMagicSecretHolder();
		// 优先获取没有通过的幻境，如果所有幻境都通过则进行扫荡操作
		for (Iterator<Entry<String, MagicChapterInfo>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, MagicChapterInfo> next = iterator.next();
			MagicChapterInfo chapterInfo = next.getValue();
			
			List<Integer> finishedStages = chapterInfo.getFinishedStages();
			int maxValue = -1;
			for (Integer value : finishedStages) {
				if (value > maxValue) {
					maxValue = value;
				}
			}
			if (maxValue % 100 == STAGE_COUNT_EACH_CHATPER) {
				continue;
			}
			if(Integer.parseInt(chapterInfo.getChapterId()) > MAX_CHATPER_ID){
				break;
			}

			if (maxValue == -1) {
				return chapterInfo.getChapterId()+"01_3";
			} else {

				maxValue++;
				return maxValue + "_" + 3;
			}
		}
		// 执行到这里就表示没有幻境可以挑战 则进行扫荡
		return null;
	}

	private boolean fight(Client client, String dungeonId) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.ENTER_MS_FIGHT);
		req.setDungeonId(dungeonId);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send]fight 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send]fight 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send]fight 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private boolean getReward(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.GET_MS_SINGLE_REWARD);

		Map<String, UserMagicSecretData> userMagicSecretDatalist = client.getMagicSecretHolder().getList();
		UserMagicSecretData userMagicSecretData = userMagicSecretDatalist.get(client.getUserId());
		req.setDungeonId(userMagicSecretData.getCurrentDungeonID());
		req.setFinishState("3");

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send]getReward 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send]getReward 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send]getReward 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private boolean openBox(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.OPEN_REWARD_BOX);
		MagicSecretHolder magicSecretHolder = client.getMagicSecretHolder();

		UserMagicSecretData userMagicSecretData = magicSecretHolder.getList().get(client.getUserId());
		int stageId = userMagicSecretData.getMaxStageID();
		String chapter = String.valueOf(stageId / 100);
		req.setChapterId(chapter);

		Builder box = msRewardBox.newBuilder();
		box.setBoxID("2");
		box.setBoxCount(1);
		req.setRwdBox(box);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send]openBox 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send]openBox 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send]openBox 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private boolean giveUpBox(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.GIVE_UP_REWARD_BOX);
		MagicSecretHolder magicSecretHolder = client.getMagicSecretHolder();
		UserMagicSecretData userMagicSecretData = magicSecretHolder.getList().get(client.getUserId());
		int stageId = userMagicSecretData.getMaxStageID();
		String chapter = String.valueOf(stageId / 100);
		req.setChapterId(chapter);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send]giveUpBox 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send]giveUpBox 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send]giveUpBox 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private boolean exchangeBuff(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.EXCHANGE_BUFF);
		Map<String, MagicChapterInfo> magiChapterInfolist = client.getMagicChapterInfoHolder().getList();
		String chapterId = client.getMagicSecretHolder().getChapterId();
		
		if (magiChapterInfolist.size() > 0) {
			MagicChapterInfo magiChapterInfo = magiChapterInfolist.get(chapterId);
			req.setChapterId(magiChapterInfo.getChapterId());
			req.setBuffId(magiChapterInfo.getUnselectedBuff().get(0) + "");
		} else {
			req.setChapterId(client.getMagicSecretHolder().getChapterId());
			req.setBuffId("1");
		}

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send]exchangeBuff 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send]exchangeBuff 服务器处理消息失败 " + result);
						return false;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send]exchangeBuff 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

	private boolean sweep(Client client) {
		MagicSecretReqMsg.Builder req = MagicSecretReqMsg.newBuilder();
		req.setReqType(msRequestType.GET_MS_SWEEP_REWARD);
		Map<String, MagicChapterInfo> magiChapterInfolist = client.getMagicChapterInfoHolder().getList();

		// 随机一个幻境进行扫荡

		Set<String> keySet = magiChapterInfolist.keySet();
		List<String> chapterList = new ArrayList<String>();
		chapterList.addAll(keySet);

		Collections.shuffle(chapterList);

		String chapterId = chapterList.get(0);

		req.setChapterId(chapterId);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MAGIC_SECRET, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_MAGIC_SECRET;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					MagicSecretRspMsg rsp = MagicSecretRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MagicSecretHandler[send]exchangeBuff 转换响应消息为null");
						return false;
					}
					msResultType result = rsp.getRstType();
					if(result.equals(msResultType.TIMES_NOT_ENOUGH)){
						RobotLog.fail("MagicSecretHandler[send]exchangeBuff 服务器处理乾坤幻境已经完成次数 " + result);
						return true;
					}
					if (!result.equals(msResultType.SUCCESS)) {
						RobotLog.fail("MagicSecretHandler[send]exchangeBuff 服务器处理消息失败 " + result);
						return false;
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MagicSecretHandler[send]exchangeBuff 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}

}