package com.rw.dataaccess;

import com.rw.fsutil.cacheDao.loader.DataCreator;

/**
 * 
 * <pre>
 * 玩家的核心创建部分，在玩家创建的同时一并创建完成
 * 如抛出异常或者返回null，玩家创建失败
 * 需要注意此接口暂不支持对老玩家及已创建玩家的维护(后续会在玩家登录的时候进行检查和创建)
 * 即已创建角色缺少对此接口的{@link #create(PlayerParam)}调用
 * </pre>
 * 
 * @author Jamaz
 *
 * @param <T>
 */
public interface PlayerCoreCreation<T> extends DataCreator<T, PlayerParam>{

	/**
	 * 通过玩家初始化参数创建指定对象
	 * @param param
	 * @return
	 */
	public T create(PlayerParam param);
	
}
