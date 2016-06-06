package com.playerdata.fixEquip.cfg;

import java.util.ArrayList;
import java.util.List;



public class RoleFixEquipCfg {

	private String roleId;
	
	private String fixEquip1;
	private String fixEquip2;
	private String fixEquip3;
	private String fixEquip4;
	private String fixEquip5;
	private String fixEquip6;
	
	public String getRoleId() {
		return roleId;
	}
	public String getFixEquip1() {
		return fixEquip1;
	}
	public String getFixEquip2() {
		return fixEquip2;
	}
	public String getFixEquip3() {
		return fixEquip3;
	}
	public String getFixEquip4() {
		return fixEquip4;
	}
	public String getFixEquip5() {
		return fixEquip5;
	}
	public String getFixEquip6() {
		return fixEquip6;
	}

	
	public List<String> getExpCfgIdList(){
		List<String> expList = new ArrayList<String>();
		expList.add(fixEquip5);
		expList.add(fixEquip6);
		return expList;
		
	}
	public List<String> getNormCfgIdList(){
		List<String> expList = new ArrayList<String>();
		expList.add(fixEquip1);
		expList.add(fixEquip2);
		expList.add(fixEquip3);
		expList.add(fixEquip4);
		return expList;
	}



	




	

	
	
	
	
}
