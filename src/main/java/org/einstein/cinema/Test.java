package org.einstein.cinema;

import java.util.concurrent.Semaphore;

public class Test {
	public static Semaphore S1 = new Semaphore(4); // 0     4   
	public static Semaphore S2 = new Semaphore(0); // 1     0      
	public static Semaphore S3 = new Semaphore(1); // 1     0   

	public static class ThreadA extends Thread {
		public void run () {
			while (true) {
				double soma = 0;

				try {
					S1.acquire();
					for (int i=0; i<10000; i++) {
						for (int j=0; j<2000; j++) {
							soma = soma + Math.sin(i) + Math.sin(j);
						}
					}
					System.out.println("A");

					if (S1.availablePermits() == 0) {
						S3.release();
						S2.release();
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
	}

	public static class ThreadB extends Thread {
		public void run () {
			while (true) {
				double soma = 0;

				try {
					S3.acquire();
					S2.acquire();
					for (int i=0; i<10000; i++) {
						for (int j=0; j<2000; j++) {
							soma = soma + Math.sin(i) + Math.sin(j);
						}
					}
					System.out.println("B");
					S1.release(4);
					S3.release();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
	}
}
