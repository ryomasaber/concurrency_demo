package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁 Created by Saber on 2021/3/24 09:17
 */
@Slf4j
public class ThreadReentrantLock {

	static ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) throws InterruptedException {
		//重入锁
//		main();

		//打断重入锁
//		main2();

		//尝试获取锁(无等待时间)
//		main3();

		//尝试获取锁(有等待时间)
		main4();
	}

	private static void main() {
		lock.lock();
		try {
			log.debug("main 获取锁");
			m1();
		} finally {
			lock.unlock();
			log.debug("main 释放锁");
		}
	}

	private static void main2() throws InterruptedException {

		Thread t1 = new Thread(()->{
			m3();
		},"t1");

		lock.lock();
		log.debug("main2 获取锁");

		t1.start();
		Thread.sleep(1000);
		log.debug("main2 打断t1的锁");
		//打断t1的锁
		t1.interrupt(); //打断m3的锁 使m3方法提前终止
		lock.unlock(); //释放主线程的锁
	}

	private static void main3() throws InterruptedException {

		Thread t1 = new Thread(()->{
			tryLock();
		},"t1");

		lock.lock();
		try {
			log.debug("main3 获取锁");

			t1.start();
			Thread.sleep(1000);
		} finally {
			lock.unlock();//m3 正常释放锁
			log.debug("main3释放锁");
		}

	}

	private static void main4() throws InterruptedException {

		Thread t1 = new Thread(()->{
			tryLock(2);
		},"t1");

		lock.lock();
		try {
			log.debug("main4获取锁");

			t1.start();
			Thread.sleep(3000);
		} finally {
			lock.unlock();//main4 正常释放锁
			log.debug("main4释放锁");
		}

	}


	public static void m1() {

		lock.lock();
		try {
			log.debug("m1 获得锁");
			m2();
		} finally {
			lock.unlock();
			log.debug("m1 释放锁");
		}
	}

	public static void m2() {

		lock.lock();
		try {
			log.debug("m2 获得锁");
		} finally {
			lock.unlock();
			log.debug("m2 释放锁");
		}
	}

	public static void m3() {
		try {
			log.debug("尝试获取锁");

			lock.lockInterruptibly();

		} catch (InterruptedException e) {
			e.printStackTrace();
			log.debug("m3 锁被打断,返回");
			return;
		}

		try {
			log.debug("m3 获得锁");
		} finally {
			lock.unlock();
			log.debug("m3 释放锁");
		}
	}

	public static void tryLock(){
		if(!lock.tryLock()){
			log.debug("未获得锁,返回");
			return;
		}
		try {
			log.debug("获得锁");
		}finally {
			lock.unlock();
			log.debug("释放锁");
		}
	}

	private static void tryLock(int timeout) {
		try {
			log.debug("尝试获得锁");
			if(!lock.tryLock(timeout, TimeUnit.SECONDS)){
				log.debug("等待{}秒后未获得锁,返回",timeout);
				return;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.debug("被打断 未获得锁,返回");
			return;
		}
		try {
			log.debug("获得锁");
		}finally {
			lock.unlock();
			log.debug("释放锁");
		}
	}
}
