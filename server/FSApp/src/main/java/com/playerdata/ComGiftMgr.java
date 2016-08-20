package com.playerdata;

import java.util.Iterator;
import java.util.Set;

import org.apache.tools.ant.taskdefs.Replace;

import com.alibaba.druid.util.StringUtils;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.countType.data.ActivityCountTypeSubItem;
import com.rw.fsutil.util.StringUtil;
import com.rw.service.Email.EmailUtils;
import com.rw.service.log.template.maker.LogTemplateMaker;
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
	
	/**
	 *  传入comgiftdao表里的道具奖励 包id,邮箱的对话格式id，将包里的奖励道具派出到用户邮箱 
	 * @param player
	 * @param giftid 奖励id，对应gift奖励表格
	 * @param emailid 使用的邮件格式id
	 * @param mark 用来标记奖励的明细,目前只支持附加在标题上
	 * @return
	 */
	public boolean addGiftTOEmailById(Player player, String giftid  ,String emailid ,String mark){
		boolean isadd = false;		
		String sb = makegiftToMailStr(giftid);	
		if(sb == null||StringUtils.isEmpty(sb)){
			GameLog.error("comgiftmgr-邮件", player.getUserId(), "没有传入奖励", null);
			return false;
		}
		isadd = addRewardToEmail(player,sb,emailid,mark);
		return isadd;
	}
	/**
	 * 传入id_number,id_number格式的奖励
	 * @param player
	 * @param tagInfo
	 * @param emailid
	 * @param mark
	 * @return
	 */
	public boolean addtagInfoTOEmail(Player player, String tagInfo  ,String emailid ,String mark){
		boolean isadd = false;		
		String sb = makeTagInfoToMailStr(tagInfo);
		if(sb == null||StringUtils.isEmpty(sb)){
			GameLog.error("comgiftmgr-邮件", player.getUserId(), "没有传入奖励", null);
			return false;
		}
		isadd = addRewardToEmail(player,sb,emailid,mark);
		return isadd;
	}
	
	/**
	 * 传入id_number_day,id_number_day格式的奖励
	 * @param player
	 * @param tagInfo
	 * @param emailid
	 * @param mark
	 * @return
	 */
	public boolean addtagoffathionInfoTOEmail(Player player, String tagInfo  ,String emailid ,String mark){
		boolean isadd = false;			
		StringBuffer newTmp  = new StringBuffer();
		String[] tmpstr = tagInfo.split(",");
		int length = tmpstr.length;
		for(String str : tmpstr){
			String[] tmpStrPing = str.split("_");
			StringBuffer newTmpSingel = new StringBuffer();
//			if(StringUtils.equals("900006", tmpStrPing[0])&&player.getUserGameDataMgr().){
//				
//			}
			newTmpSingel.append(tmpStrPing[0]).append(tmpStrPing[2]).append("_").append(tmpStrPing[1]);
			newTmp.append(newTmpSingel.toString());
			length--;
			if(length>0){
				newTmp.append(",");
			}			
		}
		
		String sb = makeTagInfoToMailStr(newTmp.toString());	
		if(sb == null||StringUtils.isEmpty(sb)){
			GameLog.error("comgiftmgr-邮件", player.getUserId(), "没有传入奖励", null);
			return false;
		}
		isadd = addRewardToEmail(player,sb,emailid,mark);
		return isadd;
	}
	
	
	private boolean addRewardToEmail(Player player,String rewardStr,String emailid ,String mark){
		boolean isadd = false;
		EmailCfg cfg = EmailCfgDAO.getInstance().getEmailCfg(emailid);	
		EmailData emailData = new EmailData();
		if(cfg != null){
		emailData.setEmailAttachment(rewardStr);
		
		emailData.setTitle(replace(cfg.getTitle(),mark));
		emailData.setContent(replace(cfg.getContent(),mark));
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
	
	
	/**将传入的str中的某个符号替换为mark*/
	private static String replace(String str, String mark) {
		if(StringUtils.isEmpty(mark)||StringUtils.isEmpty(str)){
			return str;
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~comgift.str="+ str + " mark=" + mark);
		String newstr = str.replace("{0}", mark);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~comgift.newstr="+ str + " mark=" + mark);
		return newstr;
	}

	
	
	/**通过gift奖励包的id，生成 邮箱的奖励表字符串 */
	private String makegiftToMailStr(String giftid){
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
	
	/**通过副本发送格式的奖励类格式，生成 邮箱的奖励表字符串 */
	private String makeTagInfoToMailStr(String tagInfo){
		StringBuilder sb = new StringBuilder();
		String[] idAndNums = tagInfo.split(",");
		int prizeSize = idAndNums.length;
		for(String idAndNum:idAndNums){
			String[] idOrNum = idAndNum.split("_");
			sb.append(Integer.parseInt(idOrNum[0])).append("~").append(Integer.parseInt(idOrNum[1]));
			if (--prizeSize > 0) {
				sb.append(",");
			}
		}			
		return sb.toString();		
	}
	

}
