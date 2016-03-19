package com.rw.common;

import com.rw.Client;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 *业务模块协议命令处理
 * @author HC
 * @date 2015年12月14日 下午4:20:51
 * @Description 
 */
public interface MsgReciver {
	
	
	public Command getCmd();
	/**
	 * 处理协议
	 * 
	 * @param client
	 * @param response
	 */
	public boolean execute(Client client, Response response);
}