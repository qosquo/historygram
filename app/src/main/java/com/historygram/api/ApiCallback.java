package com.historygram.api;

public interface ApiCallback<T> {
    void success(T result);
    void fail(Exception error);
}
