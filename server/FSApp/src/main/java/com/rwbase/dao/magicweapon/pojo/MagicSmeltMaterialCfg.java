package com.rwbase.dao.magicweapon.pojo;

import java.util.HashMap;

/**
 * @Author HC
 * @date 2016年10月6日 下午5:15:34
 * @desc 法宝熔炼需要的材料
 **/

public class MagicSmeltMaterialCfg {
	private int id;
	private int aptitude;// 资质
	private String goods;// 需要的材料
	private HashMap<Integer, Integer> needMaterialMap = new HashMap<Integer, Integer>();

	public int getId() {
		return id;
	}

	public int getAptitude() {
		return aptitude;
	}

	public String getGoods() {
		return goods;
	}

	public HashMap<Integer, Integer> getNeedMaterialMap() {
		return needMaterialMap;
	}

	public void setNeedMaterialMap(HashMap<Integer, Integer> needMaterialMap) {
		this.needMaterialMap = needMaterialMap;
	}
	
	
}