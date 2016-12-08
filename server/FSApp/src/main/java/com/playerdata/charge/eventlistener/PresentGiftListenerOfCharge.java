package com.playerdata.charge.eventlistener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.playerdata.ComGiftMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.VipMgr;
import com.playerdata.charge.IChargeEventListener;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.FirstChargeCfg;
import com.playerdata.charge.cfg.FirstChargeCfgDao;
import com.playerdata.charge.cfg.VipGiftCfg;
import com.playerdata.charge.cfg.VipGiftCfgDao;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.gift.ComGiftCfg;
import com.rwbase.dao.gift.ComGiftCfgDAO;
import com.rwproto.MsgDef.Command;
import com.rwproto.VipProtos.VIPGiftNotify;

public class PresentGiftListenerOfCharge implements IChargeEventListener{

	@Override
	public void notifyCharge(Player player, ChargeCfg target, int preVipLv) {
		ChargeInfo chargeInfo = ChargeInfoHolder.getInstance().get(player.getUserId());
		List<Integer> list = Collections.emptyList();
		int presentFirstCfgId = 0;
		boolean present = false;
		if (chargeInfo.getCount() == 1) {
			// 处理首充奖励
			presentFirstCfgId = this.processFirstChargeReward(player, chargeInfo, target.getGoldCount());
			if (presentFirstCfgId > 0) {
				present = true;
			}
		}
		if (preVipLv != player.getVip()) {
			// 发送VIP等级礼包
			list = presentVipGift(player, preVipLv);
			present = true;
		}
		if (present) {
			VIPGiftNotify.Builder builder = VIPGiftNotify.newBuilder();
			if (list.size() > 0) {
				builder.addAllVipLv(list);
			}
			if (presentFirstCfgId > 0) {
				builder.setFirstChargeGiftId(presentFirstCfgId);
			}
			player.SendMsg(Command.MSG_VIP_GIFT_NOTIFY, builder.build().toByteString());
			ChargeInfoHolder.getInstance().update(player);
		}
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
		if (ItemBagMgr.getInstance().addItem(player, itemList)) {
			VipMgr vipMgr = player.getVipMgr();
			for (int i = begin; i < end; i++) {
				vipMgr.setVipGiftTaken(i);
				list.add(i);
			}
		}
		return list;
	}

	private Map<String, Integer> getVipGiftContent(int begin, int end) {
		if (begin + 1 == end) {
			ComGiftCfg comGiftCfg = ComGiftCfgDAO.getInstance().getCfgById(VipGiftCfgDao.getInstance().getByVip(begin).getGift());
			return new HashMap<String, Integer>(comGiftCfg.getGiftMap());
		} else {
			VipGiftCfgDao vipGiftCfgDAO = VipGiftCfgDao.getInstance();
			ComGiftCfgDAO comGiftCfgDAO = ComGiftCfgDAO.getInstance();
			Map<String, Integer> totalContentMap = new HashMap<String, Integer>();
			VipGiftCfg giftCfg;
			ComGiftCfg comGiftCfg;
			Map<String, Integer> giftMap;
			for (int now = begin; now < end; now++) {
				giftCfg = vipGiftCfgDAO.getByVip(now);
				comGiftCfg = comGiftCfgDAO.getCfgById(giftCfg.getGift());
				giftMap = comGiftCfg.getGiftMap();
				for (Iterator<String> keyItr = giftMap.keySet().iterator(); keyItr.hasNext();) {
					String key = keyItr.next();
					Integer nowValue = totalContentMap.get(key);
					Integer giftValue = giftMap.get(key);
					if (nowValue == null) {
						nowValue = giftValue;
					} else {
						nowValue += giftValue;
					}
					totalContentMap.put(key, nowValue);
				}
			}
			return totalContentMap;
		}
	}
}
