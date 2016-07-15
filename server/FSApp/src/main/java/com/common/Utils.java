package com.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rwbase.dao.battletower.pojo.RewardInfo;

/*
 * @author HC
 * @date 2015年11月16日 下午4:17:37
 * @Description 
 */
public class Utils {
	/**
	 * <pre>
	 * 解析奖励字符串 
	 * <b>分隔符全部是英文状态下的</b>
	 * </pre>
	 * 
	 * @param rewardStr 奖励的信息
	 * @param firstSeparator 一级分隔符；例如填入规则为1000_1,1002_2那么','就是一级分割。默认是';'
	 * @param secondSeparator 二级分隔符；例如填入规则为1000_1,1002_2那么'_'就是一级分割。默认是':'
	 * @return
	 */
	public static List<RewardInfo> parseRewardInfo(String rewardStr, String firstSeparator, String secondSeparator) {
		if (rewardStr == null || rewardStr.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		if (firstSeparator == null || firstSeparator.isEmpty()) {
			firstSeparator = ";";
		}

		if (secondSeparator == null || secondSeparator.isEmpty()) {
			secondSeparator = ":";
		}

		String[] temp0 = rewardStr.split(firstSeparator);
		if (temp0.length <= 0) {
			return Collections.EMPTY_LIST;
		}

		List<RewardInfo> rewardList = new ArrayList<RewardInfo>();
		for (int i = 0, size = temp0.length; i < size; i++) {
			String temp1 = temp0[i];
			if (temp1 == null || temp1.isEmpty()) {
				continue;
			}

			String[] temp2 = temp1.split(secondSeparator);
			if (temp2.length != 2) {
				continue;
			}

			RewardInfo rewardInfo = new RewardInfo();
			rewardInfo.setId(Integer.parseInt(temp2[0]));
			rewardInfo.setCount(Integer.parseInt(temp2[1]));
			rewardList.add(rewardInfo);
		}

		return rewardList;
	}
	
	
	/**
	 * 提供精确的除法运算
	 * @param v1 分子
	 * @param v2 分母
	 * @param scale 表示精确到小数点后几位，要大于0
	 * @return
	 */
	public static double div(double v1, double v2, int scale){
		if(scale < 0){
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}