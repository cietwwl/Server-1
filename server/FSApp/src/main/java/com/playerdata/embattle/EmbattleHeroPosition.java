package com.playerdata.embattle;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年7月14日 下午6:29:11
 * @Description 
 */
@SynClass
public class EmbattleHeroPosition {
	private String id;// 角色Id
	private int pos;// 站位

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "EmbattleHeroPosition [id=" + id + ", pos=" + pos + "]";
	}
	
	
}