package com.bm.targetSell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.net.TargetSellOpType;
import com.bm.targetSell.param.RoleAttrs;
import com.bm.targetSell.param.TargetSellAbsArgs;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellHeartBeatParam;
import com.bm.targetSell.param.TargetSellRoleDataParam;
import com.bm.targetSell.param.TargetSellSendRoleItems;
import com.bm.targetSell.param.TargetSellServerErrorParam;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.charge.dao.ChargeInfo;
import com.playerdata.charge.dao.ChargeInfoHolder;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.MD5;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rwbase.common.PlayerDataMgr;
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
import com.rwproto.StoreProtos.tagCommodity;


/**
 * 精准营销管理类
 * @author Alex
 *
 * 2016年8月25日 下午9:29:52
 */
public class TargetSellManager {
	
	
	public final static String appKey = "6489CD1B7E9AE5BD8311435";
	public final static String appId = "1012";
	
	public final String Heart_Beat_MD5_Str;
	
	private static TargetSellManager manager = new TargetSellManager();
	private BenefitDataDAO dataDao;
	private TargetSellManager(){
		Heart_Beat_MD5_Str = MD5.getMD5String("appId=" +appId+"||"+ appKey);
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
	 * 获取心跳消息参数
	 * @return
	 */
	public String getHeartBeatMsgData(){
		TargetSellData data = TargetSellData.create(TargetSellOpType.OPTYPE_5001);
		TargetSellHeartBeatParam param = new TargetSellHeartBeatParam();
		param.setAppId(appId);
		param.setTime((int) (System.currentTimeMillis()/ 1000));
		data.setOpType(TargetSellOpType.OPTYPE_5001);
		data.setSign(Heart_Beat_MD5_Str);
		data.setArgs(manager.toJsonObj(param));
		String jsonString = manager.toJsonString(data);
		return jsonString;
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
		roleData.setAppId(appId);
		roleData.setChannelId(user.getChannelId());
		roleData.setRoleId(userID);
		roleData.setUserId(user.getAccount());
		roleData.setTime((int)(System.currentTimeMillis()/1000));
		RoleAttrs attrs = new RoleAttrs();
		attrs.setCharge(totalChargeMoney);
		roleData.setArgs(attrs);
		data.setArgs(toJsonObj(roleData));
		data.setSign(roleData.initMD5Str());
		sendMsg(toJsonString(data));
	}

	/**
	 * 检查角色特惠积分 一般在角色登录的时候进行此操作
	 * @param player
	 */
	public void checkBenefitScoreAndSynData(Player player){
		
		TargetSellRecord record = dataDao.get(player.getUserId());
		if(record == null){
			return;
		}
		if(System.currentTimeMillis() >= record.getNextClearScoreTime()){
			//到达清0时间，重置并设置下次清0时间
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
			
			
			
			roleData.setAppId(appId);
			roleData.setChannelId(user.getChannelId());
			roleData.setRoleId(data.getRoleId());
			roleData.setUserId(user.getAccount());
			roleData.setTime((int)(System.currentTimeMillis()/1000));
			
			//TODO 组装角色所有属性，这部分后面要抽取出来
			RoleAttrs attrs = new RoleAttrs();
			attrs.setCharge(charge.getTotalChargeMoney());
			attrs.setLevel(player.getLevel());
			attrs.setVipLevel(player.getVip());
			attrs.setPower(player.getHeroMgr().getFightingAll(player));
			
			roleData.setArgs(attrs);
			sellData.setArgs(toJsonObj(roleData));
			sellData.setSign(roleData.initMD5Str());
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
		sellData.setSign(data.initMD5Str());
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
//		TargetSellData data = new TargetSellData();
//		TargetSellHeartBeatParam param = new TargetSellHeartBeatParam();
//		param.setAppId("58");
//		param.setTime((int) (System.currentTimeMillis()/ 1000));
//		data.setOpType(5001);
//		data.setSign(param.initMD5Str());
//		data.setArgs(manager.toJsonObj(param));
//		String jsonString = manager.toJsonString(data);
//		
//		System.out.println(jsonString);
//		
//		
//		TargetSellData t = FastJsonUtil.deserialize(jsonString, TargetSellData.class);
//		TargetSellHeartBeatParam p = JSONObject.toJavaObject(t.getArgs(), TargetSellHeartBeatParam.class);
//		
//		System.out.println(p.getAppId()+"_"+p.getTime());
		

	}

	

	

	

	
}
