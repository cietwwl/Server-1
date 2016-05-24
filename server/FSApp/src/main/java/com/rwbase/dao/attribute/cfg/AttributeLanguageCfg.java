package com.rwbase.dao.attribute.cfg;

/*
 * @author HC
 * @date 2016年5月13日 下午4:47:59
 * @Description 
 */
public class AttributeLanguageCfg {
	private String id;// 名字
	private String chinese;// 中文名字
	private int attrType;// 对应的属性类型

	/**
	 * 获取英文名字
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取中文名字
	 * 
	 * @return
	 */
	public String getChinese() {
		return chinese;
	}

	/**
	 * 获取属性类型
	 * 
	 * @return
	 */
	public int getAttrType() {
		return attrType;
	}
}