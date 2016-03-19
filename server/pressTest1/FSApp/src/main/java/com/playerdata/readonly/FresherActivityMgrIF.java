package com.playerdata.readonly;

import java.util.Enumeration;
import java.util.List;

import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemIF;

/**
 * 开服活动管理器对外的接口
 * @author lida
 *
 */
public interface FresherActivityMgrIF {
	public List<FresherActivityItemIF> getFresherActivityItems(eActivityType type);
	
	public String achieveFresherActivityReward(Player player, int cfgId);
}
