package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止
 * Created by Saber on 2021/3/24 17:56
 */
@Slf4j
public class ThreadMonitor {

	public static void main(String[] args) throws InterruptedException {
		TwoPhaseTermination phase = TwoPhaseTermination.getInstance();
		TwoPhaseTermination phase2 = TwoPhaseTermination.getInstance();
		log.debug("1.开始监控");
		phase.start();

		log.debug("2.开始监控");
		phase2.start();
		phase2.start();



		TimeUnit.SECONDS.sleep(10);
		log.debug("等待10秒后终止监控");
		phase.stop();
	}


}

@Slf4j
class TwoPhaseTermination{

	private Thread thread;

	private volatile boolean stop = false;

	private volatile boolean isRunning = false; //todo 犹豫模式->防止重复操作

	private static volatile TwoPhaseTermination INSTANCE = null;

	private TwoPhaseTermination() {
	}

	//双重校验锁单例模式
	public static TwoPhaseTermination getInstance() {
		if(INSTANCE == null){
			synchronized (TwoPhaseTermination.class){
				if(INSTANCE == null){
					INSTANCE = new TwoPhaseTermination();
				}
			}
		}
		return INSTANCE;
	}

	public void start(){

		synchronized (this) {
			//todo 犹豫模式->防止重复操作 begin
			if (isRunning){
				log.debug("正在运行中,不用再启动了亲(づ￣3￣)づ╭❤～");
				return;
			}
			isRunning = true;
			//todo 犹豫模式->防止重复操作 end
		}

		thread = new Thread(()->{
			while (true){

				// 终止监控
				if(stop){
					log.debug("监控终止...");

					break;
				}

				try {
					Thread.sleep(2000);

					log.debug("监控功能工作了....");
				} catch (InterruptedException e) {
					//此处的异常可以忽略
					e.printStackTrace();
				}
			}
		},"monitor");

		thread.start();
	}

	public void stop(){
		stop = true;
		//此处的打断,主要是为了强制终止睡眠进而结束线程,stop方法调用后不会再有监控,即:停止监控是立即的
		//如果不打断,则在调用stop方法后,还有可能会再监控一次,即:停止监控室延时的
		thread.interrupt();
	}
}
