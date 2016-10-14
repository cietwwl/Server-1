package com.rw.dataaccess.attachment;

import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.attachment.RoleExtPropertyStoreCache;

/**
 * <pre>
 * 角色扩展属性创建所需的相关信息
 * 每个{@link RoleExtPropertyStoreCache}类型游戏世界之缓存只有一份
 * </pre>
 * @author Jamaz
 *
 */
public class RoleExtCreateInfo {

	public final short type;
	public final RoleExtPropertyCreator<RoleExtProperty, Object> creator;
	public final RoleExtPropertyStoreCache<RoleExtProperty> cache;

	public RoleExtCreateInfo(short type, RoleExtPropertyCreator<RoleExtProperty, Object> creator, RoleExtPropertyStoreCache<RoleExtProperty> cache) {
		super();
		this.type = type;
		this.creator = creator;
		this.cache = cache;
	}

}
