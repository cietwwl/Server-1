package com.bm.player;

/*
 * @author HC
 * @date 2016年2月1日 下午2:19:32
 * @Description 角色数据修改订阅者
 */
public abstract class PlayerChangePopertySubscribe implements Subscribe {

	public PlayerChangePopertySubscribe(PlayerChangePopertyObserver observer) {
		observer.addSubscribe(this);
	}
}