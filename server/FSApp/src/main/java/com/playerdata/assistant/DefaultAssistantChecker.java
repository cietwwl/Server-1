package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class DefaultAssistantChecker implements IAssistantCheck {

	/**
	 * 清空了额外参数param，方便重新计算
	 * @param player
	 * @return
	 */
	public AssistantEventID doCheck(Player player) {
		param = null;
		return null;
	}

	protected String param;
	@Override
	public String getExtraParam() {
		return param;
	}
}
