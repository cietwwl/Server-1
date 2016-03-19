package com.rwbase.dao.group.pojo.cfg;

/*
 * @author HC
 * @date 2016年1月27日 下午4:59:18
 * @Description 帮派功能对应的数据
 */
public class GroupFunctionCfg {
	private int functionType;// 帮派功能的值
	private String postList;// 可以使用此功能的职位
	private int needGroupLevel;// 需要帮派的等级

	public int getFunctionType() {
		return functionType;
	}

	public String getPostList() {
		return postList;
	}

	public int getNeedGroupLevel() {
		return needGroupLevel;
	}
}