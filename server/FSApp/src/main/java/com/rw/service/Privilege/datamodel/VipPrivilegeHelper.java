package com.rw.service.Privilege.datamodel;

import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.vip.PrivilegeCfgDAO;

public class VipPrivilegeHelper {
	private static VipPrivilegeHelper instance = new VipPrivilegeHelper();
	public static VipPrivilegeHelper getShareInstance(){
		if (instance == null){
			instance = new VipPrivilegeHelper();
		}
		return instance;
	}
	
	public int getDef(int vip,EPrivilegeDef def){
		//TODO 参考 PrivilegeCfgDAO
		return PrivilegeCfgDAO.getInstance().getDef(vip, def);
	}
}
