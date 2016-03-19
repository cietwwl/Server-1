package com.rw.common;

/*
 * 协议公共工具类
 * @author HC
 * @date 2015年12月14日 下午4:38:38
 * @Description 
 */
public class ProtoUtils {
	/**
	 * 转换协议Id
	 *
	 * @param protoType {@link MsgDef.Command} 协议大类型
	 * @param subProtoIndex 协议某个模块对应的子处理索引 <br/>
	 *            <b>exp:登录平台的子协议索引获取 {@link AccountLoginProtos.eAccountLoginType#ACCOUNT_LOGIN_VALUE}</b>
	 * @return
	 */
	public static int parseProtoId(int protoType, int subProtoIndex) {
		return protoType * 100;
	}
}