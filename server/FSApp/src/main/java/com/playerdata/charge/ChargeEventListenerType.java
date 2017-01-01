package com.playerdata.charge;

import com.playerdata.charge.eventlistener.ActivityDailyRechargeListenerOfCharge;
import com.playerdata.charge.eventlistener.ChargeRankListenerOfCharge;
import com.playerdata.charge.eventlistener.DailyTaskListenerOfCharge;
import com.playerdata.charge.eventlistener.EvilBaoArriveListenerOfCharge;
import com.playerdata.charge.eventlistener.NotifyClientListenerOfCharge;
import com.playerdata.charge.eventlistener.PresentGiftListenerOfCharge;
import com.playerdata.charge.eventlistener.TargetSellListenerOfCharge;
import com.playerdata.charge.eventlistener.UserEventMgrListenerOfCharge;

public enum ChargeEventListenerType {

	PRESENT_GIFT(new PresentGiftListenerOfCharge()), // 赠送礼包
	USER_EVENT_MGR(new UserEventMgrListenerOfCharge()), // 事件通知
	TARGET_SELL_MGR(new TargetSellListenerOfCharge()), // 精准营销
	ACTIVITY_DAILY_RECHARGE_MGR(new ActivityDailyRechargeListenerOfCharge()), // 充值任务
	EVIL_BAO_ARRIVE(new EvilBaoArriveListenerOfCharge()), // 申公豹驾到
	NOTIFY_CLIENT(new NotifyClientListenerOfCharge()), // 通知客户端充值成功
	DAILY_TASK(new DailyTaskListenerOfCharge()), // 日常任务
	CHARGE_RANKING(new ChargeRankListenerOfCharge()), // 充值排行榜
	;
	private IChargeEventListener _listener;

	private ChargeEventListenerType(IChargeEventListener listener) {
		_listener = listener;
	}
	
	public IChargeEventListener getListener() {
		return _listener;
	}
}
