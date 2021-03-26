package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Saber on 2021/3/23 22:10
 */
@Slf4j
public class DeadLock {

	public static void main(String[] args) {

		Object A = new Object();
		Object B = new Object();

		Thread t1 = new Thread("t1") {
			@Override
			public void run() {
				synchronized (A){
					log.debug("t1 lock A success");
					synchronized (B){
						log.debug("t1 lock B success");
					}
					log.debug("t1 unlock B success");
				}
				log.debug("t1 unlock A success");
			}
		};

		Thread t2 = new Thread("t2") {
			@Override
			public void run() {
				synchronized (B){
					log.debug("t1 lock B success");
					synchronized (A){
						log.debug("t1 lock A success");
					}
					log.debug("t1 unlock A success");
				}
				log.debug("t1 unlock B success");
			}
		};

		t1.start();
		t2.start();
		log.debug("main start...");
	}
}
