package com.rwbase.dao.chat.pojo.cfg;

/**
 * @Author HC
 * @date 2016年12月17日 上午9:43:00
 * @desc 聊天的违规配置
 **/

public class ChatIllegalCfg {
	private int level;// 限制的等级
	private int vipLevel;// 限制的VIP等级
	private int interval;// 发言间隔
	private int illegalInterval;// 违规频率
	private int repeatedTimes;// 重复内容次数

	/**
	 * 获取所属的分段最低的等级ID
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * 获取所属VIP分段的最低的VIP等级
	 * 
	 * @return
	 */
	public int getVipLevel() {
		return vipLevel;
	}

	/**
	 * 获取发言间隔
	 * 
	 * @return
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * 违规频率
	 * 
	 * @return
	 */
	public int getIllegalInterval() {
		return illegalInterval;
	}

	/**
	 * 获取重复发言触发违规的次数
	 * 
	 * @return
	 */
	public int getRepeatedTimes() {
		return repeatedTimes;
	}
}