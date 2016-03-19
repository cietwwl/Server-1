package com.rwbase.dao.guildSecretArea;

import java.util.UUID;

import com.bm.rank.RankType;
import com.bm.rank.secret.SecretExtAttribute;
import com.bm.rank.secret.SecretInfoComp;
import com.playerdata.Player;
import com.rw.fsutil.cacheDao.DataKVCacheDao;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;


public class SecretAreaInfoDAO extends DataKVCacheDao<SecretAreaInfo> {
	private static SecretAreaInfoDAO m_instance = new SecretAreaInfoDAO();
	private SecretAreaInfoDAO(){}
	
	public static SecretAreaInfoDAO getInstance(){
		return m_instance;
	}
	public SecretAreaInfo findTarget(Player player){
		SecretInfoComp comp = new SecretInfoComp();
		String testKey = UUID.randomUUID().toString();
		comp.setSecretId(testKey);
		comp.setBattleForce(player.getHeroMgr().getFightingTeam());
		comp.setEndTime(System.currentTimeMillis());
		comp.setSourceNum(0);
		Ranking ranking = RankingFactory.getRanking(RankType.SECRET_RANK);
		RankingEntry<SecretInfoComp, SecretExtAttribute> newEntry=	ranking.addOrUpdateRankingEntry(testKey, comp, comp);
		int place=ranking.getRanking(testKey);//获取所在位置
		ranking.removeRankingEntry(testKey);
		int size = ranking.size();
		int block=10;//轮流上下变动的人数  
		int f =-1;//加减符号
		for(int i=0;i<size;i++){
			int index=place;
			if(i%block==0){
				f=-f;
				index = place-f*block*(i/block);//
			}
			if(size<block){
				index =(int) (Math.random() * size);//随即
			}else{
				index = index+f*(int) (Math.random() * block);//获取所在位置    上下波动边界值 （index-n*block） <=遍历值<=（index+n*block）
			}
			
			if(index<1||index>size){
				continue;
			}
			RankingEntry<SecretInfoComp, SecretExtAttribute> otherPlayer=ranking.getRankingEntry(index);
			if(otherPlayer==null){
				continue;
			}
			SecretAreaInfo secretInfo= dataMap.get(otherPlayer.getKey());
			if(secretInfo==null){//不存在
				continue;
			}
			if (secretInfo.getClosed()) {//已过时
				continue;
			}
			if (secretInfo.getProtect()) {//掠夺上限的秘境
				continue;
			}
			if(secretInfo.getSecretArmyMap().get(player.getUserId())!=null){//	//自己的秘境
				continue;
			}
//			if (secretInfo.getFightTime()!=0) {//保护时间的秘境
//				continue;
//			}
			// 小于10分钟的秘境(确保有掠夺资源)
			if (System.currentTimeMillis()-secretInfo.getBegainTime()  < 10* 60 * 1000) {
				continue;
			}
			return secretInfo;
		}
		return null;
	}
	
}