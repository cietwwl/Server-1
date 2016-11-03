package com.rw.handler.group.holder;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.GroupBaseData;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年3月15日 下午4:28:21
 * @Description 帮派基础数据的Holder
 */
public class GroupBaseDataHolder {
	private int version;
	private SynDataListHolder<GroupBaseData> listHolder = new SynDataListHolder<GroupBaseData>(GroupBaseData.class);
	private GroupBaseData groupBaseData;

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		version = msgDataSyn.getVersion();
		groupBaseData = listHolder.getItemList().get(0);
	}

	/**
	 * 获取帮派基础数据的版本号
	 * 
	 * @return
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 获取帮派等级
	 * 
	 * @return
	 */
	public int getGroupLevel() {
		if (groupBaseData == null) {
			return 0;
		}
		return groupBaseData.getGroupLevel();
	}

	/**
	 * 
	 * 获取帮派的等级
	 * 
	 * @return
	 */
	public String getGroupId() {
		if (groupBaseData == null) {
			return null;
		}

		return groupBaseData.getId();
	}
}