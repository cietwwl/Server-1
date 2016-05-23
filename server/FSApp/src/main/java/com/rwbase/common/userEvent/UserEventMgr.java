package com.rwbase.common.userEvent;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.eventHandler.UserEventAdvanceDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventArenaDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventAttachDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventBattleTowerDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventBattleTowerHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventChargeDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventCoinSpendDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventCopyWinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventEliteCopyWinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGambleCoinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGambleGoldDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGambleGoldHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGoldSpendDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventLoginDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventLoginHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventTreasureLandDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventUpGradeStarDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventUseGoldHandler;

public class UserEventMgr {
	private static UserEventMgr instance = new UserEventMgr();
	
	
	private Map<UserEventType,IUserEventHandler> eventHandlerMap = new HashMap<UserEventType,IUserEventHandler>();
	
	public static UserEventMgr getInstance() {
		return instance;
	}
	
	private UserEventMgr(){
		eventHandlerMap.put(UserEventType.LOGIN, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.USE_GOLD, new UserEventUseGoldHandler());
		eventHandlerMap.put(UserEventType.COPY_WIN, new UserEventCopyWinHandler());
		eventHandlerMap.put(UserEventType.ELITE_COPY_WIN, new UserEventEliteCopyWinHandler());
		eventHandlerMap.put(UserEventType.BATTLETOWER, new UserEventBattleTowerHandler());
		eventHandlerMap.put(UserEventType.GAMBLE_COIN, new UserEventGambleCoinHandler());
		eventHandlerMap.put(UserEventType.GAMBLE_GOLD, new UserEventGambleGoldHandler());
		//------------------------------我是淫荡的分割线！！！通用1↑，通用2↓
		eventHandlerMap.put(UserEventType.LOGINDAILY, new UserEventLoginDailyHandler());
		eventHandlerMap.put(UserEventType.TREASURELANDDAILY, new UserEventTreasureLandDailyHandler());
		
		eventHandlerMap.put(UserEventType.UPGRADESTARDAILY, new UserEventUpGradeStarDailyHandler());
		eventHandlerMap.put(UserEventType.ADVANCEDAILY, new UserEventAdvanceDailyHandler());
		eventHandlerMap.put(UserEventType.BATTLETOWERDAILY, new UserEventBattleTowerDailyHandler());
		
		eventHandlerMap.put(UserEventType.ARENADAILY, new UserEventArenaDailyHandler());
		eventHandlerMap.put(UserEventType.COINSPENDDAILY, new UserEventCoinSpendDailyHandler());
		eventHandlerMap.put(UserEventType.CHARGEDAILY, new UserEventChargeDailyHandler());
		
		eventHandlerMap.put(UserEventType.GAMBLEGOLDDAILY, new UserEventGambleGoldDailyHandler());
		eventHandlerMap.put(UserEventType.ATTACHDAILY, new UserEventAttachDailyHandler());
		eventHandlerMap.put(UserEventType.GOLDSPENDDAILY, new UserEventGoldSpendDailyHandler());
		
	}
	
	
	public void RoleLogin(Player player, long lastLoginTime) {
		
		UserEvent userEvent = new UserEvent(UserEventType.LOGIN, lastLoginTime);
		raiseEvent(player, userEvent);
		
		UserEvent userEventOther = new UserEvent(UserEventType.LOGINDAILY, lastLoginTime);
		raiseEvent(player, userEventOther);
	}
	
	
	
	public void UseGold(Player player, int GoldSpending) {
		
		UserEvent userEvent = new UserEvent(UserEventType.USE_GOLD, GoldSpending);
		raiseEvent(player, userEvent);
		goldSpendDaily(player, GoldSpending);
	}
	
	
	public void CopyWin(Player player, int winnum) {
		
		UserEvent userEvent = new UserEvent(UserEventType.COPY_WIN, winnum);
		raiseEvent(player, userEvent);
	}
	
	public void ElityCopyWin(Player player, int winnum) {
		
		UserEvent userEvent = new UserEvent(UserEventType.ELITE_COPY_WIN, winnum);
		raiseEvent(player, userEvent);
	}
	
	public void TreasureLandCopyWin(Player player, int winnum) {
		
		UserEvent userEvent = new UserEvent(UserEventType.TREASURELANDDAILY, winnum);
		raiseEvent(player, userEvent);
	}
	
	
	public void BattleTower(Player player, int winnum) {
		
		UserEvent userEvent = new UserEvent(UserEventType.BATTLETOWER, winnum);
		raiseEvent(player, userEvent);
		
		battleTowerDaily(player, winnum);
		
	}
	
	/**此处传入的是赌博协议里的count编号*/
	public void Gamble(Player player, int count , eSpecialItemId moneyType) {
		if(moneyType ==eSpecialItemId.Coin){
			GambleCoin(player,count);
		}else if(moneyType == eSpecialItemId.Gold){
			GambleGold(player, count);
		}
	}
	
	
	public void GambleCoin(Player player, int count ) {
		
		UserEvent userEvent = new UserEvent(UserEventType.GAMBLE_COIN, count);
		raiseEvent(player, userEvent);
	}
	
	public void GambleGold(Player player, int count) {
			UserEvent userEvent = new UserEvent(UserEventType.GAMBLE_GOLD, count);
			raiseEvent(player, userEvent);
			GambleGoldDaily(player, count);
			
	}
	
	public void GambleGoldDaily(Player player, int count) {
		UserEvent userEvent = new UserEvent(UserEventType.GAMBLEGOLDDAILY, count);
		raiseEvent(player, userEvent);
		
		
}
	
	public void UpGradeStarDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.UPGRADESTARDAILY, count);
		raiseEvent(player, userEvent);		
	}
	
	public void advanceDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.ADVANCEDAILY, count);
		raiseEvent(player, userEvent);		
	}
	
	public void battleTowerDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.BATTLETOWERDAILY, count);
		raiseEvent(player, userEvent);
	}
	
	public void ArenaDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.ARENADAILY, count);
		raiseEvent(player, userEvent);
	}
	
	public void coinSpendDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.COINSPENDDAILY, count);
		raiseEvent(player, userEvent);
	}
	
	public void attachDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.ATTACHDAILY, count);
		raiseEvent(player, userEvent);
	}
	
	public void goldSpendDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.GOLDSPENDDAILY, count);
		raiseEvent(player, userEvent);
	}
	
	
	public void raiseEvent(Player player, UserEvent userEvent){
		IUserEventHandler eventHandler = eventHandlerMap.get(userEvent.getEventType());
		if(eventHandler!=null){
			eventHandler.doEvent(player, userEvent.getParam());
		}
	}
	
	
}
