package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.ERoleAttrs;
import com.playerdata.Player;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;

public class AchieveCreateTime extends AbsAchieveAttrValue{

	@Override
	public void achieveAttrValue(Player player, User user, ERoleAttrs roleType, Object param, Map<String, Object> AttrMap, BenefitAttrCfgDAO benefitAttrCfgDAO) {
		
		BenefitAttrCfg cfg = benefitAttrCfgDAO.getCfgById(roleType.getIdStr());
		String createTimeValue = DateUtils.getDateTimeFormatString(user.getCreateTime(), "yyyyMMdd");
		AttrMap.put(cfg.getAttrName(), createTimeValue);
	}

}
