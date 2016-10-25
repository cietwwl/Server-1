package com.bm.worldBoss.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBHPCfg;
import com.bm.worldBoss.cfg.WBHPCfgDAO;
import com.bm.worldBoss.cfg.WBSettingCfgDAO;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.dao.battle.pojo.BattleCfgDAO;
import com.rwbase.dao.battle.pojo.cfg.CopyMonsterInfoCfg;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class WBDataHolder {
	private static WBDataHolder instance = new WBDataHolder();
	
	private static eSynType synType = eSynType.WB_DATA;
	private AtomicInteger version = new AtomicInteger(-1);

	
	public static WBDataHolder getInstance(){
		return instance;
	}
	


	public void syn(Player player, int versionP) {
		if(versionP == -1 || version.get() != versionP){
			
			WBData wbData = get();
			if (wbData != null) {
				ClientDataSynMgr.synData(player, wbData, synType, eSynOpType.UPDATE_SINGLE,version.get());
			} else {
				GameLog.error("WBDataHolder", "#syn()", "find wbData fail");
			}
		}
		
	}

	public WBData get() {		
		return  WBDataDao.getInstance().get();
	}
	
	
	public boolean newBoss(WBCfg wbCfg){
		WBData oldData = get();	
		WBData newData = null;
		if(oldData == null){
			newData = WBData.newInstance();
			int bossInitLevel = WBSettingCfgDAO.getInstance().getCfg().getBossInitLevel();
			newData.setBossLevel(bossInitLevel);		
			
		}else{			
			newData = oldData.nextInstance();
		}
		
		boolean success = init(newData, wbCfg );
		
		return success;
	}
	
	private boolean init(WBData data, WBCfg wbCfg){
		
		
		CopyMonsterInfoCfg cfg = BattleCfgDAO.getInstance().getCopyMonsterInfoByCopyID(wbCfg.getCopyId());
		String monsterCfgId=null;
		if(cfg.getEnemyList().size() > 0 ){
			List<String> enemyList = cfg.getEnemyList();
			for (String monsterId : enemyList) {
				if(StringUtils.isNotBlank(monsterId)){
					monsterCfgId = monsterId;
					break;
				}
			}			
		}		
		
		MonsterCfg monsterCfg = MonsterCfgDao.getInstance().getCfgById(monsterCfgId);
		if(monsterCfg!=null){
			data.setMonsterCfgId(monsterCfg.getId());
			WBHPCfg wbHpCfg = WBHPCfgDAO.getInstance().getCfgById(String.valueOf(data.getBossLevel()));
			float lifeFactor = wbHpCfg.getFactor();
			long maxLife = (long)(monsterCfg.getLife()*lifeFactor);			
			
			data.setMaxLife(maxLife);
			data.setCurLife(maxLife);	
			data.setPreStartTime(wbCfg.getPreStartTime());
			data.setStartTime(wbCfg.getStartTime());
			data.setEndTime(wbCfg.getEndTime());
			data.setFinishTime(wbCfg.getFinishTime());
			data.setWbcfgId(wbCfg.getId());
			boolean update = WBDataDao.getInstance().update(data);
			
			GameLog.info(LogModule.WorldBoss.getName(), "WBDataHolder[init]", "world boss update wbdata result: " + update);
			return update;
		}
		
		return false;
		
	
	}
	 
	
	public void update(){	
		WBData wbData = get();
		WBDataDao.getInstance().update(wbData);
		version.incrementAndGet();			
	}
	
	public long decrHp(Player player, long delta){
		
		WBData wbData = get();
		long curLifeTmp = wbData.getCurLife() - delta;
		wbData.setCurLife(curLifeTmp);
		
		if(curLifeTmp <= 0){
			LastFightInfo lastFightInfo = new LastFightInfo();
			lastFightInfo.setTime(System.currentTimeMillis())
							.setUserId(player.getUserId())
							.setUserName(player.getUserName());;							
			
			wbData.setLastFightInfo(lastFightInfo );
		}
		
		
		
		update();
		return curLifeTmp;
		
	}

	public void update(Player player) {
		update();
		syn(player,-1);
	}
	




}
