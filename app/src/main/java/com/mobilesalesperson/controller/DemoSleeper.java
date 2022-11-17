package com.mobilesalesperson.controller;

/**
 * @author T.SARAVANAN class used for printing line one by one...
 */
public class DemoSleeper {

	private DemoSleeper() {

	}

	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}