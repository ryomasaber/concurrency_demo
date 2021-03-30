package com.saber.Lock;

/**
 * 双重校验锁单例->DCL(double check lock)
 * Created by Saber on 2021/3/30 13:43
 */
public class DCLSingleton {

	private static volatile DCLSingleton instance = null;

	private DCLSingleton() {}

	public static DCLSingleton getInstance(){
		if(instance == null){
			synchronized (DCLSingleton.class){
				if(instance == null){
					instance = new DCLSingleton();
				}
				return instance;
			}
		}

		return instance;
	}
}
