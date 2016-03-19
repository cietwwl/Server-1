package com.bm.login;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.user.accountInfo.TableAccountDAO;

public class AccoutBM {
	
	private TableAccountDAO tableAccountDAO = TableAccountDAO.getInstance();
	private static AccoutBM instance = new AccoutBM();
	
	public static AccoutBM getInstance(){
		return instance;
	}
	
	public TableAccount getByAccountId(String accountId) {
		
		if (StringUtils.isBlank(accountId)) {
			return null;
		}
		TableAccount accout = tableAccountDAO.getAccountByAccountId(accountId);
		return accout;
	}
	
	public TableAccount getByOpenAccount(String openAccount){
		if (StringUtils.isBlank(openAccount)) {
			return null;
		}
		TableAccount accout = tableAccountDAO.getAccountByOpenAccount(openAccount);
		return accout;
	}
	
	public int update(TableAccount account) {
		
		if (!tableAccountDAO.saveOrUpdate(account)) {
			GameLog.info("userAccount", account.getAccount(), "userAccount 更新失败,res=-1", null);
			return -1;
		}
		return 0;
	}
	
	public TableAccount createRandomAccount(String openAccountId) 
	{
		TableAccount account = null;
		String accountId = getGuestAccountId();
		if(StringUtils.isNotBlank(accountId)){
			String password = getRandomUUID();
			account = createAccount(accountId, password, openAccountId);
		}
		return account;
	}
	
	public String getGuestAccountId() 
	{
		String guestAccount = null;
		for (int i = 0; i < 10; i++) {
			Calendar instance = Calendar.getInstance();
			SimpleDateFormat mmdd=new SimpleDateFormat("MMdd");;
			int random = RandomUtils.nextInt(1000000);
			guestAccount = mmdd.format(instance.getTime())+random;
			TableAccount byAccountId = getByAccountId(guestAccount);
			if(byAccountId==null){
				break;
			}
		}
		return guestAccount;
	}
	
	public TableAccount createAccount(String accountId, String password, String openAccountId)
	{
		TableAccount account = new TableAccount();
		account.setAccount(accountId);
		account.setPassword(password);
		account.setOpenAccount(openAccountId);
		account.setRegisterTime(System.currentTimeMillis());
		
		int update = update(account);
		if(update==0){
			return account;
		}else{
			return null;
		}
	}

	private static String getRandomUUID() 
	{
		return UUID.randomUUID().toString();
	}

	
}
