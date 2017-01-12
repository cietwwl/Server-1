package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.common.Action;
import com.common.RefInt;
import com.common.RefLong;
import com.common.RefParam;
import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.playerdata.readonly.FashionMgrIF;
import com.rw.netty.UserChannelMgr;
import com.rw.service.Email.EmailUtils;
import com.rw.service.fashion.FashionHandle;
import com.rwbase.common.NotifyChangeCallBack;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.dao.fashion.FashionBeingUsed;
import com.rwbase.dao.fashion.FashionBeingUsedHolder;
import com.rwbase.dao.fashion.FashionBuyRenewCfg;
import com.rwbase.dao.fashion.FashionBuyRenewCfgDao;
import com.rwbase.dao.fashion.FashionCommonCfg;
import com.rwbase.dao.fashion.FashionCommonCfgDao;
import com.rwbase.dao.fashion.FashionEffectCfg;
import com.rwbase.dao.fashion.FashionEffectCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fashion.FashionItemHolder;
import com.rwbase.dao.fashion.FashionItemIF;
import com.rwbase.dao.fashion.FashionQuantityEffectCfg;
import com.rwbase.dao.fashion.FashionQuantityEffectCfgDao;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.pojo.HeadBoxCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos;
import com.rwproto.FashionServiceProtos.FashionCommon;
import com.rwproto.FashionServiceProtos.FashionEventType;
import com.rwproto.FashionServiceProtos.FashionResponse;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.MsgDef;

public class FashionMgr implements FashionMgrIF, PlayerEventListener {
	private static TimeUnit DefaultTimeUnit = TimeUnit.DAYS;
	private static String ExpiredEMailID = "10030";
	// private static String GiveEMailID = "10036";
	private static String ExpiredNotifycation = "您的时装%s已过期，请到试衣间续费";

	private Player m_player = null;
	private FashionItemHolder fashionItemHolder;
	private FashionBeingUsedHolder fashionUsedHolder;// 调用fashionUsedHolder更新后需要调用notifyProxy发布更新同志
	private boolean isInited = false;
	private NotifyChangeCallBack notifyProxy = new NotifyChangeCallBack();

	// // 缓存的增益数据，清空会重新计算
	// private BattleAddedEffects totalEffects = null;
	private int validCountCache;

	public void init(Player playerP) {
		m_player = playerP;
		isInited = true;
		String userId = playerP.getUserId();
		fashionItemHolder = new FashionItemHolder(userId, notifyProxy);
		fashionUsedHolder = FashionBeingUsedHolder.getInstance();
	}

	public boolean isInited() {
		return isInited;
	}

	public static boolean UpgradeIdLogic(int fid, RefInt newFid) {
		if ((fid / 100) == 100) {
			newFid.value = 900000 + fid % 100;
			return true;
		}
		return false;
	}

	/**
	 * 兼容旧的配置，映射所有1开头的时装ID到9开头
	 */
	public void convertData() {
		FashionHandle.getInstance().convertData(fashionItemHolder, fashionUsedHolder, getFashionBeingUsed(), m_player.getUserId());
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		// nothing to do
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		convertData();
	}

	public void regChangeCallBack(final Action callBack) {
		Action hook = new Action() {
			@Override
			public void doAction() {
				RecomputeBattleAdded();
				callBack.doAction();
				// 时装过期计算完毕再计算当前激活的头像框
				RecomputeUnlockHeadFrame();
				// 因为回调可能发送请求，时装穿戴数据的发送需要放在最后
				sendFashionBeingUsedChanged();
				GameLog.info("时装", m_player.getUserId(), "发送同步数据", null);
			}
		};
		notifyProxy.regChangeCallBack(hook);
	}

	// 重新计算时装激活的头像框
	protected void RecomputeUnlockHeadFrame() {
		List<FashionItem> lst = fashionItemHolder.getBroughtItemList();
		List<String> dataList = new ArrayList<String>(lst.size());
		FashionCommonCfgDao fashionHelper = FashionCommonCfgDao.getInstance();
		HeadBoxCfgDAO headHelper = HeadBoxCfgDAO.getInstance();
		String uid = m_player.getUserId();
		for (FashionItem fashionItem : lst) {
			FashionCommonCfg cfg = fashionHelper.getConfig(fashionItem.getFashionId());
			if (cfg != null) {
				HeadBoxCfg hcfg = headHelper.getCfgById(String.valueOf(cfg.getFrameIconId()));
				if (hcfg != null) {
					dataList.add(hcfg.getSpriteId());
				} else {
					GameLog.error("时装", uid, "HeadBoxCfg.csv找不到解锁时装,frameIconId:" + cfg.getFrameIconId());
				}
			} else {
				GameLog.error("时装", uid, "FashionCommonCfg.csv找不到时装ID:" + fashionItem.getFashionId());
			}
		}
		m_player.getSettingMgr().setFashionUnlockHeadBox(dataList);
	}

	/**
	 * 增加时装
	 * 
	 * @param cfg 不能为空
	 * @param buyCfg 不能为空
	 * @return
	 */
	public boolean buyFashionItemNotCheck(FashionCommonCfg cfg, FashionBuyRenewCfg buyCfg) {
		FashionItem item = newFashionItem(cfg, buyCfg);
		fashionItemHolder.addItem(m_player, item);

		// compute total effect
		if (item != null)
			createOrUpdate();
		return item != null;
	}

	/**
	 * 这个函数假设传入的时装不是永久时装
	 * 
	 * @param item 不能为空
	 * @param renewDay
	 */
	public void renewFashion(FashionItem item, int renewDay) {
		long now = System.currentTimeMillis();
		// 更新购买/续费时间，和有效期
		item.setBuyTime(now);
		long expiredTime = -1;
		if (renewDay <= 0) {
			// expiredTime = -1;
			GameLog.info("时装", m_player.getUserId(), "续费为永久时装" + item.getFashionId(), null);
		} else {
			expiredTime = item.getExpiredTime();
			if (expiredTime < now) {
				// 过期了，重新设置
				expiredTime = now;
			}
			// 在上次有效期内延长对应的时间，如果已经过期，使用当前时间作为基数
			expiredTime += DefaultTimeUnit.toMillis(renewDay);
		}
		item.setExpiredTime(expiredTime);
		item.setBrought(true);
		// 更新时装，特殊效果并推送
		if (!updateFashionItem(item)) {
			GameLog.error("时装", m_player.getUserId(), "更新续费后的时装失败,ID=" + item.getId());
		}
		updateQuantityEffect(getFashionBeingUsed());
	}

	/**
	 * 没有穿在身上的不能脱 不负责向客户端同步穿着数据，调用者根据需要进行同步
	 * 
	 * @param fashionId
	 * @return
	 */
	public boolean takeOffFashion(int fashionId) {
		FashionBeingUsed fashionUsed = getFashionBeingUsed();
		if (takeOff(fashionId, fashionUsed)) {
			fashionUsedHolder.update(fashionUsed);
			notifyProxy.delayNotify();
			// 兼容旧的逻辑，相当于调用changeFashState(fashionId, FashState.OFF)
			// 当删除FashionItem 的 state字段，这段逻辑就不再需要
			return true;
		}
		return false;
	}

	/**
	 * 不检查是否过期，调用者自行检查 不负责同步时装使用数据，调用者根据需要向客户端发送（机器人是不需要的！） 不能是已经穿在身上的，如果想换，必须先调用takeOffFashion脱了再穿
	 * 
	 * @param fashionId
	 * @param tip
	 * @return
	 */
	public boolean putOnFashion(int fashionId, RefParam<String> tip) {
		FashionBeingUsed fashionUsed = createOrUpdate();
		if (isFashionBeingUsed(fashionId, fashionUsed)) {
			LogError(tip, "时装已经穿上", ",fashionId=" + fashionId);
			return false;
		}

		FashionItem item = fashionItemHolder.getItem(fashionId);
		if (item == null) {
			LogError(tip, "时装未购买", ",fashionId=" + fashionId);
			return false;
		}

		if (putOn(item, fashionUsed)) {
			fashionUsedHolder.update(fashionUsed);
			notifyProxy.delayNotify();
			// 兼容旧的逻辑，相当于调用changeFashState(fashionId, FashState.ON)
			// 当删除FashionItem 的 state字段，这段逻辑就不再需要
			return true;
		}

		LogError(tip, "无法穿上时装", ",类型不对,fashionId=" + fashionId);
		return false;
	}

	//
	// /**
	// * 计算增益数据并缓存
	// *
	// * @return
	// */
	// public IEffectCfg getEffectData() {
	// if (totalEffects == null) {
	// AttrData addedValues = new AttrData();
	// AttrData addedPercentages = new AttrData();
	// FashionBeingUsed used = createOrUpdate();
	// if (used != null) {
	// int career = m_player.getCareer();
	// IEffectCfg[] list = used.getEffectList(getValidCount(), career);
	// for (int i = 0; i < list.length; i++) {
	// IEffectCfg eff = list[i];
	// if (eff != null) {
	// addedValues.plus(eff.getAddedValues());
	// addedPercentages.plus(eff.getAddedPercentages());
	// }
	// }
	// }
	// totalEffects = new BattleAddedEffects(addedValues, addedPercentages);
	// }
	// return totalEffects;
	// }

	/**
	 * 获取时装增加的总属性
	 * 
	 * @return
	 */
	public Map<Integer, AttributeItem> getAttributeMap() {

		FashionBeingUsed used = createOrUpdate();
		if (used != null) {
			int career = m_player.getCareer();
			int[] usingList = used.getUsingList();

			return getAttrMap(usingList, career, getValidCount());
		}

		return null;
	}

	public boolean save() {
		fashionItemHolder.flush();
		return true;
	}

	public void onMinutes() {
		checkExpired();
		notifyProxy.checkDelayNotify();
	}

	/**
	 * 发送所有
	 */
	public void syncAll() {
		checkExpired();
		fashionItemHolder.synAllData(m_player, 0);
		notifyProxy.checkDelayNotify();
		// TODO 临时解决客户端木有时装配置的问题
		FashionResponse.Builder fashionResponse = FashionResponse.newBuilder();
		fashionResponse.setEventType(FashionEventType.getFashiondata);
		FashionCommon.Builder common = FashionCommon.newBuilder();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		common.setBuyRenewCfg(cfgHelper.getConfigProto());
		String userId = m_player.getUserId();
		FashionUsed.Builder fashion = m_player.getFashionMgr().getFashionUsedBuilder(userId);
		common.setUsedFashion(fashion);
		fashionResponse.setFashionCommon(common);
		fashionResponse.setError(ErrorType.SUCCESS);
		UserChannelMgr.sendAyncResponse(userId, MsgDef.Command.MSG_FASHION, "syncAll", fashionResponse.build().toByteString());
	}

	/**
	 * 过期判断
	 * 
	 * @param fashionId
	 * @param tip 不能为空
	 * @return
	 */
	public boolean isExpired(int fashionId, RefParam<String> tip) {
		FashionItem item = fashionItemHolder.getItem(fashionId);
		long now = System.currentTimeMillis();
		return isExpired(fashionId, tip, item, now);
	}

	public FashionItem getItem(int fashionModelId) {
		return fashionItemHolder.getItem(fashionModelId);
	}

	public FashionUsedIF getFashionUsed(String userId) {
		return fashionUsedHolder.get(userId);
	}

	public FashionUsedIF getFashionUsed() {
		return fashionUsedHolder.get(m_player.getUserId());
	}

	public FashionUsed.Builder getFashionUsedBuilder(String userId) {
		FashionUsed.Builder fashionUsed = FashionUsed.newBuilder();
		FashionUsedIF fashion = getFashionUsed(userId);
		if (fashion != null) {
			if (fashion.getWingId() != -1)
				fashionUsed.setWingId(fashion.getWingId());
			if (fashion.getSuitId() != -1)
				fashionUsed.setSuitId(fashion.getSuitId());
			if (fashion.getPetId() != -1)
				fashionUsed.setPetId(fashion.getPetId());
			if (fashion.getTotalEffectPlanId() != -1)
				fashionUsed.setSpecialEffectId(fashion.getTotalEffectPlanId());
		}
		return fashionUsed;
	}

	/**
	 * 赠送时装 有效期expaireTimeCount设置为－1表示永久有效
	 * 
	 * @param fashionId
	 * @param expaireTimeCount
	 * @param userId
	 * @param sendEmail
	 */
	public static boolean giveFashionItem(int fashionId, int expaireTimeCount, String userId, boolean putOnNow, boolean sendEmail, TimeUnit timingUnit) {
		Player player = PlayerMgr.getInstance().find(userId);
		if (player != null) {
			return player.getFashionMgr().giveFashionItem(fashionId, expaireTimeCount, putOnNow, sendEmail, timingUnit);
		}
		return false;
	}

	/**
	 * 赠送时装 有效期expaireTimeCount设置为－1表示永久有效 timingUnit为空表示使用默认的时间单位（目前的单位是天）
	 * 
	 * @param fashionId
	 * @param expaireTimeCount
	 * @param player
	 * @param putOnNow
	 * @param sendEmail
	 * @param timingUnit
	 */
	public static boolean giveFashionItem(int fashionId, int expaireTimeCount, Player player, boolean putOnNow, boolean sendEmail, TimeUnit timingUnit) {
		if (player != null) {
			return player.getFashionMgr().giveFashionItem(fashionId, expaireTimeCount, putOnNow, sendEmail, timingUnit);
		}
		return false;
	}

	public boolean giveFashionItem(int fashionId, int expaireTimeCount, boolean putOnNow, boolean sendEmail, TimeUnit timingUnit) {
		Player player = m_player;
		if (player == null) {
			return false;
		}
		FashionCommonCfg fashionCfg = FashionCommonCfgDao.getInstance().getConfig(fashionId);
		if (fashionCfg == null) {
			return false;
		}

		FashionItem old = fashionItemHolder.getItem(fashionId);
		if (old != null && old.isBrought()) {// 已经有这件时装，不能再赠送
			if (old.getExpiredTime() < 0) {// 永久的不给替换了
				return true;
			}

			if (timingUnit == null) {
				timingUnit = DefaultTimeUnit;
			}

			// 有效期<=0看成永久时装
			old.setExpiredTime(expaireTimeCount <= 0 ? -1 : old.getExpiredTime() + timingUnit.toMillis(expaireTimeCount));
			old.setBrought(true);
			notifyProxy.checkDelayNotify();
			fashionItemHolder.updateItem(player, old);
			return true;
		}

		if (old == null) {
			old = newFashionItem(fashionCfg, expaireTimeCount, timingUnit);
			fashionItemHolder.addItem(player, old);
		} else {
			if (old.getExpiredTime() < 0) {// 永久的不给替换了
				return true;
			}

			if (timingUnit == null) {
				timingUnit = DefaultTimeUnit;
			}
			long now = System.currentTimeMillis();
			// 有效期<=0看成永久时装
			old.setExpiredTime(expaireTimeCount <= 0 ? -1 : now + timingUnit.toMillis(expaireTimeCount));
			old.setBrought(true);
			notifyProxy.checkDelayNotify();
			fashionItemHolder.updateItem(player, old);
		}

		if (putOnNow) {
			FashionBeingUsed fashionUsed = createOrUpdate();
			putOn(old, fashionUsed);
		}

		if (sendEmail) {
			List<String> args = new ArrayList<String>();
			args.add(fashionCfg.getName());
			// EmailUtils.sendEmail(player.getUserId(), GiveEMailID, args);
			// GameLog.info("时装", player.getUserId(), "发送赠送时装的邮件", null);
		}
		notifyProxy.checkDelayNotify();
		return true;
	}

	/**
	 * 必须已经初始化玩家才能赠送
	 * 
	 * @param fashionId
	 * @param days
	 * @param putOnNow
	 * @param sendEmail
	 */
	public void giveFashionItem(int fashionId, int days, boolean putOnNow, boolean sendEmail) {
		giveFashionItem(fashionId, days, putOnNow, sendEmail, DefaultTimeUnit);
	}

	public boolean GMSetExpiredTime(int fashionId, long minutes) {
		FashionItem item = fashionItemHolder.getItem(fashionId);
		if (item == null) {
			GameLog.info("时装", m_player.getUserId(), "无法重置时装过期时间，找不到时装:" + fashionId, null);
			return false;
		}
		long now = System.currentTimeMillis();
		item.setExpiredTime(now + TimeUnit.MINUTES.toMillis(minutes));
		item.setBrought(true);
		fashionItemHolder.updateItem(m_player, item);
		GameLog.info("时装", m_player.getUserId(), "成功重置时装过期时间，还有" + minutes + "分钟过期", null);
		notifyProxy.checkDelayNotify();
		return true;
	}

	public boolean GMSetFashion(int fashionModelId) {
		FashionItem item = fashionItemHolder.getItem(fashionModelId);
		if (item == null) {
			FashionCommonCfg fashionCfg = FashionCommonCfgDao.getInstance().getConfig(fashionModelId);
			if (fashionCfg == null) {
				GameLog.info("时装", m_player.getUserId(), "GM赠送时装失败,无效时装ID:" + fashionModelId, null);
				return false;
			}
			item = newFashionItem(fashionCfg, -1);
			fashionItemHolder.addItem(m_player, item);
			GameLog.info("时装", m_player.getUserId(), "GM赠送时装:" + fashionModelId, null);
		}
		item.setBrought(true);
		fashionItemHolder.updateItem(m_player, item);
		// FashionBeingUsed fashionUsed = createOrUpdate();
		// putOn(item, fashionUsed);
		// fashionUsedHolder.update(fashionUsed);
		GameLog.info("时装", m_player.getUserId(), "成功设置永久时装:" + fashionModelId, null);
		notifyProxy.checkDelayNotify();
		return true;
	}

	/**
	 * 职业改变
	 */
	public void changeSuitCareer() {
		// 修改设计之后，时装穿戴的数据与职业性别无关，因此无需修改！
		// 但是战斗属性增益跟职业有关系，需要重新计算增益
		RecomputeBattleAdded();
		notifyProxy.checkDelayNotify();
		/*
		 * List<FashionItem> list = fashionItemHolder.getItemList(); for (FashionItem fasItem : list) { FashionCfg fashcfg = FashionCfgDao.getInstance().getConfig(fasItem.getId()); if(fashcfg ==
		 * null){ continue; } if(fasItem.getType() == FashType.suit.ordinal()){ FashionCfg newcfg = FashionCfgDao.getInstance().getConfig(fashcfg.getSuitId(),m_pPlayer.getCareer(),m_pPlayer.getSex());
		 * FashionItem newitem = newFash(newcfg); if(newitem != null){ newitem.setState(fasItem.getState()); fashionItemHolder.addItem(m_pPlayer, newitem); } fashionItemHolder.removeItem(m_pPlayer,
		 * fasItem); } }
		 */
	}

	/**
	 * Handler业务完成之后如果有涉及时装数据修改的请求，就应该调用这个方法检查是否需要向客户端发送同步数据
	 */
	public void OnLogicEnd() {
		notifyProxy.checkDelayNotify();
	}

	@Override
	public List<FashionItemIF> search(ItemFilter predicate) {
		return fashionItemHolder.search(predicate);
	}

	/**
	 * 向客户端发送时装穿着数据
	 */
	private void sendFashionBeingUsedChanged() {
		// notify client that Fashion Being Used Changed!
		FashionResponse.Builder response = FashionResponse.newBuilder();
		response.setEventType(FashionEventType.getFashiondata);
		FashionCommon.Builder common = FashionCommon.newBuilder();
		FashionUsed.Builder fashion = getFashionUsedBuilder(m_player.getUserId());
		common.setUsedFashion(fashion);
		response.setFashionCommon(common);
		response.setError(ErrorType.SUCCESS);
		m_player.SendMsg(MsgDef.Command.MSG_FASHION, response.build().toByteString());
	}

	/**
	 * 创建时装数据，不写数据库
	 * 
	 * @param cfg
	 * @param buyCfg
	 * @return
	 */
	private FashionItem newFashionItem(FashionCommonCfg cfg, FashionBuyRenewCfg buyCfg) {
		return newFashionItem(cfg, buyCfg.getDay());
	}

	private FashionItem newFashionItem(FashionCommonCfg cfg, int days) {
		return newFashionItem(cfg, days, DefaultTimeUnit);
	}

	private FashionItem newFashionItem(FashionCommonCfg cfg, int expaireTimeCount, TimeUnit timingUnit) {
		FashionItem item = new FashionItem();
		item.setFashionId(cfg.getId());
		item.setType(cfg.getFashionType().ordinal());
		item.setUserId(m_player.getUserId());
		item.InitStoreId();
		long now = System.currentTimeMillis();
		item.setBuyTime(now);
		if (expaireTimeCount > 0) {
			if (timingUnit == null) {
				timingUnit = DefaultTimeUnit;
				GameLog.info("时装", m_player.getUserId(), "创建时装没有指定时间单位，使用默认值:" + DefaultTimeUnit);
			}
			item.setExpiredTime(now + timingUnit.toMillis(expaireTimeCount));
		} else {
			item.setExpiredTime(-1);
			GameLog.info("时装", m_player.getUserId(), "增加永久时装:" + cfg.getId(), null);
		}
		return item;
	}

	private boolean updateFashionItem(FashionItem item) {
		if (item != null) {
			fashionItemHolder.updateItem(m_player, item);
			return true;
		}
		return false;
	}

	/**
	 * 脱衣服，不写数据库
	 * 
	 * @param fashionId
	 * @param fashionUsed
	 * @return
	 */
	private boolean takeOff(int fashionId, FashionBeingUsed fashionUsed) {
		if (fashionUsed != null) {
			if (fashionUsed.getWingId() == fashionId) {
				fashionUsed.setWingId(-1);
				return true;
			}
			if (fashionUsed.getSuitId() == fashionId) {
				fashionUsed.setSuitId(-1);
				return true;
			}
			if (fashionUsed.getPetId() == fashionId) {
				fashionUsed.setPetId(-1);
				return true;
			}
		}
		return false;
	}

	private boolean isFashionBeingUsed(int fashionId, FashionUsedIF fashionUsed) {
		if (fashionUsed != null) {
			if (fashionUsed.getWingId() == fashionId)
				return true;
			if (fashionUsed.getSuitId() == fashionId)
				return true;
			if (fashionUsed.getPetId() == fashionId)
				return true;
		}
		return false;
	}

	/**
	 * 穿戴，不写数据库 传入的两个参数都不能为空！
	 * 
	 * @param item
	 * @param fashionUsed
	 * @return
	 */
	private boolean putOn(FashionItem item, FashionBeingUsed fashionUsed) {
		int fashionId = item.getFashionId();
		int typeInt = item.getType();

		if (FashionServiceProtos.FashionType.Wing_VALUE == typeInt) {
			fashionUsed.setWingId(fashionId);
			return true;
		}
		if (FashionServiceProtos.FashionType.Suit_VALUE == typeInt) {
			fashionUsed.setSuitId(fashionId);
			return true;
		}
		if (FashionServiceProtos.FashionType.Pet_VALUE == typeInt) {
			fashionUsed.setPetId(fashionId);
			return true;
		}
		return false;
	}

	/**
	 * 相对于time这个时间是否已经过期 有错误认为已经过期
	 * 
	 * @param fashionId
	 * @param tip
	 * @param item
	 * @param time
	 * @return
	 */
	private boolean isExpired(int fashionId, RefParam<String> tip, FashionItem item, long time) {
		RefLong expired = new RefLong();
		// getExpiredTime返回负数或零表示永久时装
		if (getExpiredTime(fashionId, tip, item, expired)) {
			return (expired.value > 0 && expired.value <= time);
		}
		// 有错误认为已经过期
		return true;
	}

	/**
	 * 返回负数或零表示永久时装
	 * 
	 * @param fashionId
	 * @param tip
	 * @param item
	 * @param expiredTime
	 * @return
	 */
	private boolean getExpiredTime(int fashionId, RefParam<String> tip, FashionItem item, RefLong expiredTime) {
		if (item == null) {
			LogError(tip, "时装未购买", ",fashionId=" + fashionId);
			return false;
		}
		long buyTime = item.getBuyTime();
		long expired = item.getExpiredTime();
		if (expired > 0 && buyTime > expired) {
			LogError(tip, "时装数据异常", ",购买时间比到期时间迟！fashionId=" + fashionId);
			return false;
		}
		expiredTime.value = expired;
		return true;
	}

	/**
	 * 调用者需要使用notifyProxy.checkDelayNotify来检查是否需要发送更新通知
	 */
	private void checkExpired() {
		List<FashionItem> list = fashionItemHolder.getBroughtItemList();
		if (list.isEmpty())
			return;
		long now = System.currentTimeMillis();
		FashionBeingUsed fashionUsed = getFashionBeingUsed();
		RefParam<String> tip = new RefParam<String>();
		for (FashionItem fasItem : list) {
			int fashionId = fasItem.getFashionId();
			if (isExpired(fashionId, tip, fasItem, now)) {
				takeOff(fashionId, fashionUsed);
				notifyProxy.delayNotify();

				FashionCommonCfg fashcfg = FashionCommonCfgDao.getInstance().getConfig(fashionId);
				if (fashcfg != null) {// 兼容旧的数据
					String fashionName = fashcfg.getName();
					if (fasItem.isBrought() && fashcfg != null && !StringUtils.isBlank(fashionName)) {
						final List<String> args = new ArrayList<String>();
						args.add(fashionName);
						GameLog.info("时装", m_player.getUserId(), "发送时装过期的邮件", null);
						PlayerTask task = new PlayerTask() {
							@Override
							public void run(Player player) {
								EmailUtils.sendEmail(player.getUserId(), ExpiredEMailID, args);
							}
						};
						GameWorldFactory.getGameWorld().asyncExecute(m_player.getUserId(), task);
						m_player.NotifyCommonMsg(String.format(ExpiredNotifycation, fashionName));
						fasItem.setBrought(false);
					}
				}

				// 允许购买的时装需要从购买数据库中删除，但是不允许购买的时装需要保留在数据库中继续勾引玩家
				if (fashcfg == null || !fashcfg.getNotAllowBuy()) {
					fashionItemHolder.removeItem(m_player, fasItem);
				} else {
					fashionItemHolder.updateItem(m_player, fasItem);
				}
			}
		}
	}

	/**
	 * 设置重新计算战斗增益的标志 与时装穿戴数据的改变有关(FashionBeingUsedHolder负责存储) 有效期时装总数带来的增益(FashionItemHolder负责存储) 以及职业变更有关系 时装的穿戴，脱下和过期会导致改变 有效期时装数量或者变更（购买，续费，过期）会导致变化
	 */
	private void RecomputeBattleAdded() {
		// totalEffects = null;
		createOrUpdate();
	}

	private void LogError(RefParam<String> tip, String userTip, String addedLog) {
		tip.value = userTip;
		if (addedLog != null) {
			GameLog.error("时装", m_player.getUserId(), tip.value + addedLog);
		}
	}

	/**
	 * 获取有效期内的时装总数
	 * 
	 * @return
	 */
	public int getValidCount() {
		int result = 0;
		long now = System.currentTimeMillis();
		RefParam<String> tip = new RefParam<String>();
		List<FashionItem> lst = fashionItemHolder.getItemList();
		for (FashionItem fasItem : lst) {
			int fashionId = fasItem.getFashionId();
			if (!isExpired(fashionId, tip, fasItem, now)) {
				result++;
			}
		}
		return result;
	}

	/**
	 * 
	 * 获取用户拥有，并且没有过期的时装
	 * 
	 * @return
	 */
	public List<FashionItem> getOwnedFashions() {
		return fashionItemHolder.getBroughtItemList();
	}

	private FashionBeingUsed getFashionBeingUsed() {
		FashionBeingUsed result = fashionUsedHolder.get(m_player.getUserId());
		return result;
	}

	/**
	 * 刷新有效时装增益 有效期时装数量或者变更（购买，续费，过期）会导致变化
	 * 
	 * @return
	 */
	private void updateQuantityEffect(FashionBeingUsed result) {
		if (result == null)
			return;
		int validCount = getValidCount();
		if (validCount != validCountCache) {
			validCountCache = validCount;
			FashionQuantityEffectCfg eff = FashionQuantityEffectCfgDao.getInstance().searchOption(validCount);
			if (eff != null) {
				int quantity = eff.getQuantity();
				if (quantity != result.getTotalEffectPlanId()) {
					result.setTotalEffectPlanId(quantity);
					fashionUsedHolder.update(result);
					notifyProxy.delayNotify();
				}
			}
		}
	}

	private FashionBeingUsed createOrUpdate() {
		FashionBeingUsed fashionUsed = getFashionBeingUsed();
		if (fashionUsed == null) {
			// 首次穿时装，初始化FashionBeingUsed
			fashionUsed = fashionUsedHolder.newFashion(m_player.getUserId());
			notifyProxy.delayNotify();
		}
		updateQuantityEffect(fashionUsed);
		return fashionUsed;
	}

	/**
	 * 计算属性
	 * 
	 * @param usingList
	 * @param career
	 * @param validCount
	 * @return
	 */
	public static Map<Integer, AttributeItem> getAttrMap(int[] usingList, int career, int validCount) {
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		FashionEffectCfgDao effectCfgDAO = FashionEffectCfgDao.getInstance();
		// 基础的时装
		for (int i = 0, len = usingList.length; i < len; i++) {
			FashionEffectCfg cfg = effectCfgDAO.getConfig(usingList[i], career);
			if (cfg == null) {
				continue;
			}

			AttributeUtils.calcAttribute(cfg.getAttrDataMap(), cfg.getPrecentAttrDataMap(), map);
		}

		FashionQuantityEffectCfg qualictyCfg = FashionQuantityEffectCfgDao.getInstance().getConfig(validCount);
		if (qualictyCfg != null) {
			AttributeUtils.calcAttribute(qualictyCfg.getAttrDataMap(), qualictyCfg.getPrecentAttrDataMap(), map);
		}

		return map;
	}
}
