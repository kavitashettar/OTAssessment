package com.otassess.task;

public class TwitterIDGenerator {
	int tweetCounter = 0;

	public synchronized void increment() {
		tweetCounter++;
	}

	public synchronized int getCounter() {
		return tweetCounter;
	}
	
	public int getTwitterId() {
		increment();
		return getCounter();
	}
}
