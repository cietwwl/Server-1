package com.rounter.service.impl;

import org.springframework.stereotype.Service;

import com.rounter.param.Request9Game;
import com.rounter.param.Response9Game;
import com.rounter.service.IUCService;

@Service
public class UIServiceImpl implements IUCService{

	@Override
	public Response9Game getRoleInfo(Request9Game request) {
		Response9Game resp = new Response9Game();
		resp.setData(request.getData());
		resp.setId(request.getId());
		resp.setState(request.getClient());
		return resp;
	}

}
