package com.rw.service.Privilege.datamodel;

import com.rwproto.PrivilegeProtos.StorePrivilegeNames;

@SuppressWarnings("unused")
public class storePrivilege extends AbstractConfigChargeSource<StorePrivilegeNames> {
  private String source; //特权来源
  private boolean isOpenBlackmarketStore; //永久特殊商店1(黑市商店)开启
  private boolean isOpenMysteryStore; //永久特殊商店2(神秘商人)开启
  private int storeFreeRefreshCnt; //普通商店可以免费刷新x次
  private int mysteryStoreFreeRefreshCnt; //神秘商人可以免费刷新x次
  private int bmstoreFreeRefreshCnt; //黑市商人可以免费刷新x次


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void ExtraInitAfterLoad(IPrivilegeConfigSourcer<StorePrivilegeNames> cfgHelper) {
		Throwable cause = null;
		try {
			ExtraInitAfterLoad(StorePrivilegeNames.class, cfgHelper);
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