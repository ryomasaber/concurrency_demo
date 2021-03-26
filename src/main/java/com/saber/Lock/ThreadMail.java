package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Saber on 2021/3/23 17:40
 */
public class ThreadMail {

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 3; i++) {
			new People().start();
		}
		TimeUnit.SECONDS.sleep(1);

		for (Integer id : MailBoxes.getIds()) {
			new PostMan(id, "内容"+id).start();
		}
	}
}

@Slf4j
class People extends Thread{
	@Override
	public void run() {
		//获取邮箱
		GuidedObject2 go = MailBoxes.createGuidedObject();
		log.debug("开始获取邮箱,id={}",go.getId());
		Object mail = go.get(5000);
		log.debug("邮箱ID:{},内容:{}",go.getId(),mail);
	}
}

@Slf4j
class PostMan extends Thread{

	/**邮箱ID*/
	private Integer id;
	/**内容*/
	private String mail;

	public PostMan(Integer id,String mail){
		this.id = id;
		this.mail = mail;
	}

	@Override
	public void run() {
		GuidedObject2 go = MailBoxes.getMailById(id);
		log.debug("送信信.id={},内容={}",id,mail);
		go.complete(mail);
	}
}

class MailBoxes{

	private static Map<Integer,GuidedObject2> boxes = new Hashtable<>();
	private static int id = 0;

	/**
	 * 生成id
	 */
	private static synchronized int generateId(){
		return id++;
	}

	public static GuidedObject2 createGuidedObject(){
		GuidedObject2 obj = new GuidedObject2(generateId());
		boxes.put(obj.getId(),obj);
		return obj;
	}

	/**
	 * 获取所有id
	 */
	public static Set<Integer> getIds(){
		return boxes.keySet();
	}

	/**
	 * 根据邮箱ID获取邮件
	 */
	public static GuidedObject2 getMailById(Integer id){
		return boxes.remove(id);
	}
}

class GuidedObject2{
	private Integer id;

	private Object response;

	public GuidedObject2(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public synchronized Object get(long timeout){
		long begin = System.currentTimeMillis();
		long passedtime = 0;
		while (response == null){
			long waittime = timeout - passedtime;
			if(waittime <= 0){
				break;
			}

			try {
				this.wait(waittime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			passedtime = System.currentTimeMillis() - begin;
		}

		return response;
	}

	public synchronized void complete(Object obj){
		this.response = obj;
		this.notifyAll();
	}
}
