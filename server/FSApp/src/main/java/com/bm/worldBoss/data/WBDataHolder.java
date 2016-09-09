package com.bm.worldBoss.data;

import java.util.concurrent.atomic.AtomicInteger;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.battleVerify.MonsterCfg;
import com.playerdata.battleVerify.MonsterCfgDao;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class WBDataHolder {
	private static WBDataHolder instance = new WBDataHolder();
	
	private static eSynType synType = eSynType.WB_DATA;
	final private String WB_DATA_ID = "worldBossId";
	private AtomicInteger version = new AtomicInteger(-1);

	
	public static WBDataHolder getInstance(){
		return instance;
	}
	


	public void syn(Player player, int versionP) {
		if(versionP == -1 || version.get()!=versionP){
			
			WBData wbData = get();
			if (wbData != null) {
				ClientDataSynMgr.synData(player, wbData, synType, eSynOpType.UPDATE_SINGLE);
			} else {
				GameLog.error("WBDataHolder", "#syn()", "find wbData fail:" + WB_DATA_ID);
			}
		}
		
	}

	public WBData get() {
		return  WBDataDao.getInstance().get(WB_DATA_ID);
	}
	public boolean newBoss(String wbCfgId){
		WBData oldData = get();	
		WBData newData = null;
		if(oldData == null){
			newData = WBData.newInstance(WB_DATA_ID);
		}else{			
			newData = oldData.newInstance();
		}
		
		WBCfg wbCfg = WBCfgDAO.getInstance().getCfgById(wbCfgId);
		init(newData, wbCfg );
		boolean success = WBDataDao.getInstance().update(newData);
		
		return success;
	}
	
	private void init(WBData data, WBCfg wbCfg){	
		String monsterId = wbCfg.getBossId();
		MonsterCfg monster = MonsterCfgDao.getInstance().getConfig ( monsterId );
		data.setId(monsterId);
		int maxLife = monster.getLife();
		data.setMaxLife(maxLife);
		data.setCurLife(maxLife);		
	}
	 
	
	private void update(){		
		WBDataDao.getInstance().update(WB_DATA_ID);
		version.incrementAndGet();			
	}
	
	public long decrHp(long delta){
		
		WBData wbData = get();
		long curLifeTmp = wbData.getCurLife() - delta;
		wbData.setCurLife(curLifeTmp);
		update();
		return curLifeTmp;
		
	}

	public void update(Player player) {
		update();
		syn(player,-1);
	}
	



}
