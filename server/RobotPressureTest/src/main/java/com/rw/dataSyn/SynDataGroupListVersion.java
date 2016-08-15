package com.rw.dataSyn;

//客户端传过来给服务端判断版本号
public class SynDataGroupListVersion {

	//同步的id
	private String groupId;
	//同步的版本
	private int version;

	public String getGroupId() {
		return groupId;
	}

	public int getVersion() {
		return version;
	}
}
