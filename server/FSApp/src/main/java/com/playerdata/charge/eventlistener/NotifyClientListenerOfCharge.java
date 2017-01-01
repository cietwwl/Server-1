package com.playerdata.charge.eventlistener;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;
import com.rwproto.MsgDef.Command;

public class NotifyClientListenerOfCharge implements IChargeEventListener {

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		player.SendMsg(Command.MSG_CHARGE_NOTIFY, null, ByteString.EMPTY); // 充值成功通知
	}

}
