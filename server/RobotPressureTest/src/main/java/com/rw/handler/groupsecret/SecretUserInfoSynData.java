package com.rw.handler.groupsecret;

import com.rw.dataSyn.SynItem;

public class SecretUserInfoSynData implements SynItem{
	private String userId;// 角色的Id
	
	private int keyCount;// 当前钥石数量
	
	
	
	
	public int getKeyCount() {
		return keyCount;
	}




	public void setKeyCount(int keyCount) {
		this.keyCount = keyCount;
	}




	@Override
	public String getId() {
		
		return userId;
	}

}
