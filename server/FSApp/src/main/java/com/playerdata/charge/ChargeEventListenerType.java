package com.playerdata.charge;

import com.playerdata.charge.eventlistener.ActivityDailyRechargeListenerOfCharge;
import com.playerdata.charge.eventlistener.ChargeInfoUpdateListenerOfCharge;
import com.playerdata.charge.eventlistener.DailyTaskListenerOfCharge;
import com.playerdata.charge.eventlistener.EvilBaoArriveListenerOfCharge;
import com.playerdata.charge.eventlistener.NotifyClientListenerOfCharge;
import com.playerdata.charge.eventlistener.PresentGiftListenerOfCharge;
import com.playerdata.charge.eventlistener.TargetSellListenerOfCharge;
import com.playerdata.charge.eventlistener.UserEventMgrListenerOfCharge;

public enum ChargeEventListenerType {

	PRESENT_GIFT(PresentGiftListenerOfCharge.class),
	USER_EVENT_MGR(UserEventMgrListenerOfCharge.class),
	TARGET_SELL_MGR(TargetSellListenerOfCharge.class),
	ACTIVITY_DAILY_RECHARGE_MGR(ActivityDailyRechargeListenerOfCharge.class),
	EVIL_BAO_ARRIVE(EvilBaoArriveListenerOfCharge.class),
	CHARGE_INFO_UPDATE(ChargeInfoUpdateListenerOfCharge.class),
	NOTIFY_CLIENT(NotifyClientListenerOfCharge.class),
	DAILY_TASK(DailyTaskListenerOfCharge.class)
	;
	private IChargeEventListener _listener;

	private ChargeEventListenerType(Class<? extends IChargeEventListener> clazz) {
		try {
			_listener = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public IChargeEventListener getListener() {
		return _listener;
	}
}
