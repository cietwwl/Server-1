package com.playerdata;

import java.util.Iterator;
import java.util.Set;

import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.gift.ComGiftCfg;
import com.rwbase.dao.gift.ComGiftCfgDAO;



public class ComGiftMgr {
	
	private static ComGiftMgr c_instance = new ComGiftMgr();
	
	public static ComGiftMgr getInstance() {
		if (c_instance == null) {
			c_instance = new ComGiftMgr();
		}
		return c_instance;
	}
	/**传入comgiftdao表里的道具奖励 包id，将包里的奖励道具派出到用户背包 */
	public void addGiftById(Player player, String string){
		ComGiftCfg giftcfg = ComGiftCfgDAO.getInstance().getCfgById(string);	
		if(giftcfg == null){
			return;
		}
		Set<String> keyset = giftcfg.getGiftMap().keySet();
		Iterator<String> iterable = keyset.iterator();
		while(iterable.hasNext()){
			String giftid = iterable.next();
			int count = giftcfg.getGiftMap().get(giftid);
			player.getItemBagMgr().addItem(Integer.parseInt(giftid),count);
		}		
	}
	
	/**传入comgiftdao表里的道具奖励 包id,邮箱的对话格式id，将包里的奖励道具派出到用户邮箱 */
	public boolean addGiftTOEmailById(Player player, String giftid  ,String emailid){
		boolean isadd = false;
		
		
		String sb = makegiftToMail(giftid);		
		EmailCfg cfg = EmailCfgDAO.getInstance().getEmailCfg(emailid);	
		EmailData emailData = new EmailData();
		if(cfg != null){
		emailData.setEmailAttachment(sb);
		emailData.setTitle(cfg.getTitle());
		emailData.setContent(cfg.getContent());
		emailData.setSender(cfg.getSender());
		emailData.setCheckIcon(cfg.getCheckIcon());
		emailData.setSubjectIcon(cfg.getSubjectIcon());
		emailData.setDeleteType(EEmailDeleteType.valueOf(cfg.getDeleteType()));
		emailData.setDelayTime(cfg.getDelayTime());
		emailData.setDeadlineTime(cfg.getDeadlineTime());
		EmailUtils.sendEmail(player.getUserId(), emailData);
		isadd = true;
		}
		return isadd;
	}
	/**通过gift奖励包的id，生成 邮箱的奖励表字符串 */
	private String makegiftToMail(String giftid){
		StringBuilder sb = new StringBuilder();
		ComGiftCfg giftcfg = ComGiftCfgDAO.getInstance().getCfgById(giftid);
		if(giftcfg == null){
			return null;
		}
		
		Set<String> keyset = giftcfg.getGiftMap().keySet();
		Iterator<String> iterable = keyset.iterator();
		int prizeSize = keyset.size();
		while(iterable.hasNext()){
			String subgiftid = iterable.next();
			int count = giftcfg.getGiftMap().get(subgiftid);
			sb.append(subgiftid).append("~").append(count);
			if (--prizeSize > 0) {
				sb.append(",");
			}
		}
		
		
		return sb.toString();
		
	}


}
