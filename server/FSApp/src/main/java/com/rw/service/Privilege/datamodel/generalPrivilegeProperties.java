package com.rw.service.Privilege.datamodel;

import com.common.BaseConfig;

@SuppressWarnings("unused")
public class generalPrivilegeProperties extends BaseConfig implements IThresholdConfig {
  private com.rwproto.PrivilegeProtos.GeneralPrivilegeNames name; //关键字段:特权控制点
  private int threshold; //上限
  private String buyTip; //特权提升提示模板
  private String nomoreTip; //特权到达上限提示模板
  private String enableTip; //特权开启提示模板


	public com.rwproto.PrivilegeProtos.GeneralPrivilegeNames getName() {
		return name;
	}

	@Override
	public int getThreshold() {
		return threshold;
	}

}