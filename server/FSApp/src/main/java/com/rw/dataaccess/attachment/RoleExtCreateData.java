package com.rw.dataaccess.attachment;

import java.util.List;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;

public class RoleExtCreateData {

	public final short type;
	public final RoleExtPropertyCreator<RoleExtProperty, Object> creator;
	public final RoleExtPropertyStoreCache<RoleExtProperty> cache;
	private List<InsertRoleExtDataWrap<RoleExtProperty>> datas;

	public RoleExtCreateData(short type, RoleExtPropertyCreator<RoleExtProperty, Object> creator, RoleExtPropertyStoreCache<RoleExtProperty> cache) {
		this.creator = creator;
		this.cache = cache;
		this.type = type;
	}

	public void setDatas(List<InsertRoleExtDataWrap<RoleExtProperty>> datas) {
		this.datas = datas;
	}

	public List<InsertRoleExtDataWrap<RoleExtProperty>> getDatas() {
		return datas;
	}
}
