package com.rw.service.log.behavior;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.protobuf.ProtocolMessageEnum;
import com.rwproto.MsgDef.Command;

public class GameBehaviorRecord {

	private final String userId;
	private final Command command;
	private final ProtocolMessageEnum msgType;
	private final int viewId;
	private int mapId;

	public GameBehaviorRecord(String userId, Command command, ProtocolMessageEnum msgType, int viewId) {
		super();
		this.userId = userId;
		this.command = command;
		this.msgType = msgType;
		this.viewId = viewId;
	}

	public String getUserId() {
		return userId;
	}

	public Command getCommand() {
		return command;
	}

	public ProtocolMessageEnum getMsgType() {
		return msgType;
	}

	public int getViewId() {
		return viewId;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getTypeId() {
		// String mapId;
		// if (typeList.size() >= 5) {
		// Object oMapId = typeList.get(4);
		// mapId = oMapId.toString();
		// } else {
		// mapId = viewId.toString();
		// }
		if (mapId > 0) {
			return String.valueOf(mapId);
		} else if (viewId > 0) {
			return String.valueOf(viewId);
		} else {
			return "0";
		}
	}
	
//	public static void main(String[] args) throws UnknownHostException {
//		System.setProperty("java.net.preferIPv4Stack", "false");
//		System.setProperty("java.net.preferIPv6Address", "true");
//		InetAddress add = InetAddress.getByName("micmiu.com");
//		System.out.println(add.getHostAddress());
//		System.out.println(add.getHostName());
//	}

	public static void main(String[] args) throws UnknownHostException {
		System.setProperty("java.net.preferIPv6Addresses", "true");
		InetAddress add = InetAddress.getLocalHost();
		System.out.println(add.getClass());
		System.out.println(add.getHostAddress());
	}
}
