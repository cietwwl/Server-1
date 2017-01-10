package com.playerdata.charge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.ServerSwitch;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.behavior.GameBehaviorMgr;
import com.rw.service.yaowanlog.YaoWanLogHandler;
import com.rwbase.ServerTypeMgr;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.ChargeServiceProto;
import com.rwproto.ChargeServiceProto.ChargeCfgData;
import com.rwproto.ChargeServiceProto.ChargeServiceCommonRspMsg;
import com.rwproto.ChargeServiceProto.RequestType;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;

public class ChargeMgr {

	private static ChargeMgr instance = new ChargeMgr();
	private static final Boolean PRESENT = Boolean.TRUE;

	public static ChargeMgr getInstance() {
		return instance;
	}

	private IChargeCallbackChecker<ChargeContentPojo> _checker;
	private final Map<String, Boolean> _processOrders = new ConcurrentHashMap<String, Boolean>(128, 1.0f);

	protected ChargeMgr() {
		_checker = new YinHanChargeCallbackChecker();
	}
	
	private void checkVipMonthCardExists(Player player) {
		if (isValid(player, ChargeTypeEnum.VipMonthCard)) {
			ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
			if (chargeInfo != null) {
				List<ChargeCfg> allCfg = ChargeCfgDao.getInstance().getAllCfg();
				for (int i = 0; i < allCfg.size(); i++) {
					ChargeCfg cfg = allCfg.get(i);
					if (cfg.getChargeType() == ChargeTypeEnum.VipMonthCard) {
						if (!chargeInfo.isContainsId(cfg.getId())) {
							chargeInfo.addChargeCfgId(cfg.getId());
							ChargeInfoHolder.getInstance().updateToDB(chargeInfo);
							break;
						}
					}
				}
			}
		}
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

	public void syn(Player player, int version) {
		checkVipMonthCardExists(player);
		sendAllChargeCfg(player);
		ChargeInfoHolder.getInstance().syn(player, version);
	}

	public ChargeInfo getChargeInfo(String userId) {
		return ChargeInfoHolder.getInstance().get(userId);
	}
	
	private void sendAllChargeCfg(Player player) {
		List<ChargeCfgData> allCfgProtos = ChargeCfgDao.getInstance().getAllCfgProtos();
		ChargeServiceCommonRspMsg.Builder builder = ChargeServiceCommonRspMsg.newBuilder();
		builder.setReqType(RequestType.GetChargeCfg);
		builder.setIsSuccess(true);
		builder.addAllAllChargeCfgs(allCfgProtos);
		player.SendMsg(Command.MSG_CHARGE_CFG_REQUEST, builder.build().toByteString());
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
		if (StringUtils.isBlank(uid)) {
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
			} else {
				boolean checkMonneyMatch = true;
				if (ServerTypeMgr.getInstance().getServerType().isIos()) {
					// 苹果版本只检查普通重充值是否金额匹配
					checkMonneyMatch = target.getChargeType() == ChargeTypeEnum.Normal; 
				}
				if (checkMonneyMatch) {
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
						updateChargeInfoAndAddVipExp(player, target, chargeParam.getFriendId()); // 更新ChargeInfo数据和vip经验
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
						YaoWanLogHandler.getHandler().sendChargeLogHandler(player, chargeParam, chargeContentPojo.getCpTradeNo(), chargeContentPojo.getMoney());
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
	private void updateChargeInfoAndAddVipExp(Player player, ChargeCfg target, String friendId) {
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		chargeInfo.addTotalChargeGold(target.getVipExp()).addTotalChargeMoney(target.getMoneyCount()).addCount(1);
		if (target.getChargeType() == ChargeTypeEnum.VipMonthCard && friendId != null && (friendId = friendId.trim()).length() > 0) {
			ChargeInfo friendChargeInfo = ChargeInfoHolder.getInstance().get(friendId);
			if (!friendChargeInfo.isContainsId(target.getId())) {
				friendChargeInfo.addChargeCfgId(target.getId());
			}
			Player friend = PlayerMgr.getInstance().findPlayerFromMemory(friendId);
			if(friend != null) {
				ChargeInfoHolder.getInstance().update(friend);
			} else {
				ChargeInfoHolder.getInstance().updateToDB(friendChargeInfo);
			}
		} else {
			if (!chargeInfo.isContainsId(target.getId())) {
				chargeInfo.addChargeCfgId(target.getId());
			}
		}
		ChargeInfoHolder.getInstance().update(player);

		// 升级vip，如果达到条件
		updateVipLv(player, chargeInfo);

		// 设置界面更新vip
		player.getSettingMgr().checkOpen();
		// System.out.println("=========================== 玩家：" + player +"，总共充值次数：" + chargeInfo.getCount() +" =========================== ");
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
						GameLog.error("chargemgr", "sdk-充值", "重复的订单编号！商品id：" + chargeContentPojo.getItemId() + " ； uid =" + chargeContentPojo.getUserId() + " 订单号 = " + chargeContentPojo.getCpTradeNo());
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
				updateChargeInfoAndAddVipExp(player, target, null);
			}
			result.setSuccess(success);
			result.setTips("充值成功.");
		} else {
			result.setTips("商品不存在！");
		}
		return result;
	}

	/**
	 * 
	 * <pre>
	 * 直接赠送月卡给玩家，此方法一般情况下只允许充值返利功能调用。
	 * 此方法内部只负责把月卡添加到玩家身上，并不处理任何的VIP经验已经VIP礼包的发放
	 * </pre>
	 * 
	 * @param player 目标玩家
	 * @param monthCardType 月卡类型
	 * @param count 月卡的数量，至尊月卡只会处理一次
	 * @return
	 */
	public ChargeResult addMonthCard(Player player, ChargeTypeEnum monthCardType, int count) {
		ChargeResult result = ChargeResult.newResult(false);
		if (count <= 0) {
			result.setTips("非法参数");
			return result;
		}
		if (player != null) {
			switch (monthCardType) {
			case VipMonthCard:
				count = 1;
				break;
			case MonthCard:
				break;
			default:
				result.setTips("该类型不是月卡！");
				return result;
			}
			List<ChargeCfg> allCfg = ChargeCfgDao.getInstance().getAllCfg();
			for (ChargeCfg cfg : allCfg) {
				if (cfg.getChargeType() == monthCardType) {
					ChargeParam chargeParam = new ChargeParam();
					for (int i = 0; i < count; i++) {
						monthCardType.getAction().doCharge(player, cfg, chargeParam);
					}
					ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
					if (!chargeInfo.isContainsId(cfg.getId())) {
						chargeInfo.addChargeCfgId(cfg.getId());
					}
					break;
				}
			}
			result.setSuccess(true);
		} else {
			result.setTips("参数错误！");
		}
		return result;
	}

	/**
	 * <pre>
	 * 屏蔽玩家首充奖励。
	 * 调用此方法会把玩家的是否发放首充奖励设置为true
	 * </pre>
	 * 
	 * @param player 目标玩家
	 * @param syn 是否同步到客户端
	 */
	public boolean disableFirstChargeRewardOfPlayer(Player player, boolean syn) {
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		if (chargeInfo != null) {
			chargeInfo.setFirstAwardTaken(true);
			if (syn) {
				ChargeInfoHolder.getInstance().update(player);
			} else {
				ChargeInfoHolder.getInstance().updateToDB(chargeInfo);
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * <pre>
	 * 为玩家增加VIP经验
	 * </pre>
	 * 
	 * @param player 目标玩家
	 * @param vipExp 增加的VIP经验
	 * @param syn 是否同步到客户端
	 */
	public boolean addVipExp(Player player, int vipExp, boolean syn) {
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		if (chargeInfo != null) {
			chargeInfo.addTotalChargeGold(vipExp);
			if (syn) {
				ChargeInfoHolder.getInstance().update(player);
			} else {
				ChargeInfoHolder.getInstance().updateToDB(chargeInfo);
			}
			updateVipLv(player, chargeInfo);
			return true;
		}
		return false;
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
					// System.out.println("充值回调，类型：" + type + "，listener类型：" + type.getListener());
				} catch (Exception e) {
					GameLog.error("chargeMgr", player.getUserId(), "充值事件通知出错，类型：" + type, e);
				}
			}
		}

	}

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		String format = sdf.format(new Date(1481689764958l));
		System.err.println(format);
	}
}
