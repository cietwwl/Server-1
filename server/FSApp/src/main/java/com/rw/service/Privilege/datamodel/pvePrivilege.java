package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.PvePrivilegeNames;

@SuppressWarnings("unused")
public class pvePrivilege extends AbstractConfigChargeSource<PvePrivilegeNames> {
  private String source; //特权来源
  private int maxResetCount; //封神台可重置次数
  private int sweepTimeDec; //封神台每层扫荡时间减少X秒
  private int arrayMaxResetCnt; //万仙阵可重置次数
  private int warfareRewardAdd; //无尽战火积分增加x%（万分比）
  private int warfareResetCnt; //无尽战火可重置次数
  private int treasureTimeDec; //聚宝之地挑战cd减少x秒
  private int treasureResetCnt; //聚宝之地可重置次数
  private int expTimeDec; //炼气山谷挑战cd减少x秒
  private int expResetCnt; //炼气山谷可重置次数
  private int survivalTimeDec; //生存幻境挑战cd减少x秒
  private int survivalResetCnt; // 生存幻境可重置次数


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<PvePrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(PvePrivilegeNames.class, cfgHelper);
		} catch (IllegalArgumentException e) {
			cause = e;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			cause = e;
			e.printStackTrace();
		}
		if (cause != null) {
			throw new RuntimeException(cause);
		}
	}
}