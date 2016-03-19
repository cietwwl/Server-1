package com.rwbase.dao.hero.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_user_hero")
@SynClass
public class TableUserHero implements TableUserHeroIF {
	@Id
	private String userId;// 用户ID
	private List<String> heroIds = new ArrayList<String>();// 已拥有英雄ID


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getHeroIds() {
		return new ArrayList<String>(heroIds);
	}

	public void setHeroIds(List<String> heroIdsP) {
		this.heroIds = heroIdsP;
	}

	public void addHeroId(String heroId){
		this.heroIds.add(heroId);
	}

	
}
