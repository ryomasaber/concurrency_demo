package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Saber on 2021/3/25 16:13
 */
@Slf4j
public class DatePaseSafeUnsafeThread {

	//循环次数
	static final int loopnumber=100;
	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static void main(String[] args) throws InterruptedException {

		log.warn("----------  {} -----------","unsafe method");
		unsafe();

		TimeUnit.SECONDS.sleep(2);
		log.warn("----------  {} -----------","safe method -> sync");
		safeBySync();

		TimeUnit.SECONDS.sleep(2);
		log.warn("----------  {} -----------","safe method -> SafeClass");
		safeBySafeClass();
	}


	/**
	 * 不安全的方法
	 */
	private static void unsafe() {
		long begin = System.nanoTime();

		List<Thread> list = new ArrayList<>();
		for (int i = 0; i < loopnumber; i++) {

			Thread t = new Thread(() -> {
				try {
					log.debug("{}", sdf.parse("1993-03-15"));
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			});
			list.add(t);
		}
		//线程启动
		list.forEach(thread -> thread.start());
		//等待所有线程执行完毕
		for (Thread thread : list) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long end = System.nanoTime();

		log.info("cost:{}ns",(end-begin)/1000_000);
	}

	/**
	 * 线程安全的方法-线程同步
	 */
	private static void safeBySync(){
		long begin = System.nanoTime();

		List<Thread> list = new ArrayList<>();

		for (int i = 0; i < loopnumber; i++) {
			Thread t = new Thread(() -> {
				synchronized (sdf) {
					try {
						log.debug("{}", sdf.parse("1993-03-15"));
					} catch (ParseException e) {
						log.error(e.getMessage());
					}
				}
			});

			list.add(t);
		}

		//线程启动
		list.forEach(thread -> thread.start());
		//等待所有线程执行完毕
		for (Thread thread : list) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long end = System.nanoTime();

		log.info("cost:{}ns",(end-begin)/1000_000);
	}

	/**
	 * 使用线程安全的类-对象不可变
	 */
	private static void safeBySafeClass() {

		long begin = System.nanoTime();

		List<Thread> list = new ArrayList<>();

		for (int i = 0; i < loopnumber; i++) {

			Thread t = new Thread(() -> {
				TemporalAccessor parse = df.parse("1993-03-15");
				log.debug("{}",parse);
			});

			list.add(t);
		}

		list.forEach(thread -> thread.start());
		list.forEach(t-> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		long end = System.nanoTime();
		log.info("cost:{}ns",(end-begin)/1000_000);
	}
}
