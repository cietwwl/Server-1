package com.rw.dataaccess.processor;

import java.util.Date;
import java.util.List;

import com.rw.dataaccess.PlayerCreatedParam;
import com.rw.dataaccess.PlayerCreatedProcessor;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.pojo.HeadBoxType;
import com.rwbase.dao.user.UserGameData;

public class UserGameDataProcessor implements PlayerCreatedProcessor<UserGameData>{

	@Override
	public UserGameData create(PlayerCreatedParam param) {
		UserGameData user = new UserGameData();
		user.setUserId(param.getUserId());
		user.setRookieFlag(1);
		user.setIphone(false);
		user.setLastAddPowerTime(new Date().getTime());
		user.setPower(PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_POWER_START_VALUE));
		user.setFreeChat(PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_CHAT_FREE_COUNT));
		List<String> defaultHeadBoxList = HeadBoxCfgDAO.getInstance().getHeadBoxByType(HeadBoxType.HEADBOX_DEFAULT);
		//TODO 这个逻辑应该放在setting中完成
		user.setHeadFrame(defaultHeadBoxList.get(0));
		return user;
	}

}
