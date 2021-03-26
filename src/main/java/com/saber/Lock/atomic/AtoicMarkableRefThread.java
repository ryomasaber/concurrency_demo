package com.saber.Lock.atomic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * 使用原子类标记元素是否改变过
 * Created by Saber on 2021/3/25 10:52
 */
@Slf4j
public class AtoicMarkableRefThread {

	public static void main(String[] args) throws InterruptedException {

		GarbageBag bag = new GarbageBag("装满了垃圾");
		AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag,true);

		log.debug("垃圾满了麽:{}",ref.isMarked());
		log.debug("start...");
		GarbageBag prev = ref.getReference();
		log.debug(prev.toString());

		//保洁阿姨来收垃圾了
		new Thread(()->{
			log.debug("垃圾满了吗?:{}",ref.isMarked());
			prev.setDesc("空垃圾袋");
			boolean flag = ref.compareAndSet(prev, prev, true, false);
			log.debug("垃圾倒走了?:{}",flag);

			log.debug("垃圾满了吗?:{}",ref.isMarked());
		},"保洁阿姨").start();

		log.debug("垃圾满了吗?:{}",ref.isMarked());
		Thread.sleep(1000);
		log.debug("想换一只垃圾袋了?");
		boolean flag = ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
		log.debug("换了么?{}",flag);
		log.debug(ref.getReference().toString());
	}
}


class GarbageBag {

	private String desc;

	public GarbageBag(String desc) {
		this.desc = desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("GarbageBag{");
		sb.append("desc='").append(desc).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
