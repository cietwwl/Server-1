package com.rw.handler.group.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rwproto.GroupPrayProto.PrayEntry;

/**
 * @Author HC
 * @date 2016年12月26日 下午2:58:26
 * @desc
 **/

public class GroupPrayData {
	private List<PrayEntry> entryList = new ArrayList<PrayEntry>();// 所有的祈福数据
	private Random r = new Random();

	public void setEntryList(List<PrayEntry> entryList) {
		this.entryList = entryList;
	}

	/**
	 * 随机获取一个可以赠送祈福的人
	 * 
	 * @return
	 */
	public String randomPrayUserId() {
		if (entryList.isEmpty()) {
			return "";
		}

		int index = r.nextInt(entryList.size());
		PrayEntry prayEntry = entryList.get(index);
		if (prayEntry == null) {
			return "";
		}

		return prayEntry.getMemberId();
	}
}