package com.rw.handler.group.data;

import com.rw.dataSyn.SynItem;

/*
 * @author HC
 * @date 2016年3月15日 下午4:25:30
 * @Description 帮派基础数据
 */
public class GroupBaseData implements SynItem {
	private String id;// 唯一的Id
	private int groupLevel;// 创建帮派的等级<byte>
	private int groupExp;// 当前帮派的经验
	private String groupName;// 帮派的名字(数据库中是以Name来作为数据表的唯一索引)
	private String iconId;// 帮派的图标Id
	private int supplies;// 帮派的储备物资
	private String announcement;// 帮派内部公告 Base64加密
	private String declaration;// 帮派的宣言 Base64加密
	private int validateType;// 帮派的验证类型<byte>
	private int applyLevel;// 申请进入帮派的等级<short>
	private long dismissTime;// 帮派申请解散的时间

	public String getId() {
		return id;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public int getGroupExp() {
		return groupExp;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getIconId() {
		return iconId;
	}

	public int getSupplies() {
		return supplies;
	}

	public String getAnnouncement() {
		return announcement;
	}

	public String getDeclaration() {
		return declaration;
	}

	public int getValidateType() {
		return validateType;
	}

	public int getApplyLevel() {
		return applyLevel;
	}

	public long getDismissTime() {
		return dismissTime;
	}
}