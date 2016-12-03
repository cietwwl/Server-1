package com.playerdata.charge;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ProtocolMessageEnum;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItemHolder;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.playerdata.charge.checker.YinHanChargeCallbackChecker;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.charge.dao.ChargeRecord;
import com.playerdata.charge.dao.ChargeRecordDAO;
import com.playerdata.charge.data.ChargeOrderInfo;
import com.playerdata.charge.data.ChargeParam;
import com.rw.chargeServer.ChargeContentPojo;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.ServerSwitch;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.behavior.GameBehaviorMgr;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.ChargeServiceProto;
import com.rwproto.MsgDef;

public class ChargeMgr {
	
	private static ChargeMgr instance = new ChargeMgr();
	private static final Boolean PRESENT = Boolean.TRUE;
	
	public static ChargeMgr getInstance(){
		return instance;
	}
	
	private IChargeCallbackChecker<ChargeContentPojo> _checker;
	private final Map<String, Boolean> _processOrders = new ConcurrentHashMap<String, Boolean>(128, 1.0f);
	protected ChargeMgr() {
		_checker = new YinHanChargeCallbackChecker();
	}
	
	public boolean isValid(Player player, ChargeTypeEnum monthCardType) {
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();
		ActivityTimeCardTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if (dataItem == null) {
			return false;
		}
		List<ActivityTimeCardTypeSubItem> monthCardList = dataItem.getSubItemList();
		ActivityTimeCardTypeSubItem targetItem = null;
		String cardtype = monthCardType.getCfgId();
		for (ActivityTimeCardTypeSubItem itemTmp : monthCardList) {
			if (StringUtils.equals(itemTmp.getChargetype(), cardtype)) {
				targetItem = itemTmp;
				break;
			}
		}
		if (targetItem != null) {
			return targetItem.getDayLeft() > 0;
		}
		return false;
	}
	
	public void syn(Player player, int version){		
		ChargeInfoHolder.getInstance().syn(player,version);
	}
	
	public ChargeInfo getChargeInfo(String userId){
		return ChargeInfoHolder.getInstance().get(userId);
	}
	
	// 主要检测是不是测试订单，并且能不能使用测试订单
	private boolean verifyTestOrder(ChargeContentPojo chargeContentPojo) {
		String privateField = chargeContentPojo.getPrivateField();
		if (privateField != null && privateField.length() > 0) {
			ChargeParam info = JsonUtil.readValue(privateField, ChargeParam.class);
			if (info != null && info.getOrderInfo() != null && info.getOrderInfo().getOrderId().equals(ChargeOrderInfo.TEST_KEY)) {
				return ServerSwitch.isTestCharge();
			}
		}
		return false;
	}
	
	private ChargeRecord createChargeRecord(Player player, ChargeContentPojo chargeContentPojo) {
		ChargeRecord chargeRecord = _checker.generateChargeRecord(chargeContentPojo);
		chargeRecord.setSdkUserId(player.getUserDataMgr().getAccount());
		return chargeRecord;
	}
	
	private Player getPlayerOfChargeOrder(ChargeContentPojo chargeContentPojo) {
		Player player = null;		
		String uid = chargeContentPojo.getRoleId();
		if(StringUtils.isBlank(uid)){
			GameLog.error("chargemgr", "sdk-充值", "uid异常，无法获取uid，订单号为" + chargeContentPojo.getCpTradeNo());
			return player;
		}
		player = PlayerMgr.getInstance().find(uid);
		return player;
	}
	
	private boolean chargeType(Player player, ChargeContentPojo chargeContentPojo) {
		int vipBefore = player.getVip();
		String itemId = chargeContentPojo.getItemId().trim(); // ios包没有 itemId字段
		
		String privateField = chargeContentPojo.getPrivateField();
		ChargeParam chargeParam = JsonUtil.readValue(privateField, ChargeParam.class);
		String entranceId = chargeParam.getChargeEntrance();

		if (StringUtils.isEmpty(itemId)) {
			itemId = chargeParam.getProductId();
		}
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);

		boolean success = false;
		if (target != null) {
			if (ServerSwitch.isTestCharge()) {
				GameLog.error("chargemgr", "sdk-充值", "充值测试,价格为1分； 商品价格 =" + target.getMoneyCount() + " 订单金额 =" + chargeContentPojo.getMoney() + " 商品id=" + chargeContentPojo.getItemId() + " 订单号=" + chargeContentPojo.getCpTradeNo());
			} else if (chargeContentPojo.getMoney() != target.getMoneyCount()) {
				GameLog.error("chargemgr", "sdk-充值", "充值失败,价格不匹配； 商品价格 =" + target.getMoneyCount() + " 订单金额 =" + chargeContentPojo.getMoney() + " 商品id=" + chargeContentPojo.getItemId() + " 订单号=" + chargeContentPojo.getCpTradeNo());
				return false;
			}

			IChargeAction action = target.getChargeType().getAction();
			if (action != null) {
				success = action.doCharge(player, target, chargeParam);
				if (success) {
					updateChargeInfoAndAddVipExp(player, target); // 更新ChargeInfo数据和vip经验
					GameWorldFactory.getGameWorld().asynExecute(new ChargeNotifyTask(player, target, vipBefore));
					registerBehavior(player);
					BILogMgr.getInstance().logPayFinish(player, chargeContentPojo, vipBefore, target, entranceId);
				} else {
					GameLog.error("chargemgr", "sdk-充值", "充值失败,商品价值;  " + chargeContentPojo.getMoney() + "元" + ",充值类型 =" + target.getChargeType() + " 商品id =" + chargeContentPojo.getItemId() + " 订单号 =" + chargeContentPojo.getCpTradeNo());
				}
			} else {
				GameLog.error("chargemgr", "sdk-充值", "充值失败, 找不到对应类型的充值行为； 商品id =" + chargeContentPojo.getItemId() + " 订单号 =" + chargeContentPojo.getCpTradeNo() + "，chargeType=" + target.getChargeType());
			}
		}
		return success;
	}

	private void registerBehavior(Player player){
	    MsgDef.Command command = MsgDef.Command.MSG_CHARGE;
	    ChargeServiceProto.ChargeServiceCommonReqMsg.Builder req = ChargeServiceProto.ChargeServiceCommonReqMsg.newBuilder();
	    req.setReqType(ChargeServiceProto.RequestType.Charge);
	    ProtocolMessageEnum type = req.getReqType();
	    String value = String.valueOf(type.getNumber());
	    GameBehaviorMgr.getInstance().registerBehavior(player, command, type, value, 0);
	}
	
	/**
	 * 充值完成，更新vip信息
	 * 
	 * @param player
	 * @param target
	 */
	private void updateChargeInfoAndAddVipExp(Player player, ChargeCfg target) {
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		chargeInfo.addTotalChargeGold(target.getVipExp()).addTotalChargeMoney(target.getMoneyCount()).addCount(1);
		if (!chargeInfo.isContainsId(target.getId())) {
			chargeInfo.addChargeCfgId(target.getId());
		}
		ChargeInfoHolder.getInstance().update(player);
		
		// 升级vip，如果达到条件
		int totalChargeGold = chargeInfo.getTotalChargeGold(); // 充值的总金额
		PrivilegeCfgDAO privilegeCfgDAO = PrivilegeCfgDAO.getInstance();
		PrivilegeCfg cfg = privilegeCfgDAO.getCfg(player.getVip() + 1);
		while (cfg != null && cfg.getRechargeCount() <= totalChargeGold) {
			player.AddVip(1);
			cfg = privilegeCfgDAO.getCfg(player.getVip() + 1); // 获取下一级的cfg
		}
		
		// 设置界面更新vip
		player.getSettingMgr().checkOpen();
//		System.out.println("=========================== 玩家：" + player + "，总共充值次数：" + chargeInfo.getCount() + " =========================== ");
	}
	
	public boolean charge(ChargeContentPojo chargeContentPojo){
		if (chargeContentPojo.getCpTradeNo() == null) {
			return false;
		}
		Boolean pre = _processOrders.put(chargeContentPojo.getCpTradeNo(), PRESENT);
		if (pre != null) {
			// 订单处理中
			GameLog.info("chargeMgr", chargeContentPojo.getCpTradeNo(), "订单正在处理中！");
			return true;
		}
		boolean success = false;
		// 充值，保存订单，返回结果
		Player player = getPlayerOfChargeOrder(chargeContentPojo);
		try {
			if (player != null) {
				if (!_checker.checkChargeCallback(chargeContentPojo)) {
					if (!verifyTestOrder(chargeContentPojo)) {
						return false;
					}
				}
				if (!ChargeRecordDAO.getInstance().isRecordExists(chargeContentPojo.getCpTradeNo())) {
					ChargeRecord chargeRecord = createChargeRecord(player, chargeContentPojo);
					if (ChargeRecordDAO.getInstance().addChargeRecord(chargeRecord)) {
						success = chargeType(player, chargeContentPojo);
					} else {
						GameLog.error("chargemgr", "sdk-充值", "重复的订单编号！面额" + chargeContentPojo.getMoney() + "元" + " ； uid =" + chargeContentPojo.getUserId() + " 订单号 = " + chargeContentPojo.getCpTradeNo());
					}
				} else {
					GameLog.error("chargemgr", "sdk-充值", "重复的订单编号！面额" + chargeContentPojo.getMoney() + "元" + " ； uid =" + chargeContentPojo.getUserId() + " 订单号 = " + chargeContentPojo.getCpTradeNo());
				}
			}
		} finally {
			_processOrders.remove(chargeContentPojo.getCpTradeNo());
		}
		return success;
	}
	
	public ChargeResult testCharge(Player player, String itemId) {

		ChargeResult result = ChargeResult.newResult(false);
		if (!ServerSwitch.isTestCharge()) {
			result.setTips("非法操作");
			return result;
		}

		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);
		if (target != null) {
			boolean success = target.getChargeType().getAction().doCharge(player, target, new ChargeParam());
			if (success) {
				updateChargeInfoAndAddVipExp(player, target);
			}
			result.setSuccess(success);
			result.setTips("充值成功.");
		} else {
			result.setTips("商品不存在！");
		}
		return result;
	}
	
	protected static class ChargeNotifyTask implements Runnable {

		private Player player;
		private ChargeCfg target;
		private int vipBefore;

		public ChargeNotifyTask(Player player, ChargeCfg target, int vipBefore) {
			this.player = player;
			this.target = target;
			this.vipBefore = vipBefore;
		}

		@Override
		public void run() {
			EnumSet<ChargeEventListenerType> allListenerType = EnumSet.allOf(ChargeEventListenerType.class);
			for (ChargeEventListenerType type : allListenerType) {
				try {
					type.getListener().notifyCharge(player, target, vipBefore);
//					System.out.println("充值回调，类型：" + type + "，listener类型：" + type.getListener());
				} catch (Exception e) {
					GameLog.error("chargeMgr", player.getUserId(), "充值事件通知出错，类型：" + type, e);
				}
			}
		}

	}
}
