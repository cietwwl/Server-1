package com.server.paramers;

/**
 * 统一响应结构
 * @author Alex
 * 2017年1月13日 下午5:38:37
 */
public class RESTResponse {

	
	private static final String OK = "ok";
	private static final String ERROR = "error";
	
	private Meta meta;
	private Object data;
	
	public RESTResponse success(){
		this.meta = new Meta(true, OK);
		return this;
	}
	
	
	public RESTResponse success(Object data){
		this.meta = new Meta(true, OK);
		this.data = data;
		return this;
	}
	
	
	public RESTResponse failure(){
		this.meta = new Meta(false, ERROR);
		return this;
	}
	
	public RESTResponse failure(String message){
		this.meta = new Meta(false, message);
		return this;
	}
	
	public RESTResponse failure(Object data){
		this.data = data;
		this.meta = new Meta(false, ERROR);
		return this;
	}
	
	public Meta getMeta() {
		return meta;
	}


	public Object getData() {
		return data;
	}
	
	
	
	public class Meta{
		
		private boolean success;
		private String msg;

		public Meta(boolean success, String msg) {
			this.success = success;
			this.msg = msg;
		}
		public Meta(boolean success) {
			this.success = success;
		}
		public boolean isSuccess() {
			return success;
		}
		public String getMsg() {
			return msg;
		}
		
	}




	
}
