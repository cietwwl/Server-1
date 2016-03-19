package com.dx.gods.controller.admin.sec;

import java.util.Arrays;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dx.gods.controller.admin.common.DXAdminController;


public class AdminUserController extends DXAdminController {

	private AdminUser adminUser;
	private MD5Encoder md5Encoder;
	private List<AdminUser> users;
	private String[] roles;
	private UserDetailServiceImpl userDetailsService;

	public String listUser() {
		users = userDetailsService.getAllUser();	
		return SUCCESS;
	}

	public String addUser() {
		if (adminUser != null) {
			adminUser.setEnable(1);
			String pwd = adminUser.getPassword();
			if (pwd == null || pwd.trim().isEmpty()) {
				pwd = adminUser.getUsername() + "@gods.dx";
			}
			adminUser.setPassword(md5Encoder.encodePassword(pwd, null));
			String r = "";
			for (String s : roles) {
				r += s + ",";
			}
			adminUser.setRoles(r.substring(0, r.length() - 1));
			String[] channelIds = ServletActionContext.getRequest().getParameterValues("channelId");
			if(channelIds!=null && channelIds.length>0) {
				String channel = "";
				int endIndex = channelIds.length-1;
				for(int i=0;i<channelIds.length;i++) {
					channel = channel + channelIds[i];
					if(i < endIndex) {
						channel = channel + ",";
					}
				}
				adminUser.setChannel(channel);
			}
			String[] zoneIds = ServletActionContext.getRequest().getParameterValues("zoneId");
			if(zoneIds!=null && zoneIds.length>0) {
				String zone = "";
				int endIndex = zoneIds.length-1;
				for(int i=0;i<zoneIds.length;i++) {
					zone = zone + zoneIds[i];
					if(i < endIndex) {
						zone = zone + ",";
					}
				}
				adminUser.setZoneId(zone);
			}
			int a = userDetailsService.saveOrUpdate(adminUser);
			if (a == 1) {
				return SUCCESS;
			}
		}
		return ERROR;
	}

	public String toEdit() {
		adminUser = userDetailsService.getByUserName(adminUser.getUsername());		
		return SUCCESS;
	}

	public String editUser() {
		if (adminUser != null) {
			AdminUser oldUser = null;
			try {
				oldUser = userDetailsService.getByUserName(adminUser.getUsername());
			} catch (Exception e) {
				e.printStackTrace();
				return ERROR;
			}
			adminUser.setPassword(oldUser.getPassword());
			String r = "";
			for (String s : roles) {
				r += s + ",";
			}
			adminUser.setRoles(r.substring(0, r.length() - 1));
			String[] channelIds = ServletActionContext.getRequest().getParameterValues("channelId");
			if(channelIds!=null && channelIds.length>0) {
				String channel = "";
				int endIndex = channelIds.length-1;
				for(int i=0;i<channelIds.length;i++) {
					channel = channel + channelIds[i];
					if(i < endIndex) {
						channel = channel + ",";
					}
				}
				adminUser.setChannel(channel);
			}
			String[] zoneIds = ServletActionContext.getRequest().getParameterValues("zoneId");
			if(zoneIds!=null && zoneIds.length>0) {
				String zone = "";
				int endIndex = zoneIds.length-1;
				for(int i=0;i<zoneIds.length;i++) {
					zone = zone + zoneIds[i];
					if(i < endIndex) {
						zone = zone + ",";
					}
				}
				adminUser.setZoneId(zone);
			}
			userDetailsService.saveOrUpdate(adminUser);
			return SUCCESS;
		}
		return ERROR;
	}

	public String deleteUser() {
		if (adminUser != null) {
			userDetailsService.deleteUser(adminUser.getUsername());
			return SUCCESS;
		}
		return ERROR;
	}

	public String cpwd() {
		if (roles != null && !roles[2].trim().equals("")) {
			adminUser = userDetailsService.getByUserName(getAdminName());
			if (md5Encoder.isPasswordValid(adminUser.getPassword(), roles[0], null) && roles[1].equals(roles[2])) {
				adminUser.setPassword(md5Encoder.encodePassword(roles[1], null));
				userDetailsService.saveOrUpdate(adminUser);
				SecurityContextHolder.clearContext();
				return SUCCESS;
			}
		}
		return ERROR;
	}

	public AdminUser getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(AdminUser adminUser) {
		this.adminUser = adminUser;
	}

	public MD5Encoder getMd5Encoder() {
		return md5Encoder;
	}

	public void setMd5Encoder(MD5Encoder md5Encoder) {
		this.md5Encoder = md5Encoder;
	}

	public List<AdminUser> getUsers() {
		return users;
	}

	public void setUsers(List<AdminUser> users) {
		this.users = users;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public UserDetailServiceImpl getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailServiceImpl userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	public int getAdminUserRole() {
		return Integer.parseInt(adminUser.getRoles());
	}

	public List<Integer> getZonesForAdminUser() {
		String[] zoneStrArray = adminUser.getZoneId().split(",");
		Integer[] zonesIntArray = new Integer[zoneStrArray.length];
		for(int i=0;i<zoneStrArray.length;i++) {
			zonesIntArray[i] = Integer.valueOf(zoneStrArray[i]);
		}
		return Arrays.asList(zonesIntArray);
	}
	
	public List<String> getChannelsForAdminUser() {
		return Arrays.asList(adminUser.getChannel().split(","));
	}
	
}
