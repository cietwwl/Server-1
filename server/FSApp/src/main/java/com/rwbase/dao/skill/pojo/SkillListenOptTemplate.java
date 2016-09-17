package com.rwbase.dao.skill.pojo;

import java.util.Arrays;

import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.rw.service.skill.SkillConstant.SkillOptType;

/**
 * @Author HC
 * @date 2016年8月23日 下午2:33:46
 * @desc
 **/

public class SkillListenOptTemplate {
	private final int optId;// 操作Id
	private final int optType;// 操作类型
	private final String[] arguments;// 参数

	public SkillListenOptTemplate(SkillListenOptCfg cfg) {
		this.optId = cfg.getOptId();
		this.optType = cfg.getOptType();

		String args = cfg.getArguments();
		if (StringUtils.isEmpty(args)) {
			throw new ExceptionInInitializerError(String.format("操作Id为[%s]：初始化SkillListenOpt出现了异常，模版表中填写了空的arguments参数", optId));
		}

		this.arguments = HPCUtil.parseStringArray(args, "|");
		if (optType == SkillOptType.REPLACE_LISTEN_ID.value) {
			if ((this.arguments.length - 1) % 2 != 0) {
				throw new ExceptionInInitializerError(String.format("操作Id为[%s]：初始化SkillListenOpt出现了异常，填写的是替换监听ID类型，但是监听的参数却不是成对出现", optId));
			}
		}
	}

	public int getOptId() {
		return optId;
	}

	public int getOptType() {
		return optType;
	}

	public String[] getArguments() {
		return Arrays.copyOf(arguments, arguments.length);
	}
}