package com.bm.targetSell.net;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.TargetSellAbsArgs;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellHeartBeatParam;
import com.bm.targetSell.param.TargetSellSendRoleItems;
import com.bm.targetSell.param.TargetSellServerErrorParam;
import com.log.GameLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;

/**
 * 与银汉营销系统消息处理器
 * @author Alex
 *
 * 2016年8月25日 下午9:58:34
 */
public class BenefitSystemMsgService {
	
	private static BenefitSystemMsgService handler = new BenefitSystemMsgService();

	public static BenefitSystemMsgService getHandler(){
		return handler;
	}
	
	protected BenefitSystemMsgService() {
	}
	
	public void doTask(String jsonStr){
		JSONObject json = (JSONObject) JSONObject.parse(jsonStr);
		int opType = json.getIntValue("opType");
		String sign = json.getString("sign");
		JSONObject args = json.getJSONObject("args");
		
//		TargetSellData sellData = FastJsonUtil.deserialize(jsonStr, TargetSellData.class);
//		int type = sellData.getOpType();
		try {
			ITargetSellMsgExcutor excutor = null;
			switch (opType) {
			case TargetSellOpType.OPTYPE_5003:
			case TargetSellOpType.OPTYPE_5005:
				excutor = JSONObject.toJavaObject(args, TargetSellAbsArgs.class);
				break;
			case TargetSellOpType.OPTYPE_5004:
				excutor = JSONObject.toJavaObject(args, TargetSellSendRoleItems.class);
				break;
			case TargetSellOpType.OPTYPE_5009:
				excutor = JSONObject.toJavaObject(args, TargetSellServerErrorParam.class);
				break;
			default:
				break;
			}

			if(excutor == null){
				//201 参数有误
				TargetSellManager.getInstance().buildErrorMsg(TargetSellOpType.ERRORCODE_201, opType, jsonStr);
				return;
			}
			
			if(!StringUtils.equals(TargetSellManager.MD5_Str, sign)){
				//检查sign参数不通过
				GameLog.error("TargetSell'", "TargetSellMsgHandler[doTask]", "检查sign参数，发现不正确，发送的sign："
						+ args +"，校验得到的sign值："+ TargetSellManager.MD5_Str, null);
				//202 签名错误
				TargetSellManager.getInstance().buildErrorMsg(TargetSellOpType.ERRORCODE_202, opType, jsonStr);
				return;
			}
			
			excutor.excuteMsg(opType);
			
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellMsgService[doTask]", "解析精准服推送到的消息出现异常", e);
		}
		
	}
	
	
	

}
