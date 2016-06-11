package com.rw.handler.group.data;

import java.util.List;

import com.rwproto.GroupPersonalProto.GroupSimpleInfo;

/*
 * @author HC
 * @date 2016年3月19日 下午9:36:19
 * @Description 帮派请求之后暂时缓存的数据
 */
public class GroupRequestCacheData {
	private List<GroupSimpleInfo> simpleInfoList;// 推荐帮派的数据

	/**
	 * 获取推荐的列表
	 * 
	 * @return
	 */
	public List<GroupSimpleInfo> getSimpleInfoList() {
		return simpleInfoList;
	}

	/**
	 * 设置推荐的列表
	 * 
	 * @param simpleInfoList
	 */
	public void setSimpleInfoList(List<GroupSimpleInfo> simpleInfoList) {
		this.simpleInfoList = simpleInfoList;
	}
}