package com.google.firebase.app.internal.cpp;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CppThreadDispatcherContext {
  private long cancelFunctionPtr;
  private long functionData;
  private long functionPtr;
  private final Lock lock;

  public CppThreadDispatcherContext(final long functionPtr, final long functionData, final long cancelFunctionPtr) {
    super();
    this.lock = new ReentrantLock();
    this.functionPtr = functionPtr;
    this.functionData = functionData;
    this.cancelFunctionPtr = cancelFunctionPtr;
  }

  private void clear() {
    try {
      this.lock.lock();
      this.functionPtr = 0L;
      this.functionData = 0L;
      this.cancelFunctionPtr = 0L;
    } finally {
      this.lock.unlock();
    }
  }

  private static native void nativeFunction(final long p0, final long p1);

  public boolean acquireExecuteCancelLock() {
    this.lock.lock();
    return this.functionPtr != 0L;
  }

  public void cancel() {
    try {
      this.lock.lock();
      if(this.cancelFunctionPtr != 0L) {
        nativeFunction(this.cancelFunctionPtr, this.functionData);
      }
    } finally {
      this.clear();
      this.lock.unlock();
    }
  }

  public void execute() {
    try {
      this.lock.lock();
      if(this.functionPtr != 0L) {
        nativeFunction(this.functionPtr, this.functionData);
      }
    } finally {
      this.clear();
      this.lock.unlock();
    }
  }

  public void releaseExecuteCancelLock() {
    this.lock.unlock();
  }
}
