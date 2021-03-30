package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * 线程排序
 * <p>
 *     有A,B,C三个线程,
 *     1.如何保证三个线程同时执行?
 *     2.如何保证三个线程顺序执行?
 *     3.如何保证三个线程有序交错运行?
 * </p>
 * Created by Saber on 2021/3/30 15:23
 */
@Slf4j
public class ThreadOrderTest {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("---- ABC三个线程同时执行 ----");
		runSameTime();
		Thread.sleep(1000);
		System.out.println("---- ABC三个线程顺序执行 ----");
		runOrdered();
		System.out.println("---- ABC三个线程顺序交错执行 ----");
		run5abcOrdered();
	}

	/**
	 * ABC3个线程同时执行
	 */
	public static void runSameTime() throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(1);
		Thread a = new Thread(() -> {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.debug("a");
		}, "A");

		Thread b = new Thread(() -> {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.debug("b");
		}, "B");

		Thread c = new Thread(() -> {
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.debug("c");
		}, "C");

		a.start();
		b.start();
		c.start();

		cdl.countDown();

		a.join();
		b.join();
		c.join();
	}

	/**
	 * ABC三个线程按ABC的顺序执行
	 */
	public static void runOrdered() throws InterruptedException {
		CyclicBarrier cbA = new CyclicBarrier(2);
		CyclicBarrier cbB = new CyclicBarrier(2);
		CyclicBarrier cbC = new CyclicBarrier(3);

		int loopnumber = 3;

		Thread a = new Thread(() -> {
			for (int i = 0; i < loopnumber; i++) {
				log.debug("a");
				try {
					cbA.await();
					cbC.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		}, "A");

		Thread b = new Thread(() -> {
			for (int i = 0; i < loopnumber; i++) {
				try {
					cbA.await();

					log.debug("b");

					cbB.await();
					cbC.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		}, "B");

		Thread c = new Thread(() -> {
			for (int i = 0; i < loopnumber; i++) {
				try {
					cbB.await();
					log.debug("c");
					cbC.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		}, "C");

		a.start();
		b.start();
		c.start();

		a.join();
		b.join();
		c.join();
	}

	/**
	 * ABC三个线程按ABC的顺序交错执行
	 */
	public static void run5abcOrdered() throws InterruptedException {
		Semaphore sp1 = new Semaphore(1);
		Semaphore sp2 = new Semaphore(1);
		Semaphore sp3 = new Semaphore(1);

		int loopnumber = 5;

		sp2.acquire();
		sp3.acquire();

		Thread a = new Thread(()->{
			for (int i = 0; i < loopnumber; i++) {
				try {
					sp1.acquire();
					System.out.print("a");
					//释放线程B
					sp2.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		},"A");

		Thread b = new Thread(()->{
			for (int i = 0; i < loopnumber; i++) {
				try {
					sp2.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.print("b");
				//释放线程B
				sp3.release();
			}
		},"B");

		Thread c = new Thread(()->{
			for (int i = 0; i < loopnumber; i++) {
				try {
					sp3.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.print("c");
				//释放线程B
				sp1.release();
			}
		},"C");

		a.start();
		b.start();
		c.start();

		a.join();
		b.join();
		c.join();
	}

}
