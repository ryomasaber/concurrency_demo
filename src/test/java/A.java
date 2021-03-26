

/**
 * Created by Saber on 2021/3/24 21:21
 */
public class A {

	static int num = 0;
	static boolean ready = false; //报告中出现3行 有 0,1,4

	static int result = 0;

	public static void actor1() {
		// Put the code for first thread here
		if (ready) {
			result = num + num;
		} else {
			result = 1;
		}
	}

	public static void actor2() {
		// Put the code for second thread here
		num = 2;
		ready = true;
	}

	public static void main(String[] args) {
		new Thread(()->{
			actor1();
		},"t1").start();

		new Thread(()->{
			actor2();
		},"t1").start();

		System.out.println("result = " + result);
	}
}
