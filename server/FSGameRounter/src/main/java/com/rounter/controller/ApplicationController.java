package com.rounter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rounter.param.Request9Game;
import com.rounter.service.IUCService;


@RestController
public class ApplicationController {
	Logger logger = LoggerFactory.getLogger("mainLogger");

	@RequestMapping("/hello")
	public String greeting() {
		Integer r = 0;
		synchronized (r) {
			logger.info("recv msg:{}", "hello");
			try {
				
				new IUCService(){

					@Override
					public void getRoleInfo(Request9Game request, r) {
						// TODO Auto-generated method stub
						
					}
					
				}
				r.wait(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("recv msg:{}", "hello");
		return "Say hello";
	}
}
