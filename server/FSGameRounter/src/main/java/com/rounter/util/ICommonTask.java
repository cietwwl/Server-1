package com.rounter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 定义的一个周期性的任务接口  注意所有实现类都要放在这个包内
 * 实现类上要有组件的注解@Component
 * 需要写在{@link #doWork()}实现上加@Scheduled注解
 * cron 表达式及含义如下：
 * 
 * CRON表达式    含义 
 * 0 0 12 * * ?"    每天中午十二点触发 
 * 0 15 10 ? * *"    每天早上10：15触发 
 * 0 15 10 * * ?"    每天早上10：15触发 
 * 0 15 10 * * ? *"    每天早上10：15触发 
 * 0 15 10 * * ? 2005"    2005年的每天早上10：15触发 
 * 0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发 
 * 0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发 
 * 0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 
 * 0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发 
 * 0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发 
 * 0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发 
 * 可参看demo:{@link TestTask}
 * </pre>
 * @author Alex
 *
 * 2016年12月10日 下午7:52:44
 */
public interface ICommonTask {

	Logger logger = LoggerFactory.getLogger("mainLogger");
	
	void doWork();
}
