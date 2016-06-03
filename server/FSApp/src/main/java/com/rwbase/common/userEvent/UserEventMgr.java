package com.rwbase.common.userEvent;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventAdvanceDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventArenaDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventAttachDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventBattleTowerDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventChargeDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventCoinSpendDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventGambleGoldDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventGoldSpendDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventLoginDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventTreasureLandDailyHandler;
import com.rwbase.common.userEvent.dailyEventHandler.UserEventUpGradeStarDailyHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventBattleTowerHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventCopyWinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventEliteCopyWinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGambleCoinHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventGambleGoldHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventLoginHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventUseGoldHandler;
import com.rwproto.BattleTowerServiceProtos.EKeyType;

public class UserEventMgr {
	private static final String Player = null;


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
		//------------------------------我是淫荡的分割线！！！通用2↑，活跃之王↓
		eventHandlerMap.put(UserEventType.GoldSpendingKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.GivePowerKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.TreasureLandKingAttive, new UserEventLoginHandler());
		
		eventHandlerMap.put(UserEventType.TowerKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.BattleTowerKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.AttachKingAttive, new UserEventLoginHandler());
		
		eventHandlerMap.put(UserEventType.ResetElityKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.HeroUpgradeKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.WarfareDifficultyTwoKingAttive, new UserEventLoginHandler());
		
		eventHandlerMap.put(UserEventType.BuyInTowerShopKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.BuyPowerKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.FactionDonateKingAttive, new UserEventLoginHandler());
		
		eventHandlerMap.put(UserEventType.UseSweepTicketKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.LearnSkillInfactionKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.StrengthenMagicKingAttive, new UserEventLoginHandler());
		
		eventHandlerMap.put(UserEventType.UseSilverKeyKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.GambleGoldKingAttive, new UserEventLoginHandler());
		eventHandlerMap.put(UserEventType.arenaKingAttive, new UserEventLoginHandler());		
	}
	
	/*传入登陆时间*/
	public void RoleLogin(Player player, long lastLoginTime) {
		
		UserEvent userEvent = new UserEvent(UserEventType.LOGIN, lastLoginTime);
		raiseEvent(player, userEvent);		
		RoleLoginDaily(player, lastLoginTime);
	}
	
	/*传入充值额度*/
	public void charge(Player player ,int chargevalue){
		UserEvent userEvent = new UserEvent(UserEventType.CHARGE, chargevalue);
		raiseEvent(player, userEvent);		
		chargeDaily(player ,chargevalue);
	}
	
	
	
	
	/*传入消耗钻石数*/
	public void UseGold(Player player, int GoldSpending) {		
		UserEvent userEvent = new UserEvent(UserEventType.USE_GOLD, GoldSpending);
		raiseEvent(player, userEvent);
		goldSpendDaily(player, GoldSpending);
		goldSpendKingActive(player, GoldSpending);
	}
	
	

	/*传入通关副本数*/
	public void CopyWin(Player player, int winnum) {		
		UserEvent userEvent = new UserEvent(UserEventType.COPY_WIN, winnum);
		raiseEvent(player, userEvent);
	}
	
	/*传入通关精英本数*/
	public void ElityCopyWin(Player player, int winnum) {		
		UserEvent userEvent = new UserEvent(UserEventType.ELITE_COPY_WIN, winnum);
		raiseEvent(player, userEvent);
	}
	
	/*传入封神台当前达到层数*/
	public void BattleTower(Player player, int winnum) {		
		UserEvent userEvent = new UserEvent(UserEventType.BATTLETOWER, winnum);
		raiseEvent(player, userEvent);		
		battleTowerDaily(player, winnum);		
		battleTowerKingActive(player, winnum);
	}
	
	

	/**此处传入的是钓鱼获得的经验丹count数级货币类型*/
	public void Gamble(Player player, int count , eSpecialItemId moneyType) {
		if(moneyType ==eSpecialItemId.Coin){
			GambleCoin(player,count);
		}else if(moneyType == eSpecialItemId.Gold){
			GambleGold(player, count);
		}
	}
	
	/**钓鱼行为分类-金币*/
	public void GambleCoin(Player player, int count ) {
		
		UserEvent userEvent = new UserEvent(UserEventType.GAMBLE_COIN, count);
		raiseEvent(player, userEvent);
	}
	
	/**钓鱼行为分类-钻石*/
	public void GambleGold(Player player, int count) {
			UserEvent userEvent = new UserEvent(UserEventType.GAMBLE_GOLD, count);
			raiseEvent(player, userEvent);
			GambleGoldDaily(player, count);
			GambleGoldKingActive(player, count);
	}
	
	

	/**登陆行为分类-每日*/
	private void RoleLoginDaily(Player player, long lastLoginTime) {
		UserEvent userEventOther = new UserEvent(UserEventType.LOGINDAILY, lastLoginTime);
		raiseEvent(player, userEventOther);
	}
	
	
	/*传入聚宝之地通关次数*/
	public void TreasureLandCopyWinDaily(Player player, int winnum) {		
		UserEvent userEvent = new UserEvent(UserEventType.TREASURELANDDAILY, winnum);
		raiseEvent(player, userEvent);		
		TreasureLandCopyWinKingActive(player,winnum);
		
	}
	
	
	

	/*钻石钓鱼分类-每日*/
	private void GambleGoldDaily(Player player, int count) {
		UserEvent userEvent = new UserEvent(UserEventType.GAMBLEGOLDDAILY, count);
		raiseEvent(player, userEvent);		
	}
	
	/*传入升级次数*/
	public void UpGradeStarDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.UPGRADESTARDAILY, count);
		raiseEvent(player, userEvent);		
	}
	
	/*传入进阶次数*/
	public void advanceDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.ADVANCEDAILY, count);
		raiseEvent(player, userEvent);		
	}
	
	/**封神台行为分类-每日*/
	private void battleTowerDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.BATTLETOWERDAILY, count);
		raiseEvent(player, userEvent);
	}
	
	/**付费行为分类-每日*/
	private void chargeDaily(com.playerdata.Player player2, int chargevalue) {
		UserEvent userEvent = new UserEvent(UserEventType.CHARGEDAILY, chargevalue);
		raiseEvent(player2, userEvent);		
	}
	
	/*传入获得的积分数*/
	public void ArenaDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.ARENADAILY, count);
		raiseEvent(player, userEvent);
		arenaKingActive(player,1);
	}
	
	

	/*传入金币消耗数*/
	public void coinSpendDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.COINSPENDDAILY, count);
		raiseEvent(player, userEvent);
	}
	
	/*传入附灵升的级数*/
	public void attachDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.ATTACHDAILY, 1);
		raiseEvent(player, userEvent);
		attachKingActive(player, count);
	}
	
	

	/**消费行为分类-每日*/
	private void goldSpendDaily(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.GOLDSPENDDAILY, count);
		raiseEvent(player, userEvent);
	}
	
	/**消费行为分类-活跃之王*/
	private void goldSpendKingActive(com.playerdata.Player player,
			int goldSpending) {
		UserEvent userEvent = new UserEvent(UserEventType.GoldSpendingKingAttive, goldSpending);
		raiseEvent(player, userEvent);		
	}
	
	/*传入赠送体力次数*/
	public void givePowerKingActive(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.GivePowerKingAttive, count);
		raiseEvent(player, userEvent);
	}
	
	/**聚宝之地行为分类-活跃之王*/
	private void TreasureLandCopyWinKingActive(Player player,
			int count) {
		UserEvent userEvent = new UserEvent(UserEventType.TreasureLandKingAttive, count);
		raiseEvent(player, userEvent);		
	}
	
	/**传入万仙阵获得徽记数*/
	public void TowerKingActive(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.TowerKingAttive, count);
		raiseEvent(player, userEvent);			
	}
	
	/**封神台行为分类-活跃之王*/
	private void battleTowerKingActive(Player player, int count) {
		UserEvent userEvent = new UserEvent(UserEventType.BattleTowerKingAttive, count);
		raiseEvent(player, userEvent);			
	}
	
	
	/**附灵行为分类-活跃之王*/
	private void attachKingActive(Player player, int count) {
		UserEvent userEvent = new UserEvent(UserEventType.AttachKingAttive, count);
		raiseEvent(player, userEvent);		
	}
	
	/**传入重置精英本次数*/
	public void ResetElityKingActive(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.ResetElityKingAttive, count);
		raiseEvent(player, userEvent);			
	}
	
	/**传入英雄当前级数*/
	public void heroUpGradeKingActive(Player player,int count){
		UserEvent userEvent = new UserEvent(UserEventType.HeroUpgradeKingAttive, count);
		raiseEvent(player, userEvent);			
	}
	
	/**传入无尽战火的关卡id及小怪波数*/
	public void warFareDifficultyTwoKingActive(Player player,int levelId ,int count){
		
		int[] ints = {levelId,count};
		UserEvent userEvent = new UserEvent(UserEventType.WarfareDifficultyTwoKingAttive, ints);
		raiseEvent(player, userEvent);
	}
	
	/**传入仙阵商店购买次数*/
	public void buyInTowerShopKingActive(Player player ,int count){
		UserEvent userEvent = new UserEvent(UserEventType.BuyInTowerShopKingAttive, count);
		raiseEvent(player, userEvent);
	}
	
	/**传入购买体力次数*/
	public void buyPowerKingActive(Player player ,int count){
		UserEvent userEvent = new UserEvent(UserEventType.BuyPowerKingAttive, count);
		raiseEvent(player, userEvent);
	}
	
	/**传入捐献次数*/
	public void factionDonateKingAttive(Player player ,int count){
		UserEvent userEvent = new UserEvent(UserEventType.FactionDonateKingAttive, count);
		raiseEvent(player, userEvent);
	}
	
	/**传入使用扫荡券数*/
	public void UseSweepTicketKingAttive(Player player ,int count){
		UserEvent userEvent = new UserEvent(UserEventType.UseSweepTicketKingAttive, count);
		raiseEvent(player, userEvent);
	}
	
	/**传入帮派学习技能次数*/
	public void LearnSkillInfactionKingAttive(Player player ,int count){
		UserEvent userEvent = new UserEvent(UserEventType.LearnSkillInfactionKingAttive, count);
		raiseEvent(player, userEvent);
	}
	
	/**传入法宝当前级别数*/
	public void StrengthenMagicKingAttive(Player player ,int count){
		UserEvent userEvent = new UserEvent(UserEventType.StrengthenMagicKingAttive, count);
		raiseEvent(player, userEvent);
	}
	
	/**传入使用钥匙类型和个数*/
	public void UseSilverKeyKingAttive(Player player ,EKeyType type,int count){
		if(type == EKeyType.KEY_SILVER){
			UserEvent userEvent = new UserEvent(UserEventType.UseSilverKeyKingAttive, count);
			raiseEvent(player, userEvent);
		}
	}
	
	/**钓鱼行为分类-钻石钓鱼活跃之王*/
	private void GambleGoldKingActive(Player player, int count) {
		UserEvent userEvent = new UserEvent(UserEventType.GambleGoldKingAttive, count);
		raiseEvent(player, userEvent);
		
	}
	
	/**竞技行为分类-活跃之王*/
	private void arenaKingActive(Player player, int count) {
		UserEvent userEvent = new UserEvent(UserEventType.AttachKingAttive, count);
		raiseEvent(player, userEvent);
		
	}
	
	
	public void raiseEvent(Player player, UserEvent userEvent){
		IUserEventHandler eventHandler = eventHandlerMap.get(userEvent.getEventType());
		if(eventHandler!=null){
			eventHandler.doEvent(player, userEvent.getParam());
		
		}
	}
	
	
}
