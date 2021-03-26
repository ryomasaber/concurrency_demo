package com.saber.Lock.atomic;

import com.saber.UnSafeAccessor;
import sun.misc.Unsafe;

/**
 * Created by Saber on 2021/3/25 15:32
 */
public class MyAtomicIntegerTest {

	public static void main(String[] args) {
		MyAtomicInteger integer = new MyAtomicInteger(10);

		System.out.println("----	start	----");

		int count = 100;
		Thread t1 = new Thread(() -> {
			for (int i = 0; i < count; i++) {

				System.out.println("t1 +1 -> i = " + i);
				integer.increment(1);
				System.out.println("t1 +1 -> integer = " + integer.get());
			}
		}, "t1");

		Thread t2 = new Thread(() -> {
			for (int i = 0; i < count; i++) {

				System.out.println("t2 -1 -> i = " + i);
				integer.decrement(1);
				System.out.println("t2 -1 -> integer = " + integer.get());
			}
		}, "t2");

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("----	end	----");

		System.out.println("最终结果:integer = " + integer);

	}
}

/**
 * 自定义原子类
 */
final class MyAtomicInteger{

	private volatile int value;

	//value在内存中的偏移量
	private static final long valueOffset;

	private static final Unsafe UNSAFE;

	static {
		UNSAFE = UnSafeAccessor.getUnsafe();
		try {
			valueOffset = UNSAFE.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e );
		}
	}

	public MyAtomicInteger(int value) {
		this.value = value;
	}

	public int get(){
		return value;
	}

	public void decrement(int amount){
		while (true){
			int prev = this.value;
			int next = prev - amount;

			if(UNSAFE.compareAndSwapInt(this, valueOffset, prev, next)){
				return;
			}
		}
	}

	public void increment(int amount){
		while (true){
			int prev = this.value;
			int next = prev + amount;

			if(UNSAFE.compareAndSwapInt(this, valueOffset, prev, next)){
				return;
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MyAtomicInteger{");
		sb.append("value=").append(value);
		sb.append('}');
		return sb.toString();
	}
}
