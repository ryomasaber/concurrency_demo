package com.saber.Lock.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by Saber on 2021/3/28 09:41
 */
@Slf4j
public class MyLock implements Lock {

	private MySync sync = new MySync();

	/**
	 * 加锁(不成功会进入等待队列)
	 */
	@Override
	public void lock() {
		sync.acquire(1);
	}

	/**
	 * 加锁,可打断
	 * @throws InterruptedException
	 */
	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);
	}

	/**
	 * 尝试释放锁,仅尝试一次
	 * @return
	 */
	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	/**
	 * 带尝试时间的释放锁
	 * @param time
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireNanos(1, unit.toNanos(time));
	}

	/**
	 * 释放锁
	 */
	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition newCondition() {
		return sync.newCondtion();
	}

	//独占锁-同步器类
	class MySync extends AbstractQueuedSynchronizer{

		//获取锁
		@Override
		protected boolean tryAcquire(int arg) {

			if(compareAndSetState(0, 1)){
				//设置当前独占锁主人为当前线程
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}

		//释放锁
		@Override
		protected boolean tryRelease(int arg) {
			//释放独占锁
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}

		//是否持有独占锁
		@Override
		protected boolean isHeldExclusively() {
			return getState() == 1;
		}

		public Condition newCondtion(){
			return new ConditionObject();
		}

	}


	public static void main(String[] args) {
		Lock lock = new MyLock();

		new Thread(()->{
			lock.lock();
			log.debug("locked");

			try {
				log.debug("running...");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}finally {
				log.debug("unlocked");
				lock.unlock();
			}
		},"t1").start();

		new Thread(()->{
			lock.lock();
			log.debug("locked");

			try {
				log.debug("running...");
			}finally {
				log.debug("unlocked");
				lock.unlock();
			}
		},"t2").start();
	}

}
