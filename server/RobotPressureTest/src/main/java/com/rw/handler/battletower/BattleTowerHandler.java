package com.rw.handler.battletower;

import java.util.List;
import java.util.Random;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.PrintMsg;
import com.rw.common.MsgLog;
import com.rw.common.MsgReciver;
import com.rw.handler.battletower.data.BattleTowerData;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonReqMsg;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonRspMsg;
import com.rwproto.BattleTowerServiceProtos.BossInfoMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.EKeyType;
import com.rwproto.BattleTowerServiceProtos.ERequestType;
import com.rwproto.BattleTowerServiceProtos.EResponseState;
import com.rwproto.BattleTowerServiceProtos.GetFriendBattleTowerRankInfoReqMsg;
import com.rwproto.BattleTowerServiceProtos.GetFriendBattleTowerRankInfoRspMsg;
import com.rwproto.BattleTowerServiceProtos.GetStrategyListReqMsg;
import com.rwproto.BattleTowerServiceProtos.GetStrategyListRspMsg;
import com.rwproto.BattleTowerServiceProtos.OpenChallengeViewRspMsg;
import com.rwproto.BattleTowerServiceProtos.OpenMainViewRspMsg;
import com.rwproto.BattleTowerServiceProtos.OpenTryLuckViewRspMsg;
import com.rwproto.BattleTowerServiceProtos.SweepEndRspMsg;
import com.rwproto.BattleTowerServiceProtos.SweepStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.SweepStartRspMsg;
import com.rwproto.BattleTowerServiceProtos.UseLuckyKeyReqMsg;
import com.rwproto.BattleTowerServiceProtos.UseLuckyKeyRspMsg;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 * @author HC
 * @date 2016年2月2日 下午4:50:59
 * @Description 
 */
public class BattleTowerHandler {

	private static final Random r = new Random();
	private static BattleTowerHandler handler = new BattleTowerHandler();

	public static BattleTowerHandler getHandler() {
		return handler;
	}

	private BattleTowerHandler() {
	}

	private static final Command command = Command.MSG_BATTLE_TOWER;
	private static final String functionName = "封神台";

	public boolean openMainView(Client client) {
		if (client == null) {
			return false;
		}

		final BattleTowerData battleTowerData = client.getBattleTowerData();

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.OPEN_MAIN_VIEW);

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					OpenMainViewRspMsg rsp = OpenMainViewRspMsg.parseFrom(commonRsp.getRspBody());
					battleTowerData.setSweepFloor(rsp.getSweepFloor());
					battleTowerData.setBossInfoMsg(rsp.getBossInfoMsgList());
					battleTowerData.setHighestFloor(rsp.getHighestFloor());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--打开主界面出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean openChallengeView(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.OPEN_CHALLENGE_VIEW);

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					OpenChallengeViewRspMsg rsp = OpenChallengeViewRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--打开挑战界面出现错误，OpenChallengeViewRspMsg==null");
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--打开挑战界面出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean challengeBattleStart(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_START);
		ChallengeStartReqMsg.Builder req = ChallengeStartReqMsg.newBuilder();
		req.setFloor(client.getBattleTowerData().getSweepFloor(r));
		commonReq.setReqBody(req.build().toByteString());

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(null));
		return success;
	}

	public boolean challengeBattleEnd(Client client) {
		if (client == null) {
			return false;
		}

		final BattleTowerData battleTowerData = client.getBattleTowerData();

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_END);
		ChallengeEndReqMsg.Builder req = ChallengeEndReqMsg.newBuilder();
		final int value = battleTowerData.getSweepFloor(r) + 2;
		req.setFloor(value);
		// boolean result = r.nextBoolean();
		// req.setResult(result);
		req.setResult(true);
		commonReq.setReqBody(req.build().toByteString());

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					ChallengeEndRspMsg rsp = ChallengeEndRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--挑战结束出现错误，ChallengeEndRspMsg==null");
					} else {
						battleTowerData.addBossInfo(rsp.getBossInfoMsgList());// 增加Boss
						battleTowerData.setSweepFloor(value + 1);
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--挑战结束出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean openLuckyView(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.OPEN_TRY_LUCK_VIEW);

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					OpenTryLuckViewRspMsg rsp = OpenTryLuckViewRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--打开兑换界面出现错误，OpenTryLuckViewRspMsg==null");
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--打开兑换界面出现异常", e);
				}
			}
		}));

		return success;
	}

	private static final EKeyType[] keyType = new EKeyType[] { EKeyType.KEY_COPPER, EKeyType.KEY_GOLD, EKeyType.KEY_SILVER };

	public boolean useLuckyKey(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.USE_LUCKY_KEY);
		UseLuckyKeyReqMsg.Builder req = UseLuckyKeyReqMsg.newBuilder();
		req.setKeyType(keyType[r.nextInt(keyType.length)]);
		req.setUseNum(r.nextInt(10));
		commonReq.setReqBody(req.build().toByteString());

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					UseLuckyKeyRspMsg rsp = UseLuckyKeyRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--使用幸运钥匙出现错误，UseLuckyKeyRspMsg==null");
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--使用幸运钥匙出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean sweepStart(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.SWEEP_START);

		SweepStartReqMsg.Builder req = SweepStartReqMsg.newBuilder();
		req.setFloor(client.getBattleTowerData().getSweepFloor(r));
		commonReq.setReqBody(req.build().toByteString());

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					SweepStartRspMsg rsp = SweepStartRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--扫荡开始出现错误，SweepStartRspMsg==null");
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--扫荡开始出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean sweepEnd(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.SWEEP_END);

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					SweepEndRspMsg rsp = SweepEndRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--扫荡结束出现错误，SweepEndRspMsg==null");
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--扫荡结束出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean challengeBossStart(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_BOSS_START);

		BattleTowerData battleTowerData = client.getBattleTowerData();
		List<BossInfoMsg> bossInfoMsg = battleTowerData.getBossInfoMsg();
		BossInfoMsg boss = null;
		if (!bossInfoMsg.isEmpty()) {
			int index = r.nextInt(bossInfoMsg.size());
			boss = bossInfoMsg.get(index);
			if (boss != null) {
				battleTowerData.setBattleTowerBossId(boss.getBossId());
			}
		}

		ChallengeBossStartReqMsg.Builder req = ChallengeBossStartReqMsg.newBuilder();
		req.setBossId(boss == null ? 0 : boss.getBossId());
		commonReq.setReqBody(req.build().toByteString());

		MsgLog.info("挑战BossId---" + battleTowerData.getBossId());
		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(null));
		return success;
	}

	public boolean challengeBossEnd(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_BOSS_END);

		final int bossId = client.getBattleTowerData().getBossId();

		ChallengeBossEndReqMsg.Builder req = ChallengeBossEndReqMsg.newBuilder();
		req.setBossId(bossId);
		// req.setResult(r.nextBoolean());
		req.setResult(true);

		commonReq.setReqBody(req.build().toByteString());

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					ChallengeBossEndRspMsg rsp = ChallengeBossEndRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--挑战Boss结束出现错误，ChallengeBossEndRspMsg==null");
					}

					MsgLog.info("挑战的BossId是" + bossId + "");
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--挑战Boss结束出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean getFriendRankInfo(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.GET_FRIEND_RANK_LIST);

		GetFriendBattleTowerRankInfoReqMsg.Builder req = GetFriendBattleTowerRankInfoReqMsg.newBuilder();
		// req.setPageIndex(r.nextInt(5) + 1);
		req.setPageIndex(1);
		commonReq.setReqBody(req.build().toByteString());

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					GetFriendBattleTowerRankInfoRspMsg rsp = GetFriendBattleTowerRankInfoRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--获取好友排行出现错误，GetFriendBattleTowerRankInfoRspMsg==null");
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--获取好友排行出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean getStrategyList(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.GET_STRATEGY_LIST);

		GetStrategyListReqMsg.Builder req = GetStrategyListReqMsg.newBuilder();
		req.setFloor(client.getBattleTowerData().getStrategyFloor(r));
		commonReq.setReqBody(req.build().toByteString());

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					GetStrategyListRspMsg rsp = GetStrategyListRspMsg.parseFrom(commonRsp.getRspBody());
					if (rsp == null) {
						MsgLog.info(functionName + "--获取攻略出现错误，GetStrategyListRspMsg==null");
					}
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error(functionName + "--获取攻略出现异常", e);
				}
			}
		}));

		return success;
	}

	public boolean resetData(Client client) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.RESET_BATTLE_TOWER_DATA);

		boolean success = client.getMsgHandler().sendMsg(command, commonReq.build().toByteString(), new Receiver(null));

		return success;
	}

	class Receiver implements MsgReciver {

		private PrintMsg<BattleTowerCommonRspMsg> msg;

		public Receiver(PrintMsg<BattleTowerCommonRspMsg> msg) {
			this.msg = msg;
		}

		@Override
		public Command getCmd() {
			return command;
		}

		@SuppressWarnings("finally")
		@Override
		public boolean execute(Client client, Response response) {
			try {
				BattleTowerCommonRspMsg commonRsp = BattleTowerCommonRspMsg.parseFrom(response.getSerializedContent());
				EResponseState rspState = commonRsp.getRspState();
				if (rspState == EResponseState.RSP_FAIL) {
					return false;
				}

				if (msg != null) {
					msg.print(commonRsp);
				}

				if (commonRsp.getReqType() == ERequestType.RESET_BATTLE_TOWER_DATA && rspState == EResponseState.RSP_SUCESS) {
					client.getBattleTowerData().setSweepFloor(0);
				}

				MsgLog.info("消息：" + commonRsp.getReqType() + ",处理结果是：" + rspState);
			} catch (InvalidProtocolBufferException e) {
				MsgLog.error(functionName + "--解析Common消息出现异常", e);
			} finally {
				return true;
			}
		}
	}
}