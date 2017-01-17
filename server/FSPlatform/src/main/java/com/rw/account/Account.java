package com.rw.account;

import org.apache.commons.lang3.StringUtils;

import com.bm.login.AccoutBM;
import com.rwbase.dao.user.accountInfo.TableAccount;
import com.rwbase.dao.whiteList.TableWhiteList;
import com.rwbase.dao.whiteList.TableWhiteListDAO;
import com.rwbase.dao.whiteList.TableWhiteListHolder;

public class Account {
	private String accountId;
	
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
		TableAccount tableAccount = getTableAccount();
		if(tableAccount == null){
			return false;
		}
		return TableWhiteListDAO.getInstance().CheckIsInWhiteList(tableAccount.getOpenAccount());
	}
}
