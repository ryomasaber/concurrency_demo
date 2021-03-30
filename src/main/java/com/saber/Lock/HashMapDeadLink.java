package com.saber.Lock;

import static java.util.Objects.hash;

/**
 * JDK1.7的HashMap死链重现 需要运行时jdk为1.7,否则不会复现
 * Created by Saber on 2021/3/29 09:46
 */
public class HashMapDeadLink {

	public static void main(String[] args) {
		System.out.println("长度为16时,桶下标为1的key");
		for (int i = 0; i < 16; i++) {
			if (hash(i) % 16 == 1) {
				System.out.println("i = " + i);
			}
		}

		System.out.println("长度为32时,桶下标为1的key");
		for (int i = 0; i < 64; i++) {
			if (hash(i) % 32 == 1) {
				System.out.println("i = " + i);
			}
		}
	}
}
