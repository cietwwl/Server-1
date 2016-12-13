package com.rounter.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.ReqRoleInfo;
import com.rounter.param.impl.Request9Game;
import com.rounter.param.impl.Response9Game;
import com.rounter.service.IUCService;
import com.rounter.state.UCStateCode;
import com.rounter.util.JsonUtil;
import com.rounter.util.Utils;
import com.rw.fsutil.common.Pair;


@RestController
@RequestMapping("/9game")
public class _9GameController extends AbsController<UCStateCode, String>{

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
		Pair<UCStateCode,String> beforeOpt = beforeOpt(request);
		UCStateCode stateCode = beforeOpt.getT1();
		String t2 = beforeOpt.getT2();
		if(stateCode != UCStateCode.STATE_OK){
			return t2;
		}
		
		ReqRoleInfo roleInfo = JsonUtil.readValue(t2, ReqRoleInfo.class);
		roleInfo.setRequestID(request.getId());
		
		IResponseData responseData = ucService.getRoleInfo(roleInfo);
		Pair<UCStateCode,String> afterOpt = afterOpt(responseData);
		return afterOpt.getT2();
	}
	
	
	
	
	/**
	 * 从请求的消息中获取client节点中的caller参数值
	 * @param request
	 * @return
	 */
	private String getCaller(Request9Game request){
		JSONObject client = request.getClient();
		return client.getString("caller");
	}
	
	/**
	 * 从请求的消息中获取data节点中的params参数值
	 * @param request
	 * @return
	 */
	public String getDataParam(Request9Game request){
		JSONObject data = request.getData();
		return data.getString("params");
	}
	
	/**
	 * 检查请求的参数是否正确
	 * @param request
	 * @return
	 */
	private UCStateCode checkCondition(Request9Game request){
		//检查签名是否正确
		String caller = getCaller(request);
		if(caller == null){
			//没有caller数据，数据有误
			return UCStateCode.STATE_CALLER_ERROR;
		}
		String params = getDataParam(request);
		if(params == null){
			//没有caller数据，数据有误
			return UCStateCode.STATE_PARAM_ERROR;
		}
		String sign = Utils.get9GameSign(caller, params);
		if(!StringUtils.equals(sign, request.getSign())){
			//签名检查不通过，返回通知
			return UCStateCode.STATE_SIGN_ERROR;
		}
		return UCStateCode.STATE_OK;
	}
	
	private String responseString(UCStateCode code, long id, String dataJsonStr){
		Response9Game resp = new Response9Game();
		resp.setId(id);
		Map<String, Object> state = new HashMap<String, Object>();
		state.put("code", code.getId());
		state.put("msg", code.getMsg());
		resp.setState(state);
		if(StringUtils.isNotBlank(dataJsonStr)){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("params", dataJsonStr);
			resp.setData(data);
		}
		return JsonUtil.writeValue(resp);
	}

	@Override
	Pair<UCStateCode, String> beforeOpt(Object... param) {
		Request9Game request = (Request9Game)param[0];
		UCStateCode stateCode = checkCondition(request);
		if(stateCode != UCStateCode.STATE_OK){
			//校验不通过，直接返回错误消息
			return Pair.Create(stateCode, responseString(stateCode, request.getId(), null));
		}
		
		logger.info("Get role info request from 9game, dataStr:{}", request.toString());
		//先进行解密
		String params = getDataParam(request);
		logger.info("before decrypt 9game data string:{}", params);
		String decryptStr = Utils.decrypt9Game(params);
		logger.info("after decrypt 9game data string:{}", decryptStr);
		return Pair.Create(stateCode, decryptStr);
	}

	@Override
	Pair<UCStateCode, String> afterOpt(Object... param) {
		IResponseData responseData = (IResponseData) param[0];
		UCStateCode respCode = UCStateCode.getCodeByID(responseData.getStateCode());
		if(respCode != UCStateCode.STATE_OK){
			//处理有问题
			return Pair.Create(respCode, responseString(respCode, responseData.getId(), null));
		}
		//进行加密
		String dataStr = Utils.encrypt9Game(responseData.getData().toJSONString());
		
		//转为json字符串
		String returnStr = responseString(respCode, responseData.getId(), dataStr);
		logger.info("Response role info to 9game:{}", returnStr);
		return Pair.Create(respCode, returnStr);
	}
	

	
}
