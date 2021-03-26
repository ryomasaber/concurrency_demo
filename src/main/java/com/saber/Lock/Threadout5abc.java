package com.saber.Lock;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程1输出 a 5次, 线程2输出 b 5次, 线程3输出 c 5次 Created by Saber on 2021/3/24 13:18
 */
public class Threadout5abc {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("\n-------	 等待通知	--------");
		waitNotify();

		Thread.sleep(2000);

		System.out.println("\n-------	 等待通知进阶版	--------");
		waitNotify2();

		Thread.sleep(2000);

		System.out.println("\n-------	 ReentrantLock	--------");
		reentrantLock();

		Thread.sleep(2000);

		System.out.println("\n-------	 ReentrantLock进阶版	--------");
		reentrantLock2();

		Thread.sleep(2000);

		System.out.println("\n-------	 LockSupport	--------");
		lockSupport();

		Thread.sleep(2000);

		System.out.println("\n-------	 LockSupport进阶版	--------");
		lockSupport2();

		Thread.sleep(2000);

		System.out.println("\n-------	 SynchronousQueue	--------");
		threadQueue();

	}

	//sync+wait/notify
	public static void waitNotify() throws InterruptedException {
		final Object lock = new Object();
		final int num = 5;
		AtomicInteger n = new AtomicInteger(1);

		Thread t1 = new Thread(() -> {
			for (int i = 0; i < num; i++) {
				synchronized (lock) {
					while (n.get() != 1) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("a");
					n.set(2);
					lock.notifyAll();
				}
			}
		}, "t1");

		Thread t2 = new Thread(() -> {
			for (int i = 0; i < num; i++) {
				synchronized (lock) {
					while (n.get() != 2) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("b");
					n.set(3);
					lock.notifyAll();
				}
			}

		}, "t2");

		Thread t3 = new Thread(() -> {
			for (int i = 0; i < num; i++) {
				synchronized (lock) {
					while (n.get() != 3) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					System.out.print("c");
					n.set(1);
					lock.notifyAll();
				}
			}
		}, "t3");

		t1.start();
		t2.start();
		t3.start();
	}

	//等待/通知 进阶版
	public static void waitNotify2() {
		WaitNofity wn = new WaitNofity(1, 5);

		new Thread(() -> {
			wn.print("a", 1, 2);
		}, "t2").start();

		new Thread(() -> {
			wn.print("b", 2, 3);
		}, "t3").start();

		new Thread(() -> {
			wn.print("c", 3, 1);
		}, "t1").start();

	}

	static Thread t1,t2,t3;
	//park/unpark
	public static void lockSupport() throws InterruptedException {
		final int num = 5;

		t1 = new Thread(() -> {
			for (int i = 0; i < num; i++) {
				LockSupport.park();
				System.out.print("a");
				LockSupport.unpark(t2);
			}
		}, "t1");

		t2 = new Thread(() -> {
			for (int i = 0; i < num; i++) {
				LockSupport.park();
				System.out.print("b");
				LockSupport.unpark(t3);
			}
		}, "t2");

		t3 = new Thread(() -> {
			for (int i = 0; i < num; i++) {
				LockSupport.park();
				System.out.print("c");
				LockSupport.unpark(t1);
			}
		}, "t3");

		t1.start();
		t2.start();
		t3.start();

		Thread.sleep(1000);
		//主线程唤醒t1
		LockSupport.unpark(t1);
	}

	//park/unpark进阶版
	public static void lockSupport2() throws InterruptedException {
		final int num = 5;
		ParkUnpark lock = new ParkUnpark(num);

		t1 = new Thread(() -> {
			lock.print("a", t2);
		}, "t1");

		t2 = new Thread(() -> {
			lock.print("b", t3);
		}, "t2");

		t3 = new Thread(() -> {
			lock.print("c", t1);
		}, "t3");

		t1.start();
		t2.start();
		t3.start();

		Thread.sleep(1000);
		//主线程唤醒t1
		LockSupport.unpark(t1);

	}

	//lock/unlock
	public static void reentrantLock() {
		ReentrantLock lock = new ReentrantLock();
		Condition a = lock.newCondition();
		Condition b = lock.newCondition();
		Condition c = lock.newCondition();
		final int num = 5;
		AtomicInteger n = new AtomicInteger(1);

		new Thread(() -> {
			lock.lock();
			try {
				for (int i = 0; i < num; i++) {
					while (n.get() != 1) {
						try {
							a.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("a");
					n.set(2);
					b.signal();
				}
			} finally {
				lock.unlock();
			}
		}, "t1").start();

		new Thread(() -> {
			lock.lock();
			try {
				for (int i = 0; i < num; i++) {
					while (n.get() != 2) {
						try {
							b.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("b");
					n.set(3);
					c.signal();
				}
			} finally {
				lock.unlock();
			}
		}, "t2").start();

		new Thread(() -> {
			lock.lock();
			try {
				for (int i = 0; i < num; i++) {
					while (n.get() != 3) {
						try {
							c.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("c");
					n.set(1);
					a.signal();
				}
			} finally {
				lock.unlock();
			}
		}, "t3").start();
	}

	//lock/unlock进阶版
	public static void reentrantLock2() throws InterruptedException {
		AwaitSignal lock = new AwaitSignal(5);
		Condition a = lock.newCondition();
		Condition b = lock.newCondition();
		Condition c = lock.newCondition();

		new Thread(() -> {
			lock.print("a", a, b);
		}, "t1").start();

		new Thread(() -> {
			lock.print("b", b, c);
		}, "t2").start();

		new Thread(() -> {
			lock.print("c", c, a);
		}, "t3").start();

		Thread.sleep(1000);
		lock.lock();
		try {
			//主线程唤起a
			a.signal();
		} finally {
			lock.unlock();
		}
	}

	//阻塞队列的方式
	public static void threadQueue() {
		SynchronousQueue<String> queue = new SynchronousQueue();
		final int num = 5;
		new Thread(()->{
			for (int i = 0; i < num; i++) {

				try {
					queue.put("a");
					queue.put("b");
					queue.put("c");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"put").start();

		new Thread(()->{
			for (int i = 0; i < num * 3; i++) {
				try {
					System.out.print(queue.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"get").start();

	}
}


class WaitNofity {
	//等待标记
	private int flag;
	//循环次数
	private int loopNumber;

	public WaitNofity(int initFalg, int loopNumber) {
		this.flag = initFalg;
		this.loopNumber = loopNumber;
	}

	/**
	 * 打印字符串
	 *
	 * @param text     字符串
	 * @param waitFlag 等待标记
	 * @param nextFlag 下一个等待标记
	 */
	public void print(String text, int waitFlag, int nextFlag) {
		for (int i = 0; i < loopNumber; i++) {
			synchronized (this) {
				while (flag != waitFlag) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.print(text);
				flag = nextFlag;
				this.notifyAll();
			}
		}
	}
}

class AwaitSignal extends ReentrantLock {
	//循环次数
	private int loopnumber;

	public AwaitSignal(int loopnumber) {
		this.loopnumber = loopnumber;
	}

	public void print(String text, Condition current, Condition next) {
		for (int i = 0; i < loopnumber; i++) {
			lock();
			try {
				current.await();
				System.out.print(text);
				next.signal();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.print(text);
			} finally {
				unlock();
			}
		}

	}

}

class ParkUnpark{
	private int loopnumber;

	public ParkUnpark(int loopnumber){
		this.loopnumber = loopnumber;
	}

	public void print(String text,Thread next){
		for (int i = 0; i < loopnumber; i++) {
			LockSupport.park();
			System.out.print(text);
			LockSupport.unpark(next);
		}
	}
}
