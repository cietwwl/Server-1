package com.bm.targetSell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.timer.Timer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.net.BenefitMsgController;
import com.bm.targetSell.net.TargetSellOpType;
import com.bm.targetSell.param.RoleAttrs;
import com.bm.targetSell.param.TargetSellAbsArgs;
import com.bm.targetSell.param.TargetSellApplyRoleItemParam;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellGetItemParam;
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
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.MD5;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;
import com.rwbase.dao.copy.pojo.ItemInfo;
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
import com.rwproto.MsgDef.Command;
import com.rwproto.TargetSellProto.TargetSellReqMsg;
import com.rwproto.TargetSellProto.TargetSellRespMsg;
import com.rwproto.TargetSellProto.UpdateBenefitScore;
import com.rwproto.TargetSellProto.UpdateBenefitScore.Builder;


/**
 * 精准营销管理类
 * @author Alex
 *
 * 2016年8月25日 下午9:29:52
 */
public class TargetSellManager {
	
	
	public final static String publicKey = "6489CD1B7E9AE5BD8311435";//用于校验的key
	public final static String appId = "1012";//用于校验的游戏服务器app id
	public final static int linkId = GameManager.getZoneId();//这个id是随意定的，银汉那边并不做特殊要求.所以暂时用zoneID
	public final static String ACTION_NAME = "all"; //默认的actionName
	
	public final static String MD5_Str = MD5.getMD5String(appId + "," + linkId + "," + publicKey);

	
	private static TargetSellManager manager = new TargetSellManager();
	private BenefitDataDAO dataDao;

	
	//充值前记录，<userID, Pair<充值的物品组id, 记录时刻>>
	private static final ConcurrentHashMap<String, Pair<Integer, Long>> PreChargeMap = new ConcurrentHashMap<String, Pair<Integer,Long>>();   

	//前置记录有效时间,如果超过这个时间就当作是其他的充值
	private static final long VALIABLE_TIME = 1 * Timer.ONE_MINUTE;
	
	private final static BenefitItemComparator Item_Comparetor = new BenefitItemComparator();
	
	
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
	
	
	public <T extends TargetSellAbsArgs> T iniCommonParam(T p, String channelID, String userID, String account){
		if(p == null){
			return null;
		}
		p = initDefaultParam(p);
		if(StringUtils.isNotBlank(channelID)){
			p.setChannelId(channelID);
		}
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
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		int score = 0;
		TargetSellRecord record = dataDao.get(player.getUserId());
		if(record == null){
			score = (int) charge;
			record = new TargetSellRecord();
			record.setNextClearScoreTime(getNextRefreshTimeMils());
			dataDao.commit(record);
		}else{
			score = record.getBenefitScore();
			score += charge;
		}
		//检查一下是否前置充值记录
		Pair<Integer, Long> preCharge = PreChargeMap.remove(player.getUserId());
		if(preCharge != null){
			//存在前置记录，检查积分是否可以购买
			Map<Integer, BenefitItems> map = record.getItemMap();
			BenefitItems items = map.get(preCharge.getT1());
			if (items.getRecharge() <= score && (preCharge.getT2() + VALIABLE_TIME) >= System.currentTimeMillis()) {
				// 积分可以购买并且还没有超时
				map.remove(preCharge.getT1());
				boolean suc = player.getItemBagMgr().addItem(tranfer2ItemInfo(items));
				if(suc){
					score -= items.getRecharge();
					//通知精准服
					notifyBenefitServerRoleGetItem(player, preCharge.getT1());
				}
			}
		}
		record.setBenefitScore(score);
		dataDao.update(record);
		
		//通知前端
		if(record.getItemMap() != null && !record.getItemMap().isEmpty()){
			player.SendMsg(Command.MSG_BENEFIT_ITEM, getUpdateBenefitScoreMsgData(record.getBenefitScore(), record.getNextClearScoreTime()));
		}
	}
	
	/**
	 * 组装更新优惠积分数据
	 * @param score
	 * @return
	 */
	private ByteString getUpdateBenefitScoreMsgData(int score, long nextRefreshTime){
		Builder msg = UpdateBenefitScore.newBuilder();
		msg.setScore(score);
		msg.setNextRefreshTime(nextRefreshTime);
		return msg.build().toByteString();
	}
	
	
	
	/**
	 * 玩家充值后总金额变化通知
	 * @param userID 角色id
	 * @param totalChargeMoney 充值后的总金额
	 */
	public void increaseChargeMoney(String userID, int totalChargeMoney) {
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
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
		roleData.setAttrs(attrs);
		data.setArgs(toJsonObj(roleData));
		sendMsg(toJsonString(data));
	}

	/**
	 * 检查角色特惠积分 一般在角色登录的时候进行此操作
	 * @param player
	 */
	public void checkBenefitScoreAndSynData(Player player){
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		//向精准服请求一下，让它知道角色登录  ---- 这里改为5002
		pushRoleAllAttrsData(player, null);
		long nowTime = System.currentTimeMillis();
		TargetSellRecord record = dataDao.get(player.getUserId());
		if(record == null){
			return;
		}
		
		//检查一下物品是否已经超时
		Map<Integer, BenefitItems> map = record.getItemMap();
		if(map == null || map.isEmpty()){
			return;
		}
		Map<Integer, BenefitItems> searchMap = new HashMap<Integer, BenefitItems>(map);
		int currentSecnd = (int) (nowTime / 1000);
		for (Iterator<Entry<Integer, BenefitItems>> itr = searchMap.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer, BenefitItems> entry = itr.next();
			if(entry.getValue().getFinishTime() < currentSecnd){
				map.remove(entry.getKey());
				continue;
			}
		}

		
		if(nowTime >= record.getNextClearScoreTime()){
			//到达清0时间，自动购买物品重置并设置下次清0时间
			//TODO 这里还要添加自动购买物品的逻辑,如果还没有开放不购买
//			if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.TARGET_SELL, player)){
//				
//			}
//			List<ItemInfo> list = autoBuy(record);
//			if(!list.isEmpty()){
//				player.getItemBagMgr().addItem(list);
//			}
			//-------------------重置--------------------------------
			record.setBenefitScore(0);
			record.setNextClearScoreTime(getNextRefreshTimeMils());
		}
		dataDao.update(record);
		//同步到前端
		if(map.isEmpty()){
			//发现没有数据，主动请求一下
			applyRoleBenefitItem(player);
			return;
		}
		ClientDataSynMgr.synData(player, record, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
	}
	
	/**
	 * 自动领取物品,物品自动添加入背包
	 * @param record
	 * @return 
	 */
	private List<ItemInfo> autoBuy(TargetSellRecord record){
		Map<Integer, BenefitItems> itemMap = record.getItemMap();
		if(itemMap.isEmpty()){
			return Collections.emptyList();
		}
		Map<Integer, Integer> recieveMap = record.getRecieveMap();
		List<BenefitItems> itemList = new ArrayList<BenefitItems>(itemMap.values());
		Collections.sort(itemList, Item_Comparetor);
		List<ItemInfo> addItems = new ArrayList<ItemInfo>();
		int score = record.getBenefitScore();
		for (BenefitItems i : itemList) {
			
			boolean add = false;
			if(i.getRecharge() <= score){
				
				Integer integer = recieveMap.get(i.getItemGroupId());
				if(integer == null || integer > 0){
					recieveMap.put(i.getItemGroupId(), i.getPushCount() - 1);
					add = true;
				}
				
				if(add){
					score -= i.getRecharge();
					addItems.addAll(tranfer2ItemInfo(i));
					itemMap.remove(i.getItemGroupId());
				}
			}
		}
		
		return addItems;
	}

	/**
	 * 通知精准服角色领取目标物品
	 * @param player
	 * @param itemGroupID
	 */
	private void notifyBenefitServerRoleGetItem(Player player, int itemGroupID){
		if(player == null){
			return;
		}
		TargetSellData data = TargetSellData.create(TargetSellOpType.OPTYPE_5007);
		TargetSellGetItemParam itemP = new TargetSellGetItemParam();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		if(user == null){
			GameLog.error("TargetSell", "TargetSellManager[notifyBenefitServerRoleGetItem]", "角色领取优惠物品，通知精准服时无法找到user数据", null);
			return;
		}
		itemP = iniCommonParam(itemP, user.getChannelId(), player.getUserId(), user.getAccount());
		itemP.setItemGroupId(itemGroupID);
		data.setArgs(toJsonObj(itemP));
		sendMsg(toJsonString(data));
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
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		TargetSellRecord record = dataDao.get(itemData.getRoleId());
		Map<Integer, BenefitItems> map = new HashMap<Integer, BenefitItems>();
		for (BenefitItems i : itemData.getItems()) {
			map.put(i.getItemGroupId(), i);
		}
		if(record == null){
			record = new TargetSellRecord();
			record.setBenefitScore(0);
			record.setNextClearScoreTime(getNextRefreshTimeMils());
			record.setItemMap(map);
			record.setUserId(itemData.getRoleId());
			dataDao.commit(record);
		}else{
			record.setItemMap(map);
			dataDao.update(record);
		}
		
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
			Player player = PlayerMgr.getInstance().find(data.getRoleId());
			pushRoleAllAttrsData(player, data);
			break;
		case TargetSellOpType.OPTYPE_5005:
			cleanRoleAllBenefitItems(data);
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 推送角色属性
	 * @param player
	 * @param attrs   这个可以是全部属性或者是部分属性
	 */
	public void pushRoleAttrData(Player player, RoleAttrs attrs){
		try {
			
			TargetSellData msgData = TargetSellData.create(TargetSellOpType.OPTYPE_5002);
			TargetSellRoleDataParam roleData = new TargetSellRoleDataParam();

			roleData.setAttrs(attrs);
			msgData.setArgs(toJsonObj(roleData));
			sendMsg(toJsonString(msgData));
			
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[pushRoleAttrData]", "发送角色属性到精准服时出现异常", e);
		}
	}
	
	/**
	 * 推送玩家所有属性 
	 * @param player
	 * @param data 
	 */
	public void pushRoleAllAttrsData(Player player, TargetSellAbsArgs data){
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		try {
			
			TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5002);
			TargetSellRoleDataParam roleData = new TargetSellRoleDataParam();
			
			User user = UserDataDao.getInstance().getByUserId(player.getUserId());
			ChargeInfo charge = ChargeInfoHolder.getInstance().get(player.getUserId());
			
			
			roleData = iniCommonParam(roleData, user.getChannelId(), player.getUserId(), user.getAccount());
			
			//TODO 组装角色所有属性，这部分后面要抽取出来
			RoleAttrs attrs = new RoleAttrs();
			attrs.setCharge(charge.getTotalChargeMoney());
			attrs.setLevel(player.getLevel());
			attrs.setVipLevel(player.getVip());
			attrs.setAllPower(player.getHeroMgr().getFightingAll(player));
			attrs.setTeamPower(player.getHeroMgr().getFightingTeam(player));
			attrs.setCreateTime(user.getCreateTime());
			attrs.setLastLoginTime(user.getLastLoginTime());
			
			
			roleData.setAttrs(attrs);
			sellData.setArgs(toJsonObj(roleData));
			sendMsg(toJsonString(sellData));
			
			
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[pushRoleAttrsData]", "发送角色所有属性到精准服时出现异常", e);
			//TODO 发送参数错误信息反馈回精准服
			if(data != null){
				buildErrorMsg(TargetSellOpType.ERRORCODE_101, TargetSellOpType.OPTYPE_5003, data);
			}
		}
	}
	
	/**
	 * 清除角色所有精准优惠物品
	 * @param data
	 */
	public void cleanRoleAllBenefitItems(TargetSellAbsArgs data){
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		try {
			TargetSellRecord record = dataDao.get(data.getRoleId());
			if(record == null){
				//角色这个时候还没有数据   要不要创建
				return;
			}
			Map<Integer, BenefitItems> list = record.getItemMap();
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
	 * 玩家领取物品
	 * @param player
	 * @param itemGroupId
	 * @return
	 */
	public ByteString roleGetItem(Player player, TargetSellReqMsg request) {
		int itemGroupId = request.getItemGroupId();
		TargetSellRespMsg.Builder respMsg = TargetSellRespMsg.newBuilder();
		try {
			respMsg.setReqType(request.getReqType());
			TargetSellRecord record = dataDao.get(player.getUserId());
			
			if(record == null){
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("不存在目标数据");
				return respMsg.build().toByteString();
			}
			Map<Integer, BenefitItems> itemMap = record.getItemMap();
			if(!itemMap.containsKey(itemGroupId)){
				//不存在此道具
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("无法找到目标道具");
				return respMsg.build().toByteString();
			}
			//判断领取次数
			Map<Integer, Integer> recieveMap = record.getRecieveMap();
			
			if(recieveMap!= null && recieveMap.containsKey(itemGroupId) && recieveMap.get(itemGroupId) <= 0){
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("已经达到可领取上限，不可再领取");
				return respMsg.build().toByteString();
			}
			
			
			BenefitItems items = itemMap.remove(itemGroupId);
			//添加道具
			boolean addItem = player.getItemBagMgr().addItem(tranfer2ItemInfo(items));
			if(addItem){
				//通知精准服，玩家领取了道具,同时保存一下可领取次数
				notifyBenefitServerRoleGetItem(player, itemGroupId);
				if(recieveMap == null){
					recieveMap = new HashMap<Integer, Integer>();
				}
				recieveMap.put(itemGroupId, items.getPushCount() - 1);
			}
			dataDao.update(record);
			respMsg.setIsSuccess(true);
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[roleGetItem]", "玩家领取物品时出现异常", e);
		}
		
		return respMsg.build().toByteString();
	}
	
	
	
	/**
	 * 将BenefitItems 转换为List<ItemInfo>
	 * @param item
	 * @return
	 */
	private List<ItemInfo> tranfer2ItemInfo(BenefitItems item){
		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		String itemIds = item.getItemIds();
		String[] itemData = itemIds.split(",");
		for (String subStr : itemData) {
			String[] sb = subStr.split("\\*");
			ItemInfo i;
			if(sb.length < 2){
				 i = new ItemInfo(Integer.parseInt(sb[0].trim()), 1);
			}else{
				i = new ItemInfo(Integer.parseInt(sb[0].trim()), Integer.parseInt(sb[1].trim()));
			}
			itemInfoList.add(i);
		}
		return itemInfoList;
	}
	
	/**
	 * 玩家在精准营销界面申请充值
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString roleChargeItem(Player player, TargetSellReqMsg request) {
		TargetSellRespMsg.Builder respMsg = TargetSellRespMsg.newBuilder();
		respMsg.setReqType(request.getReqType());
		int id = request.getItemGroupId();
		//检查一下是否存在道具组ID
		TargetSellRecord record = dataDao.get(player.getUserId());
		Map<Integer, BenefitItems> map = record.getItemMap();
		if(map.containsKey(id)){
			PreChargeMap.put(player.getUserId(), Pair.Create(id, System.currentTimeMillis()));
			respMsg.setIsSuccess(true);
		}else{
			respMsg.setIsSuccess(false);
			respMsg.setTipsMsg("不存在此奖励记录");
		}
		return respMsg.build().toByteString();
	}
	
	
	/**
	 * 发送数据到精准服
	 * @param content
	 */
	private void sendMsg(final String content){
		if(!ServerSwitch.isOpenTargetSell()){
			return;
		}
		
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
			
			@Override
			public void run() {
				//TODO 这里接入发送接口
				BenefitMsgController.getInstance().addMsg(content);
			}
		});
	}

	

	/**
	 * 物品比较器
	 * @author Alex
	 * 2016年9月22日 下午5:03:14
	 */
	private static class BenefitItemComparator implements Comparator<BenefitItems>{

		@Override
		public int compare(BenefitItems o1, BenefitItems o2) {
			if(o1.getRecharge() >= o2.getRecharge()){
				return -1;
			}
			return 1;
		}
		
	}

	
	
}
