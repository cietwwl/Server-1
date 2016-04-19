package com.rw.dataaccess;

public interface PlayerCreatedProcessor<T> {

	/**
	 * 通过玩家初始化参数创建指定对象
	 * @param param
	 * @return
	 */
	public T create(PlayerCreatedParam param);
	
}
