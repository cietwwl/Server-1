package com.rw.handler.group.holder;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.GroupMemberData;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年3月15日 下午4:58:41
 * @Description 帮派正式成员的Holder
 */
public class GroupNormalMemberHolder {
	private SynDataListHolder<GroupMemberData> listHolder = new SynDataListHolder<GroupMemberData>(GroupMemberData.class);

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
	}
}