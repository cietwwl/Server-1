package com.rw.handler.group.holder;

import java.util.List;
import java.util.Random;

import com.rw.dataSyn.SynDataListHolder;
import com.rw.handler.group.data.GroupSkillItem;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年3月19日 下午9:33:30
 * @Description 帮派研发技能数据Holder
 */
public class GroupResearchSkillDataHolder {
	private int version;
	private SynDataListHolder<GroupSkillItem> listHolder = new SynDataListHolder<GroupSkillItem>(GroupSkillItem.class);

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);
		version = msgDataSyn.getVersion();
	}

	/**
	 * 获取帮派研发技能的版本号
	 * 
	 * @return
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 获取一个随机的技能
	 * 
	 * @param r
	 * @return
	 */
	public GroupSkillItem getRandomSkill(Random r) {
		List<GroupSkillItem> itemList = listHolder.getItemList();
		if (itemList == null || itemList.isEmpty()) {
			return null;
		}

		int index = r.nextInt(itemList.size());
		return itemList.get(index);
	}
}