package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.email.TableEmail;

public class EmailProcessor implements PlayerCreatedProcessor<TableEmail>{

	@Override
	public TableEmail create(PlayerCreatedParam param) {
		TableEmail email = new TableEmail();
		email.setUserId(param.getUserId());
		return email;
	}

}
