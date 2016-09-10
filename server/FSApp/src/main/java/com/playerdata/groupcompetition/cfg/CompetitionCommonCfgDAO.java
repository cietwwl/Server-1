package com.playerdata.groupcompetition.cfg;

public class CompetitionCommonCfgDAO {

	private static final CompetitionCommonCfgDAO _instance = new CompetitionCommonCfgDAO();
	private CompetitionCommonCfg cfg = new CompetitionCommonCfg();
	
	public static final CompetitionCommonCfgDAO getInstance() {
		return _instance;
	}
	
	public CompetitionCommonCfg getCfg() {
		return cfg;
	}
}
