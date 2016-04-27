package com.rw.dataaccess.processor;

import com.rw.dataaccess.PlayerParam;
import com.rw.dataaccess.PlayerCoreCreation;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;

public class UserGroupProcessor implements PlayerCoreCreation<UserGroupAttributeData>{

	@Override
	public UserGroupAttributeData create(PlayerParam param) {
		UserGroupAttributeData data = new UserGroupAttributeData();
		data.setUserId(param.getUserId());
		data.setGroupId("");
		return data;
	}

}
