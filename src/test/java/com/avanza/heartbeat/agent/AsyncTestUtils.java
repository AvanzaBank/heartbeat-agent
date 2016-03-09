package com.avanza.heartbeat.agent;

import java.util.concurrent.TimeUnit;

public class AsyncTestUtils {

	public static void eventually(Runnable test, long durationMillis) {
		long start = System.nanoTime();
		AssertionError lastError = null;
		while (System.nanoTime() - start < TimeUnit.MILLISECONDS.toNanos(durationMillis)) {
			try {
				test.run();
				return;
			} catch (AssertionError error) {
				lastError = error;
			}
			sleep(50);
		}
		throw lastError;
	}

	private static void sleep(int sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
