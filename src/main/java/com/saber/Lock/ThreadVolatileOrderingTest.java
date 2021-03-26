package com.saber.Lock;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * 指令重排 Created by Saber on 2021/3/24 19:12
 */
@JCStressTest(Mode.Continuous)
//表示有可能出现的结果,其中0是我们感兴趣的结果
@Outcome(id = {"1, 0","4, 0"}, expect = Expect.ACCEPTABLE, desc = "ok")
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "!!!!")
@State
@Description(value = "没有使用volatile防止指令重排")
public class ThreadVolatileOrderingTest {

	int num = 0;
	boolean ready = false; //报告中出现3行 有 0,1,4

	@Actor
	public void actor1(II_Result r) {
		// Put the code for first thread here
		if (ready) {
			r.r1 = num + num;
		} else {
			r.r1 = 1;
		}
	}

	@Actor
	public void actor2(II_Result r) {
		// Put the code for second thread here
		num = 2;
		ready = true;
	}

}
