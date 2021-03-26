package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程同步之顺序控制-wait->nofity版
 * 要求:先打印2,后打印1
 * Created by Saber on 2021/3/24 11:49
 */
@Slf4j
public class ThreadSyncOrderWaitNofity {

	static final Object lock = new Object();
	static boolean has2runed = false;

	public static void main(String[] args) throws InterruptedException {

		syncWaitNorify();

		Thread.sleep(2000);
		log.debug("-------- ReentrantLock版   -----------");
		reentrantLock();

		Thread.sleep(2000);
		log.debug("-------- LockSupport版   -----------");
		lockSupportPark();
	}

	private static void syncWaitNorify() {
		new Thread(()->{
			synchronized (lock){
				while (!has2runed){
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				log.debug("1");

			}
		},"t1").start();

		new Thread(()->{
			synchronized (lock){
				log.debug("2");
				has2runed=true;
				lock.notify();
			}
		},"t2").start();
	}

	public static void reentrantLock(){
		ReentrantLock lock = new ReentrantLock();
		Condition condition = lock.newCondition();
		AtomicBoolean t2runned = new AtomicBoolean(false);

		new Thread(()->{
			lock.lock();
			try {
				while (!t2runned.get()){
					try {
						condition.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					log.debug("1");
				}
			}finally {
				lock.unlock();
			}
		},"t1").start();

		new Thread(()->{
			lock.lock();
			try {
				log.debug("2");
				t2runned.set(true);
				condition.signal();
			}finally {
				lock.unlock();
			}
		},"t2").start();
	}

	public static void lockSupportPark(){

		Thread t1 = new Thread(() -> {
			LockSupport.park();
			log.debug("1");
		}, "t1");
		t1.start();

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(()->{
			log.debug("2");

			LockSupport.unpark(t1);
		},"t1").start();
	}
}
