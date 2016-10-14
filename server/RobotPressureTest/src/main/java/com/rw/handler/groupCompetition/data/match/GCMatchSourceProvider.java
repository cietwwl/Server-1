package com.rw.handler.groupCompetition.data.match;

import java.util.List;

/**
 * 
 * 匹配资源提供者
 * 
 * @author CHEN.P
 *
 */
public interface GCMatchSourceProvider<T extends IGCMatchSource> {

	/**
	 * 
	 * 获取可用的匹配资源
	 * 
	 * @return
	 */
	public List<T> getAvaliableSource();
}
