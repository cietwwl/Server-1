package com.rwbase.dao.email;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.dropitem.DropCfgDAO;

public class EmailCfgDAO  extends CfgCsvDao<EmailCfg>{
	public static EmailCfgDAO getInstance() {
		return SpringContextUtil.getBean(EmailCfgDAO.class);
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
