package com.rwbase.dao.role.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/*
 * @author HC
 * @date 2016年4月16日 上午12:35:25
 * @Description 
 */
public class InlayTemplate {
	private final String modelId;// 佣兵ID
	private final List<Integer> openLv;// 宝石槽解锁等级
	private final List<Integer> prior;// 宝石选择优先级

	private final int extraNum1;// 额外奖励条件1需求宝石数
	/** <等级,<属性名字,增加数值>> */
	private final Map<Integer, Map<String, String>> extraValue1;// 奖励数值

	private final int extraNum2;// 额外奖励条件1需求宝石数
	/** <等级,<属性名字,增加数值>> */
	private final Map<Integer, Map<String, String>> extraValue2;// 奖励数值

	private final int extraNum3;// 额外奖励条件1需求宝石数
	/** <等级,<属性名字,增加数值>> */
	private final Map<Integer, Map<String, String>> extraValue3;// 奖励数值

	public InlayTemplate(InlayCfg cfg) {
		this.modelId = cfg.getRoleId();

		// 宝石解锁等级
		String openLvStr = cfg.getOpenLv();
		if (!StringUtils.isEmpty(openLvStr)) {
			String[] strArr = openLvStr.split(",");

			int len = strArr.length;
			List<Integer> openLv = new ArrayList<Integer>(len);

			for (int i = 0; i < len; i++) {
				openLv.add(Integer.valueOf(strArr[i]));
			}

			this.openLv = Collections.unmodifiableList(openLv);
		} else {
			this.openLv = Collections.unmodifiableList(Collections.EMPTY_LIST);
		}

		// 优先级
		String priorStr = cfg.getPrior();
		if (!StringUtils.isEmpty(priorStr)) {
			String[] strArr = priorStr.split(",");

			int len = strArr.length;
			List<Integer> prior = new ArrayList<Integer>();

			for (int i = 0; i < len; i++) {
				prior.add(Integer.valueOf(strArr[i]));
			}

			this.prior = Collections.unmodifiableList(prior);
		} else {
			this.prior = Collections.unmodifiableList(Collections.EMPTY_LIST);
		}

		this.extraNum1 = cfg.getExtraNum1();
		this.extraNum2 = cfg.getExtraNum2();
		this.extraNum3 = cfg.getExtraNum3();

		this.extraValue1 = Collections.unmodifiableMap(parseStr2Map(cfg.getExtraLv1(), cfg.getExtraValue1()));
		this.extraValue2 = Collections.unmodifiableMap(parseStr2Map(cfg.getExtraLv2(), cfg.getExtraValue2()));
		this.extraValue3 = Collections.unmodifiableMap(parseStr2Map(cfg.getExtraLv3(), cfg.getExtraValue3()));
	}

	/**
	 * 解析数据
	 * 
	 * @param lvStr
	 * @param attrValue
	 * @return
	 */
	private Map<Integer, Map<String, String>> parseStr2Map(String lvStr, String attrValue) {
		Map<Integer, Map<String, String>> map = new HashMap<Integer, Map<String, String>>();
		if (StringUtils.isEmpty(lvStr) || StringUtils.isEmpty(attrValue)) {
			return map;
		}

		String[] lvStrArr = lvStr.split(",");
		String[] attrArr = attrValue.split(",");

		int attrValueLen = attrArr.length;

		for (int i = 0, len = lvStrArr.length; i < len; i++) {
			Integer value = Integer.valueOf(lvStrArr[i]);

			if (i < attrValueLen) {
				break;
			}

			String[] attrValueArr = attrArr[i].split(":");

			Map<String, String> temp = map.get(value);
			if (temp == null) {
				temp = new HashMap<String, String>();
				map.put(value, temp);
			}

			temp.put(attrValueArr[0], attrValueArr[1]);
		}

		return map;
	}

	public String getModelId() {
		return modelId;
	}

	public List<Integer> getOpenLv() {
		return openLv;
	}

	public List<Integer> getPrior() {
		return prior;
	}

	public int getExtraNum1() {
		return extraNum1;
	}

	public Map<Integer, Map<String, String>> getExtraValue1() {
		return extraValue1;
	}

	public int getExtraNum2() {
		return extraNum2;
	}

	public Map<Integer, Map<String, String>> getExtraValue2() {
		return extraValue2;
	}

	public int getExtraNum3() {
		return extraNum3;
	}

	public Map<Integer, Map<String, String>> getExtraValue3() {
		return extraValue3;
	}
}