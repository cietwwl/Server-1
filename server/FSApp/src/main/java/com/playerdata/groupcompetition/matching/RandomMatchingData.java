package com.playerdata.groupcompetition.matching;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.groupcompetition.data.IGCUnit;
import com.playerdata.groupcompetition.util.GCompUtil;

/**
 * 
 * 随机匹配的过渡数据
 * 
 * @author CHEN.P
 *
 */
class RandomMatchingData implements IGCUnit {

	private String userId; // 主角的userId
	private List<String> heroIds; // 上阵的英雄id
	private volatile boolean cancel; // 是否已经取消
	private boolean robot; // 是否机器人
	private long deadline; // 匹配超时时间
	private volatile boolean removed; // 是否已经删除了
	
	private RandomMatchingData() {}
	
	private static RandomMatchingData createGeneralData(String userId, List<String> pHeroIds) {
		RandomMatchingData data = new RandomMatchingData();
		data.userId = userId;
		data.heroIds = new ArrayList<String>(pHeroIds);
		return data;
	}
	
	/**
	 * 
	 * 创建一个主动提交的随机匹配数据，这个会有一个匹配超时的数据
	 * 
	 * @param userId
	 * @param pHeroIds
	 * @return
	 */
	static RandomMatchingData createActiveSubmitData(String userId, List<String> pHeroIds) {
		RandomMatchingData data = createGeneralData(userId, pHeroIds);
		data.deadline = System.currentTimeMillis() + GCompUtil.getMatchingTimeoutMillis();
//		GCompUtil.log("随机匹配数据：{}，deadline：{}", userId, data.deadline);
		return data;
	}
	
	/**
	 * 
	 * 创建一个机器人的数据
	 * 
	 * @param userId
	 * @param pHeroIds
	 * @return
	 */
	static RandomMatchingData createRobotData(String userId, List<String> pHeroIds) {
		RandomMatchingData data = createGeneralData(userId, pHeroIds);
		data.robot = true;
		return data;
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
		this.cancel = value;
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
	
	/**
	 * 
	 * @param pDeadline
	 */
	public void setDeadline(long pDeadline) {
		this.deadline = pDeadline;
	}

	@Override
	public String toString() {
		return "RandomMatchingData [userId=" + userId + ", cancel=" + cancel + ", robot=" + robot + "]";
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
}
