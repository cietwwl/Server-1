package com.rw.service.log.eLog;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class eBILogRegSubChannelToClientPlatForm {
	private String regSubChannelId ;
	private String clientPlayForm ;
	private AtomicInteger count ;
//	private HashMap<String, AtomicInteger> regSubchannelAndClientPlayForm = new HashMap<String, AtomicInteger>();
	
	
	public String getregSubChannelId(){
		return regSubChannelId;
	}
	
	public void setregSubChannelId(String setregSubChannelId){
		this.regSubChannelId = setregSubChannelId;
	}
	
	public String getclientPlayForm(){
		return clientPlayForm;
	}
	
	public void setclientPlayForm(String clientPlayForm){
		this.clientPlayForm = clientPlayForm;
	}
	
	public AtomicInteger getcount(){
		return count;
	}
	
	public void setCount(AtomicInteger count){
		this.count = count;
	}
	
	
	
//	public HashMap getregSubChannelAndClientPlayForm(){
//		return regSubchannelAndClientPlayForm;
//	}
	
	

}
