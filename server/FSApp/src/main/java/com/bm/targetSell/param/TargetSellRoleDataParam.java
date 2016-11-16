package com.bm.targetSell.param;

import java.util.Map;


/**
 * 5002玩家属性变化
 * @author Alex
 * 2016年9月17日 下午5:32:54
 */
public class TargetSellRoleDataParam extends TargetSellAbsArgs{
	
//	//角色变化的属性
//	private RoleAttrs attrs;
	//角色变化的属性
	private Map<String, Object> attrs;

//	public RoleAttrs getAttrs() {
//		return attrs;
//	}

	public void setAttrs(Map<String, Object> attrs) {
		this.attrs = attrs;
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}


}
