package com.saber.Lock;

import com.saber.Main;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Saber on 2021/2/8
 */
public class SellingTickets_sync implements Runnable{

	static int count = 100;

	String name;

	CountDownLatch countDownLatch;

	public SellingTickets_sync(String name) {
		this.name = name;
	}

	public SellingTickets_sync(String name, CountDownLatch countDownLatch) {
		this.name = name;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {

		if(count>0){

			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			synchronized (Main.class) {
				if(count > 0)  {
					count--;
					System.out.format("[%s]成功购买一张票,剩余票数为:%d\n",name,count);
				}
				else{
					System.out.format("[%s]用户您好,今日票已售完,请您明日早点来呦\n",name);
				}
			}


		}else {
			System.out.format("[%s]用户您好,今日票已售完,请您明日早点来呦\n",name);
		}

		countDownLatch.countDown();
	}


	public static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();

		Thread thread1 = new Thread(new SellingTickets_sync("用户1"));
		Thread thread2 = new Thread(new SellingTickets_sync("用户2"));
		Thread thread3 = new Thread(new SellingTickets_sync("用户3"));

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
			Thread thread = new Thread(new SellingTickets_sync("用户"+i,countDownLatch));
			System.out.println("state.toString() = " + thread.getState());
			thread.start();
			System.out.println("state.toString() = " + thread.getState());
		}


		countDownLatch.await();

		long end = System.currentTimeMillis();

		System.out.println("测试结束,共耗时:"+(end-start));
	}
}
