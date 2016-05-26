package com.playerdata;

import com.playerdata.common.PlayerEventListener;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Email.EmailUtils;
import com.rwbase.dao.business.SevenDayGifCfg;
import com.rwbase.dao.business.SevenDayGifCfgDAO;
import com.rwbase.dao.business.SevenDayGifInfo;
import com.rwbase.dao.business.SevenDayGifInfoDAO;
import com.rwbase.dao.email.EEmailDeleteType;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;
import com.rwbase.dao.email.EmailData;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class DailyGifMgr implements PlayerEventListener{
	
	private SevenDayGifInfoDAO dao = SevenDayGifInfoDAO.getInstance();
	protected Player m_pPlayer = null;
	
    private final static int DailyGifEmail = 10035;
    private String userId;
    
	public void init(Player pRole) {
		m_pPlayer = pRole;
		this.userId  = pRole.getUserId();
	}
	
	@Override
	public void notifyPlayerCreated(Player player) {
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		updataCount(player);
	}

	public SevenDayGifInfo getTable() {
		return dao.get(userId);
	}

	public boolean save() {
		dao.update(userId);
		return true;
	}

	/*** 上线检查是否增加天数 **/
	public void updataCount(Player pRole) {
		SevenDayGifInfo table = getTable();
		if (table.getCount() >= 7) {
			return;
		}
		
		long resTime = table.getLastResetTime();
		if (resTime == 0) {
			table.setCount(1);
			table.setLastResetTime(System.currentTimeMillis());
			save();
		} else {
			// Calendar lastDay = DateUtils.getCalendar(table.getLastResetTime());
			// if (DateUtils.dayChanged(lastDay)) {
			// table.setCount(table.getCount() + 1);
			// }
			// TODO HC 检查同步的时间
			if (DateUtils.isResetTime(5, 0, 0, table.getLastResetTime())) {
				table.setCount(table.getCount() + 1);
				table.setLastResetTime(System.currentTimeMillis());
				save();
			}
		}
	}
	
	public void checkIsSevenDay(Player pRole)
	{
		SevenDayGifInfo table = getTable();
		if(table.isGetGif())return;
		User user = UserDataDao.getInstance().getByUserId(pRole.getUserId());
		if(user!=null)
		{
			//如果超过7天，则关闭7日礼功能
			int day = DateUtils.getDayDistance(user.getCreateTime(),System.currentTimeMillis());
			if(day>=7)
			{
				sendEmailGift(pRole);
				table.setCount(day+1);
			}
			return;
		}
	}
	
	/**超过7天，通过邮件发送未领取的奖励*/
	private void sendEmailGift(Player pRole)
	{
		SevenDayGifInfo table = getTable();
		int count = table.getCount();//登陆的天数
		SevenDayGifCfg gifCfg;
		String[] rewardsStrs;
		String[] rewardItemStrs;
		StringBuilder sb;
		EmailData emailData;
	    for(int i=1;i<count;i++)
	    {
	    	if(!table.getCounts().contains(i))
	    	{
	    		//还没领取奖励
	    		gifCfg = SevenDayGifCfgDAO.getInstance().getCfg(i);
	    		sb = new StringBuilder();
	    		if(gifCfg!=null)
	    		{
	    			rewardsStrs = gifCfg.goods.split("\\|");
	    			
	    			for(int k=0,length=rewardsStrs.length;k<length;k++)
	    			{
	    				rewardItemStrs = rewardsStrs[k].split("_");
	    				if(rewardItemStrs.length>1)
	    				{
	    					sb.append(rewardItemStrs[0]);
	    					sb.append("~");
	    					sb.append(rewardItemStrs[1]);
	    					if(k<length-1)
	    					  sb.append(",");
	    				}
	    			}
	    		}
	    		
	    	    EmailCfg cfg = EmailCfgDAO.getInstance().getEmailCfg((DailyGifEmail+i)+"");
	    	    if(cfg!=null)
	    	    {
	    	    	emailData = new EmailData();
	    	    	emailData.setEmailAttachment(sb.toString());
	    			emailData.setTitle(cfg.getTitle());
	    			emailData.setContent(cfg.getContent());
	    			emailData.setSender(cfg.getSender());
	    			emailData.setCheckIcon(cfg.getCheckIcon());
	    			emailData.setSubjectIcon(cfg.getSubjectIcon());
	    			emailData.setDeleteType(EEmailDeleteType.valueOf(cfg.getDeleteType()));
	    			emailData.setDelayTime(cfg.getDelayTime());
	    			emailData.setDeadlineTime(cfg.getDeadlineTime());
	    			EmailUtils.sendEmail(pRole.getUserId(), emailData);
	    	    }
	    	}
	    }
	    table.setGetGif(true);
	}

}