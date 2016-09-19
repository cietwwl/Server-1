package com.bm.targetSell;

import java.util.Calendar;

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
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.MD5;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.service.log.infoPojo.ClientInfo;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.targetSell.BenefitDataDAO;
import com.rwbase.dao.targetSell.TargetSellRecord;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwbase.gameworld.GameWorldFactory;
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
//		dataDao = BenefitDataDAO.getDao();
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
		TargetSellData data = new TargetSellData();
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
		TargetSellData data = new TargetSellData();
		
		TargetSellRoleDataParam roleData = new TargetSellRoleDataParam();
		roleData.setAppId(appId);
		roleData.setChannelId(user.getChannelId());
		roleData.setRoleId(userID);
		roleData.setUserId(user.getAccount());
		roleData.setTime((int)(System.currentTimeMillis()/1000));
		RoleAttrs attrs = new RoleAttrs();
		attrs.setCharge(totalChargeMoney);
		roleData.setArgs(attrs);
		data.setOpType(TargetSellOpType.OPTYPE_5002);
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
			int dayOfWeek = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.REFRESH_BENEFIT_SCORE_TIME);
			long timeMils = DateUtils.getTargetDayOfWeekTimeMils(dayOfWeek, 5, true);
			record.setNextClearScoreTime(timeMils);
			dataDao.update(record);
		}
		
		//同步到前端
		
	}
	
	
	


	/**
	 * 更新角色的精准营销物品
	 * @param itemData
	 */
	public void updateRoleItems(TargetSellSendRoleItems itemData) {
		
	}

	
	/**
	 * 推送玩家属性或清空玩家所有精准营销物品
	 * @param data
	 */
	public void pushRoleAttrOrCleanItems(TargetSellAbsArgs data) {
		
	}
	
	
	/**
	 * 接收到错误信息
	 * @param data
	 */
	public void processError(TargetSellServerErrorParam data) {
		
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
