package com.playerdata.assistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.common.RefParam;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwbase.dao.assistant.cfg.AssistantCfgDao;
import com.rwbase.dao.assistant.pojo.AssistantData;
import com.rwbase.dao.assistant.pojo.AssistantDataHolder;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class AssistantMgr {

	final private Comparator<AssistantCfg> comparator = new Comparator<AssistantCfg>() {

		@Override
		public int compare(AssistantCfg source, AssistantCfg target) {
			return source.getPriority() - target.getPriority();
		}

	};

	private AssistantDataHolder assistantDataHolder;

	private Player player = null;

	private List<IAssistantCheck> checkList = new ArrayList<IAssistantCheck>();

	// 初始化
	public boolean init(Player playerP) {
		player = playerP;
		assistantDataHolder = new AssistantDataHolder(playerP);
		checkList.add(new AssistantDailyActivityCheck());
		checkList.add(new AssistantSignCheck());
		checkList.add(new AssistantGambleCheck());
		checkList.add(new AssistantPowerCheck());
		checkList.add(new AssistantEquipCheck());
		checkList.add(new AssistantHeroUpgradeStarCheck());
		checkList.add(new AssistantHeroLevelUpCheck());
		checkList.add(new AssistantUpdateSkillCheck());
		checkList.add(new AssistantCheckTaoist());
		
		checkList.add(new AssistantCheckFixEquipAdvance());
		checkList.add(new AssistantCheckFixEquipStarUp());
		
		return true;
	}

	public void doCheck() {
		AssistantData assistantData = assistantDataHolder.get();
		ArrayList<AssistantCfg> activeEventList = assistantData.getCfgList();
		activeEventList.clear();
		CfgOpenLevelLimitDAO helper = CfgOpenLevelLimitDAO.getInstance();
		RefParam<String> outtip = new RefParam<String>();
		
		for (IAssistantCheck iAssistantCheck : checkList) {
			eOpenLevelType openType = iAssistantCheck.getOpenType();
			if (openType != null){
				if (!helper.isOpen(openType, player,outtip)) {
					continue;
				}
			}
			
			AssistantEventID assistantEvent = iAssistantCheck.doCheck(player);
			if (assistantEvent != null) {
				AssistantCfg cfgById = AssistantCfgDao.getInstance().getCfgById(assistantEvent);
				if (cfgById != null) {
					activeEventList.add(cfgById);
					String extraParam = iAssistantCheck.getExtraParam();
					assistantData.putParam(assistantEvent, extraParam);
				}
			}
		}
		int size = activeEventList.size();
		if (size > 0) {
			Collections.sort(activeEventList, comparator);
			// AssistantCfg target = activeEventList.get(0);
			boolean rebuild = true;
			List<AssistantEventID> oldEvents = assistantData.getAssistantEvent();
			if (oldEvents != null) {
				int oldSize = oldEvents.size();
				if (oldSize == size) {
					boolean theSame = true;
					for (int i = 0; i < size; i++) {
						AssistantCfg cfg = activeEventList.get(i);
						if (oldEvents.get(i) != cfg.getEventIDField()) {
							theSame = false;
							break;
						}
					}
					if (theSame) {
						rebuild = false;
					}
				}
			}
			if (rebuild) {
				ArrayList<AssistantEventID> list = new ArrayList<AssistantEventID>();
				for (int i = 0; i < size; i++) {
					list.add(activeEventList.get(i).getEventIDField());
				}
				assistantDataHolder.setAssistantEventID(list);
			}
		} else {
			assistantDataHolder.setAssistantEventID(Collections.EMPTY_LIST);
		}

	}

	public void synData() {
		assistantDataHolder.synData();
	}

}
