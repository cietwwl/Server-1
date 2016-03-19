package com.rw.handler.group.holder;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.GroupLog;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年3月15日 下午4:32:55
 * @Description 帮派日志Holder
 */
public class GroupLogHolder {
	private SynDataListHolder<GroupLog> listHolder = new SynDataListHolder<GroupLog>(GroupLog.class);

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
	}
}