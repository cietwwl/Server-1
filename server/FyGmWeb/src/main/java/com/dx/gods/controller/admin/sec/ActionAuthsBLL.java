package com.dx.gods.controller.admin.sec;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;


public class ActionAuthsBLL {
	private JdbcTemplate jdbcTemplate;

	public List<ActionAuths> loadAll() {
		try {
			return (List<ActionAuths>) jdbcTemplate.query("select * from action_auths_cfg", new BeanPropertyRowMapper<ActionAuths>(ActionAuths.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean saveOrUpdate(final ActionAuths actionAuths) {
		String sql = "replace into action_auths_cfg(`name`,`roles`,`desc`) values(?,?,?)";
		try {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, actionAuths.getName());
					ps.setString(2, actionAuths.getRoles());
					ps.setString(3, actionAuths.getDesc());
				}
			});
			clearCache();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ActionAuths getByName(String name) {
		List<ActionAuths> list = loadAll();
		if (list != null) {
			for (ActionAuths actionAuths : list) {
				String actionAuthsName = actionAuths.getName();
				if (actionAuthsName.equals(name)) {
					return actionAuths;
				}
			}
		}
		return null;
	}

	public boolean clearCache() {
		return true;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


}
