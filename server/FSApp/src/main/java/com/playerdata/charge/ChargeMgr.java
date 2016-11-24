package com.playerdata.charge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.bm.targetSell.TargetSellManager;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolMessageEnum;
import com.log.GameLog;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.VipMgr;
import com.playerdata.activity.dailyCharge.ActivityDailyRechargeTypeMgr;
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeCfgDAO;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfgDAO;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItem;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeItemHolder;
import com.playerdata.activity.timeCardType.data.ActivityTimeCardTypeSubItem;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.playerdata.charge.cfg.FirstChargeCfg;
import com.playerdata.charge.cfg.FirstChargeCfgDao;
import com.playerdata.charge.cfg.VipGiftCfg;
import com.playerdata.charge.cfg.VipGiftCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.playerdata.charge.dao.ChargeRecord;
import com.playerdata.charge.dao.ChargeRecordDAO;
import com.playerdata.charge.data.ChargeOrderInfo;
import com.playerdata.charge.data.ChargeParam;
import com.rw.chargeServer.ChargeContentPojo;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.manager.ServerSwitch;
import com.rw.service.Email.EmailUtils;
import com.rw.service.Privilege.MonthCardPrivilegeMgr;
import com.rw.service.dailyActivity.DailyActivityHandler;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.behavior.GameBehaviorMgr;
import com.rwbase.common.enu.eTaskFinishDef;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.gift.ComGiftCfg;
import com.rwbase.dao.gift.ComGiftCfgDAO;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.ChargeServiceProto;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;
import com.rwproto.VipProtos;
import com.rwproto.VipProtos.VIPGiftNotify;

public class ChargeMgr {

	public static final String SEND_MONTH_CARD_SUCCESS_EMAIL_ID = "17001";
	public static final String SEND_MONTH_CARD_FAIL_EMAIL_ID = "17002";
	
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
	
	public boolean isValid(Player player,ChargeTypeEnum monthCardType){
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();		
		ActivityTimeCardTypeItem dataItem = dataHolder.getItem(player.getUserId());
		if(dataItem == null){
//			GameLog.error("chargemgr", player.getUserId(), "数据库没数据就设置月卡特权");
			return false;
		}
		List<ActivityTimeCardTypeSubItem>  monthCardList = dataItem.getSubItemList();
		ActivityTimeCardTypeSubItem targetItem = null;
		String cardtype= monthCardType.getCfgId();
		for (ActivityTimeCardTypeSubItem itemTmp : monthCardList) {
			if(StringUtils.equals(itemTmp.getChargetype(), cardtype)){
				targetItem = itemTmp;
				break;
			}
		}
		if(targetItem!=null){
			return targetItem.getDayLeft() > 0?true:false;
		}		
		return false;
	}
	
	public void syn(Player player, int version){		
		ChargeInfoHolder.getInstance().syn(player,version);
	}
	
	public ChargeInfo getChargeInfo(String userId){
		return ChargeInfoHolder.getInstance().get(userId);
	}
	
	public ChargeResult buyAndTakeVipGift(Player player, String itemId){		
		ChargeResult result = ChargeResult.newResult(false);		
		VipGiftCfg target = VipGiftCfgDao.getInstance().getCfgById(itemId);
		int viplevel = Integer.parseInt(itemId);
		
		if(target!=null && !player.getVipMgr().isVipGiftTaken(viplevel)){			
			boolean success = buyVipGift(player, target,result);
			if(success){
				player.getVipMgr().setVipGiftTaken(viplevel);
				success = takeVipGift(player,target);
				result.setTips("购买成功");
				result.setSuccess(success);	
				
			}else{
				result.setTips("等级不足或货币不足");
				result.setSuccess(false);	
				player.getVipMgr().failToBuyVipGift(viplevel);
			}			
		}else{
			result.setSuccess(false);
			result.setTips("购买失败！");
		}
		return result;
	}
	
	private boolean takeVipGift(Player player,VipGiftCfg target) {
		ComGiftMgr.getInstance().addGiftById(player,target.getGift());			
		return true;
	}

	private boolean buyVipGift(Player player, VipGiftCfg target,ChargeResult result) {
		if(player.getVip() < Integer.parseInt(target.getVipLv())){
			result.setTips("Vip等级低于购买礼包等级");
			GameLog.error("chargeMgr", player.getUserId(), "Vip等级低于购买礼包等级");
			return false;
		}	
		
		if(player.getUserGameDataMgr().getGold() < Integer.parseInt(target.getCurCost())){
			result.setTips("货币不够");
			GameLog.error("chargeMgr", player.getUserId(), "货币不够");
			return false;
		}
		player.getUserGameDataMgr().addGold(-Integer.parseInt(target.getCurCost()));
		
		return true;
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

	public boolean charge(ChargeContentPojo chargeContentPojo){
		if (chargeContentPojo.getCpTradeNo() == null) {
			return false;
		}
		Boolean pre = _processOrders.put(chargeContentPojo.getCpTradeNo(), PRESENT);
		if (pre != null) {
			// 订单处理中
			return true;
		}
		boolean success = false;
		// 充值，保存订单，返回结果
		Player player = get(chargeContentPojo);
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
	
	private Player get(ChargeContentPojo chargeContentPojo) {
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
		String friendId = chargeParam.getFriendId();
	    
		if (StringUtils.isEmpty(itemId)) {
			itemId = chargeParam.getProductId();
		}
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);

		if (target != null) {
			if (ServerSwitch.isTestCharge()) {
				GameLog.error("chargemgr", "sdk-充值", "充值测试,价格为1分； 商品价格 =" + target.getMoneyCount() + " 订单金额 =" + chargeContentPojo.getMoney() + " 商品id=" + chargeContentPojo.getItemId() + " 订单号="
						+ chargeContentPojo.getCpTradeNo());
			} else if (chargeContentPojo.getMoney() != target.getMoneyCount()) {
				GameLog.error("chargemgr", "sdk-充值", "充值失败,价格不匹配； 商品价格 =" + target.getMoneyCount() + " 订单金额 =" + chargeContentPojo.getMoney() + " 商品id=" + chargeContentPojo.getItemId() + " 订单号="
						+ chargeContentPojo.getCpTradeNo());
				return false;
			}

			boolean success = false;
			if (target.getChargeType() == ChargeTypeEnum.Normal) {
				success = doCharge(player, target);
			} else if (target.getChargeType() == ChargeTypeEnum.MonthCard || target.getChargeType() == ChargeTypeEnum.VipMonthCard) {
				List<ActivityTimeCardTypeSubCfg> timeCardList = ActivityTimeCardTypeSubCfgDAO.getInstance().getAllCfg();
				for (ActivityTimeCardTypeSubCfg timecardcfg : timeCardList) {
					if (timecardcfg.getChargeType() == target.getChargeType()) {
						Player friendPlayer = null;
						if (StringUtils.isNotBlank(friendId)) {
							friendPlayer = PlayerMgr.getInstance().find(friendId);
							// 找不到要赠送的好友
							if (null == friendPlayer) {
								sendMonthCardFailHandler(player);
							}
						}
						if (null != friendPlayer) {
							success = sendMonthCard(friendPlayer, player, timecardcfg.getId(), target).isSuccess();
						} else {
							success = buyMonthCard(player, timecardcfg.getId(), target).isSuccess();
						}
						break;
					}
				}
			}
			UserEventMgr.getInstance().charge(player, chargeContentPojo.getMoney() / 100);
			// 这里检查一下精准营销有没有此角色的充值请求
//			TargetSellManager.getInstance().playerCharge(player, ServerSwitch.isTestCharge() ? target.getMoneyCount() : chargeContentPojo.getFee());
			TargetSellManager.getInstance().playerCharge(player, chargeContentPojo.getMoney() / 100);
			if (success) {
				ActivityDailyRechargeTypeMgr.getInstance().addFinishCount(player, chargeContentPojo.getMoney() / 100);
				EvilBaoArriveMgr.getInstance().addFinishCount(player, chargeContentPojo.getMoney() / 100);
				registerBehavior(player);
				BILogMgr.getInstance().logPayFinish(player, chargeContentPojo, vipBefore, target, entranceId);

				ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
				if (!chargeInfo.isContainsId(target.getId())) {
					chargeInfo.addChargeCfgId(target.getId());
					ChargeInfoHolder.getInstance().update(player);
				}

				player.SendMsg(Command.MSG_CHARGE_NOTIFY, null, ByteString.EMPTY); // 充值成功通知
				GameLog.error("chargemgr", "sdk-充值", "充值成功;  " + chargeContentPojo.getMoney() + "分" + ",充值类型 =" + target.getChargeType() + " 订单号 =" + chargeContentPojo.getCpTradeNo());
			} else {
				GameLog.error("chargemgr", "sdk-充值", "充值失败,商品价值;  " + chargeContentPojo.getMoney() + "元" + ",充值类型 =" + target.getChargeType() + " 商品id =" + chargeContentPojo.getItemId() + " 订单号 ="
						+ chargeContentPojo.getCpTradeNo());
			}
		} else {
			GameLog.error("chargemgr", "sdk-充值", "充值失败,未找到商品  ； 商品id =" + chargeContentPojo.getItemId() + " 订单号 =" + chargeContentPojo.getCpTradeNo());
		}
		return true;
	}

	private void registerBehavior(Player player){
	    MsgDef.Command command = MsgDef.Command.MSG_CHARGE;
	    ChargeServiceProto.ChargeServiceCommonReqMsg.Builder req = ChargeServiceProto.ChargeServiceCommonReqMsg.newBuilder();
	    req.setReqType(ChargeServiceProto.RequestType.Charge);
	    ProtocolMessageEnum type = req.getReqType();
	    String value = String.valueOf(type.getNumber());
	    GameBehaviorMgr.getInstance().registerBehavior(player, command, type, value, 0);
	}
	
	public ChargeResult charge(Player player, String itemId){
		
		ChargeResult result = ChargeResult.newResult(false);
		
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(itemId);
		if(target!=null){
			boolean success = doCharge(player, target);
			result.setSuccess(success);
			result.setTips("充值成功.");
		}else{
			result.setTips("充值类型错误.");
		}
		return result;
	}
	
	private int processFirstChargeReward(Player player, ChargeInfo chargeInfo, int addGold) {
		if (!chargeInfo.isFirstAwardTaken() && chargeInfo.getCount() > 0) {
			chargeInfo.setFirstAwardTaken(true);

			FirstChargeCfg cfg = FirstChargeCfgDao.getInstance().getAllCfg().get(0);

			// 首充的额外钻石奖励
			int addgoldfirstcharge = addGold * cfg.getAwardTimes();
			if (addgoldfirstcharge > cfg.getAwardMax()) {
				addgoldfirstcharge = cfg.getAwardMax();
			}
			if (addgoldfirstcharge > 0) {
				player.getUserGameDataMgr().addGold(addgoldfirstcharge);
			}

			// 首充礼包
			ComGiftMgr.getInstance().addGiftById(player, FirstChargeCfgDao.getInstance().getAllCfg().get(0).getReward());
			return cfg.getCfgId();
		}
		return 0;
	}

	private boolean doCharge(Player player, ChargeCfg target) {

		int addGold = target.getGoldCount();
		int money = target.getMoneyCount();
		
		
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		chargeInfo.addTotalChargeGold(addGold).addTotalChargeMoney(money).addCount(1);
		player.getUserGameDataMgr().addReCharge(addGold);
		chargeInfo.setLastChargeTime(System.currentTimeMillis());
		chargeInfo.setLastCharge(money);
		
		//派发限购的钻石奖励
		if (target.getGiveCount() > 0) {
//			ChargeInfoSubRecording sublist = null;
			boolean isExtraGive = true;
//			boolean isHasRecording = false;
			for (ChargeInfoSubRecording sub : chargeInfo.getPayTimesList()) {
				if (StringUtils.equals(target.getId(), sub.getId())) {// 有该道具的购买记录
//					isHasRecording = true;
					if (target.getGiveCount() <= sub.getCount()) {// 还有多余的限购次数
//						isExtraGive = true;
//						sublist = sub;
						isExtraGive = false;
						break;
					}
				}
			}
//			if (!isHasRecording) {// 可限购且没记录
//				isExtraGive = true;
//			}
			if (isExtraGive) {
//				if (sublist != null) {// 只有限购次数大于1才会有数据而进入；；
//
//				} else {
//					ChargeInfoSubRecording sub = ChargeCfgDao.getInstance().newSubItem(target.getId());
//					sub.setCount(sub.getCount() + 1);
//					chargeInfo.getPayTimesList().add(sub);
//					player.getUserGameDataMgr().addGold(target.getExtraGive());// 派出额外的钻石
//				}
				ChargeInfoSubRecording sub = ChargeCfgDao.getInstance().newSubItem(target.getId());
				sub.setCount(sub.getCount() + 1);
				chargeInfo.getPayTimesList().add(sub);
				player.getUserGameDataMgr().addGold(target.getExtraGive());// 派出额外的钻石
			}
		}
		
		
		
//		//派发首冲的额外奖励-------------------------------
//		if (chargeInfo.getCount() == 1) {
//			FirstChargeCfg cfg = FirstChargeCfgDao.getInstance().getAllCfg().get(0);
//			int addgoldfirstcharge = addGold * cfg.getAwardTimes() < cfg.getAwardMax() ? addGold * cfg.getAwardTimes() : cfg.getAwardMax();
//			player.getUserGameDataMgr().addGold(addgoldfirstcharge);
//		}
		
		player.getTaskMgr().AddTaskTimes(eTaskFinishDef.Recharge);
		
		//升级vip，如果达到条件
		List<Integer> presentVipLvList = upgradeVip(player, chargeInfo, false);
		// 设置界面更新vip
		player.getSettingMgr().checkOpen();
		
		int presentFirstCfgId = 0;
		if (chargeInfo.getCount() == 1) {
			presentFirstCfgId = this.processFirstChargeReward(player, chargeInfo, addGold);
		}
		
		if (presentVipLvList.size() > 0 || presentFirstCfgId > 0) {
			VIPGiftNotify.Builder builder = VIPGiftNotify.newBuilder();
			if (presentVipLvList.size() > 0) {
				builder.addAllVipLv(presentVipLvList);
			}
			if (presentFirstCfgId > 0) {
				builder.setFirstChargeGiftId(presentFirstCfgId);
			}
			player.SendMsg(Command.MSG_VIP_GIFT_NOTIFY, builder.build().toByteString());
		}
		
		ChargeInfoHolder.getInstance().update(player);
		
		return true;
		
	}

	private List<Integer> upgradeVip(Player player, ChargeInfo chargeInfo, boolean sendMsg) {
		int totalChargeGold = chargeInfo.getTotalChargeGold();
		PrivilegeCfgDAO privilegeCfgDAO = PrivilegeCfgDAO.getInstance();
		PrivilegeCfg cfg = privilegeCfgDAO.getCfg(player.getVip() + 1);
		if (cfg == null) {
			return Collections.emptyList();
		}
		int preVip = player.getVip();
		while (cfg.getRechargeCount() <= totalChargeGold) {
			player.AddVip(1);
			cfg = privilegeCfgDAO.getCfg(player.getVip() + 1); // 获取下一级的cfg
			if (cfg == null) {
				// 没有下一级的cfg，已到达VIP上限
				break;
			}
		}
		if (preVip != player.getVip()) {
			// 新添加的直接发送VIP等级礼包
			List<Integer> list = presentVipGift(player, preVip);
			if (sendMsg) {
				player.SendMsg(Command.MSG_VIP_GIFT_NOTIFY, VipProtos.VIPGiftNotify.newBuilder().addAllVipLv(list).build().toByteString());
			}
			return list;
		}
		return Collections.emptyList();
	}
	
	private Map<String, Integer> getVipGiftContent(int begin, int end) {
		if (begin + 1 == end) {
			ComGiftCfg comGiftCfg = ComGiftCfgDAO.getInstance().getCfgById(VipGiftCfgDao.getInstance().getByVip(begin).getGift());
			return new HashMap<String, Integer>(comGiftCfg.getGiftMap());
		} else {
			VipGiftCfgDao vipGiftCfgDAO = VipGiftCfgDao.getInstance();
			ComGiftCfgDAO comGiftCfgDAO = ComGiftCfgDAO.getInstance();
			VipGiftCfg giftCfg;
			ComGiftCfg comGiftCfg;
			Map<String, Integer> map = new HashMap<String, Integer>();
			Map<String, Integer> giftMap;
			for (int now = begin; now < end; now++) {
				giftCfg = vipGiftCfgDAO.getByVip(now);
				comGiftCfg = comGiftCfgDAO.getCfgById(giftCfg.getGift());
				giftMap = comGiftCfg.getGiftMap();
				for (Iterator<String> keyItr = giftMap.keySet().iterator(); keyItr.hasNext();) {
					String key = keyItr.next();
					Integer nowValue = map.get(key);
					Integer giftValue = giftMap.get(key);
					if (nowValue == null) {
						nowValue = giftValue;
					} else {
						nowValue += giftValue;
					}
					map.put(key, nowValue);
				}
			}
			return map;
		}
	}
	
	// 赠送VIP礼包
	private List<Integer> presentVipGift(Player player, int preVip) {
		int nowVip = player.getVip();
		int end = nowVip + 1;
		int begin = preVip + 1;
		Map<String, Integer> map = this.getVipGiftContent(begin, end);
		List<ItemInfo> itemList = new ArrayList<ItemInfo>(map.size());
		String strItemId;
		for (Iterator<String> keyItr = map.keySet().iterator(); keyItr.hasNext();) {
			strItemId = keyItr.next();
			itemList.add(new ItemInfo(Integer.parseInt(strItemId), map.get(strItemId).intValue()));
		}
		List<Integer> list = new ArrayList<Integer>(end - begin);
		if (player.getItemBagMgr().addItem(itemList)) {
			VipMgr vipMgr = player.getVipMgr();
			for (int i = begin; i < end; i++) {
				vipMgr.setVipGiftTaken(i);
				list.add(i);
			}
		}
		return list;
	}

	public ChargeResult gerRewardForFirstPay(Player player) {
		ChargeResult result = ChargeResult.newResult(false);
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		if (chargeInfo != null) {
			if (!chargeInfo.isFirstAwardTaken() && chargeInfo.getCount() > 0) {
				chargeInfo.setFirstAwardTaken(true);
				ComGiftMgr.getInstance().addGiftById(player, FirstChargeCfgDao.getInstance().getAllCfg().get(0).getReward());

				ChargeInfoHolder.getInstance().update(player);
				result.setSuccess(true);
			} else {
				result.setTips("数据异常 没有首冲奖励");
			}
		}
		return result;
	}
	
	public ChargeResult buyMonthCardByGm(Player player, String chargeItemId) {
		ChargeResult result = ChargeResult.newResult(false);
		result.setTips("配置表异常");
		
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(chargeItemId);

		if (target != null) {
			List<ActivityTimeCardTypeSubCfg> timeCardList = ActivityTimeCardTypeSubCfgDAO
					.getInstance().getAllCfg();
			for (ActivityTimeCardTypeSubCfg timecardcfg : timeCardList) {
				if (timecardcfg.getChargeType() == target.getChargeType()) {
					result = buyMonthCard(player, timecardcfg.getId(),target);
					break;
				}
			}
		}else{
			result.setSuccess(false);
			result.setTips("没这个商品");
		}
		
		return result;
	}

	// 发送月卡给指定的玩家
	private IReadOnlyPair<ChargeResult, ActivityTimeCardTypeSubItem> sendMonthCardToTarget(Player targetPlayer, String timeCardSubCfgId) {
		ChargeResult result = ChargeResult.newResult(false);
		ActivityTimeCardTypeItemHolder dataHolder = ActivityTimeCardTypeItemHolder.getInstance();
		ActivityTimeCardTypeItem dataItem = dataHolder.getItem(targetPlayer.getUserId());
		if (dataItem == null) {// 首次读取创建记录
			dataItem = ActivityTimeCardTypeCfgDAO.getInstance().newItem(targetPlayer);
			if (dataItem != null) {
				dataHolder.addItem(targetPlayer, dataItem);
			}
		}

		List<ActivityTimeCardTypeSubItem> monthCardList = dataItem.getSubItemList();
		ActivityTimeCardTypeSubItem targetItem = null;
		ChargeTypeEnum cardtypenume = ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId).getChargeType();

		String cardtype = cardtypenume.getCfgId();
		for (ActivityTimeCardTypeSubItem itemTmp : monthCardList) {
			if (StringUtils.equals(itemTmp.getChargetype(), cardtype)) {
				targetItem = itemTmp;
				break;
			}
		}

		if (targetItem == null) {// newitem已添加list，不会null
			GameLog.error("chargemgr", "买月卡", "chargeMgr.list里没有该项月卡类型！！" + targetPlayer);
			result.setTips("购买月卡异常");
		} else {
			int tempdayleft = targetItem.getDayLeft();
//			targetItem.setDayLeft(targetItem.getDayLeft() + ActivityTimeCardTypeSubCfgDAO.getInstance().getBynume(cardtypenume).getDays());
			if (cardtypenume == ChargeTypeEnum.VipMonthCard) {
				if(targetItem.getDayLeft() > 0){
					result.setSuccess(false);
					result.setTips("已经拥有至尊月卡，不能再购买");
					return Pair.Create(result, null);
				}else{
					targetItem.setDayLeft(Short.MAX_VALUE * 2); // 至尊月卡，现在是终身的，所以这里设置一个很长的剩余天数
				}
			} else {
				targetItem.setDayLeft(targetItem.getDayLeft() + ActivityTimeCardTypeSubCfgDAO.getInstance().getBynume(cardtypenume).getDays());
			}
			dataHolder.updateItem(targetPlayer, dataItem);
			result.setSuccess(true);
			
			GameLog.info("月卡", targetPlayer.getUserId(), "日常任务刷新", null);
			DailyActivityHandler.getInstance().sendTaskList(targetPlayer);

			if (tempdayleft < ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId).getDaysLimit()) {
				result.setTips("购买月卡成功");
			} else {
				result.setTips("剩余日期超过5天但依然冲了钱。。。");
				GameLog.error("chargemgr", "买月卡", "没到期也能付费,玩家名 =" + targetPlayer.getUserName() + " 月卡cfgid =" + timeCardSubCfgId);
			}
		}
		return Pair.Create(result, targetItem);
	}
	
	/**
	 * 充值完成，更新vip信息
	 * @param player
	 * @param target
	 */
	private void addVipExp(Player player, ChargeCfg target){
		int addGold = target.getVipExp();
		int money = target.getMoneyCount();
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		chargeInfo.addTotalChargeGold(addGold).addTotalChargeMoney(money).addCount(1);
		ChargeInfoHolder.getInstance().update(player);
		// 升级vip，如果达到条件
		upgradeVip(player, chargeInfo, true);
		// 设置界面更新vip
		player.getSettingMgr().checkOpen();
	}
	
	/**
	 * 赠送月卡
	 * @param friendPlayer
	 * @param selfPlayer
	 * @param timeCardSubCfgId
	 * @param target
	 * @return
	 */
	public ChargeResult sendMonthCard(Player friendPlayer, Player selfPlayer, String timeCardSubCfgId, ChargeCfg target) {
		IReadOnlyPair<ChargeResult, ActivityTimeCardTypeSubItem> pairResult = this.sendMonthCardToTarget(friendPlayer, timeCardSubCfgId);
		if(!pairResult.getT1().isSuccess()){
			//至尊月卡赠送失败的处理逻辑
			sendMonthCardFailHandler(selfPlayer);
		}
		ChargeResult result = pairResult.getT1();
		if (result.isSuccess()) {
			String orderStr = pairResult.getT2().getChargetype();
			try {
				if (StringUtils.isNotBlank(orderStr)) {
					int order = Integer.parseInt(orderStr);
					ChargeTypeEnum[] values = ChargeTypeEnum.values();
					if (0 <= order && order < values.length) {
						ChargeTypeEnum type = values[order];
						MonthCardPrivilegeMgr.getShareInstance().signalMonthCardChange(friendPlayer, type, true);
						sendMonthCardSuccessHandler(selfPlayer, friendPlayer.getUserId(), timeCardSubCfgId);
					}
				}
			} catch (Exception e) {
				GameLog.info("特权", friendPlayer.getUserId(), "无法获取充值类型:" + orderStr, e);
			}
		}
		addVipExp(selfPlayer, target);
		return result;
	}
	
	/**
	 * 购买月卡
	 * @param player
	 * @param timeCardSubCfgId
	 * @param target
	 * @return
	 */
	public ChargeResult buyMonthCard(Player player, String timeCardSubCfgId, ChargeCfg target) {
//		UserEventMgr.getInstance().charge(player, 30);// 模拟充值的充值活动传入，测试用，正式服需注释
		IReadOnlyPair<ChargeResult, ActivityTimeCardTypeSubItem> pairResult = this.sendMonthCardToTarget(player, timeCardSubCfgId);
		ChargeResult result = pairResult.getT1();
		if (result.isSuccess()) {
			String orderStr = pairResult.getT2().getChargetype();
			try {
				if (StringUtils.isNotBlank(orderStr)) {
					int order = Integer.parseInt(orderStr);
					ChargeTypeEnum[] values = ChargeTypeEnum.values();
					if (0 <= order && order < values.length) {
						ChargeTypeEnum type = values[order];
						MonthCardPrivilegeMgr.getShareInstance().signalMonthCardChange(player, type, true);
					}
				}
			} catch (Exception e) {
				GameLog.info("特权", player.getUserId(), "无法获取充值类型:" + orderStr, e);
			}
			addVipExp(player, target);
		}
		return result;
	}
	
	/**
	 * 赠送月卡失败的处理
	 * @param player
	 */
	public void sendMonthCardFailHandler(Player player){
		EmailUtils.sendEmail(player.getUserId(), SEND_MONTH_CARD_FAIL_EMAIL_ID);
	}
	
	/**
	 * 赠送月卡成功
	 * @param player
	 */
	public void sendMonthCardSuccessHandler(Player player, String friendId, String timeCardSubCfgId){
		EmailCfg emailCfg = EmailCfgDAO.getInstance().getCfgById(SEND_MONTH_CARD_SUCCESS_EMAIL_ID);
		String sendTime = DateUtils.getDateTimeFormatString("yyyy年MM月dd日 HH:mm:ss");
		
		ActivityTimeCardTypeSubCfg cardCfg = ActivityTimeCardTypeSubCfgDAO.getInstance().getById(timeCardSubCfgId);
		ChargeTypeEnum cardEnum = cardCfg.getChargeType();
		
		String content = String.format(emailCfg.getContent(), player.getUserName(), sendTime, cardEnum.getName(), cardCfg.getGold());
		EmailUtils.sendEmail(friendId, SEND_MONTH_CARD_SUCCESS_EMAIL_ID, "", content);
	}
}
