package com.rwbase.dao.user.accountInfo;

import com.rw.fsutil.cacheDao.PFDataRdbDao;

public class TableAccountDAO extends PFDataRdbDao<TableAccount> {

	private static TableAccountDAO instance = new TableAccountDAO();

	protected TableAccountDAO() {
	}

	public static TableAccountDAO getInstance() {
		return instance;
	}

	public TableAccount getAccountByAccountId(String accountId) {
		TableAccount account = this.getObject(accountId);
		return account;
	}

	public TableAccount getAccountByOpenAccount(String openAccountId) {
		TableAccount account = this.findOneByKey("openAccount", openAccountId);
		return account;
	}

	public boolean saveOrUpdate(TableAccount target) {
		return super.saveOrUpdate(target);
	}
}
