package com.google.firebase.app.internal.cpp;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class JniResultCallback<TResult> {
  public static final String TAG = "FirebaseCb";
  private long callbackData;
  private long callbackFn;
  private Callback callbackHandler;

  protected JniResultCallback(final long n, final long n2) {
    super();
    this.callbackHandler = null;
    this.initializeNativeCallbackFunctionAndData(n, n2);
  }

  public JniResultCallback(final Task<TResult> task, final long n, final long n2) {
    super();
    this.callbackHandler = null;
    this.initializeNativeCallbackFunctionAndData(n, n2);
    this.initializeWithTask(task);
  }

  private native void nativeOnResult(final Object p0, final boolean p1, final boolean p2, final String p3, final long p4, final long p5);

  public void cancel() {
    this.onCompletion(null, false, true, "cancelled");
  }

  protected void initializeNativeCallbackFunctionAndData(final long callbackFn, final long callbackData) {
    this.callbackFn = callbackFn;
    this.callbackData = callbackData;
  }

  protected void initializeWithTask(final Task<TResult> task) {
    synchronized(this) {
      (this.callbackHandler = (JniResultCallback.Callback) new JniResultCallback.TaskCallback((Task) task)).register();
    }
  }

  public void onCompletion(final Object o, final boolean b, final boolean b2, final String s) {
    synchronized(this) {
      if(this.callbackHandler != null) {
        this.nativeOnResult(o, b, b2, s, this.callbackFn, this.callbackData);
        this.callbackHandler.disconnect();
        this.callbackHandler = null;
      }
    }
  }

  private interface Callback {
    void disconnect();

    void register();
  }


  private class TaskCallback<TResult> implements OnSuccessListener<TResult>, OnFailureListener, OnCanceledListener, Callback {
    private final Object lockObject;
    private Task<TResult> task;

    public TaskCallback(final Task<TResult> task) {
      super();
      this.lockObject = new Object();
      this.task = task;
    }

    public void disconnect() {
      synchronized(this.lockObject) {
        this.task = null;
      }
    }

    public void onCanceled() {
      synchronized(this.lockObject) {
        if(this.task != null) {
          JniResultCallback.this.cancel();
        }
        this.disconnect();
      }
    }

    public void onFailure(final Exception ex) {
      synchronized(this.lockObject) {
        if(this.task != null) {
          JniResultCallback.this.onCompletion((Object) ex, false, false, ex.getMessage());
        }
        this.disconnect();
      }
    }

    public void onSuccess(final TResult tResult) {
      synchronized(this.lockObject) {
        if(this.task != null) {
          JniResultCallback.this.onCompletion((Object) tResult, true, false, (String) null);
        }
        this.disconnect();
      }
    }

    public void register() {
      this.task.addOnSuccessListener((OnSuccessListener) this);
      this.task.addOnFailureListener((OnFailureListener) this);
      this.task.addOnCanceledListener((OnCanceledListener) this);
    }
  }
}
