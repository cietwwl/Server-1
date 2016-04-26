package com.rwbase.common.userEvent;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.eventHandler.UserEventBattleTowerHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventCopyWinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventEliteCopyWinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGambleCoinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGambleGoldHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventLoginHandler;
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
		
	}
	
	
	public void RoleLogin(Player player, long lastLoginTime) {
		
		UserEvent userEvent = new UserEvent(UserEventType.LOGIN, lastLoginTime);
		raiseEvent(player, userEvent);
	}
	
	
	
	public void UseGold(Player player, int GoldSpending) {
		
		UserEvent userEvent = new UserEvent(UserEventType.USE_GOLD, GoldSpending);
		raiseEvent(player, userEvent);
	}
	
	
	public void CopyWin(Player player, int winnum) {
		
		UserEvent userEvent = new UserEvent(UserEventType.COPY_WIN, winnum);
		raiseEvent(player, userEvent);
	}
	
	public void ElityCopyWin(Player player, int winnum) {
		
		UserEvent userEvent = new UserEvent(UserEventType.ELITE_COPY_WIN, winnum);
		raiseEvent(player, userEvent);
	}
	
	public void BattleTower(Player player, int winnum) {
		
		UserEvent userEvent = new UserEvent(UserEventType.BATTLETOWER, winnum);
		raiseEvent(player, userEvent);
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
	}
	
	public void raiseEvent(Player player, UserEvent userEvent){
		IUserEventHandler eventHandler = eventHandlerMap.get(userEvent.getEventType());
		if(eventHandler!=null){
			eventHandler.doEvent(player, userEvent.getParam());
		}
	}
	
	
}
