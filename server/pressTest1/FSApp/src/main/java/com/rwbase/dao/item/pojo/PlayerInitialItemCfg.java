package com.rwbase.dao.item.pojo;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.rwbase.dao.role.pojo.RoleCfg;

enum Separator {
	/** 一级分隔符 */
	FIRST_SEP(";"),
	/** 二级分隔符 */
	SECOND_SEP("_");

	public final String SEP_MARK;

	private Separator(String sep_mark) {
		this.SEP_MARK = sep_mark;
	}
}

/**
 * 
 * @author HC
 * @date 2015年12月21日 下午5:50:28
 * @Description
 */
public class PlayerInitialItemCfg {
	/** 配置的Id */
	private String id;
	/** 初始化英雄列表 */
	private String initHeros;
	/** 初始化穿戴法宝 */
	private String initMagic;
	/** 初始化物品列表 */
	private String initItems;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInitHeros() {
		return initHeros;
	}

	public void setInitHeros(String initHeros) {
		this.initHeros = initHeros;
	}

	public String getInitMagic() {
		return initMagic;
	}

	public void setInitMagic(String initMagic) {
		this.initMagic = initMagic;
	}

	public String getInitItems() {
		return initItems;
	}

	public void setInitItems(String initItems) {
		this.initItems = initItems;
	}

	/**
	 * <pre>
	 * 获取初始赠送的英雄列表
	 * <b>这个数据中必然不包含填表中填了modelId {@linkplain RoleCfg#getModelId()} 的数据</b>
	 * </pre>
	 * 
	 * @return
	 */
	@JsonIgnore
	public String[] getInitHeroArr() {
		String[] initHeroArr = null;
		// 初始配置
		if (initHeros != null && !initHeros.isEmpty()) {
			String[] arr0 = initHeros.split(Separator.FIRST_SEP.SEP_MARK);
			if (arr0 == null || arr0.length <= 0) {
				return null;
			}

			initHeroArr = new String[arr0.length];
			for (int i = 0, len = arr0.length; i < len; i++) {
				String arr1 = arr0[i];
				if (arr1 == null || arr1.indexOf("_") == -1) {// 如果没有下划线，或者是空串，直接过滤
					continue;
				}

				initHeroArr[i] = arr1;
			}
		}
		return initHeroArr;
	}

	/**
	 * <pre>
	 * 获取初始穿戴的法宝
	 * <b>[0]是法宝的模版Id，[1]是穿戴法宝的初始等级</b>
	 * </pre>
	 * 
	 * @return
	 */
	@JsonIgnore
	public int[] getInitMagicInfo() {
		// 初始配置
		int[] initMagicInfo = null;
		if (initMagic != null && !initMagic.isEmpty()) {
			String[] arr0 = initMagic.split(Separator.SECOND_SEP.SEP_MARK);
			if (arr0 == null || arr0.length <= 0) {
				return null;
			}

			initMagicInfo = new int[2];
			initMagicInfo[0] = Integer.parseInt(arr0[0]);
			initMagicInfo[1] = arr0.length == 2 ? Integer.parseInt(arr0[1]) : 1;
		}

		return initMagicInfo;
	}

	/**
	 * <pre>
	 * 获取初始奖励的物品
	 * 获取每个二维数组的<b>第二维是奖励物品的信息</b>
	 * [0]是模版Id，[1]是数量
	 * </pre>
	 * 
	 * @return
	 */
	@JsonIgnore
	public int[][] getInitItemArr() {
		int[][] initItemArr = null;
		// 初始配置
		if (initItems != null && !initItems.isEmpty()) {
			String[] arr0 = initItems.split(Separator.FIRST_SEP.SEP_MARK);
			if (arr0 == null || arr0.length <= 0) {
				return null;
			}

			initItemArr = new int[arr0.length][];
			for (int i = 0, len = arr0.length; i < len; i++) {
				String arr1 = arr0[i];

				String[] arr2 = arr1.split(Separator.SECOND_SEP.SEP_MARK);
				if (arr2 == null || arr2.length != 2) {
					continue;
				}

				initItemArr[i] = new int[2];
				initItemArr[i][0] = Integer.parseInt(arr2[0]);
				initItemArr[i][1] = Integer.parseInt(arr2[1]);
			}
		}
		return initItemArr;
	}
}