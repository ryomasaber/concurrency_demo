package com.saber.Lock.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 原子累加器
 * Created by Saber on 2021/3/25 13:59
 */
public class AtomicAdder {

	public static void main(String[] args) {

		System.out.println("------ AtomicLong 4个线程每个线程累加50W ------");
		for (int i = 0; i < 5; i++) {
			demo(
					()->new AtomicLong(),
					(adder)-> adder.getAndIncrement()
			);
		}

		System.out.println("------ LongAdder 4个线程每个线程累加50W ------");
		for (int i = 0; i < 5; i++) {
			demo(
					()->new LongAdder(),
					(adder)-> adder.increment()
			);
		}
	}

	private static <T> void demo(Supplier<T> addSupplier, Consumer<T> action){
		T adder = addSupplier.get();

		List<Thread> ts = new ArrayList<>();
		//4个线程,每个线程累加50W
		for (int i = 0; i < 4; i++) {
			ts.add(new Thread(()->{
				for (int j = 0; j < 50_0000; j++) {
					//执行计算操作
					action.accept(adder);
				}
			}));
		}

		long begin = System.nanoTime();

		ts.forEach(Thread::start);
		ts.forEach(t->{
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		long end = System.nanoTime();
		System.out.println("addr:"+adder+",cost:"+(end-begin)/1000_000+"ns");

	}
}
