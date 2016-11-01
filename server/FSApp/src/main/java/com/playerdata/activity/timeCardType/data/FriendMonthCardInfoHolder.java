package com.playerdata.activity.timeCardType.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfg;
import com.playerdata.activity.timeCardType.cfg.ActivityTimeCardTypeSubCfgDAO;
import com.playerdata.charge.cfg.ChargeCfg;
import com.playerdata.charge.cfg.ChargeCfgDao;
import com.playerdata.charge.cfg.ChargeTypeEnum;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.FriendServiceProtos.FriendInfo;

public class FriendMonthCardInfoHolder{
	
	private static FriendMonthCardInfoHolder instance = new FriendMonthCardInfoHolder();
	
	public static FriendMonthCardInfoHolder getInstance(){
		return instance;
	}

	final private eSynType synType = eSynType.FriendMonthCardInfo;
	
	public void synAllFriendData(Player player){
		List<FriendMonthCardInfo> synList = new ArrayList<FriendMonthCardInfo>();
		ActivityTimeCardTypeItemHolder activityTimecardHolder = ActivityTimeCardTypeItemHolder.getInstance();
		ActivityTimeCardTypeSubCfg timeCardNormalSubCfg = ActivityTimeCardTypeSubCfgDAO.getInstance().getBynume(ChargeTypeEnum.MonthCard);
		int normalCardLimit = timeCardNormalSubCfg.getDaysLimit();	
		
		List<FriendInfo> friendList = player.getFriendMgr().getFriendList();
		for(FriendInfo friend : friendList){	
			FriendMonthCardInfo cardInfo = friendItemToTimeCard(friend.getUserId(), activityTimecardHolder, normalCardLimit);
			if(null != cardInfo){
				synList.add(cardInfo);
			}
		}
		if(!synList.isEmpty()){
			ClientDataSynMgr.synDataList(player, synList, synType, eSynOpType.UPDATE_LIST);
		}
	}

	private FriendMonthCardInfo friendItemToTimeCard(String friendId, ActivityTimeCardTypeItemHolder activityTimecardHolder, int normalCardLimit) {
		ActivityTimeCardTypeItem dataItem = activityTimecardHolder.getItem(friendId);
		if(dataItem == null){
			return null;
		}
		List<ActivityTimeCardTypeSubItem> monthCardList = dataItem.getSubItemList();
		boolean isMonthCardMax = false;
		boolean isEternalCardMax = false;
		for(ActivityTimeCardTypeSubItem subItem : monthCardList){
			ChargeTypeEnum type = ChargeTypeEnum.getById(String.valueOf(subItem.getChargetype()));
			if(type == ChargeTypeEnum.MonthCard) {
				isMonthCardMax = subItem.getDayLeft() > normalCardLimit ? true : false;
			}else if(type == ChargeTypeEnum.VipMonthCard && subItem.getDayLeft() > 0){
				isEternalCardMax = true;
			}
		}
		if(isMonthCardMax || isEternalCardMax){
			FriendMonthCardInfo info = new FriendMonthCardInfo();
			info.setId(friendId);
			info.setMonthCardMax(isMonthCardMax);
			info.setEternalCardMax(isEternalCardMax);
			return info;
		}
		return null;
	}
	
	public boolean canSendMonthCard(String friendId, String chargeItemId){
		ActivityTimeCardTypeItem dataItem = ActivityTimeCardTypeItemHolder.getInstance().getItem(friendId);
		if(dataItem == null){
			return false;
		}
		ChargeCfg target = ChargeCfgDao.getInstance().getConfig(chargeItemId);

		ActivityTimeCardTypeSubCfg timeCardNormalSubCfg = ActivityTimeCardTypeSubCfgDAO.getInstance().getBynume(ChargeTypeEnum.MonthCard);
		int normalCardLimit = timeCardNormalSubCfg.getDaysLimit();	
		
		FriendMonthCardInfo cardInfo = friendItemToTimeCard(friendId, ActivityTimeCardTypeItemHolder.getInstance(), normalCardLimit);
		if(null == cardInfo){
			return true;
		}
		switch (target.getChargeType()) {
		case MonthCard:
			return !cardInfo.isMonthCardMax();
		case VipMonthCard:
			return !cardInfo.isEternalCardMax();
		default:
			return false;
		}
	}
}
