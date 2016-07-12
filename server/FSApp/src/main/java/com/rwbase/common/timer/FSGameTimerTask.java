package com.rwbase.common.timer;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.core.FSGameTimeSignal;

/**
 * 
 * 时效任务接口规范
 * 
 * @author P.C.
 *
 */
public interface FSGameTimerTask {

	/**
	 * 
	 * 时效任务的名称。没实际作用，只是作为跟踪辨别只用。
	 * 
	 * @return 时效任务的名称
	 */
	public String getName();
	
	/**
	 * 
	 * 到达预设的时间，收到定时器发来的定时信号，在本方法实现业务逻辑
	 * 
	 * @param timeSignal 报时的信号
	 * @return 业务实现者根据自身需要返回的结果。
	 * @throws Exception
	 */
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception;
	
	/**
	 * <pre>
	 * 当一轮逻辑处理完成后的通知，其实就是线程池执行完{@linkplain #onTimeSignal(FSGameTimeSignal)}后所回调的方法。
	 * 本方法未必有实际的业务作用，或可以用于对任务执行的时间和结果进行检测
	 * 可以通过调用{@linkplain FSGameTimeSignal#get()}获取上次执行返回的结果。
	 * </pre>
	 * @param timeSignal
	 */
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal);
	
	/**
	 * 执行线程池拒绝执行，此异常一般不会出现，除非执行线程池出现严重问题
	 * @param e 拒绝执行异常
	 */
	public void rejected(RejectedExecutionException e);
	
	/**
	 * 
	 * 是否继续
	 * 
	 * @return
	 */
	public boolean isContinue();
	
	/**
	 * 
	 * <pre>
	 * 获取时效任务的子时效任务
	 * 可以用于以下业务模型：
	 * 主时效任务是一个每天定时任务，例如：每天21点执行；当时间到后，激发一个秒时效任务来处理具体的业务逻辑；
	 * 通过timer统一提交，逻辑就可以不需要关心在onTimeSignal里面提交的操作
	 * </pre>
	 * 
	 * @return
	 */
	public List<FSGameTimerTaskSubmitInfo> getChildTasks();
	
}
