package com.saber.Lock.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度线程池<br/>
 * 每周四 18:00:00 执行任务<br/>
 * Created by Saber on 2021/3/28 08:29
 */
@Slf4j
public class ScheduleExectorPoolTest {

	public static void main(String[] args) {

		ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(2);

		//获取当前时间
		LocalDateTime now = LocalDateTime.now();
		//获取本周四时间(18:00:00)
		LocalDateTime time = now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);
		log.debug("now:{}",now);
		log.debug("time:{}",time);
		//如果获取的周四已经过去,则获取下个周四
		if(now.isAfter(time)){
			time = time.plusWeeks(1);
			log.debug("next time:{}",time);
		}

		//计算当前时间到周四之间相差的毫秒数
		long initalDelay = Duration.between(now, time).toMillis();
		//计算一周时间的毫秒数
		long period = 1000 * 60 * 60 * 24 * 7;
		log.debug("距离周四相隔毫秒数:{}",initalDelay);

		pool.scheduleAtFixedRate(()->{
			log.debug("到了周四,开始执行任务了");
		},initalDelay,period, TimeUnit.MILLISECONDS);
	}

}
