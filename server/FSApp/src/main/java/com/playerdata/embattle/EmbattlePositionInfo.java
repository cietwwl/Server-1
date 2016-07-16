package com.playerdata.embattle;

import java.util.ArrayList;
import java.util.List;

/*
 * @author HC
 * @date 2016年7月14日 下午6:30:06
 * @Description 
 */
public class EmbattlePositionInfo {
	private String key = "0";// 阵容自定义的Key。例如巅峰竞技场，有三队阵容，每一对阵容都会不一样
	private List<EmbattleHeroPosition> pos;// 所有英雄的站位

	public EmbattlePositionInfo() {
		pos = new ArrayList<EmbattleHeroPosition>();
	}

	public EmbattlePositionInfo(String key, List<EmbattleHeroPosition> pos) {
		this.key = key;
		this.pos = pos;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<EmbattleHeroPosition> getPos() {
		return pos;
	}

	public void setPos(List<EmbattleHeroPosition> pos) {
		this.pos = pos;
	}

	/**
	 * 获取英雄站位
	 * 
	 * @param heroId
	 * @return
	 */
	public int getHeroPos(String heroId) {
		for (int i = 0, size = pos.size(); i < size; i++) {
			EmbattleHeroPosition heroPos = pos.get(i);
			if (heroPos.getId().equals(heroId)) {
				return heroPos.getPos();
			}
		}

		return 0;
	}
}