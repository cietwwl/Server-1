package com.common.text;

/**
 * <pre>
 * 富文本协议：	
 * 标签	效果
 * [b]文本[/b]	加粗
 * [i]文本[/i]	斜体
 * [u]文本[/u]	下划线：
 * [s]文本[/s]	删除线
 * [sub]文本[/sub]	向下缩小
 * [sup]文本[/sup]	向上缩小
 * [emo]xx[/emo]	表情，xx是表情索引，从01开始
 * [url=http://www.kl321.com/]链接[/url]	链接
 * [c?]文本[-]	变色，c?是颜色符号，参看【颜色表】
 * [ffffff]文本[-]	变色，ffffff是颜色值
 * [att type="" id=""][/att] 附件
 * 
 * @creation 2016-07-18 20:27
 * @author CHEN.P
 * </pre>
 */
public enum HyperTextType {

	/**
	 * 加粗
	 */
	BOLD("[b]", "[/b]"),
	/**
	 * 斜体
	 */
	ITALIC("[i]", "[/i]"),
	/**
	 * 下划线
	 */
	UNDERLINE("[u]", "[/u]"),
	/**
	 * 删除线
	 */
	STRICKOUT("[s]","[/s]"),
	/** 
	 * 向下缩小  
	 */
	SUB("[sub]", "[/sub]"),
	/** 
	 * 向上缩小 
	 */
	SUP("[sup]", "[/sup]"),
	/** 
	 * 表情
	 * 标签包含表情的标识定义：例如[emo]01[/emo]代表:-)
	 */
	EMO("[emo]", "[/emo]"),
	/** 
	 * 链接 
	 */
	URL("[url=]", "[/url]"),
	/** 
	 * 颜色
	 * 标签的内容直接为rgb：例如[ff0000][-]代表红色，文字的内容在中间
	 */
	COLOR("[]", "[-]"),
	/**
	 * 附件
	 * 需要属性：
	 * 1）type：附件类型(1=英雄，2=道具，3=法宝）
	 * 2）id：附件的templateId
	 * 3）lv：等级
	 * 例如，英雄：
	 * [att type="1" id="100001_1"]英雄名字[/att]
	 */
	ATTACHMENT("[att]", "[/att]"),
	;
	/**
	 * 开始的标签
	 */
	public final String startSign;
	/**
	 * 结束的标签
	 */
	public final String endSign;
	
	private HyperTextType(String pStartSign, String pEndSign) {
		this.startSign = pStartSign;
		this.endSign = pEndSign;
	}
}
