package com.rounter.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rounter.param.impl.Request9Game;
import com.rounter.param.impl.Response9Game;
import com.rounter.service.IUCService;
import com.rounter.util.JsonUtil;
import com.rounter.util.Utils;


@RestController
@RequestMapping("/9game")
public class _9GameController {

	Logger logger = LoggerFactory.getLogger(_9GameController.class);
	
	@Resource
	private IUCService ucService;

	/**
	 * 获取用户在指定区服的角色列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="roleinfo", method={RequestMethod.POST})
	@ResponseBody
	public String getRoleInfo(@RequestBody Request9Game request){
		logger.info("Get role info request from 9game, dataStr:{}", request.toString());
		//先进行解密
		Utils.decrypt9Game(request);
		Response9Game resp = (Response9Game)ucService.getRoleInfo(request);
		//进行加密
		resp = Utils.encrypt9Game(resp);
		//转为json字符串
		String returnStr = JsonUtil.writeValue(resp);
		logger.info("Response role info to 9game:{}", returnStr);
		return returnStr;
	}
}
