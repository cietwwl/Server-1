package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

public class EmailLogTemplate extends BILogTemplate{

	final private String template=LogTemplate.EmailLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);

	
	public String getTextTemplate() {
		return template;
	}


	public Set<String> getInfoNameSet() {
		return infoNameSet;
	}

	public enum EamilOpType{
		EMAIL_SEND("1", "邮件发送"),
		EMAIL_RECEIVE("2", "邮件到达"),
		EMAIL_OPEN("3", "邮件收取"),
		EMAIL_DELETE("4", "邮件删除"),
		EMAIL_AUTO_DELETE("5", "系统删除"),
		;
		private String id;
		private String desc;
		private EamilOpType(String id, String desc){
			this.id = id;
			this.desc = desc;
		}
		public String getId() {
			return id;
		}
		public String getDesc() {
			return desc;
		}
	}
}
