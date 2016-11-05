package com.rwbase.dao.email;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class EmailCleaner {

	private static EmailCleaner instance = new EmailCleaner();
	
	private int maxSize = 100;
	
	public static EmailCleaner getInstance(){
		return instance;
	}
	
	public boolean isOverLimit(int size){
		return size >= maxSize;
	}
	
	public EmailItem getCleanEmail(List<EmailItem> emailList){
		if(emailList == null || emailList.size() < 0){
			return null;
		}
		EmailItem oldestEmail = null;
		EmailItem targetEmail = null;
		long oldSendTimeTmp = emailList.get(0).getSendTime();
		for (EmailItem emailTmp : emailList) {
			if(StringUtils.isBlank(emailTmp.getEmailAttachment())){
				targetEmail = emailTmp;
				break;
			}
			if(emailTmp.getSendTime() < oldSendTimeTmp){
				oldSendTimeTmp = emailTmp.getSendTime();
				oldestEmail = emailTmp; 
			}
		}
		if(targetEmail == null){
			targetEmail = oldestEmail;
		}	
		
		return targetEmail;
	}
}
