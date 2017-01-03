package com.rw.handler.worldboss;

import com.google.protobuf.ByteString;
import com.rw.AsynExecuteTask;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.WorldBossProtos.BuyBuffParam;
import com.rwproto.WorldBossProtos.CommonReqMsg;
import com.rwproto.WorldBossProtos.CommonReqMsg.Builder;
import com.rwproto.WorldBossProtos.CommonRspMsg;
import com.rwproto.WorldBossProtos.FightUpdateParam;
import com.rwproto.WorldBossProtos.RequestType;

public class WorldBossHandler implements RandomMethodIF{

	private static WorldBossHandler handler = new WorldBossHandler();
	private final static String functionName = "世界boss";
	
	public static WorldBossHandler getInstance(){
		return handler;
	}
	
	private final static Command WORLDBOSS = Command.MSG_WORLD_BOSS;
	
	
	/**
	 * 请求同步副本信息
	 * @param client
	 */
	public boolean applyEnterBattleReadyZone(Client client){
		if(client == null){
			return false;
		}
		try {
			Builder req = CommonReqMsg.newBuilder();
			req.setReqType(RequestType.Enter);
			int version = client.getWbDataHolder().getWBDataVersion();
			req.setWbDataVersion(version);
			client.getMsgHandler().sendMsg(WORLDBOSS, req.build().toByteString(), new PrintMsgReciver(WORLDBOSS, functionName, "请求世界boss数据") {
				
				@Override
				public boolean execute(Client client, Response response) {
					ByteString content = response.getSerializedContent();
					try {
						CommonRspMsg resp = CommonRspMsg.parseFrom(content);
						if(resp.getIsSuccess()){
							RobotLog.info(functionName() +"成功");
							return true;
						}else{
							if(resp.getTipMsg() != null){
								RobotLog.info(functionName() +"失败,"+ resp.getTipMsg());
							}else{
								RobotLog.info(functionName() +"失败");
							}
							return false;
						}
						
						
					} catch (Exception e) {
						RobotLog.fail(functionName() + "失败，请求数据时出现异常!", e);
					}
					return false;
				}
				
				private String functionName(){
					return functionName +"["+ protoType + "]";
					
				}
				
			});
			return true;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	
	/**
	 * 请求购买buff
	 * @param client
	 */
	public boolean applyBuyBuff(Client client){
		
		if(client == null){
			return false;
		}
		try {
			
			
			Builder requst = CommonReqMsg.newBuilder();
			requst.setReqType(RequestType.BuyBuff);
			BuyBuffParam.Builder b = BuyBuffParam.newBuilder();
			b.setCfgId("101");
			requst.setBuyBuffParam(b);
			client.getMsgHandler().sendMsg(WORLDBOSS, requst.build().toByteString(), new PrintMsgReciver(WORLDBOSS,functionName, "世界boss请求buybuff") {
				
				@Override
				public boolean execute(Client client, Response response) {
					ByteString content = response.getSerializedContent();
					try {
						CommonRspMsg resp = CommonRspMsg.parseFrom(content);
						if(resp.getIsSuccess()){
							RobotLog.info(functionName() +"成功");
							return true;
						}else{
							if(resp.getTipMsg() != null){
								RobotLog.info(functionName() +"失败,"+ resp.getTipMsg());
							}else{
								RobotLog.info(functionName() +"失败");
							}
							return true;
						}
						
						
					} catch (Exception e) {
						RobotLog.fail(functionName() + "失败，请求数据时出现异常!", e);
					}
					return false;
				}
				
				private String functionName(){
					return functionName +"["+ protoType + "]";
					
				}
			});
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean applyBuyCD(Client client){
		if(client == null){
			return false;
		}
		try {
			
			Builder requst = CommonReqMsg.newBuilder();
			requst.setReqType(RequestType.BuyCD);
			client.getMsgHandler().sendMsg(WORLDBOSS, requst.build().toByteString(), new PrintMsgReciver(WORLDBOSS,functionName, "世界boss请求复活") {
				
				@Override
				public boolean execute(Client client, Response response) {
					ByteString content = response.getSerializedContent();
					try {
						CommonRspMsg resp = CommonRspMsg.parseFrom(content);
						if(resp.getIsSuccess()){
							RobotLog.info(functionName() +"成功");
							return true;
						}else{
							if(resp.getTipMsg() != null){
								RobotLog.info(functionName() +"失败,"+ resp.getTipMsg());
							}else{
								RobotLog.info(functionName() +"失败");
							}
							return true;
						}
						
						
					} catch (Exception e) {
						RobotLog.fail(functionName() + "失败，请求数据时出现异常!", e);
					}
					return false;
				}
				
				private String functionName(){
					return functionName +"["+ protoType + "]";
					
				}
			});
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	public boolean applyBeginBattle(Client client){
		if(client == null){
			return false;
		}
	
		
		try {
			
			Builder requst = CommonReqMsg.newBuilder();
			requst.setReqType(RequestType.FightBegin);
			client.getMsgHandler().sendMsg(WORLDBOSS, requst.build().toByteString(), new PrintMsgReciver(WORLDBOSS,functionName, "世界boss请求开始战斗") {
				
				@Override
				public boolean execute(Client client, Response response) {
					ByteString content = response.getSerializedContent();
					try {
						CommonRspMsg resp = CommonRspMsg.parseFrom(content);
						if(resp.getIsSuccess()){
							RobotLog.info(functionName() +"成功");
							//成功后立即更新一下boss血量
							client.addAsynExecuteResp(new UpdateBossHPTask());
							return true;
						}else{
							if(resp.getTipMsg() != null){
								RobotLog.info(functionName() +"失败,"+ resp.getTipMsg());
							}else{
								RobotLog.info(functionName() +"失败");
							}
							return false;
						}
						
						
					} catch (Exception e) {
						RobotLog.fail(functionName() + "失败，请求数据时出现异常!", e);
					}
					return false;
				}
				
				private String functionName(){
					return functionName +"["+ protoType + "]";
					
				}
			});
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	private boolean applyDecreaseBossHp(Client client, long hurt){
		if(client == null){
			return false;
		}
		try {
			
			
			
			Builder requst = CommonReqMsg.newBuilder();
			requst.setReqType(RequestType.FightUpdate);
			FightUpdateParam.Builder fightParam = FightUpdateParam.newBuilder();
			fightParam.setHurt(hurt);
			requst.setFightUpdateParam(fightParam);
			client.getMsgHandler().sendMsg(WORLDBOSS, requst.build().toByteString(), new PrintMsgReciver(WORLDBOSS,functionName, "世界boss请求扣血") {
				
				@Override
				public boolean execute(Client client, Response response) {
					ByteString content = response.getSerializedContent();
					try {
						CommonRspMsg resp = CommonRspMsg.parseFrom(content);
						if(resp.getIsSuccess()){
							RobotLog.info(functionName() +"成功");
							//成功后结束战斗
							client.addAsynExecuteResp(new EndBossBattleTask());
							return true;
						}else{
							if(resp.getTipMsg() != null){
								RobotLog.info(functionName() +"失败,"+ resp.getTipMsg());
							}else{
								RobotLog.info(functionName() +"失败");
							}
							return false;
						}
						
						
					} catch (Exception e) {
						RobotLog.fail(functionName() + "失败，请求数据时出现异常!", e);
					}
					return false;
				}
				
				private String functionName(){
					return functionName +"["+ protoType + "]";
					
				}
			});
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	private boolean endBattle(Client client){
		if(client == null){
			return false;
		}
		try {
			
			Builder requst = CommonReqMsg.newBuilder();
			requst.setReqType(RequestType.FightEnd);
			client.getMsgHandler().sendMsg(WORLDBOSS, requst.build().toByteString(), new PrintMsgReciver(WORLDBOSS,functionName, "世界boss请求结束战斗") {
				
				@Override
				public boolean execute(Client client, Response response) {
					ByteString content = response.getSerializedContent();
					try {
						CommonRspMsg resp = CommonRspMsg.parseFrom(content);
						if(resp.getIsSuccess()){
							RobotLog.info(functionName() +"成功");
							return true;
						}else{
							if(resp.getTipMsg() != null){
								RobotLog.info(functionName() +"失败,"+ resp.getTipMsg());
							}else{
								RobotLog.info(functionName() +"失败");
							}
							return false;
						}
						
						
					} catch (Exception e) {
						RobotLog.fail(functionName() + "失败，请求数据时出现异常!", e);
					}
					return false;
				}
				
				private String functionName(){
					return functionName +"["+ protoType + "]";
					
				}
			});
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean executeMethod(Client client) {
		return applyEnterBattleReadyZone(client);
	}
	
	
	class UpdateBossHPTask implements AsynExecuteTask {

		@Override
		public void executeResp(Client client) {
			//扣一下boss的血量
			long curLift = client.getWbDataHolder().getCurLift();
			if(curLift > 0){
				applyDecreaseBossHp(client, 5000);
			}else{
				//如果boss已经死，则发送结束
				endBattle(client);
			}

		}

	}
	
	class EndBossBattleTask implements AsynExecuteTask{

		@Override
		public void executeResp(Client client) {
			endBattle(client);
		}
		
	}

}
