package com.rw.actionHelper;

import com.rw.handler.RandomMethodIF;
import com.rw.handler.GroupCopy.GroupCopyHandler;
import com.rw.handler.activity.ActivityCountHandler;
import com.rw.handler.activity.daily.ActivityDailyCountHandler;
import com.rw.handler.battle.PVEHandler;
import com.rw.handler.battle.PVPHandler;
import com.rw.handler.battletower.BattleTowerHandler;
import com.rw.handler.chat.ChatHandler;
import com.rw.handler.copy.CopyHandler;
import com.rw.handler.daily.DailyHandler;
import com.rw.handler.email.EmailHandler;
import com.rw.handler.equip.EquipHandler;
import com.rw.handler.fashion.FashionHandler;
import com.rw.handler.fixEquip.FixEquipHandler;
import com.rw.handler.fresheractivity.FresherActivityHandler;
import com.rw.handler.friend.FriendHandler;
import com.rw.handler.gamble.GambleHandler;
import com.rw.handler.group.GroupBaseHandler;
import com.rw.handler.group.GroupMemberHandler;
import com.rw.handler.group.GroupPersonalHandler;
import com.rw.handler.group.GroupPrayHandler;
import com.rw.handler.groupCompetition.service.GroupCompSameSceneHandler;
import com.rw.handler.groupCompetition.service.GroupCompetitionHandler;
import com.rw.handler.groupCompetition.service.GroupCompetitionQuizHandler;
import com.rw.handler.groupsecret.GroupSecretHandler;
import com.rw.handler.groupsecret.GroupSecretMatchHandler;
import com.rw.handler.hero.HeroHandler;
import com.rw.handler.itembag.ItemBagHandler;
import com.rw.handler.magic.MagicHandler;
import com.rw.handler.magicSecret.MagicSecretHandler;
import com.rw.handler.mainService.MainHandler;
import com.rw.handler.peakArena.PeakArenaHandler;
import com.rw.handler.sevenDayGift.DailyGiftHandler;
import com.rw.handler.sign.SignHandler;
import com.rw.handler.store.StoreHandler;
import com.rw.handler.taoist.TaoistHandler;
import com.rw.handler.task.TaskHandler;
import com.rw.handler.teamBattle.service.TeamBattleHandler;
import com.rw.handler.worShip.WorShipHandler;
import com.rw.handler.career.SelectCareerHandler;;

public enum ActionEnum {
	TeamBattle(0, TeamBattleHandler.getInstance()),
	PVP(1, PVPHandler.instance()),
	PVE(2, PVEHandler.instance(), 6000),
	ActivityDailyCount(3, ActivityDailyCountHandler.getHandler()),
	Chat(4, ChatHandler.instance()),
	Gamble(5, GambleHandler.instance()),
	Store(6, StoreHandler.instance()),
	ItemBag(7, ItemBagHandler.instance()),
	Equip(8, EquipHandler.instance()),
	Hero(9, HeroHandler.getHandler()),
	Magic(10, MagicHandler.getHandler()),
	Friend(11, FriendHandler.instance()),
	Task(12, TaskHandler.instance(), 20),
	Email(13, EmailHandler.instance()),
	BattleTower(14, BattleTowerHandler.getHandler()),
	GroupBase(15, GroupBaseHandler.getHandler()),
	GroupPersonal(16, GroupPersonalHandler.getHandler()),
	GroupMember(17, GroupMemberHandler.getHandler()),
	WorShip(18, WorShipHandler.getHandler()),
	MainCity(19, MainHandler.getHandler()),
	//DailyActivity(20, DailyActivityHandler.getHandler()),
	Copy(21, CopyHandler.getHandler(), 30),
	ActivityCount(22, ActivityCountHandler.getHandler()),
	DailyGift(23, DailyGiftHandler.getHandler()),
	FresherActivity(24, FresherActivityHandler.getInstance()),
	Sign(25, SignHandler.getInstance(), 3),
	Daily(26, DailyHandler.getInstance()),
	Fashion(27, FashionHandler.getInstance()),
	PeakArena(28, PeakArenaHandler.getHandler()),
	GroupSecret(29, GroupSecretHandler.getInstance()),
	GroupSecretMatch(30, GroupSecretMatchHandler.getInstance()),
	FixEquip(31, FixEquipHandler.instance()),
	Taoist(32, TaoistHandler.getHandler(), 30),
	MagicSecret(33, MagicSecretHandler.getHandler(), 20),
	GroupCopy(34, GroupCopyHandler.getInstance()),
	GroupCompetitionQuiz(35, GroupCompetitionQuizHandler.getHandler()),
	GroupCompSameScene(36, GroupCompSameSceneHandler.getHandler()),
	GroupCompetition(37, GroupCompetitionHandler.getHandler()),
	SelectCareer(38, SelectCareerHandler.instance(), 0),
	GroupPray(39, GroupPrayHandler.getHandler(), 0),
	;
	
	private int indx;
	private RandomMethodIF exeHandler;
	private int rate;
	
	ActionEnum(int indx, RandomMethodIF exeHandler, int initRate){
		this.indx = indx;
		this.exeHandler = exeHandler;
		this.rate = initRate;
	}
	
	ActionEnum(int indx, RandomMethodIF exeHandler){
		this.indx = indx;
		this.exeHandler = exeHandler;
		this.rate = 10;
	}
	
	public int getIndx() {
		return indx;
	}

	public RandomMethodIF getExeHandler() {
		return exeHandler;
	}

	public int getInitRate() {
		return rate;
	}
}
