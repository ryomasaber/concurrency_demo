package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 哲学家吃饭-死锁
 * Created by Saber on 2021/3/23 22:30
 */
@Slf4j
public class ChopstickDeadLock {
	public static void main(String[] args) throws InterruptedException {
		Chopstick c1 = new Chopstick("1");
		Chopstick c2 = new Chopstick("2");
		Chopstick c3 = new Chopstick("3");
		Chopstick c4 = new Chopstick("4");
		Chopstick c5 = new Chopstick("5");

		new Philosopher("苏格拉底", c1, c2).start();
		new Philosopher("柏拉图", c2, c3).start();
		new Philosopher("亚里斯多德", c3, c4).start();
		new Philosopher("阿基米德", c4, c5).start();
		new Philosopher("孔子", c5, c1).start();
	}
}

@Slf4j
class Philosopher extends Thread{
	/*左手筷子*/
	private Chopstick left;
	/*右手筷子*/
	private Chopstick right;

	public Philosopher(String name, Chopstick left, Chopstick right) {
		//调用父类方法设置线程名称
		super(name);
		this.left = left;
		this.right = right;
	}

	@Override
	public void run() {
		while (true) {
			//拿到左手筷子
			synchronized (left){
				//拿到右手筷子
				synchronized (right){
					//吃饭
					eat();
				}
			}
		}

	}

	private void eat(){
		log.debug("抢到了筷子[左筷{},右筷{}] 正在吃饭....",left,right);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

@Slf4j
class Chopstick{

	/*筷子名字*/
	private String name;

	public Chopstick(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("筷子{");
		sb.append("name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
