package com.dx.gods.controller.admin.sec;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

public class AdminUser implements UserDetails, Serializable {

	private static final long serialVersionUID = 5082697937991688066L;
	private String username;
	private String password;
	private int enable;
	private String roles;
	private String zoneId;
	private String channel;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
		String roles = this.roles;
		if (roles != null) {
			String[] ss = roles.split(",");
			for (String item : ss) {
				auths.add(new GrantedAuthorityImpl(item));
			}
		}
		return auths;
	}

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isAccountNonExpired() {
		return false;
	}

	public boolean isAccountNonLocked() {
		return false;
	}

	public boolean isCredentialsNonExpired() {
		return false;
	}

	public boolean isEnabled() {
		return enable == 1;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Transient
	public String getRolesDesc() {
		return Role.getRoles(roles);
	}

}
