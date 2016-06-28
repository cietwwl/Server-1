package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.log.GameLog;
import com.playerdata.common.PlayerEventListener;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.HeadCfgDAO;
import com.rwbase.dao.setting.HeadTypeList;
import com.rwbase.dao.setting.SettingDataHolder;
import com.rwbase.dao.setting.TableSettingDataDAO;
import com.rwbase.dao.setting.pojo.HeadBoxCfg;
import com.rwbase.dao.setting.pojo.HeadBoxType;
import com.rwbase.dao.setting.pojo.HeadCfg;
import com.rwbase.dao.setting.pojo.HeadType;
import com.rwbase.dao.setting.pojo.TableSettingData;

public class SettingMgr implements PlayerEventListener {

	private static final String DefaultHeadFrame = "20002";
	private SettingDataHolder settingDataHolder;
	private Player m_Player;

	public void init(Player player) {
		m_Player = player;
		settingDataHolder = new SettingDataHolder(player);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		if (StringUtils.isEmpty(player.getHeadImage())) // 老号的头像处理
		{
			String headImage = player.getSettingMgr().setCareerHeadImage();
			List<String> list = new ArrayList<String>();
			list.add(headImage);
			HeadTypeList headBox = player.getSettingMgr().getHeadPicByType(HeadType.HEAD_CARRER);
			if (headBox == null) {
				headBox = new HeadTypeList();
				headBox.setType(HeadType.HEAD_CARRER);
			}
			headBox.setDataList(list);
			TableSettingData settingData = settingDataHolder.get();
			settingDataHolder.get().getOwnHeadBox().add(headBox);
			TableSettingDataDAO.getInstance().update(settingData);
		}
	}

	public void syn() {
		settingDataHolder.syn(m_Player);
	}

	public boolean checkIfInTime() {
		int timeLeft = getChangeNameTimeLeft();
		if (timeLeft == 0) {
			return true;
		}
		return false;
	}

	public int getChangeNameTimeLeft() {
		if (settingDataHolder.get().getLastRenameTimeInMill() <= 0) {
			return 0;
		}
		long timeGap = Calendar.getInstance().getTimeInMillis() - settingDataHolder.get().getLastRenameTimeInMill();
		// long timeGap = System.currentTimeMillis() -
		// settingDataHolder.get().getLastRenameTime();
		int timeLeft = (int) (4 * 3600 - timeGap / 1000);
		timeLeft = timeLeft > 0 ? timeLeft : 0;
		return timeLeft;
	}

	public void checkOpen() {
		if (m_Player.getVip() >= 2) {// vip开放 ,需配置另加
			addHeadBox(DefaultHeadFrame);
		}

	}

	public List<String> getHeadNameList() {
		List<String> headNameList = new ArrayList<String>();
		List<String> headList = getHeadPicByTypeList(HeadType.HEAD_CARRER);
		headNameList.add(headList.get(0));
		headList = getHeadPicByTypeList(HeadType.HEAD_BASE);
		headNameList.addAll(headList);
		headList = getHeadPicByTypeList(HeadType.HEAD_MISSION);
		headNameList.addAll(headList);
		return headNameList;

	}

	/**
	 * 返回spriteID列表
	 * @return
	 */
	public List<String> getHeadBoxNameList() {
		List<String> headBoxNameList = new ArrayList<String>();
		List<String> headBoxList = getHeadBoxByTypeList(HeadBoxType.HEADBOX_DEFAULT);
		headBoxNameList.add(headBoxList.get(0));
		int minRange = HeadBoxType.getMin()+1;
		int maxRange = HeadBoxType.getMax()+1;
		for (int i=minRange;i<maxRange;i++){
			headBoxList = getHeadBoxByTypeList(i);
			if (headBoxList != null){
				headBoxNameList.addAll(headBoxList);
			}
		}
		return headBoxNameList;
	}

	public void setLastChangeName() {
		settingDataHolder.get().setLastRenameTimeInMill(Calendar.getInstance().getTimeInMillis());
		settingDataHolder.update(m_Player);
	}

	public List<String> getHeadBoxByTypeList(int type) {
		List<HeadTypeList> ownHeadBox = settingDataHolder.get().getOwnHeadBox();
		if(ownHeadBox.size() <= type){
			return null;
		}
		HeadTypeList headType = ownHeadBox.get(type);
		if (headType != null) {
			return headType.getDataList();
		}
		return null;
	}
	
	/**
	 * 在玩家可用头像列表中搜索是否存在
	 * @param headBoxSpriteId
	 * @return
	 */
	public boolean isValidHeadBoxFrameId(String headBoxSpriteId){
		List<HeadTypeList> lst = settingDataHolder.get().getOwnHeadBox();
		int size = lst.size();
		for (int i = 0; i < size; i++) {
			HeadTypeList item = lst.get(i);
			if (item != null && item.getDataList().contains(headBoxSpriteId)){
				return true;
			}
		}
		return false;
	}

	public List<String> getHeadPicByTypeList(int type) {
		HeadTypeList headType = settingDataHolder.get().getOwnHeadPic().get(type);
		if (headType != null) {
			return headType.getDataList();
		}
		return null;

	}

	public HeadTypeList getHeadBoxByType(int type) {
		List<HeadTypeList> boxList = settingDataHolder.get().getOwnHeadBox();
		return boxList.get(type);
	}

	public HeadTypeList getHeadPicByType(int type) {
		List<HeadTypeList> headPicList = settingDataHolder.get().getOwnHeadPic();
		return headPicList.get(type);

	}

	/*
	 * 添加头像
	 */
	public void addHead(String headName) {
		if (checkIfHeadCfgHas(headName)) {
			if (checkIfHeadDataHas(headName)) {
				GameLog.debug("已经拥有这个头像");
			} else {
				int type = HeadCfgDAO.getInstance().getTypeOfHead(headName);
				List<String> list = getHeadPicByTypeList(type);
				if (type == HeadType.HEAD_CARRER) // 职业头像的话只需要更换
				{
					list.clear();
				}
				list.add(headName);
				settingDataHolder.update(m_Player);
			}
		} else {
			GameLog.debug("there is nothing in the HeadCfgJson : " + headName);
		}
	}

	/*
	 * 添加头像框
	 */
	public void addHeadBox(String headBoxName) {
		if (checkIfHeadBoxDataHas(headBoxName)) {
			GameLog.debug("已经拥有这个头像");
		} else {
			int type = HeadBoxCfgDAO.getInstance().getTypeOfHeadBox(headBoxName);
			List<String> list = getHeadBoxByTypeList(type);
			list.add(headBoxName);
			settingDataHolder.update(m_Player);
		}
	}

	/*
	 * 检查数据库里是否拥有此头像
	 */
	public boolean checkIfHeadDataHas(String headName) {
		boolean hasData = false;
		int type = HeadCfgDAO.getInstance().getTypeOfHead(headName);
		if (type == HeadType.NULL) {
			GameLog.debug("没有这种类型的头像" + type);
		} else {
			List<String> list = getHeadPicByTypeList(type);
			if (list.contains(headName)) {
				GameLog.debug("已经拥有这个头像");
				hasData = true;
			} else {
				hasData = false;
				GameLog.debug("没有这个头像，不能更换");
			}
		}
		return hasData;
	}

	/*
	 * 检查数据库里是否拥有此头像框
	 */
	public boolean checkIfHeadBoxDataHas(String headBoxName) {
		boolean hasData = false;
		HeadBoxCfg cfg = HeadBoxCfgDAO.getInstance().getCfg(headBoxName);
		if (cfg == null) {
			GameLog.debug("没有头像框对应的配置，ID:" + headBoxName);
		} else {
			if (isValidHeadBoxFrameId(headBoxName)) {
				//GameLog.debug("已经拥有这个头像");
				hasData = true;
			} else {
				GameLog.debug("没有这个头像框，不能更换");
				hasData = false;
			}
		}
		return hasData;
	}

	/*
	 * 检查配置表里是否拥有此头像
	 */
	public boolean checkIfHeadCfgHas(String headName) {
		HeadCfg headCfg = (HeadCfg) HeadCfgDAO.getInstance().getCfg(headName);
		if (headCfg == null) {
			return false;
		}
		return true;
	}

	/*
	 * 检查配置表里是否拥有此头像框
	 */
	public boolean checkIfHeadBoxCfgHas(String headBoxName) {
		HeadBoxCfg headBoxCfg = (HeadBoxCfg) HeadBoxCfgDAO.getInstance().getCfg(headBoxName);
		if (headBoxCfg == null) {
			return false;
		}
		return true;
	}

	/*
	 * 设定职业头像，升职阶用
	 */
	public String setCareerHeadImage() {
		String headImage = HeadCfgDAO.getInstance().getCareerHead(m_Player.getCareer(), m_Player.getStarLevel(), m_Player.getSex());
		String strCurHeadImage = m_Player.getHeadImage();
		int type = HeadCfgDAO.getInstance().getTypeOfHead(strCurHeadImage);
		addHead(headImage);
		if (type == HeadType.HEAD_CARRER || strCurHeadImage == null) // 当前是职业头像的话就直接更换
		{
			m_Player.SetHeadId(headImage);
			return headImage;
		} else // 否则不变
		{
			m_Player.SetHeadId(strCurHeadImage);
			return strCurHeadImage;
		}

	}

	/*
	 * 更改默认头像框
	 */
	public void setDefaultHeadBox(String headBoxName) {
		List<String> defaultHeadBoxList = HeadBoxCfgDAO.getInstance().getHeadBoxByType(HeadBoxType.HEADBOX_DEFAULT);
		m_Player.getUserGameDataMgr().setHeadBox(defaultHeadBoxList.get(0));
	}

	/**
	 * 配置的spriteID列表
	 * @param dataList
	 */
	public void setFashionUnlockHeadBox(List<String> dataList){
		List<HeadTypeList> boxList = settingDataHolder.get().getOwnHeadBox();
		int oldSize = boxList.size();
		
		if (HeadBoxType.HEADBOX_FASHION < oldSize){
			// 如果时装激活的头像框因为过期而实效，需要重置用户头像框为默认值!
			HeadTypeList lst = boxList.get(HeadBoxType.HEADBOX_FASHION);
			lst.setDataList(dataList);
			
			HeadTypeList empty = new HeadTypeList(HeadBoxType.HEADBOX_FASHION);
			boxList.set(HeadBoxType.HEADBOX_FASHION, empty);
			List<String> checkList = getHeadBoxNameList();
			String currentHeadFrame = m_Player.getHeadFrame();
			if (!checkList.contains(currentHeadFrame) && !dataList.contains(currentHeadFrame)){
				List<String> defaultHeadBoxList = getHeadBoxByTypeList(HeadBoxType.HEADBOX_DEFAULT);
				if (defaultHeadBoxList.size() > 0){
					currentHeadFrame = defaultHeadBoxList.get(0);
				}else{
					currentHeadFrame = DefaultHeadFrame; 
				}
				m_Player.getUserGameDataMgr().setHeadBox(currentHeadFrame);
			}
			
			boxList.set(HeadBoxType.HEADBOX_FASHION, lst);
		}else{//兼容旧数据
			int newSize = HeadBoxType.getValidCount();
			List<HeadTypeList> newList = new ArrayList<HeadTypeList>(newSize);
			for (int i = 0;i<newSize;i++){
				if (i<oldSize){
					newList.add(boxList.get(i));
				}else{
					HeadTypeList item=new HeadTypeList(i);
					newList.add(item);
				}
			}
			settingDataHolder.get().setOwnHeadBox(newList);
			boxList = newList;
			HeadTypeList lst = boxList.get(HeadBoxType.HEADBOX_FASHION);
			lst.setDataList(dataList);
		}
		settingDataHolder.update(m_Player);
	}
	
	public void flush() {
	}

}
