package com.rw.handler.chat.attach;

import com.rw.Client;

/**
 * @Author HC
 * @date 2016年8月8日 下午5:37:22
 * @desc
 **/

public interface IAttachParse {
	/**
	 * 处理附件
	 * 
	 * @param client
	 * @param id
	 * @param extraInfo
	 */
	public boolean attachHandler(Client client, String id, String extraInfo);
}