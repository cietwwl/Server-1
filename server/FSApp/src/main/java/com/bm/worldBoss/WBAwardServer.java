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
		
		if(running.get()){
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
		sendFinalKillAttackAward();
		sendRankAwards();
		sendKillBossAward();
	}

	//发送boss击杀奖励，如果没有击杀boss，则不发送
	private void sendKillBossAward(){
		WBData data = WBDataHolder.getInstance().get();
		boolean killBoss = data.isKilled();
		if(killBoss){
			WBExAwardCfg cfg = WBExAwardCfgDAO.getInstance().getCfg(data.getSurvivalCount());
			if(cfg == null){
				GameLog.error(LogModule.WorldBoss.getName(), "WBAwardServer[sendKillBossAward]", "Can not find the WBExAwardCfg, the key is:" + data.getSurvivalCount(), null);
				return;
			}
		
			List<WBHurtItem> rankList = WBHurtRankMgr.getRankList();
			List<String> args = new ArrayList<String>();
			int rankIndex = 1;
			for (WBHurtItem wbHurtItem : rankList) {
				args.add(String.valueOf(rankIndex) );
				
				EmailUtils.sendEmail(wbHurtItem.getUserId(),cfg.getAwardId(),cfg.getAward(),args);
				rankIndex++;
				
				args.clear();
			}	
			
		}
	}
	private void sendRankAwards(){
		
//		WBData wbData = WBDataHolder.getInstance().get();
//		boolean isBossKilled = wbData.isKilled();
		List<WBRankAwardCfg> allCfg = WBRankAwardCfgDAO.getInstance().getAllCfg();
		for (WBRankAwardCfg wbRankAwardCfg : allCfg) {
//			GameLog.info(LogModule.WorldBoss.getName(), "WBAwardServer[sendRankAwards]", "world boss sendAward start, wbRankAwardCfgId: " + wbRankAwardCfg.getId());
			
			String awardId = wbRankAwardCfg.getAwardId();
			String award = wbRankAwardCfg.getAward();
//			int survivalCount = wbData.getSurvivalCount();
//			if(isBossKilled && survivalCount > 0){
//				WBExAwardCfg exAwardCfg = WBExAwardCfgDAO.getInstance().getCfgById(String.valueOf(survivalCount));
//				if(exAwardCfg!=null){
//					String exAward = exAwardCfg.getAward();
//					awardId = exAwardCfg.getAwardId();
//					award = StringUtils.join(new String[]{award,exAward});
//				}
//			}		
			
			
			int offset = wbRankAwardCfg.getOffset();
			int size = wbRankAwardCfg.getSize();
			
//			GameLog.info(LogModule.WorldBoss.getName(), "WBAwardServer[sendRankAwards]", "world boss sendAward start, offset: " + offset+" size:"+size);
			
			List<WBHurtItem> rankList = WBHurtRankMgr.getRankList(offset, offset + size -1);
			List<String> args = new ArrayList<String>();
			int rankIndex = offset;
			for (WBHurtItem wbHurtItem : rankList) {
				args.add(String.valueOf(rankIndex) );
				
				EmailUtils.sendEmail(wbHurtItem.getUserId(),awardId,award,args);
				rankIndex++;
				
				args.clear();
			}	
			
//			GameLog.info(LogModule.WorldBoss.getName(), "WBAwardServer[sendRankAwards]", "world boss sendAward finish, offset: " + offset+" size:"+size);
		}
		
	}
	
	/**
	 * 给最后一击的角色发奖励
	 */
	private void sendFinalKillAttackAward(){
		
		WBData wbData = WBDataHolder.getInstance().get();
		LastFightInfo lastFightInfo = wbData.getLastFightInfo();
		if (lastFightInfo != null) {
			String wbcfgId = wbData.getWbcfgId();
			WBCfg wbCfg = WBCfgDAO.getInstance().getCfgById(wbcfgId);
			String killAttackAwardId = wbCfg.getKillAttackAwardId();
			String awardContent = wbCfg.getKillAttackAward();
			String userId = lastFightInfo.getUserId();
			EmailUtils.sendEmail(userId, killAttackAwardId, awardContent);
		}
		
	}

}
