package com.dx.gods.controller.admin.sec;

public enum Role {

	ROLE_MANAGER("管理员"), 
	ROLE_DEVELOPER("技术"), 
	ROLE_DESIGNER("产品"), 
	ROLE_SERVICE("客服"),
	ROLE_SENIOR_CHANNEL_PARTNER("高级渠道合作伙伴"),
	ROLE_ORDINARY_CHANNEL_PARTNER("普通渠道合作伙伴");

	private String role;

	Role(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public static String getRoles(String roles) {
		String r = "";
		if (roles != null) {
			String[] ss = roles.split(",");
			for (Role role : Role.values()) {
				for (String string : ss) {
					if (role.ordinal()==Integer.parseInt(string)) {
						r += role.getRole() + " ";
					}
				}
			}
		}
		return r;
	}
}
