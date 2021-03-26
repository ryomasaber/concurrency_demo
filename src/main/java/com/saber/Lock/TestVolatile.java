package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Saber on 2021/3/24 22:54
 */
@Slf4j
public class TestVolatile {

	static volatile boolean inited = false;

	void init(){

		if(!inited) {
			synchronized (this) {
				if (inited) {
					return;
				}

				doInit();
				inited = true;
			}
		}
		log.debug("init didn't again....");

	}

	private void doInit(){
		log.debug("init.....");
	}

	public static void main(String[] args) {
		TestVolatile test = new TestVolatile();

		new Thread(()->{

			test.init();
		},"t1").start();

		new Thread(()->{
			test.init();
		},"t2").start();
	}
}
