package com.bm.rank.populatity;

/**
 * @Author HC
 * @date 2016年10月13日 下午4:59:15
 * @desc
 **/

public class PopularityData {
	private String userId;// 角色的Id
	private int praise;// 获取点赞的数量

	public PopularityData() {
	}

	public PopularityData(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取角色的Id
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 获取当前点赞的数量
	 * 
	 * @return
	 */
	public int getPraise() {
		return praise;
	}

	/**
	 * 设置最新的点赞数量
	 * 
	 * @param praise
	 */
	public synchronized void setPraise(int praise) {
		this.praise = praise;
	}
}