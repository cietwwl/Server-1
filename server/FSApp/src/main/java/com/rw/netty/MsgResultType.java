package com.rw.netty;

public enum MsgResultType {

	/**
	 * 同步全部数据
	 */
	SYNC_ALL,

	/**
	 * 请求的游戏服没有开放
	 */
	ZONE_NOT_OPEN,

	/**
	 * 功能没有开放
	 */
	FUNCTION_NOT_OPEN,

	/**
	 * 异常
	 */
	EXCEPTION,
	
	/**
	 * 消息还未解析
	 */
	NOT_PARSE,
	
	/**
	 * 消息重连
	 */
	RECONNECT,
	
	/**
	 * 非法字符
	 */
	DIRTY_WORD,
	/**
	 * 昵称为空
	 */
	EMPTY_NICK,
	/**
	 * 昵称已被注册
	 */
	DUPLICATED_NICK,
	
	/**
	 * 消息处理找不到玩家(可能超时或者不存在)
	 */
	NO_PLAYER,
	/**
	 * 账号被封
	 */
	ACCOUNT_BLOCK,
	
	/**
	 * 账号被强制下线
	 */
	KICK_OFF,
	/**
	 * 停服
	 */
	SHUTDOWN,
	/**
	 * 达到在线人数上限
	 */
	ONLINE_LIMIT,
	/**
	 * 没有账号
	 */
	ACCOUNT_NOT_EXIST,
	/**
	 * 服务器维护中
	 */
	SERVER_MAINTAIN,
	/**
	 * 角色不存在
	 */
	ROLE_NOT_EXIST,
	;
	public String getPreDesc(Object prefix) {
		if (prefix == null) {
			return name();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append('-').append(name());
		return sb.toString();
	}

	public String getPostDesc(Object postfix) {
		if (postfix == null) {
			return name();
		}
		return name() + '-' + postfix;
	}
}
