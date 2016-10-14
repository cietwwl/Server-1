package com.rwbase.dao.praise.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.util.DateUtils;

/**
 * @Author HC
 * @date 2016年10月13日 下午4:14:28
 * @desc
 **/

@SynClass
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class PraiseData {
	@Id
	@IgnoreSynField
	private String userId;// 数据属于角色的Id
	@IgnoreSynField
	private long lastTime;// 更新的时间
	private List<String> praiseIdList;// 已经赞美的人的Id列表

	public PraiseData() {
		praiseIdList = new ArrayList<String>();
	}

	public PraiseData(String userId) {
		this();
		this.userId = userId;
	}

	/**
	 * 获取UserId
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 获取上一个刷新的时间点
	 * 
	 * @return
	 */
	public long getLastTime() {
		return lastTime;
	}

	/**
	 * 获取已经点赞过的角色Id列表
	 * 
	 * @return
	 */
	public List<String> getPraiseIdList() {
		return new ArrayList<String>(praiseIdList);
	}

	// ==================================================逻辑处理区域

	/**
	 * 检查是否已经点赞过某个人
	 * 
	 * @param userId
	 * @return
	 */
	public synchronized boolean hasPraisedSomeone(String userId) {
		if (praiseIdList.isEmpty()) {
			return false;
		}

		return praiseIdList.contains(userId);
	}

	/**
	 * 增加给点赞的人Id
	 * 
	 * @param userId
	 */
	public synchronized void addPraise(String userId) {
		praiseIdList.add(userId);
	}

	/**
	 * 检查是否要重置数据
	 * 
	 * @param now 当前的时间
	 * @return 返回是否有更新数据
	 */
	public synchronized boolean checkOrResetData(long now) {
		if (!DateUtils.isResetTime(5, 0, 0, lastTime)) {
			return false;
		}

		lastTime = now;
		praiseIdList.clear();
		return true;
	}
}