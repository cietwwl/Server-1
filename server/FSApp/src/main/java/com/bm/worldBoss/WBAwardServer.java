package com.bm.worldBoss;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;

import com.bm.worldBoss.cfg.WBCfg;
import com.bm.worldBoss.cfg.WBCfgDAO;
import com.bm.worldBoss.cfg.WBExAwardCfg;
import com.bm.worldBoss.cfg.WBExAwardCfgDAO;
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
		sendKillAttackAward();
		sendRankAwards();
	}

	
	private void sendRankAwards(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		boolean isBossKilled = wbData.isKilled();
		List<WBRankAwardCfg> allCfg = WBRankAwardCfgDAO.getInstance().getAllCfg();
		for (WBRankAwardCfg wbRankAwardCfg : allCfg) {
			GameLog.info(LogModule.WorldBoss.getName(), "WBAwardServer[sendRankAwards]", "world boss sendAward start, wbRankAwardCfgId: " + wbRankAwardCfg.getId());
			
			String awardId = wbRankAwardCfg.getAwardId();
			String award = wbRankAwardCfg.getAward();
			int survivalCount = wbData.getSurvivalCount();
			if(isBossKilled && survivalCount > 0){
				WBExAwardCfg exAwardCfg = WBExAwardCfgDAO.getInstance().getCfgById(String.valueOf(survivalCount));
				if(exAwardCfg!=null){
					String exAward = exAwardCfg.getAward();
					awardId = exAwardCfg.getAwardId();
					award = StringUtils.join(new String[]{award,exAward});
				}
			}		
			
			
			int offset = wbRankAwardCfg.getOffset();
			int size = wbRankAwardCfg.getSize();
			
			GameLog.info(LogModule.WorldBoss.getName(), "WBAwardServer[sendRankAwards]", "world boss sendAward start, offset: " + offset+" size:"+size);
			
			List<WBHurtItem> rankList = WBHurtRankMgr.getRankList(offset, size);
			List<String> args = new ArrayList<String>();
			int rankIndex = offset;
			for (WBHurtItem wbHurtItem : rankList) {
				args.add(String.valueOf(rankIndex) );
				
				EmailUtils.sendEmail(wbHurtItem.getUserId(),awardId,award,args);
				rankIndex++;
				
				args.clear();
			}	
			
			GameLog.info(LogModule.WorldBoss.getName(), "WBAwardServer[sendRankAwards]", "world boss sendAward finish, offset: " + offset+" size:"+size);
		}
		
	}
	
	private void sendKillAttackAward(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		LastFightInfo lastFightInfo = wbData.getLastFightInfo();
		if(lastFightInfo!=null){
			String wbcfgId = wbData.getWbcfgId();
			WBCfg wbCfg = WBCfgDAO.getInstance().getCfgById(wbcfgId);
			String killAttackAwardId = wbCfg.getKillAttackAwardId();
			String awardContent = wbCfg.getKillAttackAward();
			String userId = lastFightInfo.getUserId();
			EmailUtils.sendEmail(userId, killAttackAwardId,awardContent);
		}		
		
	}

}
