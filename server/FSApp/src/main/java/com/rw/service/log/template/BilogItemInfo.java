package com.rw.service.log.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.gift.ComGiftCfg;
import com.rwbase.dao.gift.ComGiftCfgDAO;
import com.rwbase.dao.sign.pojo.SignCfg;
import com.rwproto.CopyServiceProtos.TagSweepInfo;

public class BilogItemInfo {
	
	private int itemId;
	private int num;
	

	public static List<BilogItemInfo> fromItemList(List<? extends ItemInfo> list){
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(list == null){
			return newlist;
		}
		for(ItemInfo subitem : list){
			BilogItemInfo newsubitem = new BilogItemInfo();
			newsubitem.setItemId(subitem.getItemID());
			newsubitem.setNum(subitem.getItemNum());
			newlist.add(newsubitem);			
		}		
		return newlist;
	}
	
	public static List<BilogItemInfo> fromItemSweepList(List<? extends ItemInfo> list){
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(list == null){
			return newlist;
		}
		Map<Integer, BilogItemInfo> newlisttmp = new HashMap<Integer, BilogItemInfo>();		
		for(ItemInfo subitem : list){
			BilogItemInfo newsubitem = new BilogItemInfo();
			if(newlisttmp.get(subitem.getItemID()) == null){
				newsubitem.setItemId(subitem.getItemID());
				newsubitem.setNum(subitem.getItemNum());
				newlisttmp.put(subitem.getItemID(), newsubitem);
			}else{
				newsubitem.setItemId(subitem.getItemID());
				int numnew = newlisttmp.get(subitem.getItemID()).getNum() + subitem.getItemNum();
				newsubitem.setNum(numnew);
				newlisttmp.put(subitem.getItemID(), newsubitem);				
			}				
		}
		return new ArrayList<BilogItemInfo>(newlisttmp.values());
		
	}
	
	
	
	
	/**各种奇葩的格式，'aid:anum,bid:bnum','aid_anum,bid_bnum'*/
	public static List<BilogItemInfo> fromStrArr(String[] strlist){
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(strlist == null){
			return newlist;
		}
		for(String subitem : strlist){
			BilogItemInfo newsubitem = new BilogItemInfo();
			String[] split2 = subitem.split(":");
			if (split2.length < 2) {
				split2 = subitem.split("_");
			}
			if(split2.length < 2){
				continue;
			}
			newsubitem.setItemId(Integer.parseInt(split2[0]));
			newsubitem.setNum(Integer.parseInt(split2[1]));
			newlist.add(newsubitem);			
		}
		return newlist;
	}
	
	/**各种奇葩的格式，'aid_anum,bid_bnum','aid~anum,bid~bnum'*/
	public static List<BilogItemInfo> fromStr(String str){
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(str == null){
			return newlist;
		}
		String[] sublist = str.split(",");		
		for(String subitem : sublist){
			BilogItemInfo newsubitem = new BilogItemInfo();
			String[] split2 = subitem.split("_");
			if (split2.length < 2) {
				continue;
			}
			newsubitem.setItemId(Integer.parseInt(split2[0]));
			newsubitem.setNum(Integer.parseInt(split2[1]));
			newlist.add(newsubitem);			
		}
		if(newlist.size() == 0){//一二三四，再来一次
			for(String subitem : sublist){
				BilogItemInfo newsubitem = new BilogItemInfo();
				String[] split2 = subitem.split("~");
				if (split2.length < 2) {
					continue;
				}
				newsubitem.setItemId(Integer.parseInt(split2[0]));
				newsubitem.setNum(Integer.parseInt(split2[1]));
				newlist.add(newsubitem);			
			}			
		}
		return newlist;
	}
	
	
	
	public static List<BilogItemInfo> fromEmailId(String emailId){
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(emailId == null){
			return newlist;
		}
		ComGiftCfg giftcfg = ComGiftCfgDAO.getInstance().getCfgById(emailId);
		if(giftcfg == null){
			return newlist;
		}
		Set<String> keyset = giftcfg.getGiftMap().keySet();
		Iterator<String> iterable = keyset.iterator();
		while(iterable.hasNext()){
			String subgiftid = iterable.next();
			int count = giftcfg.getGiftMap().get(subgiftid);
			BilogItemInfo subbilogitem = new BilogItemInfo();
			subbilogitem.setItemId(Integer.parseInt(subgiftid));
			subbilogitem.setNum(count);
			newlist.add(subbilogitem);
		}
		
		return newlist;
	}
	
	public static List<BilogItemInfo> fromSignCfg(SignCfg cfg){		
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(cfg == null){
			return newlist;
		}
		BilogItemInfo newsubitem = new BilogItemInfo();
		newsubitem.setItemId(Integer.parseInt(cfg.getItemID()));
		newsubitem.setNum(cfg.getItemNum());
		newlist.add(newsubitem);	
		return newlist;
	}
	
	
	public static List<BilogItemInfo> fromMap(Map<Integer, Integer>  map){
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(map == null){
			return newlist;
		}
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			BilogItemInfo newsubitem = new BilogItemInfo();
			newsubitem.setItemId(entry.getKey());
			newsubitem.setNum(entry.getValue());
			newlist.add(newsubitem);			
		}

		return newlist;
	}
	
	public static List<BilogItemInfo> fromComGiftID(String  giftId){
		List<BilogItemInfo> newlist = new ArrayList<BilogItemInfo>();
		if(giftId == null){
			return newlist;
		}
		ComGiftCfg giftcfg = ComGiftCfgDAO.getInstance().getCfgById(giftId);	
		if(giftcfg == null){
			return newlist;
		}		
		Set<String> keyset = giftcfg.getGiftMap().keySet();
		Iterator<String> iterable = keyset.iterator();
		while(iterable.hasNext()){
			BilogItemInfo newsubitem = new BilogItemInfo();
			String id = iterable.next();
			newsubitem.setItemId(Integer.parseInt(id));
			newsubitem.setNum(giftcfg.getGiftMap().get(id));
			newlist.add(newsubitem);
		}

		return newlist;
	}
	
	
	
	
	public int getItemId() {
		return itemId;
	}


	public void setItemId(int itemId) {
		this.itemId = itemId;
	}


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}






	
//	public static List<BilogItemInfo> fromItemList(List<ItemInfo> list){
//		
//		
//		
//		return null;
//	}
	
	
	
	
}
