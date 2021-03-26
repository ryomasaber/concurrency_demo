package com.saber.Lock.atomic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 给定一个数组,使用多线程将数组中的值都累加到10000
 * --验证普通数组和原子数组的线程安全问题
 * Created by Saber on 2021/3/25 12:43
 */
public class AtomicIntegerLambda {

	public static void main(String[] args) {

		System.out.println("--------	普通数组累加-不安全	--------");
		demo(
				() -> new int[10],
				(array) -> array.length,
				(array, index) -> array[index]++,
				array -> System.out.println(Arrays.toString(array))
		);

		System.out.println("--------	原子数组累加-安全		--------");

		demo(
				() -> new AtomicIntegerArray(10),
				(array) -> array.length(),
				(array, index) -> array.getAndIncrement(index),
				array -> System.out.println(array)
		);
	}

	/**
	 * @param arraySupplier 提供数组,可以是线程不安全数组或线程安全数组
	 * @param lengthFun     获取数组长度的方法
	 * @param putConsumer   自增方法,同传array,index
	 * @param printConsumer 打印数组的方法
	 * @param <T>
	 */
	public static <T> void demo(
			Supplier<T> arraySupplier,
			Function<T, Integer> lengthFun,
			BiConsumer<T, Integer> putConsumer,
			Consumer<T> printConsumer
	) {

		List<Thread> ts = new ArrayList<>();
		T array = arraySupplier.get();
		int length = lengthFun.apply(array);
		for (int i = 0; i < length; i++) {
			//每个线程对数组做10000次操作
			ts.add(new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					putConsumer.accept(array, j % length);
				}
			}));
		}

		long begin = System.currentTimeMillis();
		//遍历启动线程
		ts.forEach(Thread::start);
		//等待所有线程执行完毕
		ts.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		printConsumer.accept(array);
		long end = System.currentTimeMillis();
		System.out.println("cons = " + (end - begin) + " ms");
	}
}
