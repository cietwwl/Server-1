package com.bm.worldBoss;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.bm.worldBoss.cfg.WBRankAwardCfg;
import com.bm.worldBoss.cfg.WBRankAwardCfgDAO;
import com.bm.worldBoss.data.LastFightInfo;
import com.bm.worldBoss.data.WBData;
import com.bm.worldBoss.data.WBDataHolder;
import com.bm.worldBoss.rank.WBHurtItem;
import com.bm.worldBoss.rank.WBHurtRankMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.service.Email.EmailUtils;

public class WBAwardServer {
	
	private static WBAwardServer instance = new WBAwardServer();
	
	public static WBAwardServer getInstance(){
		return instance;
	}

	final private ExecutorService awardService = Executors.newSingleThreadExecutor(new SimpleThreadFactory("world_boss_service"));
	
	private AtomicBoolean running = new AtomicBoolean(false);
	
	public void doAwardTask(){
		
		if(!running.get()){
			return;
		}
		
		awardService.submit(new Runnable() {
			
			@Override
			public void run() {
				running.set(true);
				try {					
					doWBSendAward();
				} catch (Throwable e) {
					GameLog.error(LogModule.WorldBoss, "WBAwardServer[doAwardTask]", "", e);
				}finally{
					running.set(false);
				}
				
			}
		});		
	}
	
	public boolean isRunning(){
		return running.get();
	}
	
	
	private  void doWBSendAward(){
		sendRankAwards();
		sendKillAttackAward();
	}

	
	private void sendRankAwards(){
		
		List<WBRankAwardCfg> allCfg = WBRankAwardCfgDAO.getInstance().getAllCfg();
		for (WBRankAwardCfg wbRankAwardCfg : allCfg) {
			int offset = wbRankAwardCfg.getOffset();
			int size = wbRankAwardCfg.getSize();
			List<WBHurtItem> rankList = WBHurtRankMgr.getRankList(offset, size);
			for (WBHurtItem wbHurtItem : rankList) {			
				EmailUtils.sendEmail(wbHurtItem.getUserId(),wbRankAwardCfg.getAwardId());
			}		
		}
		
	}
	
	private void sendKillAttackAward(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		LastFightInfo lastFightInfo = wbData.getLastFightInfo();
		if(lastFightInfo!=null){
			String wbcfgId = wbData.getWbcfgId();
			WBCfg wbCfg = WBCfgDAO.getInstance().getCfgById(wbcfgId);
			String killAttackAwardId = wbCfg.getKillAttackAwardId();
			String userId = lastFightInfo.getUserId();
			EmailUtils.sendEmail(userId, killAttackAwardId);
		}		
		
	}

}
