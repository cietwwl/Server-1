package com.rwbase.dao.battletower.pojo.cfg.dao;

import com.log.GameLog;
import com.rw.config.ConfigMap;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerCommonCfg;

public class BattleTowerCommonCfgConfig extends ConfigMap<String, BattleTowerCommonCfg> {
	private static BattleTowerCommonCfgConfig instance;

	public static BattleTowerCommonCfgConfig getInstance() {
		if (instance == null)
			instance = new BattleTowerCommonCfgConfig();
		return instance;
	}

	protected BattleTowerCommonCfgConfig() {
		super();
		helper = BattleTowerCommonCfg.LoadCfg();
		ExtraInit();
	}

	public void loadIndex() {
	}

	public void unloadIndex() {
	}

	private String openboxtip;
	private int usekeycount;

	public String getOpenboxtip() {
		return openboxtip;
	}

	public int getUsekeycount() {
		return usekeycount;
	}

	private void ExtraInit() {
		BattleTowerCommonCfg boxtipCfg = this.getCfgById("OpenBoxTip");
		String boxtip = "点击宝匣，一次性可使用{0}把钥匙，则全部使用!";
		if (boxtipCfg != null)
			boxtip = boxtipCfg.getValue();
		openboxtip = boxtip;

		BattleTowerCommonCfg useKeyCountCfg = this.getCfgById("UseKeyCount");
		int useKeyCount = 10;
		if (useKeyCountCfg != null) {
			try {
				useKeyCount = Integer.parseInt(useKeyCountCfg.getValue());
			} catch (Exception ex) {
				GameLog.error("封神台", "配置文件BattleTowerCommonCfg.csv", "UseKeyCount配置不正确");
			}
		}
		usekeycount = useKeyCount;

	}
}
