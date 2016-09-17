package com.rwbase.dao.skill.optlisten;

import java.util.ArrayList;
import java.util.List;

import com.rw.service.skill.SkillConstant.SkillOptType;
import com.rwbase.dao.skill.pojo.SkillListenOptTemplate;

/**
 * @Author HC
 * @date 2016年8月23日 下午5:42:45
 * @desc
 **/

public class ReplaceListenIdOpt implements IOptListen {

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
		List<String> deleteIdList = new ArrayList<String>(len);

		for (int i = 1; i < len;) {
			String deleteId = arguments[i];// 要被删除的Id
			String addId = arguments[i + 1];// 要被添加的Id

			// 如果没有包含要删除的Id或者是已经包含了增加的Id，直接跳过2个索引，因为这里是成对出现的
			if (!listenIdList.contains(deleteId) || listenIdList.contains(addId)) {
				i += 2;
				continue;
			}

			if (!deleteIdList.contains(deleteId)) {
				deleteIdList.add(deleteId);
			}

			if (!addIdList.contains(addId)) {
				addIdList.add(addId);
			}

			i += 2;
		}

		if (deleteIdList.isEmpty() && addIdList.isEmpty()) {
			return null;
		}

		return new OptResult(deleteIdList, addIdList);
	}

	@Override
	public int getOptType() {
		return SkillOptType.REPLACE_LISTEN_ID.value;
	}
}