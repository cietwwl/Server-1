package com.bm.targetSell;

import java.util.ArrayList;
import java.util.List;

import sun.util.resources.OpenListResourceBundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.net.TargetSellOpType;
import com.bm.targetSell.param.RoleAttrs;
import com.bm.targetSell.param.TargetSellAbsArgs;
import com.bm.targetSell.param.TargetSellApplyRoleItemParam;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellHeartBeatParam;
import com.bm.targetSell.param.TargetSellRoleDataParam;
import com.bm.targetSell.param.TargetSellSendRoleItems;
import com.bm.targetSell.param.TargetSellServerErrorParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.MD5;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.manager.ServerSwitch;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.targetSell.BenefitDataDAO;
import com.rwbase.dao.targetSell.BenefitItems;
import com.rwbase.dao.targetSell.TargetSellRecord;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.TargetSellProto.UpdateBenefitScore;
import com.rwproto.TargetSellProto.UpdateBenefitScore.Builder;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAP12Binding;


/**
 * 精准营销管理类
 * @author Alex
 *
 * 2016年8月25日 下午9:29:52
 */
public class TargetSellManager {
	
	
	public final static String publicKey = "6489CD1B7E9AE5BD8311435";
	public final static String appId = "1012";
	public final static int linkId = 90173356;//这个id是随意定的，银汉那边并不做特殊要求
	public final static String ACTION_NAME = "all"; //默认的actionName
	
	public final static String MD5_Str = MD5.getMD5String(appId + "," + linkId + "," + publicKey);

	private static TargetSellManager manager = new TargetSellManager();
	private BenefitDataDAO dataDao;

	private TargetSellManager() {
		dataDao = BenefitDataDAO.getDao();
	}
	
	public static TargetSellManager getInstance(){
		return manager;
	}
	
	public String toJsonString(TargetSellData data){
		return JSON.toJSONString(data);
		
	}
	
	
	public JSONObject toJsonObj(Object obj){
		String str = FastJsonUtil.serialize(obj);
		return JSONObject.parseObject(str);
	}

	/**
	 * 初始化一些默认的公共参数
	 * @param p
	 * @return
	 */
	private <T extends TargetSellHeartBeatParam> T initDefaultParam(T p) {
		if (p == null) {
			return null;
		}
		p.setAppId(appId);
		p.setTime((int) (System.currentTimeMillis() / 1000));
		p.setLinkId(linkId);
		p.setIsTest(ServerSwitch.getTargetSellTest());
		return p;
	}
	
	
	private <T extends TargetSellAbsArgs> T iniCommonParam(T p, String channelID, String userID, String account){
		if(p == null){
			return null;
		}
		p = initDefaultParam(p);
		p.setChannelId(channelID);
		p.setRoleId(userID);
		p.setUserId(account);
		return p;
	}
	
	/**
	 * 获取心跳消息参数
	 * @return
	 */
	public String getHeartBeatMsgData() {
		TargetSellData data = TargetSellData.create(TargetSellOpType.OPTYPE_5001);
		TargetSellHeartBeatParam param = new TargetSellHeartBeatParam();
		param = initDefaultParam(param);
		data.setArgs(manager.toJsonObj(param));
		String jsonString = manager.toJsonString(data);
		return jsonString;
	}
	
	
	/**
	 * <pre>
	 * 玩家充值通知   
	 * 这里会检查是否有目标玩家的精准营销充值前置记录，
	 * 如果存在就发送物品，如果没有就将金额转为优惠积分·
	 * </pre>
	 * @param player
	 * @param charge
	 */
	public void playerCharge(Player player, float charge) {
		TargetSellRecord record = dataDao.get(player.getUserId());
		if(record == null){
			record = new TargetSellRecord();
			record.setBenefitScore((int) charge);
			record.setNextClearScoreTime(getNextRefreshTimeMils());
			dataDao.commit(record);
		}else{
			int score = record.getBenefitScore();
			score += charge;
			record.setBenefitScore(score);
			dataDao.update(record);
		}
		
		//通知前端
//		player.SendMsg(Command., pBuffer);
		
		
		
	}
	
	private ByteString getUpdateBenefitScoreMsgData(int score){
		Builder msg = UpdateBenefitScore.newBuilder();
		msg.setScore(score);
		return msg.build().toByteString();
	}
	
	
	
	/**
	 * 玩家充值后总金额变化通知
	 * @param userID 角色id
	 * @param totalChargeMoney 充值后的总金额
	 */
	public void increaseChargeMoney(String userID, int totalChargeMoney) {
		User user = UserDataDao.getInstance().getByUserId(userID);
		if(user == null){
			GameLog.error("TargetSell", "TargetSell[increaseChargeMoney]", "玩家充值后精准营销系统无法找到User数据", null);
			return;
		}
		TargetSellData data = TargetSellData.create(TargetSellOpType.OPTYPE_5002);
		
		TargetSellRoleDataParam roleData = new TargetSellRoleDataParam();
		roleData = iniCommonParam(roleData, user.getChannelId(), userID, user.getAccount());
		RoleAttrs attrs = new RoleAttrs();
		attrs.setCharge(totalChargeMoney);
		roleData.setArgs(attrs);
		data.setArgs(toJsonObj(roleData));
		sendMsg(toJsonString(data));
	}

	/**
	 * 检查角色特惠积分 一般在角色登录的时候进行此操作
	 * @param player
	 */
	public void checkBenefitScoreAndSynData(Player player){
		//向精准服请求一下，让它知道角色登录
		applyRoleBenefitItem(player);
		
		TargetSellRecord record = dataDao.get(player.getUserId());
		if(record == null){
			return;
		}
		if(System.currentTimeMillis() >= record.getNextClearScoreTime()){
			//到达清0时间，购买物品重置并设置下次清0时间
			//TODO 这里还要添加自动购买物品的逻辑
			
			//-------------------重置--------------------------------
			record.setBenefitScore(0);
			record.setNextClearScoreTime(getNextRefreshTimeMils());
			dataDao.update(record);
		}
		
		//同步到前端
		ClientDataSynMgr.synData(player, record, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
	}
	

	

	/**
	 * 获取下次积分重置时间
	 * @return
	 */
	private long getNextRefreshTimeMils(){
		int dayOfWeek = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.REFRESH_BENEFIT_SCORE_TIME);
		return DateUtils.getTargetDayOfWeekTimeMils(dayOfWeek, 5, true);
	}


	/**
	 * 更新角色的精准营销物品
	 * @param itemData
	 */
	public void updateRoleItems(TargetSellSendRoleItems itemData) {
		TargetSellRecord record = dataDao.get(itemData.getRoleId());
		if(record == null){
			record = new TargetSellRecord();
			record.setBenefitScore(0);
			record.setNextClearScoreTime(getNextRefreshTimeMils());
			record.setRewardItems(new ArrayList<BenefitItems>());
			record.setUserId(itemData.getRoleId());
			dataDao.commit(record);
		}
		record.setRewardItems(itemData.getItems());
		dataDao.update(record);
		
		//更新到前端
		synData(itemData.getRoleId(), record);
	}

	
	/**
	 * 推送消息到前端
	 * @param userId
	 * @param data
	 */
	private void synData(String userId, TargetSellRecord data){
		Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
		if(player == null){
			//角色不在线，不通知
			return;
		}
		ClientDataSynMgr.synData(player, data, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
	}
	
	
	/**
	 * 推送玩家属性或清空玩家所有精准营销物品
	 * @param data
	 * @param msgType 消息类型
	 */
	public void pushRoleAttrOrCleanItems(TargetSellAbsArgs data, int msgType) {
		switch (msgType) {
		case TargetSellOpType.OPTYPE_5003:
			pushRoleAttrsData(data);
			break;
		case TargetSellOpType.OPTYPE_5005:
			cleanRoleAllBenefitItems(data);
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 推送玩家所有属性
	 * @param data
	 */
	public void pushRoleAttrsData(TargetSellAbsArgs data){
		try {
			
			TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5003);
			TargetSellRoleDataParam roleData = new TargetSellRoleDataParam();
			
			User user = UserDataDao.getInstance().getByUserId(data.getRoleId());
			ChargeInfo charge = ChargeInfoHolder.getInstance().get(data.getRoleId());
			Player player = PlayerMgr.getInstance().find(data.getRoleId());
			
			
			roleData = iniCommonParam(roleData, user.getChannelId(), player.getUserId(), user.getAccount());
			
			//TODO 组装角色所有属性，这部分后面要抽取出来
			RoleAttrs attrs = new RoleAttrs();
			attrs.setCharge(charge.getTotalChargeMoney());
			attrs.setLevel(player.getLevel());
			attrs.setVipLevel(player.getVip());
			attrs.setPower(player.getHeroMgr().getFightingAll(player));
			
			roleData.setArgs(attrs);
			sellData.setArgs(toJsonObj(roleData));
			sendMsg(toJsonString(sellData));
			
			
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[pushRoleAttrsData]", "发送角色所有属性到精准服时出现异常", e);
			//TODO 发送参数错误信息反馈回精准服
			buildErrorMsg(TargetSellOpType.ERRORCODE_101, TargetSellOpType.OPTYPE_5003, data);
		}
	}
	
	/**
	 * 清除角色所有精准优惠物品
	 * @param data
	 */
	public void cleanRoleAllBenefitItems(TargetSellAbsArgs data){
		
		try {
			TargetSellRecord record = dataDao.get(data.getRoleId());
			if(record == null){
				//角色这个时候还没有数据   要不要创建
				return;
			}
			List<BenefitItems> list = record.getRewardItems();
			list.clear();
			dataDao.update(record);
			
			synData(data.getRoleId(), record);
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[cleanRoleAllBenefitItems]", "清除角色所有精准优惠物品时出现异常", e);
			buildErrorMsg(TargetSellOpType.ERRORCODE_101, TargetSellOpType.OPTYPE_5005, data);
		}

	}
	
	
	/**
	 * 组装错误消息内容
	 * @param errorCode
	 * @param opType
	 * @param obj
	 */
	public void buildErrorMsg(int errorCode, int opType, Object obj){
		TargetSellServerErrorParam errorData = new TargetSellServerErrorParam();
		errorData.setAppId(appId);
		errorData.setErrorCode(errorCode);
		errorData.setErrorOpType(opType);
		errorData.setOrignalParam(obj);
		processError(errorData);
	}
	
	/**
	 * 接收到错误信息
	 * @param data
	 */
	private void processError(TargetSellServerErrorParam data) {
		TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5008);
		sellData.setArgs(toJsonObj(data));
		sendMsg(toJsonString(sellData));
	}
	
	/**
	 * 向精准服请求玩家优惠物品数据
	 * @param player
	 */
	public void applyRoleBenefitItem(Player player){
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		if(user == null){
			GameLog.error("TargetSell", "TargetSell[applyRoleBenefitItem]", "玩家充值后精准营销系统无法找到User数据", null);
			return;
		}
		
		TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5006);
		TargetSellApplyRoleItemParam roleItemP = new TargetSellApplyRoleItemParam();
		roleItemP = iniCommonParam(roleItemP, user.getChannelId(), player.getUserId(), user.getAccount());
		roleItemP.setActionName(ACTION_NAME);
		sellData.setArgs(toJsonObj(roleItemP));
		sendMsg(toJsonString(sellData));
		
	}
	
	/**
	 * 发送数据到精准服
	 * @param content
	 */
	private void sendMsg(final String content){
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
			
			@Override
			public void run() {
				//TODO 这里接入发送接口
			}
		});
	}

	
	
	public static void main(String[] args){
//		TargetSellData data = TargetSellData.create(TargetSellOpType.OPTYPE_5001);
//		TargetSellHeartBeatParam param = new TargetSellHeartBeatParam();
//		param.setAppId("58");
//		param.setTime((int) (System.currentTimeMillis()/ 1000));
//		data.setArgs(manager.toJsonObj(param));
//		String jsonString = manager.toJsonString(data);
//		
//		System.out.println(jsonString);
//		
//		
//		TargetSellData t = FastJsonUtil.deserialize(jsonString, TargetSellData.class);
//		TargetSellHeartBeatParam p = JSONObject.toJavaObject(t.getArgs(), TargetSellHeartBeatParam.class);
//		
//		System.out.println(t.getOpType()+"_"+t.getSign()+"_" + p.getAppId());
		

	}

	

	

	

	

	
}
