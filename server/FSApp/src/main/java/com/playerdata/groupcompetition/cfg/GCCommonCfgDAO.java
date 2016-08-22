package com.playerdata.groupcompetition.cfg;

public class GCCommonCfgDAO {

	private static final GCCommonCfgDAO _instance = new GCCommonCfgDAO();
	private GCCommonCfg cfg = new GCCommonCfg();
	
	public static final GCCommonCfgDAO getInstance() {
		return _instance;
	}
	
	public GCCommonCfg getCfg() {
		return cfg;
	}
}
