package com.bm.rank.group.membernum;

/*
 * @author HC
 * @date 2016年1月20日 上午11:40:20
 * @Description 排行榜成员数量比较，谁的多谁就靠前
 */
public class GroupMemberNumComparable implements Comparable<GroupMemberNumComparable> {
	private int memberNum;// 成员的数量

	@Override
	public int compareTo(GroupMemberNumComparable o) {
		// int result = memberNum - o.memberNum;
		// if (result > 0) {
		// return -1;
		// } else if (result < 0) {
		// return 1;
		// }

		return o.memberNum - memberNum;
	}

	public int getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(int memberNum) {
		this.memberNum = memberNum;
	}
}