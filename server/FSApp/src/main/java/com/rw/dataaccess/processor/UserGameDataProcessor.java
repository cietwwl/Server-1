package com.rw.dataaccess.processor;

import java.util.List;

import com.rw.dataaccess.PlayerParam;
import com.rw.dataaccess.PlayerCoreCreation;
import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.pojo.HeadBoxType;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.vip.PrivilegeCfgDAO;

public class UserGameDataProcessor implements PlayerCoreCreation<UserGameData> {

	@Override
	public UserGameData create(PlayerParam param) {
		UserGameData user = new UserGameData();
		user.setUserId(param.getUserId());
		user.setRookieFlag(1);
		user.setIphone(false);
		user.setLastAddPowerTime(System.currentTimeMillis());
		user.setPower(PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_POWER_START_VALUE));
		user.setFreeChat(PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_CHAT_FREE_COUNT));
		List<String> defaultHeadBoxList = HeadBoxCfgDAO.getInstance().getHeadBoxByType(HeadBoxType.HEADBOX_DEFAULT);
		// TODO 这个逻辑应该放在setting中完成
		user.setHeadFrame(defaultHeadBoxList.get(0));
		// 新创建角色vip是0，以后需要改再加入到参数中
		int max = PrivilegeCfgDAO.getInstance().getDef(0, EPrivilegeDef.SKILL_POINT_COUNT);
		user.setSkillPointCount(max);
		user.setLastRecoverSkillPointTime(System.currentTimeMillis());
		return user;
	}

}
