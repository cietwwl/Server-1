package com.dx.gods.controller.admin.sec;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ActionAuths implements Serializable {

	private static final long serialVersionUID = -445840822469961126L;
	private String name;
	private String roles;
	private String desc = "";

	public ActionAuths() {
		super();
	}

	public ActionAuths(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Set<String> getAuths() {
		Set<String> set = new HashSet<String>();
		if (roles != null && !roles.isEmpty()) {
			String[] ss = roles.split(",");
			for (int i = 0; i < ss.length; i++) {
				set.add(ss[i]);
			}
		}
		return set;
	}

}
