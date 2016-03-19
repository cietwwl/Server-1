package com.playerdata.readonly;

import com.rwbase.common.enu.EPrivilegeDef;

public interface VipMgrIF {
	/**
	 * 获取主角VIP特权
	 * @param type
	 * @return
	 */
	public int GetPrivilege(EPrivilegeDef type);
	/**
	 * 获取主角VIP配置表特权
	 * @param type
	 * @return
	 */
	public int GetMaxPrivilege(EPrivilegeDef type);
}
