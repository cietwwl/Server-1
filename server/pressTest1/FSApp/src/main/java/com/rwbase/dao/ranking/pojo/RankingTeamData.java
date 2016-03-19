package com.rwbase.dao.ranking.pojo;

import java.util.List;

public class RankingTeamData {
	private RankingMagicData magicData;
	private List<RankingHeroData> heroList;
	public RankingMagicData getMagicData() {
		return magicData;
	}
	public void setMagicData(RankingMagicData magicData) {
		this.magicData = magicData;
	}
	public List<RankingHeroData> getHeroList() {
		return heroList;
	}
	public void setHeroList(List<RankingHeroData> heroList) {
		this.heroList = heroList;
	}
}
