package com.rw.service.platformService;

import com.rw.service.http.request.RequestObject;

public class PlatformServiceHandler {
	public RequestObject request;
	
	public PlatformServiceHandler(RequestObject object){
		this.request = object;
	}

	public RequestObject getRequest() {
		return request;
	}
}
