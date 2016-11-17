package com.playerdata.fightinggrowth;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.itemPrivilege.PrivilegeDescItem;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthTitleCfgDAO;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthTitleCfg;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.pojo.HeadBoxCfg;

public class FSuserFightingGrowthMgr {

	private static final FSuserFightingGrowthMgr _instance = new FSuserFightingGrowthMgr();

	private FSUserFightingGrowthHolder _holder = FSUserFightingGrowthHolder.getInstance();

	protected FSuserFightingGrowthMgr() {
	}

	public static FSuserFightingGrowthMgr getInstance() {
		return _instance;
	}

	private Pair<String, Boolean> checkUpgradeCondition(Player player, FSUserFightingGrowthTitleCfg nextTitleCfg) {
		// 检查晋级条件
		String userId = player.getUserId();
		if (player.getHeroMgr().getFightingTeam(userId) < nextTitleCfg.getFightingRequired()) {
			// 战斗力不符合
			return Pair.Create(FSFightingGrowthTips.getTipsFightingNotReached(nextTitleCfg.getFightingRequired()), false);
		}
		Map<Integer, Integer> itemMap = nextTitleCfg.getItemRequiredMap();
		if (itemMap.size() > 0) {
			ItemBagMgr instance = ItemBagMgr.getInstance();
			for (Iterator<Integer> keyItr = itemMap.keySet().iterator(); keyItr.hasNext();) {
				Integer itemCfgId = keyItr.next();
				int count = itemMap.get(itemCfgId).intValue();
				if (itemCfgId < eSpecialItemId.eSpecial_End.getValue()) {
					eSpecialItemId currencyType = eSpecialItemId.getDef(itemCfgId);
					if (!player.getUserGameDataMgr().isEnoughCurrency(currencyType, count)) {
						String currencyName;
						switch (currencyType) {
						case Gold:
							currencyName = "钻石";
							break;
						case Coin:
							currencyName = "金币";
							break;
						default:
							currencyName = "货币";
							break;
						}
						return Pair.Create(FSFightingGrowthTips.getTipsCurrencyNotEnough(currencyName, count), false);
					}
				} else {
					if (instance.getItemCountByModelId(userId, itemCfgId) < count) {
						// 材料数量不符合
						return Pair.Create(FSFightingGrowthTips.getTipsItemNotEnough(ItemCfgHelper.GetConfig(itemCfgId).getName(), count), false);
					}
				}
			}
		}
		return Pair.Create(null, true);
	}

	private boolean executeUpgradeCondition(Player player, FSUserFightingGrowthTitleCfg nextTitleCfg) {
		// 执行晋级条件
		Map<Integer, Integer> itemMap = nextTitleCfg.getItemRequiredMap();
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		for (Iterator<Integer> keyItr = itemMap.keySet().iterator(); keyItr.hasNext();) {
			Integer itemCfgId = keyItr.next();
			int count = itemMap.get(itemCfgId);
			if (itemCfgId < eSpecialItemId.eSpecial_End.getValue()) {
				if (!player.getUserGameDataMgr().deductCurrency(eSpecialItemId.getDef(itemCfgId), count)) {
					return false;
				}
			} else {
				if (!itemBagMgr.useItemByCfgId(player, itemCfgId, count)) {
					return false;
				}
			}
		}
		return true;
	}

	private void sendUpgradeTitleReward(Player player, FSUserFightingGrowthTitleCfg cfg) {
		// 发送战力提升晋级奖励
		// if (cfg.getItemRewardMap().size() > 0) {
		// String attachment = EmailUtils.createEmailAttachment(cfg.getItemRewardMap());
		// EmailUtils.sendEmail(player.getUserId(), cfg.getEmailCfgIdOfReward(), attachment, Arrays.asList(cfg.getFightingTitle()));
		// }
		ItemBagMgr.getInstance().addItem(player, cfg.getItemRewardList());
	}

	/**
	 * 
	 * 获取玩家当前的称号
	 * 
	 * @param player
	 * @return
	 */
	public String getCurrentTitleName(Player player) {
		FSUserFightingGrowthData data = this._holder.getUserFightingGrowthData(player);
		if (StringUtils.isEmpty(data.getCurrentTitleKey())) {
			return "";
		}
		return FSUserFightingGrowthTitleCfgDAO.getInstance().getCfgById(data.getCurrentTitleKey()).getFightingTitle();
	}

	/**
	 * 
	 * 获取玩家当前的称号
	 * 
	 * @param player
	 * @return
	 */
	public String getCurrentTitleId(Player player) {
		FSUserFightingGrowthData data = this._holder.getUserFightingGrowthData(player);
		if (StringUtils.isEmpty(data.getCurrentTitleKey())) {
			return "";
		}
		return data.getCurrentTitleKey();
	}

	/**
	 * 
	 * 获取玩家当前的称号的图标
	 * 
	 * @param player
	 * @return
	 */
	public String getCurrentTitleIcon(Player player) {
		FSUserFightingGrowthData data = this._holder.getUserFightingGrowthData(player);
		if (StringUtils.isEmpty(data.getCurrentTitleKey())) {
			return "";
		}
		return FSUserFightingGrowthTitleCfgDAO.getInstance().getCfgById(data.getCurrentTitleKey()).getFightingIcon();
	}

	public FSUserFightingGrowthHolder getHolder() {
		return _holder;
	}

	/**
	 * 
	 * 请求提升战力称号
	 * 
	 * @param player
	 * @return
	 */
	public Pair<String, Boolean> upgradeFightingTitle(Player player) {
		FSUserFightingGrowthData userFightingGrowthData = _holder.getUserFightingGrowthData(player);
		FSUserFightingGrowthTitleCfg nextTitleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getNextFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
		boolean success = false;
		String tips;
		if (nextTitleCfg != null) {
			Pair<String, Boolean> pair = this.checkUpgradeCondition(player, nextTitleCfg);
			tips = pair.getT1();
			success = pair.getT2();
		} else {
			// 满级了
			tips = FSFightingGrowthTips.getTipsFightingGrowthIsMax();
		}
		if (success) {
			if (this.executeUpgradeCondition(player, nextTitleCfg)) {
				userFightingGrowthData.setCurrentTitlKey(nextTitleCfg.getKey());
				this.sendUpgradeTitleReward(player, nextTitleCfg);
				success = true;
				tips = FSFightingGrowthTips.getTipsUpgradeTitleSuccess();
			} else {
				success = false;
				tips = FSFightingGrowthTips.getTipsConsumeItemFail();
			}
		}
		if (success) {
			_holder.updateToDB(player);
			// 同步数据
			_holder.synData(player);
			if (nextTitleCfg.getFrameIconId() > 0) {
				HeadBoxCfg cfg = HeadBoxCfgDAO.getInstance().getCfgById(String.valueOf(nextTitleCfg.getFrameIconId()));
				player.getSettingMgr().addHeadBox(cfg.getSpriteId());
			}
		}
		Pair<String, Boolean> result = Pair.Create(tips, success);
		return result;
	}

	public List<? extends PrivilegeDescItem> getPrivilegeDescItem(Player player) {
		String privId = getCurrentTitleId(player);
		if (org.apache.commons.lang3.StringUtils.isBlank(privId)) {
			return null;
		}
		FSUserFightingGrowthTitleCfg cfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getCfgById(privId);
		if (null == cfg) {
			return null;
		}
		return cfg.getPrivilegeDescItem();
	}

	public void synFightingTitleData(Player player) {
		_holder.synFightingTitleBaseData(player);
	}
}
