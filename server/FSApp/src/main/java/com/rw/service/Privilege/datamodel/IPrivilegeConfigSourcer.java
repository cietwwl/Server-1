package com.rw.service.Privilege.datamodel;

import java.lang.reflect.Field;
import java.util.List;

import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder;

public interface IPrivilegeConfigSourcer<PrivilegeNameEnum extends Enum<PrivilegeNameEnum>> {
	/**
	 * 从provider获得当前这个配置包含的每个特权点的属性，
	 * 然后使用IPrivilegeWare的put方法存放对应的特权
	 * @param privilegeMgr
	 * @param privilegeIndex
	 */
	public void putPrivilege(IPrivilegeWare privilegeMgr,List<IPrivilegeProvider> providers);
	
	// 综合各个provider的结果：求最大值
	public AllPrivilege.Builder combine(AllPrivilege.Builder acc, AllPrivilege pri);

	public void setValue(AllPrivilege.Builder holder,PrivilegeProperty.Builder value);
	
	public PrivilegeProperty.Builder getValue(AllPrivilege.Builder holder);

	public PrivilegeProperty getValue(AllPrivilege pri);
	
	public Object getValue(PrivilegeProperty currentPri, PrivilegeNameEnum pname);

	public Field getConfigField(PrivilegeNameEnum name);

	public boolean eq(PrivilegeProperty oldValue, Builder newValue);

	public String[] getChargeSource(Enum<?> name);
	
	public IConfigChargeSource<PrivilegeNameEnum> getCfg(String chargetType);

	public PropertyWriter getProWriter(PrivilegeNameEnum name);

	public Field getConfigField(String name);
}
