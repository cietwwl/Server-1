package com.rwbase.dao.email;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class EmailCleaner {

	private static EmailCleaner instance = new EmailCleaner();
	private static Comparator<EmailItem> _CMP = new EmailItemComparator();
	
	private int maxSize = 100;
	
	public static EmailCleaner getInstance(){
		return instance;
	}
	
	public boolean isOverLimit(int size){
		return size >= maxSize;
	}
	
	public EmailItem getCleanEmail(List<EmailItem> emailList) {
		if (emailList == null || emailList.size() < 0) {
			return null;
		}
		Collections.sort(emailList, _CMP);
		EmailItem targetEmail = null;
		EmailItem oldestEmail = emailList.get(0);
//		long oldSendTimeTmp = oldestEmail.getSendTime();
		if (StringUtils.isBlank(oldestEmail.getEmailAttachment())) {
			targetEmail = oldestEmail;
		} else {
			EmailItem emailTmp;
			for (int i = 1, size = emailList.size(); i < size; i++) {
				emailTmp = emailList.get(i);
				if (StringUtils.isBlank(emailTmp.getEmailAttachment())) {
					targetEmail = emailTmp;
					break;
				}
				/*if (emailTmp.getSendTime() < oldSendTimeTmp) {
					oldSendTimeTmp = emailTmp.getSendTime();
					oldestEmail = emailTmp;
				}*/
			}
			if (targetEmail == null) {
				targetEmail = oldestEmail;
			}
		}
		return targetEmail;
	}
	
	private static class EmailItemComparator implements Comparator<EmailItem> {

		@Override
		public int compare(EmailItem o1, EmailItem o2) {
			long o1SendTime = o1.getSendTime();
			long o2SendTime = o2.getSendTime();
			if (o1SendTime < o2SendTime) {
				return -1;
			} else if (o1SendTime > o2SendTime) {
				return 1;
			} else {
				if (StringUtils.isBlank(o1.getEmailAttachment())) {
					return -1;
				} else if (StringUtils.isBlank(o2.getEmailAttachment())) {
					return 1;
				} else {
					return 0;
				}
			}
		}

	}
}
