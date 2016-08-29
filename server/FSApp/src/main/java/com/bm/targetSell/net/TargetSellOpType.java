package com.bm.targetSell.net;

/**
 * <pre>
 * 通讯操作码
 * 以下所有参数前都是要添加二个字段:
 * 
 * </pre>
 * @author Alex
 *
 * 2016年8月25日 下午10:15:14
 * 
 */
public interface TargetSellOpType {
	
	/**
	 * <pre>
	 * 客户端心跳接口
	 * 每1分钟,客户端触发一次  server -> sell system
	 * int     optype 接口号 :{@link TargetSellOpType#OPTYPE_5001}
	 * String  sign   数字签名  值:MD5(appId=xxx ||appKey)
	 * args(参数)：
	 * { 
	 *  String  appId 数字
	 * 	int     time 时间戳
	 * }
	 * </pre>
	 */
	public int OPTYPE_5001 = 5001;
	
	
	/**
	 * <pre>
	 * 玩家属性变化接口
	 * 玩家属性发生变化时,游戏服主动把玩家变更属性送到精准营销系统；sever -> sell system
	 * int     opType 接口号 :{@link TargetSellOpType#OPTYPE_5002}
	 * String  sign   数字签名  MD5(appId=xxx&userId=xxx&channelId=xxx&roleId=xxx||appKey)
	 * args(参数)：
	 * { 	
	 * 	String  appId 数字
	 * 	String  userId 用户id
	 * 	String  channelId 主渠道码
	 * 	String  roleId  角色ID  (此参数非必填)	
	 * 	int     time 时间戳
	 * 	JSONObject attrs 具体属性参数
	 * }
	 * </pre>
	 */
	public int OPTYPE_5002 = 5002;
	
	/**
	 * <pre>
	 * 通知推送玩家全部属性
	 * 通知游戏服,需要通过接口:{@link TargetSellOpType#OPTYPE_5002}推送某玩的全部属性; sell system -> server
	 * int  opType 接口号：
	 * String sign 数字签名 值:MD5(appId=xxx&userId=xxx&channelId=xxx&roleId=xxx||appKey)
	 * args(参数):
	 * {
	 * 
	 * }
	 * </pre>
	 */
	public int OPTYPE_5003 = 5003;

}
