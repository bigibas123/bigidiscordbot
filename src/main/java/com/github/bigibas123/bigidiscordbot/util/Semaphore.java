package com.github.bigibas123.bigidiscordbot.util;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class Semaphore extends java.util.concurrent.Semaphore implements AutoCloseable {

	public Semaphore(int permits) {
		super(permits);
	}

	public Semaphore(int permits, boolean fair) {
		super(permits, fair);
	}

	public Semaphore lock() {
		this.acquireUninterruptibly();
		return this;
	}


	public void unlock() {
		this.release();
	}

	public Semaphore lock(int permits) {
		this.acquireUninterruptibly(permits);
		return this;
	}

	public void unlock(int permits){
		this.release(permits);
	}

	@Override
	public void close() {
		this.unlock();
	}
	public void close(int permits){
		this.unlock(permits);
	}

	public boolean tryLock(){
		return this.tryAcquire();
	}
	public boolean tryLock(int permits){
		return this.tryAcquire(permits);
	}
	@SneakyThrows
	public boolean tryLock(long timeout, TimeUnit unit){
		return this.tryAcquire(timeout,unit);
	}
	@SneakyThrows
	public boolean tryLock(int permits, long timeout, TimeUnit unit){
		return this.tryAcquire(permits,timeout,unit);
	}
}
