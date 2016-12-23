package com.bm.randomBoss;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import com.rwbase.dao.randomBoss.db.RandomBossRecord;

/**
 * 随机boss比较器
 * @author Alex
 * 2016年12月22日 上午10:48:30
 */
public class RandomBossComparator implements Comparator<RandomBossRecord>{
	
	private String ownerID;
	
	

	public RandomBossComparator(String ownerID) {
		this.ownerID = ownerID;
	}



	/**
	 * <pre>
	 * 比较逻辑：
	 * 1.还没有死血量少的在前。
	 * 2.自己发现的在前；
	 * </pre>
	 */
	@Override
	public int compare(RandomBossRecord o1, RandomBossRecord o2) {
		
		//比较血量
		if(o1.getLeftHp() < o2.getLeftHp() && o1.getLeftHp() != 0){
			return 1;
		}else if(o1.getLeftHp() > o2.getLeftHp()){
			return -1;
		}
		
		//血量相同，比较是不是自己的
		return checkOwner(o1,o2);
	}



	/**
	 * 检查是否属于角色自己
	 * @param o1
	 * @param o2
	 * @return
	 */
	private int checkOwner(RandomBossRecord o1, RandomBossRecord o2) {
		if(StringUtils.equals(o1.getOwnerID(), ownerID)){
			return 1;
		}else if(StringUtils.equals(o2.getOwnerID(), ownerID)){
			return -1;
		}
		return 0;
	}

	

	
	
}
