package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 生产者消费者-消息队列
 * Created by Saber on 2021/3/23 18:49
 */
@Slf4j
public class ThreadMessageQueue {

	public static void main(String[] args) {

		MessageQueue queue = new MessageQueue(2);

		//创建1个消费者线程
		new Thread("消费者"){
			@Override
			public void run() {
				while (true){
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message message = queue.take();
					log.debug("消费者,消息={}",message);
				}
			}
		}.start();

		//创建3个生产者线程
		for (int i = 0; i < 3; i++) {
			int id = i;
			new Thread("生产者"+ id){
				@Override
				public void run() {
					String msg = "内容,id="+ id;
					Message message = new Message(id,msg);
					queue.put(message);
				}
			}.start();
		}

	}


}

@Slf4j
class MessageQueue{
	private LinkedList<Message> list = new LinkedList();
	private int capcity;

	public MessageQueue(int capcity) {
		this.capcity = capcity;
	}

	/**
	 * 取消息
	 * @return
	 */
	public Message take(){
		synchronized (list){
			//队列中没有值,等待,队列中有值时取走头部
			while (list.isEmpty()) {
				try {
					log.debug("队列为空,消费者等待");
					list.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Message message = list.removeFirst();
			log.debug("消费了一个消息,消息id:{}",message.getId());
			list.notifyAll();
			return message;
		}
	}

	/**
	 * 放消息
	 * @param message
	 */
	public void put(Message message){
		synchronized (list){
			//队列容量满了,等待队列空了再放
			while (list.size() == capcity){
				try {
					log.debug("队列满了,生产者等待");
					list.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			list.addLast(message);
			log.debug("生产了一个消息,消息id:{}",message.getId());
			list.notifyAll();
		}
	}


}

class Message{

	private Integer id;

	private Object value;

	public Message(Integer id, Object value) {
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Message{");
		sb.append("id=").append(id);
		sb.append(", value=").append(value);
		sb.append('}');
		return sb.toString();
	}
}
