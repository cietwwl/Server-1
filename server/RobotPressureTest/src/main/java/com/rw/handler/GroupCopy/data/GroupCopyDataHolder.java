package com.rw.handler.GroupCopy.data;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 帮派副本数据
 * @author Alex
 *
 * 2016年8月5日 上午11:28:18
 */
public class GroupCopyDataHolder {
	
	private final AtomicInteger lvDataVersion = new AtomicInteger(0);
	
	public int getLvDataVersion(){
		return lvDataVersion.get();
	}

	
}
