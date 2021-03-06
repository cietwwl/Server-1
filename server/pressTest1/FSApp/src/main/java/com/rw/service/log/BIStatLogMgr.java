package com.rw.service.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.bm.serverStatus.ServerStatusMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.log.db.BILogDbMgr;
import com.rw.service.log.db.BIUser;

public class BIStatLogMgr {
	
	private static BIStatLogMgr instance = new BIStatLogMgr();
	
	private static BILogDbMgr biLogDbMgr;
	
	private BIStatLogMgr(){
		
//		String url = "jdbc:mysql://127.0.0.1:3306/fs_data_mt?useUnicode=true&amp;characterEncoding=utf8&amp;characterResultSets=utf8";
//		String username = "root";
//		String password = "123456";
//		int maxActive = 10;
//		DruidDataSource dataSource;
//		try {
//			dataSource = JdbcTemplateFactory.newDataSource(url, username, password, maxActive );
//			biLogDbMgr = new BILogDbMgr(dataSource);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
		
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		biLogDbMgr = new BILogDbMgr(dataSource);
	}
	
	public static BIStatLogMgr getInstance(){
		return instance;
	}
	
	public void doStat(){
		if(DateUtils.dayChanged(ServerStatusMgr.getLastBIStatLogTime())){	
			ServerStatusMgr.setLastBIStatLogTime(System.currentTimeMillis());
			try {				
				logZoneCountByUserGameData();
				logZoneCountByUser();
			} catch (Exception e) {
				GameLog.error(LogModule.BILOG.getName(), "BIStatLogMgr", "BIStatLogMgr[doStat] error", e);
			}
		}
		
	}

	private void logZoneCountByUserGameData(){
		
		final Map<String,BICounter> coinAccount = new HashMap<String,BICounter>();
		final String sql = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM user_other LEFT JOIN user ON user_other.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
		doDbCount(sql, new BIIntefaceCount(){

			@Override
			public void doCount(BIUser user) {
				String regChannelId = null;
				if(user.getZoneRegInfo()!=null){
					regChannelId = user.getZoneRegInfo().getRegChannelId();
				}
				long coin = user.getDbvalue().getCoin();
				getCounter(coinAccount, regChannelId, "totalCount").add(coin);
				
			}

			
		});
		logCoin(coinAccount);
	}
	
	private void logCoin(Map<String, BICounter> coinAccount) {
		for (BICounter biCounterTmp : coinAccount.values()) {
			BILogMgr.getInstance().logZoneCountCoin(biCounterTmp.getRegChannelId(), biCounterTmp.getCount());
		}
	}

	private void logZoneCountByUser(){
		
		final Map<String,BICounter> levelSpread = new HashMap<String,BICounter>();
		final Map<String,BICounter> vipSpread = new HashMap<String,BICounter>();
		final Map<String,BICounter> totalAccount = new HashMap<String,BICounter>();
		
		final String sql = "SELECT userId,zoneId,vip,level,zoneRegInfo FROM user ORDER BY userId LIMIT ? OFFSET ?;";
		doDbCount(sql, new BIIntefaceCount(){

			@Override
			public void doCount(BIUser user) {
				String regChannelId = null;
				if(user.getZoneRegInfo()!=null){
					regChannelId = user.getZoneRegInfo().getRegChannelId();
				}
				
				String level = String.valueOf(user.getLevel());
				String vip = String.valueOf(user.getVip());
				getCounter(levelSpread,regChannelId, level).incr();
				getCounter(vipSpread, regChannelId, vip).incr();
				getCounter(totalAccount, regChannelId, "totalCount").incr();
				
			}

			
		});
		
		logLevelSpread(levelSpread);
		logVipSpread(vipSpread);
		logTotalAccount(totalAccount);
	}
	
	private void doDbCount(String sql, BIIntefaceCount counter){
		int OFFSET = 0;
		final int LIMIT = 1000;
		List<BIUser> userList = biLogDbMgr.query(sql, new Object[]{LIMIT, OFFSET}, BIUser.class);
		while(!CollectionUtils.isEmpty(userList)){
			for (BIUser user : userList) {
				counter.doCount(user);
			}
			OFFSET = OFFSET + LIMIT;
			userList = biLogDbMgr.query(sql, new Object[]{LIMIT, OFFSET}, BIUser.class);
		}
		
	}
	
	private void logTotalAccount(Map<String, BICounter> totalAccount) {
		for (BICounter biCounterTmp : totalAccount.values()) {
			BILogMgr.getInstance().logZoneCountTotalAccount(biCounterTmp.getRegChannelId(), biCounterTmp.getCount());
		}
		
		
	}



	private void logVipSpread(Map<String, BICounter> vipSpread) {
		for (BICounter biCounterTmp : vipSpread.values()) {
			BILogMgr.getInstance().logZoneCountVipSpread(biCounterTmp.getRegChannelId(), biCounterTmp.getSpreadId(), biCounterTmp.getCount());
		}
		
	}



	private void logLevelSpread(Map<String, BICounter> levelSpread) {
		for (BICounter biCounterTmp : levelSpread.values()) {
			BILogMgr.getInstance().logZoneCountLevelSpread(biCounterTmp.getRegChannelId(), biCounterTmp.getSpreadId(), biCounterTmp.getCount());
		}
	}



	private BICounter getCounter(Map<String,BICounter> countMap,  String regChannelId, String spreadId){
		
		if(StringUtils.isBlank(regChannelId)){
			regChannelId = "empty";
		}
		
		String counterId = makeId(regChannelId,spreadId);
		BICounter counter = countMap.get(counterId);
		if(counter==null){
			counter = new BICounter(regChannelId,spreadId);
			countMap.put(counterId, counter);
		}
		return counter;
	}
	
	private interface BIIntefaceCount{
		public void doCount(BIUser user);
	}
	
	private String makeId(String regChannelId, String spreadId){
		return regChannelId+"_"+spreadId;
	}
	
	private class BICounter{
		
		private String regChannelId;
		//分布id 比如等级
		private String spreadId;
		
		private AtomicLong count = new AtomicLong();
		
		public BICounter(String regChannelIdP,  String spreadIdP){
			this.regChannelId = regChannelIdP;
			this.spreadId = spreadIdP;
		}
		public void add(long count){
			this.count.addAndGet(count);
		}
		public void incr(){
			this.count.incrementAndGet();
		}
		public long getCount(){
			return this.count.get();
		}

		public String getRegChannelId() {
			return regChannelId;
		}

		public String getSpreadId() {
			return spreadId;
		}
		
	}
	
	
	
	public static void main(String[] args) {
		BIStatLogMgr.getInstance().logZoneCountByUserGameData();
	}
	
}
