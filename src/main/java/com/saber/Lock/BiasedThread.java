package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * 偏向锁
 * Created by Saber on 2021/3/23 11:13
 */
@Slf4j(topic = "c.BiasedThread")
public class BiasedThread {
//	private final static Logger logger = LoggerFactory.getLogger(BiasedThread.class);

	public static void main(String[] args) throws InterruptedException {
		Dog dog = new Dog();
		ClassLayout layout = ClassLayout.parseInstance(dog);
		log.debug(layout.toPrintable());
//
//		TimeUnit.SECONDS.sleep(4);
//		log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());

		synchronized (dog){
			log.debug(layout.toPrintable());
		}
		log.debug(layout.toPrintable());
	}
}


class Dog{

}
