package com.rw.dataaccess.attachment;

import java.util.Collections;
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
	 * 检查是否需要预加载
	 * 一般发生在玩家主动加载数据(如登录)的时候进行检查
	 * 返回true表示需要对数据预加载
	 * </pre>
	 * 
	 * @param currentTimeMillis
	 * @return
	 */
	public boolean requiredToPreload(Param params);

	/**
	 * <pre>
	 * 当{@link #requiredToPreload(Object)}返回true但数据库不存在记录时
	 * 必然回调此方法创建记录，合并后批量插入到数据库
	 * 如果数据库不存在记录，逻辑不希望生成新的记录但后续需要访问这个类型{@link PlayerExtPropertyStore}
	 * 可以通过返回空列表{@link Collections#emptyList()}指定，这样可以达到在不生成记录的情况下，减少对这个类型的{@link PlayerExtPropertyStore}数据库查询操作
	 * 如此性能是最佳的
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
