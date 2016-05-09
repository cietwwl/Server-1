package com.rw.service.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.dao.power.PowerInfoDataHolder;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.user.CfgBuyCoin;
import com.rwbase.dao.user.CfgBuyCoinDAO;
import com.rwbase.dao.user.CfgBuyPower;
import com.rwbase.dao.user.CfgBuyPowerDAO;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;
import com.rwproto.MainServiceProtos.EMainResultType;
import com.rwproto.MainServiceProtos.MsgMainRequest;
import com.rwproto.MainServiceProtos.MsgMainResponse;
import com.rwproto.MainServiceProtos.PowerInfo;
import com.rwproto.MainServiceProtos.TagCfgBuyCoin;
import com.rwproto.MainServiceProtos.TagCfgBuyPower;
import com.rwproto.MainServiceProtos.TagIndexInfo;

public class MainHandler {
	private static MainHandler instance = new MainHandler();
	private CfgBuyCoinDAO cfgBuyCoinDAO = CfgBuyCoinDAO.getInstance();
	private CfgBuyPowerDAO cfgBuyPowerDAO = CfgBuyPowerDAO.getInstance();

	private MainHandler() {
	}

	public static MainHandler getInstance() {
		return instance;
	}

	/** 获取体力信息 */
	public ByteString getPowerInfo(MsgMainRequest mainRequest, Player pPlayer) {
		MsgMainResponse.Builder mainResponse = MsgMainResponse.newBuilder().setRequest(mainRequest);
		PowerInfo.Builder towerInfo = PowerInfo.newBuilder();
		towerInfo.setBuyCount(pPlayer.getUserGameDataMgr().getBuyPowerTimes());
		towerInfo.setLastRecoverTime(pPlayer.getTableUserOther().getLastAddPowerTime());
		mainResponse.setPowerInfo(towerInfo.build());
		return mainResponse.build().toByteString();
	}

	/** 获取首页信息 */
	public ByteString index(MsgMainRequest mainRequest, Player pPlayer) {
		MsgMainResponse.Builder mainResponse = MsgMainResponse.newBuilder().setRequest(mainRequest);
		TagIndexInfo indexInfo = transformIndexInfo(pPlayer);
		mainResponse.setIndexInfo(indexInfo);
		return mainResponse.build().toByteString();
	}

	/** 请求连续购买金币 */
	public ByteString toContinuousBuyCoin(MsgMainRequest mainRequest, Player pPlayer) {
		MsgMainResponse.Builder mainResponse = MsgMainResponse.newBuilder().setRequest(mainRequest);

		PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(pPlayer.getVip());
		if (pPlayer.getUserGameDataMgr().getBuyCoinTimes() >= privilege.getMoneyCount() || pPlayer.getVip() < 0) {// 无购买次数
			mainResponse.setEMainResultType(EMainResultType.LOW_VIP);
			return mainResponse.build().toByteString();
		}
		TagCfgBuyCoin tagCfgBuyCoin = transformToContinuousCfgBuyCoin(pPlayer);
		if (tagCfgBuyCoin.getTimes() <= 0) {
			mainResponse.setEMainResultType(EMainResultType.NOT_ENOUGH_GOLD);
			return mainResponse.build().toByteString();
		}
		mainResponse.setTagCfgBuyCoin(tagCfgBuyCoin);
		return mainResponse.build().toByteString();
	}

	/** 连续购买金币 */
	public ByteString continuousBuyCoin(MsgMainRequest mainRequest, Player pPlayer) {
		MsgMainResponse.Builder mainResponse = MsgMainResponse.newBuilder().setRequest(mainRequest);
		PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(pPlayer.getVip());
		if (pPlayer.getUserGameDataMgr().getBuyCoinTimes() >= privilege.getMoneyCount() || pPlayer.getVip() < 0) {// 无购买次数
			mainResponse.setEMainResultType(EMainResultType.LOW_VIP);
			return mainResponse.build().toByteString();
		}
		TagCfgBuyCoin tagCfgBuyCoin = getBuyCount(pPlayer);
		if (tagCfgBuyCoin.getTimes() <= 0) {
			mainResponse.setEMainResultType(EMainResultType.NOT_ENOUGH_GOLD);
			return mainResponse.build().toByteString();
		}
		List<TagCfgBuyCoin> list = transformContinuousCfgBuyCoin(pPlayer);
		mainResponse.addAllAllTagCfgBuyCoin(list);
		int _getCoin = 0;
		for (int i = 0; i < list.size(); i++) {
			_getCoin += list.get(i).getCoin() * list.get(i).getCityMultiple();
		}
		pPlayer.getUserGameDataMgr().addGold(-tagCfgBuyCoin.getNeedPurse());
		pPlayer.AddBuyCoinTimes(tagCfgBuyCoin.getTimes());
		pPlayer.getUserGameDataMgr().addCoin(_getCoin);
		return mainResponse.build().toByteString();
	}

	/** 点金手购买金币 */
	public ByteString buyCoin(MsgMainRequest mainRequest, Player pPlayer) {
		MsgMainResponse.Builder mainResponse = MsgMainResponse.newBuilder().setRequest(mainRequest);
		PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(pPlayer.getVip());
		if (pPlayer.getUserGameDataMgr().getBuyCoinTimes() >= privilege.getMoneyCount()) {
			mainResponse.setEMainResultType(EMainResultType.LOW_VIP);
			return mainResponse.build().toByteString();
		}
		int times = pPlayer.getUserGameDataMgr().getBuyCoinTimes();
		CfgBuyCoin cfgBuyCoin = getCfgBuyCoin(times + 1);
		if (cfgBuyCoin == null) {// 走到这里需检查配置表
			mainResponse.setEMainResultType(EMainResultType.LOW_VIP);
			return mainResponse.build().toByteString();
		}
		if (pPlayer.getUserGameDataMgr().addGold(-cfgBuyCoin.getNeedPurse()) >= 0) {
			int cityMulple = getCritPercent(cfgBuyCoin);

			TagCfgBuyCoin tagCfgBuyCoin = transformCfgBuyCoin(cfgBuyCoin, cityMulple);
			mainResponse.setTagCfgBuyCoin(tagCfgBuyCoin);

			pPlayer.getUserGameDataMgr().addCoin(tagCfgBuyCoin.getCoin() * tagCfgBuyCoin.getCityMultiple());
			pPlayer.AddBuyCoinTimes(1);

			return mainResponse.build().toByteString();
		} else {
			mainResponse.setEMainResultType(EMainResultType.NOT_ENOUGH_GOLD);
		}
		return mainResponse.build().toByteString();
	}

	/** 计算暴击概率 */
	private int getCritPercent(CfgBuyCoin cfgBuyCoin) {
		int[] it = { cfgBuyCoin.getCritPercent1(), cfgBuyCoin.getCritPercent2(), cfgBuyCoin.getCritPercent3(), cfgBuyCoin.getCritPercent4(), cfgBuyCoin.getCritPercent5(),
				cfgBuyCoin.getCritPercent6(), cfgBuyCoin.getCritPercent7(), cfgBuyCoin.getCritPercent8(), cfgBuyCoin.getCritPercent9(), cfgBuyCoin.getCritPercent10() };

		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < it.length; i++) {
			for (int j = 0; j < it[i]; j++) {
				list.add(it[i]);
			}
		}
		if (list.size() == 0) {
			return 1;
		}
		Random random = new Random();
		int listIndex = random.nextInt(list.size());
		Integer integer = list.get(listIndex);
		for (int k = 0; k < it.length; k++) {
			if (integer == it[k]) {
				return k + 1;
			}
		}
		return 1;
	}

	/** 请求购买体力 */
	public ByteString toBuyPower(MsgMainRequest mainRequest, Player pPlayer) {
		MsgMainResponse.Builder mainResponse = MsgMainResponse.newBuilder().setRequest(mainRequest);
		PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(pPlayer.getVip());
		if (pPlayer.getUserGameDataMgr().getBuyPowerTimes() >= privilege.getPowerCount()) {
			mainResponse.setEMainResultType(EMainResultType.LOW_VIP);
			return mainResponse.build().toByteString();
		}
		CfgBuyPower cfgBuyPower = getCfgBuyPower(pPlayer.getUserGameDataMgr().getBuyPowerTimes() + 1);
		TagCfgBuyPower.Builder tagCfgBuyPowerBuilder = TagCfgBuyPower.newBuilder();
		tagCfgBuyPowerBuilder.setTimes(pPlayer.getUserGameDataMgr().getBuyPowerTimes());
		tagCfgBuyPowerBuilder.setNeedPurse(cfgBuyPower.getNeedPurse());
		tagCfgBuyPowerBuilder.setPower(cfgBuyPower.getPower());
		mainResponse.setTagCfgBuyPower(tagCfgBuyPowerBuilder.build());
		return mainResponse.build().toByteString();
	}

	/** 购买体力 */
	public ByteString buyPower(MsgMainRequest mainRequest, Player pPlayer) {
		MsgMainResponse.Builder mainResponse = MsgMainResponse.newBuilder().setRequest(mainRequest);
		PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(pPlayer.getVip());
		if (pPlayer.getUserGameDataMgr().getBuyPowerTimes() >= privilege.getPowerCount()) {
			mainResponse.setEMainResultType(EMainResultType.LOW_VIP);
			return mainResponse.build().toByteString();
		}
		int times = pPlayer.getUserGameDataMgr().getBuyPowerTimes();
		CfgBuyPower cfgBuyPower = getCfgBuyPower(times + 1);
		TagCfgBuyPower.Builder tagCfgBuyPowerBuilder = TagCfgBuyPower.newBuilder();
		tagCfgBuyPowerBuilder.setTimes(pPlayer.getUserGameDataMgr().getBuyPowerTimes());
		tagCfgBuyPowerBuilder.setNeedPurse(cfgBuyPower.getNeedPurse());
		tagCfgBuyPowerBuilder.setPower(cfgBuyPower.getPower());
		mainResponse.setTagCfgBuyPower(tagCfgBuyPowerBuilder.build());

		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(String.valueOf(pPlayer.getLevel()));
		if (pPlayer.getUserGameDataMgr().getPower() >= cfg.getMostPower()) {
			mainResponse.setEMainResultType(EMainResultType.POWER_LIMIT);// 体力已达上限
		} else if (pPlayer.getUserGameDataMgr().addGold(-cfgBuyPower.getNeedPurse()) >= 0) {
			pPlayer.addPower(cfgBuyPower.getPower());
			pPlayer.getUserGameDataMgr().incBuyPowerTimes();
			com.rwproto.MainServiceProtos.TagIndexInfo.Builder newBuilder = TagIndexInfo.newBuilder();
			newBuilder.setDiamond(pPlayer.getUserGameDataMgr().getGold());
			newBuilder.setPower(pPlayer.getUserGameDataMgr().getPower());
			TagIndexInfo indexInfo = newBuilder.build();
			mainResponse.setIndexInfo(indexInfo);

			// 购买体力日常任务
			pPlayer.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Power, 1);

			// TODO HC 把改变数据推送到前台
			PowerInfoDataHolder.synPowerInfo(pPlayer);
		} else {
			mainResponse.setEMainResultType(EMainResultType.NOT_ENOUGH_GOLD);
		}

		return mainResponse.build().toByteString();
	}

	private TagCfgBuyCoin transformToContinuousCfgBuyCoin(Player pPlayer) {
		return getBuyCount(pPlayer);
	}

	private List<TagCfgBuyCoin> transformContinuousCfgBuyCoin(Player pPlayer) {
		TagCfgBuyCoin tagCfgBuyCoin = getBuyCount(pPlayer);
		List<TagCfgBuyCoin> list = new ArrayList<TagCfgBuyCoin>();
		for (int j = 0; j < tagCfgBuyCoin.getTimes(); j++) {
			CfgBuyCoin cfgBuyCoin = getCfgBuyCoin(pPlayer.getUserGameDataMgr().getBuyCoinTimes() + j + 1);
			list.add(transformCfgBuyCoin(cfgBuyCoin, getCritPercent(cfgBuyCoin)));
		}
		return list;
	}

	/** 获取到可购买次数 */
	private TagCfgBuyCoin getBuyCount(Player pPlayer) {
		TagCfgBuyCoin.Builder tagCfgBuyCoin = TagCfgBuyCoin.newBuilder();

		int curTimes = pPlayer.getUserGameDataMgr().getBuyCoinTimes();// 当前购买次数
		int tempTimes = 0;// 可购买次数
		int tempNeedPurse = 0;// 购买总消耗
		int tempCoin = 0;// 购买最少获得

		PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(pPlayer.getVip());
		for (int i = 0; i < 10; i++) {
			if (curTimes + tempTimes < privilege.getMoneyCount()) {// 可再购买
				CfgBuyCoin cfgBuyCoin = getCfgBuyCoin(curTimes + 1 + tempTimes);
				if (pPlayer.getUserGameDataMgr().getGold() >= cfgBuyCoin.getNeedPurse() + tempNeedPurse) {// 水晶足够
					tempTimes += 1;
					tempNeedPurse += cfgBuyCoin.getNeedPurse();
					tempCoin += cfgBuyCoin.getCoin();
				} else {
					break;
				}
			} else {
				break;
			}
		}
		tagCfgBuyCoin.setTimes(tempTimes);
		tagCfgBuyCoin.setNeedPurse(tempNeedPurse);
		tagCfgBuyCoin.setCoin(tempCoin);
		return tagCfgBuyCoin.build();
	}

	private TagCfgBuyCoin transformCfgBuyCoin(CfgBuyCoin cfgBuyCoin, int cityMultiple) {
		TagCfgBuyCoin.Builder tagCfgBuyCoinBuilder = TagCfgBuyCoin.newBuilder();
		tagCfgBuyCoinBuilder.setTimes(cfgBuyCoin.getTimes()).setNeedPurse(cfgBuyCoin.getNeedPurse()).setCoin(cfgBuyCoin.getCoin()).setCityMultiple(cityMultiple);
		return tagCfgBuyCoinBuilder.build();
	}

	private TagIndexInfo transformIndexInfo(Player player) {
		// TableUser playData = player.getPlayerData();
		com.rwproto.MainServiceProtos.TagIndexInfo.Builder newBuilder = TagIndexInfo.newBuilder();
		newBuilder.setLevel(player.getLevel());
		newBuilder.setCoin(player.getUserGameDataMgr().getCoin());
		newBuilder.setPower(player.getUserGameDataMgr().getPower());

		String levelStr = String.valueOf(player.getLevel());
		RoleUpgradeCfg cfg = (RoleUpgradeCfg) RoleUpgradeCfgDAO.getInstance().getCfgById(levelStr);
		int maxPower = cfg == null ? 0 : cfg.getMaxPower();
		newBuilder.setMaxPower(maxPower);
		newBuilder.setHeadIamgeId(player.getHeadImage());
		newBuilder.setDiamond(player.getUserGameDataMgr().getGold());
		// if(privilegeUserInfo != null){
		// newBuilder.setVipLevel(privilegeUserInfo.getVipLevel());
		// }
		return newBuilder.build();
	}

	public CfgBuyCoin getCfgBuyCoin(int times) {
		CfgBuyCoin cfgBuyCoin = (CfgBuyCoin) cfgBuyCoinDAO.getCfgById(String.valueOf(times));
		return cfgBuyCoin;
	}

	public CfgBuyPower getCfgBuyPower(int times) {
		CfgBuyPower cfgBuyPower = (CfgBuyPower) cfgBuyPowerDAO.getCfgById(String.valueOf(times));
		return cfgBuyPower;
	}
}
