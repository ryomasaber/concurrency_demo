package com.saber.Lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CAS-Compare And Set ->比较并设置
 * <p>
 * 描述: 余额一共1W元,一共1000个线程来取钱,每个线程取10块 Created by Saber on 2021/3/25 08:52
 */
public class ThreadAccountCas {

	public static void main(String[] args) {

		System.out.println("------	线程不安全实现	-----");
		Account account = new AccountUnSafe(10000);
		Account.demo(account);

		System.out.println("------	有锁的线程安全实现	-----");
		Account account2 = new AccountSafe(10000);
		Account.demo(account2);

		System.out.println("------	无锁的线程安全实现-CAS	-----");
		Account account3 = new AccountCas(10000);
		Account.demo(account3);
	}
}


interface Account {

	/**
	 * 获取余额
	 */
	Integer getBalance();

	/**
	 * 取款
	 *
	 * @param amount 取款金额
	 */
	void withDraw(Integer amount);

	static void demo(Account account) {
		List<Thread> list = new ArrayList<>(1000);
		for (int i = 0; i < 1000; i++) {
			Thread t = new Thread(() -> {
				account.withDraw(10);
			}, "t" + 0);

			list.add(t);
		}

		long start = System.currentTimeMillis();
		list.forEach(Thread::start);
		list.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		long end = System.currentTimeMillis();
		System.out.println("balance = " + account.getBalance() + ",cost=" + (end - start) + "ms");
	}
}

//线程不安全的实现
class AccountUnSafe implements Account {

	private Integer balance;

	public AccountUnSafe(Integer balance) {
		this.balance = balance;
	}

	/**
	 * 获取余额
	 */
	@Override
	public Integer getBalance() {
		return balance;
	}

	/**
	 * 取款
	 *
	 * @param amount 取款金额
	 */
	@Override
	public void withDraw(Integer amount) {
		balance = balance - amount;
	}
}

//线程安全的实现
class AccountSafe implements Account {

	private Integer balance;

	public AccountSafe(Integer balance) {
		this.balance = balance;
	}

	/**
	 * 获取余额
	 */
	@Override
	public Integer getBalance() {
		synchronized (this) {
			return balance;
		}
	}

	/**
	 * 取款
	 *
	 * @param amount 取款金额
	 */
	@Override
	public void withDraw(Integer amount) {
		synchronized (this) {
			balance = balance - amount;
		}
	}
}

//线程安全的无锁实现-CAS
class AccountCas implements Account {

	private AtomicInteger balance;

	public AccountCas(Integer balance) {
		this.balance = new AtomicInteger(balance);
	}

	/**
	 * 获取余额
	 */
	@Override
	public Integer getBalance() {
		return balance.get();
	}

	/**
	 * 取款
	 *
	 * @param amount 取款金额
	 */
	@Override
	public void withDraw(Integer amount) {
//		while (true){
//			//获取余额最新值
//			int prev = balance.get();
//			//计算取钱后的余额
//			int next = prev-amount;
//			//更新余额
//			if(balance.compareAndSet(prev, next)){
//				break;
//			}
//		}
		//上面注释的代码等价于下面的
		balance.addAndGet(-1 * amount);
	}
}
