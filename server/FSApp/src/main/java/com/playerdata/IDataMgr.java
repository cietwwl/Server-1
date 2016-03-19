package com.playerdata;

/**
 * 数据管理类基类
 *
 * 
 * @author Swinsun
 * @Modified-by Swinsun
 * @version 
 * 
 */

public class IDataMgr {
	
	protected Hero m_pOwner = null; //这个不开放给外面使用，Mgr的拥有者，可能是Hero,Player
	protected Player m_pPlayer = null; //这个不开放给外面使用,恒定是Player，如果Mgr是属于Hero， 那这个就是Player

	
	//初始化
    public boolean initPlayer(Hero pOwner)
    {
    	m_pOwner = pOwner;
    	m_pPlayer = ((Hero)m_pOwner).getPlayer();
		return false;
	}
    
    public boolean load()
    {
		return true;
	}
    
    //should override
    public boolean save() {
		return true;
	}
    
}
