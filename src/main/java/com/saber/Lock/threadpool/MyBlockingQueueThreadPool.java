package com.saber.Lock.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义实现阻塞队列的线程池
 * -- 问题: 阻塞队列满了后没有实现任务提交的拒绝策略
 * Created by Saber on 2021/3/26 08:59
 */
@Slf4j
public class MyBlockingQueueThreadPool {

	public static void main(String[] args) {

		ThreadPool pool = new ThreadPool(4, 200, TimeUnit.MILLISECONDS,20,
				((queue, task) -> {
					//1.使用当前线程运行被拒绝的任务
//					task.run();
					//2.死等
//					queue.put(task);
					//3.丢弃
					log.info("丢弃:{}",task);
					//4.抛异常 后面的任务不会执行
					throw new RuntimeException("任务无法执行..."+task.hashCode());
				}));

		for (int i = 0; i < 30; i++) {
			String taskName = String.valueOf("t_"+i);
			int finalI = i;
			pool.execute(()->{
				try {
					Thread.sleep(new Random().nextInt(5000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.debug("print {}", finalI);
			},taskName,200,TimeUnit.MILLISECONDS);
		}
	}
}

@Slf4j
class ThreadPool{

	//任务队列
	private BlockingQueue<Runnable> taskQueue;
	//核心运行线程集合
	private HashSet<Worker> workers = new HashSet<>();
	//核心池大小
	private int coreSize;
	//超时时间
	private int timeout;
	//超时时间单位
	private TimeUnit timeUnit;
	//拒绝策略
	private RejectPolicy<Runnable> rejectPolicy;

	public ThreadPool(int coreSize, int timeout, TimeUnit timeUnit, int blockingCapcity,RejectPolicy<Runnable> rejectPolicy) {
		this.coreSize = coreSize;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.taskQueue = new BlockingQueue<>(blockingCapcity);
		this.rejectPolicy = rejectPolicy;
	}

	//执行任务
	public void execute(Runnable task){
		synchronized (workers) {
			String taskName = String.valueOf(new Random().nextInt(1000));
			//如果工作线程数小于核心线程数,则立即创建,并运行
			if(workers.size() < coreSize){
				Worker worker = new Worker(task,taskName);
				workers.add(worker);
				log.info("新增 worker...{}",task);
				worker.start();
			}else {
				//如果工作线程已满,则加入等待队列
				taskQueue.put(task);
			}
		}
	}

	//执行任务
	public void execute(Runnable task,String taskName,long timeout,TimeUnit timeUnit){
		synchronized (workers) {
			//如果工作线程数小于核心线程数,则立即创建,并运行
			if(workers.size() < coreSize){
				Worker worker = new Worker(task,taskName);
				workers.add(worker);
				log.info("新增 worker...{}",taskName);
				worker.start();
			}else {
				//如果工作线程已满,则加入等待队列
				if(!taskQueue.put(task, timeout, timeUnit)){
					log.error("执行拒绝策略...{}",taskName);
					//如果加入失败,则执行拒绝策略
					rejectPolicy.reject(taskQueue, task);
				}
			}
		}
	}

	class Worker extends Thread{

		private Runnable task;
		//线程名称
		private String name;

		public Worker(Runnable task,String name) {
			//设置线程名称
			super(name);
			this.task = task;
			this.name = name;
		}

		@Override
		public void run() {
			//当前线程执行完毕,则从等待队列中获取任务
//			while (task != null || (task = taskQueue.take()) != null){//当没有新任务提交时,程序不会终止
			while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null){//没有新任务提交,现有任务执行完毕后,程序会终止
				try {
					log.debug("running...{}",name);
					task.run();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					//当前线程执行完毕
					task = null;
				}
			}

			//任务运行完毕后,将其从线程集合中移除
			synchronized (workers) {
				log.warn("remove worker...{}",name);
				workers.remove(this);
			}
		}
	}
}

@Slf4j
class BlockingQueue<T>{

	//队列
	private Deque<T> queue;
	//容量
	private int capcity;
	//锁
	private ReentrantLock lock = new ReentrantLock();
	//空队列等待条件
	private Condition emptyWaitSet = lock.newCondition();
	//满队列等待条件
	private Condition fullWaitSet = lock.newCondition();
	//构造方法初始化队列
	public BlockingQueue(int capcity) {
		this.capcity = capcity;
		queue = new ArrayDeque<>(capcity);
	}

	//取
	public T take(){
		lock.lock();
		try {
			//队列为空,则等待
			while (queue.isEmpty()){
				try {
					emptyWaitSet.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			//队列不为空,则取出头部元素
			T t = queue.removeFirst();
			//通知put方法可以存元素了
			fullWaitSet.signal();
			return t;
		}finally {
			lock.unlock();
		}
	}

	//带有超时时间的取
	public T poll(long timeout, TimeUnit unit){
		lock.lock();

		try {
			long waitNanos = unit.toNanos(timeout);
			//队列为空,则等待
			while (queue.isEmpty()){
				try {
					if(waitNanos <= 0){
						return null;
					}
					//返回剩余时间
					waitNanos = emptyWaitSet.awaitNanos(waitNanos);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			//队列不为空,则取出头部元素
			T t = queue.removeFirst();
			//通知put方法可以存元素了
			fullWaitSet.signal();
			return t;
		}finally {
			lock.unlock();
		}
	}

	//放
	public void put(T t){
		lock.lock();
		try {
			//如果队列已满,则等待
			while (queue.size() == capcity){
				try {
					log.warn("等待加入任务队列...{}",t);
					fullWaitSet.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			log.debug("加入任务队列...{}",t);
			//队列不满,则放元素
			queue.addLast(t);
			//通知take方法可以取了
			emptyWaitSet.signal();

		}finally {
			lock.unlock();
		}
	}

	//放-带有超时时间的
	public boolean put(T t, long timeout, TimeUnit unit) {

		lock.lock();
		try {
			long waitNanos = unit.toNanos(timeout);

			//如果队列已满,则等待
			while (queue.size() == capcity){
				try {
					log.warn("等待加入任务队列...{}",t);

					if(waitNanos <= 0){
						return false;
					}
					waitNanos = fullWaitSet.awaitNanos(waitNanos);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			log.debug("加入任务队列...{}",t);
			//队列不满,则放元素
			queue.addLast(t);
			//通知take方法可以取了
			emptyWaitSet.signal();

			return true;

		}finally {
			lock.unlock();
		}
	}

	//获取队列大小
	public int size(){
		lock.lock();
		try {
			return queue.size();
		}finally {
			lock.unlock();
		}

	}

}

//拒绝策略
@FunctionalInterface
interface RejectPolicy<T>{

	void reject(BlockingQueue<T> queue,Runnable task);
}
