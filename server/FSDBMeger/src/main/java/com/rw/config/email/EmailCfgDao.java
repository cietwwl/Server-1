package com.rw.config.email;

import java.util.Map;

import com.rw.config.CfgCsvDao;
import com.rw.config.CfgCsvHelper;
import com.rw.utils.SpringContextUtil;

public class EmailCfgDao extends CfgCsvDao<EmailCfg>{

	public static EmailCfgDao getInstance() {
		return SpringContextUtil.getBean(EmailCfgDao.class);
	}
	
	@Override
	protected Map<String, EmailCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("email/email.csv", EmailCfg.class);
		return cfgCacheMap;
	}

}
