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

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
	}

	public UserGroupData getUserGroupData() {
		List<UserGroupData> itemList = listHolder.getItemList();
		if (itemList == null || itemList.isEmpty()) {
			return null;
		}

		return itemList.get(0);
	}
}