package com.playerdata.groupcompetition.matching;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.groupcompetition.util.GCompUtil;

/**
 * 
 * 随机匹配的过渡数据
 * 
 * @author CHEN.P
 *
 */
class RandomMatchingData {

	private String userId; // 主角的userId
	private List<String> heroIds; // 上阵的英雄id
	private boolean cancel; // 是否已经取消
	private boolean robot; // 是否机器人
	private long deadline; // 匹配超时时间
	
	public RandomMatchingData(String pUserId, List<String> pHeroIds) {
		this.userId = pUserId;
		this.heroIds = new ArrayList<String>(pHeroIds);
		this.deadline = System.currentTimeMillis() + GCompUtil.getMatchingTimeoutMillis();
	}
	
	/**
	 * 
	 * 获取主角的userId
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * 
	 * 获取上阵的英雄id
	 * 
	 * @return
	 */
	public List<String> getHeroIds() {
		return heroIds;
	}
	
	/**
	 * 
	 * 设置英雄id
	 * 
	 * @param list
	 */
	public void setHeroIds(List<String> list) {
		this.heroIds = new ArrayList<String>(list);
	}
	
	/**
	 * 
	 * 设置是否取消
	 * 
	 * @param value
	 */
	public void setCancel(boolean value) {
		this.cancel = true;
	}
	
	/**
	 * 
	 * 是否已经取消
	 * 
	 * @return
	 */
	public boolean isCancel() {
		return cancel;
	}
	
	/**
	 * 
	 * 是否机器人数据
	 * 
	 * @return
	 */
	public boolean isRobot() {
		return robot;
	}
	
	/**
	 * 
	 * 设置是否机器人数据
	 * 
	 * @param robot
	 */
	public void setRobot(boolean robot) {
		this.robot = robot;
	}
	
	/**
	 * 
	 * <pre>
	 * 获取匹配的超时时间
	 * System.currentTimeMillis()比这个时间大的话，就必须给他匹配上
	 * </pre>
	 * 
	 * @return
	 */
	public long getDeadline() {
		return deadline;
	}
}
