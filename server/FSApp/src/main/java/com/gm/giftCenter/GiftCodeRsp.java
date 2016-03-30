package com.gm.giftCenter;

import java.util.List;

/*
 * @author HC
 * @date 2016年3月28日 下午3:50:31
 * @Description 
 */
public class GiftCodeRsp {
	private int count;
	private String msg;
	private List<GiftCodeResponse> result;
	private int status;

	public int getCount() {
		return count;
	}

	public String getMsg() {
		return msg;
	}

	public List<GiftCodeResponse> getResult() {
		return result;
	}

	public int getStatus() {
		return status;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setResult(List<GiftCodeResponse> result) {
		this.result = result;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}