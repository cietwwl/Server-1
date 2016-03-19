package com.bm.rank.peakArena;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.bm.arena.ArenaConstant;
import com.bm.rank.arena.FightingMember;

/**
 * 巅峰竞技场扩展属性
 * @author Jamaz
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeakArenaExtAttribute extends FightingMember{

	public PeakArenaExtAttribute() {
		super(ArenaConstant.PEAK_ARENA_FIGHTING_TIMEOUT);
		// TODO Auto-generated constructor stub
	}

	private int winCount;		//连胜次数
	private int level;			//等级
	private String name;		//名字
	private String headImage;	//头像
	private int fighting;		//战力


	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

}
