package com.bm.targetSell.net;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.param.ITargetSellData;
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
public class TargetSellMsgHandler {
	
	private static TargetSellMsgHandler handler = new TargetSellMsgHandler();

	public static TargetSellMsgHandler getHandler(){
		return handler;
	}
	
	private TargetSellMsgHandler() {
	}
	
	public void doTask(String jsonStr){
		TargetSellData sellData = FastJsonUtil.deserialize(jsonStr, TargetSellData.class);
		
		try {
			
			ITargetSellData args = null;
			int type = sellData.getOpType();
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
				return;
			}
			
			
			boolean sign = checkSign(sellData, args);
			if(!sign){
				//202 签名错误
				return;
			}
			args.handlerMsg();
			
		} catch (Exception e) {
			//101 系统内部错误
			
		}
		
	}
	
	
	/**
	 * 检查sign是否正确
	 * @param msgData
	 * @param args TODO
	 * @return
	 */
	private boolean checkSign(TargetSellData msgData, ITargetSellData args){
		if(msgData == null){
			return false;
		}
		
		if(!StringUtils.equals(args.initMD5Str(), msgData.getSign())){
			//检查sign参数不通过
			GameLog.error("TargetSell'", "TargetSellMsgHandler[doTask]", "检查sign参数，发现不正确，发送的sign："
					+msgData.getSign() +"，校验得到的sign值："+ args.initMD5Str(), null);
			
			return false;
		}
		return true;
	}
	

}
