package com.rw.dataaccess.attachment;

import java.util.List;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;

/**
 * <pre>
 * 临时对象,用于创建{@link RoleExtProperty}
 * </pre>
 * @author Jamaz
 *
 */
public class RoleExtCreateInfoWrap {

	public final RoleExtCreateInfo createInfo;
	private List<InsertRoleExtDataWrap<RoleExtProperty>> datas;

	public RoleExtCreateInfoWrap(RoleExtCreateInfo createInfo) {
		this.createInfo = createInfo;
	}

	public void setDatas(List<InsertRoleExtDataWrap<RoleExtProperty>> datas) {
		this.datas = datas;
	}

	public List<InsertRoleExtDataWrap<RoleExtProperty>> getDatas() {
		return datas;
	}
}
