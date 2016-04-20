package com.rw.dataaccess.processor;

import java.util.ArrayList;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.email.TableEmail;

public class EmailProcessor implements PlayerCreatedProcessor<TableEmail>{

	@Override
	public TableEmail create(PlayerCreatedParam param) {
		TableEmail email = new TableEmail();
		email.setUserId(param.getUserId());
		EmailData data = EmailUtils.createEmailData("10003", "", new ArrayList<String>());
		EmailUtils.setEamil(email, data, System.currentTimeMillis());
		return email;
	}

}
