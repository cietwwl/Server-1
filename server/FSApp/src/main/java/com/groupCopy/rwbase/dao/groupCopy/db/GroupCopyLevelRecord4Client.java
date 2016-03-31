package com.groupCopy.rwbase.dao.groupCopy.db;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.SynClass;



@SuppressWarnings("unused")
@SynClass
public class GroupCopyLevelRecord4Client{

	@Id
	private String id; // 唯一id
	
	private GroupCopyLevelRecord groupCopyLevelRecord;
	
	private UserGroupCopyLevelRecord userGroupCopyLevelRecord;
	
	public GroupCopyLevelRecord4Client(GroupCopyLevelRecord groupCopyLevelRecordP,UserGroupCopyLevelRecord userGroupCopyLevelRecordP){
	
		this.id = groupCopyLevelRecordP.getId();
		this.groupCopyLevelRecord = groupCopyLevelRecordP;
		this.userGroupCopyLevelRecord = userGroupCopyLevelRecordP;		
	
	}
	
	
}
