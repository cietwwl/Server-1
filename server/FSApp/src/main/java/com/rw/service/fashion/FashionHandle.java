package com.rw.service.fashion;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.arena.ArenaRobotDataMgr;
import com.common.RefInt;
import com.common.RefParam;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.FashionMgr;
import com.playerdata.Player;
import com.rwbase.common.NotifyChangeCallBack;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.fashion.FashionBeingUsed;
import com.rwbase.dao.fashion.FashionBeingUsedHolder;
import com.rwbase.dao.fashion.FashionBuyRenewCfg;
import com.rwbase.dao.fashion.FashionBuyRenewCfgDao;
import com.rwbase.dao.fashion.FashionCommonCfg;
import com.rwbase.dao.fashion.FashionCommonCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fashion.FashionItemHolder;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos.FashionCommon;
import com.rwproto.FashionServiceProtos.FashionRequest;
import com.rwproto.FashionServiceProtos.FashionResponse;
import com.rwproto.FashionServiceProtos.FashionResponse.Builder;
import com.rwproto.FashionServiceProtos.FashionUsed;

public class FashionHandle {
	private static FashionHandle instance = new FashionHandle();

	private FashionHandle() {
	};

	public static FashionHandle getInstance() {
		return instance;
	}

	/**
	 * 购买时装，不做续费处理，如果已经购买了，会返回错误
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString buyFash(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		int fashionId = req.getFashionId();
		FashionItem item = player.getFashionMgr().getItem(fashionId);
		if (item != null) {
			return setErrorResponse(response, player, ",ID=" + item.getId(), "时装已经购买，请续费");
		}

		FashionCommonCfg fashionCfg = FashionCommonCfgDao.getInstance().getConfig(fashionId);
		if (fashionCfg == null) {
			return setErrorResponse(response, player, ",ID=" + fashionId, "没有相应时装配置");
		}

		// 检查购买配置
		String buyPlanId = req.getBuyRenewPlanId();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		FashionBuyRenewCfg cfg = cfgHelper.getBuyConfig(fashionId, buyPlanId);
		if (cfg == null) {
			return setErrorResponse(response, player, ",ID=" + buyPlanId, "没有对应购买方案");
		}
		int cost = cfg.getNum();
		eSpecialItemId currencyType = cfg.getCoinType();
		if (cost <= 0 || currencyType.geteAttrId() == null) {
			return setErrorResponse(response, player, ",货币类型或货币值错误,ID=" + buyPlanId, "购买方案配置错误");
		}

		// 扣费
		if (!player.getUserGameDataMgr().deductCurrency(currencyType, cost)) {
			return setErrorResponse(response, player, null, "货币不足！", currencyType);
		}

		// 更新时装数据
		if (!player.getFashionMgr().buyFashionItemNotCheck(fashionCfg, cfg)) {
			return setErrorResponse(response, player, "严重错误：无法创建FashionItem,fashionId=" + fashionId, "购买失败");
		}

		if (req.getWearNow()) {
			FashionMgr fashionMgr = player.getFashionMgr();
			RefParam<String> tip = new RefParam<String>();
			if (fashionMgr.isExpired(fashionId, tip)) {
				return setErrorResponse(response, player, null, tip.value == null ? "时装已过期" : tip.value);
			}
			if (!fashionMgr.putOnFashion(fashionId, tip)) {
				return setErrorResponse(response, player, null, tip.value);
			}
		}

		response.setFashionId(fashionId);
		return SetSuccessResponse(response, player);
	}

	public ByteString offFash(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		int fashionId = req.getFashionId();
		FashionMgr fashionMgr = player.getFashionMgr();
		if (!fashionMgr.takeOffFashion(fashionId)) {
			return setErrorResponse(response, player, ",脱不了时装，fashionId=" + fashionId, "无效时装");
		}

		response.setFashionId(fashionId);
		return SetSuccessResponse(response, player);
	}

	public ByteString onFash(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		int fashionId = req.getFashionId();
		FashionMgr fashionMgr = player.getFashionMgr();
		// 检查是否过期
		RefParam<String> tip = new RefParam<String>();
		if (fashionMgr.isExpired(fashionId, tip)) {
			return setErrorResponse(response, player, null, tip.value == null ? "时装已过期" : tip.value);
		}

		if (!fashionMgr.putOnFashion(fashionId, tip)) {
			return setErrorResponse(response, player, null, tip.value);
		}

		response.setFashionId(fashionId);
		return SetSuccessResponse(response, player);
	}

	/**
	 * 获取时装配置和穿戴状态
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString getFashionData(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		FashionCommon.Builder common = FashionCommon.newBuilder();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		common.setBuyRenewCfg(cfgHelper.getConfigProto());

		FashionUsed.Builder fashion = player.getFashionMgr().getFashionUsedBuilder(player.getUserId());
		common.setUsedFashion(fashion);
		response.setFashionCommon(common);
		return SetSuccessResponse(response);
	}

	/**
	 * 续费，不允许续费永久时装
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString renewFashion(Player player, FashionRequest req) {
		FashionResponse.Builder response = getResponse(req);
		// 必须已购买才能续费
		int renewFashionId = req.getFashionId();
		FashionMgr fashionMgr = player.getFashionMgr();
		FashionItem item = fashionMgr.getItem(renewFashionId);
		if (item == null || item.getBuyTime() <= 0) {
			return setErrorResponse(response, player, ",FashionID=" + String.valueOf(renewFashionId), "没有对应时装");
		}

		if (item.getExpiredTime() <= 0) {
			return setErrorResponse(response, player, ",FashionID=" + String.valueOf(renewFashionId), "永久时装不需要续费");
		}

		// 检查配置是否正确
		String planId = req.getBuyRenewPlanId();
		FashionBuyRenewCfgDao cfgHelper = FashionBuyRenewCfgDao.getInstance();
		FashionBuyRenewCfg renewCfg = cfgHelper.getRenewConfig(renewFashionId, planId);
		if (renewCfg == null) {
			return setErrorResponse(response, player, ",ID=" + planId, "没有对应续费方案");
		}
		int cost = renewCfg.getNum();
		eSpecialItemId currencyType = renewCfg.getCoinType();
		int renewDay = renewCfg.getDay();
		if (cost <= 0 || currencyType.geteAttrId() == null) {
			return setErrorResponse(response, player, ",货币类型或货币值错误,ID=" + planId, "续费方案配置错误");
		}

		// 扣费
		if (!player.getUserGameDataMgr().deductCurrency(currencyType, cost)) {
			return setErrorResponse(response, player, null, "货币不足！", currencyType);
		}

		fashionMgr.renewFashion(item, renewDay);
		response.setFashionId(renewFashionId);
		return SetSuccessResponse(response, player);
	}

	public void convertData(FashionItemHolder fashionItemHolder, FashionBeingUsedHolder fashionUsedHolder,
		FashionBeingUsed fashionUsed, String uid) {
		List<FashionItem> lst = fashionItemHolder.getItemList();
		RefInt oldId = new RefInt();
		for (FashionItem fashionItem : lst) {
			if (fashionItem.UpgradeOldData(oldId)) {
				fashionItemHolder.directRemove(uid, oldId.value);
				fashionItemHolder.directAddItem(uid, fashionItem);
			}
		}

		boolean isChanged = false;
		if (fashionUsed != null && fashionUsed.UpgradeOldData()) {
			isChanged = true;
		}

		if (fashionUsed != null) {
			int[] usingList = fashionUsed.getUsingList();
			for (int i = 0; i < usingList.length; i++) {
				int fashionModelId = usingList[i];
				if (fashionModelId != -1) {
					FashionItem item = fashionItemHolder.getItem(fashionModelId);
					if (item == null) {
						// 因为旧数据在检查时装过期的时候，无法找到ID而没有脱下时装!
						fashionUsed.setUsing(i, -1);
						isChanged = true;
					}
				}
			}
		}

		if (isChanged) {
			fashionUsedHolder.update(fashionUsed);
		}
	}

	public FashionUsed.Builder getFashionUsedProto(String uid) {
		// 绕开player直接加载时装数据
		// 更新旧的时装ID
		NotifyChangeCallBack notifyProxy = new NotifyChangeCallBack();
		FashionItemHolder fashionItemHolder = new FashionItemHolder(uid, notifyProxy);

		FashionBeingUsedHolder holder = FashionBeingUsedHolder.getInstance();
		FashionBeingUsed fashUsing = holder.get(uid);
		convertData(fashionItemHolder, holder, fashUsing, uid);

		UserDataDao userDataDAO = UserDataDao.getInstance();
		User user = userDataDAO.getByUserId(uid);
		boolean isRobot = false;
		if (user != null) {
			isRobot = user.isRobot();
		}

		if (!isRobot) {
			FashionUsedIF fashionUsed = fashUsing;
			if (fashionUsed != null) {
				// by Franky:
				FashionUsed.Builder value = FashionUsed.newBuilder();
				boolean fashionSet = false;
				int wingId = fashionUsed.getWingId();
				if (wingId != -1) {
					value.setWingId(wingId);
					fashionSet = true;
				}
				int petId = fashionUsed.getPetId();
				if (petId != -1) {
					value.setPetId(petId);
					fashionSet = true;
				}
				int suitId = fashionUsed.getSuitId();
				if (suitId != -1) {
					value.setSuitId(suitId);
					fashionSet = true;
				}
				if (fashionSet) {
					return value;
				}
			}
		} else {
			int[] fashionIdArr = ArenaRobotDataMgr.getMgr().getFashionIdArr(uid);
			if (fashionIdArr == null || fashionIdArr.length != 3) {
				return null;
			}
			FashionUsed.Builder value = FashionUsed.newBuilder();
			boolean fashionSet = false;
			int suitId = fashionIdArr[0];
			if (suitId > 0) {
				value.setSuitId(suitId);
				fashionSet = true;
			}
			int wingId = fashionIdArr[1];
			if (wingId > 0) {
				value.setWingId(wingId);
				fashionSet = true;
			}
			int petId = fashionIdArr[2];
			if (petId > 0) {
				value.setPetId(petId);
				fashionSet = true;
			}
			if (fashionSet) {
				return value;
			}
		}
		return null;
	}

	private ByteString setErrorResponse(Builder response, Player player, String addedLog, String reason, ErrorType err) {
		if (addedLog != null) {
			String errorReason = reason + addedLog;
			GameLog.error("时装", player.getUserId(), errorReason);
		}

		if (!StringUtils.isBlank(reason)) {
			response.setTips(reason);
		}
		response.setError(err);
		return response.build().toByteString();
	}

	private ByteString setErrorResponse(Builder response, Player player, String addedLog, String reason) {
		return setErrorResponse(response, player, addedLog, reason, ErrorType.FAIL);
	}

	private ByteString setErrorResponse(Builder response, Player player, String addedLog, String reason, eSpecialItemId currencyType) {
		ErrorType err = ErrorType.FAIL;
		if (currencyType == null) {
			err = ErrorType.FAIL;
		} else if (currencyType == eSpecialItemId.Gold) {
			err = ErrorType.NOT_ENOUGH_GOLD;
		} else if (currencyType == eSpecialItemId.Coin) {
			err = ErrorType.NOT_ENOUGH_COIN;
		}
		return setErrorResponse(response, player, addedLog, reason, err);
	}

	private FashionResponse.Builder getResponse(FashionRequest req) {
		FashionResponse.Builder response = FashionResponse.newBuilder();
		response.setEventType(req.getEventType());
		return response;
	}

	/**
	 * 当逻辑可能修改时装购买列表或穿戴数据的时候，执行这个方法进行刷新
	 * 
	 * @param response
	 * @param player
	 * @return
	 */
	private ByteString SetSuccessResponse(Builder response, Player player) {
		player.getFashionMgr().OnLogicEnd();
		return SetSuccessResponse(response);
	}

	private ByteString SetSuccessResponse(FashionResponse.Builder response) {
		response.setError(ErrorType.SUCCESS);
		return response.build().toByteString();
	}
}
