package com.playerdata.fightinggrowth;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthTitleCfgDAO;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthTitleCfg;

public class FSuserFightingGrowthMgr {

	private static final FSuserFightingGrowthMgr _instance = new FSuserFightingGrowthMgr();
	
	private FSUserFightingGrowthHolder _holder = FSUserFightingGrowthHolder.getInstance();
	
	protected FSuserFightingGrowthMgr() {}
	
	public static FSuserFightingGrowthMgr getInstance() {
		return _instance;
	}
	
	private Pair<String, Boolean> checkUpgradeCondition(Player player, FSUserFightingGrowthTitleCfg nextTitleCfg) {
		// 检查晋级条件
		if(player.getHeroMgr().getFightingAll(player) < nextTitleCfg.getFightingRequired()) {
			// 战斗力不符合
			return Pair.Create(FSFightingGrowthTips.getTipsFightingNotReached(nextTitleCfg.getFightingRequired()), false);
		} 
		Map<Integer, Integer> itemMap = nextTitleCfg.getItemRequiredMap();
		if (itemMap.size() > 0) {
			Map.Entry<Integer, Integer> entry;
			for (Iterator<Map.Entry<Integer, Integer>> itr = itemMap.entrySet().iterator(); itr.hasNext();) {
				entry = itr.next();
				if(player.getItemBagMgr().getItemCountByModelId(entry.getKey()) < entry.getValue()) {
					// 材料数量不符合
					return Pair.Create(FSFightingGrowthTips.getTipsItemNotEnough(ItemCfgHelper.GetConfig(entry.getKey()).getName(), entry.getValue()), false);
				}
			}
		}
		return Pair.Create(null, true);
	}
	
	private boolean executeUpgradeCondition(Player player, FSUserFightingGrowthTitleCfg nextTitleCfg) {
		// 执行晋级条件
		Map<Integer, Integer> itemMap = nextTitleCfg.getItemRequiredMap();
		Map.Entry<Integer, Integer> entry;
		for(Iterator<Map.Entry<Integer, Integer>> itr = itemMap.entrySet().iterator(); itr.hasNext();) {
			entry = itr.next();
			if(!player.getItemBagMgr().useItemByCfgId(entry.getKey(), entry.getValue())) {
				return false;
			}
		}
		return true;
	}
	
	private void sendUpgradeTitleReward(Player player, FSUserFightingGrowthTitleCfg cfg) {
		// 发送战力提升晋级奖励
		if (cfg.getItemRewardMap().size() > 0) {
			String attachment = EmailUtils.createEmailAttachment(cfg.getItemRequiredMap());
			EmailUtils.sendEmail(player.getUserId(), cfg.getEmailCfgIdOfReward(), attachment, Arrays.asList(cfg.getFightingTitle()));
		}
	}
	
	/**
	 * 
	 * 获取玩家当前的称号
	 * 
	 * @param player
	 * @return
	 */
	public String getCurrentTitle(Player player) {
		FSUserFightingGrowthData data = this._holder.getUserFightingGrowthData(player);
		if (StringUtils.isEmpty(data.getCurrentTitleKey())) {
			return "";
		}
		return FSUserFightingGrowthTitleCfgDAO.getInstance().getCfgById(data.getCurrentTitleKey()).getFightingTitle();
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
		if(success) {
			_holder.updateToDB(player);
			// 同步数据
			_holder.synData(player);
		}
		Pair<String, Boolean> result = Pair.Create(tips, success);
		return result;
	}
}
