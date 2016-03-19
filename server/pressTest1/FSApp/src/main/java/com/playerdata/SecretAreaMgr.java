package com.playerdata;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bm.rank.RankType;
import com.bm.rank.secret.SecretExtAttribute;
import com.bm.rank.secret.SecretInfoComp;
import com.bm.secretArea.SecretAreaInfoGMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.guildSecretArea.SecretAreaBattleInfo;
import com.rwbase.dao.guildSecretArea.SecretAreaBattleInfoHolder;
import com.rwbase.dao.guildSecretArea.SecretAreaDefHolder;
import com.rwbase.dao.guildSecretArea.SecretAreaDefRecord;
import com.rwbase.dao.guildSecretArea.SecretAreaInfo;
import com.rwbase.dao.guildSecretArea.SecretAreaInfoDAO;
import com.rwbase.dao.guildSecretArea.SecretAreaUserInfo;
import com.rwbase.dao.guildSecretArea.SecretAreaUserInfoHolder;
import com.rwbase.dao.guildSecretArea.SecretAreaUserRecord;
import com.rwbase.dao.guildSecretArea.SecretAreaUserRecordHolder;
import com.rwbase.dao.guildSecretArea.projo.SecretArmy;
import com.rwbase.dao.guildSecretArea.projo.SecretUserSource;
import com.rwbase.dao.guildSecretArea.projo.SourceType;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.secretArea.SecretAreaCfgDAO;
import com.rwbase.dao.secretArea.pojo.SecretAreaCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.SecretAreaProtos.EMterialType;
import com.rwbase.dao.guildSecretArea.projo.ESecretType;

public class SecretAreaMgr {

	private String roleId;
	private SecretAreaUserInfoHolder secretAreaUserInfoHolder;//持有玩家秘境基本信息
	private SecretAreaDefHolder secretAreaDefHolder;//持有玩家掠夺记录
	private SecretAreaUserRecordHolder secretAreaUserRecordHolder;//持有玩家秘境奖励信息
	private SecretAreaBattleInfoHolder secretAreaBattleInfoHolder;//持有玩家战斗信息

	public static long SecretkeyTime = 0;// 一点密钥时间恢复
	public static final int KEY_COST_PER_FIGHT = 2;// 密钥花费
	public static final int SECRET_KEY_RECOVER_LIMIT = 30;// 密钥	恢复上限	
	public static final int SECRET_KEY_LIMIT = 100;// 密钥可拥有最大数量（不断领取掠夺密钥情况）
	
	private Player m_pPlayer;//当前玩家
	
	public void init(Player player){
		m_pPlayer = player;
		roleId = player.getUserId();
		//初始化
		secretAreaUserInfoHolder = new SecretAreaUserInfoHolder(player);
		secretAreaDefHolder = new SecretAreaDefHolder(roleId);
		secretAreaUserRecordHolder = new SecretAreaUserRecordHolder(roleId);
		secretAreaBattleInfoHolder = new SecretAreaBattleInfoHolder(player);
		SecretkeyTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.SECRET_REVERT_TIME)* 60 * 1000;// 一点密钥时间恢复（秒）
	}
	
	public void synData(){
		Player player = this.m_pPlayer;

		
		List<String> currAreaIdList = secretAreaUserInfoHolder.get().getCurrentAreaList();//玩家已开辟的秘境id列表
		boolean isUpdate=synUserOwenAreaList(player, currAreaIdList);// 发送玩家所有拥有秘境到客户端
		
		if(isUpdate){//更新玩家基本信息（立即更新）
			secretAreaUserInfoHolder.udpate(player);
		}else{//这个数据库不是马上更新
			secretAreaUserInfoHolder.synData(player);
		}
		secretAreaDefHolder.synData(player);//发送掠夺记录信息
		secretAreaUserRecordHolder.synData(player);//发送秘境奖励信息
		secretAreaBattleInfoHolder.synData(player);//发送战斗信息
	}
	/**
	 * 发送玩家所有拥有秘境到客户端
	 * @param player 玩家
	 * @param currAreaIdList 拥有秘境列表
	 * @return 是否成功
	 */
	private boolean synUserOwenAreaList(Player player, List<String> currAreaIdList) {
		List<SecretAreaInfo> haveAreaInfoList = new ArrayList<SecretAreaInfo>();
		boolean isUpdateUserInfo=false;
		if(currAreaIdList!=null&&currAreaIdList.size()!=0){//拥有秘境
			for (int i=currAreaIdList.size()-1;i>=0 ; i--) {
				String secretId = currAreaIdList.get(i);
				SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);
				if(areaInfo!=null){
					haveAreaInfoList.add(areaInfo);//添加到更新列表
				}else{//移除不存在的秘境  秘境消失之后上线的玩家
					currAreaIdList.remove(secretId);
					isUpdateUserInfo = true;
				}
			}
		}
		//发送秘境列表信息集合到客户端
		ClientDataSynMgr.synDataList(player, haveAreaInfoList, eSynType.SECRETAREA_INFO, eSynOpType.UPDATE_LIST);
		return isUpdateUserInfo;
	}
	//数据库更新对应表
	public void flush(){
		secretAreaUserInfoHolder.flush();
		secretAreaDefHolder.flush();
		secretAreaUserRecordHolder.flush();
		secretAreaBattleInfoHolder.flush();
	}
	
	/**时间更新密钥**/
	public void updateKeyNumByTime() {
		SecretAreaUserInfo secretAreaUserInfo= getSecretAreaUserInfo();//玩家秘境基本信息
		if (secretAreaUserInfo.getSecretKey() >= SecretAreaMgr.SECRET_KEY_RECOVER_LIMIT){//大于可拥有密钥上限 不更新
			return;
		}
		
		if(secretAreaUserInfo.getKeyUseTime()<=0){//回复一点密钥时间（没有可回复密钥）
			return;
		}
		if (secretAreaUserInfo.getKeyUseTime() < System.currentTimeMillis()) {//恢复时间到
			int currKey = secretAreaUserInfo.getSecretKey()+1;//回复一点密钥
			int overCount =(int)((System.currentTimeMillis()-secretAreaUserInfo.getKeyUseTime())/SecretAreaMgr.SecretkeyTime);//一段时间登入 情况
			currKey+=overCount;//总回复的密钥
			currKey=currKey>SecretAreaMgr.SECRET_KEY_RECOVER_LIMIT?SecretAreaMgr.SECRET_KEY_RECOVER_LIMIT:currKey;
			if (currKey < SecretAreaMgr.SECRET_KEY_RECOVER_LIMIT) {
				long useTime = (System.currentTimeMillis()+SecretAreaMgr.SecretkeyTime);//回复时间更新
				secretAreaUserInfo.setKeyUseTime(useTime);//更新下点密钥恢复时间
			}else{//已经恢复满
				secretAreaUserInfo.setKeyUseTime(0);
			}	
			secretAreaUserInfo.setSecretKey(currKey);//更新密钥
			//同步
			updateAreaUserInfo();
		}
	}
	
	/**
	 * 时间更新秘境资源
	 */
	public void updateSecretByTime() {
		SecretAreaUserInfo secretUserInfo= getSecretAreaUserInfo();
		List<String> secertList=secretUserInfo.getCurrentAreaList();//玩家拥有的所有秘境
		for(int i=0;i<secertList.size();i++){
			String secretId = secertList.get(i);
			SecretAreaInfo areaInfo=SecretAreaInfoGMgr.getInstance().getById(secretId);
			if (areaInfo == null){//无 可能转化为资源领取
				continue;
			}
//			if(areaInfo.getClosed()){
//				continue;
//			}
			//更新秘境保护时间状态
			if(areaInfo.getFightTime()!=0&&System.currentTimeMillis()>areaInfo.getFightTime()){
				areaInfo.setFightTime(0);//保护时间结束
			}
			//时间到更新秘境状态 
			if (System.currentTimeMillis() >= areaInfo.getEndTime()) {//时间到
				areaInfo.setClosed(true);//设置关闭状态
				Ranking ranking = RankingFactory.getRanking(RankType.SECRET_RANK);
				ranking.removeRankingEntry(areaInfo.getId());//从秘境排行中移除
			}
			//更新秘境资源状态
			updateArmySource(areaInfo,m_pPlayer.getUserId());//时间更新拥有资源
			if(areaInfo.getClosed()){
				updateUserRecord(areaInfo);//秘境转资源奖励
				deleteSecretRank(areaInfo.getId());//关闭的秘境从排行中移除
			}
		}
		
	}
	/**
	 * 更新秘境资源
	 * @param areaInfo  更新的秘境信息
	 * @param targetId  更新对象的userId
	 */
	public void updateArmySource(SecretAreaInfo areaInfo,String targetId){//时间更新拥有资源，队伍调整更新资源
		SecretArmy secretArmy = areaInfo.getSecretArmyMap().get(targetId);//驻守信息
//		for(SecretArmy army:areaInfo.getSecretArmyMap().values()){
//			System.out.print("army="+army.getUserId());
//		
//		}
		if(secretArmy==null)
			return;
		int currSourceNum = secretArmy.getSourceNum();//当前资源
		int currMaterialNum = secretArmy.getGuildMaterial();//公会材料
		List<Integer> getSourceList = getTypeTotalNum(areaInfo,targetId);//获得资源
		if(getSourceList.size()>0){//有获得资源
			secretArmy.setSourceNum(currSourceNum+getSourceList.get(0));//更新资源
			long updateTime = System.currentTimeMillis()>areaInfo.getEndTime()?areaInfo.getEndTime():System.currentTimeMillis();//当前更新资源时间
			secretArmy.setSourceChangeTime(updateTime);//获取一次资源更新时间记录
			secretArmy.setGuildMaterial(currMaterialNum+getSourceList.get(1));//更新公会材料
			SecretAreaInfoGMgr.getInstance().update(m_pPlayer,areaInfo);//更新保存秘境，发送客户端更新
		}
	}
	/**
	 * //更新秘境排行信息
	 * @param areaInfo 秘境信息
	 */
	public void updateSecretRank(SecretAreaInfo areaInfo){//更新秘境排行信息
		Ranking ranking = RankingFactory.getRanking(RankType.SECRET_RANK);
		RankingEntry<SecretInfoComp, SecretExtAttribute> entry = ranking.getRankingEntry(areaInfo.getId());//获取秘境排行信息
		SecretInfoComp comp=null;
		if(entry==null||entry.getComparable()==null){//无添加过
			comp = new SecretInfoComp();
		}else{
			comp = entry.getComparable();
		}
		
		int totalFight = getTotalFight(areaInfo);
		int sourceNum = getTotalSource(areaInfo);
		comp.setSecretId(areaInfo.getId());
		//排列条件
		comp.setBattleForce(totalFight);//战斗力
		comp.setEndTime(areaInfo.getEndTime());//结束时间
		comp.setSourceNum(sourceNum);//资源数量
		//更新秘境排行排列
		RankingEntry<SecretInfoComp, SecretExtAttribute> newEntry=	ranking.addOrUpdateRankingEntry(areaInfo.getId(), comp, comp);
		System.out.print(newEntry);
	}
	/**
	 * 去除秘境排行信息
	 * @param secretId 秘境id
	 */
	public void deleteSecretRank(String secretId){//更新秘境排行信息
		Ranking ranking = RankingFactory.getRanking(RankType.SECRET_RANK);
		RankingEntry<SecretInfoComp, SecretExtAttribute> entry = ranking.getRankingEntry(secretId);
		if(entry==null||entry.getComparable()==null){
			return;
		}
		ranking.removeRankingEntry(secretId);
	}
	/**
	 * 驻守秘境玩家的总战斗力
	 * @param secretInfo 秘境
	 * @return 总战斗力
	 */
	private int getTotalFight(SecretAreaInfo secretInfo){
		int fight=0;//战斗力
		ConcurrentHashMap<String,SecretArmy> SecretArmyMap= secretInfo.getSecretArmyMap();//驻守信息
		if(SecretArmyMap==null){
			return 0;
		}
		Enumeration<SecretArmy> armyEnum=secretInfo.getArmyEnumeration();
		boolean isHas = false;
		while(armyEnum.hasMoreElements()){//累加战斗力
			SecretArmy army = armyEnum.nextElement();
			fight+=army.getBattleForce();
		}
		return fight;
	
	}
	/**
	 * 秘境 所有玩家产出资源
	 * @param secretInfo 秘境
	 * @return所有玩家产出资源
	 */
	private int getTotalSource(SecretAreaInfo secretInfo){
		int source=0;
		ConcurrentHashMap<String,SecretArmy> SecretArmyMap= secretInfo.getSecretArmyMap();//驻守信息
		if(SecretArmyMap==null){
			return 0;
		}
		Enumeration<SecretArmy> armyEnum=secretInfo.getArmyEnumeration();
		boolean isHas = false;
		while(armyEnum.hasMoreElements()){
			SecretArmy army = armyEnum.nextElement();//累加产出资源
			source+=army.getSourceNum();
		}
		return source;
	
	}
	
	/**
	 * //秘境转资源奖励
	 * @param areaInfo 转化的秘境
	 */
	private void updateUserRecord(SecretAreaInfo areaInfo){//秘境转资源奖励
		if (areaInfo.getClosed()) {//###需加锁
			SecretAreaInfoGMgr.getInstance().remove(m_pPlayer, areaInfo);//通知删除事件 ###需加锁
			Enumeration<String> userIdEnum=areaInfo.getSecretArmyKeys();
			while(userIdEnum.hasMoreElements()){//每个驻守玩家分发奖励
				String userId = userIdEnum.nextElement();
				SecretAreaUserRecord userRecord= new SecretAreaUserRecord();//秘境奖励
				userRecord.setUserId(userId);
				userRecord.setId(areaInfo.getId());
				userRecord.setGetGiftTime(""+System.currentTimeMillis());
				userRecord.setSecretType(areaInfo.getSecretType());
				userRecord.setStatus(0);
				SecretArmy secretArmy=areaInfo.getSecretArmyMap().get(userId);
				List<SecretUserSource> userSourceList = new ArrayList<SecretUserSource>();//资源集合
				SecretUserSource userSource = new SecretUserSource();
				if(secretArmy!=null){
					List<SourceType> sourceNumList=new ArrayList<SourceType>();;
					//秘境资源产出
					SourceType source = new SourceType();
					source.setSecretType(areaInfo.getSecretType());
					source.setNum(secretArmy.getSourceNum());
					source.setMaterialType(getMaterialBySecretType(areaInfo.getSecretType()).getNumber());
					sourceNumList.add(source);
					
					//公会材料
					SourceType sourceMater = new SourceType();
					sourceMater.setSecretType(areaInfo.getSecretType());
					sourceMater.setNum(secretArmy.getGuildMaterial());
					sourceMater.setMaterialType(EMterialType.guildMaterial.getNumber());
					sourceNumList.add(sourceMater);
					userSource.setUserId(secretArmy.getUserId());
					userSource.setSourceList(sourceNumList);//一个玩家产出资源
				}
				userSourceList.add(userSource);
				userRecord.setUserSourceList(userSourceList);//当前玩家资源集合
				updateUserRecord(userId,userRecord,areaInfo);//每个玩家更新对应产出的奖励 ###需加锁
			}
		}
	}
	private EMterialType getMaterialBySecretType(ESecretType secretType){
		EMterialType type=EMterialType.goldType;
		switch (secretType) {
		case GOLD_TYPE_ONE:
		case GOLD_TYPE_THREE:
		case GOLD_TYPE_TEN:
			type=EMterialType.goldType;
			break;
		case EXP_TYPE_ONE:
		case EXP_TYPE_THREE:
		case EXP_TYPE_TEN:
			type=EMterialType.expType;
			break;
		case STONG_TYPE_ONE:
		case STONG_TYPE_THREE:
		case STONG_TYPE_TEN:
			type=EMterialType.strenthType;
			break;
		}
		return type;
	}
	private void updateUserRecord(String userId,final SecretAreaUserRecord userRecord,final SecretAreaInfo areaInfo){
		GameWorldFactory.getGameWorld().asyncExecute(userId, new PlayerTask() {
			@Override
			public void run(Player player) {
				// TODO Auto-generated method stub
				SecretAreaUserInfo userInfo=player.getSecretMgr().getSecretAreaUserInfo();
				int index=userInfo.getCurrentAreaList().indexOf(areaInfo.getId());
				if(index!=-1){
					userInfo.getCurrentAreaList().remove(areaInfo.getId());//现有秘境移除
				}
				if(areaInfo.getOwnerId().equals(player.getUserId())){
					
					int haveIndex=userInfo.getOwnAreaIdList().indexOf(areaInfo.getId());
					if(haveIndex==-1)
					userInfo.getOwnAreaIdList().add(areaInfo.getId());//放入备用秘境
				}
				player.getSecretMgr().updateAreaUserInfo();
				
				if(player.getSecretMgr().getAreaUserRecord(userRecord.getId())!=null){//加锁  已存在
					return;
				}
				player.getSecretMgr().addAreaUserRecord(userRecord);
			}
		});
	}
	
	/**
	 * 获取资源数量
	 * @param secret 秘境数据
	 * @param playerId 玩家
	 * @param fightValue z战斗力
	 * @return list<Integer> list[0]资源 list[1]公会材料
	 */
	private List<Integer> getTypeTotalNum(SecretAreaInfo secret, String targetId){
		SecretArmy army=secret.getSecretArmyMap().get(targetId);
		List<Integer> sourceList = new ArrayList<Integer>();
		if(army==null){
			return sourceList;
		}
		long begainTime = army.getSourceChangeTime();
		int fightValue =army.getBattleForce();
		SecretAreaCfg secretCfg = SecretAreaCfgDAO.getInstance().getSecretCfg(secret.getSecretType().getNumber());
		if(secretCfg==null)
			return sourceList;
		float speed = speedPerHour(secretCfg,fightValue);// 获取资源速度小时
		long currTime = (System.currentTimeMillis() > secret.getEndTime() ? secret.getEndTime() : System.currentTimeMillis());
		long second = (currTime - begainTime)/1000;
		float hour = (float)second/60/60;
		int totalNum =(int)(hour*speed);//原有加增加
		sourceList.add(totalNum);
		
		//每分钟帮派建筑的产出数量总产出值=配置值*产出时间*60
		int guildMaterial = (int)(secretCfg.getGuildRate()*60*hour);
		sourceList.add(guildMaterial);
		return sourceList;
	}

	/**计算速度
	 * @param type 秘境类型
	 * @param fightValue 总战斗力
	 * @return
	 */
	private float speedPerHour(SecretAreaCfg secretCfg, int fightValue) {// 获取资源速度/ 秒 type 资源类型  fightValue战斗力
		float speed = (secretCfg.getTypeRate() * fightValue * 60);
		return speed;
	}
	
	/**
	 * 
	 * @param playerId
	 * @return 秘境玩家基本信息
	 */
	public SecretAreaUserInfo getSecretAreaUserInfo() {//获取玩家秘境基本信息
		return secretAreaUserInfoHolder.get();
	}
	public void updateAreaUserInfo(){//玩家基本信息 发送到客户端
		 secretAreaUserInfoHolder.udpate(m_pPlayer);;
	}
	
	
	/**
	 * 
	 * @param secretId
	 * @return 秘境信息
	 */
	public SecretAreaInfo getSecretAreaInfo(String areaId){//获取秘境
		return SecretAreaInfoDAO.getInstance().get(areaId);
	}
	
	
	/**
	 * 
	 * @param  recordId
	 * @return 秘境奖励
	 */
	public SecretAreaUserRecord getAreaUserRecord(String recordId){//获取秘境奖励
		return secretAreaUserRecordHolder.getReord(recordId);
	}
	/**
	 * 
	 * @param Id
	 * @return 秘境奖励
	 */
	public void addAreaUserRecord(SecretAreaUserRecord userRecord){//添加获取秘境奖励
		 secretAreaUserRecordHolder.addRecord(m_pPlayer,userRecord);
	}
	/**
	 * 
	 * @param tId
	 * @return 秘境奖励
	 */
	public void delectAreaUserRecord(String recordId){//奖励删除
		 secretAreaUserRecordHolder.removeReord(m_pPlayer, recordId);
	}
	
	
	/**
	 * 
	 * @param secretId
	 * @return 秘境战斗信息
	 */
	public SecretAreaBattleInfo getAreaBattleInfo(){//获取秘境
		return secretAreaBattleInfoHolder.get();
	}
	/**
	 * 
	 * @return 更新战斗信息
	 */
	public void BattleInfoToClient(){//获取秘境
		secretAreaBattleInfoHolder.udpate(m_pPlayer);;
	}
	/**
	 * 
	 * @return 更新战斗信息
	 */
	public void restBattleInfo(){//获取秘境
		secretAreaBattleInfoHolder.reset(m_pPlayer);;
	}
	
	
	public void addDefRecord(SecretAreaDefRecord defRecord){//记录信息
		 secretAreaDefHolder.addRecord(m_pPlayer,defRecord);
	}
	public SecretAreaDefRecord getDefRecord(String id){//记录信息
		return secretAreaDefHolder.get(id);
	}
	public void updateDefRecord(SecretAreaDefRecord defRecord){//记录信息
		 secretAreaDefHolder.updateRecord(m_pPlayer,defRecord);
	}
	/**
	 * @param recordId 记录id
	 * @return
	 */
	public SecretAreaDefRecord getSecretDefRecord(String recordId){//记录信息
		return secretAreaDefHolder.getReord(recordId);
	}
	/**
	 * @param recordId 记录id
	 * @return
	 */
	public List<SecretAreaDefRecord> getSecretDefRecordList(){//记录信息
		return secretAreaDefHolder.getRecordList();
	}
	/**
	 * @param 根据秘境id获取记录
	 * @return
	 */
	public SecretAreaDefRecord getSecretRecordBySecretID(String secretId){//记录信息
		return secretAreaDefHolder.get(secretId);
	}
	/**
	 * 移除记录
	 * @param recordId 记录id
	 * @return
	 */
	public void removeRecordGift(String recordId){//记录信息
		secretAreaDefHolder.removeReord(m_pPlayer, recordId);
	}
	
	/**
	 * 移除记录All
	 * @param recordId 记录id
	 * @return
	 */
	public void removeDefRecordList(List<String> removeIdList){//记录信息
		secretAreaDefHolder.removeAllReord(m_pPlayer,removeIdList);
	}
	/**
	 * GM完成自己所有秘境
	 */
	public void getAllSecret(){
		List<String> currAreaIdList = secretAreaUserInfoHolder.get().getCurrentAreaList();
		List<SecretAreaInfo> haveAreaInfoList = new ArrayList<SecretAreaInfo>();
		boolean isUpdateUserInfo=false;
		if(currAreaIdList!=null&&currAreaIdList.size()!=0){
			for (int i=currAreaIdList.size()-1;i>=0 ; i--) {
				String secretId = currAreaIdList.get(i);
				SecretAreaInfo areaInfo = SecretAreaInfoGMgr.getInstance().getById(secretId);
				if(areaInfo!=null){
					//转化为奖励
					areaInfo.setClosed(true);
					SecretArmy army=areaInfo.getSecretArmyMap().get(m_pPlayer.getUserId());
					army.setSourceNum(0);
					army.setGuildMaterial(0);
					army.setSourceChangeTime(System.currentTimeMillis()-getTypeEndTime(areaInfo.getSecretType()));
					updateArmySource(areaInfo,m_pPlayer.getUserId());//时间更新拥有资源
					updateUserRecord(areaInfo);
					deleteSecretRank(areaInfo.getId());
				}else{//移除不存在的秘境  秘境消失之后上线的玩家
					currAreaIdList.remove(secretId);
					isUpdateUserInfo = true;
					deleteSecretRank(areaInfo.getId());
				}
			}
		}

	}
	private long getTypeEndTime(ESecretType secretType){
		long endTime=0;
		switch (secretType) {
		case GOLD_TYPE_ONE:
		case EXP_TYPE_ONE:
		case STONG_TYPE_ONE:
			endTime = 60 * 60 * 1000;
			break;
		case GOLD_TYPE_THREE:
		case EXP_TYPE_THREE:
		case STONG_TYPE_THREE:
			endTime = 3 * 60 * 60 * 1000;
			break;
		case GOLD_TYPE_TEN:
		case EXP_TYPE_TEN:
		case STONG_TYPE_TEN:
			endTime =  10 * 60 * 60 * 1000;
			break;
	}
		return endTime;
	
}
}

