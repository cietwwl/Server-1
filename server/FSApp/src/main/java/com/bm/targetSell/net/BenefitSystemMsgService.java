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
	
	private BenefitSystemMsgService() {
	}
	
	public void doTask(String jsonStr){
		TargetSellData sellData = FastJsonUtil.deserialize(jsonStr, TargetSellData.class);
		
		int type = sellData.getOpType();
		try {
			
			ITargetSellMsgHandler args = null;
			switch (type) {
			case TargetSellOpType.OPTYPE_5003:
			case TargetSellOpType.OPTYPE_5005:
				args = JSONObject.toJavaObject(sellData.getArgs(), TargetSellAbsArgs.class);
				break;
			case TargetSellOpType.OPTYPE_5004:
				args = JSONObject.toJavaObject(sellData.getArgs(), TargetSellSendRoleItems.class);
				break;
			case TargetSellOpType.OPTYPE_5009:
				args = JSONObject.toJavaObject(sellData.getArgs(), TargetSellServerErrorParam.class);
				break;

			default:
				break;
			}

			if(args == null){
				//201 参数有误
				TargetSellManager.getInstance().buildErrorMsg(TargetSellOpType.ERRORCODE_201, type, jsonStr);
				return;
			}
			
			if(!StringUtils.equals(TargetSellManager.MD5_Str, sellData.getSign())){
				//检查sign参数不通过
				GameLog.error("TargetSell'", "TargetSellMsgHandler[doTask]", "检查sign参数，发现不正确，发送的sign："
						+sellData.getSign() +"，校验得到的sign值："+ TargetSellManager.MD5_Str, null);
				//202 签名错误
				TargetSellManager.getInstance().buildErrorMsg(TargetSellOpType.ERRORCODE_202, type, jsonStr);
				return;
			}
			
			args.handlerMsg(type);
			
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellMsgService[doTask]", "解析精准服推送到的消息出现异常", e);
		}
		
	}
	
	
	

}
