package com.rw.handler.battletower;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.PrintMsg;
import com.rw.common.MsgLog;
import com.rw.common.MsgReciver;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonReqMsg;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonRspMsg;
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
	private Client client;// 客户端对象

	public BattleTowerHandler(Client client) {
		this.client = client;
	}

	public boolean openMainView() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.OPEN_MAIN_VIEW);

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					OpenMainViewRspMsg rsp = OpenMainViewRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean openChallengeView() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.OPEN_CHALLENGE_VIEW);

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					OpenChallengeViewRspMsg rsp = OpenChallengeViewRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean challengeBattleStart(int floor) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_START);
		ChallengeStartReqMsg.Builder req = ChallengeStartReqMsg.newBuilder();
		req.setFloor(floor);
		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(null));

		return true;
	}

	public boolean challengeBattleEnd(int floor, boolean result) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_END);
		ChallengeEndReqMsg.Builder req = ChallengeEndReqMsg.newBuilder();
		req.setFloor(floor);
		req.setResult(result);
		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					ChallengeEndRspMsg rsp = ChallengeEndRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean openLuckyView() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.OPEN_TRY_LUCK_VIEW);

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					OpenTryLuckViewRspMsg rsp = OpenTryLuckViewRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean useLuckyKey() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.USE_LUCKY_KEY);
		UseLuckyKeyReqMsg.Builder req = UseLuckyKeyReqMsg.newBuilder();
		req.setKeyType(EKeyType.KEY_COPPER);
		req.setUseNum(-1);
		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					UseLuckyKeyRspMsg rsp = UseLuckyKeyRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean sweepStart(int floor) {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.SWEEP_START);

		SweepStartReqMsg.Builder req = SweepStartReqMsg.newBuilder();
		req.setFloor(floor);
		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					SweepStartRspMsg rsp = SweepStartRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean sweepEnd() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.SWEEP_END);

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					SweepEndRspMsg rsp = SweepEndRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean challengeBossStart() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_BOSS_START);

		ChallengeBossStartReqMsg.Builder req = ChallengeBossStartReqMsg.newBuilder();
		req.setBossId(1);
		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(null));

		return true;
	}

	public boolean challengeBossEnd() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.CHALLENGE_BOSS_END);

		ChallengeBossEndReqMsg.Builder req = ChallengeBossEndReqMsg.newBuilder();
		req.setBossId(1);
		req.setResult(true);

		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					ChallengeBossEndRspMsg rsp = ChallengeBossEndRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean getFriendRankInfo() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.GET_FRIEND_RANK_LIST);

		GetFriendBattleTowerRankInfoReqMsg.Builder req = GetFriendBattleTowerRankInfoReqMsg.newBuilder();
		req.setPageIndex(1);
		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					GetFriendBattleTowerRankInfoRspMsg rsp = GetFriendBattleTowerRankInfoRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean getStrategyList() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.GET_STRATEGY_LIST);

		GetStrategyListReqMsg.Builder req = GetStrategyListReqMsg.newBuilder();
		req.setFloor(4);
		commonReq.setReqBody(req.build().toByteString());

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(new PrintMsg<BattleTowerCommonRspMsg>() {

			@Override
			public void print(BattleTowerCommonRspMsg commonRsp) {
				try {
					GetStrategyListRspMsg rsp = GetStrategyListRspMsg.parseFrom(commonRsp.getRspBody());
					System.err.println(rsp.toBuilder().build());
				} catch (InvalidProtocolBufferException e) {
					MsgLog.error("PrintMsg===================================", e);
				}
			}
		}));

		return true;
	}

	public boolean resetData() {
		if (client == null) {
			return false;
		}

		BattleTowerCommonReqMsg.Builder commonReq = BattleTowerCommonReqMsg.newBuilder();
		commonReq.setReqType(ERequestType.RESET_BATTLE_TOWER_DATA);

		client.getMsgHandler().sendMsg(Command.MSG_BATTLE_TOWER, commonReq.build().toByteString(), new Receiver(null));

		return true;
	}

	class Receiver implements MsgReciver {

		private PrintMsg<BattleTowerCommonRspMsg> msg;

		public Receiver(PrintMsg<BattleTowerCommonRspMsg> msg) {
			this.msg = msg;
		}

		@Override
		public Command getCmd() {
			return Command.MSG_BATTLE_TOWER;
		}

		@SuppressWarnings("finally")
		@Override
		public boolean execute(Client client, Response response) {
			try {
				BattleTowerCommonRspMsg commonRsp = BattleTowerCommonRspMsg.parseFrom(response.getSerializedContent());
				EResponseState rspState = commonRsp.getRspState();
				System.err.println(rspState);
				System.err.println(commonRsp.getReqType());
				if (rspState == EResponseState.RSP_FAIL) {
					return false;
				}

				if (msg != null) {
					msg.print(commonRsp);
				} else {
					System.err.println(commonRsp.toBuilder().build());
				}
			} catch (InvalidProtocolBufferException e) {
				MsgLog.error("Execute===================================", e);
			} finally {
				return true;
			}
		}
	}
}