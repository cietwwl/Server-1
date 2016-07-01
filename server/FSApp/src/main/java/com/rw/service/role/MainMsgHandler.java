package com.rw.service.role;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.mainmsg.CfgPmdDAO;
import com.rwbase.dao.mainmsg.PmdCfg;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.RoleQualityColorCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.role.pojo.RoleQualityColorCfg;
import com.rwproto.MainMsgProtos.EMsgType;
import com.rwproto.MainMsgProtos.MainMsgResponse;
import com.rwproto.MsgDef.Command;


public class MainMsgHandler {
	private static MainMsgHandler instance = new MainMsgHandler();

	private MainMsgHandler() {
	}

	public static MainMsgHandler getInstance() {
		return instance;
	}
	
	public void sendPmdGm(Player player,String[] arrCommandContents) {
	
		if(arrCommandContents!=null)
		{
			int id=0;
			if(arrCommandContents.length>0)
			{
				id=new Integer(arrCommandContents[0]);
			}
			
			
				List<String> arr=new ArrayList<String>();
				for(int i=1;i<arrCommandContents.length;i++)
				{
					arr.add(arrCommandContents[i]);
				}
				sendPmd(id,arr);
				
			
		}
	}

	/***发送跑码灯信息*****/
	public void sendPmd(int id,List<String> arr) {
		
		MainMsgResponse.Builder res = MainMsgResponse.newBuilder();
		res.setId(id);
		res.setType(EMsgType.PmdMsg);
		if(arr!=null&&arr.size()>0)
		{
			for(int i=0;i<arr.size();i++)
			{
				if(i==0)
				{
					res.setInfo1(arr.get(i));
				}else if(i==1)
				{
					res.setInfo2(arr.get(i));
				}else if(i==2)
				{
					res.setInfo3(arr.get(i));
				}else if(i==3)
				{
					res.setInfo4(arr.get(i));
				}else if(i==4)
				{
					res.setInfo5(arr.get(i));
				}
			}
		}

		
		sendPmdAll(res.build().toByteString());
	}


	
	private void sendPmdAll(ByteString pBuffer) {
		Map<String, Player> playeMap=PlayerMgr.getInstance().getAllPlayer();
		List<Player> list=new ArrayList<Player>();
		list.addAll(playeMap.values());
		for(Player player : list)
		{
			player.SendMsg(Command.MSG_MainMsg, pBuffer);
		}
		
	}
	
	
	/***发送自定义码灯信*****/
	public void sendPmdNotId(String des) {
		List<String> arr=new ArrayList<String>();
		arr.add(des);
		sendPmd(0,arr);
	}
	
	/**祭坛抽到物品(非佣兵),物品id为**/
	public void sendPmdJtGoods(Player player,String goodsId) 
	{
		PmdCfg cfg=CfgPmdDAO.getInstance().getCfg(1);
		if(cfg!=null&&cfg.content.indexOf(goodsId+"")!=-1)
		{
			List<String> arr=new ArrayList<String>();
			arr.add(player.getUserName());
			arr.add(goodsId+"");
			sendPmd(1,arr);
		}
	
	}
		
	
	/**祭坛抽到特殊佣兵**/
	public void sendPmdJtYb(Player player,String goodsId) 
	{
		PmdCfg cfg=CfgPmdDAO.getInstance().getCfg(1);
		if(cfg!=null&&cfg.content.indexOf(goodsId+"")!=-1)
		{
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		arr.add(goodsId+"");
		sendPmd(2,arr);
		}
	}
		
		
	
	/**领取首冲礼包**/
	public void sendPmdSc(Player player) 
	{
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		//arr.add(ybName+"");
		sendPmd(3,arr);
	}
		

	
	/**购买月卡**/
	public void sendPmdYk(Player player) 
	{
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		//arr.add(ybName+"");
		sendPmd(4,arr);
	}
		

	
	/**开服活动**/
	public void sendPmdHd(Player player) 
	{
		//List<String> arr=new ArrayList<String>();
		//arr.add(player.getUserName());
		//arr.add(ybName+"");
		sendPmd(5,null);
	}
	
	
	
	/**通关万仙阵**/
	public void sendPmdWxz(Player player) 
	{
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		//arr.add(ybName+"");
		sendPmd(6,arr);
	}
	
	
	/**无尽战火通关难度XX**/
	public void sendPmdSl(Player player,int num) 
	{
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		//arr.add(ybName+"");
		sendPmd(7,arr);
	}
	
	
	/**通关试炼塔层数（玩家通关10的倍数层数触发公告）**/
	public void sendPmdSlt(Player player,int num) 
	{
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		arr.add(num+"");
		sendPmd(8,arr);
	}
	
	
	/**伙伴升星,3星以及以上**/
	public void sendPmdHpsx(Player player,String pName,int num) 
	{
		if(num<=3)
		{
			return;
		}
		
		String colors="[ffffff]";
		if(num==3)
		{
			colors="[00b5ff]";
		}else if(num==4)
		{
			colors="[ef00ff]";
		}else if(num==5)
		{
			colors="[ff7800]";
		}
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		arr.add(pName+"");
		arr.add(colors+num+"[-]");
		sendPmd(9,arr);
	}
	
	//进阶显示规则：
	//ID:3 、4 、5显示为蓝色  蓝色 +1 蓝色 +2
	//id:6、7、8、9、10显示为紫色
	//id:11、12、13显示为橙色
	/**伙伴进阶,蓝色以上**/
	public void sendPmdHpJj(Player player,String pName,int num, RoleQualityCfg roleQualityCfg) 
	{
	
	    String st="";
		String qualityName = roleQualityCfg.getQualityName();
		int msgId;
		if(qualityName.indexOf("白色") != -1 || qualityName.indexOf("绿色") != -1){
			return;
		}
		RoleQualityColorCfg roleQualityColorCfg = RoleQualityColorCfgDAO.getInstante().getConfig(roleQualityCfg.getQuality());
		if(roleQualityColorCfg.getActiveSkill() == 1){
			msgId = 10;
		}else{
			msgId = 14;
		}
		String colors="[ffffff]";
		if(qualityName.indexOf("蓝色") != -1)
		{
			st= roleQualityCfg.getQualityName();
			colors="[00b5ff]";
		}else if(qualityName.indexOf("紫色") != -1)
		{
			st= roleQualityCfg.getQualityName();
		
			colors="[ef00ff]";
		}else if(qualityName.indexOf("橙色") != -1)
		{
			st= roleQualityCfg.getQualityName();
			colors="[ff7800]";
		}
		
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		arr.add(pName+"");
		arr.add(colors+st+"[-]");
		sendPmd(msgId,arr);
	}
	
	
	
	/**主角升星,3星以及以上**/
	public void sendPmdZjsx(Player player,int num) 
	{
		if(num<=3)
		{
			return;
		}
		String colors="[ffffff]";
		if(num==3)
		{
			colors="[00b5ff]";
		}else if(num==4)
		{
			colors="[ef00ff]";
		}else if(num==5)
		{
			colors="[ff7800]";
		}
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		arr.add(colors+num+"[-]");
		sendPmd(11,arr);
	}
	
	
	/**主角进阶,蓝色以上**/
	public void sendPmdZjJj(Player player,int num) 
	{
		String qualityId = player.getMainRoleHero().getQualityId();
		RoleQualityCfg roleQualityCfg = RoleQualityCfgDAO.getInstance().getCfgById(qualityId);
		String st="";
		int msgId;
		String qualityName = roleQualityCfg.getQualityName();
		if(qualityName.indexOf("白色") != -1 || qualityName.indexOf("绿色") != -1){
			return;
		}
		RoleQualityColorCfg roleQualityColorCfg = RoleQualityColorCfgDAO.getInstante().getConfig(roleQualityCfg.getQuality());
		if(roleQualityColorCfg.getActiveSkill() == 1){
			msgId = 15;
		}else{
			msgId = 12;
		}
		String colors="[ffffff]";
		if(qualityName.indexOf("蓝色") != -1)
		{
			st= roleQualityCfg.getQualityName();
			colors="[00b5ff]";
		}else if(qualityName.indexOf("紫色") != -1)
		{
			st= roleQualityCfg.getQualityName();
		
			colors="[ef00ff]";
		}else if(qualityName.indexOf("橙色") != -1)
		{
			st= roleQualityCfg.getQualityName();
			colors="[ff7800]";
		}
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		arr.add(colors+st+"[-]");
		sendPmd(msgId,arr);
	}
	
	
	
	/**稀有法宝的获得,A、S级**/
	public void sendPmdFb(Player player,String fbName,int num) 
	{
		if(num<=2)
		{
			return;
		}
		String colors="[ffffff]";
		if(num==3)
		{
			colors="[00b5ff]";
		}else if(num==4)
		{
			colors="[ef00ff]";
		}else if(num==5)
		{
			colors="[ff7800]";
		}
		List<String> arr=new ArrayList<String>();
		arr.add(player.getUserName());
		arr.add(colors+fbName+"[-]");
		sendPmd(13,arr);
	}
	
	
	
}
