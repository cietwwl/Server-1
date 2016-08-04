package com.rw.service.inlay;



import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.InlayMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.InlayProtos.MsgInlayRequest;
import com.rwproto.InlayProtos.MsgInlayResponse;



public class InlayHandler {

	private static InlayHandler instance = new InlayHandler();



	public static InlayHandler getInstance() {
		return instance;
	}

	

	public ByteString  InlayOne(Player playe,MsgInlayRequest msgReques) {
		MsgInlayResponse.Builder res = MsgInlayResponse.newBuilder();
		res.setType(msgReques.getType());
		
		
		
		List<ItemData> itemlist=playe.getItemBagMgr().getItemListByCfgId(msgReques.getGemId());
		if(itemlist==null||itemlist.size()==0)
		{
			 playe.NotifyCommonMsg("找不到物品");
			return null;
		}
		
		InlayMgr inlayMgr=null;
		ItemData itemData=itemlist.get(0);
		GemCfg gemCfg=ItemCfgHelper.getGemCfg(itemData.getModelId());
		if(playe.getUserId().equals(msgReques.getRoleId()))
		{
			inlayMgr=playe.getMainRoleHero().getInlayMgr();
			if(gemCfg!=null&&gemCfg.getLevel()>playe.getLevel())
			{
				 playe.NotifyCommonMsg("需要英雄等级:" + gemCfg.getLevel());
				return null;
			}
			
		}else
		{
			Hero hero=playe.getHeroMgr().getHeroById(msgReques.getRoleId());
			if(hero==null)
			{
				 playe.NotifyCommonMsg("找不到佣兵");
				return null;
			}
			inlayMgr=hero.getInlayMgr();
			
			if(gemCfg!=null&&gemCfg.getLevel()>hero.getLevel())
			{
				 playe.NotifyCommonMsg("英雄等级不足");
				return null;
			}
			
		}
		
//		if(!inlayMgr.CheckAddSize())
		if(!inlayMgr.CheckAddSize(playe, msgReques.getRoleId()))
		{
			 playe.NotifyCommonMsg("佩戴位置已满");
				return null;
		}
	
		
		

		
		
		if(!inlayMgr.CheckAddType(msgReques.getRoleId(), itemData.getModelId()))
		{
			 playe.NotifyCommonMsg("不可佩戴相同颜色宝石");
			return null;
		}
		
		if(!inlayMgr.InlayGem(playe, msgReques.getRoleId(), itemData))
		{
		 playe.NotifyCommonMsg("没有更多位置可佩戴");
			return null;
		}
		
		 playe.NotifyCommonMsg("佩戴："+ItemCfgHelper.getGemCfg(msgReques.getGemId()).getName()+"\n[00ff00]"+getAttrValueDes(msgReques.getGemId(),"+")+"[-]");
		
		
		res.setRoleId(msgReques.getRoleId());
	
		return res.build().toByteString();
		
	}
	

	public ByteString  XieXiaAll(Player playe,MsgInlayRequest msgReques) {
		MsgInlayResponse.Builder res = MsgInlayResponse.newBuilder();
		res.setType(msgReques.getType());
		
	
		
		InlayMgr inlayMgr=null;
		
		if(playe.getUserId().equals(msgReques.getRoleId()))
		{
			inlayMgr=playe.getMainRoleHero().getInlayMgr();
		}else
		{
			Hero hero=playe.getHeroMgr().getHeroById(msgReques.getRoleId());
			if(hero==null)
			{
				 playe.NotifyCommonMsg("找不到佣兵");
				return null;
			}
			inlayMgr=hero.getInlayMgr();
		}
		if(msgReques.getGemId()<=0)
		{
			if(!inlayMgr.XieXiaAll(playe, msgReques.getRoleId()))
			{
				 playe.NotifyCommonMsg("没有宝石可卸下");
			}else
			{
				 playe.NotifyCommonMsg("宝石已全部卸下");
			}
		}else
		{
			if(!inlayMgr.XieXia(playe, msgReques.getRoleId(), msgReques.getGemId()))
			{
				
			}else
			{
				 playe.NotifyCommonMsg("卸下："+ItemCfgHelper.getGemCfg(msgReques.getGemId()).getName()+"\n[ff0000]"+getAttrValueDes(msgReques.getGemId(),"-")+"[-]");
			}
		}
		
		return res.build().toByteString();
	}
	
	
	public ByteString  InlayAll(Player playe,MsgInlayRequest msgReques) {
		MsgInlayResponse.Builder res = MsgInlayResponse.newBuilder();
		res.setType(msgReques.getType());

		
		InlayMgr inlayMgr=null;
		
		if(playe.getUserId().equals(msgReques.getRoleId()))
		{
			inlayMgr=playe.getMainRoleHero().getInlayMgr();
		}else
		{
			Hero hero=playe.getHeroMgr().getHeroById(msgReques.getRoleId());
			if(hero==null)
			{
				 playe.NotifyCommonMsg("找不到佣兵");
				return null;
			}
			inlayMgr=hero.getInlayMgr();
		}
		
		if(!inlayMgr.InlayAll(playe, msgReques.getRoleId()))
		{
			 //playe.NotifyCommonMsg("没有更多位置可镶嵌");
		}else
		{
			playe.NotifyCommonMsg("一键镶嵌成功");
		}
	
		return res.build().toByteString();
	}
	
	  public  String getAttrValueDes(int gemId,String typeSt)
	    {
	        String des = "";
	        GemCfg cfg = ItemCfgHelper.getGemCfg(gemId);
	        if (cfg != null)
	        {

	            if (cfg.getAttack() > 0)
	            {
	                des = getAttrDes("attack") + " "+typeSt + cfg.getAttack();
	            }
	            else if (cfg.getLife() > 0)
	            {
	                des = getAttrDes("life") + " "+typeSt + cfg.getLife();
	            }else if (cfg.getPhysiqueDef() > 0)
	            {
	                des = getAttrDes("physicqueDef") + " "+typeSt + cfg.getPhysiqueDef();
	            }else if (cfg.getSpiritDef() > 0)
	            {
	                des = getAttrDes("spiritDef") + " "+typeSt + cfg.getSpiritDef();
	            }else if (cfg.getAttackVampire() > 0)
	            {
	                des = getAttrDes("attackVampire") + " "+typeSt+ cfg.getAttackVampire();
	            }else if (cfg.getCritical() > 0)
	            {
	                des = getAttrDes("critical") + " "+typeSt + cfg.getCritical();
	            }else if (cfg.getCriticalHurt() > 0)
	            {
	                des = getAttrDes("criticalHurt") + " "+typeSt + cfg.getCriticalHurt();
	            }else if (cfg.getToughness() > 0)
	            {
	                des = getAttrDes("toughness") + " "+typeSt + cfg.getToughness();
	            }else if (cfg.getLifeReceive() > 0)
	            {
	                des = getAttrDes("lifeReceive") + " "+typeSt + cfg.getLifeReceive();
	            }else if (cfg.getEnergyReceive() > 0)
	            {
	                des = getAttrDes("energyReceive") + " "+typeSt + cfg.getEnergyReceive();
	            }else if (cfg.getEnergyTrans() > 0)
	            {
	                des = getAttrDes("energyTrans") + " "+typeSt + cfg.getEnergyTrans();
	            }else if (cfg.getAttackSpeed() > 0)
	            {
	                des = getAttrDes("attackSpeed") + " "+typeSt + cfg.getAttackSpeed();
	            }
	 
	        }
	        return des;

	    }


	    public  String getAttrDes(String attrName)
	    {
	    	String des = "";
	        if (attrName == "life")
	        {
	            return "生命值";
	        }else if (attrName == "attack")
	        {
	            return "攻击";
	        }else if (attrName == "physicqueDef")
	        {
	            return "物理防御";
	        }else if (attrName == "spiritDef")
	        {
	            return "法术防御";
	        }else if (attrName == "attackVampire")
	        {
	            return "攻击吸血";
	        }else if (attrName == "critical")
	        {
	            return "暴击";
	        }else if (attrName == "criticalHurt")
	        {
	            return "暴击伤害";
	        }else if (attrName == "toughness")
	        {
	            return "韧性";
	        }else if (attrName == "lifeReceive")
	        {
	            return "生命回复";
	        }else if (attrName == "energyReceive")
	        {
	            return "能量回复";
	        }else if (attrName == "energyTrans")
	        {
	            return "能量转化";
	        }else if (attrName == "attackSpeed")
	        {
	            return "攻击速度";
	        }
	        return des;
	    }
	
	


}
