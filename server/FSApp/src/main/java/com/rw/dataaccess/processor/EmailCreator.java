package com.rw.dataaccess.processor;

import java.util.ArrayList;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.email.TableEmail;

public class EmailCreator implements DataExtensionCreator<TableEmail>{
	public static final String email = "10003"; 
	
	@Override
	public TableEmail create(String userId) {
		TableEmail email = new TableEmail();
		email.setUserId(userId);
		EmailData data = EmailUtils.createEmailData(EmailCreator.email, "", new ArrayList<String>());
		EmailUtils.setEamil(email, data, System.currentTimeMillis());
		return email;
	}

}
