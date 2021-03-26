package com.saber.Lock;

import com.saber.Main;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Saber on 2021/2/8
 */
public class SellingTickets_pool implements Runnable{

	static int count = 100;

	String name;

	CountDownLatch countDownLatch;

	public SellingTickets_pool(String name) {
		this.name = name;
	}

	public SellingTickets_pool(String name, CountDownLatch countDownLatch) {
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
					System.out.format("[%s]成功购买一张票,剩余票数为:%d,线程名:%s\n",name,count,Thread.currentThread().getName());
				}
				else{
					System.out.format("[%s]用户您好,今日票已售完,请您明日早点来呦,线程名:%s\n",name,Thread.currentThread().getName());
				}
			}


		}else {
			System.out.format("[%s]用户您好,今日票已售完,请您明日早点来呦\n",name);
		}

		countDownLatch.countDown();
	}


	public static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();

		Thread thread1 = new Thread(new SellingTickets_pool("用户1"));
		Thread thread2 = new Thread(new SellingTickets_pool("用户2"));
		Thread thread3 = new Thread(new SellingTickets_pool("用户3"));

//		thread1.start();
//		thread1.join(); //加上join()可以让线程顺序执行
//		thread2.start();
//		thread2.join();
//		thread3.start();
//		thread3.join();


//		while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive()){
//
//		}

		ExecutorService service = Executors.newScheduledThreadPool(4);

		int size = 105;
		CountDownLatch countDownLatch = new CountDownLatch(size-1);

		for (int i = 1; i < size; i++) {
			service.submit(new SellingTickets_pool("用户"+i,countDownLatch));
		}


		countDownLatch.await();

		long end = System.currentTimeMillis();

		System.out.println("测试结束,共耗时:"+(end-start));

		service.shutdown();
	}
}
