package com.rwbase.dao.battletower.pojo.cfg;

import java.util.HashMap;
import org.apache.commons.csv.CSVRecord;
import com.rw.config.ConfigMemHelper;
import com.rw.config.ConvertionUtil;

public class BattleTowerCommonCfg {
	private final String Name; // 组Id
	private final String Value; // 副本Id

	public String getName() {
		return Name;
	}

	public String getValue() {
		return Value;
	}

	protected BattleTowerCommonCfg(CSVRecord csvRecord) {
		int columnSize = csvRecord.size();
		Name = ConvertionUtil.parseString(csvRecord, 0, columnSize);
		Value = ConvertionUtil.parseString(csvRecord, 2, columnSize);
	}

	private static class ConfigHelper extends ConfigMemHelper<String, BattleTowerCommonCfg> {
		@Override
		protected HashMap<String, BattleTowerCommonCfg> initDict() {
			HashMap<String, BattleTowerCommonCfg> dict = ConvertionUtil.LoadConfig(BattleTowerCommonCfg.class.getClassLoader(), "config/battleTower/BattleTowerCommonCfg.csv",
					new ConvertionUtil.Constructor<String, BattleTowerCommonCfg>() {
						@Override
						public BattleTowerCommonCfg build(String key, CSVRecord rec) {
							return new BattleTowerCommonCfg(rec);
						}
					}, ConvertionUtil.StringKeyParser);
			return dict;
		}
	}

	private static ConfigHelper helper;

	public static ConfigMemHelper<String, BattleTowerCommonCfg> LoadCfg() {
		if (helper == null)
			helper = new ConfigHelper();
		return helper;
	}
}