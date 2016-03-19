package com.rw.handler.group.data;

import com.rw.dataSyn.SynItem;

/*
 * @author HC
 * @date 2016年3月15日 下午4:25:50
 * @Description 帮派成员数据
 */
public class GroupMemberData implements SynItem {

	private String userId;// 对应的角色Id
	private int post;// 帮派中的职位<byte>
	private long logoutTime;// 登出的时间
	// ///////////////////////////////////////成员基础信息
	private String name;// 成员名字
	private int level;// 成员的等级<short>
	private String headId;// 头像的Id
	private int vipLevel;// 成员的Vip等级<byte>
	private int contribution;// 个人的贡献
	private String templateId = "";// 成员的模版Id
	// //////////////////////////////////////申请帮派时的数据
	private int fighting;// 战斗力
	private long applyTime;// 申请加入帮派的时间
	private long receiveTime;// 接受加入帮派的时间

	@Override
	public String getId() {
		return userId;
	}

	public String getUserId() {
		return userId;
	}

	public int getPost() {
		return post;
	}

	public long getLogoutTime() {
		return logoutTime;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public String getHeadId() {
		return headId;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getContribution() {
		return contribution;
	}

	public String getTemplateId() {
		return templateId;
	}

	public int getFighting() {
		return fighting;
	}

	public long getApplyTime() {
		return applyTime;
	}

	public long getReceiveTime() {
		return receiveTime;
	}
}