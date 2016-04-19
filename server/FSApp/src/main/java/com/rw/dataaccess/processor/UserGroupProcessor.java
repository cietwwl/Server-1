package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;

public class UserGroupProcessor implements PlayerCreatedProcessor<UserGroupAttributeData>{

	@Override
	public UserGroupAttributeData create(PlayerCreatedParam param) {
		UserGroupAttributeData data = new UserGroupAttributeData();
		data.setUserId(param.getUserId());
		data.setGroupId("");
		return data;
	}

}
