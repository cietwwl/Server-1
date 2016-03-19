package com.rwbase.dao.assistant.cfg;

import com.rw.config.ConfigMap;

public class AssistantCfgDao extends ConfigMap<AssistantCfg.AssistantEventID,AssistantCfg>{
	private	static AssistantCfgDao instance;
	public static AssistantCfgDao getInstance() {
		if (instance == null)
			instance = new AssistantCfgDao();
		return instance;
	}
	protected AssistantCfgDao() {
		super();
		helper = AssistantCfg.LoadCfg();
	}
	public void loadIndex(){}
	public void unloadIndex(){}
}
