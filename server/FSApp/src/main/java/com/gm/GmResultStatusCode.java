package com.gm;

public enum GmResultStatusCode {
	STATUS_SUCCESS(0,"成功"),
	STATUS_ROLE_NOT_FOUND(1, "找不到该角色"),
	STATUS_ARGUMENT_ERROR(2, "参数出错"),
	STATUS_DELETE_ITEM_FAIL(3, "删除玩家包裹失败"),
	STATUS_EXECUTE_GM_COMMAND_FAIL(4, "执行GM命令失败"),
	STATUS_NOT_FIND_GMMAIL(5, "找不到全服邮件"),
	STATUS_GMMAIL_CLOSE(6, "当前的邮件已关闭"),
	STATUS_INVALID_NOTICE_HEAD(7, "公告标题超长"),
	STATUS_INVALID_DALAY_TIME(8, "过期时间错误，最大值七天"),
	STATUS_INVALID_ATTACHMENT(9, "无效附件，发送失败"),
	STATUS_NOT_FIND_GROUP(10, "找不到帮派"),
	;
	private int status;
	private String statusDesc;
	
	private GmResultStatusCode(int _status, String _statusDesc){
		this.status = _status;
		this.statusDesc = _statusDesc;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}
}
