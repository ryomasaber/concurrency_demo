package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static java.lang.Thread.sleep;

/**
 * Created by Saber on 2021/3/23 19:27
 */
@Slf4j
public class ThreadParkUnPark {

	public static void main(String[] args) throws InterruptedException {

		log.debug("----------  先park 后unpark  --------------");
		//先park 后unpark
		method(2,1);

		sleep(1000);

		log.debug("----------  先unpark 后park  --------------");
		//先unpark,后park
		method(1,2);
	}

	private static void method(int mainWait, int threadWait) throws InterruptedException {
		Thread t1 = new Thread("t1"){
			@Override
			public void run() {
				log.debug("start....");
				try {
					sleep(threadWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.debug("park...");
				LockSupport.park();
				log.debug("resume....");
			}
		};
		t1.start();

		sleep(mainWait);
		log.debug("main unpack t1");
		LockSupport.unpark(t1);
	}

}
