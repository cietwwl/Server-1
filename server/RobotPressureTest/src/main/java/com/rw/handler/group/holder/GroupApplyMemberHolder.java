package com.rw.handler.group.holder;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.GroupMemberData;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年3月15日 下午4:58:13
 * @Description 申请成员的Holder
 */
public class GroupApplyMemberHolder {
	public int version;// 版本号
	private SynDataListHolder<GroupMemberData> listHolder = new SynDataListHolder<GroupMemberData>(GroupMemberData.class);

	public void syn(MsgDataSyn msgDataSyn) {
		version = msgDataSyn.getVersion();
		listHolder.Syn(msgDataSyn);
	}
}