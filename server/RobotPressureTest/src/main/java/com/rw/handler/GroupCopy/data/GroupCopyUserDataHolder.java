package com.rw.handler.GroupCopy.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.common.RobotLog;
import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

/**
 * 帮派副本个人数据
 * @author Alex
 *
 * 2016年8月16日 上午11:49:13
 */
public class GroupCopyUserDataHolder {
	
	private SynDataListHolder<UserGroupCopyMapRecord> userDataHolder = new SynDataListHolder<UserGroupCopyMapRecord>(UserGroupCopyMapRecord.class);

	/**角色每章节的剩余的进入次数 key=chaterID, value=count*/
	private Map<String, Integer> dataMap = new HashMap<String, Integer>();
	
	public void syn(MsgDataSyn dataSyn){
		RobotLog.info("同步角色帮派副本个人数据");
		userDataHolder.Syn(dataSyn);
		List<UserGroupCopyMapRecord> itemList = userDataHolder.getItemList();
		dataMap.clear();
		for (UserGroupCopyMapRecord record : itemList) {
			dataMap.put(record.getChaterID(), record.getLeftFightCount());
		}
	}

	
	
	public int getLeftFightCount(String chaterID){
		return dataMap.get(chaterID);
	}
}
