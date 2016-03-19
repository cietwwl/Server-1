package com.dx.gods.controller.admin.sec;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class UserDetailServiceImpl implements UserDetailsService {
	private JdbcTemplate jdbcTemplate;

	public UserDetails loadUserByUsername(String username) {
		AdminUser adminUser = getByUserName(username);
		if(adminUser == null){
			return null;
		}
		return new User(adminUser.getUsername(), adminUser.getPassword(), adminUser.isEnabled(), true, true, true, adminUser.getAuthorities());
	}

	public AdminUser getByUserName(String username) {
		List<AdminUser> users = getAllUser();
		if (users != null && !users.isEmpty()) {
			for (AdminUser adminUser : users) {
				if (adminUser.getUsername().equals(username)) {
					return adminUser;
				}
			}
		}
		return null;
	}

	public void clearCache() {
		//do nothing
	}

	@SuppressWarnings("unused")
	public List<AdminUser> getAllUser() {
		AdminUser[] users = null;
		List<AdminUser> ul = null;
		if (users != null && users.length > 0) {
			ul = new ArrayList<AdminUser>(Arrays.asList(users));
		} else {
			try {
				ul = (List<AdminUser>) jdbcTemplate.query("select * from admin_user", new BeanPropertyRowMapper<AdminUser>(AdminUser.class));
//				memoryUtil.updateMemory("com.dx.gods.controller.admin.sec.UserDetailServiceImpl.getAllUser", ul);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ul;
	}

	public int saveOrUpdate(final AdminUser adminUser) {
		String sql = "replace into admin_user(username,password,roles,enable,zoneId,channel) values (?,?,?,?,?,?)";
		int a = -1;
		try {
			a = jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, adminUser.getUsername());
					ps.setString(2, adminUser.getPassword());
					ps.setString(3, adminUser.getRoles());
					ps.setInt(4, adminUser.getEnable());
					ps.setString(5, adminUser.getZoneId());
					ps.setString(6, adminUser.getChannel());
				}
			});
			clearCache();
		} catch (Exception e) {
			a = -1;
		}
		return a;
	}

	public void deleteUser(String username) {
		try {
			jdbcTemplate.execute("delete from admin_user where username='" + username + "'");
			clearCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
