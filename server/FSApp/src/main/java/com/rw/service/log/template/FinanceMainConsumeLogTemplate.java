package com.rw.service.log.template;

import java.util.Set;

import com.rw.service.log.template.maker.LogTemplate;

public class FinanceMainConsumeLogTemplate extends BILogTemplate{
	
	final private String template=LogTemplate.FinanceGoldConsumeLogTemplate;
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
