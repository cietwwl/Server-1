package com.bm.rank.populatity;

/**
 * @Author HC
 * @date 2016年10月13日 下午4:51:59
 * @desc
 **/

public class PopularityRankComparable implements Comparable<PopularityRankComparable> {
	private int praise;// 点赞的数量

	@Override
	public int compareTo(PopularityRankComparable o) {
		return this.praise - o.praise;
	}

	/**
	 * 获取个人当前获取的点赞数
	 * 
	 * @return
	 */
	public int getPraise() {
		return praise;
	}

	/**
	 * 设置新的点赞数
	 * 
	 * @param praise
	 */
	public void setPraise(int praise) {
		this.praise = praise;
	}
}