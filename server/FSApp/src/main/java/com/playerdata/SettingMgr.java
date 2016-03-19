package com.playerdata;

import java.util.ArrayList;
import java.util.Calendar;
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

	private SettingDataHolder settingDataHolder;
	private Player m_Player;

	public void init(Player player) {
		m_Player = player;
		settingDataHolder = new SettingDataHolder(player);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		TableSettingData settingData = new TableSettingData();
		settingData.setUserID(player.getUserId());
		settingData.setLastRenameTimeInMill(0);
		List<HeadTypeList> headTypeList = new ArrayList<HeadTypeList>();
		List<HeadTypeList> headBoxList = new ArrayList<HeadTypeList>();
		for (int i = 0; i < 3; i++) {
			List<String> headlist = new ArrayList<String>();
			List<String> headBoxlist = new ArrayList<String>();
			HeadTypeList headType = new HeadTypeList();
			headType.setType(i);
			headType.setDataList(headlist);
			headTypeList.add(headType);

			HeadTypeList boxType = new HeadTypeList();
			boxType.setType(i);
			boxType.setDataList(headBoxlist);
			headBoxList.add(boxType);
		}
		settingData.setOwnHeadPic(headTypeList);
		settingData.setOwnHeadBox(headBoxList);

		// ********添加基础头像*********//
		List<String> baseHeadList = HeadCfgDAO.getInstance().getHeadByType(HeadType.HEAD_BASE);
		for (int i = 0; i < baseHeadList.size(); i++) {
			headTypeList.get(HeadType.HEAD_BASE).getDataList().add(baseHeadList.get(i));
		}

		// ********添加新手头像,在这之前应该已经修改了主角的头像*********//
		String newPlayerHead = HeadCfgDAO.getInstance().getCareerHead(player.getCareer(), player.getStarLevel(), player.getSex());
		List<String> headList = headTypeList.get(HeadType.HEAD_CARRER).getDataList();
		headList.clear();
		headList.add(newPlayerHead);

		// ********添加默认头像框,在这之前应该已经修改了主角的头像框*********//
		List<String> defaultHeadBoxList = HeadBoxCfgDAO.getInstance().getHeadBoxByType(HeadBoxType.HEADBOX_DEFAULT);
		HeadTypeList headBox = headBoxList.get(HeadBoxType.HEADBOX_DEFAULT);
		List<String> BoxList = headBox.getDataList();
		BoxList.add(defaultHeadBoxList.get(0));
		// player.getSettingMgr().setDefaultHeadBox(defaultHeadBoxList.get(0));
		player.getUserGameDataMgr().setHeadBox(defaultHeadBoxList.get(0));
		TableSettingDataDAO.getInstance().update(settingData);
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
			addHeadBox("20002");
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

	public List<String> getHeadBoxNameList() {
		List<String> headBoxNameList = new ArrayList<String>();
		List<String> headBoxList = getHeadBoxByTypeList(HeadBoxType.HEADBOX_DEFAULT);
		headBoxNameList.add(headBoxList.get(0));
		headBoxList = getHeadBoxByTypeList(HeadBoxType.HEADBOX_BASE);
		headBoxNameList.addAll(headBoxList);
		headBoxList = getHeadBoxByTypeList(HeadBoxType.HEADBOX_MISSION);
		headBoxNameList.addAll(headBoxList);
		return headBoxNameList;
	}

	public void setLastChangeName() {
		settingDataHolder.get().setLastRenameTimeInMill(Calendar.getInstance().getTimeInMillis());
		settingDataHolder.update(m_Player);
	}

	public List<String> getHeadBoxByTypeList(int type) {
		HeadTypeList headType = settingDataHolder.get().getOwnHeadBox().get(type);
		if (headType != null) {
			return headType.getDataList();
		}
		return null;
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
		int type = HeadBoxCfgDAO.getInstance().getTypeOfHeadBox(headBoxName);
		if (type == HeadBoxType.NULL) {
			GameLog.debug("没有这种类型的头像" + type);
		} else {
			List<String> list = getHeadBoxByTypeList(type);
			if (list.contains(headBoxName)) {
				GameLog.debug("已经拥有这个头像");
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

	public void flush() {
	}

}
