package com.bm.targetSell.param;



/**
 *  5008 5009协议错误参数
 * @author Alex
 * 2016年9月17日 下午5:44:41
 */
public class TargetSellServerErrorParam extends TargetSellHeartBeatParam {

	/**发现错误的操作码*/
	private int errorOpType;
	
	/**
	 * 错误代码：101 系统内部有误；201 参数有误码；202 sign签名有误，
	 * 其他查看全局状态码
	 */
	private int errorCode;
	
	/**错误信息*/
	private String errorMsg;
	
	/**原来的参数*/
	private Object orignalParam;
	


	public int getErrorOpType() {
		return errorOpType;
	}



	public void setErrorOpType(int errorOpType) {
		this.errorOpType = errorOpType;
	}



	public int getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}




	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	public Object getOrignalParam() {
		return orignalParam;
	}


	public void setOrignalParam(Object orignalParam) {
		this.orignalParam = orignalParam;
	}



	@Override
	public void handlerMsg(int msgType) {
		//精准服返回的错误信息，只要记录下就可以了
	}

	
}
