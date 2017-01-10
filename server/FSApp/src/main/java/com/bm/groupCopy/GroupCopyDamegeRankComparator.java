package com.bm.groupCopy;

import java.util.Comparator;

import org.springframework.util.StringUtils;

import com.rwbase.dao.groupCopy.db.ApplyInfo;
import com.rwbase.dao.groupCopy.db.CopyItemDropAndApplyRecord;
import com.rwbase.dao.groupCopy.db.DropInfo;
import com.rwbase.dao.groupCopy.db.GroupCopyArmyDamageInfo;
import com.rwproto.GroupCopyAdminProto.MemberInfo;
import com.rwproto.GroupCopyAdminProto.MemberInfo.Builder;

public class GroupCopyDamegeRankComparator implements Comparator<GroupCopyArmyDamageInfo>{

	@Override
	public int compare(GroupCopyArmyDamageInfo o1, GroupCopyArmyDamageInfo o2) {
		if(o1.getDamage() > o2.getDamage()){
			return 1;
		}else if(o1.getDamage() < o2.getDamage()){
			return -1;
		}
		return o1.getTime() > o2.getTime() ? 1 : -1;
	}

	public static class DamageComparator implements Comparator<MemberInfo.Builder>{

		@Override
		public int compare(Builder o1, Builder o2) {
			if(o1.getDamage() > o2.getDamage()){
				return -1;
			}
			return 1;
		}

	}
	
	public static class ApplyItemComparator implements Comparator<CopyItemDropAndApplyRecord>{

		@Override
		public int compare(CopyItemDropAndApplyRecord o1, CopyItemDropAndApplyRecord o2) {
			if(Integer.parseInt(o1.getChaterID()) < Integer.parseInt(o2.getChaterID()))
				return -1;
			return 1;
				
			
		}
		

	}
	
	public static class DropItemComparator implements Comparator<DropInfo>{

		@Override
		public int compare(DropInfo o1, DropInfo o2) {
			
			return o1.getTime() < o2.getTime() ? -1 : 1;
		}
		
	}
	
	public static class ApplyRoleComparator implements Comparator<ApplyInfo>{

		@Override
		public int compare(ApplyInfo o1, ApplyInfo o2) {
			if(StringUtils.isEmpty(o1.getDistRoleName()) && StringUtils.isEmpty(o2.getDistRoleName()) ){
				return o1.getApplyTime() < o2.getApplyTime() ? -1 : 1;
			}else if(!StringUtils.isEmpty(o1.getDistRoleName()) && StringUtils.isEmpty(o2.getDistRoleName())){
				return -1;
			}else if(StringUtils.isEmpty(o1.getDistRoleName()) && !StringUtils.isEmpty(o2.getDistRoleName())){
				return 1;
			}else if(!StringUtils.isEmpty(o1.getDistRoleName()) && !StringUtils.isEmpty(o2.getDistRoleName())){
				return o1.getApplyTime() < o2.getApplyTime() ? -1 : 1;
			}
			return 0;
		}
		
	}
}
