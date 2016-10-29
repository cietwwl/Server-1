package com.playerdata.activity.growthFund;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rw.netty.UserChannelMgr;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwproto.GrowthFundServiceProto.EGrowthFundRequestType;
import com.rwproto.GrowthFundServiceProto.EGrowthFundResultType;
import com.rwproto.GrowthFundServiceProto.GrowthFundResponse;
import com.rwproto.MsgDef.Command;

public class GrowthFundBoughtCountSynTask implements IGameTimerTask {

	@Override
	public String getName() {
		return "GrowthFundBoughtCountSynTask";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		GrowthFundGlobalData globalData = ActivityGrowthFundMgr.getInstance().getGlobalData();
		if (globalData.isNeedToSyn().compareAndSet(true, false)) {
			int boughtCount = globalData.getAlreadyBoughtCount();
			GrowthFundResponse.Builder builder = GrowthFundResponse.newBuilder();
			builder.setBoughtCount(boughtCount);
			builder.setReqType(EGrowthFundRequestType.NOTIFY_BOUGHT_COUNT_CHANGE);
			builder.setResultType(EGrowthFundResultType.SUCCESS);
			GrowthFundResponse response = builder.build();
//			List<Player> allPlayers = PlayerMgr.getInstance().getOnlinePlayers();
//			for (int i = 0, size = allPlayers.size(); i < size; i++) {
//				allPlayers.get(i).SendMsg(Command.MSG_BUY_GROWTH_FUND, response.toByteString());
//			}
			UserChannelMgr.broadcastMsg(Command.MSG_BUY_GROWTH_FUND, response.toByteString());
		}
		return "SUCCESS";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		
	}

	@Override
	public boolean isContinue() {
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

}
