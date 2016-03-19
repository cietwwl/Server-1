
package com.rw.service.gm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import com.bm.guild.GuildGTSMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.RankingMgr;
import com.playerdata.guild.GuildDataMgr;
import com.rw.service.Email.EmailUtils;
import com.rw.service.gm.hero.GMHeroProcesser;
import com.rw.service.ranking.ERankingType;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.common.enu.eStoreConditionType;
import com.rwbase.dao.copy.cfg.MapCfg;
import com.rwbase.dao.copy.cfg.MapCfgDAO;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.MsgGMResponse;
import com.rwproto.GMServiceProtos.eGMResultType;
import com.rwproto.MsgDef.Command;

public class GMHandler {
	private HashMap<String, String> funcCallBackMap = new HashMap<String, String>();
	private static GMHandler instance = new GMHandler();
	//是否激活gm指令
	private boolean active = false;

	private GMHandler() {
		initMap();
	};

	public static GMHandler getInstance() {
		return instance;
	}

	private void initMap() {

		funcCallBackMap.put("additem", "addItem");
		funcCallBackMap.put("addpower", "addPower");
		funcCallBackMap.put("addcoin", "addCoin");
		funcCallBackMap.put("addgold", "addGold");
		funcCallBackMap.put("addhero", "addHero");
		funcCallBackMap.put("clrbag", "clrBag");
		funcCallBackMap.put("setlevel", "setLevel");
		funcCallBackMap.put("ranksort", "rankSort");
		funcCallBackMap.put("addexp", "addExp");
		funcCallBackMap.put("sendemail", "sendEmail");
		funcCallBackMap.put("addheroexp", "addHeroExp");
		funcCallBackMap.put("setattack", "setAttack");
		funcCallBackMap.put("addattack", "addAttack");
		funcCallBackMap.put("setmap", "setMap");
		funcCallBackMap.put("commonmsg", "commonMsg");// --test
		funcCallBackMap.put("setvip", "setVip");
		funcCallBackMap.put("setskillpointcount", "setskillpointcount");
		funcCallBackMap.put("recharge", "recharge");
		funcCallBackMap.put("reloadarena", "reloadArena");// --test
		funcCallBackMap.put("servertime", "serverTime");
		funcCallBackMap.put("createarena", "createArena");// --test

		funcCallBackMap.put("addguildnum", "addguildNum");// --test
		funcCallBackMap.put("sendtestemail", "sendTestEmail");// --test
		funcCallBackMap.put("addtower", "addTowerNum");// --test
		funcCallBackMap.put("setwjzh", "setWjzh");//
		funcCallBackMap.put("probstore", "probstore");
		funcCallBackMap.put("sendpmd", "sendPmd");//
		funcCallBackMap.put("addarenacoin", "addArenaCoin");
		funcCallBackMap.put("getallsecret", "getAllSecret");
		funcCallBackMap.put("teambringit", "teamBringit");
		funcCallBackMap.put("addhero", "addHero1");
		funcCallBackMap.put("setteam1", "setTeam1");
		funcCallBackMap.put("setteam2", "setTeam2");
	}

	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/** GM命令 */
	public boolean rankSort(String[] arrCommandContents, Player player) {
//		if (arrCommandContents == null || arrCommandContents.length < 1) {
//			System.out.println(" command param not right ...");
//			return false;
//		}
//		String rankType = arrCommandContents[0];
//		if (player != null) {
//			RankingMgr.getInstance().onUpdateSort(ERankingType.valueOf(Integer.parseInt(rankType)));
//			return true;
//		}
		return false;
	}

	// 函数名与funcCallBackMap里面的value相同
	public boolean addItem(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 2) {
			System.out.println(" command param not right ...");
			return false;
		}
		int itemId = Integer.parseInt(arrCommandContents[0]);
		int itemNum = Integer.parseInt(arrCommandContents[1]);
		if (player != null) {
			// TODO @fixing HC 减少作弊的循环次数，提高效率，如果是可叠加物品就直接把全部数量放过去就好了
			// for (int i = 0; i < itemNum; i++) {
			// player.getItemBagMgr().addItem(itemId, itemNum);
			player.getItemBagMgr().addItem(itemId, itemNum);
			// }
			return true;
		}
		return false;
	}

	/*
	 * 设定副本地图的通关关卡
	 */
	public boolean setMap(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			player.NotifyCommonMsg("命令有误，请重新输入");
			return false;
		}
		String id = arrCommandContents[0];
		if (id == null) {
			player.NotifyCommonMsg("mapid有错");
			return false;
		}
		MapCfg map = (MapCfg) MapCfgDAO.getInstance().getCfgById(id);
		if (map == null) {
			player.NotifyCommonMsg("mapid有错");
			return false;
		}

		MsgCopyResponse.Builder copyResponse = player.getCopyRecordMgr().setMapByGM(map);// 获取要新增的关卡...
		player.SendMsg(Command.MSG_CopyService, copyResponse.build().toByteString());
		return true;
	}

	public boolean addPower(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int nAddPower = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.addPower(nAddPower);
			return true;
		}
		return false;
	}

	public boolean addHero(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		String heroId = arrCommandContents[0];
		if (player != null) {
			player.getHeroMgr().addHero(heroId);
			return true;
		}
		return false;
	}

	public boolean addCoin(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().addCoin(addNum);
			return true;
		}
		return false;
	}

	public boolean addGold(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().addGold(addNum);
			return true;
		}
		return false;
	}

	public boolean clrBag(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			if ("1".equals(arrCommandContents[0])) {
				player.getItemBagMgr().removeAllItems();
				return true;
			}
		}
		return false;
	}

	public boolean setVip(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int num = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.setVip(num);
			//设置界面更新vip
			player.getSettingMgr().checkOpen();
			return true;
		}
		return false;
	}
	public boolean setskillpointcount(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int num = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.getUserGameDataMgr().setSkillPointCount(num);
			return true;
		}
		return false;
	}

	public boolean recharge(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		int addNum = Integer.parseInt(arrCommandContents[0]);
		if (player != null) {
			player.AddRecharge(addNum);
			return true;
		}
		return false;
	}

	public boolean setLevel(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		String newLevel = arrCommandContents[0];
		if (player != null) {
			player.SetLevel(Integer.parseInt(newLevel));
			return true;
		}
		return false;
	}

	public boolean addExp(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		long addExp = Long.parseLong(arrCommandContents[0]);
		if (player != null) {
			player.addUserExp(addExp);
			return true;
		}
		return false;
	}

	public boolean sendEmail(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 2) {
			System.out.println(" command param not right ...");
			return false;
		}
		EmailUtils.sendEmail(player.getUserId(), arrCommandContents[0], arrCommandContents[1]);
		return true;
	}
	
	public boolean sendTestEmail(String[] arrCommandContents, Player player) {
		
		List<Integer> mailList = new ArrayList<Integer>();
		
		for (int i = 10004; i <= 10009; i++) {
			mailList.add(i);
		}
		
		for (int i = 10031; i <= 10035; i++) {
			mailList.add(i);
		}
		
		
		for (Integer iTemp : mailList) {			
			EmailUtils.sendEmail(player.getUserId(), String.valueOf(iTemp));
		}
		return true;
	}
	
	public boolean addTowerNum(String[] arrCommandContents, Player player) {
		int num = Integer.parseInt(arrCommandContents[0]);
		player.getTowerMgr().addTowerNum(num);;
		return true;
	}
	
	public boolean setWjzh(String[] arrCommandContents, Player player) {
	    player.unendingWarMgr.getTable().setNum(Integer.parseInt(arrCommandContents[0]) - 1);
		player.unendingWarMgr.save();
		return true;
	}
	
	
	
	public boolean addHeroExp(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if(arrCommandContents.length == 1){
			long addExp = Long.parseLong(arrCommandContents[0]);
			if(player != null){
				player.getHeroMgr().AddAllHeroExp(addExp);
				return true;
			}
			return false;			
		}
		
		int heroId =Integer.parseInt(arrCommandContents[0]);
		long addExp = Long.parseLong(arrCommandContents[1]);
		if (player != null) {
			player.getHeroMgr().getHeroByModerId(heroId).addHeroExp(addExp);
			return true;
		}
		return false;
	}

	public boolean setAttack(String[] arrCommandContents, Player player) {
		//此方法不再支持
		System.out.println(" command param not support any more ...");
		return false;
	}

	public boolean addAttack(String[] arrCommandContents, Player player) {
		//此方法不再支持
		System.out.println(" command param not support any more ...");
		return false;
	}

	public boolean commonMsg(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, arrCommandContents[0]);
			return true;
		}
		return false;
	}

	public boolean reloadArena(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			if ("1".equals(arrCommandContents[0])) {
//				ArenaBM.getInstance().initArenaInfoList();
				return true;
			}
		}
		return false;
	}

	public boolean serverTime(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			System.out.println(" command param not right ...");
			return false;
		}
		if (player != null) {
			if ("1".equals(arrCommandContents[0])) {
				Calendar c = Calendar.getInstance();
				;
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, c.getTime().toString());
				return true;
			}
		}
		return false;
	}

	public boolean addguildNum(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {

			return false;
		}
		if (player != null) {
			if ("1".equals(arrCommandContents[0])) {
				String guildId = player.getGuildUserMgr().getGuildId();
				GuildDataMgr guildMgr = GuildGTSMgr.getInstance().getById(guildId);
				guildMgr.getGuildPropTSMgr().gmAdd(player, 1);
				return true;
			}
		}
		return false;
	}

	
	public boolean probstore(String[] arrCommandContents, Player player){
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		if (player != null) {
			player.getStoreMgr().ProbStore(eStoreConditionType.WarCopy);
			return true;
		}
		return false;
	}
	
	public boolean sendPmd(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		if (player != null) {
			MainMsgHandler.getInstance().sendPmdGm(player, arrCommandContents);
			return true;
		}
		return false;
	}
	
	public boolean addArenaCoin(String[] arrCommandContents, Player player) {
		if (arrCommandContents == null || arrCommandContents.length < 1) {
			return false;
		}
		if (player != null) {
			int addNum = Integer.parseInt(arrCommandContents[0]);
			player.getUserGameDataMgr().addArenaCoin(addNum);
			return true;
		}
		return false;
	}
	
	public ByteString executeGMCommand(Player player, MsgGMRequest msgGMRequest) {
		MsgGMResponse.Builder msgGMResponse = MsgGMResponse.newBuilder().setMsgGMRequest(msgGMRequest);

		boolean bFuncCallBackState = false;
		String content = msgGMRequest.getContent();
		String[] arrCommands = content.split(";");
		String command;
		String[] arrCommandContents;
		int argsNum;
		for (int i = 0; i < arrCommands.length; i++) {
			command = arrCommands[i];
			// command = command.replace("* ","");
			arrCommandContents = command.split(" ");
			argsNum = arrCommandContents.length;
			if (argsNum < 3 || !arrCommandContents[0].equals("*")) {
				System.out.println(" command param not right or no * ...");
				bFuncCallBackState = false;
				break;
			}
			if (funcCallBackMap.containsKey(arrCommandContents[1].toLowerCase())) {
				String methodName = funcCallBackMap.get(arrCommandContents[1].toLowerCase());
				Method declaredMethod;
				try {
					declaredMethod = this.getClass().getDeclaredMethod(methodName, new Class[] { String[].class, Player.class });
					String[] param = new String[argsNum - 2];
					for (int k = 0; k < argsNum - 2; k++) {
						param[k] = arrCommandContents[k + 2];
					}
					bFuncCallBackState = (Boolean) declaredMethod.invoke(this, new Object[] { param, player });
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(" no related method ...");
				bFuncCallBackState = false;
			}
		}
		// System.out.println(bFuncCallBackState);
		if (bFuncCallBackState) {
			msgGMResponse.setEGMResultType(eGMResultType.SUCCESS);
			return msgGMResponse.build().toByteString();
		} else {
			msgGMResponse.setEGMResultType(eGMResultType.FAIL);
			return msgGMResponse.build().toByteString();
		}
	}
	
	
	public boolean executeGMCommand(Player player, String content) {
		boolean bFuncCallBackState = false;
		String[] arrCommands = content.split(";");
		String command;
		String[] arrCommandContents;
		int argsNum;
		for (int i = 0; i < arrCommands.length; i++) {
			command = arrCommands[i];
			arrCommandContents = command.split(" ");
			argsNum = arrCommandContents.length;
			if (argsNum < 3 || !arrCommandContents[0].equals("*")) {
				System.out.println(" command param not right or no * ...");
				bFuncCallBackState = false;
				break;
			}
			if (funcCallBackMap.containsKey(arrCommandContents[1].toLowerCase())) {
				String methodName = funcCallBackMap.get(arrCommandContents[1].toLowerCase());
				Method declaredMethod;
				try {
					declaredMethod = this.getClass().getDeclaredMethod(methodName, new Class[] { String[].class, Player.class });
					String[] param = new String[argsNum - 2];
					for (int k = 0; k < argsNum - 2; k++) {
						param[k] = arrCommandContents[k + 2];
					}
					bFuncCallBackState = (Boolean) declaredMethod.invoke(this, new Object[] { param, player });
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(" no related method ...");
				bFuncCallBackState = false;
			}
		}
		if (bFuncCallBackState) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getAllSecret(String[] arrCommandContents, Player player){
		if (arrCommandContents == null) {
			return false;
		}
		if (player != null) {
			player.getSecretMgr().getAllSecret();;
			return true;
		}
		return false;
	}
	
	public boolean teamBringit(String[] arrCommandContents, Player player){
//		if(arrCommandContents == null){
//			return false;
//		}
		if(player != null){
			GMHeroProcesser.processTeamBringit(arrCommandContents, player);
			return true;
		}
		return false;
	}
	
	public boolean addHero1(String[] arrCommandContents, Player player){
//		if(arrCommandContents == null){
//			return false;
//		}
		if(player != null){
			GMHeroProcesser.processAddhero(arrCommandContents, player);
			return true;
		}
		return false;
	}
	
	public boolean setTeam1(String[] arrCommandContents, Player player){
		
		if(arrCommandContents == null){
			return false;
		}
		if(player != null){
			GMHeroProcesser.processSetteam1(arrCommandContents, player);
			return true;
		}
		return false;
	}
	
	public boolean setTeam2(String[] arrCommandContents, Player player){
		if(arrCommandContents == null){
			return false;
		}
		if(player != null){
			GMHeroProcesser.processSetteam2(arrCommandContents, player);
			return true;
		}
		return false;
	}
}
