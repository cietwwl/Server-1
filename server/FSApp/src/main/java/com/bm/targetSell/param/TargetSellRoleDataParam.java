package com.bm.targetSell.param;


/**
 * 5002玩家属性变化
 * @author Alex
 * 2016年9月17日 下午5:32:54
 */
public class TargetSellRoleDataParam extends TargetSellAbsArgs{
	
	//角色变化的属性
	private RoleAttrs attrs;

	public RoleAttrs getArgs() {
		return attrs;
	}

	public void setArgs(RoleAttrs args) {
		this.attrs = args;
	}


	
	


}
