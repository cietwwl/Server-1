package com.rw.dataaccess.processor;

import java.util.ArrayList;

import com.playerdata.PlayerMgr;
import com.rw.dataaccess.PlayerParam;
import com.rw.dataaccess.PlayerCoreCreation;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.service.Email.EmailUtils;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.email.TableEmail;

public class EmailCreator implements DataExtensionCreator<TableEmail>{
	static final String email = "10003"; 
	
	@Override
	public TableEmail create(String userId) {
		TableEmail email = new TableEmail();
		email.setUserId(userId);
		EmailData data = EmailUtils.createEmailData(this.email, "", new ArrayList<String>());
		EmailUtils.setEamil(email, data, System.currentTimeMillis());
		BILogMgr.getInstance().logActivityBegin(PlayerMgr.getInstance().find(userId), null, BIActivityCode.CREATROLE_REWARDS_EMAIL,0,0);
		return email;
	}

}
