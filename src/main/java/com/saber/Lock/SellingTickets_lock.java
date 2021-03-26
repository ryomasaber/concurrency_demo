package com.saber.Lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Saber on 2021/2/7
 */
public class SellingTickets_lock implements Runnable{

	static int count = 100;

	String name;

	CountDownLatch countDownLatch;

	private static ReentrantLock lock = new ReentrantLock();

	public SellingTickets_lock(String name) {
		this.name = name;
	}

	public SellingTickets_lock(String name, CountDownLatch countDownLatch) {
		this.name = name;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		lock.lock();
		try {
			if(count > 0)  {
				count--;
				System.out.format("[%s]成功购买一张票,剩余票数为:%d\n",name,count);

			}
			else{
				System.out.format("[%s]用户您好,今日票已售完,请您明日早点来呦\n",name);
			}
		} finally {
			lock.unlock();

			countDownLatch.countDown();
		}
	}


	public static void main(String[] args) throws InterruptedException {

		long start = System.currentTimeMillis();

		Thread thread1 = new Thread(new SellingTickets_lock("用户1"));
		Thread thread2 = new Thread(new SellingTickets_lock("用户2"));
		Thread thread3 = new Thread(new SellingTickets_lock("用户3"));

//		thread1.start();
//		thread1.join(); //加上join()可以让线程顺序执行
//		thread2.start();
//		thread2.join();
//		thread3.start();
//		thread3.join();


//		while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive()){
//
//		}

		int size = 105;
		CountDownLatch countDownLatch = new CountDownLatch(size-1);

		for (int i = 1; i < size; i++) {
			Thread thread = new Thread(new SellingTickets_lock("用户"+i,countDownLatch));
			thread.start();
		}

		countDownLatch.await();

		long end = System.currentTimeMillis();

		System.out.println("测试结束,共耗时:"+(end-start));
	}
}
