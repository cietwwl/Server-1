package com.rwbase.dao.groupsecret.creator;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretDefendRecordData;

/*
 * @author HC
 * @date 2016年5月28日 上午10:24:10
 * @Description 
 */
public class GroupSecretDefendRecordDataCreator implements DataExtensionCreator<GroupSecretDefendRecordData> {

	@Override
	public GroupSecretDefendRecordData create(String key) {
		GroupSecretDefendRecordData data = new GroupSecretDefendRecordData();
		data.setUserId(key);
		return data;
	}
}