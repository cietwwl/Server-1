package com.playerdata.teambattle.cfg;
import com.common.BaseConfig;

public class TeamStoreCfg extends BaseConfig {
	private int id; //兑换id
	private int goodsId; //物品ID
	private int goodsNumber; //每次兑换的物品数量
	private String name; //物品名称
	private int score; //兑换所需的组队积分

	public int getId() {
		return id;
	}
	
	public int getGoodsId() {
		return goodsId;
	}
	
	public int getGoodsNumber() {
		return goodsNumber;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}

}
