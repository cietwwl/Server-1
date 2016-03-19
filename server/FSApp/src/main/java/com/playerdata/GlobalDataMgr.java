package com.playerdata;




import com.rwbase.dao.globalData.GlobalDataDAO;
import com.rwbase.dao.globalData.TableGlobalData;



public class GlobalDataMgr {
	

	private static TableGlobalData vo;
	public static void init()
	{
		vo=GlobalDataDAO.getTableGlobalData();
		
	}
	
	public static void save()
	{
		GlobalDataDAO.updata(vo);
	}
	
	
	public static int getAndSaveGuildId()
	{
		int gulidId=vo.getGulidIndex();
		gulidId+=1;
		vo.setGulidIndex(gulidId);
		save();
		return 10000+gulidId;
	}

	
	
}
