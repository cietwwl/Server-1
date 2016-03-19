package com.dx.gods.controller.admin.sec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.jdbc.core.JdbcTemplate;

import com.dx.gods.controller.admin.common.DXAdminController;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class ActionAuthsController extends DXAdminController {

	private List<ActionAuths> list;
	private Map<String, Set<String>> map = new HashMap<String, Set<String>>();
	private JdbcTemplate jdbcTemplate;
	private ActionAuthsBLL actionAuthsBLL;
	private ActionAuths actionAuths;
	private String[] roles;

	public String getActionList() {
		RuntimeConfiguration rc = Dispatcher.getInstance().getConfigurationManager().getConfiguration().getRuntimeConfiguration();
		Map<String, Map<String, ActionConfig>> mapConfigs = rc.getActionConfigs();
		list = new ArrayList<ActionAuths>();
		for (Map<String, ActionConfig> map : mapConfigs.values()) {
			for (ActionConfig acf : map.values()) {
				list.add(new ActionAuths(acf.getName()));
			}
		}
		List<ActionAuths> aList = actionAuthsBLL.loadAll();
		if (aList != null) {
			for (ActionAuths actionAuths : aList) {
				for (int i = 0; i < list.size(); i++) {
					if (actionAuths.getName().equals(list.get(i).getName())) {
						list.set(i, actionAuths);
						break;
					}
				}
			}
		}
		return SUCCESS;
	}

	public String update() {
		if (actionAuths.getName() != null) {
			String r = "";
			if (roles!=null) {
				for (String s : roles) {
					r += s + ",";
				}
				actionAuths.setRoles(r.substring(0, r.length() - 1));
			}
			actionAuthsBLL.saveOrUpdate(actionAuths);
		}
		return SUCCESS;
	}

	public Map<String, Set<String>> getMap() {
		return map;
	}

	public void setMap(Map<String, Set<String>> map) {
		this.map = map;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ActionAuthsBLL getActionAuthsBLL() {
		return actionAuthsBLL;
	}

	public void setActionAuthsBLL(ActionAuthsBLL actionAuthsBLL) {
		this.actionAuthsBLL = actionAuthsBLL;
	}

	public ActionAuths getActionAuths() {
		return actionAuths;
	}

	public void setActionAuths(ActionAuths actionAuths) {
		this.actionAuths = actionAuths;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public List<ActionAuths> getList() {
		return list;
	}

	public void setList(List<ActionAuths> list) {
		this.list = list;
	}

}
