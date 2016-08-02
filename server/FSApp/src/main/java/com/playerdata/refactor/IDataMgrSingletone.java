package com.playerdata.refactor;

public interface IDataMgrSingletone {

	public boolean load(String key);
    
    public boolean save(String key);
}
