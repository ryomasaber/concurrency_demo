package com.saber;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * Created by Saber on 2021/3/24 20:36
 */
// See jcstress-samples or existing tests for API introduction and testing guidelines

@JCStressTest
//表示有可能会出现的结果，其中0是我们感兴趣的结果
@Outcome(id = {"0, 0"}, expect = Expect.ACCEPTABLE_INTERESTING, desc = "!!!!")
//@Outcome(id = {"0","0, 0","1, 0","4, 0"}, expect = Expect.ACCEPTABLE_INTERESTING, desc = "!!!!")
@Outcome(id = {"1, 0","4, 0"}, expect = Expect.ACCEPTABLE, desc = "ok")
@State
public class ConcurrencyTest {

	int num = 0;
	boolean ready = false;

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
