package com.rw.service.guild;








public class GuildHandler {
//	private static GuildHandler instance = new GuildHandler();
//
//
//	private GuildHandler() {
//	}
//
//	public static GuildHandler getInstance() {
//		return instance;
//	}
//
//	public ByteString baseList(Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		res.setType(EGuildType.SelectMsg);
//		List<TableGuild> list=GuildMgr.getGuildBaseList(player);
//		
//		if(list.size()>0)
//		{			
//			list=sortGuildList(list);
//		}
//		
//		for(int i=0;i<list.size();i++)
//		{
//			if(i<50)
//			{
//			TableGuild tableGuild=list.get(i);
//		    res.addGuildList(getBaseInfo(tableGuild,false));
//			}
//		}
//	
//		res.setNameList(GuildMgr.getAllName());
//		return res.build().toByteString();
//	}
//	
//	
//	public ByteString getMyGuildInfo(Player player) {
//		TableGuild tableGuild=GuildMgr.getGuild(player.getUserGameDataMgr().getGuildId());
//		GuildMgr.refreshOnLineMemberInfo(tableGuild);
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		res.setType(EGuildType.MyMsg);
//		res.setGuildInfo(getBaseInfo(tableGuild,true));
//		
//		return res.build().toByteString();
//	}
//
//	
//	public void sendMyGuildInfo(Player player) {
//		TableGuild tableGuild=GuildMgr.getGuild(player.getUserGameDataMgr().getGuildId());
//		GuildMgr.refreshOnLineMemberInfo(tableGuild);
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		res.setType(EGuildType.MyMsg);
//		res.setGuildInfo(getBaseInfo(tableGuild,true));
//		
//		player.SendMsg(MsgDef.Command.MSG_Guild, res.build().toByteString());
//	}
//
//	
//	private GuildBaseInfo.Builder getBaseInfo(TableGuild tableGuild,boolean isAllInfo) {
//		GuildBaseInfo.Builder baseInfo=GuildBaseInfo.newBuilder();
//		baseInfo.setGuildName(tableGuild.getName());
//		baseInfo.setUid(tableGuild.getGuildId());
//		baseInfo.setGuildId(tableGuild.getGuildNum());
//		baseInfo.setSize(tableGuild.getMemberMap().size());
//		baseInfo.setLevel(tableGuild.getLevel());
//		baseInfo.setUnLevel(tableGuild.getUnLevel());
//		baseInfo.setDes(tableGuild.getDes());
//		baseInfo.setContribute(tableGuild.getContribute());
//		baseInfo.setTotalContribute(tableGuild.getTotalContribute());
//		baseInfo.setDismissTimer(tableGuild.getDismissTimer());
//		baseInfo.setChangeName(tableGuild.getChangeName());
//		baseInfo.setUnType(tableGuild.getType());
//		baseInfo.setIcon(tableGuild.getIcon());
//		baseInfo.setIconBox(tableGuild.getIconBox());
//		baseInfo.setActiveValue(tableGuild.getActiveValue());
//		
//		List<GuildApplyInfo> applyList=new ArrayList<GuildApplyInfo>();
//		applyList.addAll(tableGuild.getApplyMap().values());
//		for(GuildApplyInfo GuildApplyInfo : applyList)
//		{
//			GuildApply.Builder guildApply=GuildApply.newBuilder();
//			guildApply.setLevel(GuildApplyInfo.getLevel());
//			guildApply.setPlayerId(GuildApplyInfo.getPlayerId());
//			guildApply.setPlayerName(GuildApplyInfo.getPlayerName());
//			guildApply.setIconId(GuildApplyInfo.getIconId());
//		    baseInfo.addApplyList(guildApply);
//		}
//		
//		
//		
//		//baseInfo.addApplyList(value)
//		
//		
//		if(isAllInfo)
//		{
//			List<GuildMember> list=new ArrayList<GuildMember>();
//			list.addAll(tableGuild.getMemberMap().values());
//			
//			list=sortGuildMemberList(list);
//			for(int i=0;i<list.size();i++)
//			{
//			baseInfo.addMemberList(getMemberInfo(list.get(i)));
//			}
//		}
//		
//		return baseInfo;
//		
//	}
//	
//
//	private GuildMemberInfo.Builder getMemberInfo(GuildMember GuildMember) {
//		GuildMemberInfo.Builder baseInfo=GuildMemberInfo.newBuilder();
//		baseInfo.setPlayerId(GuildMember.getPlayerId());
//		baseInfo.setPlayerName(GuildMember.getPlayerName());
//		baseInfo.setPosition(GuildMember.getPosition());
//		baseInfo.setTotalContribute(GuildMember.getTotalContribute());
//		baseInfo.setContribute(GuildMember.getContribute());
//	    baseInfo.setOnLine(PlayerMgr.getInstance().getPlayerList().containsKey(GuildMember.getPlayerId()));
//	    baseInfo.setTempContribute(GuildMember.getTempContribute());
//	    baseInfo.setIsDonate(GuildMember.isDonate());
//	    baseInfo.setLoginTimer(GuildMember.getLoginTimer());
//	    baseInfo.setLevel(GuildMember.getLevel());
//	    baseInfo.setSendEmailTimer(GuildMember.getSendEmailTimer());
//	    if(GuildMember.getIconId()!=null)
//	    {
//	    	 baseInfo.setIconId(GuildMember.getIconId());
//	    }
//	   
//	 
//	
//	   // baseInfo.set
//	
//		return baseInfo;
//	}
//	
//	public ByteString create(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		
//		TableGuild tableGuild=GuildMgr.createGuild(player, req.getGuildName(),req.getIcon());
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//		}
//		
//		return res.build().toByteString();
//		
//	}
//
//	
//	public ByteString apply(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		//GuildBaseInfo.Builder baseInfo=GuildBaseInfo.newBuilder();
//		
//		GuildApplyInfo GuildApplyInfo=GuildMgr.apply(player, req.getUid());
//		if(GuildApplyInfo!=null)
//		{
////			res.setType(req.getType());
////			GuildApply.Builder GuildApply=GuildApply.newBuilder();
////			GuildApply.setLevel(GuildApplyInfo.getLevel());
////			GuildApply.setPlayerId(GuildApplyInfo.getPlayerId());
////			GuildApply.setPlayerName(GuildApplyInfo.getPlayerName());
////		    baseInfo.addApplyList(GuildApply);
//			
//			return baseList(player);
//	
//		}
//		//res.setGuildInfo(baseInfo);
//		return res.build().toByteString();
//		
//	}
//	
//	
//	
//	public ByteString ignore(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.ignore(player, req.getUid());
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//		return res.build().toByteString();
//		
//	}
//
//	
//	public ByteString pass(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.pass(player, req.getUid());
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//		return res.build().toByteString();
//		
//	}
//
//	
//	public ByteString exit(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		GuildMgr.exitGuild(player);
//	    res.setType(req.getType());
//		return res.build().toByteString();
//		
//	}
//	
//	public ByteString kick(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.kickGuild(player, req.getUid());
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//		return res.build().toByteString();
//		
//	}
//	
//	public ByteString promote(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.promote(player, req.getUid());
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//		return res.build().toByteString();
//		
//	}
//	
//	public ByteString demotion(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.demotion(player, req.getUid());
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//		return res.build().toByteString();
//		
//	}
//	
//	public ByteString assignment(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.assignment(player, req.getUid());
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//		return res.build().toByteString();
//		
//	}
//	
//	
//	public ByteString dismiss(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		TableGuild tableGuild=GuildMgr.dismiss(player);
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//	
//	}
//	
//	public ByteString setEmail(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.setEmail(player, req.getTitle(), req.getContent());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//		
//	}
//	
//	public ByteString updataNotice(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.updataNotice(player,req.getContent());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//		
//	}
//	
//	public ByteString donate(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.donate(player,req.getNum());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//		
//	}
//	
//	
//	public ByteString uplevel(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.upgrade(player,req.getUpType());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//		
//	}
//	
//	
//	public ByteString updataUnlevel(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.changeJoinLevel(player,req.getUpType());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//		
//	}
//	
//	
//	public ByteString updataType(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.changeType(player,req.getUpType());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		
//		return res.build().toByteString();
//		
//	}
//	
//	
//	
//	public ByteString updataIcon(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.changeIcon(player,req.getIcon());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//		
//	}
//	
//	
//	public ByteString updataName(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		TableGuild tableGuild=GuildMgr.updataName(player,req.getGuildName());
//		
//		if(tableGuild!=null)
//		{
//			res.setType(req.getType());
//			GuildMgr.refreshOnLineMemberInfo(tableGuild);
//			res.setGuildInfo(getBaseInfo(tableGuild,true));
//			
//		}
//	
//		return res.build().toByteString();
//		
//	}
//	
//	
//	
//	public ByteString log(GuildRequest req,Player player) {
//		GuildResponse.Builder res = GuildResponse.newBuilder();
//		
//		//GuildBaseInfo.Builder baseInfo=GuildBaseInfo.newBuilder();
//		
//		List<GuildLogInfo> list=GuildMgr.getLog(player);
//		
//		if(list!=null)
//		{
//			int fbSize=1;
//				if(list.size()>0)
//				{
//					fbSize=list.size()/50;
//					if(list.size()%50!=0)
//					{
//						fbSize++;
//					}
//				}
//			
//			res.setType(req.getType());
//			
//		    for(int i=0;i<fbSize;i++)
//		    {
//		    	GuildBaseInfo.Builder baseInfo=GuildBaseInfo.newBuilder();
//		
//		    	if(i==0)
//		    	{
//		    		baseInfo.setIcon(1);
//		    	}else
//		    	{
//		    		baseInfo.setIcon(0);
//		    	}
//		    	
//		    	if(i==(fbSize-1))
//		    	{
//		    		baseInfo.setIconBox(1);
//		    	}else
//		    	{
//		    		baseInfo.setIconBox(0);
//		    	}
//		    	
//		    	
//		        for(int j=0;j<50;j++)
//			    {
//		        	int index=(i*50)+j;
//		        	if(index<list.size())
//		        	{
//		        		GuildLog.Builder log=GuildLog.newBuilder();
//		        		GuildLogInfo item=list.get(index);
//						log.setCTimer(item.getcTimer());
//						log.setDes(item.getDes());
//						log.setMyName(item.getMyName());
//						log.setOtherName(item.getOtherName());
//						log.setType(item.getType());
//						baseInfo.addLogList(log);
//		        		
//		        	}
//		        	//list.get((i*50)+j);
//			    }
//		        res.setGuildInfo(baseInfo);
//		        player.SendMsg(MsgDef.Command.MSG_Guild, res.build().toByteString());
//		        
//		        
//		    	   
////				for(GuildLogInfo item : list)
////				{
////					GuildLog.Builder log=GuildLog.newBuilder();
////					
////					log.setCTimer(item.getcTimer());
////					log.setDes(item.getDes());
////					log.setMyName(item.getMyName());
////					log.setOtherName(item.getOtherName());
////					log.setType(item.getType());
////					
////					baseInfo.addLogList(log);
////				}
//				
//		    	
//		    	
//		    }
//	
//		}
//		//res.setGuildInfo(baseInfo);
//		//return res.build().toByteString();
//		return null;
//		
//	}
//	
//	
//	
//	
//	public void sendExit(PlayerIF player) {
//		
//		String userId = player.getTableUser().getUserId();
//		if(!PlayerMgr.getInstance().getPlayerList().containsKey(userId))
//		{
//			return;
//		}
//		
//
//	    GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {
//			
//			@Override
//			public void run(Player player) {
//				
//				GuildResponse.Builder res = GuildResponse.newBuilder();
//				res.setType(EGuildType.exit);
//				player.SendMsg(MsgDef.Command.MSG_Guild, res.build().toByteString());
//			}
//		});
//		
//	}
//	
//	
//
//	private List<TableGuild> sortGuildList(List<TableGuild> list) {
//		//服务器筛选最强佣兵优先级：筛选依次为：高到低品阶>高到低等级>高到低资质>高到低战力>低到高佣兵ID
//		Collections.sort(list,new Comparator<TableGuild>()
//		{
//			public int compare(TableGuild o1, TableGuild o2) {
//				if(o1.getLevel() > o2.getLevel()){
//					return -1;
//				}else if(o1.getLevel() ==o2.getLevel()){
//					if(o1.getCreateTimer() < o2.getCreateTimer()){
//					return -1;
//					}
//				}else{
//			
//				}
//				return 0;
//			}});
//		return list;
//	}
//	
//	
//	private List<GuildMember> sortGuildMemberList(List<GuildMember> list) {
//		//服务器筛选最强佣兵优先级：筛选依次为：高到低品阶>高到低等级>高到低资质>高到低战力>低到高佣兵ID
//		Collections.sort(list,new Comparator<GuildMember>()
//		{
//			public int compare(GuildMember o1, GuildMember o2) {
//				if(o1.getPosition() > o2.getPosition()){
//					return 1;
//					
//				}else if(o1.getPosition() == o2.getPosition()){
//					 if(o1.getJoinTimer() > o2.getJoinTimer()){
//							return 1;
//					 }
//			
//				}
//				return 0;
//			}});
//		return list;
//	}
//	
//	
	
}
