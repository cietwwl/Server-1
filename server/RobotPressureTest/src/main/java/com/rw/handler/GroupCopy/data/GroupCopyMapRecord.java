package com.rw.handler.GroupCopy.data;

import com.rw.dataSyn.SynItem;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;

public class GroupCopyMapRecord implements SynItem{

	private String id; // chaterID_groupID
	private String groupId; // 帮派ID
	
	private String chaterID; //对应章节id
	
	private String curLevelID;//当前章节id
	
	private double progress;
	
	private GroupCopyMapStatus status;
	
	@Override
	public String getId() {
		return id;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getChaterID() {
		return chaterID;
	}

	public String getCurLevelID() {
		return curLevelID;
	}

	public double getProgress() {
		return progress;
	}

	public GroupCopyMapStatus getStatus() {
		return status;
	}

}
