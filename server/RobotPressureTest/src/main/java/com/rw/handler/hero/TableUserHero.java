package com.rw.handler.hero;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TableUserHero implements SynItem{
	private String userId;// 用户ID
	private List<String> heroIds = new ArrayList<String>();// 已拥有英雄ID
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return userId;
	}

	public List<String> getHeroIds() {
		return heroIds;
	}
}
