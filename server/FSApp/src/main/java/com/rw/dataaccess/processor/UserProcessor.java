package com.rw.dataaccess.processor;

import org.apache.commons.lang3.StringUtils;
import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.dao.user.User;

public class UserProcessor implements PlayerCreatedProcessor<User>{

	@Override
	public User create(PlayerCreatedParam param) {
		String accountId = param.getAccountId();
		User baseInfo = new User();
		baseInfo.setUserId(param.getUserId());
		baseInfo.setAccount(param.getAccountId());
		baseInfo.setZoneId(param.getZoneId());
		baseInfo.setExp(0);
		baseInfo.setLevel(1);
		baseInfo.setUserName(param.getUserName());
		baseInfo.setSex(param.getSex());
		baseInfo.setCreateTime(System.currentTimeMillis()); // 记录创建角色的时间
		// 设置默认头像
		baseInfo.setHeadImage(param.getHeadImage());
		String clientInfoJson = param.getClientInfoJson();
		if (StringUtils.isNotBlank(clientInfoJson)) {
			ClientInfo clienInfo = ClientInfo.fromJson(clientInfoJson);
			baseInfo.setZoneRegInfo(ZoneRegInfo.fromClientInfo(clienInfo, accountId));
		}
		return baseInfo;
	}

}
