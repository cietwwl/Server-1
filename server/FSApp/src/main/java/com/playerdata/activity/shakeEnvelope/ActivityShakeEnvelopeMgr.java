package com.playerdata.activity.shakeEnvelope;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.shakeEnvelope.cfg.ActivityShakeEnvelopeCfg;
import com.playerdata.activity.shakeEnvelope.cfg.ActivityShakeEnvelopeCfgDAO;
import com.playerdata.activity.shakeEnvelope.cfg.TimeSectionPair;
import com.playerdata.activity.shakeEnvelope.data.ActivityShakeEnvelopeItem;
import com.playerdata.activity.shakeEnvelope.data.ActivityShakeEnvelopeItemHolder;
import com.playerdata.activity.shakeEnvelope.data.ActivityShakeEnvelopeSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.dropitem.DropItemManager;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.ActivityCommonTypeProto.ActivityCommonRspMsg.Builder;
import com.rwproto.ActivityCommonTypeProto.CommonItem;
import com.rwproto.ActivityCommonTypeProto.ResultForEnvelope;
import com.rwproto.ActivityCommonTypeProto.ResultType;

/**
 * 摇一摇奖励
 * @author aken
 *
 */
public class ActivityShakeEnvelopeMgr extends AbstractActivityMgr<ActivityShakeEnvelopeItem> {
	
	private static final int ACTIVITY_INDEX_BEGIN = 190000;
	private static final int ACTIVITY_INDEX_END = 200000;

	private static ActivityShakeEnvelopeMgr instance = new ActivityShakeEnvelopeMgr();
	
	public static ActivityShakeEnvelopeMgr getInstance() {
		return instance;
	}

	/**
	 * 领取摇一摇红包奖励
	 * @param player
	 * @param response
	 */
	public void getEnvelopeReward(Player player, Builder response, String cfgId) {
		ActivityShakeEnvelopeCfg cfg = ActivityShakeEnvelopeCfgDAO.getInstance().getCfgById(cfgId);
		if(null == cfg){
			response.setResult(ResultType.EXCEPTION);
			response.setTipMsg("要领取的红包不存在");
			return;
		}
		ActivityShakeEnvelopeItem item = ActivityShakeEnvelopeItemHolder.getInstance().getItem(player.getUserId(), String.valueOf(cfg.getId()));
		if(null == item){
			response.setResult(ResultType.FAIL);
			response.setTipMsg("领取失败，可能是活动未开启");
			return;
		}
		long currentTime = DateUtils.getSecondLevelMillis();
		boolean haveReward = false;
		for(TimeSectionPair section : cfg.getSections()){
			if(section.getStartTime() < currentTime && currentTime < section.getEndTime()){
				ActivityShakeEnvelopeSubItem subItem = item.getSubItemByStartTime(section.getStartTime());
				if(null == subItem){
					subItem = new ActivityShakeEnvelopeSubItem();
					subItem.setStartTime(section.getStartTime());
					subItem.setEndTime(section.getEndTime());
					item.getSubItemList().add(subItem);
				}
				if(!subItem.isGet()){
					haveReward = true;
					getEnvelopeReward(player, cfg);
					subItem.setGet(true);
					break;
				}
			}
		}
		if(!haveReward){
			response.setResult(ResultType.FAIL);
			response.setTipMsg("当前没有红包可以领取");
			return;
		}
		item.setHasReward(false);
		ActivityShakeEnvelopeItemHolder.getInstance().updateItem(player, item);
	}
	
	/**
	 * 提取摇一摇的奖励
	 * @param player
	 * @param cfg
	 */
	private List<CommonItem> getEnvelopeReward(Player player, ActivityShakeEnvelopeCfg cfg){
		ResultForEnvelope.Builder envelopeBuilder = ResultForEnvelope.newBuilder();
		List<CommonItem> resultItems = new ArrayList<CommonItem>();
		List<ItemInfo> dropItems = generateDropItem(player, cfg.getDropStr());
		try{
			ItemBagMgr.getInstance().addItem(player, dropItems);
			for(int i = 0; i < dropItems.size(); i++){
				CommonItem.Builder comItemBuilder = CommonItem.newBuilder();
				comItemBuilder.setItemId(String.valueOf(dropItems.get(i).getItemID()));
				comItemBuilder.setCount(dropItems.get(i).getItemNum());
				resultItems.add(comItemBuilder.build());
			}
		}catch(Exception ex){
			GameLog.error(LogModule.ActivityShakeEnvelope.getName(), player.getUserId(), String.format("getEnvelopeReward, 添加物品：[%s]时出现异常", dropItems), ex);
		}
		return resultItems;
	}
	
	/**
	 * 根据掉落字符串计算物品掉落
	 * 
	 * @param player
	 * @param dropStr
	 * @return
	 */
	public static List<ItemInfo> generateDropItem(Player player, String dropStr) {
		List<Integer> dropList = new ArrayList<Integer>();
		for (String str : dropStr.split(",")) {
			try {
				dropList.add(Integer.parseInt(str));
			} catch (Exception ex) {
				GameLog.error(LogModule.ActivityShakeEnvelope, player.getUserId(), String.format("generateDropItem, 由掉落字符串[%s]转整数的时候出错", dropStr), ex);
			}
		}
		ArrayList<ItemInfo> itemList = new ArrayList<ItemInfo>();
		try {
			return DropItemManager.getInstance().pretreatDrop(player, dropList, -1, false);
		} catch (DataAccessTimeoutException e) {
			GameLog.error(LogModule.ActivityShakeEnvelope, player.getUserId(), String.format("generateDropItem, 由掉落字符串[%s]计算掉落时出错", dropStr), e);
		}
		return itemList;
	}
	
	@Override
	protected List<String> checkRedPoint(Player player, final ActivityShakeEnvelopeItem item) {
		List<String> redPointList = new ArrayList<String>();
		if (!item.isHasViewed()) {
			//和下面的判断相反是防止加重复
			redPointList.add(String.valueOf(item.getCfgId()));
		}
		long currentTime = DateUtils.getSecondLevelMillis();
		ActivityShakeEnvelopeCfg cfg = ActivityShakeEnvelopeCfgDAO.getInstance().getCfgById(item.getCfgId());
		boolean hasReward = false;
		for(TimeSectionPair section : cfg.getSections()){
			if(section.getStartTime() < currentTime && currentTime < section.getEndTime()){
				ActivityShakeEnvelopeSubItem subItem = item.getSubItemByStartTime(section.getStartTime());
				if(null == subItem || !subItem.isGet()){
					//这里添加到红点列表，防止和上面的浏览加重复
					if(item.isHasViewed()){
						redPointList.add(String.valueOf(item.getCfgId()));
					}
					hasReward = true;
					break;
				}
			}
		}
		if(hasReward != item.isHasReward()){
			item.setHasReward(hasReward);
			GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {
				@Override
				public void run(Player p) {
					ActivityShakeEnvelopeItemHolder.getInstance().updateItem(p, item);
				}
			});
		}
		return redPointList;
	}
	
	protected UserActivityChecker<ActivityShakeEnvelopeItem> getHolder(){
		return ActivityShakeEnvelopeItemHolder.getInstance();
	}
	
	public boolean isThisActivityIndex(int index){
		return index < ACTIVITY_INDEX_END && index > ACTIVITY_INDEX_BEGIN;
	}
}
