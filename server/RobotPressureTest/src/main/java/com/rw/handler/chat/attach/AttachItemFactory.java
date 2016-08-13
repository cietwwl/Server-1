package com.rw.handler.chat.attach;

import java.util.EnumMap;

import com.rw.Client;
import com.rw.common.RobotLog;
import com.rwproto.ChatServiceProtos.eAttachItemType;

/**
 * @Author HC
 * @date 2016年8月8日 下午5:49:25
 * @desc
 **/

public class AttachItemFactory {
	private static AttachItemFactory instance = new AttachItemFactory();

	public static AttachItemFactory getInstance() {
		return instance;
	}

	private EnumMap<eAttachItemType, IAttachParse> parseMap = new EnumMap<eAttachItemType, IAttachParse>(eAttachItemType.class);

	private AttachItemFactory() {
		parseMap.put(eAttachItemType.Treasure, new GroupSecretAttachHandler());
	}

	/**
	 * 处理附件
	 * 
	 * @param type
	 * @param client
	 * @param id
	 * @param extraInfo
	 * @return
	 */
	public boolean attachHandler(eAttachItemType type, Client client, String id, String extraInfo) {
		if (!parseMap.containsKey(type)) {
			RobotLog.fail(String.format("不能获取到聊天附件类型为%s的处理类，发送处理失败", type));
			return false;
		}

		return parseMap.get(type).attachHandler(client, id, extraInfo);
	}
}