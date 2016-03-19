package com.rwbase.dao.group.pojo.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

/*
 * @author HC
 * @date 2016年1月27日 下午5:09:06
 * @Description 帮派功能模版
 */
public class GroupFunctionTemplate {
	private final int functionType;// 帮派功能的值
	private final List<Integer> postList;// 可以使用此功能的职位
	private final int needGroupLevel;// 需要帮派的等级

	public GroupFunctionTemplate(GroupFunctionCfg cfg) {
		this.functionType = cfg.getFunctionType();
		this.needGroupLevel = cfg.getNeedGroupLevel();

		String post = cfg.getPostList();
		if (StringUtils.isEmpty(post)) {// 职位是空的
			postList = Collections.emptyList();
		} else {// 有需要验证的职位
			String[] arr = post.split(",");

			int len = arr.length;
			List<Integer> l = new ArrayList<Integer>(len);

			for (int i = 0; i < len; i++) {
				l.add(Integer.valueOf(arr[i]));
			}

			postList = Collections.unmodifiableList(l);
		}
	}

	/**
	 * 获取帮派功能的值
	 * 
	 * @return
	 */
	public int getFunctionType() {
		return functionType;
	}

	/**
	 * <pre>
	 * 获取帮派值能使用的职位列表
	 * 如果<b>列表是null或者是空的</b>，就说明没有职业限制
	 * </pre>
	 * 
	 * @return
	 */
	public List<Integer> getPostList() {
		return postList;
	}

	/**
	 * <pre>
	 * 获取开启此功能对应的帮派等级要求
	 * 如果等级 <b><i><u><= 0</u></i></b> 就说明这个功能是不受帮派等级控制的
	 * </pre>
	 * 
	 * @return
	 */
	public int getNeedGroupLevel() {
		return needGroupLevel;
	}
}