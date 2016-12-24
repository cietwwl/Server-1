package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ProtocolMessageEnum;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.charge.ChargeEventListenerType;
import com.playerdata.charge.ChargeMgr;
import com.playerdata.charge.IChargeAction;
import com.playerdata.charge.IChargeCallbackChecker;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.charge.dao.ChargeRecord;
import com.playerdata.charge.dao.ChargeRecordDAO;
import com.playerdata.charge.data.ChargeOrderInfo;
import com.playerdata.charge.data.ChargeParam;
import com.rw.chargeServer.ChargeContentPojo;
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.ServerSwitch;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.behavior.GameBehaviorMgr;
import com.rw.service.yaowanlog.YaoWanLogHandler;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.ChargeServiceProto;
import com.rwproto.MsgDef;

public class ChargeMgrHF extends ChargeMgr {
	
//	private Map<String, Boolean> _processOrders = new ConcurrentHashMap<String, Boolean>();
	private Map<String, Boolean> _processOrders;
	private static final Boolean PRESENT = Boolean.TRUE;
	private IChargeCallbackChecker<ChargeContentPojo> _checker;
	
	@SuppressWarnings("unchecked")
	public ChargeMgrHF() throws Exception {
		Field field = ChargeMgr.class.getDeclaredField("_processOrders");
		field.setAccessible(true);
		this._processOrders = (Map<String, Boolean>)field.get(ChargeMgr.getInstance());
		field.setAccessible(false);
		
		Field fChecker = ChargeMgr.class.getDeclaredField("_checker");
		fChecker.setAccessible(true);
		_checker = (IChargeCallbackChecker<ChargeContentPojo>)fChecker.get(ChargeMgr.getInstance());
		fChecker.setAccessible(false);
	}
	
	private Player getPlayerOfChargeOrder(ChargeContentPojo chargeContentPojo) {
		Player player = null;
		String uid = chargeContentPojo.getRoleId();
		if (StringUtils.isBlank(uid)) {
			GameLog.error("chargemgr", "sdk-充值", "uid异常，无法获取uid，订单号为" + chargeContentPojo.getCpTradeNo());
			return player;
		}
		player = PlayerMgr.getInstance().find(uid);
		return player;
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
	
	private void updateVipLv(Player player, ChargeInfo chargeInfo) {
		int totalChargeGold = chargeInfo.getTotalChargeGold(); // 充值的总金额
		PrivilegeCfgDAO privilegeCfgDAO = PrivilegeCfgDAO.getInstance();
		PrivilegeCfg cfg = privilegeCfgDAO.getCfg(player.getVip() + 1);
		while (cfg != null && cfg.getRechargeCount() <= totalChargeGold) {
			player.AddVip(1);
			cfg = privilegeCfgDAO.getCfg(player.getVip() + 1); // 获取下一级的cfg
		}
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
		updateVipLv(player, chargeInfo);

		// 设置界面更新vip
		player.getSettingMgr().checkOpen();
		// System.out.println("=========================== 玩家：" + player +"，总共充值次数：" + chargeInfo.getCount() +" =========================== ");
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
			} else {
				if (target.getChargeType() == ChargeTypeEnum.Normal) {
					// 只有普通充值才进行金额校验
					int money = chargeContentPojo.getMoney();
					if (money == -1) {
						money = chargeContentPojo.getItemAmount() * 10; // itemAmount是钻石数量，商品的价格是分，所以要乘上10
					}
					if (money != target.getMoneyCount()) {
						GameLog.error("chargemgr", "sdk-充值", "充值失败,价格不匹配； 商品价格 =" + target.getMoneyCount() + " 订单金额 =" + money + " 商品id=" + chargeContentPojo.getItemId() + " 订单号=" + chargeContentPojo.getCpTradeNo());
						return false;
					}
				}
			}

			IChargeAction action = target.getChargeType().getAction();
			if (action != null) {
				registerBehavior(player);
				try {
					success = action.doCharge(player, target, chargeParam);
					if (success) {
						updateChargeInfoAndAddVipExp(player, target); // 更新ChargeInfo数据和vip经验
						EnumSet<ChargeEventListenerType> allListenerType = EnumSet.allOf(ChargeEventListenerType.class);
						for (ChargeEventListenerType type : allListenerType) {
							try {
								type.getListener().notifyCharge(player, target, vipBefore);
								// System.out.println("充值回调，类型：" + type + "，listener类型：" + type.getListener());
							} catch (Throwable e) {
								GameLog.error("chargeMgr", player.getUserId(), "充值事件通知出错，类型：" + type, e);
							}
						}
						chargeContentPojo.setMoney(target.getMoneyCount());
						BILogMgr.getInstance().logPayFinish(player, chargeContentPojo, vipBefore, target, entranceId);

						// 通知要玩，充值完成了
						YaoWanLogHandler.getHandler().sendChargeLogHandler(player, chargeParam, chargeContentPojo.getCpTradeNo(), target.getMoneyCount());
					} else {
						GameLog.error("chargemgr", "sdk-充值", "充值失败,商品价值;  " + chargeContentPojo.getMoney() + "元" + ",充值类型 =" + target.getChargeType() + " 商品id =" + chargeContentPojo.getItemId() + " 订单号 =" + chargeContentPojo.getCpTradeNo());
					}
				} finally {
					DataEventRecorder.endAndPollCollections();
				}
			} else {
				GameLog.error("chargemgr", "sdk-充值", "充值失败, 找不到对应类型的充值行为； 商品id =" + chargeContentPojo.getItemId() + " 订单号 =" + chargeContentPojo.getCpTradeNo() + "，chargeType=" + target.getChargeType());
			}
		}
		return success;
	}

	private void registerBehavior(Player player) {
		try {
			MsgDef.Command command = MsgDef.Command.MSG_CHARGE;
			ChargeServiceProto.ChargeServiceCommonReqMsg.Builder req = ChargeServiceProto.ChargeServiceCommonReqMsg.newBuilder();
			req.setReqType(ChargeServiceProto.RequestType.Charge);
			ProtocolMessageEnum type = req.getReqType();
			GameBehaviorMgr.getInstance().registerBehavior(player.getUserId(), command, type, 0);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public boolean charge(ChargeContentPojo chargeContentPojo) {
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
						GameLog.error("chargemgr", "sdk-充值",
								"重复的订单编号！商品id：" + chargeContentPojo.getItemId() + " ； uid =" + chargeContentPojo.getUserId() + " 订单号 = " + chargeContentPojo.getCpTradeNo());
					}
				} else {
					GameLog.error("chargemgr", "sdk-充值", "重复的订单编号！商品id：" + chargeContentPojo.getItemId() + " ； uid =" + chargeContentPojo.getUserId() + " 订单号 = " + chargeContentPojo.getCpTradeNo());
				}
			}
		} finally {
			_processOrders.remove(chargeContentPojo.getCpTradeNo());
		}
		return success;
	}
}
