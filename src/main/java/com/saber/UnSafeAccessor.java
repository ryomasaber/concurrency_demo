package com.saber;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by Saber on 2021/3/25 15:27
 */
public class UnSafeAccessor {

	private static final Unsafe unsafe;

	static {
//		Unsafe.getUnsafe().
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			unsafe = (Unsafe)theUnsafe.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new Error(e);
		}
	}

	public static Unsafe getUnsafe() {
		return unsafe;
	}
}
