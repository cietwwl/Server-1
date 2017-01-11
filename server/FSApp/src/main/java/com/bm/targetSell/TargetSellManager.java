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
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.net.BenefitMsgController;
import com.bm.targetSell.net.TargetSellOpType;
import com.bm.targetSell.param.TargetSellAbsArgs;
import com.bm.targetSell.param.TargetSellApplyRoleItemParam;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellGetItemParam;
import com.bm.targetSell.param.TargetSellHeartBeatParam;
import com.bm.targetSell.param.TargetSellHeroChange;
import com.bm.targetSell.param.TargetSellRoleChange;
import com.bm.targetSell.param.TargetSellRoleDataParam;
import com.bm.targetSell.param.TargetSellSendRoleItems;
import com.bm.targetSell.param.TargetSellServerErrorParam;
import com.bm.targetSell.param.attrs.AttrsProcessMgr;
import com.bm.targetSell.param.attrs.EAchieveType;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.hero.core.FSHero;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.MD5;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.manager.ServerSwitch;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.MapItemStoreFactory;
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
import com.rwbase.gameworld.PlayerPredecessor;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.TargetSellProto.TargetSellReqMsg;
import com.rwproto.TargetSellProto.TargetSellRespMsg;

/**
 * 精准营销管理类
 * 
 * @author Alex
 *
 *         2016年8月25日 下午9:29:52
 */
public class TargetSellManager {

	private Logger logger = Logger.getLogger("targetSellLogger");
	public final static String publicKey = "6489CD1B7E9AE5BD8311435";// 用于校验的key
	public final static String appId = "1012";// 用于校验的游戏服务器app id
	public static int linkId = createLinkedId();// 这个id是随意定的，银汉那边并不做特殊要求.所以暂时用zoneID
	public final static String ACTION_NAME = "all"; // 默认的actionName

	public static String MD5_Str = MD5.getMD5String(appId + "," + linkId + "," + publicKey);

	private static TargetSellManager manager = new TargetSellManager();

	private static int createLinkedId() {
		int id = (int) (System.currentTimeMillis() & (0x7fffffff));
		return id;
	}

	// 充值前记录，<userID, Pair<充值的物品组id, 记录时刻>>
	private static ConcurrentHashMap<String, Pair<Integer, Long>> PreChargeMap = new ConcurrentHashMap<String, Pair<Integer, Long>>();

	// 前置记录有效时间,如果超过这个时间就当作是其他的充值
	private static final long VALIABLE_TIME = 1 * Timer.ONE_MINUTE;

	private final static BenefitItemComparator Item_Comparetor = new BenefitItemComparator();

	public static ConcurrentHashMap<String, TargetSellRoleChange> RoleAttrChangeMap = new ConcurrentHashMap<String, TargetSellRoleChange>();

	/*
	 * key=heroID, value=ChangeAttr 这个缓存是保存部分无法找到角色id的英雄改变属性，每次RoleAttrChangeMap发送的时候会进入此缓存检查是否存在自己的英雄属性 角色下线也会做检索移除操作
	 */
	public static ConcurrentHashMap<String, TargetSellHeroChange> HeroAttrChangeMap = new ConcurrentHashMap<String, TargetSellHeroChange>();

	public TargetSellManager() {

	}

	public static TargetSellManager getInstance() {
		return manager;
	}

	public String toJsonString(TargetSellData data) {
		return JSON.toJSONString(data);

	}

	public JSONObject toJsonObj(Object obj) {
		String str = FastJsonUtil.serialize(obj);
		return JSONObject.parseObject(str);
	}

	/**
	 * 初始化一些默认的公共参数
	 * 
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

	public <T extends TargetSellAbsArgs> T initCommonParam(T p, String channelID, String userID, String account) {
		if (p == null) {
			return null;
		}
		p = initDefaultParam(p);
		if (StringUtils.isNotBlank(channelID)) {
			p.setChannelId(channelID);
		}
		p.setRoleId(userID);
		p.setUserId(account);
		return p;
	}

	/**
	 * 获取心跳消息参数
	 * 
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
	 * 
	 * @param player
	 * @param charge
	 */
	public void playerCharge(Player player, float charge) {
		try {
			if (!ServerSwitch.isOpenTargetSell()) {
				return;
			}
			int score = 0;
			BenefitDataDAO dataDao = BenefitDataDAO.getDao();
			TargetSellRecord record = dataDao.get(player.getUserId());

			StringBuilder sb = new StringBuilder();
			score = record.getBenefitScore();
			sb.append("玩家[").append(player.getUserName()).append("],id:[").append(player.getUserId()).append("],充值前精准营销积分：[").append(score)
			.append("],充值金额：[").append(charge).append("]。");
			score += charge;
			// 检查一下是否前置充值记录
			Pair<Integer, Long> preCharge = PreChargeMap.remove(player.getUserId());
			BenefitItems items = null;
			if (preCharge != null) {
				// 存在前置记录，检查积分是否可以购买
				Map<Integer, BenefitItems> map = record.getItemMap();
				items = map.remove(preCharge.getT1());
				if (items != null && items.getRecharge() <= score && (preCharge.getT2() + VALIABLE_TIME) >= System.currentTimeMillis()) {
					// 积分可以购买并且还没有超时
					sb.append("发现玩家有主动请求购买物品请求，物品组id为：[").append(items.getItemGroupId()).append("],物品为：[").append(items.getItemIds())
					.append("],消耗积分：[").append(items.getRecharge()).append("].");
					String mailID = PublicDataCfgDAO.getInstance().getPublicDataStringValueById(PublicData.BENEFIT_ITEM_BUY_SUC);
					Map<Integer, Integer> mailAttach = new HashMap<Integer, Integer>();

					List<ItemInfo> info = tranfer2ItemInfo(items);
					for (ItemInfo item : info) {
						
						Integer num = mailAttach.get(item.getItemID());
						if (num == null) {
							mailAttach.put(item.getItemID(), item.getItemNum());
						} else {
							mailAttach.put(item.getItemID(), item.getItemNum() + num);
						}
					}
					boolean suc = EmailUtils.sendEmail(player.getUserId(), mailID, mailAttach);
					sb.append("发送物品邮件成功：[" + suc + "].");
					if (suc) {
						score -= items.getRecharge();
						// 通知精准服
						notifyBenefitServerRoleGetItem(player, preCharge.getT1());
					}
				}
			}
			sb.append("检查当前充值后精准营销积分：[" + score + "]");
			logger.info(sb.toString());
			record.setBenefitScore(score);
			dataDao.update(record);

			// 通知前端
			ClientDataSynMgr.synData(player, record, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);

		} catch (Throwable e) {
			GameLog.error(LogModule.COMMON.getName(), "TargetSellManager[playerCharge]", "精准营销角色充值判断出现异常！", e);
		}
	}

	/**
	 * 组装更新优惠积分数据
	 * 
	 * @param score
	 * @param item TODO
	 * @return
	 */
//	private ByteString getUpdateBenefitScoreMsgData(int score, long nextRefreshTime, BenefitItems item) {
//		Builder msg = UpdateBenefitScore.newBuilder();
//		msg.setScore(score);
//		msg.setNextRefreshTime(nextRefreshTime);
//		if (item != null) {
//			// msg.setDataStr(item.getItemIds());
//			// msg.setItemGroupId(item.getItemGroupId());
//		}
//		return msg.build().toByteString();
//	}

	/**
	 * 检查角色特惠积分 一般在角色登录的时候进行此操作
	 * 
	 * @param player
	 */
	public void checkBenefitScoreAndSynData(Player player) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.TARGET_SELL, player)) {
			return;
		}
		// 向精准服请求一下，让它知道角色登录 ---- 这里改为5002
		pushRoleLoginData(player);
		BenefitDataDAO dataDao = BenefitDataDAO.getDao();
		long nowTime = System.currentTimeMillis();
		TargetSellRecord record = dataDao.get(player.getUserId());
		if (record == null) {
			return;
		}

		// 检查一下物品是否已经超时
		Map<Integer, BenefitItems> map = record.getItemMap();
		if (map == null || map.isEmpty()) {
			return;
		}
		Map<Integer, BenefitItems> searchMap = new HashMap<Integer, BenefitItems>(map);
		int currentSecnd = (int) (nowTime / 1000);
		for (Iterator<Entry<Integer, BenefitItems>> itr = searchMap.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer, BenefitItems> entry = itr.next();
			if (entry.getValue().getFinishTime() < currentSecnd) {
				map.remove(entry.getKey());
				continue;
			}
		}

		// System.out.println("Next clear score time:" + DateUtils.getDateTimeFormatString(record.getNextClearScoreTime(), "yyyy-MM-dd hh:mm:ss"));
		if (nowTime >= record.getNextClearScoreTime() && record.getBenefitScore() != 0) {
			// 到达清0时间，自动购买物品重置并设置下次清0时间
			StringBuilder sb = new StringBuilder();
			sb.append("角色：[").append(player.getUserName()).append("],id:[").append(player.getUserId())
			.append("]积分清0时间：[").append(DateUtils.getDateTimeFormatString(record.getNextClearScoreTime(), "yyyy-MM-dd HH:mm:ss"))
			.append("],当前积分：[").append(record.getBenefitScore()).append("],");
			Map<Integer, Integer> itemMap = autoBuy(record);
			String mailID;
			if (!itemMap.isEmpty()) {
				// 这里通过邮件发送
				mailID = PublicDataCfgDAO.getInstance().getPublicDataStringValueById(PublicData.BENEFIT_ITEM_AUTO_BUY_SUC);
				EmailUtils.sendEmail(player.getUserId(), mailID, itemMap);
			} else {
				mailID = PublicDataCfgDAO.getInstance().getPublicDataStringValueById(PublicData.BENEFIT_ITEM_AUTO_BUY_FAIL);
				EmailUtils.sendEmail(player.getUserId(), mailID);
			}
			// -------------------重置--------------------------------
			long nextRefreshTimeMils = getNextRefreshTimeMils();
			record.setBenefitScore(0);
			record.setNextClearScoreTime(nextRefreshTimeMils);
			sb.append("是否要发送自动购买道具：[").append(!itemMap.isEmpty()).append("],积分下次重置时间：").append(DateUtils.getDateTimeFormatString(nextRefreshTimeMils, "yyyy-MM-dd HH:mm:ss"));
			logger.info(sb.toString());
		}
		dataDao.update(record);
		// 同步到前端
		if (map.isEmpty()) {
			// 发现没有数据，主动请求一下
			applyRoleBenefitItem(player);
			return;
		}
		ClientDataSynMgr.synData(player, record, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
	}

	/**
	 * 自动领取物品,物品通过邮件发送
	 * 
	 * @param record
	 * @return
	 */
	private Map<Integer, Integer> autoBuy(TargetSellRecord record) {
		Map<Integer, BenefitItems> itemMap = record.getItemMap();
		if (itemMap.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<Integer, Integer> recieveMap = record.getRecieveMap();
		if (recieveMap == null) {
			recieveMap = new HashMap<Integer, Integer>();
			record.setRecieveMap(recieveMap);
		}
		List<BenefitItems> itemList = new ArrayList<BenefitItems>(itemMap.values());
		Collections.sort(itemList, Item_Comparetor);
		Map<Integer, Integer> items = new HashMap<Integer, Integer>();
		int score = record.getBenefitScore();
		StringBuilder sb = new StringBuilder("检查角色id["+record.getUserId()+"]的自动购买物品"); 
		for (BenefitItems i : itemList) {

			boolean add = false;
			if (i.getRecharge() <= score) {
				sb.append("||添加道具组id：[").append(i.getItemGroupId()).append("]，道具列表：[")
				.append(i.getItemIds()).append("],消耗积分：[").append(i.getRecharge()).append("]");
				Integer integer = recieveMap.get(i.getItemGroupId());
				if (integer == null || integer > 0) {
					recieveMap.put(i.getItemGroupId(), i.getPushCount() - 1);
					add = true;
				}

				if (add) {
					score -= i.getRecharge();
					List<ItemInfo> info = tranfer2ItemInfo(i);
					for (ItemInfo item : info) {
						Integer num = items.get(item.getItemID());
						if (num == null) {
							items.put(item.getItemID(), item.getItemNum());
						} else {
							items.put(item.getItemID(), item.getItemNum() + num);
						}

					}
					itemMap.remove(i.getItemGroupId());
				}
			}
		}

		logger.info(sb.toString());
		return items;
	}

	/**
	 * 通知精准服角色领取目标物品
	 * 
	 * @param player
	 * @param itemGroupID
	 */
	private void notifyBenefitServerRoleGetItem(Player player, int itemGroupID) {
		if (player == null) {
			return;
		}
		TargetSellData data = TargetSellData.create(TargetSellOpType.OPTYPE_5007);
		TargetSellGetItemParam itemP = new TargetSellGetItemParam();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		if (user == null) {
			GameLog.error("TargetSell", "TargetSellManager[notifyBenefitServerRoleGetItem]", "角色领取优惠物品，通知精准服时无法找到user数据", null);
			return;
		}
		itemP = initCommonParam(itemP, user.getChannelId(), player.getUserId(), user.getAccount());
		itemP.setItemGroupId(itemGroupID);
		data.setArgs(toJsonObj(itemP));
		sendMsg(toJsonString(data));
	}

	/**
	 * 获取下次积分重置时间
	 * 
	 * @return
	 */
	public long getNextRefreshTimeMils() {
		int dayOfWeek = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.REFRESH_BENEFIT_SCORE_TIME);
		return DateUtils.getTargetDayOfWeekTimeMils(dayOfWeek, 5, true);
	}

	/**
	 * 更新角色的精准营销物品
	 * 
	 * @param itemData
	 */
	public void updateRoleItems(TargetSellSendRoleItems itemData) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		BenefitDataDAO dataDao = BenefitDataDAO.getDao();
		TargetSellRecord record = dataDao.get(itemData.getRoleId());
		Map<Integer, BenefitItems> map = new HashMap<Integer, BenefitItems>();
		for (BenefitItems i : itemData.getItems()) {
			map.put(i.getItemGroupId(), i);
		}
		if (record == null) {
			record = new TargetSellRecord();
			record.setBenefitScore(0);
			record.setNextClearScoreTime(getNextRefreshTimeMils());
			record.setItemMap(map);
			record.setRecieveMap(new HashMap<Integer, Integer>());
			record.setUserId(itemData.getRoleId());
			dataDao.commit(record);
		} else {
			record.setItemMap(map);
			dataDao.update(record);
		}

		// 更新到前端
		synData(itemData.getRoleId(), record);
	}

	/**
	 * 推送消息到前端
	 * 
	 * @param userId
	 * @param data
	 */
	private void synData(String userId, TargetSellRecord data) {
		Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
		if (player == null) {
			// 角色不在线，不通知
			return;
		}
		ClientDataSynMgr.synData(player, data, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
	}

	/**
	 * 推送玩家属性或清空玩家所有精准营销物品
	 * 
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

	public void notifyHeroAttrsChange(String heroID, EAchieveType eRoleAttrID) {
		TargetSellHeroChange change = HeroAttrChangeMap.get(heroID);
		if (change == null) {
			change = new TargetSellHeroChange();
			TargetSellHeroChange old = HeroAttrChangeMap.putIfAbsent(heroID, change);
			if (old != null) {
				change = old;
			}
		}
		change.getChangeList().add(eRoleAttrID);
	}

	/**
	 * 通知属性改变
	 * 
	 * @param player
	 * @param cfgID
	 */
	public void notifyRoleAttrsChange(String userId, String cfgID) {
		TargetSellRoleChange targetSellRoleChange = RoleAttrChangeMap.get(userId);
		if (targetSellRoleChange == null) {
			targetSellRoleChange = new TargetSellRoleChange(userId, DateUtils.getSecondLevelMillis());
			// 这里做个判断，避免多线程问题
			TargetSellRoleChange old = RoleAttrChangeMap.putIfAbsent(userId, targetSellRoleChange);
			if (old != null) {
				targetSellRoleChange = old;
			}
		}
		targetSellRoleChange.addChange(cfgID);

	}

	public void packHeroChangeAttr(String userId) {
		GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerPredecessor() {

			@Override
			public void run(String userId) {
				checkAndPackHeroChanged(userId, false);
			}
		});
	}

	public void checkAndPackHeroChanged(String userId, boolean forceRead) {
		TargetSellRoleChange value = null;
		if (forceRead || RoleAttrChangeMap.containsKey(userId)) {
			value = RoleAttrChangeMap.remove(userId);
		}
		MapItemStore<FSHero> heroStore;
		if (forceRead) {
			heroStore = MapItemStoreFactory.getHeroDataCache().getFromMemoryForRead(userId);
		} else {
			heroStore = MapItemStoreFactory.getHeroDataCache().getMapItemStore(userId, FSHero.class);
		}
		List<String> heroIdList = heroStore.getReadOnlyKeyList();
		for (int i = heroIdList.size(); --i >= 0;) {
			String heroId = heroIdList.get(i);
			value = checkAndPack(userId, heroId, forceRead, value);
		}
		value = checkAndPack(userId, userId, forceRead, value);
		if (value != null) {
			packAndSendMsg(value);
		}

		checkAndRemovePreChangeRecord(userId);
	}

	private void checkAndRemovePreChangeRecord(String userID) {
		Pair<Integer, Long> pair = PreChargeMap.get(userID);
		if (pair != null) {
			long passTime = pair.getT2() + Timer.ONE_MINUTE * 10;
			if (passTime < System.currentTimeMillis()) {
				PreChargeMap.remove(userID);// 清除超时记录
			}
		}
	}

	private TargetSellRoleChange checkAndPack(String userId, String heroId, boolean forceRead, TargetSellRoleChange value) {
		TargetSellHeroChange heroChanged;
		if (forceRead || HeroAttrChangeMap.containsKey(heroId)) {
			heroChanged = HeroAttrChangeMap.remove(heroId);
		} else {
			heroChanged = null;
		}
		if (heroChanged == null) {
			return value;
		}
		if (value == null) {
			value = new TargetSellRoleChange(userId, DateUtils.getSecondLevelMillis());
		}
		AttrsProcessMgr.getInstance().addHeroChangeAttrs(userId, heroId, heroChanged.getChangeList(), value);
		return value;
	}

	public void packAndSendMsg(TargetSellRoleChange value) {

		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}

		String userId = value.getUserId();
		List<String> list = value.getChangeList();
		Player player = PlayerMgr.getInstance().find(userId);
		Map<String, Object> attrs = AttrsProcessMgr.getInstance().packChangeAttr(player, list);

		try {

			TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5002);
			TargetSellRoleDataParam roleData = new TargetSellRoleDataParam();

			User user = UserDataDao.getInstance().getByUserId(player.getUserId());

			roleData = initCommonParam(roleData, user.getChannelId(), player.getUserId(), user.getAccount());

			roleData.setAttrs(attrs);
			sellData.setArgs(toJsonObj(roleData));
			sendMsg(toJsonString(sellData));
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[pushRoleAttrsData]", "发送角色所有属性到精准服时出现异常", e);
		}

	}

	/**
	 * 推送玩家所有属性 到精准服
	 * 
	 * @param player
	 * @param data
	 */
	public void pushRoleAllAttrsData(Player player, TargetSellAbsArgs data) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		Map<String, Object> attrs = AttrsProcessMgr.getInstance().packAllAttrs(player);
		pushData(player, attrs);

	}

	public void pushRoleLoginData(Player player) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		Map<String, Object> attrs = AttrsProcessMgr.getInstance().packLoginAttr(player);
		pushData(player, attrs);

	}

	private void pushData(Player player, Map<String, Object> attrs) {
		try {

			TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5002);
			TargetSellRoleDataParam roleData = new TargetSellRoleDataParam();

			User user = UserDataDao.getInstance().getByUserId(player.getUserId());

			roleData = initCommonParam(roleData, user.getChannelId(), player.getUserId(), user.getAccount());

			roleData.setAttrs(attrs);
			sellData.setArgs(toJsonObj(roleData));
			sendMsg(toJsonString(sellData));

		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[pushRoleAttrsData]", "发送角色所有属性到精准服时出现异常", e);
		}
	}

	/**
	 * 清除角色所有精准优惠物品
	 * 
	 * @param data
	 */
	public void cleanRoleAllBenefitItems(TargetSellAbsArgs data) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		try {
			BenefitDataDAO dataDao = BenefitDataDAO.getDao();
			TargetSellRecord record = dataDao.get(data.getRoleId());
			if (record == null) {
				logger.error("精准服通知清除角色所有精准优惠物品时发现没有角色数据！");
				return;
			}
			Map<Integer, BenefitItems> list = record.getItemMap();
			list.clear();
			dataDao.update(record);
			logger.info("精准服通知清除角色所有精准优惠物品成功！");
			synData(data.getRoleId(), record);
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[cleanRoleAllBenefitItems]", "清除角色所有精准优惠物品时出现异常", e);
			buildErrorMsg(TargetSellOpType.ERRORCODE_101, TargetSellOpType.OPTYPE_5005, data);
		}

	}

	/**
	 * 组装错误消息内容
	 * 
	 * @param errorCode
	 * @param opType
	 * @param obj
	 */
	public void buildErrorMsg(int errorCode, int opType, Object obj) {
		TargetSellServerErrorParam errorData = new TargetSellServerErrorParam();
		errorData.setAppId(appId);
		errorData.setErrorCode(errorCode);
		errorData.setErrorOpType(opType);
		errorData.setOrignalParam(obj);
		processError(errorData);
	}

	/**
	 * 接收到错误信息
	 * 
	 * @param data
	 */
	private void processError(TargetSellServerErrorParam data) {
		TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5008);
		sellData.setArgs(toJsonObj(data));
		sendMsg(toJsonString(sellData));
	}

	/**
	 * 向精准服请求玩家优惠物品数据
	 * 
	 * @param player
	 */
	public void applyRoleBenefitItem(Player player) {
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		if (user == null) {
			GameLog.error("TargetSell", "TargetSell[applyRoleBenefitItem]", "玩家充值后精准营销系统无法找到User数据", null);
			return;
		}

		TargetSellData sellData = TargetSellData.create(TargetSellOpType.OPTYPE_5006);
		TargetSellApplyRoleItemParam roleItemP = new TargetSellApplyRoleItemParam();
		roleItemP = initCommonParam(roleItemP, user.getChannelId(), player.getUserId(), user.getAccount());
		roleItemP.setActionName(ACTION_NAME);
		sellData.setArgs(toJsonObj(roleItemP));
		sendMsg(toJsonString(sellData));

	}

	/**
	 * 玩家领取物品
	 * 
	 * @param player
	 * @param itemGroupId
	 * @return
	 */
	public ByteString roleGetItem(Player player, TargetSellReqMsg request) {
		int itemGroupId = request.getItemGroupId();
		TargetSellRespMsg.Builder respMsg = TargetSellRespMsg.newBuilder();
		try {
			respMsg.setReqType(request.getReqType());
			BenefitDataDAO dataDao = BenefitDataDAO.getDao();
			TargetSellRecord record = dataDao.get(player.getUserId());

			if (record == null) {
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("不存在目标数据");
				return respMsg.build().toByteString();
			}
			Map<Integer, BenefitItems> itemMap = record.getItemMap();
			if (!itemMap.containsKey(itemGroupId)) {
				// 不存在此道具
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("无法找到目标道具");
				return respMsg.build().toByteString();
			}
			// 判断领取次数
			Map<Integer, Integer> recieveMap = record.getRecieveMap();

			if (recieveMap != null && recieveMap.containsKey(itemGroupId) && recieveMap.get(itemGroupId) <= 0) {
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("已经达到可领取上限，不可再领取");
				return respMsg.build().toByteString();
			}
			BenefitItems items = itemMap.get(itemGroupId);
//			BenefitItems items = itemMap.remove(itemGroupId);
			if(items == null){
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("无法找到目标道具");
				return respMsg.build().toByteString();
			}
			//增加积分检查，避免没有积分的时候也领取
			int benefitScore = record.getBenefitScore();
			if(benefitScore < items.getRecharge()){
				respMsg.setIsSuccess(false);
				respMsg.setTipsMsg("积分不足，无法领取此奖励！");
				return respMsg.build().toByteString();
			}
			
			
			StringBuilder sb = new StringBuilder("玩家["+player.getUserName()+"],id:["+player.getUserId()
					+"],主动领取物品，当前积分：" + record.getBenefitScore()+",道具组id:" + itemGroupId + ",道具列表："+ items.getItemIds()
					+",消耗积分：" + items.getRecharge());
			// 添加道具
			boolean addItem = ItemBagMgr.getInstance().addItem(player, tranfer2ItemInfo(items));
			if (addItem) {
				itemMap.remove(itemGroupId);//删除目标道具数据
				// 通知精准服，玩家领取了道具,同时保存一下可领取次数
				notifyBenefitServerRoleGetItem(player, itemGroupId);
				if (recieveMap == null) {
					recieveMap = new HashMap<Integer, Integer>();
				}
				recieveMap.put(itemGroupId, items.getPushCount() - 1);
				record.setBenefitScore(benefitScore - items.getRecharge());
			}
			sb.append(",发送道具成功：").append(addItem);
			logger.info(sb.toString());
			dataDao.update(record);
			respMsg.setDataStr(items.getItemIds());
			respMsg.setIsSuccess(true);
			ClientDataSynMgr.synData(player, record, eSynType.BENEFIT_SELL_DATA, eSynOpType.UPDATE_SINGLE);
		} catch (Exception e) {
			GameLog.error("TargetSell", "TargetSellManager[roleGetItem]", "玩家领取物品时出现异常", e);
		}

		return respMsg.build().toByteString();
	}

	/**
	 * 将BenefitItems 转换为List<ItemInfo>
	 * 
	 * @param item
	 * @return
	 */
	private List<ItemInfo> tranfer2ItemInfo(BenefitItems item) {
		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		String itemIds = item.getItemIds();
		String[] itemData = itemIds.split(",");
		for (String subStr : itemData) {
			String[] sb = subStr.split("\\*");
			ItemInfo i;
			if (sb.length < 2) {
				i = new ItemInfo(Integer.parseInt(sb[0].trim()), 1);
			} else {
				i = new ItemInfo(Integer.parseInt(sb[0].trim()), Integer.parseInt(sb[1].trim()));
			}
			itemInfoList.add(i);
		}
		return itemInfoList;
	}

	/**
	 * 玩家在精准营销界面申请充值
	 * 
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString roleChargeItem(Player player, TargetSellReqMsg request) {
		TargetSellRespMsg.Builder respMsg = TargetSellRespMsg.newBuilder();
		respMsg.setReqType(request.getReqType());
		int id = request.getItemGroupId();
		// 检查一下是否存在道具组ID
		logger.info("玩家["+player.getUserName()+"],id:["+player.getUserId()+"]在精准营销界面申请充值，购买目标道具组id:"+ id);
		BenefitDataDAO dataDao = BenefitDataDAO.getDao();
		TargetSellRecord record = dataDao.get(player.getUserId());
		Map<Integer, BenefitItems> map = record.getItemMap();
		if (map.containsKey(id)) {
			PreChargeMap.put(player.getUserId(), Pair.Create(id, System.currentTimeMillis()));
			respMsg.setIsSuccess(true);
		} else {
			respMsg.setIsSuccess(false);
			respMsg.setTipsMsg("不存在此奖励记录");
		}
		return respMsg.build().toByteString();
	}

	// /**
	// * 角色下线通知,清除一下英雄缓存
	// * @param userID
	// */
	// public void checkLogOutRoleList(String userId) {
	// TargetSellRoleChange value = new TargetSellRoleChange(userId, System.currentTimeMillis());
	// packHeroChangeAttr(userId, value);
	// packAndSendMsg(value);
	// }

	/**
	 * 发送数据到精准服
	 * 
	 * @param content
	 */
	private void sendMsg(final String content) {
		if (!ServerSwitch.isOpenTargetSell()) {
			return;
		}
		BenefitMsgController.getInstance().addMsg(content);
	}

	/**
	 * 物品比较器
	 * 
	 * @author Alex 2016年9月22日 下午5:03:14
	 */
	private static class BenefitItemComparator implements Comparator<BenefitItems> {

		@Override
		public int compare(BenefitItems o1, BenefitItems o2) {
			if (o1.getRecharge() >= o2.getRecharge()) {
				return -1;
			}
			return 1;
		}

	}

	/**
	 * 检查并清空所有在线玩家优惠积分
	 */
	public void checkOnLineRoleList() {
		List<String> onlineList = UserChannelMgr.getOnlineList();
		for (String userID : onlineList) {
			Player player = PlayerMgr.getInstance().find(userID);
			checkBenefitScoreAndSynData(player);
		}
	}

	/**
	 * 清除机器人相关信息
	 */
	public void clearRobotRecord() {
		PlayerMgr playerMgr = PlayerMgr.getInstance();
		for (Iterator<Entry<String, TargetSellRoleChange>> iterator = RoleAttrChangeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, TargetSellRoleChange> entry = iterator.next();
			String userId = entry.getKey();
			if (playerMgr.isPersistantRobot(userId)) {
				//logger.error("remove robot:" + userId);
				iterator.remove();
				HeroAttrChangeMap.remove(userId);
				MapItemStore<FSHero> store = MapItemStoreFactory.getHeroDataCache().getFromMemoryForRead(userId);
				if (store != null) {
					List<String> heroList = store.getReadOnlyKeyList();
					for (String id : heroList) {
						HeroAttrChangeMap.remove(id);
					}
				}
				continue;
			}
		}
	}

}
