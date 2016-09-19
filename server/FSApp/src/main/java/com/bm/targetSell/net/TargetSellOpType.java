package com.bm.targetSell.net;

import com.bm.targetSell.param.TargetSellApplyRoleItemParam;
import com.bm.targetSell.param.TargetSellSendRoleItems;
import com.bm.targetSell.param.TargetSellAbsArgs;
import com.bm.targetSell.param.TargetSellGetItemParam;
import com.bm.targetSell.param.TargetSellHeartBeatParam;
import com.bm.targetSell.param.TargetSellRoleDataParam;
import com.bm.targetSell.param.TargetSellServerErrorParam;

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
	 * 参数：{@link TargetSellHeartBeatParam}
	 * </pre>
	 */
	public int OPTYPE_5001 = 5001;
	
	
	/**
	 * <pre>
	 * 玩家属性变化接口
	 * 玩家属性发生变化时,游戏服主动把玩家变更属性送到精准营销系统；sever -> sell system
	 * 参数：{@link TargetSellRoleDataParam}
	 * </pre>
	 */
	public int OPTYPE_5002 = 5002;
	
	/**
	 * <pre>
	 * 通知推送玩家全部属性
	 * 通知游戏服,需要通过接口:{@link TargetSellOpType#OPTYPE_5002}推送某玩的全部属性; sell system -> server
	 * 参数：{@link TargetSellAbsArgs}
	 * </pre>
	 */
	public int OPTYPE_5003 = 5003;

	
	/**
	 * <pre>
	 * 精准服通知游戏服推送玩家物品变化接口
	 * 参数:{@link TargetSellSendRoleItems}
	 * </pre>
	 */
	public int OPTYPE_5004 = 5004;
	
	/**
	 * <pre>
	 * 精准服通知清空玩家所有推送物品接口
	 * 参数:{@link TargetSellAbsArgs}
	 * </pre>
	 */
	public int OPTYPE_5005 = 5005;
	
	/**
	 * <pre>
	 * 游戏服向精准服请求玩家推送物品参数
	 * 参数：{@link TargetSellApplyRoleItemParam}
	 * </pre>
	 */
	public int OPTYPE_5006 = 5006;
	
	
	/**
	 * <pre>
	 * 游戏服通知精准服玩家获得物品
	 * 参数:{@link TargetSellGetItemParam}
	 * </pre>
	 */
	public int OPTYPE_5007 = 5007;
	
	
	/**
	 * <pre>
	 * 游戏服通知精准服存在错误
	 * 参数:{@link TargetSellServerErrorParam}
	 * </pre>
	 */
	public int OPTYPE_5008 = 5008;
	
	/**
	 * <pre>
	 * 精准服通知游戏服存在错误
	 * 参数:{@link TargetSellServerErrorParam}
	 * </pre>
	 */
	public int OPTYPE_5009 = 5009;
	
	/**
	 * <pre>
	 * 系统内部错误代码
	 * </pre>
	 */
	public int ERRORCODE_101 = 101;
	
	/**
	 * <pre>
	 * 参数错误代码
	 * </pre>
	 */
	public int ERRORCODE_201 = 201;
	
	
	/**
	 * <pre>
	 * sign签名错误代码
	 * </pre>
	 */
	public int ERRORCODE_202 = 202;
}
