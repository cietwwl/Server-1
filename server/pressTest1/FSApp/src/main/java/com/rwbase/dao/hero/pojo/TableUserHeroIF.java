package com.rwbase.dao.hero.pojo;

import java.util.List;

public interface TableUserHeroIF {

	/**
	 * 获取主角的UserId
	 * @return
	 */
	public String getUserId();
	
	/**
	 * 获取佣兵ID列表(只读)
	 * @return
	 */
	public List<String> getHeroIds();


	

	
}
