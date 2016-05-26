package com.rw.handler.Worship;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.WorshipServiceProtos.EWorshipRequestType;
import com.rwproto.WorshipServiceProtos.EWorshipResultType;
import com.rwproto.WorshipServiceProtos.WorshipRequest;
import com.rwproto.WorshipServiceProtos.WorshipResponse;


public class WorshipHandler {
	private static WorshipHandler handler = new WorshipHandler();

	public static WorshipHandler getHandler() {
		return handler;
	}
	


	
	/**
	 * 膜拜
	 * 
	 * @param client
	 * @return
	 */
	public boolean ArenaWorship(Client client, String message) {
		WorshipRequest.Builder req = WorshipRequest.newBuilder();
		req.setRequestType(EWorshipRequestType.WORSHIP);
		req.setWorshipCareer(ECareer.SwordsMan.getValue());//永远的行者者者者者者者者者者者者者者者者者
		

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_Worship, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_Worship;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					WorshipResponse rsp = WorshipResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("WorshipHandler[send] 转换响应消息为null");
						return false;
					}

					EWorshipResultType result = rsp.getResultType();
					if (result == EWorshipResultType.FAIL) {
						RobotLog.fail("WorshipHandler[send] 服务器处理消息失败 " + result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("WorshipHandler[send] 失败", e);
					return false;
				}
				return true;
			}

		});
		return success;
		
		
	}
	
	public enum ECareer {
		None(0, "新手"),		//新手
		Warrior(1, "力士"), 	//力士...
		SwordsMan(2, "行者"),	//行者...
		Magican(3, "术士"),     //术士...
		Priest(4, "祭祀");	
		
		private int type;
		ECareer(int type, String carrer){
			this.type = type;
		}
		public int getValue(){
			return this.type;
		}
		
		
		
	}
}

