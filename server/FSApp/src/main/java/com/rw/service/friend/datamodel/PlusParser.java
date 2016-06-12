package com.rw.service.friend.datamodel;

public interface PlusParser {

	/**
	 * 把策划配置的文本解析成计算器
	 * @param text
	 * @return
	 */
	public PlusCalculator parse(String text);
}
