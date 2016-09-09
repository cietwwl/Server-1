package com.playerdata.groupcompetition.data.match;

import java.util.List;

/**
 * 
 * 匹配资源提供者
 * 
 * @author CHEN.P
 *
 */
public interface CompetitionMatchSourceProvider<T extends CompetitionMatchSource> {

	/**
	 * 
	 * 获取可用的匹配资源
	 * 
	 * @return
	 */
	public List<T> getAvaliableSource();
}
