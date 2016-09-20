package com.rw.service.battletower;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonReqMsg;
import com.rwproto.BattleTowerServiceProtos.BattleTowerCommonRspMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeBossStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeEndReqMsg;
import com.rwproto.BattleTowerServiceProtos.ChallengeStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.ERequestType;
import com.rwproto.BattleTowerServiceProtos.EResponseState;
import com.rwproto.BattleTowerServiceProtos.GetFriendBattleTowerRankInfoReqMsg;
import com.rwproto.BattleTowerServiceProtos.GetStrategyListReqMsg;
import com.rwproto.BattleTowerServiceProtos.SweepStartReqMsg;
import com.rwproto.BattleTowerServiceProtos.UseLuckyKeyReqMsg;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2015年9月1日 上午11:05:02
 * @Description 分发消息的Service
 */
public class BattleTowerService implements FsService<BattleTowerCommonReqMsg, ERequestType> {

	
	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(BattleTowerCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		BattleTowerCommonRspMsg.Builder commonRsp = BattleTowerCommonRspMsg.newBuilder();// 通用响应消息
		try {
			ERequestType reqType = request.getReqType();
			commonRsp.setReqType(reqType);
			commonRsp.setRspState(EResponseState.RSP_FAIL);// 设置默认响应状态

			switch (reqType) {
			case OPEN_MAIN_VIEW:// 打开主界面
				BattleTowerHandler.openBattleTowerMainView(player, commonRsp);
				break;
			case OPEN_CHALLENGE_VIEW:// 打开挑战界面
				BattleTowerHandler.openBattleTowerChallengeView(player, commonRsp);
				break;
			case GET_FRIEND_RANK_LIST:// 打开好友排行列表
				GetFriendBattleTowerRankInfoReqMsg rankReq = GetFriendBattleTowerRankInfoReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.getFriendBattleTowerRankList(player, rankReq, commonRsp);
				break;
			case GET_STRATEGY_LIST:// 获取某个里程碑中的战略信息
				GetStrategyListReqMsg strategyReq = GetStrategyListReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.getStrategyRoleInfoList(player, strategyReq, commonRsp);
				break;
			case OPEN_TRY_LUCK_VIEW:// 打开试手气界面
				BattleTowerHandler.openTryLuckView(player, commonRsp);
				break;
			case SWEEP_START:// 扫荡开始
				SweepStartReqMsg sweepStartReq = SweepStartReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.sweepStart(player, sweepStartReq, commonRsp);
				break;
			case SWEEP_END:// 扫荡结束
				BattleTowerHandler.sweepEnd(player, commonRsp);
				break;
			case USE_LUCKY_KEY:// 试手气
				UseLuckyKeyReqMsg useLuckyKeyReq = UseLuckyKeyReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.useLuckyKey(player, useLuckyKeyReq, commonRsp);
				break;
			case RESET_BATTLE_TOWER_DATA:// 重置试练塔数据
				BattleTowerHandler.resetBattleTowerData(player, commonRsp);
				break;
			case CHALLENGE_START:// 挑战开始
				ChallengeStartReqMsg challengeStartReq = ChallengeStartReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.battleTowerChallengeStart(player, challengeStartReq, commonRsp);
				break;
			case CHALLENGE_END:// 挑战结束
				ChallengeEndReqMsg challengeEndReq = ChallengeEndReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.battleTowerChallengeEnd(player, challengeEndReq, commonRsp);
				break;
			case CHALLENGE_BOSS_START:// 挑战Boss开始
				ChallengeBossStartReqMsg challengeBossStartReq = ChallengeBossStartReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.challengeBossStart(player, challengeBossStartReq, commonRsp);
				break;
			case CHALLENGE_BOSS_END:// 挑战Boss结束
				ChallengeBossEndReqMsg challengeBossEndReq = ChallengeBossEndReqMsg.parseFrom(request.getReqBody());
				BattleTowerHandler.challengeBossEnd(player, challengeBossEndReq, commonRsp);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return commonRsp.build().toByteString();
		}
	}

	@Override
	public BattleTowerCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		BattleTowerCommonReqMsg reqMsg = BattleTowerCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return reqMsg;
	}

	@Override
	public ERequestType getMsgType(BattleTowerCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}