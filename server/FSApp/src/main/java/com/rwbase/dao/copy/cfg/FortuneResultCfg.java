package com.rwbase.dao.copy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FortuneResultCfg {
	
	private String id;
	
	private int upLimit;	//服务端掉落上限

	public String getId() {
		return id;
	}

	public int getUpLimit() {
		return upLimit;
	}
}
