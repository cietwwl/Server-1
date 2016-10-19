package com.rw.handler.group.holder;

import java.util.List;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.UserGroupData;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年3月15日 下午4:28:58
 * @Description 帮派个人数据的Holder
 */
public class UserGroupDataHolder {
	private SynDataListHolder<UserGroupData> listHolder = new SynDataListHolder<UserGroupData>(UserGroupData.class);
	private UserGroupData userGroupData;

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		List<UserGroupData> list = listHolder.getItemList();
		if(list.size() > 0) {
			userGroupData = list.get(0);
		} else {
			userGroupData = null;
		}
	}

	public UserGroupData getUserGroupData() {
		return userGroupData;
	}
}