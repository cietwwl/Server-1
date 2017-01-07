package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.ChargeResult;
import com.playerdata.charge.cfg.ChargeTypeEnum;

public class GmAddMonthCard implements IGmTask {

	/**
	 * type 2 普通月卡 3 至尊月卡
	 */
	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try {
			Map<String, Object> args = request.getArgs();
			String userId = GmUtils.parseString(request.getArgs(), "roleId");
			int serverId = GmUtils.parseInt(args, "serverId");
			int type = GmUtils.parseInt(args, "type");

			response.setCount(1);
			Map<String, Object> result = new HashMap<String, Object>(1, 1.5f);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list.add(result);
			response.setResult(list);

			Player player = PlayerMgr.getInstance().find(userId);
			if (player != null) {
				ChargeMgr chargeMgr = ChargeMgr.getInstance();

				ChargeTypeEnum chargeType = ChargeTypeEnum.getById(String.valueOf(type));

				String monthCardDesc = "";
				ChargeResult addMonthCard = null;
				if (chargeType == ChargeTypeEnum.MonthCard) {
					addMonthCard = chargeMgr.addMonthCard(player, ChargeTypeEnum.MonthCard, 1);
					monthCardDesc = "普通月卡";
				}
				if (chargeType == ChargeTypeEnum.VipMonthCard) {
					addMonthCard = chargeMgr.addMonthCard(player, ChargeTypeEnum.VipMonthCard, 1);
					monthCardDesc = "至尊月卡";
				}

				boolean success = addMonthCard.isSuccess();
				Logger logger = GmAddVipExp.addVipExpLogger;
				if (success) {
					logger.info("userName:" + player.getUserName() + "添加月卡:" + monthCardDesc + "成功！");
					result.put("RESULT", "添加"+monthCardDesc+"成功！");
				} else {
					logger.info("userName:" + player.getUserName() + "添加月卡:" + monthCardDesc + "失败！");
					result.put("RESULT", "添加"+monthCardDesc+"失败！");
				}
			} else {
				result.put("RESULT", "找不到指定的玩家！");
			}

		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
