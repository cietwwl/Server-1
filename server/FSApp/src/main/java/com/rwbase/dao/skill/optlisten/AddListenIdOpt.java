package com.rwbase.dao.skill.optlisten;

import java.util.ArrayList;
import java.util.List;

import com.rw.service.skill.SkillConstant.SkillOptType;
import com.rwbase.dao.skill.pojo.SkillListenOptTemplate;

/**
 * @Author HC
 * @date 2016年8月23日 下午5:42:12
 * @desc
 **/

public class AddListenIdOpt implements IOptListen {

	@Override
	public OptResult checkOptListen(String checkSkillId, List<String> listenIdList, SkillListenOptTemplate tmp) {
		int len;
		String[] arguments = tmp.getArguments();
		if (arguments == null || (len = arguments.length) <= 0) {
			return null;
		}

		if (!checkSkillId.startsWith(arguments[0])) {
			return null;
		}

		List<String> addIdList = new ArrayList<String>(len);
		for (int i = 1; i < len; i++) {
			String addId = arguments[i];
			if (!addIdList.contains(addId)) {
				addIdList.add(addId);
			}
		}

		if (addIdList.isEmpty()) {
			return null;
		}

		return new OptResult(null, addIdList);
	}

	@Override
	public int getOptType() {
		return SkillOptType.ADD_LISTEN_ID.value;
	}
}