package com.common;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/*
 * @author HC
 * @date 2015年9月7日 下午6:06:02
 * @Description 随机使用的计算权重的方式
 */
public class Weight<T> {
	// 全权重唯一一个随机类
	private static final Random r = new Random();// 随机数

	// 类变量
	private Map<T, Integer> proMap;// 权重列表
	private int totalPro;// 总概率

	public Weight(Map<T, Integer> proMap) {
		if (proMap == null) {
			proMap = new HashMap<T, Integer>();
		}

		this.proMap = proMap;

		for (Entry<T, Integer> e : proMap.entrySet()) {
			if (e.getValue() <= 0) {
				continue;
			}

			this.totalPro += e.getValue();
		}
	}

	/**
	 * 获取一个随机结果
	 * 
	 * @return
	 */
	public T getRanResult() {
		int rNum = r.nextInt(totalPro);// 随机数
		int minValue = 0;// 最小值
		int maxValue = 0;// 最大值
		for (Entry<T, Integer> e : proMap.entrySet()) {
			maxValue += e.getValue();
			if (rNum >= minValue && rNum < maxValue) {
				return e.getKey();
			}

			minValue = maxValue;
		}

		return null;
	}

	/**
	 * 要产生多少个结果，是否产生可重复的结果
	 * 
	 * @param num 要产生的数量
	 * @param canRepeat 是否可以重复
	 * @return
	 */
	public List<T> getRanMultipleRepeatResult(int num, boolean canRepeat) {
		List<T> list = new ArrayList<T>();
		if (num <= 0 || proMap.isEmpty()) {
			return list;
		}

		// 不能重复，并且总共缓存的长度还没有要求随机的数量多，就直接返回原来的数据
		if (!canRepeat && proMap.size() < num) {
			return new ArrayList<T>(proMap.keySet());// 所有的添加进去
		}

		int tempTotalPro = totalPro;// 随时变化的概率缓存
		// 判断产出的物品数量是否能打到要求的数量
		while (list.size() < num) {
			if (canRepeat) {
				T t = getRanResult();
				if (t != null) {
					list.add(t);
				}
			} else {
				int rNum = r.nextInt(tempTotalPro);// 随机数

				System.err.println(tempTotalPro + "   " + rNum);

				int minValue = 0;// 最小值
				int maxValue = 0;// 最大值
				for (Entry<T, Integer> e : proMap.entrySet()) {
					T t = e.getKey();
					if (list.contains(t)) {
						continue;
					}

					maxValue += e.getValue();
					if (rNum >= minValue && rNum < maxValue) {
						list.add(t);
						tempTotalPro -= e.getValue();
						break;
					}

					minValue = maxValue;
				}
			}
		}
		return list;
	}

	public static void main(String[] args) {
		Map<Integer, Integer> proMap = new HashMap<Integer, Integer>();
		for (int i = 1; i < 10; i++) {
			proMap.put(i, i * 10);
		}

		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, proMap);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println(sw.toString());

		Weight<Integer> w = new Weight<Integer>(proMap);
		List<Integer> ranMultipleRepeatResult1 = w.getRanMultipleRepeatResult(5, true);
		List<Integer> ranMultipleRepeatResult2 = w.getRanMultipleRepeatResult(5, false);
		System.err.println(ranMultipleRepeatResult1);
		System.err.println(ranMultipleRepeatResult2);
	}
}