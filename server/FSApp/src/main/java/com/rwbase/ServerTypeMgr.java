package com.rwbase;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.util.SpringContextUtil;

public class ServerTypeMgr {

	public static ServerTypeMgr _instance = new ServerTypeMgr();
	private ServerType _serverType; // 服务器类型

	public static ServerTypeMgr getInstance() {
		return _instance;
	}

	public void loadServerType() {
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourcePF");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String serverType = jdbcTemplate.queryForObject("select * from mt_server_type", String.class);
		ServerType eServerType = ServerType.valueOf(serverType);
		if (eServerType == null) {
			throw new IllegalArgumentException("无法解析的服务器类型：" + serverType);
		}
		this._serverType = eServerType;
	}

	public ServerType getServerType() {
		return _serverType;
	}
}
