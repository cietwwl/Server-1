package com.rw.dataaccess.attachment;

import java.util.List;
import com.rw.fsutil.cacheDao.attachment.RoleExtProperty;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public interface RoleExtPropertyCreator<T extends RoleExtProperty,Param> {


	/**
	 * <pre>
	 * 获取开放等级类型，如返回null，表示没有开放等级的限制
	 * 这个类型是用来区分数据是否加载，如果返回类型不正确，会导致数据错误被加载(占用内存)，或者没有加载(影响效率)
	 * </pre>
	 * 
	 * @return
	 */
	public eOpenLevelType getOpenLevelType();

	/**
	 * <pre>
	 * 检查是否到了开放时间
	 * 在开放时间内或者没有开放时间限制，返回true
	 * </pre>
	 * 
	 * @param currentTimeMillis
	 * @return
	 */
	public boolean validateOpenTime(long currentTimeMillis);

	/**
	 * <pre>
	 * 首次创建
	 * 当数据库没有任何记录的时候会调用此方法
	 * </pre>
	 * @param params
	 * @return
	 */
	public List<T> firstCreate(Param params);

	/**
	 * <pre>
	 * 在数据库已有记录的情况下，会在特定时间点(或事件)进行检查是否需要创建，如玩家登录
	 * 后续创建可能是基于不同的条件、或者配置表的改动
	 * 如没有记录需要创建，返回null即可
	 * </pre>
	 * @param store
	 * @param params
	 * @return
	 */
	public List<T> checkAndCreate(PlayerExtPropertyStore<T> store, Param params);

}
