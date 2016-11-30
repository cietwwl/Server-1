package com.playerdata.activity.evilBaoArrive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ComGiftMgr;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveCfg;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveCfgDAO;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveSubCfg;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveSubCfgDAO;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveItem;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveItemHolder;
import com.playerdata.activity.evilBaoArrive.data.EvilBaoArriveSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;

public class EvilBaoArriveMgr extends AbstractActivityMgr<EvilBaoArriveItem> {

	private static final int ACTIVITY_INDEX_BEGIN = 160000;
	private static final int ACTIVITY_INDEX_END = 170000;
	
	private static EvilBaoArriveMgr instance = new EvilBaoArriveMgr();

	public static EvilBaoArriveMgr getInstance() {
		return instance;
	}

	/**
	 * 添加完成的进度
	 * 
	 * @param player
	 * @param count
	 */
	public void addFinishCount(Player player, int count) {
		EvilBaoArriveItemHolder dataHolder = EvilBaoArriveItemHolder.getInstance();
		List<EvilBaoArriveItem> items = dataHolder.getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return;
		for (EvilBaoArriveItem item : items) {
			item.setFinishCount(item.getFinishCount() + count);
			dataHolder.updateItem(player, item);
		}
		dataHolder.synAllData(player);
	}
	
	/**
	 * 领取充值奖励
	 * 
	 * @param player
	 * @param activityID 活动主id
	 * @param subItemId 活动子id
	 * @return
	 */
	public ActivityComResult takeGift(Player player, String activityID, String subItemId) {
		EvilBaoArriveItemHolder dataHolder = EvilBaoArriveItemHolder.getInstance();
		EvilBaoArriveItem dataItem = dataHolder.getItem(player.getUserId(), activityID);
		ActivityComResult result = ActivityComResult.newInstance(false);
		// 未激活
		if (dataItem == null) {
			result.setReason("活动尚未开启");
		} else {
			EvilBaoArriveSubItem targetItem = null;
			List<EvilBaoArriveSubItem> subItemList = dataItem.getSubItemList();
			for (EvilBaoArriveSubItem itemTmp : subItemList) {
				if (StringUtils.equals(itemTmp.getCfgId(), subItemId)) {
					targetItem = itemTmp;
					break;
				}
			}
			if (null == targetItem) {
				result.setReason("找不到子活动类型的数据");
				return result;
			}
			if (targetItem.isGet()) {
				result.setReason("已领取过该奖励");
				return result;
			}
			EvilBaoArriveSubCfg subCfg = EvilBaoArriveSubCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
			if (null == subCfg) {
				result.setReason("找不到子活动类型的配置数据");
				return result;
			}
			EvilBaoArriveCfg cfg = EvilBaoArriveCfgDAO.getInstance().getCfgById(activityID);
			// 判断领取条件
			if (isLevelEnough(player, cfg) && dataItem.getFinishCount() >= subCfg.getAwardCount()) {
				if (takeGift(player, targetItem)) {
					result.setSuccess(true);
					dataHolder.updateItem(player, dataItem);
				} else {
					result.setSuccess(false);
					result.setReason("数据异常");
				}
			} else {
				result.setSuccess(false);
				result.setReason("不满足兑换条件");
			}
		}
		return result;
	}

	private boolean takeGift(Player player, EvilBaoArriveSubItem targetItem) {
		EvilBaoArriveSubCfg subCfg = EvilBaoArriveSubCfgDAO.getInstance().getCfgById(targetItem.getCfgId());
		if (subCfg == null) {
			GameLog.error(LogModule.ComActEvilBaoArrive, null, "通用活动找不到奖励配置文件", null);
			return false;
		}
		targetItem.setGet(true);
		ComGiftMgr.getInstance().addGiftById(player, subCfg.getAwardGift());
		return true;
	}
	
	/**
	 * 邮件补发过期未领取的奖励
	 * 
	 * @param player
	 * @param item
	 */
	@Override
	public void expireActivityHandler(Player player, EvilBaoArriveItem item) {
		List<EvilBaoArriveSubItem> subItems = item.getSubItemList();
		EvilBaoArriveCfg cfg = EvilBaoArriveCfgDAO.getInstance().getCfgById(item.getCfgId());
		if (isLevelEnough(player, cfg)) {
			EvilBaoArriveSubCfgDAO subCfgDAO = EvilBaoArriveSubCfgDAO.getInstance();
			ComGiftMgr giftMgr = ComGiftMgr.getInstance();
			for (EvilBaoArriveSubItem subItem : subItems) {
				EvilBaoArriveSubCfg subCfg = subCfgDAO.getCfgById(subItem.getCfgId());
				if (null == subCfg) {
					continue;
				}
				if (!subItem.isGet() && item.getFinishCount() >= subCfg.getAwardCount()) {
					giftMgr.addGiftTOEmailById(player, subCfg.getAwardGift(), null, cfg.getTitle());
				}
			}
		}
		item.reset();
	}

	/**
	 * 此红点不和活动红点统一判断，所以没有实现父类的红点方法
	 * @param player
	 * @return
	 */
	public List<String> getRedPoint(Player player) {
		List<String> redPointList = new ArrayList<String>();
		List<EvilBaoArriveItem> items = getHolder().getItemList(player.getUserId());
		if (null == items || items.isEmpty())
			return redPointList;
		for (EvilBaoArriveItem item : items) {
			if(haveRedPoint(player, item)){
				redPointList.add(String.valueOf(item.getCfgId()));
			}
			
		}
		return redPointList;
	}	
		
	private boolean haveRedPoint(Player player, EvilBaoArriveItem item) {
		EvilBaoArriveSubCfgDAO subCfgDao = EvilBaoArriveSubCfgDAO.getInstance();
		List<EvilBaoArriveSubItem> subItems = item.getSubItemList();
		for (EvilBaoArriveSubItem subItem : subItems) {
			EvilBaoArriveSubCfg subCfg = subCfgDao.getCfgById(subItem.getCfgId());
			if (null == subCfg){
				continue;
			}
			if ((subCfg.getAwardCount() <= item.getFinishCount() && !subItem.isGet()) || !item.isHasViewed()) {
				return true;
			}
		}
		return false;
	}
	
	protected UserActivityChecker<EvilBaoArriveItem> getHolder(){
		return EvilBaoArriveItemHolder.getInstance();
	}
	
	@Override
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
