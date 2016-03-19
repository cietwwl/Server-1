package com.rwbase.dao.guildSecretArea.projo;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.SaveAsJson;

@SynClass
public class SecretUserSource {
	public SecretUserSource(){
	}
	private String userId;//玩家
	@SaveAsJson
	private List<SourceType> sourceList;//获取的秘境资源集合
	public List<SourceType> getSourceList() {
		return sourceList;
	}
	public void setSourceList(List<SourceType> sourceList) {
		this.sourceList = sourceList;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
