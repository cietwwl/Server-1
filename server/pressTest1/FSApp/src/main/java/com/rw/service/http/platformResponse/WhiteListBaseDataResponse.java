package com.rw.service.http.platformResponse;

import java.io.Serializable;
import java.util.List;

public class WhiteListBaseDataResponse implements Serializable{
	private static final long serialVersionUID = -6182532647273100003L;
	
	private List<String> accountList;
	private boolean blnClose;
	private String process;
	
	public List<String> getAccountList() {
		return accountList;
	}
	public void setAccountList(List<String> accountList) {
		this.accountList = accountList;
	}
	public boolean isBlnClose() {
		return blnClose;
	}
	public void setBlnClose(boolean blnClose) {
		this.blnClose = blnClose;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	
}
