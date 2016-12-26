package com.gm.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;

public class GmAddVipExp implements IGmTask {

	private static Logger addVipExpLogger = Logger.getLogger("checkRebateLogger");
	
	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse resp = new GmResponse();
		resp.setCount(1);
		Map<String, Object> result = new HashMap<String, Object>(1, 1.5f);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(result);
		resp.setResult(list);
		Map<String, Object> args = request.getArgs();
		String userId = args.get("roleId").toString();
		String cfgId = args.get("chargeCfgId").toString();
		if (userId == null || userId.length() == 0) {
			result.put("RESULT", "找不到指定的玩家！");
			return resp;
		}
		ChargeCfg cfg = ChargeCfgDao.getInstance().getCfgById(cfgId);
		if (cfg == null) {
			result.put("RESULT", "找不到充值配置！");
			return resp;
		}
		Player player = PlayerMgr.getInstance().find(userId);
		if (player != null) {
			int preVip = player.getVip();
			ChargeInfo info = ChargeInfoHolder.getInstance().get(userId);
			int preVipExp = info.getTotalChargeGold();
			ChargeMgr.getInstance().addVipExp(player, cfg.getVipExp(), true);
			addVipExpLogger.info("userName:" + player.getUserName() + " add vip exp: cfgId:" + cfgId);
			ActivityDailyRechargeTypeMgr.getInstance().addFinishCount(player, cfg.getMoneyYuan());
			addVipExpLogger.info("userName:" + player.getUserName() + " add ActivityDailyRechargeTypeMgr: money:" + cfg.getMoneyYuan());
			EvilBaoArriveMgr.getInstance().addFinishCount(player, cfg.getMoneyYuan());
			addVipExpLogger.info("userName:" + player.getUserName() + " add EvilBaoArriveMgr: money:" + cfg.getMoneyYuan());
			int nowVipExp = info.getTotalChargeGold();
			result.put("RESULT", "操作成功，添加前信息：vip等级：" + preVip + "，vip经验：" + preVipExp + "；添加后结果：vip等级：" + player.getVip() + "，vip经验：" + nowVipExp);
		} else {
			result.put("RESULT", "找不到指定的玩家！");
		}
		return resp;
	}

}
