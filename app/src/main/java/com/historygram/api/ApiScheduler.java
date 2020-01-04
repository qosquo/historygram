package com.historygram.api;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiScheduler {

    private static final int NETWORK_THREADS_COUNT = 32;

    private static AtomicInteger counter = new AtomicInteger();
    private static ExecutorService networkExecutor;

    public static Handler getHandler() {
        return new Handler(Looper.getMainLooper());
    }

    public static ExecutorService getNetworkExecutor() {
        return Executors.newFixedThreadPool(NETWORK_THREADS_COUNT, runnable ->
                new Thread(runnable, "api-network-thread-" + counter.getAndIncrement()));
    }

    public static void runOnMainThread(Runnable runnable) {
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            runnable.run();
//        }
        getHandler().post(runnable);
    }

    public static void runOnMainThread(Runnable runnable, Long delay) {
        getHandler().postDelayed(runnable, delay);
    }
}
