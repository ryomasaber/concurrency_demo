package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 哲学家吃饭-使用重入锁解决死锁问题
 * Created by Saber on 2021/3/23 22:30
 */
@Slf4j
public class ChopstickDeadLockFix {
	public static void main(String[] args) throws InterruptedException {
		Chopstick2 c1 = new Chopstick2("1");
		Chopstick2 c2 = new Chopstick2("2");
		Chopstick2 c3 = new Chopstick2("3");
		Chopstick2 c4 = new Chopstick2("4");
		Chopstick2 c5 = new Chopstick2("5");

		new Philosopher2("苏格拉底", c1, c2).start();
		new Philosopher2("柏拉图", c2, c3).start();
		new Philosopher2("亚里斯多德", c3, c4).start();
		new Philosopher2("阿基米德", c4, c5).start();
		new Philosopher2("孔子", c5, c1).start();
	}
}

@Slf4j
class Philosopher2 extends Thread{
	/*左手筷子*/
	private Chopstick2 left;
	/*右手筷子*/
	private Chopstick2 right;

	public Philosopher2(String name, Chopstick2 left, Chopstick2 right) {
		//调用父类方法设置线程名称
		super(name);
		this.left = left;
		this.right = right;
	}

	@Override
	public void run() {
		while (true) {
			//拿到左手筷子
//			synchronized (left){
//				//拿到右手筷子
//				synchronized (right){
//					//吃饭
//					eat();
//				}
//			}
//todo -----------	改进点	---------------
			//尝试拿到左手筷子
			if(left.tryLock()){
				try {
					//尝试拿到右手筷子
					if(right.tryLock()){

						try {
							//吃饭
							eat();
						}finally {
							right.unlock();
						}
					}
				}finally {
					//释放左手筷子
					left.unlock();
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
class Chopstick2 extends ReentrantLock { //todo 这里增加了继承 ReentrantLock

	/*筷子名字*/
	private String name;

	public Chopstick2(String name) {
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
