package com.playerdata.charge.action;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Player;
import com.playerdata.charge.IChargeAction;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.playerdata.charge.data.ChargeParam;

public class CommonChargeAction implements IChargeAction {

	@Override
	public boolean  doCharge(Player player, ChargeCfg target, ChargeParam param) {

		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());

		player.getUserGameDataMgr().addReCharge(target.getGoldCount());
		// 派发限购的钻石奖励
		if (target.getGiveCount() > 0) {
			boolean isExtraGive = true;
			for (ChargeInfoSubRecording sub : chargeInfo.getPayTimesList()) {
				if (StringUtils.equals(target.getId(), sub.getId())) {// 有该道具的购买记录
					if (target.getGiveCount() <= sub.getCount()) {// 还有多余的限购次数
						isExtraGive = false;
						break;
					}
				}
			}
			if (isExtraGive) {
				ChargeInfoSubRecording sub = ChargeCfgDao.getInstance().newSubItem(target.getId());
				sub.setCount(sub.getCount() + 1);
				chargeInfo.getPayTimesList().add(sub);
				player.getUserGameDataMgr().addGold(target.getExtraGive());// 派出额外的钻石
			}
		}
		ChargeInfoHolder.getInstance().update(player);
		return true;
	}

}
