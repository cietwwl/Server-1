package com.rw.account;

import org.apache.commons.lang3.StringUtils;

import com.bm.login.AccoutBM;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.whiteList.TableWhiteList;
import com.rwbase.dao.whiteList.TableWhiteListHolder;

public class Account {
	private String accountId;
	private TableWhiteListHolder whiteList;
	
	public Account(){
		
	}
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public TableAccount getTableAccount() {
		return AccoutBM.getInstance().getByAccountId(accountId);
	}

	public boolean isWhiteList() {
		
		if(whiteList == null){
			TableAccount tableAccount = getTableAccount();
			String openAccount = tableAccount.getOpenAccount();
			if(!StringUtils.isEmpty(openAccount)){
				whiteList = new TableWhiteListHolder(openAccount);
			}
		}
		if(whiteList == null){
			return false;
		}
		TableWhiteList tableWhiteList = whiteList.getTableWhiteList();
		return tableWhiteList == null ? false : !tableWhiteList.isClose();
	}
}
