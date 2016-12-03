package com.bm.worldBoss;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.bm.worldBoss.cfg.WBHPCfg;
import com.bm.worldBoss.cfg.WBHPCfgDAO;
import com.bm.worldBoss.cfg.WBSettingCfg;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.bm.worldBoss.data.WBBroatCastData;
import com.bm.worldBoss.data.WBBroatCastDataHolder;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.data.WBState;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.bm.worldBoss.service.WBService;
import com.bm.worldBoss.state.IwbState;
import com.bm.worldBoss.state.WBFightEndState;
import com.bm.worldBoss.state.WBFinishState;
import com.bm.worldBoss.state.WBNewBossState;
import com.bm.worldBoss.state.WBPreStartState;
import com.bm.worldBoss.state.WBSendAwardState;
import com.bm.worldBoss.state.WBStateFSM;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.battleVerify.damageControll.DamageControllCfg;
import com.playerdata.battleVerify.damageControll.DamageControllCfgDAO;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.ServerSwitch;


public class WBMgr {

	
	private static WBMgr wbMgr = new WBMgr();
	
	private ReadLock readLock;
	private WriteLock writeLock;
	
	public WBMgr(){
		ReentrantReadWriteLock treeRwLock = new ReentrantReadWriteLock();
		this.readLock = treeRwLock.readLock();
		this.writeLock = treeRwLock.writeLock();		
	}
	
	public static WBMgr getInstance(){
		return wbMgr;
	} 

	public void synWBData(Player player, int wbDataVersion){
		WBUserMgr.getInstance().synWBUserData(player, wbDataVersion);
		WBDataHolder.getInstance().syn(player, wbDataVersion);
		WBHurtRankMgr.syn(player);
	}
	
	public boolean isBossDie(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getCurLife()<=0;
	}
	
	public boolean isAfterBossStartTime(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getStartTime() <= System.currentTimeMillis();
	}
	public boolean isBeforeBossEndTime(){
		WBData wbData = WBDataHolder.getInstance().get();
		return wbData.getEndTime() > System.currentTimeMillis();
	}
	
	

	
	public boolean initNewBoss(WBCfg nextCfg ){
		boolean success = false;
		writeLock.lock();
		try {			
			success = WBDataHolder.getInstance().newBoss(nextCfg);
			
		} finally {
			writeLock.unlock();			
		}		
		return success;
	}

	
	public ArmyInfo getBossArmy(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		WBCfg cfg = WBCfgDAO.getInstance().getCfgById(wbData.getWbcfgId());
		ArmyInfo armyInfo = null;
		
		armyInfo = ArmyInfoHelper.buildMonsterArmy(wbData.getMonsterCfgId(), cfg.getCopyId());
		ArmyHero armyHero = armyInfo.getPlayer();
		CurAttrData curAttrData = new CurAttrData();
		
		readLock.lock();
		try {			
			curAttrData.setCurLife(wbData.getCurLife());
			curAttrData.setMaxLife(wbData.getMaxLife());
		} finally {
			readLock.unlock();
		}
		
		armyHero.setCurAttrData(curAttrData);
	
		return armyInfo;		
	}
	
	public long decrHp(Player player, long hurt){	
		long trueHurt = 0;
		writeLock.lock();		
		try {			
			WBData wbData = WBDataHolder.getInstance().get();
			long beforeLife = wbData.getCurLife();
			if(beforeLife > 0){				
				long curLife = WBDataHolder.getInstance().decrHp(player, hurt);
				trueHurt = beforeLife - curLife;
//				System.out.println("curlift :" + curLife);
				if(isBossDie()){
//					System.err.println("--------------world boss was killed!!");
					broatCastBossDie(player.getUserId(), player.getUserName());
				}
			}
			
		} finally {
			writeLock.unlock();			
		}		
		synWBData(player, -1);	
		return trueHurt;		
	}
	
	private void broatCastBossDie(String killRoleID, String name) {
		
		WBBroatCastData broatCastData = new WBBroatCastData();
		broatCastData.setKillBossRole(killRoleID, name);
		
		broatCast(broatCastData);
		
	}
	public void broatCastBossLeave() {
		
		WBBroatCastData broatCastData = new WBBroatCastData();
		broatCastData.setBossLeave(true);
		
		broatCast(broatCastData);
		
	}

	private void broatCast(WBBroatCastData broatCastData) {
		List<String> allOnFightUserIds = WBOnFightMgr.getInstance().getAllOnFightUserIds();
		for (String userIdTmp: allOnFightUserIds) {
			Player online = PlayerMgr.getInstance().findPlayerFromMemory(userIdTmp);
			if(online!=null){				
//				System.out.println("broatcast world boss !, rec role:" + online.getUserName() + ", death:" + !StringUtils.isBlank(broatCastData.getKillBossRole())
//						+", leave :" + broatCastData.isBossLeave());
				WBBroatCastDataHolder.getInstance().syn(online, broatCastData);
			}
		}
	}

	public boolean adjustBossLevel(){	
		boolean success = false;
		writeLock.lock();		
		try {			
			WBData wbData = WBDataHolder.getInstance().get();
			if(wbData.isKilled()){
				wbData.setSurvivalCount(0);
				if(isQuickKilled(wbData)){
					wbData.addQuickKillCount();
				}else{
					wbData.setQuickKillCount(0);
				}
			}else{
				wbData.addSurvivalCount();
			}
			
			WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
			
			int quickKillMax = settingCfg.getQuickKillMax();
			int survialMax = settingCfg.getSurvialMax();
			if(wbData.getQuickKillCount() > quickKillMax){
				
				WBHPCfg nextLevel = WBHPCfgDAO.getInstance().getCfgById(String.valueOf(wbData.getBossLevel()+1));
				if(nextLevel!=null){					
					wbData.inrcBossLevel();
					GameLog.info(LogModule.WorldBoss.getName(), "WBMgr[adjustBossLevel]", "boss 升级:"+wbData.getBossLevel());
				}
				
			}else if(wbData.getSurvivalCount() > survialMax){
				WBHPCfg nextLevel = WBHPCfgDAO.getInstance().getCfgById(String.valueOf(wbData.getBossLevel()-1));
				if(nextLevel!=null){						
					wbData.dercBossLevel();
					GameLog.info(LogModule.WorldBoss.getName(), "WBMgr[adjustBossLevel]", "boss 降级:"+wbData.getBossLevel());
				}
			}
			
			WBDataHolder.getInstance().update();
			
		} finally {
			writeLock.unlock();			
		}		
		
		return success;		
	}
	
	private boolean isQuickKilled(WBData wbData){
		WBSettingCfg settingCfg = WBSettingCfgDAO.getInstance().getCfg();
		
		long killTimeCost = wbData.killedTimeCost();
		
		if(killTimeCost < settingCfg.getQuickKillTimeInMilli()){
			return true;
		}
		return false;
	}
	

	public boolean isSameBoss(int bossVersion) {
		WBData wbData = WBDataHolder.getInstance().get();
		if(wbData == null){
			return false;
		}
		return wbData.getVersion() == bossVersion;
	}

	public void onPlayerLogin(Player player) {
		synWBData(player, -10);//登录通知的版本不作维护
	}

	
	/**
	 * 检查伤害控制表，是否伤害已经超过控制值
	 * @param player
	 * @param updateHurt
	 * @return
	 */
	public long checkHurt(Player player, long updateHurt) {
		long checkHurt = 0; 
		DamageControllCfg cfg = DamageControllCfgDAO.getInstance().getCfgById(String.valueOf(player.getLevel()));
		if(cfg != null){
			checkHurt = updateHurt > cfg.getSingleHitMaxHurt() ? cfg.getSingleHitMaxHurt() : updateHurt;
		}
		return checkHurt;
	}

	
	/**
	 * 广播boss改变
	 * @param cleanBuff TODO 切换到新boss此参数才为true
	 */
	public void broatBossChange(boolean cleanBuff) {
		List<Player> list = PlayerMgr.getInstance().getOnlinePlayers();
		for (Player player : list) {
			if(cleanBuff){
				WBUserMgr.getInstance().cleanBuff(player.getUserId());
			}
			synWBData(player, -1);	
		}
	}

	
	/**
	 * 作弊指令召唤世界boss，除了GMHandler，其他部分禁止调用此方法
	 */
	public void reCallNewBoss() {
		//先清除旧的世界boss数据
		WBStateFSM.getInstance().setState(null);
		
		//copy一个世界boss配置出来
		WBCfg cfg = WBCfgDAO.getInstance().getAllCfg().get(0);
		WBCfg copyCfg = new WBCfg(cfg);
		int hour = DateUtils.getCurrentHour();
		int min = DateUtils.getCurMinuteOfHour();
		copyCfg.setPreStartTimeStr(hour +":" + min);
		int afterTime = min + 2;
		if(afterTime >= 60){
			hour ++;
			afterTime = afterTime % 60;
		}
		copyCfg.setStartTimeStr(hour + ":" + afterTime);
		
		afterTime = afterTime + 40;
		if(afterTime >= 60){
			hour ++;
			afterTime = afterTime % 60;
		}
		
		copyCfg.setEndTimeStr(hour + ":" + afterTime);
		afterTime = afterTime + 10;
		if(afterTime >= 60){
			hour ++;
			afterTime = afterTime % 60;
		}
		copyCfg.setFinishTimeStr(hour + ":" + afterTime);
		WBDataHolder.getInstance().newBoss(copyCfg);
		WBNewBossState state = new WBNewBossState();
		WBStateFSM.getInstance().setState(state);
		state.doEnter();
	}
	
	public void change2NextState(){
		WBStateFSM fsm = WBStateFSM.getInstance();
		WBState state = fsm.getState();
		IwbState wb = null;
		switch (state) {
		case NewBoss:
			wb = new WBPreStartState();
			break;
		case PreStart:
			wb = new WBFinishState();
			break;
		case FightStart:
			wb = new WBFightEndState();
			break;
		case FightEnd:
			wb = new WBSendAwardState();
			break;
		case SendAward:
			wb = new WBFinishState();
			break;
		case Finish:
			reCallNewBoss();
			break;

		default:
			break;
		}
		if(wb != null){
			fsm.setState(wb);
			wb.doEnter();
		}
		GameLog.info(LogModule.GM.getName(), "GM", "world boss state transform to :" + fsm.getState());
	}

	public void changeWorldBossState(int state) {
		if(state == 0){//关闭
			ServerSwitch.setOpenWorldBoss(false);
		}else if(state == 1){ //开启
			ServerSwitch.setOpenWorldBoss(true);
		}
		
		WBData data = WBDataHolder.getInstance().get();
		if(data == null){
			return;
		}
		data.setOpen(ServerSwitch.isOpenWorldBoss());
		WBDataHolder.getInstance().update();
		broatBossChange(false);
		GameLog.info(LogModule.GM.getName(), "GM", "world boss is open:" + ServerSwitch.isOpenWorldBoss());
	}
	
	
}