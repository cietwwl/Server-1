package com.rwbase.dao.email;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class EmailCfgDAO  extends CfgCsvDao<EmailCfg>{
	private static EmailCfgDAO instance = new EmailCfgDAO();
	private EmailCfgDAO(){}
	public static EmailCfgDAO getInstance(){
		return instance;
	}

	@Override
	public Map<String, EmailCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("email/emailCfg.csv",EmailCfg.class);
		return cfgCacheMap;
	}
	
	public EmailCfg getEmailCfg(String cfgId){
		return (EmailCfg)getCfgById(cfgId);
	}
}
