package com.rounter.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.rounter.controller.ucParam.Request9Game;
import com.rounter.controller.ucParam.Response9Game;
import com.rounter.loginServer.LoginServerEnum;
import com.rounter.param.IResponseData;
import com.rounter.service.IUCService;
import com.rounter.state.UCStateCode;
import com.rounter.util.JsonUtil;
import com.rounter.util.Utils;


@RestController
@RequestMapping("/9game")
public class _9GameController extends AbsController<UCStateCode, String>{

	Logger logger = LoggerFactory.getLogger("mainLogger");
	
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
		UCStateCode stateCode = beforeOpt.getKey();
		String t2 = beforeOpt.getValue();
		if(stateCode != UCStateCode.STATE_OK){
			return t2;
		}
		JSONObject json = (JSONObject) JSONObject.parse(t2);
		String account = json.getString("accountId");
		int platform = json.getIntValue("platform");
		String serverKey = null;
		if(platform == 2){
			serverKey = LoginServerEnum.UC_ANDRIOD.name();
		}else if(platform == 3){
			serverKey = LoginServerEnum.UC_IOS.name();
		}
		
		if(StringUtils.isEmpty(serverKey)){
			return responseString(UCStateCode.STATE_PARAM_ERROR, request.getId(), null);
		}
		
		
		IResponseData responseData = ucService.getRoleInfo(serverKey, account);
		Pair<UCStateCode,String> afterOpt = afterOpt(responseData, request.getId());
		logger.info("response role info msg :{}", afterOpt.getValue());
		return afterOpt.getValue();
	}
	
	/**
	 * 获取用户在指定区服的角色列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="areainfo", method={RequestMethod.POST})
	@ResponseBody
	public String getAreasInfoInfo(@RequestBody Request9Game request){
		Pair<UCStateCode,String> beforeOpt = beforeOpt(request);
		UCStateCode stateCode = beforeOpt.getKey();
		String t2 = beforeOpt.getValue();
		if(stateCode != UCStateCode.STATE_OK){
			return t2;
		}
		
		
		JSONObject json = (JSONObject) JSONObject.parse(t2);
		int platform = json.getIntValue("platform");
		String serverKey = null;
		if(platform == 2){
			serverKey = LoginServerEnum.UC_ANDRIOD.name();
		}else if(platform == 3){
			serverKey = LoginServerEnum.UC_IOS.name();
		}
		
		if(StringUtils.isEmpty(serverKey)){
			return responseString(UCStateCode.STATE_PARAM_ERROR, request.getId(), null);
		}
		int page = json.getIntValue("page") != 0 ? json.getIntValue("page") : 1;
		int count = json.getIntValue("count") != 0 ? json.getIntValue("count") : 20;
		
		IResponseData responseData = ucService.getAreasInfo(serverKey, page, count);
		Pair<UCStateCode,String> afterOpt = afterOpt(responseData, request.getId());
		logger.info("response role info msg :{}", afterOpt.getValue());
		return afterOpt.getValue();
	}
	
	/**
	 * 礼包发放接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value="sendgift", method={RequestMethod.POST})
	@ResponseBody
	public String sendGift(@RequestBody Request9Game request){
		Pair<UCStateCode,String> beforeOpt = beforeOpt(request);
		UCStateCode stateCode = beforeOpt.getKey();
		String t2 = beforeOpt.getValue();
		if(stateCode != UCStateCode.STATE_OK){
			return t2;
		}
		
		JSONObject json = (JSONObject) JSONObject.parse(t2);
		String areaId = json.getString("serverId");
		String roleID = json.getString("roleId");
		String giftId = json.getString("kaId");
		String recvDate = json.getString("getDate");
		if(areaId == null || roleID == null || giftId == null || recvDate == null){
			logger.info("发送礼包时发现参数错误，areaId:{}, roleID:{}, giftID:{}, recvDate:{}", areaId, roleID, giftId, recvDate);
			return responseString(UCStateCode.STATE_PARAM_ERROR, request.getId(), null);
		}
		
		IResponseData responseData = ucService.getGift(areaId, roleID, giftId, recvDate);
		Pair<UCStateCode,String> afterOpt = afterOpt(responseData, request.getId());
		logger.info("response role info msg :{}", afterOpt.getValue());
		return afterOpt.getValue();
	}
	
	/**
	 * 礼包编号校验
	 * @param request
	 * @return
	 */
	@RequestMapping(value="checkgift", method={RequestMethod.POST})
	@ResponseBody
	public String checkgift(@RequestBody Request9Game request){
		Pair<UCStateCode,String> beforeOpt = beforeOpt(request);
		UCStateCode stateCode = beforeOpt.getKey();
		String t2 = beforeOpt.getValue();
		if(stateCode != UCStateCode.STATE_OK){
			return t2;
		}
		
		JSONObject json = (JSONObject) JSONObject.parse(t2);
		int gameID = json.getIntValue("gameId");
		String giftId = json.getString("kaId");
		if( giftId == null || gameID == 0){
			logger.info("校验礼包时发现参数错误，gameID:{}, giftID:{}", giftId, gameID);
			return responseString(UCStateCode.STATE_PARAM_ERROR, request.getId(), null);
		}
		
		IResponseData responseData = ucService.checkGiftId(giftId);
		Pair<UCStateCode,String> afterOpt = afterOpt(responseData, request.getId());
		logger.info("response role info msg :{}", afterOpt.getValue());
		return afterOpt.getValue();
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
			logger.info("check data isn't pass, caller is null");
			return UCStateCode.STATE_CALLER_ERROR;
		}
		String params = getDataParam(request);
		if(params == null){
			//没有caller数据，数据有误
			logger.info("check data isn't pass, param is null");
			return UCStateCode.STATE_PARAM_ERROR;
		}
		String sign = Utils.get9GameSign(caller, params);
		if(!StringUtils.equals(sign, request.getSign())){
			//签名检查不通过，返回通知
			logger.info("check data isn't pass, collect sign:{}, request sign:{}", sign, request.getSign());
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
			return MutablePair.of(stateCode, responseString(stateCode, request.getId(), null));
		}
		
		logger.info("Get role info request from 9game, dataStr:{}", request.toString());
		//先进行解密
		String params = getDataParam(request);
		logger.info("before decrypt 9game data string:{}", params);
		String decryptStr = Utils.decrypt9Game(params);
		logger.info("after decrypt 9game data string:{}", decryptStr);
		return MutablePair.of(stateCode, decryptStr);
	}

	
	/**
	 * 在这个实现类里，第一个参数是service处理后的结果，可能为null,
	 * 第二个参数是请求的id，逻辑上不会为null，因为在beforeOpt里已经校验过
	 */
	@Override
	Pair<UCStateCode, String> afterOpt(Object... param) {
		long id =  (Long) param[1];
		if(param[0] == null){
			return MutablePair.of(UCStateCode.STATE_SERVER_ERROR, responseString(UCStateCode.STATE_SERVER_ERROR, id, null));
		}
		IResponseData responseData = (IResponseData) param[0];
		
		UCStateCode respCode = UCStateCode.getCodeByID(responseData.getStateCode());
		if(respCode != UCStateCode.STATE_OK){
			//处理有问题
			return MutablePair.of(respCode, responseString(respCode, id, null));
		}
		//进行加密
		String dataStr = Utils.encrypt9Game(responseData.getData().toJSONString());
		
		//转为json字符串
		String returnStr = responseString(respCode, id, dataStr);
		logger.info("Response role info to 9game:{}", returnStr);
		return MutablePair.of(respCode, returnStr);
	}
}
