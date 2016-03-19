package com.gm;

public enum GmResultStatusCode {
	STATUS_SUCCESS(0,"成功"),
	STATUS_ROLE_NOT_FOUND(1, "找不到该角色"),
	STATUS_ARGUMENT_ERROR(2, "参数出错"),
	STATUS_DELETE_ITEM_FAIL(3, "删除玩家包裹失败"),
	STATUS_EXECUTE_GM_COMMAND_FAIL(4, "执行GM命令失败"),
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
