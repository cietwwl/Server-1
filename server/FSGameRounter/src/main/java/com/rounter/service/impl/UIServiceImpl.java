package com.rounter.service.impl;

import org.springframework.stereotype.Service;

import com.rounter.param.IRequestData;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.Request9Game;
import com.rounter.param.impl.Response9Game;
import com.rounter.service.IUCService;

@Service
public class UIServiceImpl implements IUCService{

	@Override
	public IResponseData getRoleInfo(IRequestData request) {
		Response9Game resp = new Response9Game();
		if(request instanceof Request9Game){
			
		}
		return resp;
	}
	
}
