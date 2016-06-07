package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

public class ChatLogTemplate extends BILogTemplate{

	final private String template = LogTemplate.ChatLogTemplate;
	final private Set<String> infoNameSet = BILogTemplateHelper.getInfoNameSet(template);
	
	@Override
	public String getTextTemplate() {
		// TODO Auto-generated method stub
		return template;
	}

	@Override
	public Set<String> getInfoNameSet() {
		// TODO Auto-generated method stub
		return infoNameSet;
	}

}
