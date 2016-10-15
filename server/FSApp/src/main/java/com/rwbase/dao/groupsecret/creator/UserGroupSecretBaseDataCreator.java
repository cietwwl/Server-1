package com.rwbase.dao.groupsecret.creator;

import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;

/*
 * @author HC
 * @date 2016年5月28日 上午10:23:00
 * @Description 
 */
public class UserGroupSecretBaseDataCreator implements DataExtensionCreator<UserGroupSecretBaseData> {

	@Override
	public UserGroupSecretBaseData create(String key) {
		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		UserGroupSecretBaseData data = new UserGroupSecretBaseData();
		data.setUserId(key);
		data.setKeyCount(uniqueCfg == null ? 0 : uniqueCfg.getInitKeyNum());
		data.setLastResetTime(DateUtils.getSecondLevelMillis());
		return data;
	}
}