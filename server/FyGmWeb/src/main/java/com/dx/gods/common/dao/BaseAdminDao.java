package com.dx.gods.common.dao;

import org.springframework.orm.hibernate3.HibernateTemplate;

public abstract class BaseAdminDao {

	protected HibernateTemplate adminHibernateTemplate;

	public void setAdminHibernateTemplate(HibernateTemplate adminHibernateTemplate) {
		this.adminHibernateTemplate = adminHibernateTemplate;
	}
	
	
}
