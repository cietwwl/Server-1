package com.rw.service.guide.datamodel;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class NewGuideClosure {

	public boolean close; // 是否关闭新手引导

	public NewGuideClosure(boolean close) {
		super();
		this.close = close;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

}
