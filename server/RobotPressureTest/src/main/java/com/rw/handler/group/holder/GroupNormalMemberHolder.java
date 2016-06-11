package com.rw.handler.group.holder;

import java.util.List;
import java.util.Random;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.GroupMemberData;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年3月15日 下午4:58:41
 * @Description 帮派正式成员的Holder
 */
public class GroupNormalMemberHolder {
	private int version;
	private SynDataListHolder<GroupMemberData> listHolder = new SynDataListHolder<GroupMemberData>(GroupMemberData.class);

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		version = msgDataSyn.getVersion();
	}

	/**
	 * 获取成员信息版本号
	 * 
	 * @return
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 随机一个成员Id出来
	 * 
	 * @param r
	 * @return
	 */
	public String getRandomMemberId(Random r) {
		List<GroupMemberData> itemList = listHolder.getItemList();
		if (itemList == null || itemList.isEmpty()) {
			return "";
		}

		int index = r.nextInt(itemList.size());
		GroupMemberData groupMemberData = itemList.get(index);
		if (groupMemberData == null) {
			return "";
		}

		return groupMemberData.getUserId();
	}
}