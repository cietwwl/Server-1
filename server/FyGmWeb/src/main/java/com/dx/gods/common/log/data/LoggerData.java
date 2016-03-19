package com.dx.gods.common.log.data;

import java.net.URLDecoder;
import java.net.URLEncoder;

import com.dx.gods.common.utils.UtilTools;

public class LoggerData {
	
	private final static String SEPERATE_ONE = ",";
	
	protected String userName;
	protected String operatorTime;
	protected String operatorDesc;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOperatorTime() {
		return operatorTime;
	}
	public void setOperatorTime(String operatorTime) {
		this.operatorTime = operatorTime;
	}
	public String getOperatorDesc() {
		return operatorDesc;
	}
	public void setOperatorDesc(String operatorDesc) {
		this.operatorDesc = operatorDesc;
	}

	public void parse(String value){
		try {
			String[] split = value.split(",");
			this.userName = split[0].split(" ")[1].split(":")[1];
			String strTime = split[1].split(":")[1];
			this.operatorTime = UtilTools.getDateTimeString(Long.parseLong(strTime), "yyyy-MM-dd HH:mm:ss");
			this.operatorDesc = URLDecoder.decode(split[2].split(":")[1], "UTF-8"); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
