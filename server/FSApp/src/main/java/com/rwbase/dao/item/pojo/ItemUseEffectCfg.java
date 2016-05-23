package com.rwbase.dao.item.pojo;

/*
 * @author HC
 * @date 2016年5月18日 下午2:24:34
 * @Description 
 */
public class ItemUseEffectCfg {
	private int modelId;// 道具的ModelId
	private String combineUse;// 关联一起使用
	private String useEffectClass;// 对应的处理类
	private int directResult;// 直接使用的结果
	private String dropResult;// 掉落的结果

	public int getModelId() {
		return modelId;
	}

	public String getCombineUse() {
		return combineUse;
	}

	public String getUseEffectClass() {
		return useEffectClass;
	}

	public int getDirectResult() {
		return directResult;
	}

	public String getDropResult() {
		return dropResult;
	}
}