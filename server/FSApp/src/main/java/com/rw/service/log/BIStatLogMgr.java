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
import com.rw.service.log.infoPojo.ClientInfo;

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
		final Map<String,BICounter> giftGoldAccount = new HashMap<String,BICounter>();
		final Map<String,BICounter> chargeGoldAccount = new HashMap<String,BICounter>();
		final Map<String,BICounter> financeGoldAccount = new HashMap<String,BICounter>();
		
//		final String sql = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM user_other LEFT JOIN user ON user_other.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
		
//		String[] sqlStr  = new String[10];		
//		sqlStr[0] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata00 LEFT JOIN user ON table_kvdata00.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[1] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata01 LEFT JOIN user ON table_kvdata01.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[2]= "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata02 LEFT JOIN user ON table_kvdata02.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[3] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata03 LEFT JOIN user ON table_kvdata03.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[4] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata04 LEFT JOIN user ON table_kvdata04.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[5] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata05 LEFT JOIN user ON table_kvdata05.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[6] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata06 LEFT JOIN user ON table_kvdata06.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[7] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata07 LEFT JOIN user ON table_kvdata07.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[8] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata08 LEFT JOIN user ON table_kvdata08.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//		sqlStr[9] = "SELECT userId,zoneId,vip,level,zoneRegInfo,dbvalue FROM table_kvdata09 LEFT JOIN user ON table_kvdata09.dbkey=user.userId ORDER BY userId LIMIT ? OFFSET ?;";
//				
//		
//		for(String sql:sqlStr){
//			doDbcounts(sql,coinAccount,giftGoldAccount,chargeGoldAccount);
//		}
		
		String tempSql = "select userId, coin, gold, giftGold, chargeGold, zoneRegInfo from majordata left join `user` on majordata.id = `user`.userId  order by `user`.userId limit ? offset ?;";
		
		doDbcounts(tempSql, coinAccount, giftGoldAccount, chargeGoldAccount,financeGoldAccount);
				
		logCoin(coinAccount);
		logGiftGold(giftGoldAccount);
		logChargeGold(chargeGoldAccount);
		logFinanceGold(financeGoldAccount);
	}
	
	private void doDbcounts(String sql, final Map<String, BICounter> coinAccount,
			final Map<String, BICounter> giftGoldAccount,
			final Map<String, BICounter> chargeGoldAccount,
			final Map<String, BICounter> financeGoldAccount) {
		doDbCount(sql, new BIIntefaceCount(){

			@Override
			public void doCount(BIUser user) {
				String regSubChannelId = null;
				String clientPlatForm = null;
				String loginZoinId =null;
				if(user.getZoneRegInfo()!=null){
					regSubChannelId = user.getZoneRegInfo().getRegSubChannelId();
					clientPlatForm = user.getZoneRegInfo().getRegClientPlatForm();
					loginZoinId = user.getZoneRegInfo().getRegZoneId()+"";
				}
				
				long coin = user.getCoin();
				long giftGold = user.getGiftGold();
				long chargeGold = user.getChargeGold();
				
//				getCounter(coinAccount, regSubChannelId, "totalCount", clientPlatForm).add(coin);
				BICounter count = getCounter(coinAccount, regSubChannelId, "totalCount", clientPlatForm,null);
				if(count != null){
					count.add(coin);
					getCounter(giftGoldAccount, regSubChannelId, "totalCountGold", clientPlatForm,null).add(giftGold);
					getCounter(chargeGoldAccount, regSubChannelId, "totalCountGold", clientPlatForm,null).add(chargeGold);
					getCounter(financeGoldAccount, regSubChannelId, null, null,loginZoinId).add(chargeGold);
				}
			}
			

			
		});
		
	}

	private void logCoin(Map<String, BICounter> coinAccount) {
		for (BICounter biCounterTmp : coinAccount.values()) {
			BILogMgr.getInstance().logZoneCountCoin(biCounterTmp.getRegSubChannelId(), biCounterTmp.getCount(), biCounterTmp.getClientPlatForm());
		}
	}
	
	private void logGiftGold(Map<String, BICounter> giftGoldAccount) {
		for (BICounter biCounterTmp : giftGoldAccount.values()) {
			BILogMgr.getInstance().logZoneCountGiftGold(biCounterTmp.getRegSubChannelId(), biCounterTmp.getCount(), biCounterTmp.getClientPlatForm());
		}
	}

	private void logChargeGold(Map<String, BICounter> chargeGoldAccount) {
		for (BICounter biCounterTmp : chargeGoldAccount.values()) {
			BILogMgr.getInstance().logZoneCountChargeGold(biCounterTmp.getRegSubChannelId(), biCounterTmp.getCount(), biCounterTmp.getClientPlatForm());
		}
	}
	
	private void logFinanceGold(Map<String, BICounter> chargeGoldAccount) {
		for (BICounter biCounterTmp : chargeGoldAccount.values()) {
			BILogMgr.getInstance().logZoneCountFinanceGold(biCounterTmp.getRegSubChannelId(), biCounterTmp.getCount(), biCounterTmp.getLoginZoinId());
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
				String regSubChannelId = null;
				String clientPlatForm = null;
				if(user.getZoneRegInfo()!=null){
					regSubChannelId = user.getZoneRegInfo().getRegSubChannelId();
					clientPlatForm = user.getZoneRegInfo().getRegClientPlatForm();
				}
				
				
				String level = String.valueOf(user.getLevel());
				String vip = String.valueOf(user.getVip());
				
				BICounter count = getCounter(levelSpread,regSubChannelId, level, clientPlatForm,null);
				if(count != null){
					count.incr();
					getCounter(vipSpread, regSubChannelId, vip, clientPlatForm,null).incr();
					getCounter(totalAccount, regSubChannelId, "totalCount", clientPlatForm,null).incr();
					
				}
				
			}
			
			
			}
		);
		
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
			BILogMgr.getInstance().logZoneCountTotalAccount(biCounterTmp.getRegSubChannelId(), biCounterTmp.getCount(), biCounterTmp.getClientPlatForm());
		}		
		
	}



	private void logVipSpread(Map<String, BICounter> vipSpread) {
		for (BICounter biCounterTmp : vipSpread.values()) {
			BILogMgr.getInstance().logZoneCountVipSpread(biCounterTmp.getRegSubChannelId(), biCounterTmp.getSpreadId(), biCounterTmp.getCount(), biCounterTmp.getClientPlatForm());
		}
		
	}



	private void logLevelSpread(Map<String, BICounter> levelSpread) {
		for (BICounter biCounterTmp : levelSpread.values()) {
			BILogMgr.getInstance().logZoneCountLevelSpread(biCounterTmp.getRegSubChannelId(), biCounterTmp.getSpreadId(), biCounterTmp.getCount(), biCounterTmp.getClientPlatForm());
		}
	}


	/**
	 * 包含了‘注册渠道_标记符_用户平台’和‘区id_注册渠道’两种格式
	 * @param countMap
	 * @param regSubChannelId
	 * @param spreadId
	 * @param clientPlatForm
	 * @param loginZoinId
	 * @return
	 */
	private BICounter getCounter(Map<String,BICounter> countMap,  String regSubChannelId, String spreadId, String clientPlatForm,String loginZoinId){
		
		if(StringUtils.isBlank(regSubChannelId)){
			regSubChannelId = "empty";
			return null;//机器人不加入统计
		}
		if(StringUtils.isBlank(clientPlatForm)){
			clientPlatForm = "";
		}
		
		String counterId = makeId(regSubChannelId,spreadId,clientPlatForm);
		if(spreadId == null){
			makeIdOfFinance(regSubChannelId,loginZoinId);
		}
		BICounter counter = countMap.get(counterId);
		if(counter==null){
			counter = new BICounter(regSubChannelId,spreadId, clientPlatForm,loginZoinId);
			countMap.put(counterId, counter);
		}
		return counter;
	}
	
	private interface BIIntefaceCount{
		public void doCount(BIUser user);
	}
	
	private String makeId(String regChannelId, String spreadId, String clientPlatFormP){
		return regChannelId+"_"+spreadId+"_"+clientPlatFormP;
	}
	
	private String makeIdOfFinance(String regChannelId, String loginZoinId){
		return loginZoinId+"_"+regChannelId;
	}
	
	private class BICounter{
		
		private String regSubChannelId;
		//分布id 比如等级
		private String spreadId;
		
		private String clientPlatForm;
		
		private String loginZoinId;
		
		private AtomicLong count = new AtomicLong();
		
		public BICounter(String regSubChannelIdP,  String spreadIdP, String clientPlatFormP,String loginZoinId){
			this.regSubChannelId = regSubChannelIdP;
			this.spreadId = spreadIdP;
			this.clientPlatForm = clientPlatFormP;
			this.loginZoinId = loginZoinId;
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

		public String getRegSubChannelId() {
			return regSubChannelId;
		}
		public String getSpreadId() {
			return spreadId;
		}
		public String getClientPlatForm() {
			return clientPlatForm;
		}
		public String getLoginZoinId() {
			return loginZoinId;
		}
		
		
	}
	
	
	
	public static void main(String[] args) {
		BIStatLogMgr.getInstance().logZoneCountByUserGameData();
	}
	
}
