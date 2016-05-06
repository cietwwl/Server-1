package com.rw.service.Privilege;

import com.rw.fsutil.common.stream.IStream;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;

public interface IPrivilegeManager {
	/**
	 * 提取属性值的辅助函数，如果属性不存在或者不是对应的类型，则返回默认值（int:0,bool:false）
	 * @param privilegeDataSet
	 * @param pname
	 * @return
	 */
	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> Integer getIntPrivilege(PrivilegeNameEnums pname);
	
	public <PrivilegeNameEnums extends Enum<PrivilegeNameEnums>> Boolean getBoolPrivilege(PrivilegeNameEnums pname);
	
	//竞技场特权点
	public IStream<PrivilegeProperty> getArenaPrivilege();
	
	//巅峰竞技场特权点
	public IStream<PrivilegeProperty> getPeakArenaPrivilege();
}
