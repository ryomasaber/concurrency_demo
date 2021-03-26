package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 线程唤醒
 * Created by Saber on 2021/3/23 16:45
 */
@Slf4j
public class ThreadWait {

	public static void main(String[] args) {

		GuidedObject guidedObject = new GuidedObject();

		new Thread("t1"){
			@Override
			public void run() {
				log.debug("开始获取结果");
				Object obj = guidedObject.get(5000);
				log.debug("结果是:{}",obj);
			}
		}.start();

		new Thread("t2"){
			@Override
			public void run() {
				try {
					log.debug("开始设置结果");
					TimeUnit.SECONDS.sleep(7);
					guidedObject.complete(null);
					log.debug("设置结果完成");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}

@Slf4j
class GuidedObject{

	private Object response;

	public Object get(long timeout){
		synchronized (this){
			long passedtime = 0;
			long begin = System.currentTimeMillis();
			while (response == null){
				long waittime = timeout - passedtime;

				log.debug("waittime={}",waittime);
				if(waittime <= 0){
					break;
				}
				try {
					log.debug("entry wait...");
					this.wait(waittime);
					log.debug("exit wait...");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				passedtime = System.currentTimeMillis()-begin;
				log.debug("passedtime={}",passedtime);
			}
		}
		return response;
	}

	public void complete(Object obj){
		synchronized (this){
			this.response = obj;
			this.notifyAll();
		}
	}
}
