package com.rw.dataaccess.processor;

import java.util.ArrayList;
import java.util.List;

import com.rw.dataaccess.PlayerParam;
import com.rw.dataaccess.PlayerCoreCreation;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.HeadCfgDAO;
import com.rwbase.dao.setting.HeadTypeList;
import com.rwbase.dao.setting.pojo.HeadBoxType;
import com.rwbase.dao.setting.pojo.HeadType;
import com.rwbase.dao.setting.pojo.TableSettingData;

public class SettingProcessor implements PlayerCoreCreation<TableSettingData> {

	@Override
	public TableSettingData create(PlayerParam param) {
		TableSettingData settingData = new TableSettingData();
		settingData.setUserID(param.getUserId());
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
		String newPlayerHead = HeadCfgDAO.getInstance().getCareerHead(param.getCareer(), param.getStarLevel(), param.getSex());
		List<String> headList = headTypeList.get(HeadType.HEAD_CARRER).getDataList();
		headList.clear();
		headList.add(newPlayerHead);

		// ********添加默认头像框,在这之前应该已经修改了主角的头像框*********//
		List<String> defaultHeadBoxList = HeadBoxCfgDAO.getInstance().getHeadBoxByType(HeadBoxType.HEADBOX_DEFAULT);
		HeadTypeList headBox = headBoxList.get(HeadBoxType.HEADBOX_DEFAULT);
		List<String> BoxList = headBox.getDataList();
		BoxList.add(defaultHeadBoxList.get(0));
		return settingData;
	}

}
