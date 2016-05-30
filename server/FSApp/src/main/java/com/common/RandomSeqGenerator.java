package com.common;

//客户端的实现需要与服务器保持一致
public class RandomSeqGenerator {
	/**
	 * 返回－1表示无效
	 * 
	 * @return
	 */
	public int nextNum() {
		int[] seqList = currentPlan;
		if (seqList == null)
			return -1;
		if (seqIndex < 0 || seqIndex >= seqList.length)
			return -1;

		int result = seqList[seqIndex];

		int seqSize = seqList.length;
		if (seqIndex + 1 < seqSize) {// 当前组未用完
			seqIndex++;
		} else {// 当前组已经用完
			seqIndex = 0;
			if (findNextGroup() <= 0) {
				return -1;
			}
		}

		return result;
	}

	public RandomSeqGenerator(int seed, int[] seqPlanIdList, ISeqPlanHelper helper, int seedRange) {
		this.seqPlanIdList = seqPlanIdList;
		this.helper = helper;
		this.seedRange = seedRange;
		this.tmpseed = seed;

		int seqSize = findNextGroup();
		if (seqSize > 0) {
			// startIndex
			seqIndex = GeneratePsudoRandomSeq(seed, SeqCtl2, seqSize);
		}
	}
	
	public void ChangeSeqPlanIdList(int[] seqPlanIdList){
		if (seqPlanIdList == null || seqPlanIdList.length <= 0){
			return;
		}
		this.seqPlanIdList = seqPlanIdList;
		int groupSize = seqPlanIdList.length;
		int groupIndex = tmpseed % groupSize;
		int planId = seqPlanIdList[groupIndex];
		currentPlan = helper.getPlan(planId);
		if (currentPlan != null){
			seqIndex = seqIndex % currentPlan.length;
		}
	}

	/**
	 * 返回－1表示无效
	 * 
	 * @return
	 */
	private int findNextGroup() {
		if (tmpseed < 0)
			return -1;
		if (seedRange <= 0)
			return -1;

		int groupSize = seqPlanIdList.length;
		if (groupSize <= 0)
			return -1;

		tmpseed = GeneratePsudoRandomSeq(tmpseed, SeqCtl1, seedRange);
		int groupIndex = tmpseed % groupSize;
		int planId = seqPlanIdList[groupIndex];

		currentPlan = helper.getPlan(planId);
		if (currentPlan == null)
			return -1;

		int seqSize = currentPlan.length;

		if (seqSize <= 0)
			return -1;

		return seqSize;
	}

	/**
	 * 
	 * @param seed
	 *            非负数
	 * @param ctl
	 *            大于零的质数
	 * @param range
	 *            应该大于零
	 * @return
	 */
	private int GeneratePsudoRandomSeq(int seed, int ctl, int range) {
		return (seed * ctl + deltaCtl) % range;
	}

	private final static int SeqCtl1 = 19;
	private final static int SeqCtl2 = 23;
	private final static int deltaCtl = 31;

	private int[] seqPlanIdList;
	private ISeqPlanHelper helper;

	private int tmpseed;
	private int seedRange;
	private int[] currentPlan;
	private int seqIndex;
}
