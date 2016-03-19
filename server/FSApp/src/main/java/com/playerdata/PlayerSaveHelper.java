//package com.playerdata;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
//import com.log.GameLog;
//import com.log.LogModule;
//
//public class PlayerSaveHelper {
//
//	private Player player;
//	
//	private boolean saving = false;
//	
//	private AtomicInteger savedCount = new AtomicInteger(0);
//	
//	public PlayerSaveHelper(Player playerP){
//		this.player = playerP;
//	}
//	
//	final int totalToSave = 22;
//	
//	public int getProgress(){
//		return savedCount.get()*100/totalToSave;
//	}
//	
//	public synchronized int save(boolean immediately){
//		int progress = 0;
//		if(saving){
//			progress =  getProgress();
//		}else{
//			saving = true;
//			savedCount.set(0);
//			try {
//				//GameLog.error(LogModule.COMMON.getName(), player.getUserId(), "保存数据。。。。。",null);
//				doSave(immediately);				
//			} catch (Exception e) {
//				GameLog.error(LogModule.COMMON.getName(), player.getUserId(), "PlayerSaveHelper[save]用户数据保存错误", e);
//			}finally{
//				saving = false;
//			}
//		}
//		return progress;
//	}
//	
//	private void doSave(boolean immediately){
//		
//		player.getUserGameDataMgr().flush();
//		savedCount.incrementAndGet();
//		
//		player.getUserDataMgr().flush();
//		savedCount.incrementAndGet();
//		
//		player.getItemBagMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getHeroMgr().save(immediately);
//		savedCount.incrementAndGet();
//		
//		player.getSettingMgr().flush();
//		savedCount.incrementAndGet();
//		
//		player.getCopyRecordMgr().flush();
//		savedCount.incrementAndGet();
//		
//		player.getCopyDataMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getUniqueNameMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getFriendMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getVipMgr().flush();
//		savedCount.incrementAndGet();
//		
//		player.getEmailMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getGambleMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getTaskMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getGuideMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getFashionMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getSecretMgr().flush();
//		savedCount.incrementAndGet();
//		
//		player.getMagicMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getStoreMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getSignMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getFresherActivityMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getDailyActivityMgr().save();
//		savedCount.incrementAndGet();
//		
//		player.getTowerMgr().save();
//		savedCount.incrementAndGet();
//	}
//	
//}
