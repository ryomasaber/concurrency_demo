package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

/**
 * 活锁问题
 * Created by Saber on 2021/3/23 23:24
 */
@Slf4j
public class AliveLock {

	static volatile int count = 10;
	static final Object lock = new Object();

	public static void main(String[] args) {

		new Thread(() -> {
			//期望减到0,退出循环
			while (count > 0){
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count--;
				log.debug("count:{}",count);

			}
			log.debug("减到0了,退出");
		},"t1").start();

		new Thread(()->{
			//期望超过20,退出循环
			while (count < 20){
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count++;
				log.debug("count:{}",count);
			}
			log.debug("加到20了,退出");
		},"t2").start();
	}
}
