package com.rwbase.dao.assistant.cfg;

import java.util.HashMap;
import org.apache.commons.csv.CSVRecord;
import com.rw.config.ConfigMemHelper;
import com.rw.config.ConvertionUtil;

public class AssistantCfg
{
  public enum AssistantEventID{//key
    Invaild,
    Sign,
    GetItem,
    HeroAdvance,
    TakeUpEquip,
    DailyQuest,
    UpdateSkill,
    HeroLevelUp,
    GotoCopy,
    FixEquipUp,
    FixEquipAdvance,
    GotoTaoist,
  }
  private static class EventIDParser implements ConvertionUtil.Parser<AssistantEventID> {
		@Override
		public AssistantEventID parse(String str) {
			return AssistantEventID.valueOf(str);
		}
	}
  private static ConvertionUtil.Parser<AssistantEventID> EnumParser_EventID = new EventIDParser();
  private final AssistantEventID EventIDField; //key
  private final int priority; //优先级
  private final String OpenCondition; //开启条件
  private final String CloseCondition; //关闭条件

  public AssistantEventID getEventIDField() {
    return EventIDField;
  }
  public int getPriority() {
    return priority;
  }
  public String getOpenCondition() {
    return OpenCondition;
  }
  public String getCloseCondition() {
    return CloseCondition;
  }
  protected AssistantCfg(CSVRecord csvRecord) {
    int columnSize = csvRecord.size();
    EventIDField = ConvertionUtil.parse(csvRecord, 0, columnSize, null,EnumParser_EventID);
    priority = ConvertionUtil.parseint(csvRecord, 2, columnSize);
    OpenCondition = ConvertionUtil.parseString(csvRecord, 6, columnSize);
    CloseCondition = ConvertionUtil.parseString(csvRecord, 7, columnSize);
  }
	private static class ConfigHelper extends ConfigMemHelper<AssistantEventID,AssistantCfg>{
		@Override
		protected HashMap<AssistantEventID,AssistantCfg> initDict() {
			HashMap<AssistantEventID,AssistantCfg> dict = ConvertionUtil.LoadConfig(AssistantCfg.class.getClassLoader(), "config/Assistant/Assistant.csv" ,
					new ConvertionUtil.Constructor<AssistantEventID,AssistantCfg>() {
						@Override
						public AssistantCfg build(AssistantEventID key, CSVRecord rec) {
							return new AssistantCfg(rec);
						}
					}, EnumParser_EventID);
			return dict;
		}
	}
	private static ConfigHelper helper;
	public static ConfigMemHelper<AssistantEventID,AssistantCfg> LoadCfg(){
		if (helper == null) helper = new ConfigHelper();
		return helper;
	}
}