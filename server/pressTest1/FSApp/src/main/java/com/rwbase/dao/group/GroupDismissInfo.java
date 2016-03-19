package com.rwbase.dao.group;

/*
 * @author HC
 * @date 2016年1月26日 下午8:01:07
 * @Description 
 */
public class GroupDismissInfo {
	private String groupId;// 帮派Id
	private long dismissTime;// 解散的时间

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public long getDismissTime() {
		return dismissTime;
	}

	public void setDismissTime(long dismissTime) {
		this.dismissTime = dismissTime;
	}
}