package com.saber.Lock.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 原子更新器-保护字段
 * Created by Saber on 2021/3/25 13:00
 */
public class AtomicFiledUpdater {

	public static void main(String[] args) {

		Student stu = new Student();

		AtomicReferenceFieldUpdater updater =
				AtomicReferenceFieldUpdater.newUpdater(Student.class,String.class,"name");

		System.out.println(updater.compareAndSet(stu, null, "张三"));
		System.out.println("stu = " + stu);

	}
}

class Student{
	volatile String name;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Student{");
		sb.append("name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
