package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 外卖-烟 使用重入锁解决多线程并发问题
 * Created by Saber on 2021/3/24 11:13
 */
@Slf4j
public class FoodCigareeteLock {

	//房间
	static ReentrantLock ROOM = new ReentrantLock();
	//烟-等待室
	static Condition cigareeteRoom = ROOM.newCondition();
	//外卖-等待室
	static Condition foodRoom = ROOM.newCondition();
	//是否有烟
	static boolean hasCigareete = false;
	//是否有外卖
	static boolean hasFood = false;

	public static void main(String[] args) throws InterruptedException {

		new Thread(()->{
			ROOM.lock();
			try {
				log.debug("有烟没?:{}",hasCigareete);
				//没有烟.进入抽烟等待室
				while (!hasCigareete){
					try {
						log.debug("没烟,进入等待室休息一会");
						cigareeteRoom.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				log.debug("有烟了,开始干活...");
			}finally {
				ROOM.unlock();
			}
		},"小男").start();

		new Thread(()->{
			ROOM.lock();
			try {
				log.debug("有外卖吗?:{}",hasFood);
				//没有外卖,进入等待室等待
				while (!hasFood){
					try {
						log.debug("没有外卖,进入等待室休息一会");
						foodRoom.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				log.debug("有外卖了,开始干活...");
			}finally {
				ROOM.unlock();
			}
		},"小女").start();

		TimeUnit.SECONDS.sleep(1);

		new Thread(()->{
			ROOM.lock();
			try {
				log.debug("烟送到了");
				hasCigareete=true;
				cigareeteRoom.signal();
			}finally {
				ROOM.unlock();
			}
		},"送烟的").start();

		TimeUnit.SECONDS.sleep(1);

		new Thread(()->{
			ROOM.lock();
			try {
				log.debug("外卖送到了");
				hasFood = true;
				foodRoom.signal();
			}finally {
				ROOM.unlock();
			}
		},"送外卖的").start();
	}
}
