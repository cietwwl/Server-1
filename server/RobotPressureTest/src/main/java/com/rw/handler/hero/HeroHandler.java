package com.rw.handler.hero;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.HeroServiceProtos.MsgHeroRequest;
import com.rwproto.HeroServiceProtos.MsgHeroResponse;
import com.rwproto.HeroServiceProtos.eHeroResultType;
import com.rwproto.HeroServiceProtos.eHeroType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/*
 * @author HC
 * @date 2016年3月16日 上午10:09:47
 * @Description 佣兵消息处理
 */
public class HeroHandler {

	private static HeroHandler handler = new HeroHandler();
	private static final Command command = Command.MSG_Hero;
	private static final String functionName = "英雄模块";

	public static HeroHandler getHandler() {
		return handler;
	}

	private class HeroMsgReceiver extends PrintMsgReciver {

		public HeroMsgReceiver(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
		}

		@Override
		public boolean execute(Client client, Response response) {
			ByteString bs = response.getSerializedContent();
			try {
				MsgHeroResponse rsp = MsgHeroResponse.parseFrom(bs);
				if (rsp == null) {
					RobotLog.fail(parseFunctionDesc() + "转换响应消息为null");
					return false;
				} else if (rsp.getEHeroResultType() != eHeroResultType.SUCCESS) {// 不等于成功
					RobotLog.info(parseFunctionDesc() + "失败");
					return false;
				}

				RobotLog.info(parseFunctionDesc() + "成功");
				return true;
			} catch (InvalidProtocolBufferException e) {
				RobotLog.fail(parseFunctionDesc() + "失败", e);
			}
			return false;
		}

		private String parseFunctionDesc() {
			return functionName + "[" + protoType + "] ";
		}
	}

	private HeroHandler() {
	}

	/**
	 * <pre>
	 * 合成佣兵，以姜子牙为例
	 * 姜子牙的模版Id是202001
	 * 魂石Id是704001
	 * </pre>
	 * 
	 * @param client
	 * @return
	 */
	private boolean summonHero(Client client) {
		MsgHeroRequest.Builder req = MsgHeroRequest.newBuilder();
		req.setHeroType(eHeroType.SUMMON_HERO);
		req.setHeroModelId("202001");

		boolean success = client.getMsgHandler().sendMsg(command, req.build().toByteString(), new HeroMsgReceiver(command, functionName, "合成英雄"));
		return success;
	}

	/**
	 * 佣兵进阶升星，需要增加的魂石模版Id是708001
	 * 
	 * @param client
	 * @return
	 */
	public boolean heroUpgrade(Client client) {
		MsgHeroRequest.Builder req = MsgHeroRequest.newBuilder();
		req.setHeroType(eHeroType.EVOLUTION_HERO);
		req.setHeroId(client.getUserId());

		boolean success = client.getMsgHandler().sendMsg(command, req.build().toByteString(), new HeroMsgReceiver(command, functionName, "英雄升星"));
		return success;
	}
}