package com.rwbase.dao.tower.pojo;

import com.rwproto.TowerServiceProtos.eTowerDeadType;

public interface TowerHeroChangeIF {
	/**
	 * 模型id
	 * @return
	 */
	public String getRoleId();
	/**
	 * 主角减少生命  佣兵剩余生命
	 * @return
	 */
	public int getReduceLife();
	/**
	 * 主角减少能量  佣兵剩余能量
	 * @return
	 */
	public int getReduceEnegy();
	/**
	 * 生存或死亡
	 * @return
	 */
	public eTowerDeadType getIsDead();
}
