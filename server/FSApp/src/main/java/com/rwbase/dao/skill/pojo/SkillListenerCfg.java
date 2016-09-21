package com.rwbase.dao.skill.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * @Author HC
 * @date 2016年9月20日 下午8:09:02
 * @desc
 **/

public class SkillListenerCfg {
	private String id;
	private int type;// 目前只解析类型为9的数据
	private String arguments;// 参数
	private List<String> buffIdList;// 获取buff的列表

	public String getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public List<String> getBuffIdList() {
		return buffIdList;
	}

	/**
	 * 初始化解析属性
	 */
	public void initData() {
		// luther说这里类型为9的暂时都是法宝，为了快速解决bug先简单解析
		if (type != 9) {
			return;
		}

		if (StringUtils.isEmpty(arguments)) {
			return;
		}

		String[] sArr = arguments.split("\\|");
		if (sArr.length <= 1) {
			return;
		}

		int len = sArr.length;

		List<String> buffIdList = new ArrayList<String>(len);

		for (int i = 1; i < len; i++) {
			buffIdList.add(sArr[i]);
		}

		this.buffIdList = Collections.unmodifiableList(buffIdList);
	}
}