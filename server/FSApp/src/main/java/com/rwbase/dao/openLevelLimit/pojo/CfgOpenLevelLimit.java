package com.rwbase.dao.openLevelLimit.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CfgOpenLevelLimit {
	  private int type; //功能ID
	  private int minLevel; //最小等级
	  private int maxLevel; //最大等级
	  private String des; //描述
	  private int checkPointID; //开放关卡
	  private int vip;//vip等级
	  private com.rwproto.MsgDef.Command serviceId; //请求ID(程序配置，策划不要修改)
	  private String submoduleId; //请求控制参数(程序配置，策划不要修改)

	  private List<String> submoduleIdList;
	  public void ExraLoad(){
		  if (StringUtils.isNotBlank(submoduleId)){
			  String[] lst = submoduleId.split(",");
			  ArrayList<String> tmp = new ArrayList<String>(lst.length);
			  for(int i =0;i<lst.length;i++){
				  tmp.add(lst[i].trim());
			  }
			  submoduleIdList = tmp;
		  }else{
			  submoduleIdList = Collections.emptyList();
		  }
	  }
	  
	public List<String> getSubmoduleIdList() {
		return submoduleIdList;
	}

	public com.rwproto.MsgDef.Command getServiceId() {
		return serviceId;
	}
	public String getSubmoduleId() {
		return submoduleId;
	}
	public int getCheckPointID() {
		return checkPointID;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getMinLevel() {
		return minLevel;
	}
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}
	public int getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}
}
