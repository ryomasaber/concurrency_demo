package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Saber on 2021/3/24 17:20
 */
@Slf4j
public class JMMVolatile {

	static boolean run = true;

	static volatile boolean run2 = true;

	static boolean run3 = true;
	static final Object lock = new Object();

	public static void main(String[] args) throws InterruptedException {

		new Thread(()->{
			while (run){

				//当循环体内为空的时候 t1 线程不会停止

				//当循环体内不为空的桑场 t1 线程会停止

				//情况1.仅睡眠  也会导致停止
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

				//情况2. println()方法内部有同步 synchronized
//				System.out.println("run = " + run);

				//情况3. debug()方法内部有同步 synchronized
//				log.debug("run:{}",1);
			}
			log.debug("t1 stop succ");
		},"t1").start();

		new Thread(()->{
			while (run2){

			}
			log.debug("t2 stop succ");
		},"t2").start();

		new Thread(()->{
			while (true){
				synchronized (lock){
					if(!run3){
						break;
					}
				}
			}
			log.debug("t3 stop succ");
		},"t3").start();

		Thread.sleep(1000);
		log.debug("主线程停止 t1");
		run = false;
		log.debug("主线程停止 t2");
		run2 = false;
		log.debug("主线程停止 t3");
		run3 = false;
	}
}
