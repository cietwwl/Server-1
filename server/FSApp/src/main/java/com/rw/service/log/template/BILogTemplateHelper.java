package com.rw.service.log.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.task.DailyActivityCfgDAO;
import com.rwbase.dao.task.pojo.DailyActivityCfg;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;

public class BILogTemplateHelper {

	
	final static private String NAME_SPLIT = "\\$\\|";
	
	public static Set<String> getInfoNameSet(String template){
		
		Set<String> nameSet = new HashSet<String>();
		String[] infoNameSplit = template.split(NAME_SPLIT);
		for (String infoName : infoNameSplit) {
			infoName = StringUtils.substringAfter(infoName, "$");
			infoName = StringUtils.substringBefore(infoName, "$");
			if(StringUtils.isNotBlank(infoName)){
				nameSet.add(infoName);
			}
		}
		return nameSet;
	}
	
	private static Map<String,String> docToTemplateMap = new HashMap<String,String>();
	
	private static List<String> repalceOrderList = new ArrayList<String>();
	
	static {
		addTemplateToken("物品code1：数量1&物品code2：数量2: 物品code3：数量3…", "$itemList$");
		addTemplateToken("activity_time:活动时长（单位秒）(整数)", "activity_time:$activityTime$");
		addTemplateToken("online_time:本次在线时长（单位秒）(整数)", "online_time:$onlineTime$");
		addTemplateToken("游戏币新增消耗数量（新增为正数，消耗为负数）", "$coinChanged$");
		addTemplateToken("sp_case:普通驻留", "sp_case:$sp_case$");
		addTemplateToken("nm_case:精英驻留", "nm_case:$nm_case$");
		addTemplateToken("赠送充值币新增消耗数量（新增为正数，消耗为负数）", "$giftGoldChanged$");
		addTemplateToken("wifi（2g/3g/4g/wifi等）", "$loginNetType$");
		addTemplateToken("wifi(2g/3g/4g/wifi等)", "$loginNetType$");
		addTemplateToken("4（4=安卓/5=ios/7=wm）", "$loginClientPlatForm$");
		addTemplateToken("参考操作码对照表对应统计信息", "");
		addTemplateToken("4=安卓/5=ios/7=wm", "$loginClientPlatForm$");
		addTemplateToken("1=普通关卡/2=精英关卡", "$copyLevel$");
		addTemplateToken("2g/3g/4g/wifi等", "$loginNetType$");
		addTemplateToken("变动后游戏币个人持有量", "$coinRemain$");
		addTemplateToken("变动后赠送充值货币个人持有量", "$giftGoldRemain$");
		addTemplateToken("last_fight_power", "$fightbeforelevelup$");
		addTemplateToken("ip地址，不包含端口", "$loginClientIp$");
		addTemplateToken("ip地址（不包含端口）", "$loginClientIp$");
		addTemplateToken("失败为0，成功为1", "$result$");
		addTemplateToken("0=失败/1=成功", "$result$");		
		addTemplateToken("场景id/地图id", "$scenceId$");
		addTemplateToken("当前游戏客户端版本", "$clientVersion$");
		addTemplateToken("操作系统版本号", "$loginsystemVersion$");
		addTemplateToken("注册渠道ID_UID", "$regChannelId_uid$");
		addTemplateToken("用户统计信息", "$statInfo$");
		addTemplateToken("副本统计信息", "$copyInfo$");
		addTemplateToken("活动统计信息", "$activityInfo$");
		addTemplateToken("任务统计信息", "$taskInfo$");
		addTemplateToken("fight_time", "fight_time:$fightTime$");
		addTemplateToken("1=主线/2=支线", "$biTaskType$");
		addTemplateToken("参考操作码对照表", "$optype$");
		addTemplateToken("区UID创建时间", "$userCreatedTime$");
		addTemplateToken("账号唯一识别符", "$regChannelId_uid$");
		addTemplateToken("用户注册子渠道", "$regSubChannelId$");
		addTemplateToken("同时在线用户数", "$onlineCount$");
		addTemplateToken("UID创建时间", "$userCreatedTime$");
		addTemplateToken("用户登录区ID", "$loginZoneId$");
		addTemplateToken("日志的触发时间", "$logTime$");
		addTemplateToken("一级变动原因", "$ItemChangedEventType_1$");
		addTemplateToken("二级变动原因", "$ItemChangedEventType_2$");
		addTemplateToken("用户登录子渠道", "$loginSubChannelId$");
		addTemplateToken("用户登录渠道", "$loginChannelId$");
		addTemplateToken("角色创建时间", "$roleCreatedTime$");
		addTemplateToken("注册子渠道ID", "$regSubChannelId$");
		addTemplateToken("注册渠道ID", "$regSubChannelId$");
		addTemplateToken("广告短链接id", "$loginadLinkId$");				
		addTemplateToken("用户VIP等级", "$vip$");
		addTemplateToken("用户角色等级", "$level$");
		addTemplateToken("升级前等级", "$levelBeforeUp$");
		addTemplateToken("客户端版本", "$clientVersion$");
		addTemplateToken("手机运营商", "$phoneOp$");
		addTemplateToken("总账号统计", "$totalAccount$");
		addTemplateToken("IMEI信息", "$loginImei$");
		addTemplateToken("关卡code", "$copyId$");		
		addTemplateToken("赠送充值币余额", "$zoneGiftGoldRemain$");
		addTemplateToken("付费充值币余额", "$zoneChargeGoldRemain$");
		
		addTemplateToken("游戏币余额", "$zoneCoinRemain$");
		addTemplateToken("活动入口", "$activityEntry$");

		addTemplateToken("活动code", "$activityCode$");
		addTemplateToken("局次code", "$GamesCode$");		
		addTemplateToken("关卡状态", "$copyStatus$");		
		addTemplateToken("打印时间", "$logTime$");
		addTemplateToken("任务入口", "");
		addTemplateToken("关卡入口", "$copyEntrance$");
		addTemplateToken("角色统计", "$levelCount$");
		addTemplateToken("手机型号", "$loginPhoneType$");
		addTemplateToken("mac地址", "$loginImac$");
		addTemplateToken("任务ID", "$taskId$");
		
		addTemplateToken("帮派id", "$factionId$");
		addTemplateToken("sdk版本", "$loginsdkVersion$");
		addTemplateToken("sdk_id", "");
		addTemplateToken("map_id", "$mapid$");
		addTemplateToken("此处为空", "");
		addTemplateToken("此处留空", "");
		addTemplateToken("职业等级", "");	
		addTemplateToken("进程id", "$threadId$");		
		addTemplateToken("用户战力", "$fighting$");		
		addTemplateToken("角色ID", "$userId$");
		addTemplateToken("职业ID", "$careerType$");
		addTemplateToken("操作码", "$operationCode$");		
		addTemplateToken("UID", "$userId$");
		addTemplateToken("职级", "");
		addTemplateToken("为空", "");
		addTemplateToken("空", "");
		
	}
	
	private static void addTemplateToken(String token, String template){
		docToTemplateMap.put(token, template);
		repalceOrderList.add(token);
	}
	
	//文档到可处理template的转换
	public static String toTemplate(String original){
		String template = original;
		for (String nameTmp : repalceOrderList) {
			template = template.replace(nameTmp, docToTemplateMap.get(nameTmp));
		}
		
//		System.out.println(template);
		return template;
		
	}
	
	/**将传来的奖励数据统一转为银汉可识别的格式
	 * @param class1 */
	public static String getString(List<BilogItemInfo> list){
		StringBuilder str = new StringBuilder();
		str.append("");
		if(list == null){
			return str.toString();
		}
		int num = list.size();
		for(BilogItemInfo subitem : list){
			str.append(subitem.getItemId()).append("@").append(subitem.getNum());
			if(--num >0){
				str.append("&");
			}			
		}		
		return str.toString();		
	}
	
	/**
	 * 根据传进来的日常任务id返回银汉序列的活动code
	 * */
	public static BIActivityCode getByDailyTaskId(int taskid){
		BIActivityCode code = null;
		DailyActivityCfgEntity entity = DailyActivityCfgDAO.getInstance().getCfgEntity(taskid);
		DailyActivityCfg cfg = entity.getCfg();
		int type = cfg.getTaskType();
		
		switch (type) {
		case DailyActivityType.FS_Lanch:
			code = BIActivityCode.DAILY_TASK_FS_Lanch;
			break;
		case DailyActivityType.FS_Dinner:
			code = BIActivityCode.DAILY_TASK_FS_Dinner;
			break;
		case DailyActivityType.FS_Supper:
			code = BIActivityCode.DAILY_TASK_FS_Supper;
			break;
		case DailyActivityType.Dup_Normal:
			code = BIActivityCode.DAILY_TASK_Dup_Normal;
			break;	
		case DailyActivityType.Dup_Elite:
			code = BIActivityCode.DAILY_TASK_Dup_Elite;
			break;
		case DailyActivityType.Gold_Point:
			code = BIActivityCode.DAILY_TASK_Gold_Point;
			break;
		case DailyActivityType.Hero_SkillUpgrade:
			code = BIActivityCode.DAILY_TASK_Hero_SkillUpgrade;
			break;
		case DailyActivityType.Altar:
			code = BIActivityCode.DAILY_TASK_Altar;
			break;	
			
		case DailyActivityType.Hero_Strength:
			code = BIActivityCode.DAILY_TASK_Hero_Strength;
			break;
		case DailyActivityType.Arena:
			code = BIActivityCode.DAILY_TASK_Arena;
			break;
		case DailyActivityType.Trial_JBZD:
			code = BIActivityCode.DAILY_TASK_Trial_JBZD;
			break;
		case DailyActivityType.Trial2:
			code = BIActivityCode.DAILY_TASK_Trial2;
			break;	
		case DailyActivityType.Tower:
			code = BIActivityCode.DAILY_TASK_Tower;
			break;
		case DailyActivityType.Power:
			code = BIActivityCode.DAILY_TASK_Power;
			break;
		case DailyActivityType.UNENDINGWAR:
			code = BIActivityCode.DAILY_TASK_UNENDINGWAR;
			break;
		case DailyActivityType.CONST:
			code = BIActivityCode.DAILY_TASK_CONST;
			break;		
		case DailyActivityType.Trial_LQSG:
			code = BIActivityCode.DAILY_TASK_Trial_LQSG;
			break;
		case DailyActivityType.HSQJ:
			code = BIActivityCode.DAILY_TASK_HSQJ;
			break;
		case DailyActivityType.LOGIN_DAY:
			code = BIActivityCode.DAILY_TASK_LOGIN_DAY;
			break;
		case DailyActivityType.BREAKFAST:
			code = BIActivityCode.DAILY_TASK_BREAKFAST;
			break;					
		default:
			break;
		}		
		return code;
	}
	
	/**根据id和类型返回地图波数*/
	public static int getTimes(int levelId,int type){
		CopyCfg cfg = CopyCfgDAO.getInstance().getCfgById(levelId+"");
		return cfg.getSubtype();
		
	}
	
	
	public static void main(String[] args) {
		String zoneReg = "打印时间|core_gamesvr|用户登录区ID|日志的触发时间|gamesvr_reg|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4（4=安卓/5=ios/7=wm）|区UID创建时间|用户VIP等级|手机运营商|wifi（2g/3g/4g/wifi等）|手机型号|客户端版本|ip地址，不包含端口|IMEI信息|mac地址|此处为空|此处为空|gamesvr_reg|此处为空|1|此处为空";
		//区账号登录
		String zoneLogin = "打印时间|core_gamesvr|用户登录区ID|日志的触发时间|gamesvr_act|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4（4=安卓/5=ios/7=wm）|区UID创建时间|用户VIP等级|手机运营商|wifi(2g/3g/4g/wifi等)|手机型号|客户端版本|ip地址，不包含端口|IMEI信息|mac地址|此处为空|此处为空|gamesvr_login|此处为空|失败为0，成功为1|此处为空";
		//区账号登出
		String zoneLogout = "打印时间|core_gamesvr|用户登录区ID|日志的触发时间|gamesvr_act|用户登录区ID|注册渠道ID_UID|UID|用户注册子渠道|用户登录渠道|4（4=安卓/5=ios/7=wm）|区UID创建时间|用户VIP等级|手机运营商|wifi(2g/3g/4g/wifi等)|手机型号|客户端版本|ip地址，不包含端口|IMEI信息|mac地址|此处为空|此处为空|gamesvr_logout|此处为空|online_time";
		//角色注册
		String roleCreated = "打印时间|core_role|用户登录区ID|日志的触发时间|role_reg|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职业等级|此处为空|此处为空|role_reg|此处为空|1|此处为空";
		//在线用户数
		String onlineCount = "打印时间|core_stat_1|用户登录区ID|日志的触发时间|stat_role_online|用户登录区ID|注册渠道ID|同时在线用户数|4=安卓/5=ios/7=wm";
		//关卡开始
		String copyBegin = "打印时间|core_case|用户登录区ID|日志的触发时间|case|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职级|关卡入口|局次code|关卡code|此处为空|case_start|此处为空|0=失败/1=成功|为空";
		//关卡结束
		String copyEnd = "打印时间|core_case|用户登录区ID|日志的触发时间|case|用户登录区ID|注册渠道ID_UID|UID|角色ID|用户注册子渠道|用户登录渠道|4=安卓/5=ios/7=wm|UID创建时间|角色创建时间|当前游戏客户端版本|用户VIP等级|用户角色等级|用户战力|职业ID|职级|关卡入口|局次code|关卡code|此处为空|case_finish|此处为空|0=失败/1=成功|fight_time";
		
		BILogTemplateHelper.toTemplate(copyEnd);
	}
}
